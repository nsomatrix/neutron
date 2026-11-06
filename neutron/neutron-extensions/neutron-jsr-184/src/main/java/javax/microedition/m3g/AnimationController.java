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

public class AnimationController extends Object3D
{

	private int activationTime = 0;
	private int deactivationTime = 0;
	private float speed = 1.0f;
	private int refWorldTime = 0;
	private float refSequenceTime = 0;
	private float weight = 1.0f;

	protected Object3D duplicateImpl()
	{
		AnimationController copy = (AnimationController) super.duplicateImpl();
		copy.activationTime = this.activationTime;
		copy.deactivationTime = this.deactivationTime;
		copy.speed = this.speed;
		copy.refWorldTime = this.refWorldTime;
		copy.refSequenceTime = this.refSequenceTime;
		copy.weight = this.weight;
		return copy;
	}

	public boolean isActive(int worldTime)
	{
		// start == end? Then the controller is active at all world times
		if (activationTime == deactivationTime) { return true; }

		return (worldTime >= activationTime && worldTime < deactivationTime);
	}

	public int timeToActivation(int worldTime)
	{
		if (worldTime < activationTime) { return activationTime - worldTime; }
		else if (worldTime <= deactivationTime) { return 0; }

		return Integer.MAX_VALUE;
	}

	public int timeToDeactivation(int worldTime)
	{
		if (worldTime < deactivationTime) { return deactivationTime - worldTime; }

		return Integer.MAX_VALUE;
	}

	public void setActiveInterval(int start, int end)
	{
		if (start > end)
			throw new IllegalArgumentException("Start time must be inferior to end time");

		activationTime = start;
		deactivationTime = end;
	}

	public int getActiveIntervalStart() { return activationTime; }

	public int getActiveIntervalEnd() { return deactivationTime; }

	public void setSpeed(float speed, int worldTime)
	{
		this.refSequenceTime = getPosition(worldTime);
		this.refWorldTime = worldTime;
		this.speed = speed;
	}

	public float getSpeed() { return speed; }

	public void setPosition(float sequenceTime, int worldTime)
	{
		this.refSequenceTime = sequenceTime;
		this.refWorldTime = worldTime;
	}

	public float getPosition(int worldTime)
	{
		return (refSequenceTime + (speed * (float) (worldTime - refWorldTime)));
	}

	public int getRefWorldTime() { return refWorldTime; }

	public void setWeight(float weight)
	{
		if (weight < 0)
			throw new IllegalArgumentException("Weight must be positive or zero");
		this.weight = weight;
	}

	public float getWeight() { return weight; }
}
