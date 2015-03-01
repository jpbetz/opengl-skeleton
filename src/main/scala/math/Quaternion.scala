package math

object Quaternion {
  def fromAxisAngle(axis: Vector3, angle: Float): Quaternion = {
    val halfAngle = angle / 2
    val s = Math.sin(halfAngle).toFloat
    Quaternion(axis.x * s, axis.y * s, axis.z * s, scala.math.cos(halfAngle).toFloat)
  }
}

case class Quaternion(
    override val x: Float,
    override val y: Float,
    override val z: Float,
    override val w: Float) 
  extends Vector4(x, y, z, w)
