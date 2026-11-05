package javax.microedition.m3g;

public class RayIntersection {

	private float distance;
	private int submeshIndex;
	private final float[] ray = new float[6];
	private final float[] normal = new float[3];
	private final float[] texCoords = new float[2];
	private Node intersected;

	public RayIntersection() {}

	public float getDistance() { return distance; }
	public int getSubmeshIndex() { return submeshIndex; }
	public Node getIntersected() { return intersected; }

	public float getRay(float[] ray) {
		if (ray != null && ray.length >= 6) {
			System.arraycopy(this.ray, 0, ray, 0, 6);
		}
		return distance;
	}

	public void getNormal(float[] normal) {
		if (normal != null && normal.length >= 3) {
			System.arraycopy(this.normal, 0, normal, 0, 3);
		}
	}

	public float getTextureS(int index) { return texCoords[0]; }
	public float getTextureT(int index) { return texCoords[1]; }
}

