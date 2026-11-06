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

public abstract class IndexBuffer extends Object3D
{
	protected int indexCount;
	protected int[] indices;

	protected Object3D duplicateImpl()
	{
		IndexBuffer copy = (IndexBuffer) super.duplicateImpl();
		copy.indices = this.indices == null ? null : (int[]) this.indices.clone();
		copy.indexCount = this.indexCount;
		return copy;
	}

	public int getIndexCount() { return this.indexCount; }

	public void getIndices(int[] indices)
	{
		/* As per JSR-184, throw NullPointerException if the received indices is null. */
		if(indices == null) {throw new NullPointerException("Tried to get buffer's vertex indices without providing the actual indices."); }

		/* Also per JSR-184, throw IllegalArgumentException if indices.length < getIndexCount. */
		if (indices.length < getIndexCount())
			{ throw new IllegalArgumentException("Tried to return vertex indices to an array that's smaller than the object's number of indices."); }

		if (getIndexCount() > 0 && this.indices != null) { System.arraycopy(this.indices, 0, indices, 0, getIndexCount()); }
	}

	// Faster alternative to the above that doesn't copy the index array, used by Graphics3D's render()
	public int[] getIndexArray() { return indices; }
}
