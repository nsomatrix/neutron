package javax.microedition.m3g;

public class KeyframeSequence extends Object3D {

	public static final int LINEAR = 176;
	public static final int SLERP = 177;
	public static final int SPLINE = 178;
	public static final int STEP = 179;

	public static final int CONSTANT = 192;
	public static final int LOOP = 193;

	private final int numKeyframes;
	private final int numComponents;
	private final int interpolation;
	private int repeatMode = CONSTANT;
	private int duration = 0;
	private final int[] times;
	private final float[] values;

	public KeyframeSequence(int numKeyframes, int numComponents, int interpolation) {
		if (numKeyframes <= 0 || numComponents <= 0) throw new IllegalArgumentException();
		if (interpolation < LINEAR || interpolation > STEP) throw new IllegalArgumentException();
		this.numKeyframes = numKeyframes;
		this.numComponents = numComponents;
		this.interpolation = interpolation;
		this.times = new int[numKeyframes];
		this.values = new float[numKeyframes * numComponents];
	}

	public void setKeyframe(int index, int time, float[] value) {
		if (index < 0 || index >= numKeyframes || value == null || value.length < numComponents) {
			throw new IndexOutOfBoundsException();
		}
		times[index] = time;
		System.arraycopy(value, 0, values, index * numComponents, numComponents);
		if (time > duration) {
			duration = time;
		}
	}

	public int getKeyframe(int index, float[] value) {
		if (index < 0 || index >= numKeyframes) throw new IndexOutOfBoundsException();
		if (value != null && value.length >= numComponents) {
			System.arraycopy(values, index * numComponents, value, 0, numComponents);
		}
		return times[index];
	}

	public void setValidRange(int first, int last) {}

	public void setDuration(int duration) {
		if (duration < 0) throw new IllegalArgumentException();
		this.duration = duration;
	}

	public int getDuration() { return duration; }

	public void setRepeatMode(int mode) {
		if (mode != CONSTANT && mode != LOOP) throw new IllegalArgumentException();
		this.repeatMode = mode;
	}

	public int getRepeatMode() { return repeatMode; }
	public int getInterpolationType() { return interpolation; }
	public int getKeyframeCount() { return numKeyframes; }
	public int getComponentCount() { return numComponents; }
}

