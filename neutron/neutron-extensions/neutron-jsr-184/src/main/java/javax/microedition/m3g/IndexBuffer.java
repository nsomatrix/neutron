package javax.microedition.m3g;

public abstract class IndexBuffer extends Object3D {

	protected IndexBuffer() {}

	public abstract int getIndexCount();

	public abstract void getIndices(int[] indices);

	public abstract int[][] getTriangles();
}

