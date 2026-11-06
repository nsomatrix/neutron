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


public class Material extends Object3D
{
	public static final int AMBIENT = 1024;
	public static final int DIFFUSE = 2048;
	public static final int EMISSIVE = 4096;
	public static final int SPECULAR = 8192;

	private int ambientColor = 0x00333333;
	private int diffuseColor = 0xFFCCCCCC;
	private int emissiveColor = 0x00000000;
	private int specularColor = 0x00000000;
	private float shininess = 0.0f;
	private boolean tracking = false;

	public Material() { }

	protected Object3D duplicateImpl()
	{
		Material copy = (Material) super.duplicateImpl();
		copy.ambientColor = this.ambientColor;
		copy.diffuseColor = this.diffuseColor;
		copy.emissiveColor = this.emissiveColor;
		copy.specularColor = this.specularColor;
		copy.shininess = this.shininess;
		copy.tracking = this.tracking;
		return copy;
	}

	public int getColor(int target)
	{
		/* As per JSR-184, throw IllegalArgumentException if target has a value other than AMBIENT, DIFFUSSE, EMISSIVE or SPECULAR. */
		if (target != AMBIENT && target != DIFFUSE && target != EMISSIVE && target != SPECULAR)
			{ throw new IllegalArgumentException("Tried to get invalid color component from material."); }

		switch(target)
		{
			case DIFFUSE: return this.diffuseColor;
			case EMISSIVE: return this.emissiveColor;
			case SPECULAR: return this.specularColor;
			case AMBIENT:
			default: return this.ambientColor;
		}
	}

	public float getShininess() { return this.shininess; }

	public boolean isVertexColorTrackingEnabled() { return this.tracking; }

	public void setColor(int target, int ARGB)
	{
		/* As per JSR-184, throw IllegalArgumentException if target has a value other than an inclusive OR of one or more of AMBIENT, DIFFUSE, EMISSIVE, SPECULAR. */
		if (target == 0 || (target & ~(AMBIENT | DIFFUSE | EMISSIVE | SPECULAR)) != 0)
			{throw new IllegalArgumentException("Trying to set material color on invalid material component."); }


		if ((target & AMBIENT)  != 0) { this.ambientColor = ARGB & 0x00FFFFFF;  }
		if ((target & DIFFUSE)  != 0) { this.diffuseColor = ARGB;  }
		if ((target & EMISSIVE) != 0) { this.emissiveColor = ARGB & 0x00FFFFFF; }
		if ((target & SPECULAR) != 0) { this.specularColor = ARGB & 0x00FFFFFF; }
	}

	public void setShininess(float shininess)
	{
		/* As per JSR-184, throw IllegalArgumentException if shininess > 128 or < 0. */
		if(shininess < 0f || shininess > 128.0f) { throw new IllegalArgumentException("Material received invalid shininess value:" + shininess); }

		this.shininess = shininess;
	}

	public void setVertexColorTrackingEnable(boolean enable) { this.tracking = enable; }

	void updateProperty(int property, float[] value)
	{
		M3GLog.log(M3GLog.LOG_DEBUG, Graphics3D.class.getPackage().getName() + "." + Graphics3D.class.getSimpleName() + ": " + "AnimTrack updating material property");

		switch (property)
		{
			case AnimationTrack.ALPHA:
				int a = M3GMath.max(0, M3GMath.min(255, (int) (value[0] <= 1.0f ? value[0] * 255.0f : value[0])));
				this.diffuseColor = (a << 24) | (this.diffuseColor & 0x00FFFFFF);
				break;
			case AnimationTrack.AMBIENT_COLOR:
				this.ambientColor = parseRGB(value);
				break;
			case AnimationTrack.DIFFUSE_COLOR:
				this.diffuseColor = (this.diffuseColor & 0xFF000000) | parseRGB(value);
				break;
			case AnimationTrack.EMISSIVE_COLOR:
				this.emissiveColor = parseRGB(value);
				break;
			case AnimationTrack.SPECULAR_COLOR:
				this.specularColor = parseRGB(value);
				break;
			case AnimationTrack.SHININESS:
				this.shininess = M3GMath.max(0.0f, M3GMath.min(128.0f, value[0]));
				break;
			default:
				super.updateProperty(property, value);
		}
	}

	boolean animTrackCompatible(AnimationTrack track)
	{
		switch (track.getTargetProperty()) {
			case AnimationTrack.ALPHA:
			case AnimationTrack.AMBIENT_COLOR:
			case AnimationTrack.DIFFUSE_COLOR:
			case AnimationTrack.EMISSIVE_COLOR:
			case AnimationTrack.SHININESS:
			case AnimationTrack.SPECULAR_COLOR:
				return true;
			default:
				return super.animTrackCompatible(track);
		}
	}

	private static int parseRGB(float[] value)
	{
		int r = M3GMath.max(0, M3GMath.min(255, (int) (value[0] <= 1.0f ? value[0] * 255.0f : value[0])));
		int g = M3GMath.max(0, M3GMath.min(255, (int) (value[1] <= 1.0f ? value[1] * 255.0f : value[1])));
		int b = M3GMath.max(0, M3GMath.min(255, (int) (value[2] <= 1.0f ? value[2] * 255.0f : value[2])));
		return (r << 16) | (g << 8) | b;
	}
}
