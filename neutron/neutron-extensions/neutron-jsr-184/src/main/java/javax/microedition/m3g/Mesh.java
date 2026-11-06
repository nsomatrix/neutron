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

public class Mesh extends Node
{
	private VertexBuffer vertices;
	private IndexBuffer[] submeshes;
	private Appearance[] appearances;

	protected Mesh() { }

	public Mesh(VertexBuffer vertices, IndexBuffer submesh, Appearance appearance)
	{
		if ((vertices == null) || (submesh == null)) { throw new NullPointerException("Cannot create mesh due to a null element"); }

		this.vertices = vertices;
		this.submeshes = new IndexBuffer[]{submesh};
		this.appearances = new Appearance[]{appearance};

		// Appearance can be null here, so only add the reference if it isn't
		if (this.appearances[0] != null) { addReference(this.appearances[0]); }
		addReference(this.vertices);
		addReference(this.submeshes[0]);
	}

	public Mesh(VertexBuffer vertices, IndexBuffer[] submeshes, Appearance[] appearances)
	{
		if ((vertices == null) || (submeshes == null))
		{
			throw new NullPointerException("Cannot create mesh due to a null element");
		}

		if (submeshes.length == 0)
		{
			throw new IllegalArgumentException("Submesh array cannot be empty");
		}

		if (appearances != null && appearances.length < submeshes.length)
		{
			throw new IllegalArgumentException("Appearances array has invalid length.");
		}

		this.vertices = vertices;
		this.submeshes = new IndexBuffer[submeshes.length];
		this.appearances = new Appearance[submeshes.length];

		addReference(this.vertices);

		for (int i = 0; i < submeshes.length; i++)
		{
			if (submeshes[i] == null) { throw new NullPointerException("Submesh " + i + " is null."); }

			this.submeshes[i] = submeshes[i];
			addReference(this.submeshes[i]);

			if (appearances != null && appearances[i] != null)
			{
				this.appearances[i] = appearances[i];
				addReference(this.appearances[i]);
			}
		}
	}

	protected Object3D duplicateImpl()
	{
		Mesh copy = (Mesh) super.duplicateImpl();

		copy.vertices = this.vertices;
		if (copy.vertices != null) { copy.addReference(copy.vertices); }

		copy.submeshes = new IndexBuffer[this.submeshes.length];
		for (int i = 0; i < this.submeshes.length; i++)
		{
			copy.submeshes[i] = this.submeshes[i];
			if (copy.submeshes[i] != null) { copy.addReference(copy.submeshes[i]); }
		}

		copy.appearances = new Appearance[this.appearances.length];
		for (int i = 0; i < this.appearances.length; i++)
		{
			copy.appearances[i] = this.appearances[i];
			if (copy.appearances[i] != null) { copy.addReference(copy.appearances[i]); }
		}

		return copy;
	}

	public Appearance getAppearance(int index)
	{
		if (index < 0 || index >= submeshes.length) { throw new IndexOutOfBoundsException("Cannot get invalid appearance index"); }

		return this.appearances[index];
	}

	public IndexBuffer getIndexBuffer(int index)
	{
		if (index < 0 || index >= submeshes.length) { throw new IndexOutOfBoundsException("Cannot get invalid index buffer index"); }

		return this.submeshes[index];
	}

	public int getSubmeshCount() { return this.submeshes.length; }

	public VertexBuffer getVertexBuffer() { return this.vertices; }

	/*
	 * Internal counterpart of getVertexBuffer(), used by the renderer and the
	 * picker, which need the deformed geometry.
	 *
	 * Per JSR-184, getVertexBuffer() must always return the original VertexBuffer
	 * supplied at construction: "The VertexBuffer is never written to by the
	 * implementation. Specifically, the results of morphing (MorphingMesh) and
	 * skinning (SkinnedMesh) are not written to the VertexBuffer, nor are they
	 * exposed to the application by any other means." Those subclasses therefore
	 * keep their posed buffer private and hand it over through this method.
	 */
	VertexBuffer getPosedVertexBuffer() { return getVertexBuffer(); }

	public void setAppearance(int index, Appearance appearance)
	{
		if (index < 0 || index >= submeshes.length) { throw new IndexOutOfBoundsException("Cannot set to invalid appearance index"); }
		removeReference(this.appearances[index]);
		this.appearances[index] = appearance;
		addReference(this.appearances[index]);
	}
}
