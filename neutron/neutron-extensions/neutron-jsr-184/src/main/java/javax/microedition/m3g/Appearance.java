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

public class Appearance extends Object3D
{

	private int layer;
	private CompositingMode compositingMode;
	private Fog fog;
	private PolygonMode polygonMode;
	private Material material;
	private Texture2D[] textures;

	public Appearance()
	{
		this.layer = 0;
		this.polygonMode = null;
		this.compositingMode = null;
		this.textures = new Texture2D[Graphics3D.NUM_TEXTURE_UNITS];
		this.material = null;
		this.fog = null;
	}

	protected Object3D duplicateImpl()
	{
		Appearance copy = (Appearance) super.duplicateImpl();

		copy.layer = this.layer;

		copy.setCompositingMode(this.compositingMode);
		copy.setFog(this.fog);
		copy.setMaterial(this.material);
		copy.setPolygonMode(this.polygonMode);

		copy.textures = new Texture2D[this.textures.length];
		for (int i = 0; i < this.textures.length; i++)
		{
			copy.setTexture(i, this.textures[i]);
		}

		return copy;
	}

	public void setLayer(int layer)
	{
		if (layer < -63 || layer > 63)
		{
			throw new IllegalArgumentException("Layer must be in range of [-63, 63]");
		}
		this.layer = layer;
	}

	public int getLayer() { return layer; }

	public void setFog(Fog fog)
	{
		this.removeReference(this.fog);
		this.fog = fog;
		this.addReference(this.fog);
	}

	public Fog getFog() { return fog; }

	public void setPolygonMode(PolygonMode polygonMode)
	{
		this.removeReference(this.polygonMode);
		this.polygonMode = polygonMode;
		this.addReference(this.polygonMode);
	}

	public PolygonMode getPolygonMode() { return polygonMode; }

	public void setMaterial(Material material)
	{
		this.removeReference(this.material);
		this.material = material;
		this.addReference(this.material);
	}

	public Material getMaterial() { return material; }

	public void setCompositingMode(CompositingMode comp)
	{
		this.removeReference(this.compositingMode);
		this.compositingMode = comp;
		this.addReference(this.compositingMode);
	}

	public CompositingMode getCompositingMode() { return this.compositingMode; }

	public void setTexture(int index, Texture2D texture)
	{
		if (index < 0 || index >= textures.length)
		{
			throw new IndexOutOfBoundsException("index must be in [0," + textures.length + "]");
		}

		this.removeReference(textures[index]);
		textures[index] = texture;
		this.addReference(textures[index]);
	}

	public Texture2D getTexture(int index)
	{
		if (index < 0 || index >= textures.length)
		{
			throw new IndexOutOfBoundsException("index must be in [0," + textures.length + "]");
		}

		return textures[index];
	}
}
