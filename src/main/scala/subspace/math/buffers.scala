package subspace.math

import java.nio.{ByteBuffer, ByteOrder, FloatBuffer}

trait Bufferable {
  def allocateBuffer: FloatBuffer
  def updateBuffer(buffer: FloatBuffer): Unit
}

object Buffers {
  def createFloatBuffer(size: Int): FloatBuffer = {
    val sizeInBytes = size << 2
    ByteBuffer.allocateDirect(sizeInBytes).order(ByteOrder.nativeOrder).asFloatBuffer()
  }
}
