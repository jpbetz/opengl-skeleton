import org.lwjgl.util.glu.GLU;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL32.*;

public class ShaderLoader {

  public static int createShader(String filename, int type) {
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
      throw new IllegalArgumentException("Could not read file: " + e.getMessage());
    }

    shaderID = glCreateShader(type);
    glShaderSource(shaderID, shaderSource);
    glCompileShader(shaderID);

    if (glGetShaderi(shaderID, GL_COMPILE_STATUS) == GL_FALSE) {
      int maxLength = glGetShaderi(shaderID, GL_INFO_LOG_LENGTH);
      String info = glGetShaderInfoLog(shaderID, maxLength);
      throw new IllegalArgumentException("Could not compile " + file.getAbsolutePath() + ": " + info);
    }

    checkForGlError("createShader");

    return shaderID;
  }

  public static int initializeProgram(Map<Integer, Integer> shaderTypeToId) {

    // Create a new shader program that links both shaders
    int programId = glCreateProgram();

    shaderTypeToId.entrySet().stream().forEach(entry -> {
      glAttachShader(programId, entry.getValue());
      checkForGlError("attachShader:" + shaderTypeToString(entry.getKey()));
    });

    glLinkProgram(programId);
    
    if(glGetProgrami(programId, GL_LINK_STATUS) == GL_FALSE)  {
      int infoLogLength = glGetProgrami(programId, GL_INFO_LOG_LENGTH);
      String infoLog = glGetProgramInfoLog(programId, infoLogLength);
      throw new IllegalArgumentException("Linker failure: " + infoLog);
    }

    shaderTypeToId.values().stream().forEach(shaderId -> {
      glDetachShader(programId, shaderId);
    });

    checkForGlError("linkProgram");

    glValidateProgram(programId);

    checkForGlError("initializeProgram");
    return programId;
  }
  
  private static final String shaderTypeToString(int shaderType) {
    switch(shaderType)
    {
      case GL_VERTEX_SHADER: return "vertex";
      case GL_GEOMETRY_SHADER: return "geometry";
      case GL_FRAGMENT_SHADER: return "fragment";
      default: return "UNKNOWN";
    }
  }

  private static void checkForGlError(String errorMessage) {
    int errorValue = glGetError();

    if (errorValue != GL_NO_ERROR) {
      String errorString = GLU.gluErrorString(errorValue);
      throw new IllegalStateException("GL Error - " + errorMessage + ": " + errorString);
    }
  }
}
