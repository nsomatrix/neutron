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

public class TriangleStripArray extends IndexBuffer
{

	TriangleStripArray() { }

	public TriangleStripArray(int[] indices, int[] stripLengths)
	{
		/* Per JSR-184, throw NullPointerException if indices or stripLengths are null */
		if(indices == null || stripLengths == null) { throw new NullPointerException("Tried to construct a TriangleStripArray with incomplete information."); }

		/*
		 * Also per JSR-184, throw IllegalArgumentException if:
		 * stripLengths is empty, any element in stripLengths is less than 3, or indices.length < sum(stripLengths).
		 */
		int totalStripLength = sum(stripLengths);

		if(stripLengths.length == 0 || indices.length < totalStripLength || hasLengthLessThan3(stripLengths))
			{ throw new IllegalArgumentException("Cannot construct TriangleStripArray, incorrect parameters received."); }

		/* also per JSR-184, throw IndexOutOfBoundsException if any element in indices is negative, or greater than 65535. */
		if(hasInvalidIndices(indices, totalStripLength))
			{ throw new IndexOutOfBoundsException("Index provided to TriangleStripArray is out of bounds."); }

		/* Setup the StripArray with explicit indices. */
		this.updateFields(true, indices, stripLengths);
	}

	public TriangleStripArray(int firstIndex, int[] stripLengths)
	{
		/* As per JSR-184, throw NullPointerException if stripLengths == null. */
		if(stripLengths == null) { throw new NullPointerException("Tried to construct TriangleStripArray with null stripLengths."); }

		/* Also per JSR-184, throw IllegalArgumentException if stripLengths.length == 0 or any element in stripLengths is less than 3. */
		if(stripLengths.length == 0 || hasLengthLessThan3(stripLengths))
			{ throw new IllegalArgumentException("Cannot construct TriangleStripArray, incorrect parameters received."); }

		/* Also per JSR-184, throw IndexOutOfBoundsException if any element in indices is negative, or if firstIndex + sum(stripLengths) is greater than 65535. */
		int totalStripLength = sum(stripLengths);

		if(firstIndex < 0 || firstIndex + totalStripLength > 65535)
			{ throw new IndexOutOfBoundsException("Index provided to TriangleStripArray is out of bounds."); }

		/* Setup the StripArray with implicit indices. */
		this.updateFields(false, new int[] { firstIndex }, stripLengths);
	}

	protected Object3D duplicateImpl() { return (TriangleStripArray) super.duplicateImpl(); }

	private void updateFields(boolean isExplicit, int[] indices, int[] stripLengths)
	{
		/* Update the number of indices from the parent by mapping all valid StripLength elements. */
		super.indexCount = calculateIndexCount(stripLengths);

		/* Update parent's indices by copying this object's indices to it. */
		super.indices = new int[this.indexCount];

		int  in_offset = 0;
		int out_offset = 0;

		for (int strip_id = 0; strip_id < stripLengths.length; strip_id++)
		{
			for (int i = 0; i < (stripLengths[strip_id] - 2); i++)
			{
				int x, y, z;
                int abs_index = in_offset + i;
                boolean swap = (i % 2 == 1);

				if (isExplicit)
				{
					x = indices[abs_index + 0];
					y = indices[abs_index + 1];
					z = indices[abs_index + 2];
				}
				else
				{
					x = indices[0] + abs_index + 0;
					y = indices[0] + abs_index + 1;
					z = indices[0] + abs_index + 2;
				}

				// Swap vertices for odd triangles to maintaining face orientation
				// so that Triangle.java doesn't need to do that in the render loop.
				super.indices[out_offset + 0] = x;
				super.indices[out_offset + 1] = swap ? z : y;
				super.indices[out_offset + 2] = swap ? y : z;

				/* Move to the next vertex on the parent object. */
				out_offset += 3;
			}

			/* Move to the next vertex on this StripArray. */
			in_offset += stripLengths[strip_id];
		}
	}

	private int sum(int[] array)
	{
		int total = 0;
		for (int value : array) { total += value; }

		return total;
	}

	private boolean hasLengthLessThan3(int[] array)
	{
		for (int value : array)
		{
			if (value < 3) { return true; }
		}
		return false;
	}

	private boolean hasInvalidIndices(int[] array, int numIndices)
    {
        for (int i = 0; i < numIndices; i++)
        {
            if (array[i] < 0 || array[i] > 65535) { return true; }
        }

        return false;
    }

	private int calculateIndexCount(int[] stripLengths)
	{
		int total = 0;
		for (int length : stripLengths) { total += (length - 2) * 3; }

		return total;
	}
}
