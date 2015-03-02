package state


import java.nio.{FloatBuffer, ShortBuffer}

import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL15._
import org.lwjgl.opengl.GL20._
import org.lwjgl.opengl.GL30._

// TODO: add UV support
class VertexArrayState(val verticesAndNormalsBuffer: FloatBuffer, val facesBuffer: ShortBuffer) extends DrawableState {
  var vertexArrayObjectId = 0
  var elementBufferId = 0
  var verticesAndNormalsObjectId = 0 // TODO: figure out how to propagate this from init to begin

  override def draw() {
    // TODO: allow facesBuffer offset and limit ot be provided instead of using 0 and capacity here
    glDrawElements(GL_TRIANGLE_FAN, facesBuffer.capacity(), GL_UNSIGNED_SHORT, 0)
  }

  override def init() {
    glEnable(GL_CULL_FACE)
    glCullFace(GL_BACK)
    glFrontFace(GL_CCW)

    // setup a vertex array for all vertex attributes (vertices, normals, and anything else)
    vertexArrayObjectId = glGenVertexArrays()
    glBindVertexArray(vertexArrayObjectId)

    // vertices and normals
    verticesAndNormalsObjectId = glGenBuffers()
    glBindBuffer(GL_ARRAY_BUFFER, verticesAndNormalsObjectId)
    glBufferData(GL_ARRAY_BUFFER, verticesAndNormalsBuffer, GL_STATIC_DRAW)
    glVertexAttribPointer(0, 3, GL_FLOAT, false, 6*java.lang.Float.BYTES, 0)
    glVertexAttribPointer(1, 3, GL_FLOAT, false, 6*java.lang.Float.BYTES, 3*java.lang.Float.BYTES)
    glBindBuffer(GL_ARRAY_BUFFER, 0)

    // close the vertex array
    glBindVertexArray(0)

    // elements
    elementBufferId = glGenBuffers()
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, elementBufferId)
    glBufferData(GL_ELEMENT_ARRAY_BUFFER, facesBuffer, GL_STATIC_DRAW)
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
  }

  override def begin() {
    // vertices
    glBindVertexArray(vertexArrayObjectId)
    glEnableVertexAttribArray(0)
    glEnableVertexAttribArray(1)

    // elements
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, elementBufferId)
  }

  override def end() {

    // elements
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)

    // vertices
    glDisableVertexAttribArray(1)
    glDisableVertexAttribArray(0)
    glBindVertexArray(0)
  }
}
