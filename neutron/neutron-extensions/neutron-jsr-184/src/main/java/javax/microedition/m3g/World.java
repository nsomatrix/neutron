package javax.microedition.m3g;

public class World extends Group {

	private Camera activeCamera = null;
	private Background background = null;

	public World() {}

	public void setActiveCamera(Camera camera) {
		this.activeCamera = camera;
	}

	public Camera getActiveCamera() {
		return activeCamera;
	}

	public void setBackground(Background background) {
		this.background = background;
	}

	public Background getBackground() {
		return background;
	}
}

