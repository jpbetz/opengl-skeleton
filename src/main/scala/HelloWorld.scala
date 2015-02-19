package hello


import java.io.{FileInputStream, File}

import model.BlenderLoader
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

class HelloWorld extends SingleWindowScene(800, 600, 3, 2) {

  override protected def init(): Unit = {
    programState.init()
    arrayDataState.init()
  }

  override protected def display(deltaTime: Double) {
    val withProgram = clear.push(programState)
    val withArrayData = withProgram.push(arrayDataState)

    withArrayData.run {
      //println(s"drawing ${faceVertices.length} vertices")
      glDrawElements(GL_TRIANGLES, faceVertices.length, GL_UNSIGNED_BYTE, 0)
    }
    
    //Display.sync(60) // Force max rate of about 60 FPS
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
    val matrixBuffer = createPerspectiveMatrix(frustumScale = 1.00f, zNear = 0.0001f, zFar = 10000.0f)
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
      glUniform3f(offsetLocation, 0.0f, 0.0f, -3.0f)
      glUniformMatrix4(perspectiveMatrixUnif, false, matrixBuffer)
    }

    override def end() {
      glUseProgram(0)
    }
  }

  //val modelFile = "src/main/resources/wt_teapot.obj"
  //val modelFile = "src/main/resources/quad.obj"
  val modelFile = "src/main/resources/monkey.obj"
  val model = BlenderLoader.loadModel(new FileInputStream(new File(modelFile)))
  val vertexPositions = model.verticesArray
  /*val vertexPositions = Array[Float](
    -0.5f, 0.5f, 0f,    // Left top         ID: 0
    -0.5f, -0.5f, 0f,   // Left bottom      ID: 1
    0.5f, -0.5f, 0f,    // Right bottom     ID: 2
    0.5f, 0.5f, 0f      // Right left       ID: 3
  )*/
  
  val faceVertices = model.faceVerticesArray
  /*val faceVertices = Array[Byte](
    // Left bottom triangle
    0, 1, 2,
    // Right top triangle
    2, 3, 0
  )*/

  val arrayDataState = new State() {
    var vertexArrayObjectId = 0
    var elementBufferId = 0
    var positionBufferObject = 0 // TODO: figure out how to propagate this from init to begin
    override def init() {
      glEnable(GL_CULL_FACE)
      glCullFace(GL_BACK)
      glFrontFace(GL_CCW)

      // vertices
      val vertexPositionsBuffer = BufferUtils.createFloatBuffer(vertexPositions.length)
      println(s"""loaded vertexPositionsBuffer with: ${vertexPositions.mkString(",")}""")
      vertexPositionsBuffer.put(vertexPositions)
      vertexPositionsBuffer.flip()

      vertexArrayObjectId = glGenVertexArrays()
      glBindVertexArray(vertexArrayObjectId)
      positionBufferObject = glGenBuffers()
      glBindBuffer(GL_ARRAY_BUFFER, positionBufferObject)
      glBufferData(GL_ARRAY_BUFFER, vertexPositionsBuffer, GL_STATIC_DRAW)
      glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0)
      glBindBuffer(GL_ARRAY_BUFFER, 0)
      glBindVertexArray(0)

      // elements
      val facesBuffer = BufferUtils.createByteBuffer(faceVertices.length)
      println(s"""loaded facesBuffer with: ${faceVertices.mkString(",")}""")
      facesBuffer.put(faceVertices)
      facesBuffer.flip()
      
      elementBufferId = glGenBuffers()
      glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, elementBufferId)
      glBufferData(GL_ELEMENT_ARRAY_BUFFER, facesBuffer, GL_STATIC_DRAW)
      glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
    }

    override def begin() {
      // vertices
      glBindVertexArray(vertexArrayObjectId)
      glEnableVertexAttribArray(0)
      
      // elements
      glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, elementBufferId)
    }

    override def end() {
      
      // elements
      glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)

      // vertices
      glDisableVertexAttribArray(0)
      glBindVertexArray(0)
    }
  }
}
