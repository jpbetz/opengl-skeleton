package subspace.math

import java.nio.FloatBuffer

object Quaternion {
  def fromAxisAngle(axis: Vector3, angle: Float): Quaternion = {
    val halfAngle = angle / 2
    val s = Math.sin(halfAngle).toFloat
    Quaternion(axis.x * s, axis.y * s, axis.z * s, scala.math.cos(halfAngle).toFloat)
  }
}

case class Quaternion(x: Float, y: Float, z: Float, w: Float) extends Bufferable {
  lazy val toVector: Vector4 = Vector4(x, y, z, w)
  lazy val toBuffer: FloatBuffer = toVector.toBuffer
}
