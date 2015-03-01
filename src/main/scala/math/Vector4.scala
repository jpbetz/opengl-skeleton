package math

import java.nio.FloatBuffer
import org.lwjgl.BufferUtils

object Vector4 {
  lazy val origin = Vector4(0f, 0f, 0f, 0f)
}

// TODO: extend AnyVal (requires scala 3.11+)
case class Vector4(x: Float, y: Float, z: Float, w: Float) {

  def length: Float = {
    Math.sqrt(x * x + y * y + z * z + w * w).toFloat
  }

  def normalize: Vector4 = {
    val l = 1f / length
    Vector4(x * l, y * l, z * l, w * l)
  }

  def dotProduct(vec: Vector4): Float = {
    x * vec.x + y * vec.y + z * vec.z + w * vec.w
  }

  def unary_- : Vector4 = negate
  def negate: Vector4 = Vector4(-x, -y, -z, -w)

  def +(vec: Vector4): Vector4 = add(vec)
  def add(vec: Vector4): Vector4 = add(vec.x, vec.y, vec.z)
  def add(x: Float, y: Float, z: Float): Vector4 = {
    Vector4(this.x + x, this.y + y, this.z + z, this.w + w)
  }

  def -(vec: Vector4): Vector4 = sub(vec)
  def sub(vec: Vector4): Vector4 = sub(vec.x, vec.y, vec.z, vec.w)
  def sub(x: Float, y: Float, z: Float, w: Float): Vector4 = {
    Vector4(this.x - x, this.y - y, this.z - z, this.w - w)
  }

  def *(f: Float): Vector4 = multiply(f)
  def multiply(f: Float): Vector4 = multiplyComponents(f, f, f, f)

  def multiplyComponents(vec: Vector4): Vector4 = multiplyComponents(vec.x, vec.y, vec.z, vec.w)
  def multiplyComponents(x: Float, y: Float, z: Float, w: Float): Vector4 = {
    Vector4(this.x * x, this.y * y, this.z * z, this.w * w)
  }

  def /(f: Float): Vector4 = divide(f)
  def divide(f: Float): Vector4 = divideComponents(f, f, f, f)

  def divideComponents(vec: Vector4): Vector4 = divideComponents(vec.x, vec.y, vec.z, vec.w)
  def divideComponents(x: Float, y: Float, z: Float, w: Float): Vector4 = {
    Vector4(this.x / x, this.y / y, this.z / z, this.w / w)
  }

  def %(f: Float): Vector4 = mod(f)
  def mod(f: Float): Vector4 = Vector4(x % f, y % f, z % f, w % f)

  // TODO:  What do i need to implement to provide min, max comparisons of multiple Vector4?

  def clamp(min: Vector4, max: Vector4): Vector4 = {
    // TODO: move this out to some other Float extension lib
    def clamp(f1: Float, min: Float, max: Float): Float = {
      scala.math.max(scala.math.min(f1, max), min)
    }
    Vector4(clamp(x, min.x, max.x), clamp(y, min.y, max.y), clamp(z, min.z, max.z), clamp(w, min.w, max.w))
  }

  def copy(): Vector4 = {
    Vector4(this.x, this.y, this.z, this.w)
  }

  override def toString: String = {
    s"($x, $y, $z, $w)"
  }

  lazy val toBuffer: FloatBuffer = {
    val direct = BufferUtils.createFloatBuffer(4)
    direct.put(x).put(y).put(z).put(w)
    direct.flip
    direct
  }
}
