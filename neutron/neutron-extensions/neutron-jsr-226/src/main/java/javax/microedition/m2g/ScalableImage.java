/*
 *  Neutron JSR-226 Scalable 2D Vector Graphics
 *
 *  Licensed under LGPL 2.1 or Apache 2.0.
 */

package javax.microedition.m2g;

import java.io.IOException;
import java.io.InputStream;

public abstract class ScalableImage {

	protected int viewportWidth = 240;
	protected int viewportHeight = 320;

	public static ScalableImage createImage(InputStream stream, SVGEventListener handler) throws IOException {
		SVGImage image = SVGImage.createEmptyImage(handler);
		if (handler != null) {
			handler.sizeChanged(image.getViewportWidth(), image.getViewportHeight());
		}
		return image;
	}

	public static ScalableImage createImage(String locator, SVGEventListener handler) throws IOException {
		SVGImage image = SVGImage.createEmptyImage(handler);
		if (handler != null) {
			handler.sizeChanged(image.getViewportWidth(), image.getViewportHeight());
		}
		return image;
	}

	public void setViewportWidth(int width) {
		this.viewportWidth = width;
	}

	public void setViewportHeight(int height) {
		this.viewportHeight = height;
	}

	public int getViewportWidth() {
		return viewportWidth;
	}

	public int getViewportHeight() {
		return viewportHeight;
	}

	public void requestCompleted(ScalableImage image) {
	}
}

