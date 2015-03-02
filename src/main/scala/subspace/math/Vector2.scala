package subspace.math

import java.nio.FloatBuffer

object Vector2 {
  lazy val origin = Vector2(0f, 0f)

  def angle(from: Vector2, to: Vector2): Float = {
    // returns the angle in [TODO: units] between from and to
    // TODO: implement
    throw new NotImplementedError()
  }
}

case class Vector2(x: Float, y: Float) extends Vector with Bufferable {

  def magnitude: Float = {
    Math.sqrt(x * x + y * y).toFloat
  }

  def normalize: Vector2 = {
    val l = 1f / magnitude
    Vector2(x * l, y * l)
  }

  def dotProduct(vec: Vector2): Float = {
    x * vec.x + y * vec.y
  }

  def unary_- : Vector2 = negate
  def negate: Vector2 = Vector2(-x, -y)

  def +(vec: Vector2): Vector2 = add(vec)
  def add(vec: Vector2): Vector2 = add(vec.x, vec.y)
  def add(x: Float, y: Float): Vector2 = {
    Vector2(this.x + x, this.y + y)
  }

  def -(vec: Vector2): Vector2 = subtract(vec.x, vec.y)
  def subtract(vec: Vector2): Vector2 = subtract(vec.x, vec.y)
  def subtract(x: Float, y: Float): Vector2 = {
    Vector2(this.x - x, this.y - y)
  }

  // component-wise multiplication
  def *(f: Float): Vector2 = scale(f)
  def /(f: Float): Vector2 = scale(1/f)
  def scale(f: Float): Vector2 = scale(f, f)
  def scale(vec: Vector2): Vector2 = scale(vec.x, vec.y)
  def scale(x: Float, y: Float): Vector2 = {
    Vector2(this.x * x, this.y * y)
  }

  def clamp(min: Vector2, max: Vector2): Vector2 = {
    Vector2(
      Floats.clamp(x, min.x, max.x),
      Floats.clamp(y, min.y, max.y)
    )
  }

  def angle(vec: Vector2): Vector2 = {
    // TODO
    throw new NotImplementedError()
  }

  def distanceTo(vec: Vector2): Float = {
    (this - vec).magnitude
  }

  def lerp(vec: Vector2, t: Float): Vector2 = {
    Vector2(
      Floats.lerp(x, vec.x, t),
      Floats.lerp(y, vec.y, t)
    )
  }

  def min(vec: Vector2): Vector2 = {
    Vector2(
      scala.math.min(x, vec.x),
      scala.math.min(y, vec.y)
    )
  }

  def max(vec: Vector2): Vector2 = {
    Vector2(
      scala.math.max(x, vec.x),
      scala.math.max(y, vec.y)
    )
  }

  def copy(): Vector2 = {
    Vector2(this.x, this.y)
  }

  override def toString: String = {
    s"($x, $y)"
  }

  lazy val toBuffer: FloatBuffer = {
    val direct = Buffers.createFloatBuffer(2)
    direct.put(x).put(y)
    direct.flip
    direct
  }
}
