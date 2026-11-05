package org.neutron.app.ui.filter;

import org.junit.Test;
import static org.junit.Assert.*;

public class XBRZTest {

	@Test
	public void testScale2xDimensionsAndExecution() {
		int w = 4;
		int h = 4;
		int[] src = new int[w * h];
		for (int i = 0; i < src.length; i++) {
			src[i] = (i % 2 == 0) ? 0xFFFF0000 : 0xFF00FF00;
		}

		int factor = 2;
		int[] dst = new int[(w * factor) * (h * factor)];
		XBRZ.scale(factor, src, dst, w, h);

		assertEquals((w * factor) * (h * factor), dst.length);
		assertNotEquals(0, dst[0]);
	}

	@Test
	public void testScale3xAnd4x() {
		int w = 2;
		int h = 2;
		int[] src = new int[] {
			0xFFFFFFFF, 0xFF000000,
			0xFF000000, 0xFFFFFFFF
		};

		int[] dst3x = new int[(w * 3) * (h * 3)];
		XBRZ.scale(3, src, dst3x, w, h);
		assertEquals(36, dst3x.length);

		int[] dst4x = new int[(w * 4) * (h * 4)];
		XBRZ.scale(4, src, dst4x, w, h);
		assertEquals(64, dst4x.length);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testScaleNullSrcThrows() {
		XBRZ.scale(2, null, new int[16], 2, 2);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testScaleInvalidFactorThrows() {
		XBRZ.scale(1, new int[4], new int[4], 2, 2);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testScaleBufferTooSmallThrows() {
		XBRZ.scale(2, new int[4], new int[8], 2, 2);
	}
}

