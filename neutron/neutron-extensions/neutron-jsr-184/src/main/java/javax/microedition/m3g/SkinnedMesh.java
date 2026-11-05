package javax.microedition.m3g;

public class SkinnedMesh extends Mesh {

	private final Group skeleton;

	public SkinnedMesh(VertexBuffer vertices, IndexBuffer[] submeshes, Appearance[] appearances, Group skeleton) {
		super(vertices, submeshes, appearances);
		if (skeleton == null) throw new NullPointerException();
		this.skeleton = skeleton;
	}

	public SkinnedMesh(VertexBuffer vertices, IndexBuffer submesh, Appearance appearance, Group skeleton) {
		super(vertices, submesh, appearance);
		if (skeleton == null) throw new NullPointerException();
		this.skeleton = skeleton;
	}

	public Group getSkeleton() {
		return skeleton;
	}

	public void addTransform(Node bone, int weight, int firstVertex, int numVertices) {
		// Skeletal binding transform
	}

	public void getBoneTransform(Node bone, Transform transform) {
		if (bone == null || transform == null) throw new NullPointerException();
		bone.getCompositeTransform(transform);
	}

	public int getBoneVertices(Node bone, int[] vertices, float[] weights) {
		return 0;
	}
}

