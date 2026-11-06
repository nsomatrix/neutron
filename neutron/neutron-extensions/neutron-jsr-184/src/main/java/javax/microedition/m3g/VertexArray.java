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

public class VertexArray extends Object3D
{

	byte[] vertArrayByteSize;
	short[] vertArrayShortSize;
	private int numVertices;
	private int numComponents;
	private int componentType;

	VertexArray() { }

	public VertexArray(int numVertices, int numComponents, int componentSize)
	{
		/* As per JSR-184, throw IllegalArgumentException if any of the parameters are outside of their allowed range. */
		if((numVertices < 1 || numVertices > 65535) || (numComponents < 2 || numComponents > 4) || (componentSize < 1 || componentSize > 2))
			{ throw new IllegalArgumentException("Vertex Array parameter is out of bounds."); }

		this.numVertices = numVertices;
		this.numComponents = numComponents;
		this.componentType = componentSize;

		// Component size is either Byte (1) or Short (2)
		switch (componentType)
		{
			case 1:
				this.vertArrayByteSize = new byte[numVertices * numComponents];
				this.vertArrayShortSize = null;
				break;
			case 2:
				this.vertArrayByteSize = null;
				this.vertArrayShortSize = new short[numVertices * numComponents];
				break;
		}
	}

	protected Object3D duplicateImpl()
	 {
		VertexArray copy = (VertexArray) super.duplicateImpl();

		if(componentType == 1)
		{
			copy.vertArrayByteSize = new byte[vertArrayByteSize.length];
			System.arraycopy(this.vertArrayByteSize, 0, copy.vertArrayByteSize, 0, this.vertArrayByteSize.length);
		}
		else
		{
			copy.vertArrayShortSize = new short[vertArrayShortSize.length];
			System.arraycopy(this.vertArrayShortSize, 0, copy.vertArrayShortSize, 0, this.vertArrayShortSize.length);
		}

		return copy;
	}


	public void get(int firstVertex, int numVertices, byte[] values)
	{
		/* As per JSR-184, throw:
		 * NullPointerException if values is null.
		 * IllegalStateException if the commponentType specifies usage of 16-bit vertex attributes.
		 * IllegalArgumentException if numVertices < 0 or values.length < numVertices * getComponentCount
		 * IndexOutOfBoundsException if firstVertex < 0 or firstVertex + numVertices > getVertexCount
		 */
		if(values == null) { throw new NullPointerException("Cannot return the values into a null array."); }
		if(this.componentType != 1) { throw new IllegalStateException("The set componentType is meant for 16-bit attributes, not 8-bit."); }
		if(numVertices < 0 || values.length < numVertices * this.numComponents)
			{ throw new IllegalArgumentException("Tried using negative number of vertices or incorrect array size."); }
		if(firstVertex < 0 || firstVertex + numVertices > this.numVertices)
			{ throw new IndexOutOfBoundsException("Tried to get a range of values that's out of bounds."); }

		System.arraycopy(this.vertArrayByteSize, firstVertex * this.numComponents,
			values, 0, numVertices * this.numComponents);
	}

	public void get(int firstVertex, int numVertices, short[] values)
	{
		/* As per JSR-184, throw:
		 * NullPointerException if values is null.
		 * IllegalStateException if the commponentType specifies usage of 8-bit vertex attributes.
		 * IllegalArgumentException if numVertices < 0 or values.length < numVertices * getComponentCount
		 * IndexOutOfBoundsException if firstVertex < 0 or firstVertex + numVertices > getVertexCount
		 */
		if(values == null) { throw new NullPointerException("Cannot return the values into a null array."); }
		if(this.componentType != 2) { throw new IllegalStateException("The set componentType is meant for 8-bit attributes, not 16-bit."); }
		if(numVertices < 0 || values.length < numVertices * this.numComponents)
			{ throw new IllegalArgumentException("Tried using negative number of vertices or incorrect array size."); }
		if(firstVertex < 0 || firstVertex + numVertices > this.numVertices)
			{ throw new IndexOutOfBoundsException("Tried to get a range of values that's out of bounds."); }

		System.arraycopy(this.vertArrayShortSize, firstVertex * this.numComponents,
			values, 0, numVertices * this.numComponents);
	}

	public int getComponentCount() { return this.numComponents; }

	public int getComponentType() { return this.componentType; }

	public int getVertexCount() { return this.numVertices; }

	public void set(int firstVertex, int numVertices, byte[] values)
	{
		/* As per JSR-184, throw:
		 * NullPointerException if values is null.
		 * IllegalStateException if the commponentType specifies usage of 16-bit vertex attributes.
		 * IllegalArgumentException if numVertices < 0 or values.length < numVertices * getComponentCount
		 * IndexOutOfBoundsException if firstVertex < 0 or firstVertex + numVertices > getVertexCount
		 */
		if(values == null) { throw new NullPointerException("Cannot return the values into a null array."); }
		if(this.componentType != 1) { throw new IllegalStateException("The set componentType is meant for 16-bit attributes, not 8-bit."); }
		if(numVertices < 0 || values.length < numVertices * this.numComponents)
			{ throw new IllegalArgumentException("Tried using negative number of vertices or incorrect array size."); }
		if(firstVertex < 0 || firstVertex + numVertices > this.numVertices)
			{ throw new IndexOutOfBoundsException("Tried to get a range of values that's out of bounds."); }

		System.arraycopy(values, 0,this.vertArrayByteSize,
			firstVertex * this.numComponents, numVertices * this.numComponents);
	}

	public void set(int firstVertex, int numVertices, short[] values)
	{
		/* As per JSR-184, throw:
		 * NullPointerException if values is null.
		 * IllegalStateException if the commponentType specifies usage of 8-bit vertex attributes.
		 * IllegalArgumentException if numVertices < 0 or values.length < numVertices * getComponentCount
		 * IndexOutOfBoundsException if firstVertex < 0 or firstVertex + numVertices > getVertexCount
		 */
		if(values == null) { throw new NullPointerException("Cannot return the values into a null array."); }
		if(this.componentType != 2) { throw new IllegalStateException("The set componentType is meant for 8-bit attributes, not 16-bit."); }
		if(numVertices < 0 || values.length < numVertices * this.numComponents)
			{ throw new IllegalArgumentException("Tried using negative number of vertices or incorrect array size."); }
		if(firstVertex < 0 || firstVertex + numVertices > this.numVertices)
			{ throw new IndexOutOfBoundsException("Tried to get a range of values that's out of bounds."); }

		System.arraycopy(values, 0,this.vertArrayShortSize,
			firstVertex * this.numComponents, numVertices * this.numComponents);
	}

}
