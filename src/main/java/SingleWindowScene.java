import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;

import static org.lwjgl.opengl.GL11.glViewport;

public abstract class SingleWindowScene implements Runnable {
  private final int width;
  private final int height;
  private final int openGlMajorVersion;
  private final int openGlMinorVersion;
  
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

      while (!Display.isCloseRequested()) {
        // Clear the screen and depth buffer
        display();
      }
      Display.destroy();
    } catch (LWJGLException e) {
      e.printStackTrace();
      System.exit(0);
    }
  }

  protected void configure() throws LWJGLException {
    PixelFormat pixelFormat = new PixelFormat();

    ContextAttribs contextAttribs = new ContextAttribs(openGlMajorVersion, openGlMinorVersion)
        .withForwardCompatible(true)
        .withProfileCore(true);

    Display.setDisplayMode(new DisplayMode(width, height));
    Display.setVSyncEnabled(true);
    Display.create(pixelFormat, contextAttribs);
    glViewport(0, 0, width, height);
  }

  protected abstract void init();
  protected abstract void display();
}
