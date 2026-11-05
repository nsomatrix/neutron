package javax.microedition.m3g;

public class Mesh extends Node {

	private VertexBuffer vertexBuffer;
	private final IndexBuffer[] submeshes;
	private final Appearance[] appearances;

	public Mesh(VertexBuffer vertices, IndexBuffer submesh, Appearance appearance) {
		this(vertices, new IndexBuffer[] { submesh }, new Appearance[] { appearance });
	}

	public Mesh(VertexBuffer vertices, IndexBuffer[] submeshes, Appearance[] appearances) {
		if (vertices == null || submeshes == null || submeshes.length == 0) {
			throw new NullPointerException();
		}
		if (appearances != null && appearances.length < submeshes.length) {
			throw new IllegalArgumentException();
		}
		this.vertexBuffer = vertices;
		this.submeshes = new IndexBuffer[submeshes.length];
		System.arraycopy(submeshes, 0, this.submeshes, 0, submeshes.length);

		this.appearances = new Appearance[submeshes.length];
		if (appearances != null) {
			System.arraycopy(appearances, 0, this.appearances, 0, submeshes.length);
		}
	}

	public void setVertexBuffer(VertexBuffer vertices) {
		if (vertices == null) throw new NullPointerException();
		this.vertexBuffer = vertices;
	}

	public VertexBuffer getVertexBuffer() {
		return vertexBuffer;
	}

	public int getSubmeshCount() {
		return submeshes.length;
	}

	public IndexBuffer getIndexBuffer(int index) {
		if (index < 0 || index >= submeshes.length) throw new IndexOutOfBoundsException();
		return submeshes[index];
	}

	public void setAppearance(int index, Appearance appearance) {
		if (index < 0 || index >= appearances.length) throw new IndexOutOfBoundsException();
		this.appearances[index] = appearance;
	}

	public Appearance getAppearance(int index) {
		if (index < 0 || index >= appearances.length) throw new IndexOutOfBoundsException();
		return appearances[index];
	}
}

