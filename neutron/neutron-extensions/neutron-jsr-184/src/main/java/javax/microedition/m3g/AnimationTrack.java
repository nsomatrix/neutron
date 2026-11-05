package javax.microedition.m3g;

public class AnimationTrack extends Object3D {

	public static final int ALPHA = 256;
	public static final int AMBIENT_COLOR = 257;
	public static final int COLOR = 258;
	public static final int CROP = 259;
	public static final int DENSITY = 260;
	public static final int DIFFUSE_COLOR = 261;
	public static final int EMISSIVE_COLOR = 262;
	public static final int FAR_DISTANCE = 263;
	public static final int INTENSITY = 264;
	public static final int MORPH_WEIGHTS = 265;
	public static final int NEAR_DISTANCE = 266;
	public static final int ORIENTATION = 267;
	public static final int PICKABILITY = 268;
	public static final int SCALE = 269;
	public static final int SHININESS = 270;
	public static final int SPECULAR_COLOR = 271;
	public static final int SPOT_ANGLE = 272;
	public static final int SPOT_EXPONENT = 273;
	public static final int TRANSLATION = 274;
	public static final int VISIBILITY = 275;

	private final KeyframeSequence sequence;
	private final int property;
	private AnimationController controller;

	public AnimationTrack(KeyframeSequence sequence, int property) {
		if (sequence == null) throw new NullPointerException();
		this.sequence = sequence;
		this.property = property;
	}

	public KeyframeSequence getKeyframeSequence() {
		return sequence;
	}

	public int getTargetProperty() {
		return property;
	}

	public void setController(AnimationController controller) {
		this.controller = controller;
	}

	public AnimationController getController() {
		return controller;
	}
}

