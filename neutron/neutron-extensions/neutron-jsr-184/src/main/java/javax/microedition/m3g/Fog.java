package javax.microedition.m3g;

public class Fog extends Object3D {

	public static final int LINEAR = 80;
	public static final int EXPONENTIAL = 81;

	private int mode = LINEAR;
	private int color = 0x00000000;
	private float density = 1.0f;
	private float near = 0.0f;
	private float far = 1.0f;

	public Fog() {}

	public void setMode(int mode) {
		if (mode != LINEAR && mode != EXPONENTIAL) throw new IllegalArgumentException();
		this.mode = mode;
	}

	public int getMode() { return mode; }

	public void setColor(int color) {
		this.color = color & 0x00FFFFFF;
	}

	public int getColor() { return color; }

	public void setDensity(float density) {
		if (density < 0.0f) throw new IllegalArgumentException();
		this.density = density;
	}

	public float getDensity() { return density; }

	public void setLinear(float near, float far) {
		this.near = near;
		this.far = far;
	}

	public float getNearDistance() { return near; }
	public float getFarDistance() { return far; }
}

