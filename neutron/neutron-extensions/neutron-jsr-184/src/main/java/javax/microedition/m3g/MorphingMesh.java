package javax.microedition.m3g;

public class MorphingMesh extends Mesh {

	private final VertexBuffer[] targets;
	private final float[] weights;

	public MorphingMesh(VertexBuffer base, VertexBuffer[] targets, IndexBuffer[] submeshes, Appearance[] appearances) {
		super(base, submeshes, appearances);
		if (targets == null || targets.length == 0) throw new IllegalArgumentException();
		this.targets = (VertexBuffer[]) targets.clone();
		this.weights = new float[targets.length];
	}

	public MorphingMesh(VertexBuffer base, VertexBuffer[] targets, IndexBuffer submesh, Appearance appearance) {
		this(base, targets, new IndexBuffer[] { submesh }, new Appearance[] { appearance });
	}

	public int getMorphTargetCount() {
		return targets.length;
	}

	public VertexBuffer getMorphTarget(int index) {
		if (index < 0 || index >= targets.length) throw new IndexOutOfBoundsException();
		return targets[index];
	}

	public void setWeights(float[] weights) {
		if (weights == null || weights.length < targets.length) throw new IllegalArgumentException();
		System.arraycopy(weights, 0, this.weights, 0, targets.length);
	}

	public void getWeights(float[] weights) {
		if (weights == null || weights.length < targets.length) throw new IllegalArgumentException();
		System.arraycopy(this.weights, 0, weights, 0, targets.length);
	}
}

