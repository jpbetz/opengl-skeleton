package com.ra4king.opengl.util.math;

import java.nio.FloatBuffer;
import java.util.Arrays;

import org.lwjgl.BufferUtils;

//import net.indiespot.struct.cp.CopyStruct;
//import net.indiespot.struct.cp.Struct;
//import net.indiespot.struct.cp.StructField;
//import net.indiespot.struct.cp.StructType;
//import net.indiespot.struct.cp.TakeStruct;

/**
 * @author Roi Atalla
 */
//@StructType
public class Matrix3 {
	public static final int LENGTH = 9;

	//@StructField(length = LENGTH)
	private float[] matrix = new float[] { 0,0,0, 0,0,0, 0,0,0 };

	public Matrix3() {
		clear();
	}

	public Matrix3(float[] m) {
		this();
		set(m);
	}

	public Matrix3(Matrix3 m) {
		this();
		set(m);
	}

	////@TakeStruct
	public Matrix3 clear() {
		for(int a = 0; a < LENGTH; a++)
			matrix[a] = 0;

		return this;
	}

	////@TakeStruct
	public Matrix3 clearToIdentity() {
		return clear().put(0, 1).put(4, 1).put(8, 1);
	}

	public float get(int index) {
		return matrix[index];
	}

	public float get(int col, int row) {
		return matrix[col * 3 + row];
	}

	////@TakeStruct
	public Matrix3 put(int index, float f) {
		matrix[index] = f;
		return this;
	}

	//@TakeStruct
	public Matrix3 put(int col, int row, float f) {
		matrix[col * 3 + row] = f;
		return this;
	}

	//@TakeStruct
	public Matrix3 putColumn(int index, Vector3 v) {
		put(index, 0, v.x());
		put(index, 1, v.y());
		put(index, 2, v.z());
		return this;
	}

	//@TakeStruct
	public Matrix3 set(float[] m) {
		if(m.length < LENGTH)
			throw new IllegalArgumentException("float array must have at least " + LENGTH + " values.");

		for(int a = 0; a < m.length && a < LENGTH; a++) {
			matrix[a] = m[a];
		}

		return this;
	}

	//@TakeStruct
	public Matrix3 set(Matrix3 m) {
        //Struct.copy(Matrix3.class, m, this);
        this.matrix = m.matrix.clone();
		return this;
	}

	//@TakeStruct
	public Matrix3 set4x4(Matrix4 m) {
		for(int a = 0; a < 3; a++) {
			put(a, 0, m.get(a, 0));
			put(a, 1, m.get(a, 1));
			put(a, 2, m.get(a, 2));
		}

		return this;
	}

	//@TakeStruct
	public Matrix3 mult(float f) {
		for(int a = 0; a < LENGTH; a++)
			put(a, get(a) * f);

		return this;
	}

	//@TakeStruct
	public Matrix3 mult(float[] m) {
		if(m.length < LENGTH)
			throw new IllegalArgumentException("float array must have at least " + LENGTH + " values.");

		return mult(new Matrix3(m));
	}

	//@TakeStruct
	public Matrix3 mult(Matrix3 m) {
		Matrix3 temp = new Matrix3();

		for(int a = 0; a < 3; a++) {
			temp.put(a, 0, get(0) * m.get(a, 0) + get(3) * m.get(a, 1) + get(6) * m.get(a, 2));
			temp.put(a, 1, get(1) * m.get(a, 0) + get(4) * m.get(a, 1) + get(7) * m.get(a, 2));
			temp.put(a, 2, get(2) * m.get(a, 0) + get(5) * m.get(a, 1) + get(8) * m.get(a, 2));
		}

		set(temp);

		return this;
	}

	//@CopyStruct
	public Vector3 mult3(Vector3 vec) {
		return new Vector3(get(0) * vec.x() + get(3) * vec.y() + get(6) * vec.z(),
				get(1) * vec.x() + get(4) * vec.y() + get(7) * vec.z(),
				get(2) * vec.x() + get(5) * vec.y() + get(8) * vec.z());
	}

	//@TakeStruct
	public Matrix3 transpose() {
		float old = get(1);
		put(1, get(3));
		put(3, old);

		old = get(2);
		put(2, get(6));
		put(6, old);

		old = get(5);
		put(5, get(7));
		put(7, old);

		return this;
	}

	public float determinant() {
		return +get(0) * get(4) * get(8) + get(3) * get(7) * get(2) + get(6) * get(1) * get(5)
				- get(2) * get(4) * get(6) - get(5) * get(7) * get(0) - get(8) * get(1) * get(3);
	}

	//@TakeStruct
	public Matrix3 inverse() {
		Matrix3 inv = new Matrix3();

		inv.put(0, +(get(4) * get(8) - get(5) * get(7)));
		inv.put(1, -(get(3) * get(8) - get(5) * get(6)));
		inv.put(2, +(get(3) * get(7) - get(4) * get(6)));

		inv.put(3, -(get(1) * get(8) - get(2) * get(7)));
		inv.put(4, +(get(0) * get(8) - get(2) * get(6)));
		inv.put(5, -(get(0) * get(7) - get(1) * get(6)));

		inv.put(6, +(get(1) * get(5) - get(2) * get(4)));
		inv.put(7, -(get(0) * get(5) - get(2) * get(3)));
		inv.put(8, +(get(0) * get(4) - get(1) * get(3)));

		return set(inv.transpose().mult(1 / determinant()));
	}

	private final FloatBuffer direct = BufferUtils.createFloatBuffer(LENGTH);

	public FloatBuffer toBuffer() {
		direct.clear();
		for(int a = 0; a < LENGTH; a++) {
			direct.put(matrix[a]);
		}
		direct.flip();
		return direct;
	}
}
