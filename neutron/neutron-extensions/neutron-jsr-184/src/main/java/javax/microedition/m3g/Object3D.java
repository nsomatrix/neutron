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

import java.util.Vector;

public abstract class Object3D implements Cloneable
{
	protected int userID = 0;
	protected Object userObject = null;
	Vector<AnimationTrack> animationTracks = new Vector<AnimationTrack>();
	Vector<Object3D> curReferences = new Vector<Object3D>();

	public Object3D() { }

	void updateProperty(int property, float[] value) { }

	public final Object3D duplicate() { return this.duplicateImpl(); }

	protected Object3D duplicateImpl()
	{
		try
		{
			Object3D copy = (Object3D) super.clone();

			copy.userID = this.userID;
			copy.userObject = this.userObject;

			copy.curReferences = new Vector<Object3D>();
			copy.animationTracks = new Vector<AnimationTrack>();

			for (int i = 0; i < this.animationTracks.size(); i++)
			{
				AnimationTrack origTrack = this.animationTracks.elementAt(i);
				AnimationTrack trackCopy = (AnimationTrack) origTrack.duplicate();
				copy.animationTracks.addElement(trackCopy);
				copy.addReference(trackCopy);
			}

			return copy;
		}
		catch (CloneNotSupportedException e)
		{
			// Object3D implements Cloneable, so this should never even happen
			M3GLog.log(M3GLog.LOG_ERROR, Graphics3D.class.getPackage().getName() + "." + Graphics3D.class.getSimpleName() + ": " + "Object3D not cloneable? " + e.getMessage());
			throw new RuntimeException(e.getMessage());
		}
	}

	public Object3D find(int userID)
	{
		Vector<Object3D> visited = new Vector<Object3D>();

		return findInternal(userID, visited);
	}

	private Object3D findInternal(int targetUserID, Vector<Object3D> visited)
	{
		if (visited.contains(this)) { return null; }
		visited.addElement(this);

		/*
		 * Per JSR-184, find() returns any reachable object with the given user ID,
		 * and 0 is a perfectly valid ID to search for - it is in fact the default
		 * ID of every object, so find(0) typically matches the object itself.
		 * 
		 * Sega Rally 3D calls world.find(0) on loaded wrapper Worlds (whose
		 * objects all carry the default ID) and treats a null return as a load
		 * failure, crashing during its loading screen.
		 */
		if (this.userID == targetUserID)
		{
			return this;
		}

		for (int i = 0; i < this.curReferences.size(); i++)
		{
			Object3D ref = this.curReferences.elementAt(i);
			if (ref != null)
			{
				Object3D found = ref.findInternal(targetUserID, visited);
				if (found != null) { return found; }
			}
		}

		return null;
	}

	public int getReferences(Object3D[] references)
	{
		if(references != null && references.length < this.curReferences.size())
			{ throw new IllegalArgumentException("references array is not large enough to hold all references"); }

		if (references != null)
		{
			// Fill up to array length or total references, whichever is smaller
			int maxToCopy = M3GMath.min(references.length, this.curReferences.size());
			for (int i = 0; i < maxToCopy; i++)
			{
				references[i] = (Object3D) this.curReferences.get(i);
			}
		}

		return curReferences.size();
	}

	public int getUserID() { return this.userID; }

	public void setUserID(int userID) { this.userID = userID; }

	public Object getUserObject() { return this.userObject; }

	public void setUserObject(Object userObject) { this.userObject = userObject; }

	public void addAnimationTrack(AnimationTrack animationTrack)
	{
		if (animationTrack == null) { throw new NullPointerException("AnimationTrack cannot be null"); }

		if (!animTrackCompatible(animationTrack))
		{
			throw new IllegalArgumentException("Animation track property is not compatible with this Object3D");
		}

		if (animationTracks.contains(animationTrack))
		{
			throw new IllegalArgumentException("AnimationTrack already exists");
		}

		int newTrackTarget = animationTrack.getTargetProperty();
		int components = animationTrack.getKeyframeSequence().getComponentCount();
		int i;
		for (i = 0; i < animationTracks.size(); i++)
		{
			AnimationTrack track = (AnimationTrack) animationTracks.elementAt(i);

			if (track.getTargetProperty() > newTrackTarget) { break; }

			if (track.getTargetProperty() == newTrackTarget && (track.getKeyframeSequence().getComponentCount() != components))
			{
				throw new IllegalArgumentException("Incompatible component count for animation track");
			}
		}

		animationTracks.add(i, animationTrack);
		addReference(animationTrack);
	}

	public AnimationTrack getAnimationTrack(int index)
	{
		if (index < 0 || index >= animationTracks.size())
		{
			throw new IndexOutOfBoundsException("AnimationTrack index is out of bounds: " + index);
		}

		return (AnimationTrack) animationTracks.elementAt(index);
	}

	public void removeAnimationTrack(AnimationTrack animationTrack)
	{
		if (animationTracks.removeElement(animationTrack))
		{
			removeReference(animationTrack);
		}
	}

	public int getAnimationTrackCount() { return animationTracks.size(); }

	public final int animate(int time)
	{
		int validity = Integer.MAX_VALUE;

		// Animate sub-references in scene graph hierarchy
		for (int i = 0; i < curReferences.size(); i++)
		{
			Object3D ref = curReferences.elementAt(i);
			if (ref != null)
			{
				int childValidity = ref.animate(time);
				validity = Math.min(validity, childValidity);
			}
		}

		if (animationTracks.isEmpty()) { return validity; }

		int numTracks = animationTracks.size();

		for (int trackIndex = 0; trackIndex < numTracks; )
		{
			AnimationTrack track = animationTracks.elementAt(trackIndex);
			KeyframeSequence sequence = track.getKeyframeSequence();

			int components = sequence.getComponentCount();
			int property = track.getTargetProperty();
			int nextProperty;

			float sumWeights = 0.0f;
			float[] sumValues = new float[components];
			float[] tempValues = new float[components];

			do
			{
				float[] weight = new float[1];
				int[] trackValidity = new int[1];

				for (int c = 0; c < components; c++) { tempValues[c] = 0.0f; }

				track.getContribution(time, tempValues, weight, trackValidity);

				if (trackValidity[0] > 0 && weight[0] > 0.0f)
				{
					sumWeights += weight[0];
					for (int c = 0; c < components; c++)
					{
						sumValues[c] += tempValues[c] * weight[0];
					}
				}

				validity = Math.min(validity, trackValidity[0]);

				if (++trackIndex == numTracks) { break; }
				track = animationTracks.elementAt(trackIndex);
				nextProperty = track.getTargetProperty();
			}
			while (nextProperty == property);

			if (sumWeights > 0.0f)
			{
				// Normalize sum values by weight sum before applying to property
				float invWeight = 1.0f / sumWeights;
				for (int c = 0; c < components; c++)
				{
					sumValues[c] *= invWeight;
				}
				updateProperty(property, sumValues);
			}
		}

		return validity;
	}

	boolean animTrackCompatible(AnimationTrack animationtrack) { return false; }

	protected void addReference(Object3D obj)
	{
		if(obj == null) { return; }
		if (!curReferences.contains(obj)) { curReferences.addElement(obj); }
	}

	protected void removeReference(Object3D obj)
	{
		if(obj == null) { return; }
		curReferences.remove(obj);
	}
}
