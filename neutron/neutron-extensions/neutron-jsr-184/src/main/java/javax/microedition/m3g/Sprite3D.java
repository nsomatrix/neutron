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


public class Sprite3D extends Node
{
	private Image2D image;
	private Appearance appearance;
	private boolean scaled;

	private int cropw;
	private int croph;
	private int cropx;
	private int cropy;

	public Sprite3D(boolean isScaled, Image2D img, Appearance a)
	{
		/* As per JSR-184, the image cannot be null, and the crop rectangle initially covers the whole image. */
		if (img == null) { throw new NullPointerException("Cannot create a Sprite3D with a null image."); }
		scaled = isScaled;
		setImage(img);
		setAppearance(a);
		cropx = 0;
		cropy = 0;
		cropw = img.getWidth();
		croph = img.getHeight();
	}

	protected Object3D duplicateImpl()
	{
		Sprite3D copy = (Sprite3D) super.duplicateImpl();

		copy.scaled = this.scaled;
		copy.setCrop(this.cropx, this.cropy, this.cropw, this.croph);

		copy.setImage(this.image);
		copy.setAppearance(this.getAppearance());
		return copy;
	}

	public Appearance getAppearance() { return appearance; }

	public int getCropHeight() { return croph; }

	public int getCropWidth() { return cropw; }

	public int getCropX() { return cropx; }

	public int getCropY() { return cropy; }

	public Image2D getImage() { return image; }

	public boolean isScaled() { return scaled; }

	public void setAppearance(Appearance a)
	{
		removeReference(appearance);
		appearance = a;
		addReference(appearance);
	}

	public void setCrop(int cropX, int cropY, int width, int height)
	{
		if (M3GMath.abs(width) > Graphics3D.MAX_TEXTURE_DIMENSION ||
			M3GMath.abs(height) > Graphics3D.MAX_TEXTURE_DIMENSION)
		{
			throw new IllegalArgumentException("Crop width or height magnitude exceeds MAX_TEXTURE_DIMENSION");
		}
		cropx=cropX;
		cropy=cropY;
		cropw=width;
		croph=height;
	}

	public void setImage(Image2D img)
	{
		if(img == null) { throw new NullPointerException("Cannot set null image on a Sprite3D"); }
		removeReference(this.image);
		this.image = img;
		addReference(this.image);
		cropx = 0;
		cropy = 0;
		cropw = img.getWidth();
		croph = img.getHeight();
	}

	@Override
	void updateProperty(int property, float[] value)
	{
		M3GLog.log(M3GLog.LOG_DEBUG, Graphics3D.class.getPackage().getName() + "." + Graphics3D.class.getSimpleName() + ": " + "AnimTrack updating Sprite3D property");
		switch (property)
		{
			case AnimationTrack.CROP:
				if (value.length > 2)
				{
					setCrop((int)value[0], (int)value[1], (int) M3GMath.max(-Graphics3D.MAX_TEXTURE_DIMENSION, M3GMath.min(Graphics3D.MAX_TEXTURE_DIMENSION, value[2])),
							(int) M3GMath.max(-Graphics3D.MAX_TEXTURE_DIMENSION, M3GMath.min(Graphics3D.MAX_TEXTURE_DIMENSION, value[3])));
				}
				else
				{
					setCrop((int)value[0], (int)value[1], getCropWidth(), getCropHeight());
				}
				break;

			default:
				super.updateProperty(property, value);
		}
	}

	boolean animTrackCompatible(AnimationTrack track)
	{
		switch (track.getTargetProperty())
		{
			case AnimationTrack.CROP:
				return true;
			default:
				return super.animTrackCompatible(track);
		}
	}
}
