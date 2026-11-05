package javax.microedition.m3g;

public class Light extends Node {

	public static final int AMBIENT = 128;
	public static final int DIRECTIONAL = 129;
	public static final int OMNI = 130;
	public static final int SPOT = 131;

	private int mode = DIRECTIONAL;
	private int color = 0x00FFFFFF;
	private float intensity = 1.0f;
	private float spotAngle = 45.0f;
	private float spotExponent = 0.0f;
	private float constantAttenuation = 1.0f;
	private float linearAttenuation = 0.0f;
	private float quadraticAttenuation = 0.0f;

	public Light() {}

	public void setMode(int mode) {
		if (mode < AMBIENT || mode > SPOT) throw new IllegalArgumentException();
		this.mode = mode;
	}

	public int getMode() {
		return mode;
	}

	public void setColor(int color) {
		this.color = color & 0x00FFFFFF;
	}

	public int getColor() {
		return color;
	}

	public void setIntensity(float intensity) {
		this.intensity = intensity;
	}

	public float getIntensity() {
		return intensity;
	}

	public void setSpotAngle(float angle) {
		if (angle < 0.0f || angle > 90.0f) throw new IllegalArgumentException();
		this.spotAngle = angle;
	}

	public float getSpotAngle() {
		return spotAngle;
	}

	public void setSpotExponent(float exponent) {
		if (exponent < 0.0f || exponent > 128.0f) throw new IllegalArgumentException();
		this.spotExponent = exponent;
	}

	public float getSpotExponent() {
		return spotExponent;
	}

	public void setAttenuation(float constant, float linear, float quadratic) {
		if (constant < 0.0f || linear < 0.0f || quadratic < 0.0f) throw new IllegalArgumentException();
		if (constant == 0.0f && linear == 0.0f && quadratic == 0.0f) throw new IllegalArgumentException();
		this.constantAttenuation = constant;
		this.linearAttenuation = linear;
		this.quadraticAttenuation = quadratic;
	}

	public float getConstantAttenuation() { return constantAttenuation; }
	public float getLinearAttenuation() { return linearAttenuation; }
	public float getQuadraticAttenuation() { return quadraticAttenuation; }
}

