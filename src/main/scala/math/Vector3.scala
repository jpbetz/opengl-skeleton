package math

import java.nio.FloatBuffer
import org.lwjgl.BufferUtils

object Vector3 {
  lazy val origin = Vector3(0f, 0f, 0f)
}

// TODO: extend AnyVal (requires scala 3.11+)
case class Vector3(x: Float, y: Float, z: Float) {

  def length: Float = {
    Math.sqrt(x * x + y * y + z * z).toFloat
  }

  def normalize: Vector3 = {
    val l = 1f / length
    Vector3(x * l, y * l, z * l)
  }

  def dotProduct(vec: Vector3): Float = {
    x * vec.x + y * vec.y + z * vec.z
  }

  def unary_- : Vector3 = negate
  def negate: Vector3 = Vector3(-x, -y, -z)

  def +(vec: Vector3): Vector3 = add(vec)
  def add(vec: Vector3): Vector3 = add(vec.x, vec.y, vec.z)
  def add(x: Float, y: Float, z: Float): Vector3 = {
    Vector3(this.x + x, this.y + y, this.z + z)
  }

  def -(vec: Vector3): Vector3 = sub(vec)
  def sub(vec: Vector3): Vector3 = sub(vec.x, vec.y, vec.z)
  def sub(x: Float, y: Float, z: Float): Vector3 = {
    Vector3(this.x - x, this.y - y, this.z - z)
  }

  def *(f: Float): Vector3 = multiply(f)
  def multiply(f: Float): Vector3 = multiplyComponents(f, f, f)

  def multiplyComponents(vec: Vector3): Vector3 = multiplyComponents(vec.x, vec.y, vec.z)
  def multiplyComponents(x: Float, y: Float, z: Float): Vector3 = {
    Vector3(this.x * x, this.y * y, this.z * z)
  }

  def /(f: Float): Vector3 = divide(f)
  def divide(f: Float): Vector3 = divideComponents(f, f, f)

  def divideComponents(vec: Vector3): Vector3 = divideComponents(vec.x, vec.y, vec.z)
  def divideComponents(x: Float, y: Float, z: Float): Vector3 = {
    Vector3(this.x / x, this.y / y, this.z / z)
  }

  def crossProduct(vec: Vector3): Vector3 = {
    Vector3(
      y * vec.z - z * vec.y,
      z * vec.x - x * vec.z,
      x * vec.y - y * vec.x
    )
  }

  def %(f: Float): Vector3 = mod(f)
  def mod(f: Float): Vector3 = Vector3(x % f, y % f, z % f)

  // TODO:  What do i need to implement to provide min, max comparisons of multiple vector3?

  def clamp(min: Vector3, max: Vector3): Vector3 = {
    // TODO: move this out to some other Float extension lib
    def clamp(f1: Float, min: Float, max: Float): Float = {
      scala.math.max(scala.math.min(f1, max), min)
    }
    Vector3(clamp(x, min.x, max.x), clamp(y, min.y, max.y), clamp(z, min.z, max.z))
  }

  // floor, ceil, round

  // distanceTo
  // lerp

  // applyEuler ?

  // TODO: needed?
  def applyAxisAngle(axis: Vector3, angle: Float): Vector3 = {
    applyQuaternion(Quaternion.fromAxisAngle(axis, angle))
  }

  def applyQuaternion(q: Quaternion): Vector3 = {
    val ix =  q.w * x + q.y * z - q.z * y
    val iy =  q.w * y + q.z * x - q.x * z
    val iz =  q.w * z + q.x * y - q.y * x
    val iw = - q.x * x - q.y * y - q.z * z

    Vector3(
      ix * q.w + iw * - q.x + iy * - q.z - iz * - q.y,
      iy * q.w + iw * - q.y + iz * - q.x - ix * - q.z,
      iz * q.w + iw * - q.z + ix * - q.y - iy * - q.x
    )
  }

  // applyProjection?
  // project, unproject
  // transformDirection
  // projectOnVector, projectOnPlane
  // reflect
  // angleTo
  // distanceTo

  def copy(): Vector3 = {
    Vector3(this.x, this.y, this.z)
  }

  override def toString: String = {
    s"($x, $y, $z)"
  }

  lazy val toBuffer: FloatBuffer = {
    val direct = BufferUtils.createFloatBuffer(3)
    direct.put(x).put(y).put(z)
    direct.flip
    direct
  }
}
