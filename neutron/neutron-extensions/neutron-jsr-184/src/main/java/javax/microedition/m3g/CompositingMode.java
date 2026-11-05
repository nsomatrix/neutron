package javax.microedition.m3g;

public class CompositingMode extends Object3D {

	public static final int ALPHA = 64;
	public static final int ALPHA_ADD = 65;
	public static final int MODULATE = 66;
	public static final int MODULATE_X2 = 67;
	public static final int REPLACE = 68;

	private int blending = REPLACE;
	private float alphaThreshold = 0.0f;
	private float factor = 1.0f;
	private boolean depthTest = true;
	private boolean depthWrite = true;
	private boolean colorWrite = true;
	private boolean alphaWrite = true;

	public CompositingMode() {}

	public void setBlending(int mode) {
		if (mode < ALPHA || mode > REPLACE) throw new IllegalArgumentException();
		this.blending = mode;
	}

	public int getBlending() { return blending; }

	public void setAlphaThreshold(float threshold) {
		if (threshold < 0.0f || threshold > 1.0f) throw new IllegalArgumentException();
		this.alphaThreshold = threshold;
	}

	public float getAlphaThreshold() { return alphaThreshold; }

	public void setFactor(float factor) {
		if (factor < 0.0f || factor > 1.0f) throw new IllegalArgumentException();
		this.factor = factor;
	}

	public float getFactor() { return factor; }

	public void setDepthTestEnable(boolean enable) { this.depthTest = enable; }
	public boolean isDepthTestEnabled() { return depthTest; }

	public void setDepthWriteEnable(boolean enable) { this.depthWrite = enable; }
	public boolean isDepthWriteEnabled() { return depthWrite; }

	public void setColorWriteEnable(boolean enable) { this.colorWrite = enable; }
	public boolean isColorWriteEnabled() { return colorWrite; }

	public void setAlphaWriteEnable(boolean enable) { this.alphaWrite = enable; }
	public boolean isAlphaWriteEnabled() { return alphaWrite; }
}

