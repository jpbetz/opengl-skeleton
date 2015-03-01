package math

import java.nio.FloatBuffer
import org.lwjgl.BufferUtils

object Vector2 {
  lazy val origin = Vector2(0f, 0f)
}

// TODO: extend AnyVal (requires scala 2.11+)
case class Vector2(x: Float, y: Float) {

  def length: Float = {
    Math.sqrt(x * x + y * y).toFloat
  }

  def normalize: Vector2 = {
    val l = 1f / length
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

  def -(vec: Vector2): Vector2 = sub(vec.x, vec.y)
  def sub(vec: Vector2): Vector2 = sub(vec.x, vec.y)
  def sub(x: Float, y: Float): Vector2 = {
    Vector2(this.x - x, this.y - y)
  }

  def *(f: Float): Vector2 = multiply(f)
  def multiply(f: Float): Vector2 = multiplyComponents(f, f)

  def multiplyComponents(vec: Vector2): Vector2 = multiplyComponents(vec.x, vec.y)
  def multiplyComponents(x: Float, y: Float): Vector2 = {
    Vector2(this.x * x, this.y * y)
  }

  def /(f: Float): Vector2 = divide(f)
  def divide(f: Float): Vector2 = divideComponents(f, f)

  def divide(vec: Vector2): Vector2 = divideComponents(vec.x, vec.y)
  def divideComponents(x: Float, y: Float): Vector2 = {
    Vector2(this.x / x, this.y / y)
  }

  def %(f: Float): Vector2 = mod(f)
  def mod(f: Float): Vector2 = Vector2(x % f, y % f)

  // TODO:  What do i need to implement to provide min, max comparisons of multiple vector2?

  def clamp(min: Vector2, max: Vector2): Vector2 = {
    // TODO: move this out to some other Float extension lib
    def clamp(f1: Float, min: Float, max: Float): Float = {
      scala.math.max(scala.math.min(f1, max), min)
    }
    Vector2(clamp(x, min.x, max.x), clamp(y, min.y, max.y))
  }

  // floor, ceil, round

  // distanceTo
  // lerp

  def copy(): Vector2 = {
    Vector2(this.x, this.y)
  }

  override def toString: String = {
    s"($x, $y)"
  }

  lazy val toBuffer: FloatBuffer = {
    val direct = BufferUtils.createFloatBuffer(2)
    direct.put(x).put(y)
    direct.flip
    direct
  }
}
