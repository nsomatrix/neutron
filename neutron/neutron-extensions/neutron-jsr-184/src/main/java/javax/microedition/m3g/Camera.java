package javax.microedition.m3g;

public class Camera extends Node {

	public static final int GENERIC = 48;
	public static final int PARALLEL = 49;
	public static final int PERSPECTIVE = 50;

	private int type = PERSPECTIVE;
	private final Transform projection = new Transform();
	private final float[] params = new float[4];

	public Camera() {
		setPerspective(45.0f, 1.0f, 1.0f, 1000.0f);
	}

	public void setParallel(float height, float aspectRatio, float near, float far) {
		if (height <= 0.0f || aspectRatio <= 0.0f || near >= far) {
			throw new IllegalArgumentException();
		}
		type = PARALLEL;
		params[0] = height;
		params[1] = aspectRatio;
		params[2] = near;
		params[3] = far;

		float w = height * aspectRatio;
		float h = height;
		float d = far - near;

		projection.setIdentity();
		projection.matrix[0] = 2.0f / w;
		projection.matrix[5] = 2.0f / h;
		projection.matrix[10] = -2.0f / d;
		projection.matrix[11] = -(far + near) / d;
	}

	public void setPerspective(float fovy, float aspectRatio, float near, float far) {
		if (fovy <= 0.0f || fovy >= 180.0f || aspectRatio <= 0.0f || near <= 0.0f || near >= far) {
			throw new IllegalArgumentException();
		}
		type = PERSPECTIVE;
		params[0] = fovy;
		params[1] = aspectRatio;
		params[2] = near;
		params[3] = far;

		float rad = (float) Math.toRadians(fovy * 0.5f);
		float f = (float) (Math.cos(rad) / Math.sin(rad));
		float d = far - near;

		projection.setIdentity();
		projection.matrix[0] = f / aspectRatio;
		projection.matrix[5] = f;
		projection.matrix[10] = -(far + near) / d;
		projection.matrix[11] = -2.0f * far * near / d;
		projection.matrix[14] = -1.0f;
		projection.matrix[15] = 0.0f;
	}

	public void setGeneric(Transform transform) {
		if (transform == null) throw new NullPointerException();
		type = GENERIC;
		projection.set(transform);
	}

	public int getProjection(Transform transform) {
		if (transform != null) {
			transform.set(projection);
		}
		return type;
	}

	public int getProjection(float[] params) {
		if (params != null && params.length >= 4 && type != GENERIC) {
			System.arraycopy(this.params, 0, params, 0, 4);
		}
		return type;
	}
}

