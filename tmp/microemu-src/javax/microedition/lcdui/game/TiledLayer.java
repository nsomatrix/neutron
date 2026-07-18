/*
 * Decompiled with CFR 0.152.
 */
package javax.microedition.lcdui.game;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Layer;

public class TiledLayer
extends Layer {
    private final int rows;
    private final int cols;
    Image img;
    private int tileHeight;
    private int tileWidth;
    private int numStaticTiles;
    private int[][] tiles;
    int[] animatedTiles;
    int numAnimatedTiles;

    public TiledLayer(int cols, int rows, Image img, int tileWidth, int tileHeight) {
        super(0, 0, cols * tileWidth, rows * tileHeight, true);
        if (img == null) {
            throw new NullPointerException();
        }
        if (cols <= 0 || rows <= 0 || tileHeight <= 0 || tileWidth <= 0) {
            throw new IllegalArgumentException();
        }
        if (img.getWidth() % tileWidth != 0 || img.getHeight() % tileHeight != 0) {
            throw new IllegalArgumentException();
        }
        this.img = img;
        this.cols = cols;
        this.rows = rows;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.numStaticTiles = img.getWidth() / tileWidth * (img.getHeight() / tileHeight);
        this.tiles = new int[rows][cols];
        this.animatedTiles = new int[5];
        this.numAnimatedTiles = 0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int createAnimatedTile(int staticTileIndex) {
        TiledLayer tiledLayer = this;
        synchronized (tiledLayer) {
            if (staticTileIndex < 0 || staticTileIndex > this.numStaticTiles) {
                throw new IndexOutOfBoundsException();
            }
            if (this.numAnimatedTiles == this.animatedTiles.length) {
                int[] temp = new int[this.numAnimatedTiles + 6];
                System.arraycopy(this.animatedTiles, 0, temp, 0, this.numAnimatedTiles);
                this.animatedTiles = temp;
            }
            this.animatedTiles[this.numAnimatedTiles] = staticTileIndex;
            ++this.numAnimatedTiles;
            return -this.numAnimatedTiles;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int getAnimatedTile(int index) {
        TiledLayer tiledLayer = this;
        synchronized (tiledLayer) {
            index = -index - 1;
            if (index < 0 || index >= this.numAnimatedTiles) {
                throw new IndexOutOfBoundsException();
            }
            return this.animatedTiles[index];
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setAnimatedTile(int index, int staticTileIndex) {
        TiledLayer tiledLayer = this;
        synchronized (tiledLayer) {
            index = -index - 1;
            if (index < 0 || index >= this.numAnimatedTiles) {
                throw new IndexOutOfBoundsException();
            }
            if (staticTileIndex < 0 || staticTileIndex > this.numStaticTiles) {
                throw new IndexOutOfBoundsException();
            }
            this.animatedTiles[index] = staticTileIndex;
        }
    }

    public int getCell(int col, int row) {
        return this.tiles[row][col];
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setCell(int col, int row, int index) {
        TiledLayer tiledLayer = this;
        synchronized (tiledLayer) {
            if (-index - 1 >= this.numAnimatedTiles || index > this.numStaticTiles) {
                throw new IndexOutOfBoundsException();
            }
            this.tiles[row][col] = index;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setStaticTileSet(Image image, int tileWidth, int tileHeight) {
        TiledLayer tiledLayer = this;
        synchronized (tiledLayer) {
            if (this.img == null) {
                throw new NullPointerException();
            }
            if (tileHeight <= 0 || tileWidth <= 0) {
                throw new IllegalArgumentException();
            }
            if (this.img.getWidth() % tileWidth != 0 || this.img.getHeight() % tileHeight != 0) {
                throw new IllegalArgumentException();
            }
            int newNumStaticTiles = this.img.getWidth() / this.getCellWidth() * (this.img.getHeight() / this.getCellHeight());
            int w = this.cols * tileWidth;
            int h = this.rows * tileHeight;
            this.setSize(w, h);
            this.img = this.img;
            this.tileWidth = tileWidth;
            this.tileHeight = tileHeight;
            if (newNumStaticTiles >= this.numStaticTiles) {
                this.numStaticTiles = newNumStaticTiles;
                return;
            }
            this.numStaticTiles = newNumStaticTiles;
            this.animatedTiles = new int[5];
            this.numAnimatedTiles = 0;
            this.fillCells(0, 0, this.getColumns(), this.getRows(), 0);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void fillCells(int col, int row, int numCols, int numRows, int index) {
        TiledLayer tiledLayer = this;
        synchronized (tiledLayer) {
            if (numCols < 0 || numRows < 0) {
                throw new IllegalArgumentException();
            }
            if (row < 0 || col < 0 || col + numCols > this.cols || row + numRows > this.rows) {
                throw new IndexOutOfBoundsException();
            }
            if (-index - 1 >= this.numAnimatedTiles || index > this.numStaticTiles) {
                throw new IndexOutOfBoundsException();
            }
            int rMax = row + numRows;
            int cMax = col + numCols;
            for (int r = row; r < rMax; ++r) {
                for (int c = col; c < cMax; ++c) {
                    this.tiles[r][c] = index;
                }
            }
        }
    }

    public final int getColumns() {
        return this.cols;
    }

    public final int getRows() {
        return this.rows;
    }

    public final int getCellWidth() {
        return this.tileWidth;
    }

    public final int getCellHeight() {
        return this.tileHeight;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void paint(Graphics g) {
        TiledLayer tiledLayer = this;
        synchronized (tiledLayer) {
            if (!this.isVisible()) {
                return;
            }
            int x = this.getX();
            int y = this.getY();
            int c0 = 0;
            int r0 = 0;
            int cMax = this.getColumns();
            int rMax = this.getRows();
            int tW = this.getCellWidth();
            int tH = this.getCellHeight();
            int cX = g.getClipX();
            int cY = g.getClipY();
            int cW = g.getClipWidth();
            int cH = g.getClipHeight();
            int x0 = x;
            int anchor = 20;
            int imgCols = this.img.getWidth() / tW;
            int imgRows = this.img.getHeight() / tH;
            int r = r0;
            while (r < rMax) {
                x = x0;
                int c = c0;
                while (c < cMax) {
                    int tile = this.getCell(c, r);
                    if (tile < 0) {
                        tile = this.getAnimatedTile(tile);
                    }
                    if (tile != 0) {
                        int xSrc = tW * (--tile % imgCols);
                        int ySrc = tile / imgCols * tH;
                        g.drawRegion(this.img, xSrc, ySrc, tW, tH, 0, x, y, anchor);
                    }
                    ++c;
                    x += tW;
                }
                ++r;
                y += tH;
            }
        }
    }
}

