package javax.microedition.lcdui.game;

import javax.microedition.lcdui.Image;
import org.junit.Test;
import static org.junit.Assert.*;

public class TiledLayerTest {

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
    public void testSetStaticTileSetReplacesImageAndDimensions() {
        Image img1 = new StubImage(64, 64);
        TiledLayer layer = new TiledLayer(4, 4, img1, 32, 32);
        assertSame(img1, layer.img);
        assertEquals(32, layer.getCellWidth());
        assertEquals(32, layer.getCellHeight());

        Image img2 = new StubImage(128, 64);
        layer.setStaticTileSet(img2, 16, 16);
        assertSame(img2, layer.img);
        assertEquals(16, layer.getCellWidth());
        assertEquals(16, layer.getCellHeight());
        assertEquals(4 * 16, layer.getWidth());
        assertEquals(4 * 16, layer.getHeight());
    }

    @Test
    public void testSetStaticTileSetNullChecksParameter() {
        Image img1 = new StubImage(64, 64);
        TiledLayer layer = new TiledLayer(4, 4, img1, 32, 32);
        try {
            layer.setStaticTileSet(null, 16, 16);
            fail("Expected NullPointerException when setting null static tile set");
        } catch (NullPointerException e) {
            // Expected
        }
    }

    @Test
    public void testSetStaticTileSetValidatesDimensionsAgainstNewImage() {
        Image img1 = new StubImage(64, 64);
        TiledLayer layer = new TiledLayer(4, 4, img1, 32, 32);

        // img2 is 50x50, not divisible by 16
        Image img2 = new StubImage(50, 50);
        try {
            layer.setStaticTileSet(img2, 16, 16);
            fail("Expected IllegalArgumentException when image dimensions not divisible by tile dimensions");
        } catch (IllegalArgumentException e) {
            // Expected
        }
    }

    @Test
    public void testSetStaticTileSetFewerTilesResetsAnimatedTiles() {
        Image img1 = new StubImage(64, 64);
        TiledLayer layer = new TiledLayer(2, 2, img1, 16, 16); // 16 static tiles
        int anim1 = layer.createAnimatedTile(1);
        layer.setCell(0, 0, anim1);
        assertEquals(anim1, layer.getCell(0, 0));

        // New image has only 4 static tiles (32x32 with 16x16 tiles = 4 tiles < 16)
        Image img2 = new StubImage(32, 32);
        layer.setStaticTileSet(img2, 16, 16);
        assertEquals(0, layer.numAnimatedTiles);
        assertEquals(0, layer.getCell(0, 0));
    }
}

