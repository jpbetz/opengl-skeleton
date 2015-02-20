package model

import java.io.InputStream
import org.lwjgl.util.vector.Vector3f
import scala.io.Source
import scala.util.parsing.combinator.RegexParsers

case class Model(faces: List[Face], vertices: List[Vertex], normals: List[Normal], uvs: List[UV]) {
  private def toArray(vectors: List[Vector3f]): Array[Float] = {
    val tuples = vectors map { vector =>
      List(vector.x, vector.y, vector.z)
    }
    tuples.flatten.toArray
  }

  // We need a primitive restart in order to use GL Triangle Fan layout with a buffer array


  // opengl expects 0 based indices, but blender object files use 1 based indices
  // also, lwjgl needs indices to be unsigned, so we used unsigned bytes
  private def toUnsignedShort(value: Int): Short = ((value-1) & 0xffff).toShort

  def faceIndicesTriangleFanArray: Array[Short] = {
    faces.flatMap(_.vertexIndices.indices.map(toUnsignedShort) ++ List(BlenderLoader.PRIMITIVE_RESTART)).toArray
  }

  def verticesArray: Array[Float] = {
    toArray(vertices.map(_.vertex))
  }

  def normalsArray: Array[Float] = {
    toArray(normals.map(_.normal))
  }

  def uvsArray: Array[Float] = {
    toArray(uvs.map(_.uv))
  }
}

sealed trait BlenderLine
case class Vertex(vertex: Vector3f) extends BlenderLine
case class Normal(normal: Vector3f) extends BlenderLine
case class UV(uv: Vector3f) extends BlenderLine

case class Face(vertexIndices: TriangleFanIndices, uvIndices: TriangleFanIndices, normalIndices: TriangleFanIndices) extends BlenderLine

case class TriangleFanIndices(indices: List[Int])

case class FaceIndices(indices: List[FaceIndex]) {
  def vertexIndices = {
    TriangleFanIndices(indices.map(_.vertex))
  }

  def uvIndices = {
    TriangleFanIndices(indices.flatMap(_.uv))
  }

  def normalIndices = {
    TriangleFanIndices(indices.flatMap(_.normal))
  }
}
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
        val faceIndices = FaceIndices(faceIndexList)
        Face(faceIndices.vertexIndices, faceIndices.uvIndices, faceIndices.normalIndices)
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
    val vertices = parsedLines.filter(_.isInstanceOf[Vertex]).map(_.asInstanceOf[Vertex]).toList
    println("vertices: ")
    println(vertices)
    val uvs = parsedLines.filter(_.isInstanceOf[UV]).map(_.asInstanceOf[UV]).toList
    val normals = parsedLines.filter(_.isInstanceOf[Normal]).map(_.asInstanceOf[Normal]).toList
    val faces = parsedLines.filter(_.isInstanceOf[Face]).map(_.asInstanceOf[Face]).toList
    println("faces: ")
    println(faces)
    Model(faces, vertices, normals, uvs)
  }
}
