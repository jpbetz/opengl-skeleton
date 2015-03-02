package model

import subspace.math.{Vector3, Matrix4x4}

// TODO: use quaternion for angle?
class SceneObject(var position: Vector3, var angle: Vector3) {

  def degreesToRadians(degrees: Float): Float = {
    degrees * scala.math.Pi.toFloat / 180
  }

  def toMatrix = {
    val rotate = Matrix4x4.identity
      .rotateByAxisAngle(degreesToRadians(angle.z), Vector3(0, 0, 1))
      .rotateByAxisAngle(degreesToRadians(angle.y), Vector3(0, 1, 0))
      .rotateByAxisAngle(degreesToRadians(angle.x), Vector3(1, 0, 0))

    val translate = Matrix4x4.identity.translate(position)

    rotate * translate
  }
}

class SceneModel(position: Vector3, angle: Vector3) extends SceneObject(position, angle)

class SceneCamera(position: Vector3, angle: Vector3) extends SceneObject(position, angle)
