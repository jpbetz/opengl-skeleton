package math

object Quaternion {
  def fromAxisAngle(axis: Vector3, angle: Float): Quaternion = {
    val halfAngle = angle / 2
    val s = Math.sin(halfAngle).toFloat
    Quaternion(axis.x * s, axis.y * s, axis.z * s, scala.math.cos(halfAngle).toFloat)
  }
}

// TODO: figure out how to best define this type.  Or just use Vector4 everywhere a quaternion is needed
case class Quaternion(x: Float, y: Float, z: Float, w: Float) {
  def toVector: Vector4 = Vector4(x, y, z, w)
}
