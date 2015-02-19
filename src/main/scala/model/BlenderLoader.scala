package model

import java.io.InputStream
import org.lwjgl.util.vector.Vector3f
import scala.io.Source
import scala.util.parsing.combinator.RegexParsers


case class Model(faces: Faces, vertices: List[Vertex], normals: List[Normal], uvs: List[UV]) {
  private def toArray(vectors: List[Vector3f]): Array[Float] = {
    val tuples = vectors map { vector =>
      List(vector.x, vector.y, vector.z)
    }
    tuples.flatten.toArray
  }

  def faceIndicesArray: Array[Byte] = {
    // opengl expects 0 based indices, but blender object files use 1 based indices
    // also, lwjgl needs indices to be unsigned, so we used unsigned bytes
    faces.faces.flatMap(_.vertexIndices.toArray).map(v => ((v-1) & 0xff).toByte).toArray
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

case class Face(vertexIndices: TriangleIndices, uvIndices: Option[TriangleIndices], normalIndices: Option[TriangleIndices])
case class Faces(faces: List[Face]) extends BlenderLine

case class TriangleIndices(p1: Int, p2: Int, p3: Int) {
  def toArray = List(p1, p2, p3)
}

case class FaceIndices(p1: FaceIndex, p2: FaceIndex, p3: FaceIndex) {
  private def optionalsToTriangleIndices(p1: Option[Int], p2: Option[Int], p3: Option[Int]) = {
    (p1, p2, p3) match {
      case (Some(_p1), Some(_p2), Some(_p3)) => Some(TriangleIndices(_p1, _p2, _p3))
      case _ => None
    }
  }
  
  def vertexIndices = {
    TriangleIndices(p1.vertex, p2.vertex, p3.vertex)
  }

  def uvIndices = {
    optionalsToTriangleIndices(p1.uv, p2.uv, p3.uv)
  }

  def normalIndices = {
    optionalsToTriangleIndices(p1.normal, p2.normal, p3.normal)
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
    def faceParser: Parser[Faces] = "f" ~ faceIndexParser ~ faceIndexParser ~ faceIndexParser ~ faceIndexParser.* ^^ {
      case f~p1~p2~p3~rest => {
        val faceIndexList = p1 :: p2 :: p3 :: rest
        val faces = faceIndexList.sliding(3) map { case List(_p1, _p2, _p3) =>
          val faceIndices = FaceIndices(_p1, _p2, _p3)
          Face(faceIndices.vertexIndices, faceIndices.uvIndices, faceIndices.normalIndices)
        }
        Faces(faces.toList)
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
    val facesList = parsedLines.filter(_.isInstanceOf[Faces]).map(_.asInstanceOf[Faces]).toList
    val faces = Faces(facesList.flatMap(_.faces))
    println("faces: ")
    println(faces)
    Model(faces, vertices, normals, uvs)
  }
}
