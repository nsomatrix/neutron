/*
 *  Neutron JSR-226 Scalable 2D Vector Graphics
 *
 *  Licensed under LGPL 2.1 or Apache 2.0.
 */

package javax.microedition.m2g;

import org.w3c.dom.Document;

public class SVGImage extends ScalableImage {

	private SVGEventListener eventListener;
	private Document document;

	public static SVGImage createEmptyImage(SVGEventListener handler) {
		SVGImage image = new SVGImage();
		image.eventListener = handler;
		return image;
	}

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document doc) {
		this.document = doc;
	}
}

