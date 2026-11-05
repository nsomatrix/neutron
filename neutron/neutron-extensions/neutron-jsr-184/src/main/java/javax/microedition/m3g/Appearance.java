package javax.microedition.m3g;

public class Appearance extends Object3D {

	private Material material;
	private PolygonMode polygonMode;
	private CompositingMode compositingMode;
	private final Texture2D[] textures = new Texture2D[2];
	private Fog fog;
	private int layer = 0;

	public Appearance() {}

	public void setMaterial(Material material) {
		this.material = material;
	}

	public Material getMaterial() { return material; }

	public void setPolygonMode(PolygonMode polygonMode) {
		this.polygonMode = polygonMode;
	}

	public PolygonMode getPolygonMode() { return polygonMode; }

	public void setCompositingMode(CompositingMode compositingMode) {
		this.compositingMode = compositingMode;
	}

	public CompositingMode getCompositingMode() { return compositingMode; }

	public void setTexture(int index, Texture2D texture) {
		if (index < 0 || index >= textures.length) throw new IndexOutOfBoundsException();
		this.textures[index] = texture;
	}

	public Texture2D getTexture(int index) {
		if (index < 0 || index >= textures.length) throw new IndexOutOfBoundsException();
		return textures[index];
	}

	public void setFog(Fog fog) {
		this.fog = fog;
	}

	public Fog getFog() { return fog; }

	public void setLayer(int layer) {
		if (layer < -63 || layer > 63) throw new IndexOutOfBoundsException();
		this.layer = layer;
	}

	public int getLayer() { return layer; }
}

