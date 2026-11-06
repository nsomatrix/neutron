package javax.microedition.lcdui.game;

import javax.microedition.lcdui.Image;
import org.junit.Test;
import static org.junit.Assert.*;

public class SpriteTest {

    private static class StubImage extends Image {
        private final int width;
        private final int height;

        StubImage(int width, int height) {
            this.width = width;
            this.height = height;
        }

        @Override
        public int getWidth() {
            return width;
        }

        @Override
        public int getHeight() {
            return height;
        }
    }

    @Test
    public void testSetTransformSwapsDimensionsOn90And270Degrees() {
        Image img = new StubImage(60, 80);
        Sprite sprite = new Sprite(img, 20, 40);

        assertEquals(20, sprite.getWidth());
        assertEquals(40, sprite.getHeight());

        // 90 deg rotation swaps width and height
        sprite.setTransform(Sprite.TRANS_ROT90);
        assertEquals(40, sprite.getWidth());
        assertEquals(20, sprite.getHeight());

        // 180 deg rotation keeps raw frame aspect ratio
        sprite.setTransform(Sprite.TRANS_ROT180);
        assertEquals(20, sprite.getWidth());
        assertEquals(40, sprite.getHeight());

        // 270 deg rotation swaps width and height
        sprite.setTransform(Sprite.TRANS_ROT270);
        assertEquals(40, sprite.getWidth());
        assertEquals(20, sprite.getHeight());

        // Mirror + 90 deg rotation swaps width and height
        sprite.setTransform(Sprite.TRANS_MIRROR_ROT90);
        assertEquals(40, sprite.getWidth());
        assertEquals(20, sprite.getHeight());

        // Mirror + 270 deg rotation swaps width and height
        sprite.setTransform(Sprite.TRANS_MIRROR_ROT270);
        assertEquals(40, sprite.getWidth());
        assertEquals(20, sprite.getHeight());

        // Mirror keeps width and height
        sprite.setTransform(Sprite.TRANS_MIRROR);
        assertEquals(20, sprite.getWidth());
        assertEquals(40, sprite.getHeight());

        // Return to TRANS_NONE
        sprite.setTransform(Sprite.TRANS_NONE);
        assertEquals(20, sprite.getWidth());
        assertEquals(40, sprite.getHeight());
    }

    @Test
    public void testSetImageResetsCollisionRectangleWhenFrameDimensionsChange() {
        Image img1 = new StubImage(40, 40);
        Sprite sprite = new Sprite(img1, 20, 20);
        sprite.setPosition(0, 0);

        // Define a restricted collision rectangle at (10, 10, 5, 5)
        sprite.defineCollisionRectangle(10, 10, 5, 5);

        // Another sprite placed at (2, 2) of size 4x4 does NOT collide with (10, 10, 5, 5)
        Image otherImg = new StubImage(4, 4);
        Sprite other = new Sprite(otherImg, 4, 4);
        other.setPosition(2, 2);

        assertFalse("Should not collide with restricted collision rect", sprite.collidesWith(other, false));

        // Change image with different frame dimensions (30x30)
        Image img2 = new StubImage(60, 60);
        sprite.setImage(img2, 30, 30);

        // Collision rectangle must be reset to full frame (0, 0, 30, 30), so (2, 2) now collides
        assertTrue("Should collide after collision rectangle reset to default", sprite.collidesWith(other, false));
    }

    @Test
    public void testSetImageOnRotatedSpriteUpdatesDimensionsProperly() {
        Image img1 = new StubImage(40, 80);
        Sprite sprite = new Sprite(img1, 20, 40);
        sprite.setTransform(Sprite.TRANS_ROT90);

        assertEquals(40, sprite.getWidth());
        assertEquals(20, sprite.getHeight());

        // New image has frame 30x50; when ROT90 is active, layer dimensions must be 50x30
        Image img2 = new StubImage(60, 100);
        sprite.setImage(img2, 30, 50);

        assertEquals(50, sprite.getWidth());
        assertEquals(30, sprite.getHeight());
    }

    @Test
    public void testReferencePixelCoordinatesWithTransforms() {
        // Raw frame: width=20, height=40
        Image img = new StubImage(20, 40);
        Sprite sprite = new Sprite(img, 20, 40);
        sprite.setPosition(100, 100);
        sprite.defineReferencePixel(5, 10);

        // TRANS_NONE: ref = (5, 10), world = (105, 110)
        assertEquals(105, sprite.getRefPixelX());
        assertEquals(110, sprite.getRefPixelY());

        // TRANS_MIRROR: W-1-refX = 19-5 = 14, Y = 10
        sprite.setTransform(Sprite.TRANS_MIRROR);
        // After setTransform, sprite moves so reference pixel in world coordinates is PRESERVED
        assertEquals(105, sprite.getRefPixelX());
        assertEquals(110, sprite.getRefPixelY());

        // Now move sprite explicitly
        sprite.setPosition(0, 0);
        // At (0, 0) with TRANS_MIRROR: refX' = 14, refY' = 10
        assertEquals(14, sprite.getRefPixelX());
        assertEquals(10, sprite.getRefPixelY());

        // TRANS_ROT90 at (0, 0): X' = H - 1 - refY = 40 - 1 - 10 = 29; Y' = refX = 5
        sprite.setTransform(Sprite.TRANS_ROT90);
        sprite.setPosition(0, 0);
        assertEquals(29, sprite.getRefPixelX());
        assertEquals(5, sprite.getRefPixelY());

        // TRANS_ROT180 at (0, 0): X' = W - 1 - refX = 14; Y' = H - 1 - refY = 29
        sprite.setTransform(Sprite.TRANS_ROT180);
        sprite.setPosition(0, 0);
        assertEquals(14, sprite.getRefPixelX());
        assertEquals(29, sprite.getRefPixelY());

        // TRANS_ROT270 at (0, 0): X' = refY = 10; Y' = W - 1 - refX = 14
        sprite.setTransform(Sprite.TRANS_ROT270);
        sprite.setPosition(0, 0);
        assertEquals(10, sprite.getRefPixelX());
        assertEquals(14, sprite.getRefPixelY());
    }

    @Test
    public void testSetRefPixelPositionPreservesTransformedLocation() {
        Image img = new StubImage(20, 40);
        Sprite sprite = new Sprite(img, 20, 40);
        sprite.defineReferencePixel(5, 10);
        sprite.setTransform(Sprite.TRANS_MIRROR_ROT90);

        // Setting reference pixel to world (200, 300) must ensure getRefPixelX/Y return (200, 300)
        sprite.setRefPixelPosition(200, 300);
        assertEquals(200, sprite.getRefPixelX());
        assertEquals(300, sprite.getRefPixelY());
    }

    @Test(expected = NullPointerException.class)
    public void testCollidesWithNullImageThrowsNullPointerException() {
        Image img = new StubImage(20, 20);
        Sprite sprite = new Sprite(img, 20, 20);
        sprite.collidesWith((Image) null, 0, 0, false);
    }
}
