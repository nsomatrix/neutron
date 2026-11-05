package javax.microedition.m3g;

public class AnimationController extends Object3D {

	private float speed = 1.0f;
	private float weight = 1.0f;
	private int activeIntervalStart = 0;
	private int activeIntervalEnd = 0;
	private float position = 0.0f;
	private int refWorldTime = 0;

	public AnimationController() {}

	public void setActiveInterval(int start, int end) {
		this.activeIntervalStart = start;
		this.activeIntervalEnd = end;
	}

	public int getActiveIntervalStart() { return activeIntervalStart; }
	public int getActiveIntervalEnd() { return activeIntervalEnd; }

	public void setSpeed(float speed, int worldTime) {
		this.speed = speed;
		this.refWorldTime = worldTime;
	}

	public float getSpeed() { return speed; }

	public void setPosition(float sequenceTime, int worldTime) {
		this.position = sequenceTime;
		this.refWorldTime = worldTime;
	}

	public float getPosition(int worldTime) {
		return position + (worldTime - refWorldTime) * speed;
	}

	public void setWeight(float weight) {
		if (weight < 0.0f) throw new IllegalArgumentException();
		this.weight = weight;
	}

	public float getWeight() { return weight; }
	public int getRefWorldTime() { return refWorldTime; }
}

