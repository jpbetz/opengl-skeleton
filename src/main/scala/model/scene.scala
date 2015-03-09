package model

import com.github.jpbetz.subspace.{Quaternion, Vector3, Matrix4x4}


// TODO: use quaternion for angle?
class SceneObject(var position: Vector3, var angle: Vector3) {

  def degreesToRadians(degrees: Float): Float = {
    degrees * scala.math.Pi.toFloat / 180
  }

  def toMatrix = {
    val degreesToRadians = scala.math.Pi.toFloat / 180
    val quat = Quaternion.forEuler(Vector3(angle.x, angle.y, angle.z) * degreesToRadians)
    Matrix4x4.forTranslationRotationScale(position, quat, Vector3.fill(1))
  }
}

class SceneModel(position: Vector3, angle: Vector3) extends SceneObject(position, angle)

class SceneCamera(position: Vector3, angle: Vector3) extends SceneObject(position, angle)
