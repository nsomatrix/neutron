package javax.microedition.m3g;

public class VertexBuffer extends Object3D {

	private VertexArray positions;
	private float positionScale = 1.0f;
	private final float[] positionBias = new float[3];

	private VertexArray normals;
	private VertexArray colors;
	private int defaultColor = 0xFFFFFFFF;

	private final VertexArray[] texCoords = new VertexArray[2];
	private final float[] texCoordScales = new float[] { 1.0f, 1.0f };
	private final float[][] texCoordBiases = new float[2][3];

	public VertexBuffer() {}

	public void setPositions(VertexArray positions, float scale, float[] bias) {
		this.positions = positions;
		this.positionScale = scale;
		if (bias != null && bias.length >= 3) {
			System.arraycopy(bias, 0, this.positionBias, 0, 3);
		} else {
			this.positionBias[0] = this.positionBias[1] = this.positionBias[2] = 0.0f;
		}
	}

	public VertexArray getPositions(float[] scaleBias) {
		if (scaleBias != null && scaleBias.length >= 4) {
			scaleBias[0] = positionScale;
			scaleBias[1] = positionBias[0];
			scaleBias[2] = positionBias[1];
			scaleBias[3] = positionBias[2];
		}
		return positions;
	}

	public void setNormals(VertexArray normals) {
		this.normals = normals;
	}

	public VertexArray getNormals() {
		return normals;
	}

	public void setColors(VertexArray colors) {
		this.colors = colors;
	}

	public VertexArray getColors() {
		return colors;
	}

	public void setDefaultColor(int argb) {
		this.defaultColor = argb;
	}

	public int getDefaultColor() {
		return defaultColor;
	}

	public void setTexCoords(int index, VertexArray texCoords, float scale, float[] bias) {
		if (index < 0 || index >= this.texCoords.length) throw new IndexOutOfBoundsException();
		this.texCoords[index] = texCoords;
		this.texCoordScales[index] = scale;
		if (bias != null && bias.length >= 3) {
			System.arraycopy(bias, 0, this.texCoordBiases[index], 0, 3);
		} else {
			this.texCoordBiases[index][0] = this.texCoordBiases[index][1] = this.texCoordBiases[index][2] = 0.0f;
		}
	}

	public VertexArray getTexCoords(int index, float[] scaleBias) {
		if (index < 0 || index >= this.texCoords.length) throw new IndexOutOfBoundsException();
		if (scaleBias != null && scaleBias.length >= 4) {
			scaleBias[0] = texCoordScales[index];
			scaleBias[1] = texCoordBiases[index][0];
			scaleBias[2] = texCoordBiases[index][1];
			scaleBias[3] = texCoordBiases[index][2];
		}
		return texCoords[index];
	}

	public int getVertexCount() {
		if (positions != null) {
			return positions.getVertexCount();
		}
		return 0;
	}
}

