package com.ra4king.opengl.util.math;

//import net.indiespot.struct.cp.CopyStruct;
//import net.indiespot.struct.cp.StructField;
//import net.indiespot.struct.cp.StructType;
//import net.indiespot.struct.cp.TakeStruct;

/**
 * @author Roi Atalla
 */
//@StructType(sizeof = 16)
public class Quaternion {
	//@StructField(offset = 0)
	private float x;

	//@StructField(offset = 4)
	private float y;

	//@StructField(offset = 8)
	private float z;

	//@StructField(offset = 12)
	private float w;

	public Quaternion() {
		reset();
	}

	public Quaternion(float x, float y, float z, float w) {
		set(x, y, z, w);
	}

	public Quaternion(float angle, Vector3 vec) {
		float s = (float)Math.sin(angle / 2);

		x = vec.x() * s;
		y = vec.y() * s;
		z = vec.z() * s;
		w = (float)Math.cos(angle / 2);
	}

	public Quaternion(Quaternion q) {
		set(q);
	}

	public float x() {
		return x;
	}

	//@TakeStruct
	public Quaternion x(float x) {
		this.x = x;
		return this;
	}

	public float y() {
		return y;
	}

	//@TakeStruct
	public Quaternion y(float y) {
		this.y = y;
		return this;
	}

	public float z() {
		return z;
	}

	//@TakeStruct
	public Quaternion z(float z) {
		this.z = z;
		return this;
	}

	public float w() {
		return w;
	}

	//@TakeStruct
	public Quaternion w(float w) {
		this.w = w;
		return this;
	}

	//@TakeStruct
	public Quaternion set(float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
		return this;
	}

	//@TakeStruct
	public Quaternion set(Quaternion q) {
		return set(q.x, q.y, q.z, q.w);
	}

	//@TakeStruct
	public Quaternion reset() {
		x = y = z = 0;
		w = 1;
		return this;
	}

	public float length() {
		return (float)Math.sqrt(x * x + y * y + z * z + w * w);
	}

	//@TakeStruct
	public Quaternion normalize() {
		float length = 1f / length();
		x *= length;
		y *= length;
		z *= length;
		w *= length;
		return this;
	}

	public float dot(Quaternion q) {
		return x * q.x + y * q.y + z * q.z + w * q.w;
	}

	//@TakeStruct
	public Quaternion add(float x, float y, float z, float w) {
		this.x += x;
		this.y += y;
		this.z += z;
		this.w += w;

		return this;
	}

	//@TakeStruct
	public Quaternion add(Quaternion q) {
		return add(q.x, q.y, q.z, q.w);
	}

	//@TakeStruct
	public Quaternion sub(float x, float y, float z, float w) {
		this.x -= x;
		this.y -= y;
		this.z -= z;
		this.w -= w;

		return this;
	}

	//@TakeStruct
	public Quaternion sub(Quaternion q) {
		return sub(q.x, q.y, q.z, q.w);
	}

	//@TakeStruct
	public Quaternion mult(float f) {
		this.x *= f;
		this.y *= f;
		this.z *= f;
		this.w *= f;

		return this;
	}

	//@CopyStruct
	public Vector3 mult3(Vector3 v) {
		Vector3 quatVector = new Vector3(x, y, z);

		Vector3 uv = quatVector.cross(v);
		Vector3 uuv = quatVector.cross(uv);

		uv.mult(w * 2);
		uuv.mult(2);

		return new Vector3(v).add(uv).add(uuv);
	}

	//@TakeStruct
	public Quaternion mult(Quaternion q) {
		float xx = w * q.x + x * q.w + y * q.z - z * q.y;
		float yy = w * q.y + y * q.w + z * q.x - x * q.z;
		float zz = w * q.z + z * q.w + x * q.y - y * q.x;
		float ww = w * q.w - x * q.x - y * q.y - z * q.z;

		x = xx;
		y = yy;
		z = zz;
		w = ww;

		return this;
	}

	//@TakeStruct
	public Quaternion conjugate() {
		x *= -1;
		y *= -1;
		z *= -1;

		return this;
	}

	//@TakeStruct
	public Quaternion inverse() {
		return normalize().conjugate();
	}

	public void toMatrix(Matrix4 mat4) {
		mat4.set(new float[] {
				1 - 2 * y * y - 2 * z * z, 2 * x * y + 2 * w * z, 2 * x * z - 2 * w * y, 0,
				2 * x * y - 2 * w * z, 1 - 2 * x * x - 2 * z * z, 2 * y * z + 2 * w * x, 0,
				2 * x * z + 2 * w * y, 2 * y * z - 2 * w * x, 1 - 2 * x * x - 2 * y * y, 0,
				0, 0, 0, 1,
		});
	}

	//@CopyStruct
	public Matrix4 toMatrix() {
		Matrix4 temp = new Matrix4();
		toMatrix(temp);
		return temp;
	}
}
