package javax.microedition.m3g;

public class Material extends Object3D {

	public static final int AMBIENT = 1024;
	public static final int DIFFUSE = 2048;
	public static final int SPECULAR = 4096;
	public static final int EMISSIVE = 8192;

	private int ambientColor = 0x00333333;
	private int diffuseColor = 0xFFCCCCCC;
	private int specularColor = 0x00000000;
	private int emissiveColor = 0x00000000;
	private float shininess = 0.0f;
	private boolean vertexColorTracking = false;

	public Material() {}

	public void setColor(int target, int argb) {
		switch (target) {
			case AMBIENT: ambientColor = argb & 0x00FFFFFF; break;
			case DIFFUSE: diffuseColor = argb; break;
			case SPECULAR: specularColor = argb & 0x00FFFFFF; break;
			case EMISSIVE: emissiveColor = argb & 0x00FFFFFF; break;
			default: throw new IllegalArgumentException();
		}
	}

	public int getColor(int target) {
		switch (target) {
			case AMBIENT: return ambientColor;
			case DIFFUSE: return diffuseColor;
			case SPECULAR: return specularColor;
			case EMISSIVE: return emissiveColor;
			default: throw new IllegalArgumentException();
		}
	}

	public void setShininess(float shininess) {
		if (shininess < 0.0f || shininess > 128.0f) throw new IllegalArgumentException();
		this.shininess = shininess;
	}

	public float getShininess() {
		return shininess;
	}

	public void setVertexColorTrackingEnable(boolean enable) {
		this.vertexColorTracking = enable;
	}

	public boolean isVertexColorTrackingEnabled() {
		return vertexColorTracking;
	}
}

