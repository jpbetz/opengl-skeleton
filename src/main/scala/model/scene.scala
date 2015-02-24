package model


import org.lwjgl.util.vector.{Matrix4f, Vector3f}

// TODO: use quaternion for angle?
class SceneObject(val position: Vector3f, val angle: Vector3f) {

  def degreesToRadians(degrees: Float): Float = {
    degrees * math.Pi.toFloat / 180
  }

  def toMatrix = {
    val matrix = new Matrix4f()
    matrix.rotate(degreesToRadians(angle.z), new Vector3f(0, 0, 1))
    matrix.rotate(degreesToRadians(angle.y), new Vector3f(0, 1, 0))
    matrix.rotate(degreesToRadians(angle.x), new Vector3f(1, 0, 0))
    matrix.translate(position)
    matrix
  }
}

class SceneModel(override val position: Vector3f, override val angle: Vector3f) extends SceneObject(position, angle)

class SceneCamera(override val position: Vector3f, override val angle: Vector3f) extends SceneObject(position, angle)
