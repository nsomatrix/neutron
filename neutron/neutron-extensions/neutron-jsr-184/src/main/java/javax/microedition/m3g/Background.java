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


public class Background extends Object3D
{

	public static final int BORDER = 32;
	public static final int REPEAT = 33;

	private int color = 0x00000000;
	private int modex = BORDER;
	private int modey = BORDER;
	private int cropw = 0;
	private int croph = 0;
	private int cropx = 0;
	private int cropy = 0;

	private Image2D image = null;
	private boolean depthclear = true;
	private boolean colorclear = true;
	private Texture2D texture = null;

	// top right, top left, bottom right, bottom left coordinates
	private float[] vertexArray = { 1.0f, 1.0f, 0.0f, -1.0f, 1.0f, 0.0f, 1.0f, -1.0f, 0.0f, -1.0f, -1.0f, 0.0f };
	private float[] textureArray = new float[4 * 2];

	public Background() { }

	protected Object3D duplicateImpl()
	{
		Background copy = (Background) super.duplicateImpl();
		copy.color = this.color;
		copy.modex = this.modex;
		copy.modey = this.modey;
		copy.cropx = this.cropx;
		copy.cropy = this.cropy;
		copy.cropw = this.cropw;
		copy.croph = this.croph;
		copy.depthclear = this.depthclear;
		copy.colorclear = this.colorclear;

		copy.setImage(this.image);
		return copy;
	}

	public int getColor() { return color; }

	public int getCropHeight() { return croph; }

	public int getCropWidth() { return cropw; }

	public int getCropX() { return cropx; }

	public int getCropY() { return cropy; }

	public Image2D getImage() { return image; }

	public int getImageModeX() { return modex; }

	public int getImageModeY() { return modey; }

	public boolean isColorClearEnabled() { return colorclear; }

	public boolean isDepthClearEnabled() { return depthclear; }

	public void setColor(int ARGB) { color = ARGB; }

	public void setColorClearEnable(boolean enable) { colorclear = enable; }

	public void setCrop(int cropX, int cropY, int width, int height)
	{
		cropx=cropX;
		cropy=cropY;
		cropw=width;
		croph=height;
	}

	public void setDepthClearEnable(boolean enable) { depthclear = enable; }

	public void setImage(Image2D img)
	{
		if (img != null && img.getFormat() != Image2D.RGB && img.getFormat() != Image2D.RGBA)
		{
			throw new IllegalArgumentException("Image format must be RGB or RGBA");
		}

		removeReference(this.image);
		this.image = img;
		addReference(this.image);

		if (img != null)
		{
			if (cropw == 0 && croph == 0)
			{
				cropw = img.getWidth();
				croph = img.getHeight();
			}

			texture = new Texture2D(img);
			texture.setFiltering(Texture2D.FILTER_LINEAR, Texture2D.FILTER_LINEAR);
			texture.setWrapping(Texture2D.WRAP_CLAMP, Texture2D.WRAP_CLAMP);
			texture.setBlending(Texture2D.FUNC_REPLACE);
		}
		else { texture = null; }
	}

	public void setImageMode(int modeX, int modeY)
	{
		if (((modeX != BORDER) && (modeX != REPEAT)) || ((modeY != BORDER) && (modeY != REPEAT)))
		{
			throw new IllegalArgumentException("Invalid image mode for background");
		}
		modex=modeX;
		modey=modeY;
	}

	@Override
	void updateProperty(int property, float[] value)
	{
		M3GLog.log(M3GLog.LOG_DEBUG, Graphics3D.class.getPackage().getName() + "." + Graphics3D.class.getSimpleName() + ": " + "AnimTrack updating background property");
		switch (property)
		{
			case AnimationTrack.ALPHA:
				int alpha = M3GMath.min(255, M3GMath.max(0, M3GMath.roundPositive(value[0] * 255.0f)));
				color = (color & 0x00FFFFFF) | (alpha << 24);
				break;
			case AnimationTrack.COLOR:
				int r = M3GMath.min(255, M3GMath.max(0, M3GMath.roundPositive(value[0] * 255.0f)));
				int g = M3GMath.min(255, M3GMath.max(0, M3GMath.roundPositive(value[1] * 255.0f)));
				int b = M3GMath.min(255, M3GMath.max(0, M3GMath.roundPositive(value[2] * 255.0f)));
				color = (color & 0xFF000000) | (r << 16) | (g << 8) | b;
				break;
			case AnimationTrack.CROP:
				int x = (int) value[0];
				int y = (int) value[1];
				int width = (value.length > 2) ? (int) value[2] : cropw;
				int height = (value.length > 3) ? (int) value[3] : croph;
				setCrop(x, y, width, height);
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
			case AnimationTrack.COLOR:
			case AnimationTrack.CROP:
				return true;
			default:
				return super.animTrackCompatible(track);
		}
	}
}
