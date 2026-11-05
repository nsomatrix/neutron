package javax.microedition.m3g;

public class VertexArray extends Object3D {

	private final int numVertices;
	private final int numComponents;
	private final int componentSize;
	private final short[] shortValues;
	private final byte[] byteValues;

	public VertexArray(int numVertices, int numComponents, int componentSize) {
		if (numVertices <= 0 || numComponents < 2 || numComponents > 4 || (componentSize != 1 && componentSize != 2)) {
			throw new IllegalArgumentException();
		}
		this.numVertices = numVertices;
		this.numComponents = numComponents;
		this.componentSize = componentSize;

		if (componentSize == 2) {
			this.shortValues = new short[numVertices * numComponents];
			this.byteValues = null;
		} else {
			this.byteValues = new byte[numVertices * numComponents];
			this.shortValues = null;
		}
	}

	public void set(int firstVertex, int numVertices, short[] values) {
		if (componentSize != 2 || values == null || firstVertex < 0 || numVertices < 0 ||
		    (firstVertex + numVertices) > this.numVertices || values.length < numVertices * numComponents) {
			throw new IllegalArgumentException();
		}
		System.arraycopy(values, 0, this.shortValues, firstVertex * numComponents, numVertices * numComponents);
	}

	public void set(int firstVertex, int numVertices, byte[] values) {
		if (componentSize != 1 || values == null || firstVertex < 0 || numVertices < 0 ||
		    (firstVertex + numVertices) > this.numVertices || values.length < numVertices * numComponents) {
			throw new IllegalArgumentException();
		}
		System.arraycopy(values, 0, this.byteValues, firstVertex * numComponents, numVertices * numComponents);
	}

	public void get(int firstVertex, int numVertices, short[] values) {
		if (componentSize != 2 || values == null) throw new IllegalArgumentException();
		System.arraycopy(this.shortValues, firstVertex * numComponents, values, 0, numVertices * numComponents);
	}

	public void get(int firstVertex, int numVertices, byte[] values) {
		if (componentSize != 1 || values == null) throw new IllegalArgumentException();
		System.arraycopy(this.byteValues, firstVertex * numComponents, values, 0, numVertices * numComponents);
	}

	public int getVertexCount() { return numVertices; }
	public int getComponentCount() { return numComponents; }
	public int getComponentType() { return componentSize; }

	public float getComponentFloat(int vertexIndex, int componentIndex) {
		int idx = vertexIndex * numComponents + componentIndex;
		if (componentSize == 2) {
			return shortValues[idx];
		} else {
			return byteValues[idx];
		}
	}
}

