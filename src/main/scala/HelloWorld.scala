package hello


import java.io.{FileInputStream, File}

import com.ra4king.opengl.util.math.Vector3
import com.ra4king.opengl.util.math.Matrix4
import io.BlenderLoader
import model._
import opengl.ShaderLoader
import org.lwjgl.BufferUtils
import org.lwjgl.input.{Mouse, Keyboard}
import org.lwjgl.opengl.Display
import org.lwjgl.util.glu.GLU
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

class HelloWorld extends SingleWindowScene(800, 600, 3, 2) {

  var camera = new SceneCamera(new Vector3(0f, 0f, -3f), new Vector3())
  var sceneModel = new SceneModel(new Vector3(), new Vector3())

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
      camera.angle.set(
        camera.angle.x + Mouse.getDY() * rotationDelta,
        camera.angle.y + -Mouse.getDX() * rotationDelta,
        camera.angle.z)
    }

    // TODO: make movement relative to camera angle
    var delta = new Vector3()
    if(Keyboard.isKeyDown(Keyboard.KEY_UP)) delta = delta.add(0, posDelta, 0)
    if(Keyboard.isKeyDown(Keyboard.KEY_DOWN)) delta = delta.add(0, -posDelta, 0)
    if(Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) delta = delta.add(posDelta, 0, 0)
    if(Keyboard.isKeyDown(Keyboard.KEY_LEFT)) delta = delta.add(-posDelta, 0, 0)

    if(Mouse.isButtonDown(1)) {
      delta = delta.add(0, 0, Mouse.getDY() * posDelta)
    }

    // TODO: rotate delta by camera.angle
    camera.position.set(camera.position.add(delta))

    sceneModel.angle.set(sceneModel.angle.x, sceneModel.angle.y + rotationDelta*2, sceneModel.angle.z)

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
      glUniformMatrix4(worldToViewMatrixUnif, false, worldToViewMatrix.toBuffer)

      val modelViewMatrix = worldToViewMatrix.mult(modelToWorldMatrix)
      glUniformMatrix4(modelToViewMatrixUnif, false, modelViewMatrix.toBuffer)


      val normalViewMatrix = new Matrix4(modelViewMatrix).inverse().transpose()
      glUniformMatrix4(modelToViewNormalMatrixUnif, false, normalViewMatrix.toBuffer)
      glUniformMatrix4(viewToPerspectiveMatrixUnif, false, perspectiveMatrixBuffer)
    }

    override def end() {
      glUseProgram(0)
    }

    def createPerspectiveMatrix(frustumScale: Float, zNear: Float, zFar: Float): FloatBuffer = {
      val matrix = new Matrix4().clearToPerspective(math.Pi.toFloat/2f, 1f, 1f, zNear, zFar)
      matrix.toBuffer
    }
  }

  //val modelFile = "src/main/resources/quad.obj"
  val modelFile = "src/main/resources/monkey.obj"
  //val modelFile = "src/main/resources/sphere.obj"
  //val modelFile = "src/main/resources/cube.obj"
  val model = BlenderLoader.loadModel(new FileInputStream(new File(modelFile)))
  val triangleData = new VertexArrayState(model.interleavedDataArray, model.triangleFanArrayElementIndices)
}
