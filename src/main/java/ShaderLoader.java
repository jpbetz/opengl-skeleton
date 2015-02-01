import org.lwjgl.opengl.Display;
import org.lwjgl.util.glu.GLU;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL11.GL_NO_ERROR;
import static org.lwjgl.opengl.GL11.glGetError;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glValidateProgram;

public class ShaderLoader {

  private static int createShader(String filename, int type) {
    StringBuilder shaderSource = new StringBuilder();
    int shaderID = 0;

    File file = new File(filename);

    if(!file.exists()) throw new IllegalArgumentException("File not found: " + file.getAbsolutePath());

    try {
      BufferedReader reader = new BufferedReader(new FileReader(file));
      String line;
      while ((line = reader.readLine()) != null) {
        shaderSource.append(line).append("\n");
      }
      reader.close();
    } catch (IOException e) {
      System.err.println("Could not read file: " + e.getMessage());
      e.printStackTrace();
      System.exit(-1);
    }

    shaderID = glCreateShader(type);
    glShaderSource(shaderID, shaderSource);
    glCompileShader(shaderID);

    if (glGetShaderi(shaderID, GL_COMPILE_STATUS) == GL_FALSE) {
      int maxLength = glGetShaderi(shaderID, GL_INFO_LOG_LENGTH);
      String info = glGetShaderInfoLog(shaderID, maxLength);
      throw new IllegalArgumentException("Could not compile " + file.getAbsolutePath() + ": " + info);
    }

    exitOnGLError("createShader");

    return shaderID;
  }

  public static int initializeProgram(String vertexShaderFilename, String fragmentShaderFilename) {
    // Load the vertex shader
    int vsId = createShader(vertexShaderFilename, GL_VERTEX_SHADER);
    // Load the fragment shader
    int fsId = createShader(fragmentShaderFilename, GL_FRAGMENT_SHADER);

    // Create a new shader program that links both shaders
    int pId = glCreateProgram();

    glAttachShader(pId, vsId);
    exitOnGLError("attachVertexShader");

    glAttachShader(pId, fsId);
    exitOnGLError("attachFragmentShader");;

    glLinkProgram(pId);
    if(glGetProgrami(pId, GL_LINK_STATUS) == GL_FALSE)
    {
      int infoLogLength = glGetProgrami(pId, GL_INFO_LOG_LENGTH);
      String infoLog = glGetProgramInfoLog(pId, infoLogLength);
      throw new IllegalArgumentException("Linker failure: " + infoLog);
    }

    exitOnGLError("linkProgram");

    glValidateProgram(pId);

    exitOnGLError("initializeProgram");
    return pId;
  }

  private static void exitOnGLError(String errorMessage) {
    int errorValue = glGetError();

    if (errorValue != GL_NO_ERROR) {
      String errorString = GLU.gluErrorString(errorValue);
      System.err.println("ERROR - " + errorMessage + ": " + errorString);

      if (Display.isCreated()) Display.destroy();
      System.exit(-1);
    }
  }
}
