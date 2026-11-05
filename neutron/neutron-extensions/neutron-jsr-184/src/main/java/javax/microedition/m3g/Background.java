package javax.microedition.m3g;

public class Background extends Object3D {

	public static final int BORDER = 32;
	public static final int REPEAT = 33;

	private int color = 0x00000000;
	private Image2D image = null;
	private int imageModeX = BORDER;
	private int imageModeY = BORDER;
	private int cropX = 0;
	private int cropY = 0;
	private int cropWidth = 0;
	private int cropHeight = 0;
	private boolean colorClear = true;
	private boolean depthClear = true;

	public Background() {}

	public void setColor(int argb) {
		this.color = argb;
	}

	public int getColor() { return color; }

	public void setImage(Image2D image) {
		this.image = image;
		if (image != null) {
			this.cropWidth = image.getWidth();
			this.cropHeight = image.getHeight();
		}
	}

	public Image2D getImage() { return image; }

	public void setImageMode(int modeX, int modeY) {
		this.imageModeX = modeX;
		this.imageModeY = modeY;
	}

	public int getImageModeX() { return imageModeX; }
	public int getImageModeY() { return imageModeY; }

	public void setCrop(int cropX, int cropY, int width, int height) {
		this.cropX = cropX;
		this.cropY = cropY;
		this.cropWidth = width;
		this.cropHeight = height;
	}

	public int getCropX() { return cropX; }
	public int getCropY() { return cropY; }
	public int getCropWidth() { return cropWidth; }
	public int getCropHeight() { return cropHeight; }

	public void setColorClearEnable(boolean enable) { this.colorClear = enable; }
	public boolean isColorClearEnabled() { return colorClear; }

	public void setDepthClearEnable(boolean enable) { this.depthClear = enable; }
	public boolean isDepthClearEnabled() { return depthClear; }
}

