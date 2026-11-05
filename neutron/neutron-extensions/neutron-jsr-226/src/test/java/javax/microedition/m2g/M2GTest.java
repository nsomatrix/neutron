package javax.microedition.m2g;

import java.io.ByteArrayInputStream;
import org.junit.Test;
import static org.junit.Assert.*;

public class M2GTest {

    @Test
    public void testScalableImageAndSVGImage() throws Exception {
        final boolean[] sizeChangedCalled = new boolean[] { false };
        SVGEventListener listener = new SVGEventListener() {
            public void sizeChanged(int width, int height) {
                sizeChangedCalled[0] = true;
            }
        };

        ScalableImage img = ScalableImage.createImage(new ByteArrayInputStream(new byte[0]), listener);
        assertNotNull(img);
        assertTrue(sizeChangedCalled[0]);
        assertEquals(240, img.getViewportWidth());
        assertEquals(320, img.getViewportHeight());

        img.setViewportWidth(480);
        img.setViewportHeight(640);
        assertEquals(480, img.getViewportWidth());
        assertEquals(640, img.getViewportHeight());

        ScalableGraphics g = new ScalableGraphics();
        g.bindTarget(new Object());
        g.setTransparency(0.8f);
        assertEquals(0.8f, g.getTransparency(), 0.001f);
        g.setRenderingQuality(ScalableGraphics.RENDERING_QUALITY_HIGH);
        assertEquals(ScalableGraphics.RENDERING_QUALITY_HIGH, g.getRenderingQuality());
        g.render(0, 0, img);
        g.releaseTarget();

        SVGAnimator animator = SVGAnimator.createAnimator((SVGImage) img);
        assertNotNull(animator);
        animator.play();
        animator.pause();
        animator.stop();
    }
}

