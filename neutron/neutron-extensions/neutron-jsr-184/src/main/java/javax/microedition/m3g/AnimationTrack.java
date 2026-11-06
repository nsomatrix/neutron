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

public class AnimationTrack extends Object3D
{

	public static final int ALPHA = 256;
	public static final int AMBIENT_COLOR = 257;
	public static final int COLOR = 258;
	public static final int CROP = 259;
	public static final int DENSITY = 260;
	public static final int DIFFUSE_COLOR = 261;
	public static final int EMISSIVE_COLOR = 262;
	public static final int FAR_DISTANCE = 263;
	public static final int FIELD_OF_VIEW = 264;
	public static final int INTENSITY = 265;
	public static final int MORPH_WEIGHTS = 266;
	public static final int NEAR_DISTANCE = 267;
	public static final int ORIENTATION = 268;
	public static final int PICKABILITY = 269;
	public static final int SCALE = 270;
	public static final int SHININESS = 271;
	public static final int SPECULAR_COLOR = 272;
	public static final int SPOT_ANGLE = 273;
	public static final int SPOT_EXPONENT = 274;
	public static final int TRANSLATION = 275;
	public static final int VISIBILITY = 276;


	public KeyframeSequence sequence;
	public int property;
	private AnimationController controller;

	private float[] sample;

	public AnimationTrack(KeyframeSequence sequence, int property)
	{
		if (sequence == null) { throw new NullPointerException("Sequence must not be null"); }
		if ((property < ALPHA) || (property > VISIBILITY)) { throw new IllegalArgumentException("Unknown property"); }
		if (!isCompatible(sequence.getComponentCount(), property)) { throw new IllegalArgumentException("Sequence is not compatible with property"); }
		this.sequence = sequence;
		this.property = property;
		addReference(this.sequence);
	}

	protected Object3D duplicateImpl()
	{
		AnimationTrack copy = (AnimationTrack) super.duplicateImpl();
		copy.sequence = this.sequence;
		copy.property = this.property;
		copy.setController(this.controller);

		addReference(copy.sequence);
		return copy;
	}

	public void getContribution(int time, float[] accumSamples, float[] weight, int[] validity)
	{
		if (this.controller == null || !controller.isActive(time))
		{
			weight[0] = 0;
			int timeToAct = (controller != null) ? controller.timeToActivation(time) : Integer.MAX_VALUE;
			validity[0] = M3GMath.max(1, timeToAct);
			return;
		}

		weight[0] = controller.getWeight();

		if (weight[0] <= 0.0f)
		{
			validity[0] = Integer.MAX_VALUE;
			return;
		}

		int sampleLength = sequence.getComponentCount();

		if (this.sample == null || this.sample.length < sampleLength)
			{ this.sample = new float[sampleLength]; }

		float speed = controller.getSpeed();
		float sampleTime = controller.getPosition(time);
		validity[0] = sequence.getSample(sampleTime, this.sample);

		int worldValidity;
		if (speed == 0.0f) { worldValidity = Integer.MAX_VALUE; }
		else { worldValidity = (int) M3GMath.roundPositive(validity[0] / M3GMath.abs(speed)); }

		int timeToDeact = controller.timeToDeactivation(time);
		validity[0] = M3GMath.max(1, M3GMath.min(worldValidity, timeToDeact));

		for (int i = 0; i < sampleLength; i++) { accumSamples[i] += this.sample[i]; }
	}

	public AnimationController getController() { return controller; }

	public void setController(AnimationController controller)
	{
		removeReference(this.controller);
		this.controller = controller;
		addReference(this.controller);
	}

	public int getTargetProperty() { return property; }

	public KeyframeSequence getKeyframeSequence() { return sequence; }

	private boolean isCompatible(int components, int property)
	{
		switch (property)
		{
			case ALPHA:
			case DENSITY:
			case FAR_DISTANCE:
			case FIELD_OF_VIEW:
			case INTENSITY:
			case NEAR_DISTANCE:
			case PICKABILITY:
			case SHININESS:
			case SPOT_ANGLE:
			case SPOT_EXPONENT:
			case VISIBILITY:
				return components == 1;
			case CROP:
				return components == 2 || components == 4;
			case AMBIENT_COLOR:
			case COLOR:
			case DIFFUSE_COLOR:
			case EMISSIVE_COLOR:
			case SPECULAR_COLOR:
			case TRANSLATION:
				return components == 3;
			case SCALE:
				return components == 1 || components == 3;
			case ORIENTATION:
				return components == 4;
			case MORPH_WEIGHTS:
				return components > 0;
			default:
				return false; // Shouldn't occur
		}
	}
}
