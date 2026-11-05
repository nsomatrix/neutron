/*
 *  Neutron JSR-226 Scalable 2D Vector Graphics
 *
 *  Licensed under LGPL 2.1 or Apache 2.0.
 */

package javax.microedition.m2g;

public class ScalableGraphics {

	public static final int RENDERING_QUALITY_LOW = 1;
	public static final int RENDERING_QUALITY_FAST = 2;
	public static final int RENDERING_QUALITY_HIGH = 3;

	private Object target;
	private float transparency = 1.0f;
	private int quality = RENDERING_QUALITY_HIGH;

	public void bindTarget(Object target) {
		this.target = target;
	}

	public void releaseTarget() {
		this.target = null;
	}

	public void render(int x, int y, ScalableImage image) {
		// Mock vector rasterization
	}

	public void setTransparency(float alpha) {
		this.transparency = Math.max(0.0f, Math.min(1.0f, alpha));
	}

	public float getTransparency() {
		return transparency;
	}

	public void setRenderingQuality(int mode) {
		this.quality = mode;
	}

	public int getRenderingQuality() {
		return quality;
	}
}

