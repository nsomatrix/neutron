package javax.microedition.m3g;

public class Image2D extends Object3D {

	public static final int ALPHA = 96;
	public static final int LUMINANCE = 97;
	public static final int LUMINANCE_ALPHA = 98;
	public static final int RGB = 99;
	public static final int RGBA = 100;

	private final int format;
	private final int width;
	private final int height;
	private final boolean mutable;
	private final int[] pixels;

	public Image2D(int format, int width, int height) {
		if (width <= 0 || height <= 0) throw new IllegalArgumentException();
		if (format < ALPHA || format > RGBA) throw new IllegalArgumentException();
		this.format = format;
		this.width = width;
		this.height = height;
		this.mutable = true;
		this.pixels = new int[width * height];
	}

	public Image2D(int format, int width, int height, byte[] image) {
		this(format, width, height);
		set(0, 0, width, height, image);
	}

	public Image2D(int format, int width, int height, byte[] image, byte[] palette) {
		this(format, width, height);
		// Palette decoding
	}

	public Image2D(int format, Object image) {
		if (image == null) throw new NullPointerException();
		if (image instanceof javax.microedition.lcdui.Image) {
			javax.microedition.lcdui.Image img = (javax.microedition.lcdui.Image) image;
			this.format = format;
			this.width = img.getWidth();
			this.height = img.getHeight();
			this.mutable = img.isMutable();
			this.pixels = new int[width * height];
			img.getRGB(pixels, 0, width, 0, 0, width, height);
		} else {
			throw new IllegalArgumentException();
		}
	}

	public void set(int x, int y, int width, int height, byte[] image) {
		if (image == null) throw new NullPointerException();
		int bytesPerPixel = (format == RGBA) ? 4 : (format == RGB ? 3 : (format == LUMINANCE_ALPHA ? 2 : 1));
		int srcIdx = 0;
		for (int r = 0; r < height; r++) {
			int row = (y + r) * this.width + x;
			for (int c = 0; c < width; c++) {
				int argb = 0xFF000000;
				if (format == RGBA) {
					int red = image[srcIdx++] & 0xFF;
					int grn = image[srcIdx++] & 0xFF;
					int blu = image[srcIdx++] & 0xFF;
					int alp = image[srcIdx++] & 0xFF;
					argb = (alp << 24) | (red << 16) | (grn << 8) | blu;
				} else if (format == RGB) {
					int red = image[srcIdx++] & 0xFF;
					int grn = image[srcIdx++] & 0xFF;
					int blu = image[srcIdx++] & 0xFF;
					argb = 0xFF000000 | (red << 16) | (grn << 8) | blu;
				} else if (format == ALPHA) {
					int alp = image[srcIdx++] & 0xFF;
					argb = (alp << 24) | 0x00FFFFFF;
				} else if (format == LUMINANCE) {
					int lum = image[srcIdx++] & 0xFF;
					argb = 0xFF000000 | (lum << 16) | (lum << 8) | lum;
				} else if (format == LUMINANCE_ALPHA) {
					int lum = image[srcIdx++] & 0xFF;
					int alp = image[srcIdx++] & 0xFF;
					argb = (alp << 24) | (lum << 16) | (lum << 8) | lum;
				}
				pixels[row + c] = argb;
			}
		}
	}

	public int getFormat() { return format; }
	public int getWidth() { return width; }
	public int getHeight() { return height; }
	public boolean isMutable() { return mutable; }
	public int[] getPixels() { return pixels; }
}

