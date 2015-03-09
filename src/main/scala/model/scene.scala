package model

import com.github.jpbetz.subspace.{Quaternion, Vector3, Matrix4x4}


// TODO: use quaternion for angle?
class SceneObject(var position: Vector3, var angle: Vector3) {

  def degreesToRadians(degrees: Float): Float = {
    degrees * scala.math.Pi.toFloat / 180
  }

  def toMatrix = {
    val z = Quaternion.forAxisAngle(Vector3(0, 0, 1), degreesToRadians(angle.z))
    val y = Quaternion.forAxisAngle(Vector3(0, 1, 0), degreesToRadians(angle.y))
    val x = Quaternion.forAxisAngle(Vector3(1, 0, 0), degreesToRadians(angle.z))
    val quat = z * y * x
    //Matrix4x4.forTranslationRotationScale(position, quat, Vector3.fill(1))
    val rotate = Matrix4x4.forRotation(quat)
    val translate = Matrix4x4.forTranslation(position)

    translate * rotate
  }
}

class SceneModel(position: Vector3, angle: Vector3) extends SceneObject(position, angle)

class SceneCamera(position: Vector3, angle: Vector3) extends SceneObject(position, angle)
