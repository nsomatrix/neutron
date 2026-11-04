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
}

