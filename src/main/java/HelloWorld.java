import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;

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
  String vertexShader = "src/main/shaders/vertex_basic.glsl";
  String fragmentShader = "src/main/shaders/fragment_basic.glsl";

  float vertexPositions[] = {
      0.75f, 0.75f, 0.0f, 1.0f,
      0.75f, -0.75f, 0.0f, 1.0f,
      -0.75f, -0.75f, 0.0f, 1.0f,
  };
  
  int vertexArrayObjectId;
  int positionBufferObject;

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
    
    // Clean up the shaders
    shaders.values().stream().forEach(shaderId -> {
      glDeleteShader(shaderId);
    });

    positionBufferObject = initVertexBuffer(vertexPositions);
    vertexArrayObjectId = glGenVertexArrays();
    glBindVertexArray(vertexArrayObjectId);
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

  protected void display() {
    // clear the buffer
    glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    
    // set up state
    glUseProgram(programId);
    glBindBuffer(GL_ARRAY_BUFFER, positionBufferObject);
    glEnableVertexAttribArray(0);
    // Define the format and source of the vertex data.
    glVertexAttribPointer(0, 4, GL_FLOAT, false, 0, 0);

    glDrawArrays(GL_TRIANGLES, 0, 3);

    // clean up state
    glDisableVertexAttribArray(0);
    glBindBuffer(GL_ARRAY_BUFFER, 0);
    glUseProgram(0);

    // draw the frame
    Display.update();
  }
}