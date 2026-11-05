package javax.microedition.m3g;

public class PolygonMode extends Object3D {

	public static final int CULL_BACK = 160;
	public static final int CULL_FRONT = 161;
	public static final int CULL_NONE = 162;
	public static final int SHADE_FLAT = 164;
	public static final int SHADE_SMOOTH = 165;
	public static final int WINDING_CCW = 168;
	public static final int WINDING_CW = 169;

	private int culling = CULL_BACK;
	private int shading = SHADE_SMOOTH;
	private int winding = WINDING_CCW;
	private boolean twoSidedLighting = false;
	private boolean localCameraLighting = false;
	private boolean perspectiveCorrection = true;

	public PolygonMode() {}

	public void setCulling(int mode) {
		if (mode < CULL_BACK || mode > CULL_NONE) throw new IllegalArgumentException();
		this.culling = mode;
	}

	public int getCulling() { return culling; }

	public void setShading(int mode) {
		if (mode != SHADE_FLAT && mode != SHADE_SMOOTH) throw new IllegalArgumentException();
		this.shading = mode;
	}

	public int getShading() { return shading; }

	public void setWinding(int mode) {
		if (mode != WINDING_CCW && mode != WINDING_CW) throw new IllegalArgumentException();
		this.winding = mode;
	}

	public int getWinding() { return winding; }

	public void setTwoSidedLightingEnable(boolean enable) { this.twoSidedLighting = enable; }
	public boolean isTwoSidedLightingEnabled() { return twoSidedLighting; }

	public void setLocalCameraLightingEnable(boolean enable) { this.localCameraLighting = enable; }
	public boolean isLocalCameraLightingEnabled() { return localCameraLighting; }

	public void setPerspectiveCorrectionEnable(boolean enable) { this.perspectiveCorrection = enable; }
	public boolean isPerspectiveCorrectionEnabled() { return perspectiveCorrection; }
}

