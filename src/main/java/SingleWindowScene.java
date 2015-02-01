import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;

import static org.lwjgl.opengl.GL11.glViewport;

public abstract class SingleWindowScene implements Runnable {
  protected int width;
  protected int height;
  private final int openGlMajorVersion;
  private final int openGlMinorVersion;

  protected double lastFrameTime;
  
  public SingleWindowScene(int width, int height, int openGlMajorVersion, int openGlMinorVersion) {
    this.width = width;
    this.height = height;
    
    this.openGlMajorVersion = openGlMajorVersion;
    this.openGlMinorVersion = openGlMinorVersion;
  }
  
  public void run() {
    try {
      configure();
      init();
      lastFrameTime = getTime();

      while (!Display.isCloseRequested()) {
        // Clear the screen and depth buffer
        double deltaTime = getDeltaTime();
        display(deltaTime);
      }
      Display.destroy();
    } catch (Throwable t) {
      t.printStackTrace();
      if (Display.isCreated()) Display.destroy();
      System.exit(-1);
    }
    System.exit(0);
  }

  protected void configure() throws LWJGLException {
    PixelFormat pixelFormat = new PixelFormat();

    ContextAttribs contextAttribs = new ContextAttribs(openGlMajorVersion, openGlMinorVersion)
        .withForwardCompatible(true)
        .withProfileCore(true);

    Display.setDisplayMode(new DisplayMode(width, height));
    Display.setVSyncEnabled(true);
    Display.create(pixelFormat, contextAttribs);
    //Display.setResizable(true);
    //Display.setTitle("Hello World");
    glViewport(0, 0, width, height);
  }

  protected abstract void init();
  protected abstract void display(double delta);

  public double getTime() {
    return System.nanoTime() / 1000000000.0;
  }

  private double getDeltaTime() {
    double time = getTime();
    int delta = (int) (time - lastFrameTime);
    lastFrameTime = time;

    return delta;
  }
}
