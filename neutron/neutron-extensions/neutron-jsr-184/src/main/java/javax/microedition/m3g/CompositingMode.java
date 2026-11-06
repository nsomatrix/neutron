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


public class CompositingMode extends Object3D
{
	public static final int ALPHA = 64;
	public static final int ALPHA_ADD = 65;
	public static final int MODULATE = 66;
	public static final int MODULATE_X2 = 67;
	public static final int REPLACE = 68;

	private int blending = REPLACE;
	private float alphaThreshold = 0.0f;
	private boolean alphaWrite = true;
	private boolean depthWrite = true;
	private boolean depthTest = true;
	private float depthOffsetUnits = 0.0f;
	private float depthOffsetFactor = 0.0f;
	private boolean colorWrite = true;


	public CompositingMode() { }

	protected Object3D duplicateImpl()
	{
		CompositingMode copy = (CompositingMode) super.duplicateImpl();
		copy.blending = this.blending;
		copy.alphaThreshold = this.alphaThreshold;
		copy.alphaWrite = this.alphaWrite;
		copy.depthWrite = this.depthWrite;
		copy.depthTest = this.depthTest;
		copy.depthOffsetUnits = this.depthOffsetUnits;
		copy.depthOffsetFactor = this.depthOffsetFactor;
		copy.colorWrite = this.colorWrite;
		return copy;
	}

	public float getAlphaThreshold() { return this.alphaThreshold; }

	public int getBlending() { return this.blending; }

	public float getDepthOffsetFactor() { return this.depthOffsetFactor; }

	public float getDepthOffsetUnits() { return this.depthOffsetUnits; }

	public boolean isAlphaWriteEnabled() { return this.alphaWrite; }

	public boolean isColorWriteEnabled() { return this.colorWrite; }

	public boolean isDepthTestEnabled() { return this.depthTest; }

	public boolean isDepthWriteEnabled() { return this.depthWrite; }

	public void setAlphaThreshold(float threshold)
	{
		if (threshold < 0.0f || 1.0f < threshold)
			{ throw new IllegalArgumentException("Invalid threshold:" + threshold); }

		this.alphaThreshold = threshold;
	}

	public void setAlphaWriteEnable(boolean enable) { this.alphaWrite = enable; }

	public void setBlending(int mode)
	{
		if (mode != ALPHA &&
			mode != ALPHA_ADD &&
			mode != MODULATE &&
			mode != MODULATE_X2 &&
			mode != REPLACE)
			{ throw new IllegalArgumentException("Invalid blend mode:" + mode); }

		this.blending = mode;
	}

	public void setColorWriteEnable(boolean enable) { this.colorWrite = enable; }

	public void setDepthOffset(float factor, float units)
	{
		this.depthOffsetFactor = factor;
		this.depthOffsetUnits = units;
	}

	public void setDepthTestEnable(boolean enable) { this.depthTest = enable; }

	public void setDepthWriteEnable(boolean enable) { this.depthWrite = enable; }

	void updateProperty(int property, float[] value)
	{
		M3GLog.log(M3GLog.LOG_DEBUG, Graphics3D.class.getPackage().getName() + "." + Graphics3D.class.getSimpleName() + ": " + "AnimTrack updating compositingMode property");
		switch (property)
		{
			case AnimationTrack.ALPHA:
				this.alphaThreshold = M3GMath.max(0.0f, M3GMath.min(1.0f, value[0]));
				break;
			default:
				super.updateProperty(property, value);
		}
	}

	boolean animTrackCompatible(AnimationTrack track)
	{
		switch (track.getTargetProperty())
		{
			case AnimationTrack.ALPHA:
				return true;
			default:
				return super.animTrackCompatible(track);
		}
	}
}
