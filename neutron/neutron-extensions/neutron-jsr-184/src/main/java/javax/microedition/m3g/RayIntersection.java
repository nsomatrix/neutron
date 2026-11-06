/*
 * Neutron 
 * 
 *  It is licensed under the following two licenses as alternatives:
 *    1. GNU Lesser General Public License (the "LGPL") version 2.1 or any newer version
 *    2. Apache License (the "AL") Version 2.0
 *
 *  You may not use this file except in compliance with at least one of
 *  the above two licenses.
 *
 *  You may obtain a copy of the LGPL at
 *      http://www.gnu.org/licenses/old-licenses/lgpl-2.1.txt
 *
 *  You may obtain a copy of the AL at
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the LGPL or the AL for the specific language governing permissions and
 *  limitations.
 * 
 */
package javax.microedition.m3g;

public class RayIntersection
{
	private Node intersected = null;
	private float distance = 0.f;
	private int submeshIndex = 0;
	private float[] textureS = new float[Graphics3D.getTextureUnitCount()];
	private float[] textureT = new float[Graphics3D.getTextureUnitCount()];
	private float[] normal = new float[3];
	private float[] ray = new float[6];

	public RayIntersection()
	{
		normal[2] = 1.f;
		ray[5] = 1.f;
	}

	public Node getIntersected() { return this.intersected; }

	public float getDistance() { return this.distance; }

	public int getSubmeshIndex() { return this.submeshIndex; }

	public float getTextureS(int index)
	{
		if (index < 0 || index >= textureS.length)
			{ throw new IndexOutOfBoundsException("Invalid texture unit"); }

		return this.textureS[index];
	}

	public float getTextureT(int index)
	{
		if (index < 0 || index >= textureS.length)
			{ throw new IndexOutOfBoundsException("Invalid texture unit"); }

		return this.textureT[index];
	}

	public float getNormalX() { return this.normal[0]; }

	public float getNormalY() { return this.normal[1]; }

	public float getNormalZ() { return this.normal[2]; }

	public void getRay(float[] ray)
	{
		if (ray == null) { throw new NullPointerException("Ray cannot be null"); }

		if (ray.length < 6) { throw new IllegalArgumentException("Invalid Ray size"); }

		System.arraycopy(this.ray, 0, ray, 0, 6);
	}

	void set(Node node, float distance, int submeshIndex, float[] ray, float[] normal, float[] texS, float[] texT)
	{
		this.intersected = node;
		this.distance = distance;
		this.submeshIndex = submeshIndex;

		if (ray != null) { System.arraycopy(ray, 0, this.ray, 0, 6); }

		if (normal != null) { System.arraycopy(normal, 0, this.normal, 0, 3); }

		if (texS != null)
		{
			int len = Math.min(texS.length, this.textureS.length);
			System.arraycopy(texS, 0, this.textureS, 0, len);
		}

		if (texT != null)
		{
			int len = Math.min(texT.length, this.textureT.length);
			System.arraycopy(texT, 0, this.textureT, 0, len);
		}
	}
}
