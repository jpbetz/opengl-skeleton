package opengl

import java.io.{File, IOException}
import java.util.Map

import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL20._
import org.lwjgl.opengl.GL32._
import org.lwjgl.util.glu.GLU

import scala.collection.JavaConverters._
import scala.io.Source


object ShaderLoader {
  def createShader(filename: String, shaderType: Int): Int = {
    var shaderID = 0
    val file = new File(filename)
    if (!file.exists) throw new IllegalArgumentException("File not found: " + file.getAbsolutePath)
    if (!file.canRead) throw new IllegalArgumentException("Cannot read file: " + file.getAbsolutePath)
    val shaderSource = try {
      Source.fromFile(file).mkString
    }
    catch {
      case e: IOException => {
        throw new IllegalArgumentException("Could not read file: " + e.getMessage)
      }
    }
    shaderID = glCreateShader(shaderType)
    glShaderSource(shaderID, shaderSource)
    glCompileShader(shaderID)
    if (glGetShaderi(shaderID, GL_COMPILE_STATUS) == GL_FALSE) {
      val maxLength = glGetShaderi(shaderID, GL_INFO_LOG_LENGTH)
      val info = glGetShaderInfoLog(shaderID, maxLength)
      throw new IllegalArgumentException("Could not compile " + file.getAbsolutePath + ": " + info)
    }
    checkForGlError("createShader")
    shaderID
  }

  def initializeProgram(shaderTypeToId: Map[Int, Int]): Int = {
    val programId = glCreateProgram

    shaderTypeToId.asScala map { case (key, value) =>
      glAttachShader(programId, value)
      checkForGlError("attachShader:" + shaderTypeToString(key))
    }

    glLinkProgram(programId)

    if (glGetProgrami(programId, GL_LINK_STATUS) == GL_FALSE) {
      val infoLogLength = glGetProgrami(programId, GL_INFO_LOG_LENGTH)
      val infoLog = glGetProgramInfoLog(programId, infoLogLength)
      throw new IllegalArgumentException("Linker failure: " + infoLog)
    }
    shaderTypeToId.values.asScala map { shaderId =>
      glDetachShader(programId, shaderId)
    }
    checkForGlError("linkProgram")
    glValidateProgram(programId)
    checkForGlError("initializeProgram")
    programId
  }

  private def shaderTypeToString(shaderType: Int) = shaderType match {
    case GL_VERTEX_SHADER => "vertex"
    case GL_GEOMETRY_SHADER => "geometry"
    case GL_FRAGMENT_SHADER => "fragment"
    case _ => throw new IllegalArgumentException("Invalid shader type: " + shaderType)
  }

  private def checkForGlError(errorMessage: String) {
    val errorValue = glGetError
    if (errorValue != GL_NO_ERROR) {
      val errorString = GLU.gluErrorString(errorValue)
      throw new IllegalStateException("GL Error - " + errorMessage + ": " + errorString)
    }
  }
}

