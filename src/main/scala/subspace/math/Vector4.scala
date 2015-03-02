package subspace.math

import java.nio.FloatBuffer

object Vector4 {
  lazy val zero = Vector4(0f, 0f, 0f, 0f)
  lazy val one = Vector4(1f, 1f, 1f, 1f)
}

case class Vector4(
    x: Float,
    y: Float,
    z: Float,
    w: Float)
  extends Vector
  with Bufferable {

  def magnitude: Float = {
    Math.sqrt(x * x + y * y + z * z + w * w).toFloat
  }

  def normalize: Vector4 = {
    val l = 1f / magnitude
    Vector4(x * l, y * l, z * l, w * l)
  }

  def unary_- : Vector4 = negate
  def negate: Vector4 = Vector4(-x, -y, -z, -w)

  def +(vec: Vector4): Vector4 = add(vec)
  def add(vec: Vector4): Vector4 = add(vec.x, vec.y, vec.z)
  def add(x: Float, y: Float, z: Float): Vector4 = {
    Vector4(this.x + x, this.y + y, this.z + z, this.w + w)
  }

  def -(vec: Vector4): Vector4 = subtract(vec)
  def subtract(vec: Vector4): Vector4 = subtract(vec.x, vec.y, vec.z, vec.w)
  def subtract(x: Float, y: Float, z: Float, w: Float): Vector4 = {
    Vector4(this.x - x, this.y - y, this.z - z, this.w - w)
  }

  // component-wise multiplication
  def *(f: Float): Vector4 = scale(f)
  def /(f: Float): Vector4 = scale(1/f)
  def scale(f: Float): Vector4 = scale(f, f, f, f)
  def scale(vec: Vector4): Vector4 = scale(vec.x, vec.y, vec.z, vec.w)
  def scale(x: Float, y: Float, z: Float, w: Float): Vector4 = {
    Vector4(this.x * x, this.y * y, this.z * z, this.w * w)
  }

  def dotProduct(vec: Vector4): Float = {
    x * vec.x + y * vec.y + z * vec.z + w * vec.w
  }

  def project(vec: Vector4): Vector4 = {
    // TODO
    throw new NotImplementedError()
  }

  def lerp(vec: Vector4, t: Float): Vector4 = {
    Vector4(
      Floats.lerp(x, vec.x, t),
      Floats.lerp(y, vec.y, t),
      Floats.lerp(z, vec.z, t),
      Floats.lerp(w, vec.w, t)
    )
  }

  def toVector3 = Vector3(x, y, z)
  def toVector2 = Vector2(x, y)

  def distanceTo(vec: Vector4): Float = {
    (this - vec).magnitude
  }

  def min(vec: Vector4): Vector4 = {
    Vector4(
      scala.math.min(x, vec.x),
      scala.math.min(y, vec.y),
      scala.math.min(z, vec.z),
      scala.math.min(w, vec.w)
    )
  }

  def max(vec: Vector4): Vector4 = {
    Vector4(
      scala.math.max(x, vec.x),
      scala.math.max(y, vec.y),
      scala.math.max(z, vec.z),
      scala.math.max(w, vec.w)
    )
  }

  def clamp(min: Vector4, max: Vector4): Vector4 = {
    Vector4(
      Floats.clamp(x, min.x, max.x),
      Floats.clamp(y, min.y, max.y),
      Floats.clamp(z, min.z, max.z),
      Floats.clamp(w, min.w, max.w)
    )
  }

  def copy(): Vector4 = {
    Vector4(this.x, this.y, this.z, this.w)
  }

  override def toString: String = {
    s"($x, $y, $z, $w)"
  }

  lazy val toBuffer: FloatBuffer = {
    val direct = Buffers.createFloatBuffer(4)
    direct.put(x).put(y).put(z).put(w)
    direct.flip
    direct
  }
}
