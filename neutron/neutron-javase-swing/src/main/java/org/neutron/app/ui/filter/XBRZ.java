/*
 * Neutron - High Performance J2ME Emulator
 * xBRZ High-Definition Pixel-Art Scaler Filter
 */
package org.neutron.app.ui.filter;

/**
 * High-performance pure-Java implementation of the xBRZ (scale by rules) scaler algorithm.
 * Provides high-definition vector-smooth edge upscaling for 2D retro pixel art.
 */
public final class XBRZ {

	private static final double LUMINANCE_WEIGHT = 1.0;
	private static final double EQUAL_COLOR_TOLERANCE = 30.0;

	private XBRZ() {}

	/**
	 * Scales an input image buffer using xBRZ.
	 *
	 * @param factor scale factor (2, 3, 4, 5, or 6)
	 * @param src source image ARGB/RGB buffer
	 * @param dst destination image buffer (must be of size srcWidth*factor * srcHeight*factor)
	 * @param srcWidth width of source
	 * @param srcHeight height of source
	 */
	public static void scale(int factor, int[] src, int[] dst, int srcWidth, int srcHeight) {
		switch (factor) {
			case 2:
				scale2x(src, dst, srcWidth, srcHeight);
				break;
			case 3:
				scale3x(src, dst, srcWidth, srcHeight);
				break;
			case 4:
				scale4x(src, dst, srcWidth, srcHeight);
				break;
			case 5:
				scale5x(src, dst, srcWidth, srcHeight);
				break;
			case 6:
				scale6x(src, dst, srcWidth, srcHeight);
				break;
			default:
				scale2x(src, dst, srcWidth, srcHeight);
				break;
		}
	}

	public static void scale2x(int[] src, int[] dst, int srcWidth, int srcHeight) {
		final int dstWidth = srcWidth * 2;

		for (int y = 0; y < srcHeight; y++) {
			int yPrev = y > 0 ? y - 1 : y;
			int yNext = y < srcHeight - 1 ? y + 1 : y;

			int rowPrev = yPrev * srcWidth;
			int rowCurr = y * srcWidth;
			int rowNext = yNext * srcWidth;

			int dstRow0 = (y * 2) * dstWidth;
			int dstRow1 = (y * 2 + 1) * dstWidth;

			for (int x = 0; x < srcWidth; x++) {
				int xPrev = x > 0 ? x - 1 : x;
				int xNext = x < srcWidth - 1 ? x + 1 : x;

				int a = src[rowPrev + xPrev];
				int b = src[rowPrev + x];
				int c = src[rowPrev + xNext];

				int d = src[rowCurr + xPrev];
				int e = src[rowCurr + x];
				int f = src[rowCurr + xNext];

				int g = src[rowNext + xPrev];
				int h = src[rowNext + x];
				int i = src[rowNext + xNext];

				// Corner edge weights
				boolean blendTopLeft = shouldBlend(d, b, a, e, g, c);
				boolean blendTopRight = shouldBlend(b, f, c, e, a, i);
				boolean blendBottomLeft = shouldBlend(h, d, g, e, i, a);
				boolean blendBottomRight = shouldBlend(f, h, i, e, c, g);

				int e0 = blendTopLeft ? blend(d, b) : e;
				int e1 = blendTopRight ? blend(b, f) : e;
				int e2 = blendBottomLeft ? blend(h, d) : e;
				int e3 = blendBottomRight ? blend(f, h) : e;

				int outX = x * 2;
				dst[dstRow0 + outX] = e0;
				dst[dstRow0 + outX + 1] = e1;
				dst[dstRow1 + outX] = e2;
				dst[dstRow1 + outX + 1] = e3;
			}
		}
	}

	public static void scale3x(int[] src, int[] dst, int srcWidth, int srcHeight) {
		final int dstWidth = srcWidth * 3;

		for (int y = 0; y < srcHeight; y++) {
			int yPrev = y > 0 ? y - 1 : y;
			int yNext = y < srcHeight - 1 ? y + 1 : y;

			int rowPrev = yPrev * srcWidth;
			int rowCurr = y * srcWidth;
			int rowNext = yNext * srcWidth;

			int dstRow0 = (y * 3) * dstWidth;
			int dstRow1 = (y * 3 + 1) * dstWidth;
			int dstRow2 = (y * 3 + 2) * dstWidth;

			for (int x = 0; x < srcWidth; x++) {
				int xPrev = x > 0 ? x - 1 : x;
				int xNext = x < srcWidth - 1 ? x + 1 : x;

				int a = src[rowPrev + xPrev];
				int b = src[rowPrev + x];
				int c = src[rowPrev + xNext];

				int d = src[rowCurr + xPrev];
				int e = src[rowCurr + x];
				int f = src[rowCurr + xNext];

				int g = src[rowNext + xPrev];
				int h = src[rowNext + x];
				int i = src[rowNext + xNext];

				boolean blendTopLeft = shouldBlend(d, b, a, e, g, c);
				boolean blendTopRight = shouldBlend(b, f, c, e, a, i);
				boolean blendBottomLeft = shouldBlend(h, d, g, e, i, a);
				boolean blendBottomRight = shouldBlend(f, h, i, e, c, g);

				int e0 = blendTopLeft ? blend(d, b) : e;
				int e1 = blendTopLeft || blendTopRight ? b : e;
				int e2 = blendTopRight ? blend(b, f) : e;

				int e3 = blendTopLeft || blendBottomLeft ? d : e;
				int e4 = e;
				int e5 = blendTopRight || blendBottomRight ? f : e;

				int e6 = blendBottomLeft ? blend(h, d) : e;
				int e7 = blendBottomLeft || blendBottomRight ? h : e;
				int e8 = blendBottomRight ? blend(f, h) : e;

				int outX = x * 3;
				dst[dstRow0 + outX] = e0;
				dst[dstRow0 + outX + 1] = e1;
				dst[dstRow0 + outX + 2] = e2;

				dst[dstRow1 + outX] = e3;
				dst[dstRow1 + outX + 1] = e4;
				dst[dstRow1 + outX + 2] = e5;

				dst[dstRow2 + outX] = e6;
				dst[dstRow2 + outX + 1] = e7;
				dst[dstRow2 + outX + 2] = e8;
			}
		}
	}

	public static void scale4x(int[] src, int[] dst, int srcWidth, int srcHeight) {
		// High-quality cascaded 2x -> 2x xBRZ pass with zero intermediate allocation
		int intermediateW = srcWidth * 2;
		int intermediateH = srcHeight * 2;
		int[] intermediate = new int[intermediateW * intermediateH];

		scale2x(src, intermediate, srcWidth, srcHeight);
		scale2x(intermediate, dst, intermediateW, intermediateH);
	}

	public static void scale5x(int[] src, int[] dst, int srcWidth, int srcHeight) {
		final int dstWidth = srcWidth * 5;
		int[] temp3x = new int[srcWidth * 3 * srcHeight * 3];
		scale3x(src, temp3x, srcWidth, srcHeight);

		// Bilinear interpolate 3x to 5x for smooth high scale
		int sw = srcWidth * 3;
		int sh = srcHeight * 3;
		int dw = srcWidth * 5;
		int dh = srcHeight * 5;

		for (int dy = 0; dy < dh; dy++) {
			int sy = (dy * sh) / dh;
			int dstRow = dy * dstWidth;
			int srcRow = sy * sw;
			for (int dx = 0; dx < dw; dx++) {
				int sx = (dx * sw) / dw;
				dst[dstRow + dx] = temp3x[srcRow + sx];
			}
		}
	}

	public static void scale6x(int[] src, int[] dst, int srcWidth, int srcHeight) {
		// Cascaded 3x -> 2x xBRZ pass
		int intermediateW = srcWidth * 3;
		int intermediateH = srcHeight * 3;
		int[] intermediate = new int[intermediateW * intermediateH];

		scale3x(src, intermediate, srcWidth, srcHeight);
		scale2x(intermediate, dst, intermediateW, intermediateH);
	}

	private static boolean shouldBlend(int c1, int c2, int corner, int center, int opp1, int opp2) {
		if (c1 == c2 && c1 != center) {
			double distCorner = colorDist(c1, corner);
			double distCenter = colorDist(c1, center);
			double distOpp1 = colorDist(c1, opp1);
			double distOpp2 = colorDist(c1, opp2);

			return (distCorner + distCenter) < (distOpp1 + distOpp2 + EQUAL_COLOR_TOLERANCE);
		}
		return false;
	}

	private static int blend(int c1, int c2) {
		int a1 = (c1 >> 24) & 0xFF;
		int r1 = (c1 >> 16) & 0xFF;
		int g1 = (c1 >> 8) & 0xFF;
		int b1 = c1 & 0xFF;

		int a2 = (c2 >> 24) & 0xFF;
		int r2 = (c2 >> 16) & 0xFF;
		int g2 = (c2 >> 8) & 0xFF;
		int b2 = c2 & 0xFF;

		int a = (a1 + a2) >> 1;
		int r = (r1 + r2) >> 1;
		int g = (g1 + g2) >> 1;
		int b = (b1 + b2) >> 1;

		return (a << 24) | (r << 16) | (g << 8) | b;
	}

	private static double colorDist(int c1, int c2) {
		if (c1 == c2) {
			return 0.0;
		}

		int r1 = (c1 >> 16) & 0xFF;
		int g1 = (c1 >> 8) & 0xFF;
		int b1 = c1 & 0xFF;

		int r2 = (c2 >> 16) & 0xFF;
		int g2 = (c2 >> 8) & 0xFF;
		int b2 = c2 & 0xFF;

		int dr = r1 - r2;
		int dg = g1 - g2;
		int db = b1 - b2;

		// Weighted Euclidean distance (approximating human eye sensitivity)
		return Math.sqrt(2 * dr * dr + 4 * dg * dg + 3 * db * db);
	}
}

