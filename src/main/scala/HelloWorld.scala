package hello


import opengl.ShaderLoader
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.Display
import org.lwjgl.util.vector.Matrix4f
import java.nio.FloatBuffer
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL15._
import org.lwjgl.opengl.GL20._
import org.lwjgl.opengl.GL30._
import state.State
import scala.collection.mutable
import scala.collection.JavaConverters._


object HelloWorld extends App {
  new HelloWorld().run
}

class HelloWorld extends SingleWindowScene(800, 600, 3, 3) {

  override protected def init(): Unit = {
    programState.init()
    arrayDataState.init()
  }

  override protected def display(deltaTime: Double) {
    val withProgram = clear.push(programState)
    val withArrayData = withProgram.push(arrayDataState)

    withArrayData.run {
      glDrawArrays(GL_TRIANGLES, 0, 36)
    }

    Display.update()
  }

  def createPerspectiveMatrix(frustumScale: Float, zNear: Float, zFar: Float): FloatBuffer = {
    val matrixBuffer = BufferUtils.createFloatBuffer(4 * 4)
    val matrix = new Matrix4f()
    matrix.m00 = frustumScale
    matrix.m11 = frustumScale
    matrix.m22 = (zFar + zNear) / (zNear - zFar)
    matrix.m23 = -1.0f
    matrix.m32 = (2 * zFar * zNear) / (zNear - zFar)
    matrix.store(matrixBuffer)
    matrixBuffer.flip()
    matrixBuffer
  }

  val clear = new State() {
    override def begin() {
      glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
      glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)
    }
  }

  val programState = new State() {
    var offsetLocation = 0
    var perspectiveMatrixUnif = 0
    var programId = 0
    val vertexShader = "src/main/shaders/MatrixPerspective.vert"
    val fragmentShader = "src/main/shaders/fragment_basic.glsl"
    val matrixBuffer = createPerspectiveMatrix(1.0f, 0.5f, 3.0f)
    override def init() {
      val shaders = mutable.Map[Int, Int]()
      shaders.put(GL_VERTEX_SHADER, ShaderLoader.createShader(vertexShader, GL_VERTEX_SHADER))
      shaders.put(GL_FRAGMENT_SHADER, ShaderLoader.createShader(fragmentShader, GL_FRAGMENT_SHADER))
      programId = ShaderLoader.initializeProgram(shaders.asJava)
      offsetLocation = glGetUniformLocation(programId, "offset")
      perspectiveMatrixUnif = glGetUniformLocation(programId, "perspectiveMatrix")
      shaders.values foreach { shaderId =>
        glDeleteShader(shaderId)
      }
    }

    override def begin() {
      glUseProgram(programId)
      glUniform2f(offsetLocation, 0.5f, 0.5f)
      glUniformMatrix4(perspectiveMatrixUnif, false, matrixBuffer)
    }

    override def end() {
      glUseProgram(0)
    }
  }

  val arrayDataState = new State() {
    var vertexArrayObjectId = 0
    var positionBufferObject = 0 // TODO: figure out how to propagate this from init to begin

    override def init() {
      vertexArrayObjectId = glGenVertexArrays()
      glBindVertexArray(vertexArrayObjectId)
      glEnable(GL_CULL_FACE)
      glCullFace(GL_BACK)
      glFrontFace(GL_CW)

      val vertexPositionsBuffer = BufferUtils.createFloatBuffer(vertexPositions.length)
      vertexPositionsBuffer.put(vertexPositions)
      vertexPositionsBuffer.flip()
      positionBufferObject = glGenBuffers()
      glBindBuffer(GL_ARRAY_BUFFER, positionBufferObject)
      glBufferData(GL_ARRAY_BUFFER, vertexPositionsBuffer, GL_STATIC_DRAW)
      glBindBuffer(GL_ARRAY_BUFFER, 0)
    }

    override def begin() {
      glBindBuffer(GL_ARRAY_BUFFER, positionBufferObject)
      glEnableVertexAttribArray(0)
      glEnableVertexAttribArray(1)
      glVertexAttribPointer(0, 4, GL_FLOAT, false, 0, 0)
      val colorDataOffset = vertexPositions.length * java.lang.Float.BYTES / 2
      glVertexAttribPointer(1, 4, GL_FLOAT, false, 0, colorDataOffset)
    }

    override def end() {
      glDisableVertexAttribArray(1)
      glDisableVertexAttribArray(0)
      glBindBuffer(GL_ARRAY_BUFFER, 0)
    }

    val vertexPositions = Array(
      0.25f,  0.25f, -1.25f, 1.0f,
      0.25f, -0.25f, -1.25f, 1.0f,
      -0.25f,  0.25f, -1.25f, 1.0f,

      0.25f, -0.25f, -1.25f, 1.0f,
      -0.25f, -0.25f, -1.25f, 1.0f,
      -0.25f,  0.25f, -1.25f, 1.0f,

      0.25f,  0.25f, -2.75f, 1.0f,
      -0.25f,  0.25f, -2.75f, 1.0f,
      0.25f, -0.25f, -2.75f, 1.0f,

      0.25f, -0.25f, -2.75f, 1.0f,
      -0.25f,  0.25f, -2.75f, 1.0f,
      -0.25f, -0.25f, -2.75f, 1.0f,

      -0.25f,  0.25f, -1.25f, 1.0f,
      -0.25f, -0.25f, -1.25f, 1.0f,
      -0.25f, -0.25f, -2.75f, 1.0f,

      -0.25f,  0.25f, -1.25f, 1.0f,
      -0.25f, -0.25f, -2.75f, 1.0f,
      -0.25f,  0.25f, -2.75f, 1.0f,

      0.25f,  0.25f, -1.25f, 1.0f,
      0.25f, -0.25f, -2.75f, 1.0f,
      0.25f, -0.25f, -1.25f, 1.0f,

      0.25f,  0.25f, -1.25f, 1.0f,
      0.25f,  0.25f, -2.75f, 1.0f,
      0.25f, -0.25f, -2.75f, 1.0f,

      0.25f,  0.25f, -2.75f, 1.0f,
      0.25f,  0.25f, -1.25f, 1.0f,
      -0.25f,  0.25f, -1.25f, 1.0f,

      0.25f,  0.25f, -2.75f, 1.0f,
      -0.25f,  0.25f, -1.25f, 1.0f,
      -0.25f,  0.25f, -2.75f, 1.0f,

      0.25f, -0.25f, -2.75f, 1.0f,
      -0.25f, -0.25f, -1.25f, 1.0f,
      0.25f, -0.25f, -1.25f, 1.0f,

      0.25f, -0.25f, -2.75f, 1.0f,
      -0.25f, -0.25f, -2.75f, 1.0f,
      -0.25f, -0.25f, -1.25f, 1.0f,




      0.0f, 0.0f, 1.0f, 1.0f,
      0.0f, 0.0f, 1.0f, 1.0f,
      0.0f, 0.0f, 1.0f, 1.0f,

      0.0f, 0.0f, 1.0f, 1.0f,
      0.0f, 0.0f, 1.0f, 1.0f,
      0.0f, 0.0f, 1.0f, 1.0f,

      0.8f, 0.8f, 0.8f, 1.0f,
      0.8f, 0.8f, 0.8f, 1.0f,
      0.8f, 0.8f, 0.8f, 1.0f,

      0.8f, 0.8f, 0.8f, 1.0f,
      0.8f, 0.8f, 0.8f, 1.0f,
      0.8f, 0.8f, 0.8f, 1.0f,

      0.0f, 1.0f, 0.0f, 1.0f,
      0.0f, 1.0f, 0.0f, 1.0f,
      0.0f, 1.0f, 0.0f, 1.0f,

      0.0f, 1.0f, 0.0f, 1.0f,
      0.0f, 1.0f, 0.0f, 1.0f,
      0.0f, 1.0f, 0.0f, 1.0f,

      0.5f, 0.5f, 0.0f, 1.0f,
      0.5f, 0.5f, 0.0f, 1.0f,
      0.5f, 0.5f, 0.0f, 1.0f,

      0.5f, 0.5f, 0.0f, 1.0f,
      0.5f, 0.5f, 0.0f, 1.0f,
      0.5f, 0.5f, 0.0f, 1.0f,

      1.0f, 0.0f, 0.0f, 1.0f,
      1.0f, 0.0f, 0.0f, 1.0f,
      1.0f, 0.0f, 0.0f, 1.0f,

      1.0f, 0.0f, 0.0f, 1.0f,
      1.0f, 0.0f, 0.0f, 1.0f,
      1.0f, 0.0f, 0.0f, 1.0f,

      0.0f, 1.0f, 1.0f, 1.0f,
      0.0f, 1.0f, 1.0f, 1.0f,
      0.0f, 1.0f, 1.0f, 1.0f,

      0.0f, 1.0f, 1.0f, 1.0f,
      0.0f, 1.0f, 1.0f, 1.0f,
      0.0f, 1.0f, 1.0f, 1.0f)
  }
}
