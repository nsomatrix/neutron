/*
 *  Neutron JSR-226 Scalable 2D Vector Graphics
 *
 *  Licensed under LGPL 2.1 or Apache 2.0.
 */

package javax.microedition.m2g;

public abstract class SVGAnimator {

	public static SVGAnimator createAnimator(final SVGImage svgImage) {
		return new SVGAnimator() {
			private int timeInc = 100;
			public Object getTargetComponent() { return null; }
			public void play() {}
			public void pause() {}
			public void stop() {}
			public int getTimeIncrement() { return timeInc; }
			public void setTimeIncrement(int timeIncrement) { this.timeInc = timeIncrement; }
		};
	}

	public static SVGAnimator createAnimator(final SVGImage svgImage, String component) {
		return createAnimator(svgImage);
	}

	public abstract Object getTargetComponent();
	public abstract void play();
	public abstract void pause();
	public abstract void stop();
	public abstract int getTimeIncrement();
	public abstract void setTimeIncrement(int timeIncrement);
}

