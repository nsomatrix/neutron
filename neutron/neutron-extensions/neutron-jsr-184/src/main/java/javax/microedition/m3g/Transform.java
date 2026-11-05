package javax.microedition.m3g;

public class Transform {

	final float[] matrix = new float[16];

	public Transform() {
		setIdentity();
	}

	public Transform(Transform transform) {
		set(transform);
	}

	public void setIdentity() {
		for (int i = 0; i < 16; i++) {
			matrix[i] = (i % 5 == 0) ? 1.0f : 0.0f;
		}
	}

	public void set(Transform transform) {
		if (transform == null) {
			throw new NullPointerException();
		}
		System.arraycopy(transform.matrix, 0, this.matrix, 0, 16);
	}

	public void set(float[] matrix) {
		if (matrix == null || matrix.length < 16) {
			throw new IllegalArgumentException();
		}
		System.arraycopy(matrix, 0, this.matrix, 0, 16);
	}

	public void get(float[] matrix) {
		if (matrix == null || matrix.length < 16) {
			throw new IllegalArgumentException();
		}
		System.arraycopy(this.matrix, 0, matrix, 0, 16);
	}

	public void postMultiply(Transform transform) {
		if (transform == null) {
			throw new NullPointerException();
		}
		float[] res = new float[16];
		float[] a = this.matrix;
		float[] b = transform.matrix;

		for (int r = 0; r < 4; r++) {
			for (int c = 0; c < 4; c++) {
				res[r * 4 + c] = a[r * 4 + 0] * b[0 * 4 + c] +
				                a[r * 4 + 1] * b[1 * 4 + c] +
				                a[r * 4 + 2] * b[2 * 4 + c] +
				                a[r * 4 + 3] * b[3 * 4 + c];
			}
		}
		System.arraycopy(res, 0, this.matrix, 0, 16);
	}

	public void postTranslate(float tx, float ty, float tz) {
		Transform t = new Transform();
		t.matrix[3] = tx;
		t.matrix[7] = ty;
		t.matrix[11] = tz;
		postMultiply(t);
	}

	public void postScale(float sx, float sy, float sz) {
		Transform t = new Transform();
		t.matrix[0] = sx;
		t.matrix[5] = sy;
		t.matrix[10] = sz;
		postMultiply(t);
	}

	public void postRotate(float angle, float ax, float ay, float az) {
		float len = (float) Math.sqrt(ax * ax + ay * ay + az * az);
		if (len == 0.0f) {
			return;
		}
		ax /= len;
		ay /= len;
		az /= len;

		float rad = (float) Math.toRadians(angle);
		float c = (float) Math.cos(rad);
		float s = (float) Math.sin(rad);
		float t = 1.0f - c;

		Transform r = new Transform();
		r.matrix[0] = t * ax * ax + c;
		r.matrix[1] = t * ax * ay - s * az;
		r.matrix[2] = t * ax * az + s * ay;

		r.matrix[4] = t * ax * ay + s * az;
		r.matrix[5] = t * ay * ay + c;
		r.matrix[6] = t * ay * az - s * ax;

		r.matrix[8] = t * ax * az - s * ay;
		r.matrix[9] = t * ay * az + s * ax;
		r.matrix[10] = t * az * az + c;

		postMultiply(r);
	}

	public void postRotateQuat(float qx, float qy, float qz, float qw) {
		float n = (float) Math.sqrt(qx * qx + qy * qy + qz * qz + qw * qw);
		if (n == 0.0f) return;
		qx /= n; qy /= n; qz /= n; qw /= n;

		Transform r = new Transform();
		r.matrix[0] = 1.0f - 2.0f * (qy * qy + qz * qz);
		r.matrix[1] = 2.0f * (qx * qy - qz * qw);
		r.matrix[2] = 2.0f * (qx * qz + qy * qw);

		r.matrix[4] = 2.0f * (qx * qy + qz * qw);
		r.matrix[5] = 1.0f - 2.0f * (qx * qx + qz * qz);
		r.matrix[6] = 2.0f * (qy * qz - qx * qw);

		r.matrix[8] = 2.0f * (qx * qz - qy * qw);
		r.matrix[9] = 2.0f * (qy * qz + qx * qw);
		r.matrix[10] = 1.0f - 2.0f * (qx * qx + qy * qy);

		postMultiply(r);
	}

	public void invert() {
		float[] m = matrix;
		float[] inv = new float[16];

		inv[0] = m[5]  * m[10] * m[15] - m[5]  * m[11] * m[14] - m[9]  * m[6]  * m[15] +
		         m[9]  * m[7]  * m[14] + m[13] * m[6]  * m[11] - m[13] * m[7]  * m[10];

		inv[4] = -m[4]  * m[10] * m[15] + m[4]  * m[11] * m[14] + m[8]  * m[6]  * m[15] -
		          m[8]  * m[7]  * m[14] - m[12] * m[6]  * m[11] + m[12] * m[7]  * m[10];

		inv[8] = m[4]  * m[9] * m[15] - m[4]  * m[11] * m[13] - m[8]  * m[5] * m[15] +
		         m[8]  * m[7] * m[13] + m[12] * m[5]  * m[11] - m[12] * m[7] * m[9];

		inv[12] = -m[4]  * m[9] * m[14] + m[4]  * m[10] * m[13] + m[8]  * m[5] * m[14] -
		           m[8]  * m[6] * m[13] - m[12] * m[5]  * m[10] + m[12] * m[6] * m[9];

		inv[1] = -m[1]  * m[10] * m[15] + m[1]  * m[11] * m[14] + m[9]  * m[2] * m[15] -
		          m[9]  * m[3]  * m[14] - m[13] * m[2]  * m[11] + m[13] * m[3] * m[10];

		inv[5] = m[0]  * m[10] * m[15] - m[0]  * m[11] * m[14] - m[8]  * m[2] * m[15] +
		         m[8]  * m[3]  * m[14] + m[12] * m[2]  * m[11] - m[12] * m[3] * m[10];

		inv[9] = -m[0]  * m[9] * m[15] + m[0]  * m[11] * m[13] + m[8]  * m[1] * m[15] -
		          m[8]  * m[3] * m[13] - m[12] * m[1]  * m[11] + m[12] * m[3] * m[9];

		inv[13] = m[0]  * m[9] * m[14] - m[0]  * m[10] * m[13] - m[8]  * m[1] * m[14] +
		          m[8]  * m[2] * m[13] + m[12] * m[1]  * m[10] - m[12] * m[2] * m[9];

		inv[2] = m[1]  * m[6] * m[15] - m[1]  * m[7] * m[14] - m[5]  * m[2] * m[15] +
		         m[5]  * m[3] * m[14] + m[13] * m[2] * m[7]  - m[13] * m[3] * m[6];

		inv[6] = -m[0]  * m[6] * m[15] + m[0]  * m[7] * m[14] + m[4]  * m[2] * m[15] -
		          m[4]  * m[3] * m[14] - m[12] * m[2] * m[7]  + m[12] * m[3] * m[6];

		inv[10] = m[0]  * m[5] * m[15] - m[0]  * m[7] * m[13] - m[4]  * m[1] * m[15] +
		          m[4]  * m[3] * m[13] + m[12] * m[1] * m[7]  - m[12] * m[3] * m[5];

		inv[14] = -m[0]  * m[5] * m[14] + m[0]  * m[6] * m[13] + m[4]  * m[1] * m[14] -
		           m[4]  * m[2] * m[13] - m[12] * m[1] * m[6]  + m[12] * m[2] * m[5];

		inv[3] = -m[1] * m[6] * m[11] + m[1] * m[7] * m[10] + m[5] * m[2] * m[11] -
		          m[5] * m[3] * m[10] - m[9] * m[2] * m[7]  + m[9] * m[3] * m[6];

		inv[7] = m[0] * m[6] * m[11] - m[0] * m[7] * m[10] - m[4] * m[2] * m[11] +
		         m[4] * m[3] * m[10] + m[8] * m[2] * m[7]  - m[8] * m[3] * m[6];

		inv[11] = -m[0] * m[5] * m[11] + m[0] * m[7] * m[9] + m[4] * m[1] * m[11] -
		           m[4] * m[3] * m[9]  - m[8] * m[1] * m[7] + m[8] * m[3] * m[5];

		inv[15] = m[0] * m[5] * m[10] - m[0] * m[6] * m[9] - m[4] * m[1] * m[10] +
		          m[4] * m[2] * m[9]  + m[8] * m[1] * m[6] - m[8] * m[2] * m[5];

		float det = m[0] * inv[0] + m[1] * inv[4] + m[2] * inv[8] + m[3] * inv[12];
		if (det == 0.0f) {
			throw new ArithmeticException("Matrix is singular");
		}

		det = 1.0f / det;
		for (int i = 0; i < 16; i++) {
			matrix[i] = inv[i] * det;
		}
	}

	public void transpose() {
		float[] t = new float[16];
		for (int r = 0; r < 4; r++) {
			for (int c = 0; c < 4; c++) {
				t[c * 4 + r] = matrix[r * 4 + c];
			}
		}
		System.arraycopy(t, 0, matrix, 0, 16);
	}

	public void transform(float[] vectors) {
		if (vectors == null) {
			throw new NullPointerException();
		}
		int n = vectors.length / 4;
		for (int i = 0; i < n; i++) {
			int idx = i * 4;
			float x = vectors[idx];
			float y = vectors[idx + 1];
			float z = vectors[idx + 2];
			float w = vectors[idx + 3];

			vectors[idx]     = matrix[0] * x + matrix[1] * y + matrix[2] * z + matrix[3] * w;
			vectors[idx + 1] = matrix[4] * x + matrix[5] * y + matrix[6] * z + matrix[7] * w;
			vectors[idx + 2] = matrix[8] * x + matrix[9] * y + matrix[10] * z + matrix[11] * w;
			vectors[idx + 3] = matrix[12] * x + matrix[13] * y + matrix[14] * z + matrix[15] * w;
		}
	}
}

