package hello

import org.lwjgl.LWJGLException
import org.lwjgl.opengl.ContextAttribs
import org.lwjgl.opengl.Display
import org.lwjgl.opengl.DisplayMode
import org.lwjgl.opengl.PixelFormat
import org.lwjgl.opengl.GL11.glViewport


abstract class SingleWindowScene(width: Int, height: Int, openGlMajorVersion: Int, openGlMinorVersion: Int) extends Runnable {
  protected var lastFrameTime: Double = .0

  def run {
    try {
      configure()
      init()
      lastFrameTime = getTime
      while (!Display.isCloseRequested) {
        val deltaTime: Float = getDeltaTime
        display(deltaTime)
      }
      Display.destroy()
    }
    catch {
      case t: Throwable => {
        t.printStackTrace()
        if (Display.isCreated) Display.destroy()
        System.exit(-1)
      }
    }
    System.exit(0)
  }

  @throws(classOf[LWJGLException])
  protected def configure() {
    // http://stackoverflow.com/questions/18702467/enable-anti-aliasing-with-lwjgl
    // http://www.java-gaming.org/topics/lwjgl-antialiased-lines/30894/view.html
    val pixelFormat: PixelFormat = new PixelFormat(8, 8, 0, 8)
    val contextAttribs: ContextAttribs = new ContextAttribs(openGlMajorVersion,
                                                            openGlMinorVersion).withForwardCompatible(true).withProfileCore(true)
    Display.setDisplayMode(new DisplayMode(width, height))
    Display.setVSyncEnabled(true)
    Display.create(pixelFormat, contextAttribs)
    glViewport(0, 0, width, height)
  }

  protected def init(): Unit

  protected def display(delta: Float)

  def getTime: Double = {
    System.nanoTime / 1000000000.0
  }

  private def getDeltaTime: Float = {
    val time: Double = getTime
    val delta: Int = (time - lastFrameTime).toInt
    lastFrameTime = time
    delta
  }
}
