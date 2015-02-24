package hello


import java.io.{FileInputStream, File}

import io.BlenderLoader
import model._
import opengl.ShaderLoader
import org.lwjgl.BufferUtils
import org.lwjgl.input.{Mouse, Keyboard}
import org.lwjgl.opengl.Display
import org.lwjgl.util.glu.GLU
import org.lwjgl.util.vector.{Matrix3f, Quaternion, Vector3f, Matrix4f}
import java.nio.FloatBuffer
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL15._
import org.lwjgl.opengl.GL20._
import org.lwjgl.opengl.GL30._
import org.lwjgl.opengl.GL31._
import state.{VertexArrayState, State}
import scala.collection.mutable
import scala.collection.JavaConverters._


object HelloWorld extends App {
  new HelloWorld().run
}

class HelloWorld extends SingleWindowScene(1600, 1200, 3, 2) {

  var camera = new SceneCamera(new Vector3f(0f, 0f, -3f), new Vector3f())
  var sceneModel = new SceneModel(new Vector3f(), new Vector3f())

  val rotationDelta = 0.1f
  val scaleDelta = 0.1f
  val posDelta = 0.1f

  override protected def init(): Unit = {
    programState.init()
    triangleData.init()
  }

  override protected def display(deltaTime: Float) {
    val withProgram = clear.push(programState)
    val withTriangleData = withProgram.push(triangleData)

    if(Mouse.isButtonDown(0)) {
      camera.angle.y += -Mouse.getDX() * rotationDelta
      camera.angle.x += Mouse.getDY() * rotationDelta
    }

    // TODO: make movement relative to camera angle
    if(Keyboard.isKeyDown(Keyboard.KEY_UP)) camera.position.translate(0, posDelta, 0)
    if(Keyboard.isKeyDown(Keyboard.KEY_DOWN)) camera.position.translate(0, -posDelta, 0)
    if(Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) camera.position.translate(posDelta, 0, 0)
    if(Keyboard.isKeyDown(Keyboard.KEY_LEFT)) camera.position.translate(-posDelta, 0, 0)

    if(Mouse.isButtonDown(1)) {
      camera.position.translate(0, 0, Mouse.getDY() * posDelta)
    }

    sceneModel.angle.y += rotationDelta*2

    withTriangleData.run {
      triangleData.draw()
    }

    Display.sync(60) // Force max rate of about 60 FPS
    Display.update()
  }

  val clear = new State() {
    override def begin() {
      glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
      glClearDepth(1.0f)
      glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)
      glEnable(GL_PRIMITIVE_RESTART)
      glPrimitiveRestartIndex(BlenderLoader.PRIMITIVE_RESTART)

      glEnable(GL_DEPTH_TEST)
      glDepthMask(true)
      glDepthFunc(GL_LEQUAL)
      glDepthRange(0.0f, 1.0f)
    }
  }

  val programState = new State() {
    var modelToViewMatrixUnif = 0
    var worldToViewMatrixUnif = 0
    var modelToViewNormalMatrixUnif = 0
    var viewToPerspectiveMatrixUnif = 0
    var programId = 0
    val vertexShader = "src/main/shaders/MatrixPerspective.vert"
    val fragmentShader = "src/main/shaders/fragment_basic.glsl"
    val perspectiveMatrixBuffer = createPerspectiveMatrix(frustumScale = 1.00f, zNear = 0.0001f, zFar = 10000.0f)

    override def init() {
      val shaders = mutable.Map[Int, Int]()
      shaders.put(GL_VERTEX_SHADER, ShaderLoader.createShader(vertexShader, GL_VERTEX_SHADER))
      shaders.put(GL_FRAGMENT_SHADER, ShaderLoader.createShader(fragmentShader, GL_FRAGMENT_SHADER))
      programId = ShaderLoader.initializeProgram(shaders.asJava)
      modelToViewMatrixUnif = glGetUniformLocation(programId, "modelToViewMatrix")
      worldToViewMatrixUnif = glGetUniformLocation(programId, "worldToViewMatrix")
      modelToViewNormalMatrixUnif = glGetUniformLocation(programId, "modelToViewNormalMatrix")
      viewToPerspectiveMatrixUnif = glGetUniformLocation(programId, "viewToPerspectiveMatrix")
      shaders.values foreach { shaderId =>
        glDeleteShader(shaderId)
      }
    }

    override def begin() {
      glUseProgram(programId)

      val modelToWorldMatrix = sceneModel.toMatrix
      val worldToViewMatrix = camera.toMatrix
      glUniformMatrix4(worldToViewMatrixUnif, false, matrixToBuffer(worldToViewMatrix))

      val modelViewMatrix = Matrix4f.mul(worldToViewMatrix, modelToWorldMatrix, null)
      glUniformMatrix4(modelToViewMatrixUnif, false, matrixToBuffer(modelViewMatrix))


      val normalViewMatrix = new Matrix4f(modelViewMatrix)
      normalViewMatrix.invert().transpose()
      glUniformMatrix4(modelToViewNormalMatrixUnif, false, matrixToBuffer(normalViewMatrix))
      glUniformMatrix4(viewToPerspectiveMatrixUnif, false, perspectiveMatrixBuffer)
    }

    override def end() {
      glUseProgram(0)
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

    def matrixToBuffer(matrix: Matrix4f) = {
      val matrixBuffer = BufferUtils.createFloatBuffer(4 * 4)
      matrix.store(matrixBuffer)
      matrixBuffer.flip()
      matrixBuffer
    }
  }

  //val modelFile = "src/main/resources/quad.obj"
  val modelFile = "src/main/resources/monkey.obj"
  //val modelFile = "src/main/resources/sphere.obj"
  //val modelFile = "src/main/resources/cube.obj"
  val model = BlenderLoader.loadModel(new FileInputStream(new File(modelFile)))
  val triangleData = new VertexArrayState(model.interleavedDataArray, model.triangleFanArrayElementIndices)
}
