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


public class Fog extends Object3D
{

	public static final int	EXPONENTIAL = 80;
	public static final int LINEAR = 81;

	private float near = 0.0f;
	private float far = 1.0f;
	private int mode = LINEAR;
	private int color = 0x00000000;
	private float density = 1.0f;

	public Fog() { }

	protected Object3D duplicateImpl()
	{
		Fog copy = (Fog) super.duplicateImpl();
		copy.near = this.near;
		copy.far = this.far;
		copy.mode = this.mode;
		copy.color = this.color;
		copy.density = this.density;
		return copy;
	}

	public int getColor() { return this.color; }

	public float getDensity() { return this.density; }

	public float getFarDistance() { return this.far; }

	public int getMode() { return this.mode; }

	public float getNearDistance() { return this.near; }

	public void setColor(int RGB) { this.color = RGB & 0x00FFFFFF; }

	public void setDensity(float value)
	{
		if(value < 0.0f) { throw new IllegalArgumentException("Invalid density value"); }
		this.density = value;
	}

	public void setLinear(float Near, float Far)
	{
		this.near = Near;
		this.far = Far;
	}

	public void setMode(int value)
	{
		if(value != LINEAR && value != EXPONENTIAL)
			{ throw new IllegalArgumentException("Fog only supports LINEAR and EXPONENTIAL types"); }
		this.mode = value;
	}

	void updateProperty(int property, float[] value)
	{
		M3GLog.log(M3GLog.LOG_DEBUG, Graphics3D.class.getPackage().getName() + "." + Graphics3D.class.getSimpleName() + ": " + "AnimTrack updating Fog property");
		switch (property)
		{
			case AnimationTrack.COLOR:
				int r = M3GMath.max(0, M3GMath.min(255, (int) (value[0] <= 1.0f ? value[0] * 255.0f : value[0])));
				int g = M3GMath.max(0, M3GMath.min(255, (int) (value[1] <= 1.0f ? value[1] * 255.0f : value[1])));
				int b = M3GMath.max(0, M3GMath.min(255, (int) (value[2] <= 1.0f ? value[2] * 255.0f : value[2])));
				this.color = (r << 16) | (g << 8) | b;
				break;
			case AnimationTrack.DENSITY:
				this.density = M3GMath.max(0.0f, value[0]);
				break;
			case AnimationTrack.FAR_DISTANCE:
				far = value[0];
				break;
			case AnimationTrack.NEAR_DISTANCE:
				near = value[0];
				break;
			default:
				super.updateProperty(property, value);
		}
	}

	boolean animTrackCompatible(AnimationTrack track)
	{
		switch (track.getTargetProperty())
		{
			case AnimationTrack.COLOR:
			case AnimationTrack.DENSITY:
			case AnimationTrack.FAR_DISTANCE:
			case AnimationTrack.NEAR_DISTANCE:
				return true;
			default:
				return super.animTrackCompatible(track);
		}
	}
}
