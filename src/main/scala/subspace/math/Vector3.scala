package subspace.math

import java.nio.FloatBuffer

object Vector3 {
  lazy val zero = Vector3(0f, 0f, 0f)
  lazy val one = Vector3(1f, 1f, 1f)

  lazy val back = Vector3(0, 0, -1)
  lazy val down = Vector3(0, -1, 0)
  lazy val forward = Vector3(0, 0, 1)
  lazy val left = Vector3(-1, 0, 0)
  lazy val right = Vector3(1, 0, 0)
  lazy val up = Vector3(0, 1, 0)
}

case class Vector3(x: Float, y: Float, z: Float) extends Vector with Bufferable {

  def magnitude: Float = {
    Math.sqrt(x * x + y * y + z * z).toFloat
  }

  def normalize: Vector3 = {
    val l = 1f / magnitude
    Vector3(x * l, y * l, z * l)
  }

  def unary_- : Vector3 = negate
  def negate: Vector3 = Vector3(-x, -y, -z)

  def +(vec: Vector3): Vector3 = add(vec)
  def add(vec: Vector3): Vector3 = add(vec.x, vec.y, vec.z)
  def add(x: Float, y: Float, z: Float): Vector3 = {
    Vector3(this.x + x, this.y + y, this.z + z)
  }

  def -(vec: Vector3): Vector3 = subtract(vec)
  def subtract(vec: Vector3): Vector3 = subtract(vec.x, vec.y, vec.z)
  def subtract(x: Float, y: Float, z: Float): Vector3 = {
    Vector3(this.x - x, this.y - y, this.z - z)
  }

  // component-wise multiplication
  def *(f: Float): Vector3 = scale(f)
  def /(f: Float): Vector3 = scale(1/f)
  def scale(f: Float): Vector3 = scale(f, f, f)
  def scale(vec: Vector3): Vector3 = scale(vec.x, vec.y, vec.z)
  def scale(x: Float, y: Float, z: Float): Vector3 = {
    Vector3(this.x * x, this.y * y, this.z * z)
  }

  def crossProduct(vec: Vector3): Vector3 = {
    Vector3(
      y * vec.z - z * vec.y,
      z * vec.x - x * vec.z,
      x * vec.y - y * vec.x
    )
  }

  def dotProduct(vec: Vector3): Float = {
    x * vec.x + y * vec.y + z * vec.z
  }

  def clamp(min: Vector3, max: Vector3): Vector3 = {
    Vector3(
      Floats.clamp(x, min.x, max.x),
      Floats.clamp(y, min.y, max.y),
      Floats.clamp(z, min.z, max.z)
    )
  }

  // TODO applyEuler?
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

  // angle between the two vectors
  def angle(vec: Vector3): Vector3 = {
    throw new NotImplementedError()
  }

  def distanceTo(vec: Vector3): Float = {
    (this - vec).magnitude
  }

  def lerp(vec: Vector3, t: Float): Vector3 = {
    Vector3(
      Floats.lerp(x, vec.x, t),
      Floats.lerp(y, vec.y, t),
      Floats.lerp(z, vec.z, t)
    )
  }

  def min(vec: Vector3): Vector3 = {
    Vector3(
      scala.math.min(x, vec.x),
      scala.math.min(y, vec.y),
      scala.math.min(z, vec.z)
    )
  }

  def max(vec: Vector3): Vector3 = {
    Vector3(
      scala.math.max(x, vec.x),
      scala.math.max(y, vec.y),
      scala.math.max(z, vec.z)
    )
  }

  def orthoNormalize(vec: Vector3): Vector3 = {
    // TODO: Makes vectors normalized and orthogonal to each other.
    throw new NotImplementedError()
  }

  def project(onNormal: Vector3): Vector3 = {
    // TODO: Projects a vector onto another vector.
    throw new NotImplementedError()
  }

  def projectOntoPlane(planeNormal: Vector3): Vector3 = {
    // TODO: Projects a vector onto a plane defined by a normal orthogonal to the plane.
    throw new NotImplementedError()
  }

  def reflect(inNormal: Vector3): Vector3 = {
    // TODO: Reflects a vector off the plane defined by a normal.
    throw new NotImplementedError()
  }

  def rotateTowards(target: Vector3, maxRadiansDelta: Float, maxMagnitudeDelta: Float): Vector3 = {
    //Rotates a vector current towards target.
    //This function is similar to MoveTowards except that the vector is treated as a direction rather than a position. The current vector will be rotated round toward the target direction by an angle of maxRadiansDelta, although it will land exactly on the target rather than overshoot. If the magnitudes of current and target are different then the magnitude of the result will be linearly interpolated during the rotation. If a negative value is used for maxRadiansDelta, the vector will rotate away from target/ until it is pointing in exactly the opposite direction, then stop.
    // TODO
    throw new NotImplementedError()
  }

  // http://docs.unity3d.com/ScriptReference/Vector3.Slerp.html
  // http://docs.unity3d.com/ScriptReference/Vector3.SmoothDamp.html

  def toVector2 = Vector2(x, y)

  def copy(): Vector3 = {
    Vector3(this.x, this.y, this.z)
  }

  override def toString: String = {
    s"($x, $y, $z)"
  }

  lazy val toBuffer: FloatBuffer = {
    val direct = Buffers.createFloatBuffer(3)
    direct.put(x).put(y).put(z)
    direct.flip
    direct
  }
}
