package model

import java.io.InputStream
import org.lwjgl.util.vector.Vector3f
import scala.io.Source
import scala.util.parsing.combinator.RegexParsers

case class Model(faces: Array[Face], vertices: Array[Vertex], normals: Array[Normal], uvs: Array[UV]) {
  val faceIds: Map[FaceIndex, Int] = faces.flatMap(_.faceIndices).toSet.zipWithIndex.toMap

  private def toArray(vectors: Array[Vector3f]): Array[Float] = {
    vectors.map(toFloats).flatten.toArray
  }

  private def toFloats(vector: Vector3f): List[Float] = {
    List(vector.x, vector.y, vector.z)
  }

  // lwjgl needs indices to be unsigned, so we used unsigned shorts
  private def toUnsignedShort(value: Int): Short = ((value) & 0xffff).toShort

  /**
   * Indices are provided in GL_TRIANGLE_FAN order and are terminated by BlenderLoader.PRIMITIVE_RESTART
   * See glPrimitiveRestartIndex for details.
   * @return
   */
  def triangleFanArrayElementIndices: Array[Short] = {
    val indices = faces flatMap { face =>
      val indicesAsShorts = face.faceIndices.map(f => toUnsignedShort(faceIds(f)))
      indicesAsShorts ++ List(BlenderLoader.PRIMITIVE_RESTART)
    }
    indices.toArray
  }

  /**
   * Interleaved vertices and normals.  3 floats for a vertex followed by 3 floats for a normal.
   * @return
   */
  def interleavedDataArray: Array[Float] = {

    val entries = faces flatMap { face =>
      face.faceIndices.map(f => (faceIds(f), (f.vertex, f.normal)))
    }
    // remove duplicates, order by face id
    val sortedFaceIndices = entries.toMap.toList.sortBy(_._1).map(_._2)
    val data = sortedFaceIndices flatMap { case (vertexId, normalId) =>
      val vertex = vertices(vertexId-1).vertex
      val normal = normalId.map(n => normals(n-1).normal).getOrElse(new Vector3f(0f, 0f, 0f))
      toFloats(vertex) ++ toFloats(normal)
    }
    data.toArray
  }
}

sealed trait BlenderLine
case class Vertex(vertex: Vector3f) extends BlenderLine
case class Normal(normal: Vector3f) extends BlenderLine
case class UV(uv: Vector3f) extends BlenderLine

case class Face(faceIndices: List[FaceIndex]) extends BlenderLine

case class ObjectGrouping(name: String) extends BlenderLine

sealed trait SmoothingGroup extends BlenderLine
case object SmoothingGroupOff extends SmoothingGroup
case class SmoothingGroupValue(value: String) extends SmoothingGroup
case class Usemtl(value: String) extends BlenderLine
case class FaceIndex(vertex: Int, uv: Option[Int], normal: Option[Int])

case class Mtllib(value: String) extends BlenderLine

/**
 * See http://www.martinreddy.net/gfx/3d/OBJ.spec
 */
object BlenderLoader {

  val PRIMITIVE_RESTART = Short.MaxValue

  object Parser extends RegexParsers {
    override protected val whiteSpace = """(\s|#.*)+""".r // treat # comments as whitespace

    def intParser: Parser[Int] = """[-+]?\d+""".r ^^ { value => value.toInt }
    def floatParser: Parser[Float] = """[-+]?(\d*\.\d+|\d+)""".r ^^ { value => value.toFloat }
    def vector3fParser: Parser[Vector3f] = floatParser ~ floatParser ~ floatParser ^^ {
      case x~y~z => new Vector3f(x, y, z)
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
