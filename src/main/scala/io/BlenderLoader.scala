package io

import java.io.InputStream
import java.nio.{FloatBuffer, ShortBuffer, ByteBuffer}

import org.lwjgl.BufferUtils

import scala.io.Source
import scala.util.parsing.combinator.RegexParsers

case class Model(faces: Array[Face], vertices: Array[Vertex], normals: Array[Normal], uvs: Array[UV]) {

  // generate ids for each distinct vertex/uv/normal combination
  private val elementIds: Map[FaceIndex, Int] = faces.flatMap(_.faceIndices).toSet.zipWithIndex.toMap

  // lwjgl needs indices to be unsigned, so we used unsigned shorts
  // TODO: We should use unsigned ints or longs here if the element count exceeds max short.  And we should raise an exception
  // if the element count exceeds what we support.
  private def toUnsignedShort(value: Int): Short = (value & 0xffff).toShort

  /**
   * Create an index list for a vertex array object.
   * Indices are provided in GL_TRIANGLE_FAN order and are terminated by BlenderLoader.PRIMITIVE_RESTART
   * See glPrimitiveRestartIndex for details.
   * @return
   */
  // TODO: build a ByteBuffer directly
  def triangleFanArrayElementIndices: ShortBuffer = {

    val shortCount = faces.map(_.faceIndices.size + 1 /* need extra short for primitive restart */).sum
    val facesBuffer = BufferUtils.createShortBuffer(shortCount)
    
    faces foreach { face =>
      face.faceIndices foreach { f =>
        facesBuffer.put(toUnsignedShort(elementIds(f)))
      }

      facesBuffer.put(BlenderLoader.PRIMITIVE_RESTART)
    }
    facesBuffer.flip()

    facesBuffer
  }

  /**
   * Interleaved vertices and normals.  3 floats for a vertex followed by 3 floats for a normal.
   * @return
   */
  // TODO: build a ByteBuffer directly
  // TODO: Add uv coordinates
  def interleavedDataArray: FloatBuffer = {
    val floatCount = elementIds.size * 3 /* x,y,z */ * 2 /* vertices and normals */
    val verticesAndNormalsBuffer = BufferUtils.createFloatBuffer(floatCount)
    val sortedFaceIndices: List[FaceIndex] = elementIds.toList.sortBy(_._2).map(_._1)

    sortedFaceIndices foreach { faceIndex =>
      val vertex = vertices(faceIndex.vertex-1).vertex
      verticesAndNormalsBuffer.put(vertex.x)
      verticesAndNormalsBuffer.put(vertex.y)
      verticesAndNormalsBuffer.put(vertex.z)
      val normal = faceIndex.normal.map(n => normals(n-1).normal).getOrElse(Vector3(0f, 0f, 0f))
      verticesAndNormalsBuffer.put(normal.x)
      verticesAndNormalsBuffer.put(normal.y)
      verticesAndNormalsBuffer.put(normal.z)
    }
    verticesAndNormalsBuffer.flip()

    verticesAndNormalsBuffer
  }
}

case class Vector3(x: Float, y: Float, z: Float)

sealed trait BlenderLine
case class Vertex(vertex: Vector3) extends BlenderLine
case class Normal(normal: Vector3) extends BlenderLine
case class UV(uv: Vector3) extends BlenderLine

case class Face(faceIndices: List[FaceIndex]) extends BlenderLine

case class ObjectGrouping(name: String) extends BlenderLine

sealed trait SmoothingGroup extends BlenderLine
case object SmoothingGroupOff extends SmoothingGroup
case class SmoothingGroupValue(value: String) extends SmoothingGroup
case class Usemtl(value: String) extends BlenderLine
case class FaceIndex(vertex: Int, uv: Option[Int], normal: Option[Int])

case class Mtllib(value: String) extends BlenderLine

// TODO: Support negative indices (e.g. -1 in a face means to use the previous v or vn)
/**
 * See http://www.martinreddy.net/gfx/3d/OBJ.spec
 */
object BlenderLoader {

  val PRIMITIVE_RESTART = Short.MaxValue

  object Parser extends RegexParsers {
    override protected val whiteSpace = """(\s|#.*)+""".r // treat # comments as whitespace

    def intParser: Parser[Int] = """[-+]?\d+""".r ^^ { value => value.toInt }
    def floatParser: Parser[Float] = """[-+]?(\d*\.\d+|\d+)""".r ^^ { value => value.toFloat }
    def vector3fParser: Parser[Vector3] = floatParser ~ floatParser ~ floatParser ^^ {
      case x~y~z => new Vector3(x, y, z)
    }

    // Vertex Data
    def faceIndexParser: Parser[FaceIndex] = intParser ~ ("/" ~ intParser.? ~ "/" ~ intParser.?).? ^^ {
      case vertex~Some(_~uv~_~normal) => FaceIndex(vertex, uv, normal)
      case vertex~None => FaceIndex(vertex, None, None)
    }
    def vertexParser: Parser[Vertex] = "v" ~ vector3fParser ^^ {
      case v~vector3f => Vertex(vector3f)
    }

    def textureVertexParser: Parser[UV] = "vt" ~ vector3fParser ^^ {
      case vt~vector3f => UV(vector3f)
    }

    def normalParser: Parser[Normal] = "vn" ~ vector3fParser ^^ {
      case vn~vector3f => Normal(vector3f)
    }

    // Element Data
    def faceParser: Parser[Face] = "f" ~ faceIndexParser ~ faceIndexParser ~ faceIndexParser ~ faceIndexParser.* ^^ {
      case f~p1~p2~p3~rest => {
        val faceIndexList = p1 :: p2 :: p3 :: rest
        Face(faceIndexList)
      }
    }

    // Grouping
    def objectNameParser: Parser[String] = """.*""".r
    def objectParser: Parser[ObjectGrouping] = "o" ~ objectNameParser ^^ {
      case o~name => ObjectGrouping(name)
    }

    def smoothingGroupParser: Parser[SmoothingGroup] = "s" ~ """.*""".r ^^ {
      case s~"off" => SmoothingGroupOff
      case s~value => SmoothingGroupValue(value)
    }

    def usemtlParser: Parser[Usemtl] = "usemtl" ~ """.*""".r ^^ {
      case usemtl~value => Usemtl(value)
    }

    def mtllibParser: Parser[Mtllib] = "mtllib" ~ """.*""".r ^^ {
      case mtllib~value => Mtllib(value)
    }

    // Top Level Parser
    def lineParser = usemtlParser | mtllibParser | normalParser | textureVertexParser | smoothingGroupParser | vertexParser | objectParser | faceParser

    def fileParser = lineParser.*

    def apply(line: String): List[BlenderLine] = parseAll(fileParser, line) match {
      case Success(result, _) => result
      case failure : NoSuccess => throw new IllegalArgumentException(failure.msg + " line: " + failure.next.pos.line + " column: " + failure.next.pos.column)
    }
  }

  def loadModel(in: InputStream): Model = {
    val inputText = Source.fromInputStream(in).mkString
    val parsedLines = Parser(inputText)
    val vertices = parsedLines.filter(_.isInstanceOf[Vertex]).map(_.asInstanceOf[Vertex]).toArray
    val uvs = parsedLines.filter(_.isInstanceOf[UV]).map(_.asInstanceOf[UV]).toArray
    val normals = parsedLines.filter(_.isInstanceOf[Normal]).map(_.asInstanceOf[Normal]).toArray
    val faces = parsedLines.filter(_.isInstanceOf[Face]).map(_.asInstanceOf[Face]).toArray
    Model(faces, vertices, normals, uvs)
  }
}
