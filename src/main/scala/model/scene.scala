package model


import com.ra4king.opengl.util.math.Vector3
import com.ra4king.opengl.util.math.Matrix4

// TODO: use quaternion for angle?
class SceneObject(val position: Vector3, val angle: Vector3) {

  def degreesToRadians(degrees: Float): Float = {
    degrees * math.Pi.toFloat / 180
  }

  def toMatrix = {
    new Matrix4().clearToIdentity()
      .rotate(degreesToRadians(angle.z), new Vector3(0, 0, 1))
      .rotate(degreesToRadians(angle.y), new Vector3(0, 1, 0))
      .rotate(degreesToRadians(angle.x), new Vector3(1, 0, 0))
      .translate(position)
  }
}

class SceneModel(override val position: Vector3, override val angle: Vector3) extends SceneObject(position, angle) {}

class SceneCamera(override val position: Vector3, override val angle: Vector3) extends SceneObject(position, angle)
