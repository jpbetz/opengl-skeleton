package com.ra4king.opengl.util.math;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

//import net.indiespot.struct.cp.Struct;
//import net.indiespot.struct.cp.StructField;
//import net.indiespot.struct.cp.StructType;
//import net.indiespot.struct.cp.TakeStruct;

/**
 * @author Roi Atalla
 */
//@StructType
public class Vector4 {
//	@StructField
	private float x;

//	@StructField
	private float y;

//	@StructField
	private float z;

//	@StructField
	private float w;

//	public static final Vector4 RIGHT = Struct.malloc(Vector4.class).set(1, 0, 0, 1);
//	public static final Vector4 LEFT = Struct.malloc(Vector4.class).set(-1, 0, 0, 1);
//	public static final Vector4 UP = Struct.malloc(Vector4.class).set(0, 1, 0, 1);
//	public static final Vector4 DOWN = Struct.malloc(Vector4.class).set(0, -1, 0, 1);
//	public static final Vector4 FORWARD = Struct.malloc(Vector4.class).set(0, 0, -1, 1);
//	public static final Vector4 BACK = Struct.malloc(Vector4.class).set(0, 0, 1, 1);

	public Vector4() {
		set(0, 0, 0, 0);
	}

	public Vector4(float v) {
		this(v, v, v, v);
	}

	public Vector4(float x, float y, float z, float w) {
		set(x, y, z, w);
	}

	public Vector4(Vector2 vec, float z, float w) {
		set(vec, z, w);
	}

	public Vector4(Vector3 vec, float w) {
		set(vec, w);
	}

	public Vector4(Vector4 vec) {
		set(vec);
	}

	public float x() {
		return x;
	}

//	@TakeStruct
	public Vector4 x(float x) {
		this.x = x;
		return this;
	}

	public float y() {
		return y;
	}

//	@TakeStruct
	public Vector4 y(float y) {
		this.y = y;
		return this;
	}

	public float z() {
		return z;
	}

//	@TakeStruct
	public Vector4 z(float z) {
		this.z = z;
		return this;
	}

	public float w() {
		return w;
	}

//	@TakeStruct
	public Vector4 w(float w) {
		this.w = w;
		return this;
	}

	public boolean equals(Vector4 v) {
		return x == v.x && y == v.y && z == v.z && w == v.w;
	}

//	@TakeStruct
	public Vector4 set(float f) {
		return set(f, f, f, f);
	}

//	@TakeStruct
	public Vector4 set(float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
		return this;
	}

//	@TakeStruct
	public Vector4 set2(Vector2 vec) {
		return set(vec, 0, 0);
	}

//	@TakeStruct
	public Vector4 set(Vector2 vec, float z, float w) {
		return set(vec.x(), vec.y(), z, w);
	}

//	@TakeStruct
	public Vector4 set3(Vector3 vec) {
		return set(vec, 0);
	}

//	@TakeStruct
	public Vector4 set(Vector3 vec, float w) {
		return set(vec.x(), vec.y(), vec.z(), w);
	}

//	@TakeStruct
	public Vector4 set(Vector4 vec) {
		//Struct.copy(Vector4.class, vec, this);
        this.x = vec.x;
        this.y = vec.y;
        this.z = vec.z;
        this.w = vec.w;
		return this;
	}

//	@TakeStruct
	public Vector4 reset() {
		x = y = z = w = 0;
		return this;
	}

	public float length() {
		return (float)Math.sqrt(lengthSquared());
	}

	public float lengthSquared() {
		return x * x + y * y + z * z + w * w;
	}

//	@TakeStruct
	public Vector4 normalize() {
		float length = 1f / length();
		x *= length;
		y *= length;
		z *= length;
		w *= length;
		return this;
	}

	public float dot(Vector4 vec) {
		return x * vec.x + y * vec.y + z * vec.z + w * vec.w;
	}

//	@TakeStruct
	public Vector4 add(float x, float y, float z, float w) {
		this.x += x;
		this.y += y;
		this.z += z;
		this.w += w;
		return this;
	}

//	@TakeStruct
	public Vector4 add(Vector4 vec) {
		return add(vec.x, vec.y, vec.z, vec.w);
	}

//	@TakeStruct
	public Vector4 sub(float x, float y, float z, float w) {
		this.x -= x;
		this.y -= y;
		this.z -= z;
		this.w -= w;
		return this;
	}

//	@TakeStruct
	public Vector4 sub(Vector4 vec) {
		return sub(vec.x, vec.y, vec.z, vec.w);
	}

//	@TakeStruct
	public Vector4 mult(float f) {
		return mult(f, f, f, f);
	}

//	@TakeStruct
	public Vector4 mult(float x, float y, float z, float w) {
		this.x *= x;
		this.y *= y;
		this.z *= z;
		this.w *= w;
		return this;
	}

//	@TakeStruct
	public Vector4 mult(Vector4 vec) {
		return mult(vec.x, vec.y, vec.z, vec.w);
	}

//	@TakeStruct
	public Vector4 divide(float f) {
		return divide(f, f, f, f);
	}

//	@TakeStruct
	public Vector4 divide(float x, float y, float z, float w) {
		this.x /= x;
		this.y /= y;
		this.z /= z;
		this.w /= w;
		return this;
	}

//	@TakeStruct
	public Vector4 divide(Vector4 vec) {
		return divide(vec.x, vec.y, vec.z, vec.w);
	}

//	@TakeStruct
	public Vector4 mod(float f) {
		x %= f;
		y %= f;
		z %= f;
		w %= f;

		return this;
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ", " + z + ", " + w + ")";
	}

	private final FloatBuffer direct = BufferUtils.createFloatBuffer(4);

	public FloatBuffer toBuffer() {
		direct.clear();
		direct.put(x).put(y).put(z).put(w);
		direct.flip();
		return direct;
	}
}
