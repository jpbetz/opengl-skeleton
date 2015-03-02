package subspace.math

import java.nio.{ByteBuffer, ByteOrder, FloatBuffer}

trait Bufferable {
  def toBuffer: FloatBuffer
}

object Buffers {
  def createFloatBuffer(size: Int): FloatBuffer = {
    val sizeInBytes = size << 2
    ByteBuffer.allocateDirect(sizeInBytes).order(ByteOrder.nativeOrder).asFloatBuffer()
  }
}
