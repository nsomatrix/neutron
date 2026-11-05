package javax.microedition.m3g;

public class TriangleStripArray extends IndexBuffer {

	private final int[] indices;
	private final int[] stripLengths;

	public TriangleStripArray(int firstIndex, int[] stripLengths) {
		if (stripLengths == null || stripLengths.length == 0) throw new IllegalArgumentException();
		this.stripLengths = (int[]) stripLengths.clone();
		int total = 0;
		for (int i = 0; i < stripLengths.length; i++) {
			if (stripLengths[i] < 3) throw new IllegalArgumentException();
			total += stripLengths[i];
		}
		this.indices = new int[total];
		for (int i = 0; i < total; i++) {
			this.indices[i] = firstIndex + i;
		}
	}

	public TriangleStripArray(int[] indices, int[] stripLengths) {
		if (indices == null || stripLengths == null || stripLengths.length == 0) throw new IllegalArgumentException();
		this.stripLengths = (int[]) stripLengths.clone();
		int total = 0;
		for (int i = 0; i < stripLengths.length; i++) {
			if (stripLengths[i] < 3) throw new IllegalArgumentException();
			total += stripLengths[i];
		}
		if (indices.length < total) throw new IllegalArgumentException();
		this.indices = new int[total];
		System.arraycopy(indices, 0, this.indices, 0, total);
	}

	public int getIndexCount() {
		return indices.length;
	}

	public void getIndices(int[] indices) {
		if (indices == null || indices.length < this.indices.length) throw new IllegalArgumentException();
		System.arraycopy(this.indices, 0, indices, 0, this.indices.length);
	}

	public int[][] getTriangles() {
		// Calculate total triangles across all strips
		int totalTris = 0;
		for (int i = 0; i < stripLengths.length; i++) {
			totalTris += (stripLengths[i] - 2);
		}

		int[][] tris = new int[totalTris][3];
		int triIdx = 0;
		int baseIdx = 0;

		for (int s = 0; s < stripLengths.length; s++) {
			int len = stripLengths[s];
			for (int i = 0; i < len - 2; i++) {
				if ((i & 1) == 0) {
					tris[triIdx][0] = indices[baseIdx + i];
					tris[triIdx][1] = indices[baseIdx + i + 1];
					tris[triIdx][2] = indices[baseIdx + i + 2];
				} else {
					tris[triIdx][0] = indices[baseIdx + i + 1];
					tris[triIdx][1] = indices[baseIdx + i];
					tris[triIdx][2] = indices[baseIdx + i + 2];
				}
				triIdx++;
			}
			baseIdx += len;
		}
		return tris;
	}
}

