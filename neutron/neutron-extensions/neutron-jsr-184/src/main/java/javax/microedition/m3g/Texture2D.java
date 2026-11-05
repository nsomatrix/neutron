package javax.microedition.m3g;

public class Texture2D extends Transformable {

	public static final int FILTER_BASE_LEVEL = 208;
	public static final int FILTER_LINEAR = 209;
	public static final int FILTER_NEAREST = 210;

	public static final int WRAP_CLAMP = 240;
	public static final int WRAP_REPEAT = 241;

	public static final int FUNC_ADD = 224;
	public static final int FUNC_BLEND = 225;
	public static final int FUNC_DECAL = 226;
	public static final int FUNC_MODULATE = 227;
	public static final int FUNC_REPLACE = 228;

	private Image2D image;
	private int levelFilter = FILTER_BASE_LEVEL;
	private int imageFilter = FILTER_NEAREST;
	private int wrappingS = WRAP_REPEAT;
	private int wrappingT = WRAP_REPEAT;
	private int blending = FUNC_MODULATE;
	private int blendColor = 0x00000000;

	public Texture2D(Image2D image) {
		setImage(image);
	}

	public void setImage(Image2D image) {
		if (image == null) throw new NullPointerException();
		this.image = image;
	}

	public Image2D getImage() { return image; }

	public void setFiltering(int levelFilter, int imageFilter) {
		this.levelFilter = levelFilter;
		this.imageFilter = imageFilter;
	}

	public int getLevelFilter() { return levelFilter; }
	public int getImageFilter() { return imageFilter; }

	public void setWrapping(int wrapS, int wrapT) {
		this.wrappingS = wrapS;
		this.wrappingT = wrapT;
	}

	public int getWrappingS() { return wrappingS; }
	public int getWrappingT() { return wrappingT; }

	public void setBlending(int func) {
		this.blending = func;
	}

	public int getBlending() { return blending; }

	public void setBlendColor(int argb) {
		this.blendColor = argb;
	}

	public int getBlendColor() { return blendColor; }
}

