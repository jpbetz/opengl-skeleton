import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

// Based on: https://bitbucket.org/alfonse/gltut/src/1d1479cc7027f1e32c5adff748f3b296f1931d84/Tut%2001%20Hello%20Triangle/tut1.cpp?at=default
// and https://bitbucket.org/alfonse/gltut/src/1d1479cc7027f1e32c5adff748f3b296f1931d84/framework/framework.cpp?at=default
public class HelloWorld extends SingleWindowScene {
  
  public HelloWorld() {
    super(800, 600, 3, 3);
  }
  
  int programId;
  String vertexShader = "src/main/shaders/MatrixPerspective.vert";
  String fragmentShader = "src/main/shaders/fragment_basic.glsl";
  FloatBuffer matrixBuffer;

  float vertexPositions[] = {
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
      0.0f, 1.0f, 1.0f, 1.0f,
  };
  
  int vertexArrayObjectId;
  int positionBufferObject;
  
  // uniforms
  int offsetLocation;
  int perspectiveMatrixUnif;
  
  public static void main(String[] argv) {
    new HelloWorld().run();
  }

  protected void init() {
    // Create shaders
    Map<Integer, Integer> shaders = new HashMap<>();
    shaders.put(GL_VERTEX_SHADER, ShaderLoader.createShader(vertexShader, GL_VERTEX_SHADER));
    shaders.put(GL_FRAGMENT_SHADER, ShaderLoader.createShader(fragmentShader, GL_FRAGMENT_SHADER));
    
    // Create a program
    programId = ShaderLoader.initializeProgram(shaders);
    
    //
    offsetLocation = glGetUniformLocation(programId, "offset");
    perspectiveMatrixUnif = glGetUniformLocation(programId, "perspectiveMatrix");

    // Clean up the shaders
    shaders.values().stream().forEach(shaderId -> {
      glDeleteShader(shaderId);
    });

    // create a data buffer
    positionBufferObject = initVertexBuffer(vertexPositions);
    
    // create a vertex array
    vertexArrayObjectId = glGenVertexArrays();
    glBindVertexArray(vertexArrayObjectId);
    
    // 
    glEnable(GL_CULL_FACE);
    glCullFace(GL_BACK);
    glFrontFace(GL_CW);
    
    /*
    glEnable(GL_DEPTH_TEST);
    glDepthMask(GL_TRUE);
    glDepthFunc(GL_LEQUAL);
    glDepthRange(0.0f, 1.0f);
     */

    // set up perspective matrix
    // Based on http://wiki.lwjgl.org/index.php?title=The_Quad_with_Projection,_View_and_Model_matrices#Projection_matrix
    // and http://www.arcsynthesis.org/gltut/Positioning/Tut04%20The%20Matrix%20Has%20You.html
    float frustumScale = 1.0f;
    float zNear = 0.5f;
    float zFar = 3.0f;

    matrixBuffer = BufferUtils.createFloatBuffer(4*4);
    Matrix4f matrix = new Matrix4f();
    matrix.m00 = frustumScale; // / ((float)width / (float)height);
    matrix.m11 = frustumScale; // 5
    matrix.m22 = (zFar + zNear) / (zNear - zFar); // 10
    matrix.m23 = -1.0f; // 11
    matrix.m32 = (2 * zFar * zNear) / (zNear - zFar); // 14
    matrix.store(matrixBuffer);
    matrixBuffer.flip();
  }

  private int initVertexBuffer(float[] vertexPositions) {
    FloatBuffer vertexPositionsBuffer = BufferUtils.createFloatBuffer(vertexPositions.length);
    vertexPositionsBuffer.put(vertexPositions);
    vertexPositionsBuffer.flip();

    int positionBufferObject = glGenBuffers();
    glBindBuffer(GL_ARRAY_BUFFER, positionBufferObject);
    glBufferData(GL_ARRAY_BUFFER, vertexPositionsBuffer, GL_STATIC_DRAW);
    glBindBuffer(GL_ARRAY_BUFFER, 0);
    return positionBufferObject;
  }

  Vector2f computePositionOffsets() {
    float fLoopDuration = 5.0f;
    float fScale = 3.14159f * 2.0f / fLoopDuration;

    double fCurrTimeThroughLoop = getTime() % fLoopDuration;
    double fXOffset = Math.cos(fCurrTimeThroughLoop * fScale) * 0.5f;
    double fYOffset = Math.sin(fCurrTimeThroughLoop * fScale) * 0.5f;
    return new Vector2f((float)fXOffset, (float)fYOffset);
  }

  protected void display(double deltaTime) {
    Vector2f offset = computePositionOffsets();

    // clear the buffer
    glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    
    // set up state
    glUseProgram(programId);
    
    // set uniforms
    glUniform2f(offsetLocation, 0.5f, 0.5f);
    
    glUniformMatrix4(perspectiveMatrixUnif, false, matrixBuffer);

    glBindBuffer(GL_ARRAY_BUFFER, positionBufferObject);
    glEnableVertexAttribArray(0);
    glEnableVertexAttribArray(1);
    // Define the format and source of the vertex data.
    glVertexAttribPointer(0, 4, GL_FLOAT, false, 0, 0);
    
    // TODO: is there a better way to get this offset?  Maybe a byte buffer view?
    int colorDataOffset = vertexPositions.length * Float.BYTES / 2;
    glVertexAttribPointer(1, 4, GL_FLOAT, false, 0, colorDataOffset /* offset is in bytes */);

    glDrawArrays(GL_TRIANGLES, 0, 36);

    // clean up state
    glDisableVertexAttribArray(0);
    glDisableVertexAttribArray(1);
    glBindBuffer(GL_ARRAY_BUFFER, 0);
    glUseProgram(0);

    // draw the frame
    Display.update();
  }
}