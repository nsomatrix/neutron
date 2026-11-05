package javax.microedition.m3g;

public abstract class Transformable extends Object3D {

	private final float[] translation = new float[3];
	private final float[] scale = new float[] { 1.0f, 1.0f, 1.0f };
	private final float[] orientation = new float[] { 0.0f, 0.0f, 0.0f, 1.0f }; // angle, ax, ay, az
	private Transform compositeTransform = null;

	protected Transformable() {}

	public void setTranslation(float tx, float ty, float tz) {
		translation[0] = tx;
		translation[1] = ty;
		translation[2] = tz;
	}

	public void getTranslation(float[] translation) {
		if (translation == null || translation.length < 3) throw new IllegalArgumentException();
		System.arraycopy(this.translation, 0, translation, 0, 3);
	}

	public void translate(float tx, float ty, float tz) {
		translation[0] += tx;
		translation[1] += ty;
		translation[2] += tz;
	}

	public void setScale(float sx, float sy, float sz) {
		scale[0] = sx;
		scale[1] = sy;
		scale[2] = sz;
	}

	public void getScale(float[] scale) {
		if (scale == null || scale.length < 3) throw new IllegalArgumentException();
		System.arraycopy(this.scale, 0, scale, 0, 3);
	}

	public void scale(float sx, float sy, float sz) {
		scale[0] *= sx;
		scale[1] *= sy;
		scale[2] *= sz;
	}

	public void setOrientation(float angle, float ax, float ay, float az) {
		orientation[0] = angle;
		orientation[1] = ax;
		orientation[2] = ay;
		orientation[3] = az;
	}

	public void getOrientation(float[] orientation) {
		if (orientation == null || orientation.length < 4) throw new IllegalArgumentException();
		System.arraycopy(this.orientation, 0, orientation, 0, 4);
	}

	public void preRotate(float angle, float ax, float ay, float az) {
		Transform t = new Transform();
		t.postRotate(angle, ax, ay, az);
		Transform cur = new Transform();
		getCompositeTransform(cur);
		t.postMultiply(cur);
		setTransform(t);
	}

	public void postRotate(float angle, float ax, float ay, float az) {
		Transform cur = new Transform();
		getCompositeTransform(cur);
		cur.postRotate(angle, ax, ay, az);
		setTransform(cur);
	}

	public void setTransform(Transform transform) {
		if (transform == null) {
			this.compositeTransform = null;
		} else {
			if (this.compositeTransform == null) {
				this.compositeTransform = new Transform();
			}
			this.compositeTransform.set(transform);
		}
	}

	public void getTransform(Transform transform) {
		if (transform == null) throw new NullPointerException();
		if (compositeTransform != null) {
			transform.set(compositeTransform);
		} else {
			transform.setIdentity();
		}
	}

	public void getCompositeTransform(Transform transform) {
		if (transform == null) throw new NullPointerException();
		transform.setIdentity();
		transform.postTranslate(translation[0], translation[1], translation[2]);
		transform.postRotate(orientation[0], orientation[1], orientation[2], orientation[3]);
		transform.postScale(scale[0], scale[1], scale[2]);
		if (compositeTransform != null) {
			transform.postMultiply(compositeTransform);
		}
	}
}

