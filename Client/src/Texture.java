public final class Texture extends DrawingArea {

	public static void nullLoader()
	{
		anIntArray1468 = null;
		anIntArray1468 = null;
		anIntArray1470 = null;
		anIntArray1471 = null;
		anIntArray1472 = null;
		aBackgroundArray1474s = null;
		aBooleanArray1475 = null;
		anIntArray1476 = null;
		anIntArrayArray1478 = null;
		anIntArrayArray1479 = null;
		anIntArray1480 = null;
		anIntArray1482 = null;
		anIntArrayArray1483 = null;
	}
	
	public static void method364()
	{
		anIntArray1472 = new int[DrawingArea.height];
		for(int j = 0; j < DrawingArea.height; j++)
			anIntArray1472[j] = DrawingArea.width * j;

		textureInt1 = DrawingArea.width / 2;
		textureInt2 = DrawingArea.height / 2;
	}

	public static void method365(int j, int k)
	{
	   anIntArray1472 = new int[k];
		for(int l = 0; l < k; l++)
			anIntArray1472[l] = j * l;

		textureInt1 = j / 2;
		textureInt2 = k / 2;
	}

	public static void method366()
	{
		anIntArrayArray1478 = null;
		for(int j = 0; j < 50; j++)
			anIntArrayArray1479[j] = null;

	}

	public static void method367()
	{
		if(anIntArrayArray1478 == null)
		{
			anInt1477 = 20;
			if(lowMem)
				anIntArrayArray1478 = new int[anInt1477][16384];
			else
				anIntArrayArray1478 = new int[anInt1477][0x10000];
			for(int k = 0; k < 50; k++)
				anIntArrayArray1479[k] = null;

		}
	}

	public static void method368(StreamLoader streamLoader)
	{
		anInt1473 = 0;
		for(int j = 0; j < 51; j++)
			try
			{
				aBackgroundArray1474s[j] = new Background(streamLoader, String.valueOf(j), 0);
				if(lowMem && aBackgroundArray1474s[j].anInt1456 == 128)
					aBackgroundArray1474s[j].method356();
				else
					aBackgroundArray1474s[j].method357();
				anInt1473++;
			}
			catch(Exception _ex) { }

	}

	public static int method369(int textureId)
	{
		if(anIntArray1476[textureId] != 0)
			return anIntArray1476[textureId];
		int r = 0;
		int g = 0;
		int b = 0;
		int colourCount = anIntArrayArray1483[textureId].length;
		for(int ptr = 0; ptr < colourCount; ptr++)
		{
			r += anIntArrayArray1483[textureId][ptr] >> 16 & 0xff;
			g += anIntArrayArray1483[textureId][ptr] >> 8 & 0xff;
			b += anIntArrayArray1483[textureId][ptr] & 0xff;
		}

		int rgb = (r / colourCount << 16) + (g / colourCount << 8) + b / colourCount;
		rgb = adjustBrightness(rgb, 1.3999999999999999D);
		if(rgb == 0)
			rgb = 1;
		anIntArray1476[textureId] = rgb;
		return rgb;
	}

	public static void method370(int textureId) {
		try {
		if(anIntArrayArray1479[textureId] == null) {
			return;
		}
		anIntArrayArray1478[anInt1477++] = anIntArrayArray1479[textureId];
		anIntArrayArray1479[textureId] = null;
		} catch(Exception e) {
			
		}
	}

	private static int[] getTexturePixels(int textureId)
	{
		anIntArray1480[textureId] = anInt1481++;
		if(anIntArrayArray1479[textureId] != null)
			return anIntArrayArray1479[textureId];
		int texels[];
		if(anInt1477 > 0)
		{
			texels = anIntArrayArray1478[--anInt1477];
			anIntArrayArray1478[anInt1477] = null;
		} else
		{
			int lastUsed = 0;
			int target = -1;
			for(int l = 0; l < anInt1473; l++)
				if(anIntArrayArray1479[l] != null && (anIntArray1480[l] < lastUsed || target == -1))
				{
					lastUsed = anIntArray1480[l];
					target = l;
				}

			texels = anIntArrayArray1479[target];
			anIntArrayArray1479[target] = null;
		}
		anIntArrayArray1479[textureId] = texels;
		Background background = aBackgroundArray1474s[textureId];
		int texturePalette[] = anIntArrayArray1483[textureId];
		if(lowMem)
		{
			aBooleanArray1475[textureId] = false;
			for(int i1 = 0; i1 < 4096; i1++)
			{
				int i2 = texels[i1] = texturePalette[background.aByteArray1450[i1]] & 0xf8f8ff;
				if(i2 == 0)
					aBooleanArray1475[textureId] = true;
				texels[4096 + i1] = i2 - (i2 >>> 3) & 0xf8f8ff;
				texels[8192 + i1] = i2 - (i2 >>> 2) & 0xf8f8ff;
				texels[12288 + i1] = i2 - (i2 >>> 2) - (i2 >>> 3) & 0xf8f8ff;
			}

		} else
		{
			if(background.anInt1452 == 64)
			{
				for(int j1 = 0; j1 < 128; j1++)
				{
					for(int j2 = 0; j2 < 128; j2++)
						texels[j2 + (j1 << 7)] = texturePalette[background.aByteArray1450[(j2 >> 1) + ((j1 >> 1) << 6)]];

				}

			} else
			{
				for(int k1 = 0; k1 < 16384; k1++)
					texels[k1] = texturePalette[background.aByteArray1450[k1]];

			}
			aBooleanArray1475[textureId] = false;
			for(int l1 = 0; l1 < 16384; l1++)
			{
				texels[l1] &= 0xf8f8ff;
				int k2 = texels[l1];
				if(k2 == 0)
					aBooleanArray1475[textureId] = true;
				texels[16384 + l1] = k2 - (k2 >>> 3) & 0xf8f8ff;
				texels[32768 + l1] = k2 - (k2 >>> 2) & 0xf8f8ff;
				texels[49152 + l1] = k2 - (k2 >>> 2) - (k2 >>> 3) & 0xf8f8ff;
			}

		}
		return texels;
	}

	public static void method372(double brightness)
	{
		int j = 0;
		for(int k = 0; k < 512; k++)
		{
			double d1 = (double)(k / 8) / 64D + 0.0078125D;
			double d2 = (double)(k & 7) / 8D + 0.0625D;
			for(int k1 = 0; k1 < 128; k1++)
			{
				double d3 = (double)k1 / 128D;
				double r = d3;
				double g = d3;
				double b = d3;
				if(d2 != 0.0D)
				{
					double d7;
					if(d3 < 0.5D)
						d7 = d3 * (1.0D + d2);
					else
						d7 = (d3 + d2) - d3 * d2;
					double d8 = 2D * d3 - d7;
					double d9 = d1 + 0.33333333333333331D;
					if(d9 > 1.0D)
						d9--;
					double d10 = d1;
					double d11 = d1 - 0.33333333333333331D;
					if(d11 < 0.0D)
						d11++;
					if(6D * d9 < 1.0D)
						r = d8 + (d7 - d8) * 6D * d9;
					else
					if(2D * d9 < 1.0D)
						r = d7;
					else
					if(3D * d9 < 2D)
						r = d8 + (d7 - d8) * (0.66666666666666663D - d9) * 6D;
					else
						r = d8;
					if(6D * d10 < 1.0D)
						g = d8 + (d7 - d8) * 6D * d10;
					else
					if(2D * d10 < 1.0D)
						g = d7;
					else
					if(3D * d10 < 2D)
						g = d8 + (d7 - d8) * (0.66666666666666663D - d10) * 6D;
					else
						g = d8;
					if(6D * d11 < 1.0D)
						b = d8 + (d7 - d8) * 6D * d11;
					else
					if(2D * d11 < 1.0D)
						b = d7;
					else
					if(3D * d11 < 2D)
						b = d8 + (d7 - d8) * (0.66666666666666663D - d11) * 6D;
					else
						b = d8;
				}
				int byteR = (int)(r * 256D);
				int byteG = (int)(g * 256D);
				int byteB = (int)(b * 256D);
				int rgb = (byteR << 16) + (byteG << 8) + byteB;
				rgb = adjustBrightness(rgb, brightness);
				if(rgb == 0)
					rgb = 1;
				anIntArray1482[j++] = rgb;
			}

		}

		for(int textureId = 0; textureId < 51; textureId++)
			if(aBackgroundArray1474s[textureId] != null)
			{
				int palette[] = aBackgroundArray1474s[textureId].anIntArray1451;
				anIntArrayArray1483[textureId] = new int[palette.length];
				for(int colourId = 0; colourId < palette.length; colourId++)
				{
					anIntArrayArray1483[textureId][colourId] = adjustBrightness(palette[colourId], brightness);
					if((anIntArrayArray1483[textureId][colourId] & 0xf8f8ff) == 0 && colourId != 0)
						anIntArrayArray1483[textureId][colourId] = 1;
				}

			}

		for(int textureId = 0; textureId < 51; textureId++)
			method370(textureId);

	}

	private static int adjustBrightness(int rgb, double intensity) {
        double r = (double) (rgb >> 16) / 256D;
        double g = (double) (rgb >> 8 & 0xff) / 256D;
        double b = (double) (rgb & 0xff) / 256D;
        r = Math.pow(r, intensity);
        g = Math.pow(g, intensity);
        b = Math.pow(b, intensity);
        int r_byte = (int) (r * 256D);
        int g_byte = (int) (g * 256D);
        int b_byte = (int) (b * 256D);
        return (r_byte << 16) + (g_byte << 8) + b_byte;
	}

	public static void drawShadedTriangle(int y_a, int y_b, int y_c, int x_a, int x_b, int x_c, int hsl1, int hsl2, int hsl3, float z_a, float z_b, float z_c) {
		if (z_a < 0 || z_b < 0 || z_c < 0) 
			return;
		int rgb1 = anIntArray1482[hsl1];
		int rgb2 = anIntArray1482[hsl2];
		int rgb3 = anIntArray1482[hsl3];
		int r1 = rgb1 >> 16 & 0xff;
		int g1 = rgb1 >> 8 & 0xff;
		int b1 = rgb1 & 0xff;
		int r2 = rgb2 >> 16 & 0xff;
		int g2 = rgb2 >> 8 & 0xff;
		int b2 = rgb2 & 0xff;
		int r3 = rgb3 >> 16 & 0xff;
		int g3 = rgb3 >> 8 & 0xff;
		int b3 = rgb3 & 0xff;
		int a_to_b = 0;
		int dr1 = 0;
		int dg1 = 0;
		int db1 = 0;
		if (y_b != y_a) {
			a_to_b = (x_b - x_a << 16) / (y_b - y_a);
			dr1 = (r2 - r1 << 16) / (y_b - y_a);
			dg1 = (g2 - g1 << 16) / (y_b - y_a);
			db1 = (b2 - b1 << 16) / (y_b - y_a);
		}
		int b_to_c = 0;
		int dr2 = 0;
		int dg2 = 0;
		int db2 = 0;
		if (y_c != y_b) {
			b_to_c = (x_c - x_b << 16) / (y_c - y_b);
			dr2 = (r3 - r2 << 16) / (y_c - y_b);
			dg2 = (g3 - g2 << 16) / (y_c - y_b);
			db2 = (b3 - b2 << 16) / (y_c - y_b);
		}
		int c_to_a = 0;
		int dr3 = 0;
		int dg3 = 0;
		int db3 = 0;
		if (y_c != y_a) {
			c_to_a = (x_a - x_c << 16) / (y_a - y_c);
			dr3 = (r1 - r3 << 16) / (y_a - y_c);
			dg3 = (g1 - g3 << 16) / (y_a - y_c);
			db3 = (b1 - b3 << 16) / (y_a - y_c);
		}
		float b_aX = x_b - x_a;
		float b_aY = y_b - y_a;
		float c_aX = x_c - x_a;
		float c_aY = y_c - y_a;
		float b_aZ = z_b - z_a;
		float c_aZ = z_c - z_a;

		float div = b_aX * c_aY - c_aX * b_aY;
		float depth_slope = (b_aZ * c_aY - c_aZ * b_aY) / div;
		float depth_increment = (c_aZ * b_aX - b_aZ * c_aX) / div;
		if(y_a <= y_b && y_a <= y_c) {
			if(y_a >= DrawingArea.bottomY) {
				return;
			}
			if(y_b > DrawingArea.bottomY) {
				y_b = DrawingArea.bottomY;
			}
			if(y_c > DrawingArea.bottomY) {
				y_c = DrawingArea.bottomY;
			}
			z_a = z_a - depth_slope * x_a + depth_slope;
			if(y_b < y_c) {
				x_c = x_a <<= 16;
				r3 = r1 <<= 16;
				g3 = g1 <<= 16;
				b3 = b1 <<= 16;
				if(y_a < 0) {
					x_c -= c_to_a * y_a;
					x_a -= a_to_b * y_a;
					r3 -= dr3 * y_a;
					g3 -= dg3 * y_a;
					b3 -= db3 * y_a;
					r1 -= dr1 * y_a;
					g1 -= dg1 * y_a;
					b1 -= db1 * y_a;
					z_a -= depth_increment * y_a;
					y_a = 0;
				}
				x_b <<= 16;
				r2 <<= 16;
				g2 <<= 16;
				b2 <<= 16;
				if(y_b < 0) {
					x_b -= b_to_c * y_b;
					r2 -= dr2 * y_b;
					g2 -= dg2 * y_b;
					b2 -= db2 * y_b;
					y_b = 0;
				}
				if(y_a != y_b && c_to_a < a_to_b || y_a == y_b && c_to_a > b_to_c) {
					y_c -= y_b;
					y_b -= y_a;
					for(y_a = anIntArray1472[y_a]; --y_b >= 0; y_a += DrawingArea.width) {
						drawShadedScanline(DrawingArea.pixels, y_a, x_c >> 16, x_a >> 16, r3, g3, b3, r1, g1, b1, z_a, depth_slope);
						x_c += c_to_a;
						x_a += a_to_b;
						r3 += dr3;
						g3 += dg3;
						b3 += db3;
						r1 += dr1;
						g1 += dg1;
						b1 += db1;
						z_a += depth_increment;
					}
					while(--y_c >= 0) {
						drawShadedScanline(DrawingArea.pixels, y_a, x_c >> 16, x_b >> 16, r3, g3, b3, r2, g2, b2, z_a, depth_slope);
						x_c += c_to_a;
						x_b += b_to_c;
						r3 += dr3;
						g3 += dg3;
						b3 += db3;
						r2 += dr2;
						g2 += dg2;
						b2 += db2;
						y_a += DrawingArea.width;
						z_a += depth_increment;
					}
					return;
				}
				y_c -= y_b;
				y_b -= y_a;
				for(y_a = anIntArray1472[y_a]; --y_b >= 0; y_a += DrawingArea.width) {
					drawShadedScanline(DrawingArea.pixels, y_a, x_a >> 16, x_c >> 16, r1, g1, b1, r3, g3, b3, z_a, depth_slope);
					x_c += c_to_a;
					x_a += a_to_b;
					r3 += dr3;
					g3 += dg3;
					b3 += db3;
					r1 += dr1;
					g1 += dg1;
					b1 += db1;
					z_a += depth_increment;
				}
				while(--y_c >= 0) {
					drawShadedScanline(DrawingArea.pixels, y_a, x_b >> 16, x_c >> 16, r2, g2, b2, r3, g3, b3, z_a, depth_slope);
					x_c += c_to_a;
					x_b += b_to_c;
					r3 += dr3;
					g3 += dg3;
					b3 += db3;
					r2 += dr2;
					g2 += dg2;
					b2 += db2;
					y_a += DrawingArea.width;
					z_a += depth_increment;
				}
				return;
			}
			x_b = x_a <<= 16;
			r2 = r1 <<= 16;
			g2 = g1 <<= 16;
			b2 = b1 <<= 16;
			if(y_a < 0) {
				x_b -= c_to_a * y_a;
				x_a -= a_to_b * y_a;
				r2 -= dr3 * y_a;
				g2 -= dg3 * y_a;
				b2 -= db3 * y_a;
				r1 -= dr1 * y_a;
				g1 -= dg1 * y_a;
				b1 -= db1 * y_a;
				z_a -= depth_increment * y_a;
				y_a = 0;
			}
			x_c <<= 16;
			r3 <<= 16;
			g3 <<= 16;
			b3 <<= 16;
			if(y_c < 0) {
				x_c -= b_to_c * y_c;
				r3 -= dr2 * y_c;
				g3 -= dg2 * y_c;
				b3 -= db2 * y_c;
				y_c = 0;
			}
			if(y_a != y_c && c_to_a < a_to_b || y_a == y_c && b_to_c > a_to_b) {
				y_b -= y_c;
				y_c -= y_a;
				for(y_a = anIntArray1472[y_a]; --y_c >= 0; y_a += DrawingArea.width) {
					drawShadedScanline(DrawingArea.pixels, y_a, x_b >> 16, x_a >> 16, r2, g2, b2, r1, g1, b1, z_a, depth_slope);
					x_b += c_to_a;
					x_a += a_to_b;
					r2 += dr3;
					g2 += dg3;
					b2 += db3;
					r1 += dr1;
					g1 += dg1;
					b1 += db1;
					z_a += depth_increment;
				}
				while(--y_b >= 0) {
					drawShadedScanline(DrawingArea.pixels, y_a, x_c >> 16, x_a >> 16, r3, g3, b3, r1, g1, b1, z_a, depth_slope);
					x_c += b_to_c;
					x_a += a_to_b;
					r3 += dr2;
					g3 += dg2;
					b3 += db2;
					r1 += dr1;
					g1 += dg1;
					b1 += db1;
					y_a += DrawingArea.width;
					z_a += depth_increment;
				}
				return;
			}
			y_b -= y_c;
			y_c -= y_a;
			for(y_a = anIntArray1472[y_a]; --y_c >= 0; y_a += DrawingArea.width) {
				drawShadedScanline(DrawingArea.pixels, y_a, x_a >> 16, x_b >> 16, r1, g1, b1, r2, g2, b2, z_a, depth_slope);
				x_b += c_to_a;
				x_a += a_to_b;
				r2 += dr3;
				g2 += dg3;
				b2 += db3;
				r1 += dr1;
				g1 += dg1;
				b1 += db1;
				z_a += depth_increment;
			}
			while(--y_b >= 0) {
				drawShadedScanline(DrawingArea.pixels, y_a, x_a >> 16, x_c >> 16, r1, g1, b1, r3, g3, b3, z_a, depth_slope);
				x_c += b_to_c;
				x_a += a_to_b;
				r3 += dr2;
				g3 += dg2;
				b3 += db2;
				r1 += dr1;
				g1 += dg1;
				b1 += db1;
				y_a += DrawingArea.width;
				z_a += depth_increment;
			}
			return;
		}
		if(y_b <= y_c) {
			if(y_b >= DrawingArea.bottomY) {
				return;
			}
			if(y_c > DrawingArea.bottomY) {
				y_c = DrawingArea.bottomY;
			}
			if(y_a > DrawingArea.bottomY) {
				y_a = DrawingArea.bottomY;
			}
			z_b = z_b - depth_slope * x_b + depth_slope;
			if(y_c < y_a) {
				x_a = x_b <<= 16;
				r1 = r2 <<= 16;
				g1 = g2 <<= 16;
				b1 = b2 <<= 16;
				if(y_b < 0) {
					x_a -= a_to_b * y_b;
					x_b -= b_to_c * y_b;
					r1 -= dr1 * y_b;
					g1 -= dg1 * y_b;
					b1 -= db1 * y_b;
					r2 -= dr2 * y_b;
					g2 -= dg2 * y_b;
					b2 -= db2 * y_b;
					z_b -= depth_increment * y_b;
					y_b = 0;
				}
				x_c <<= 16;
				r3 <<= 16;
				g3 <<= 16;
				b3 <<= 16;
				if(y_c < 0) {
					x_c -= c_to_a * y_c;
					r3 -= dr3 * y_c;
					g3 -= dg3 * y_c;
					b3 -= db3 * y_c;
					y_c = 0;
				}
				if(y_b != y_c && a_to_b < b_to_c || y_b == y_c && a_to_b > c_to_a) {
					y_a -= y_c;
					y_c -= y_b;
					for(y_b = anIntArray1472[y_b]; --y_c >= 0; y_b += DrawingArea.width) {
						drawShadedScanline(DrawingArea.pixels, y_b, x_a >> 16, x_b >> 16, r1, g1, b1, r2, g2, b2, z_b, depth_slope);
						x_a += a_to_b;
						x_b += b_to_c;
						r1 += dr1;
						g1 += dg1;
						b1 += db1;
						r2 += dr2;
						g2 += dg2;
						b2 += db2;
						z_b += depth_increment;
					}
					while(--y_a >= 0) {
						drawShadedScanline(DrawingArea.pixels, y_b, x_a >> 16, x_c >> 16, r1, g1, b1, r3, g3, b3, z_b, depth_slope);
						x_a += a_to_b;
						x_c += c_to_a;
						r1 += dr1;
						g1 += dg1;
						b1 += db1;
						r3 += dr3;
						g3 += dg3;
						b3 += db3;
						y_b += DrawingArea.width;
						z_b += depth_increment;
					}
					return;
				}
				y_a -= y_c;
				y_c -= y_b;
				for(y_b = anIntArray1472[y_b]; --y_c >= 0; y_b += DrawingArea.width) {
					drawShadedScanline(DrawingArea.pixels, y_b, x_b >> 16, x_a >> 16, r2, g2, b2, r1, g1, b1, z_b, depth_slope);
					x_a += a_to_b;
					x_b += b_to_c;
					r1 += dr1;
					g1 += dg1;
					b1 += db1;
					r2 += dr2;
					g2 += dg2;
					b2 += db2;
					z_b += depth_increment;
				}
				while(--y_a >= 0) {
					drawShadedScanline(DrawingArea.pixels, y_b, x_c >> 16, x_a >> 16, r3, g3, b3, r1, g1, b1, z_b, depth_slope);
					x_a += a_to_b;
					x_c += c_to_a;
					r1 += dr1;
					g1 += dg1;
					b1 += db1;
					r3 += dr3;
					g3 += dg3;
					b3 += db3;
					y_b += DrawingArea.width;
					z_b += depth_increment;
				}
				return;
			}
			x_c = x_b <<= 16;
			r3 = r2 <<= 16;
			g3 = g2 <<= 16;
			b3 = b2 <<= 16;
			if(y_b < 0) {
				x_c -= a_to_b * y_b;
				x_b -= b_to_c * y_b;
				r3 -= dr1 * y_b;
				g3 -= dg1 * y_b;
				b3 -= db1 * y_b;
				r2 -= dr2 * y_b;
				g2 -= dg2 * y_b;
				b2 -= db2 * y_b;
				z_b -= depth_increment * y_b;
				y_b = 0;
			}
			x_a <<= 16;
			r1 <<= 16;
			g1 <<= 16;
			b1 <<= 16;
			if(y_a < 0) {
				x_a -= c_to_a * y_a;
				r1 -= dr3 * y_a;
				g1 -= dg3 * y_a;
				b1 -= db3 * y_a;
				y_a = 0;
			}
			if(a_to_b < b_to_c) {
				y_c -= y_a;
				y_a -= y_b;
				for(y_b = anIntArray1472[y_b]; --y_a >= 0; y_b += DrawingArea.width) {
					drawShadedScanline(DrawingArea.pixels, y_b, x_c >> 16, x_b >> 16, r3, g3, b3, r2, g2, b2, z_b, depth_slope);
					x_c += a_to_b;
					x_b += b_to_c;
					r3 += dr1;
					g3 += dg1;
					b3 += db1;
					r2 += dr2;
					g2 += dg2;
					b2 += db2;
					z_b += depth_increment;
				}
				while(--y_c >= 0) {
					drawShadedScanline(DrawingArea.pixels, y_b, x_a >> 16, x_b >> 16, r1, g1, b1, r2, g2, b2, z_b, depth_slope);
					x_a += c_to_a;
					x_b += b_to_c;
					r1 += dr3;
					g1 += dg3;
					b1 += db3;
					r2 += dr2;
					g2 += dg2;
					b2 += db2;
					y_b += DrawingArea.width;
					z_b += depth_increment;
				}
				return;
			}
			y_c -= y_a;
			y_a -= y_b;
			for(y_b = anIntArray1472[y_b]; --y_a >= 0; y_b += DrawingArea.width) {
				drawShadedScanline(DrawingArea.pixels, y_b, x_b >> 16, x_c >> 16, r2, g2, b2, r3, g3, b3, z_b, depth_slope);
				x_c += a_to_b;
				x_b += b_to_c;
				r3 += dr1;
				g3 += dg1;
				b3 += db1;
				r2 += dr2;
				g2 += dg2;
				b2 += db2;
				z_b += depth_increment;
			}
			while(--y_c >= 0) {
				drawShadedScanline(DrawingArea.pixels, y_b, x_b >> 16, x_a >> 16, r2, g2, b2, r1, g1, b1, z_b, depth_slope);
				x_a += c_to_a;
				x_b += b_to_c;
				r1 += dr3;
				g1 += dg3;
				b1 += db3;
				r2 += dr2;
				g2 += dg2;
				b2 += db2;
				y_b += DrawingArea.width;
				z_b += depth_increment;
			}
			return;
		}
		if(y_c >= DrawingArea.bottomY) {
			return;
		}
		if(y_a > DrawingArea.bottomY) {
			y_a = DrawingArea.bottomY;
		}
		if(y_b > DrawingArea.bottomY) {
			y_b = DrawingArea.bottomY;
		}
		z_c = z_c - depth_slope * x_c + depth_slope;
		if(y_a < y_b) {
			x_b = x_c <<= 16;
			r2 = r3 <<= 16;
			g2 = g3 <<= 16;
			b2 = b3 <<= 16;
			if(y_c < 0) {
				x_b -= b_to_c * y_c;
				x_c -= c_to_a * y_c;
				r2 -= dr2 * y_c;
				g2 -= dg2 * y_c;
				b2 -= db2 * y_c;
				r3 -= dr3 * y_c;
				g3 -= dg3 * y_c;
				b3 -= db3 * y_c;
				z_c -= depth_increment * y_c;
				y_c = 0;
			}
			x_a <<= 16;
			r1 <<= 16;
			g1 <<= 16;
			b1 <<= 16;
			if(y_a < 0) {
				x_a -= a_to_b * y_a;
				r1 -= dr1 * y_a;
				g1 -= dg1 * y_a;
				b1 -= db1 * y_a;
				y_a = 0;
			}
			if(b_to_c < c_to_a) {
				y_b -= y_a;
				y_a -= y_c;
				for(y_c = anIntArray1472[y_c]; --y_a >= 0; y_c += DrawingArea.width) {
					drawShadedScanline(DrawingArea.pixels, y_c, x_b >> 16, x_c >> 16, r2, g2, b2, r3, g3, b3, z_c, depth_slope);
					x_b += b_to_c;
					x_c += c_to_a;
					r2 += dr2;
					g2 += dg2;
					b2 += db2;
					r3 += dr3;
					g3 += dg3;
					b3 += db3;
					z_c += depth_increment;
				}
				while(--y_b >= 0) {
					drawShadedScanline(DrawingArea.pixels, y_c, x_b >> 16, x_a >> 16, r2, g2, b2, r1, g1, b1, z_c, depth_slope);
					x_b += b_to_c;
					x_a += a_to_b;
					r2 += dr2;
					g2 += dg2;
					b2 += db2;
					r1 += dr1;
					g1 += dg1;
					b1 += db1;
					y_c += DrawingArea.width;
					z_c += depth_increment;
				}
				return;
			}
			y_b -= y_a;
			y_a -= y_c;
			for(y_c = anIntArray1472[y_c]; --y_a >= 0; y_c += DrawingArea.width) {
				drawShadedScanline(DrawingArea.pixels, y_c, x_c >> 16, x_b >> 16, r3, g3, b3, r2, g2, b2, z_c, depth_slope);
				x_b += b_to_c;
				x_c += c_to_a;
				r2 += dr2;
				g2 += dg2;
				b2 += db2;
				r3 += dr3;
				g3 += dg3;
				b3 += db3;
				z_c += depth_increment;
			}
			while(--y_b >= 0) {
				drawShadedScanline(DrawingArea.pixels, y_c, x_a >> 16, x_b >> 16, r1, g1, b1, r2, g2, b2, z_c, depth_slope);
				x_b += b_to_c;
				x_a += a_to_b;
				r2 += dr2;
				g2 += dg2;
				b2 += db2;
				r1 += dr1;
				g1 += dg1;
				b1 += db1;
				z_c += depth_increment;
				y_c += DrawingArea.width;
			}
			return;
		}
		x_a = x_c <<= 16;
		r1 = r3 <<= 16;
		g1 = g3 <<= 16;
		b1 = b3 <<= 16;
		if(y_c < 0) {
			x_a -= b_to_c * y_c;
			x_c -= c_to_a * y_c;
			r1 -= dr2 * y_c;
			g1 -= dg2 * y_c;
			b1 -= db2 * y_c;
			r3 -= dr3 * y_c;
			g3 -= dg3 * y_c;
			b3 -= db3 * y_c;
			z_c -= depth_increment * y_c;
			y_c = 0;
		}
		x_b <<= 16;
		r2 <<= 16;
		g2 <<= 16;
		b2 <<= 16;
		if(y_b < 0) {
			x_b -= a_to_b * y_b;
			r2 -= dr1 * y_b;
			g2 -= dg1 * y_b;
			b2 -= db1 * y_b;
			y_b = 0;
		}
		if(b_to_c < c_to_a) {
			y_a -= y_b;
			y_b -= y_c;
			for(y_c = anIntArray1472[y_c]; --y_b >= 0; y_c += DrawingArea.width) {
				drawShadedScanline(DrawingArea.pixels, y_c, x_a >> 16, x_c >> 16, r1, g1, b1, r3, g3, b3, z_c, depth_slope);
				x_a += b_to_c;
				x_c += c_to_a;
				r1 += dr2;
				g1 += dg2;
				b1 += db2;
				r3 += dr3;
				g3 += dg3;
				b3 += db3;
				z_c += depth_increment;
			}
			while(--y_a >= 0) {
				drawShadedScanline(DrawingArea.pixels, y_c, x_b >> 16, x_c >> 16, r2, g2, b2, r3, g3, b3, z_c, depth_slope);
				x_b += a_to_b;
				x_c += c_to_a;
				r2 += dr1;
				g2 += dg1;
				b2 += db1;
				r3 += dr3;
				g3 += dg3;
				b3 += db3;
				z_c += depth_increment;
				y_c += DrawingArea.width;
			}
			return;
		}
		y_a -= y_b;
		y_b -= y_c;
		for(y_c = anIntArray1472[y_c]; --y_b >= 0; y_c += DrawingArea.width) {
			drawShadedScanline(DrawingArea.pixels, y_c, x_c >> 16, x_a >> 16, r3, g3, b3, r1, g1, b1, z_c, depth_slope);
			x_a += b_to_c;
			x_c += c_to_a;
			r1 += dr2;
			g1 += dg2;
			b1 += db2;
			r3 += dr3;
			g3 += dg3;
			b3 += db3;
			z_c += depth_increment;
		}
		while(--y_a >= 0) {
			drawShadedScanline(DrawingArea.pixels, y_c, x_c >> 16, x_b >> 16, r3, g3, b3, r2, g2, b2, z_c, depth_slope);
			x_b += a_to_b;
			x_c += c_to_a;
			r2 += dr1;
			g2 += dg1;
			b2 += db1;
			r3 += dr3;
			g3 += dg3;
			b3 += db3;
			y_c += DrawingArea.width;
			z_c += depth_increment;
		}
	}

	public static void drawShadedScanline(int[] dest, int offset, int x1, int x2, int r1, int g1, int b1, int r2, int g2, int b2, float depth, float depth_slope) {
		int n = x2 - x1;
		if (n <= 0) {
			return;
		}
		r2 = (r2 - r1) / n;
		g2 = (g2 - g1) / n;
		b2 = (b2 - b1) / n;
		if (aBoolean1462) {
			if (x2 > DrawingArea.centerX) {
				n -= x2 - DrawingArea.centerX;
				x2 = DrawingArea.centerX;
			}
			if (x1 < 0) {
				n = x2;
				r1 -= x1 * r2;
				g1 -= x1 * g2;
				b1 -= x1 * b2;
				x1 = 0;
			}
		}
		if (x1 < x2) {
			offset += x1;
			depth += depth_slope * (float) x1;
			if (anInt1465 == 0) {
				while (--n >= 0) {
					if (true) {
						dest[offset] = (r1 & 0xff0000) | (g1 >> 8 & 0xff00) | (b1 >> 16 & 0xff);
						DrawingArea.depthBuffer[offset] = depth;
					}
					depth += depth_slope;
					r1 += r2;
					g1 += g2;
					b1 += b2;
					offset++;
				}
			} else {
				final int a1 = anInt1465;
				final int a2 = 256 - anInt1465;
				int rgb;
				int dst;
				while (--n >= 0) {
					rgb = (r1 & 0xff0000) | (g1 >> 8 & 0xff00) | (b1 >> 16 & 0xff);
					rgb = ((rgb & 0xff00ff) * a2 >> 8 & 0xff00ff) + ((rgb & 0xff00) * a2 >> 8 & 0xff00);
					dst = dest[offset];
					if (true) {
						dest[offset] = rgb + ((dst & 0xff00ff) * a1 >> 8 & 0xff00ff) + ((dst & 0xff00) * a1 >> 8 & 0xff00);
						DrawingArea.depthBuffer[offset] = depth;
					}
					depth += depth_slope;
					r1 += r2;
					g1 += g2;
					b1 += b2;
					offset++;
				}
			}
		}
	}

	public static void drawFlatTriangle(int y_a, int y_b, int y_c, int x_a, int x_b, int x_c, int k1, float z_a, float z_b, float z_c) {
		if (z_a < 0 || z_b < 0 || z_c < 0) {
			return;
		}
		int a_to_b = 0;
		if(y_b != y_a) {
			a_to_b = (x_b - x_a << 16) / (y_b - y_a);
		}
		int b_to_c = 0;
		if(y_c != y_b) {
			b_to_c = (x_c - x_b << 16) / (y_c - y_b);
		}
		int c_to_a = 0;
		if(y_c != y_a) {
			c_to_a = (x_a - x_c << 16) / (y_a - y_c);
		}
		float b_aX = x_b - x_a;
		float b_aY = y_b - y_a;
		float c_aX = x_c - x_a;
		float c_aY = y_c - y_a;
		float b_aZ = z_b - z_a;
		float c_aZ = z_c - z_a;

		float div = b_aX * c_aY - c_aX * b_aY;
		float depth_slope = (b_aZ * c_aY - c_aZ * b_aY) / div;
		float depth_increment = (c_aZ * b_aX - b_aZ * c_aX) / div;
		if(y_a <= y_b && y_a <= y_c) {
			if(y_a >= DrawingArea.bottomY)
				return;
			if(y_b > DrawingArea.bottomY)
				y_b = DrawingArea.bottomY;
			if(y_c > DrawingArea.bottomY)
				y_c = DrawingArea.bottomY;
			z_a = z_a - depth_slope * x_a + depth_slope;
			if(y_b < y_c)
			{
				x_c = x_a <<= 16;
				if(y_a < 0)
				{
					x_c -= c_to_a * y_a;
					x_a -= a_to_b * y_a;
					z_a -= depth_increment * y_a;
					y_a = 0;
				}
				x_b <<= 16;
				if(y_b < 0)
				{
					x_b -= b_to_c * y_b;
					y_b = 0;
				}
				if(y_a != y_b && c_to_a < a_to_b || y_a == y_b && c_to_a > b_to_c)
				{
					y_c -= y_b;
					y_b -= y_a;
					for(y_a = anIntArray1472[y_a]; --y_b >= 0; y_a += DrawingArea.width)
					{
						drawFlatTexturedScanline(DrawingArea.pixels, y_a, k1, x_c >> 16, x_a >> 16, z_a, depth_slope);
						x_c += c_to_a;
						x_a += a_to_b;
						z_a += depth_increment;
					}

					while(--y_c >= 0) 
					{
						drawFlatTexturedScanline(DrawingArea.pixels, y_a, k1, x_c >> 16, x_b >> 16, z_a, depth_slope);
						x_c += c_to_a;
						x_b += b_to_c;
						y_a += DrawingArea.width;
						z_a += depth_increment;
					}
					return;
				}
				y_c -= y_b;
				y_b -= y_a;
				for(y_a = anIntArray1472[y_a]; --y_b >= 0; y_a += DrawingArea.width)
				{
					drawFlatTexturedScanline(DrawingArea.pixels, y_a, k1, x_a >> 16, x_c >> 16, z_a, depth_slope);
					x_c += c_to_a;
					x_a += a_to_b;
					z_a += depth_increment;
				}

				while(--y_c >= 0) 
				{
					drawFlatTexturedScanline(DrawingArea.pixels, y_a, k1, x_b >> 16, x_c >> 16, z_a, depth_slope);
					x_c += c_to_a;
					x_b += b_to_c;
					y_a += DrawingArea.width;
					z_a += depth_increment;
				}
				return;
			}
			x_b = x_a <<= 16;
			if(y_a < 0)
			{
				x_b -= c_to_a * y_a;
				x_a -= a_to_b * y_a;
				z_a -= depth_increment * y_a;
				y_a = 0;
				
			}
			x_c <<= 16;
			if(y_c < 0)
			{
				x_c -= b_to_c * y_c;
				y_c = 0;
			}
			if(y_a != y_c && c_to_a < a_to_b || y_a == y_c && b_to_c > a_to_b)
			{
				y_b -= y_c;
				y_c -= y_a;
				for(y_a = anIntArray1472[y_a]; --y_c >= 0; y_a += DrawingArea.width)
				{
					drawFlatTexturedScanline(DrawingArea.pixels, y_a, k1, x_b >> 16, x_a >> 16, z_a, depth_slope);
					z_a += depth_increment;
					x_b += c_to_a;
					x_a += a_to_b;
				}

				while(--y_b >= 0) 
				{
					drawFlatTexturedScanline(DrawingArea.pixels, y_a, k1, x_c >> 16, x_a >> 16, z_a, depth_slope);
					z_a += depth_increment;
					x_c += b_to_c;
					x_a += a_to_b;
					y_a += DrawingArea.width;
				}
				return;
			}
			y_b -= y_c;
			y_c -= y_a;
			for(y_a = anIntArray1472[y_a]; --y_c >= 0; y_a += DrawingArea.width)
			{
				drawFlatTexturedScanline(DrawingArea.pixels, y_a, k1, x_a >> 16, x_b >> 16, z_a, depth_slope);
				z_a += depth_increment;
				x_b += c_to_a;
				x_a += a_to_b;
			}

			while(--y_b >= 0) 
			{
				drawFlatTexturedScanline(DrawingArea.pixels, y_a, k1, x_a >> 16, x_c >> 16, z_a, depth_slope);
				z_a += depth_increment;
				x_c += b_to_c;
				x_a += a_to_b;
				y_a += DrawingArea.width;
			}
			return;
		}
		if(y_b <= y_c)
		{
			if(y_b >= DrawingArea.bottomY)
				return;
			if(y_c > DrawingArea.bottomY)
				y_c = DrawingArea.bottomY;
			if(y_a > DrawingArea.bottomY)
				y_a = DrawingArea.bottomY;
			z_b = z_b - depth_slope * x_b + depth_slope;
			if(y_c < y_a)
			{
				x_a = x_b <<= 16;
				if(y_b < 0)
				{
					x_a -= a_to_b * y_b;
					x_b -= b_to_c * y_b;
					z_b -= depth_increment * y_b;
					y_b = 0;
				}
				x_c <<= 16;
				if(y_c < 0)
				{
					x_c -= c_to_a * y_c;
					y_c = 0;
				}
				if(y_b != y_c && a_to_b < b_to_c || y_b == y_c && a_to_b > c_to_a)
				{
					y_a -= y_c;
					y_c -= y_b;
					for(y_b = anIntArray1472[y_b]; --y_c >= 0; y_b += DrawingArea.width)
					{
						drawFlatTexturedScanline(DrawingArea.pixels, y_b, k1, x_a >> 16, x_b >> 16, z_b, depth_slope);
						z_b += depth_increment;
						x_a += a_to_b;
						x_b += b_to_c;
					}

					while(--y_a >= 0) 
					{
						drawFlatTexturedScanline(DrawingArea.pixels, y_b, k1, x_a >> 16, x_c >> 16, z_b, depth_slope);
						z_b += depth_increment;
						x_a += a_to_b;
						x_c += c_to_a;
						y_b += DrawingArea.width;
					}
					return;
				}
				y_a -= y_c;
				y_c -= y_b;
				for(y_b = anIntArray1472[y_b]; --y_c >= 0; y_b += DrawingArea.width)
				{
					drawFlatTexturedScanline(DrawingArea.pixels, y_b, k1, x_b >> 16, x_a >> 16, z_b, depth_slope);
					z_b += depth_increment;
					x_a += a_to_b;
					x_b += b_to_c;
				}

				while(--y_a >= 0) 
				{
					drawFlatTexturedScanline(DrawingArea.pixels, y_b, k1, x_c >> 16, x_a >> 16, z_b, depth_slope);
					z_b += depth_increment;
					x_a += a_to_b;
					x_c += c_to_a;
					y_b += DrawingArea.width;
				}
				return;
			}
			x_c = x_b <<= 16;
			if(y_b < 0)
			{
				x_c -= a_to_b * y_b;
				x_b -= b_to_c * y_b;
				z_b -= depth_increment * y_b;
				y_b = 0;
			}
			x_a <<= 16;
			if(y_a < 0)
			{
				x_a -= c_to_a * y_a;
				y_a = 0;
			}
			if(a_to_b < b_to_c)
			{
				y_c -= y_a;
				y_a -= y_b;
				for(y_b = anIntArray1472[y_b]; --y_a >= 0; y_b += DrawingArea.width)
				{
					drawFlatTexturedScanline(DrawingArea.pixels, y_b, k1, x_c >> 16, x_b >> 16, z_b, depth_slope);
					z_b += depth_increment;
					x_c += a_to_b;
					x_b += b_to_c;
				}

				while(--y_c >= 0) 
				{
					drawFlatTexturedScanline(DrawingArea.pixels, y_b, k1, x_a >> 16, x_b >> 16, z_b, depth_slope);
					z_b += depth_increment;
					x_a += c_to_a;
					x_b += b_to_c;
					y_b += DrawingArea.width;
				}
				return;
			}
			y_c -= y_a;
			y_a -= y_b;
			for(y_b = anIntArray1472[y_b]; --y_a >= 0; y_b += DrawingArea.width)
			{
				drawFlatTexturedScanline(DrawingArea.pixels, y_b, k1, x_b >> 16, x_c >> 16, z_b, depth_slope);
				z_b += depth_increment;
				x_c += a_to_b;
				x_b += b_to_c;
			}

			while(--y_c >= 0) 
			{
				drawFlatTexturedScanline(DrawingArea.pixels, y_b, k1, x_b >> 16, x_a >> 16, z_b, depth_slope);
				z_b += depth_increment;
				x_a += c_to_a;
				x_b += b_to_c;
				y_b += DrawingArea.width;
			}
			return;
		}
		if(y_c >= DrawingArea.bottomY)
			return;
		if(y_a > DrawingArea.bottomY)
			y_a = DrawingArea.bottomY;
		if(y_b > DrawingArea.bottomY)
			y_b = DrawingArea.bottomY;
		z_c = z_c - depth_slope * x_c + depth_slope;
		if(y_a < y_b)
		{
			x_b = x_c <<= 16;
			if(y_c < 0)
			{
				x_b -= b_to_c * y_c;
				x_c -= c_to_a * y_c;
				z_c -= depth_increment * y_c;
				y_c = 0;
			}
			x_a <<= 16;
			if(y_a < 0)
			{
				x_a -= a_to_b * y_a;
				y_a = 0;
			}
			if(b_to_c < c_to_a)
			{
				y_b -= y_a;
				y_a -= y_c;
				for(y_c = anIntArray1472[y_c]; --y_a >= 0; y_c += DrawingArea.width)
				{
					drawFlatTexturedScanline(DrawingArea.pixels, y_c, k1, x_b >> 16, x_c >> 16, z_c, depth_slope);
					z_c += depth_increment;
					x_b += b_to_c;
					x_c += c_to_a;
				}

				while(--y_b >= 0) 
				{
					drawFlatTexturedScanline(DrawingArea.pixels, y_c, k1, x_b >> 16, x_a >> 16, z_c, depth_slope);
					z_c += depth_increment;
					x_b += b_to_c;
					x_a += a_to_b;
					y_c += DrawingArea.width;
				}
				return;
			}
			y_b -= y_a;
			y_a -= y_c;
			for(y_c = anIntArray1472[y_c]; --y_a >= 0; y_c += DrawingArea.width)
			{
				drawFlatTexturedScanline(DrawingArea.pixels, y_c, k1, x_c >> 16, x_b >> 16, z_c, depth_slope);
				z_c += depth_increment;
				x_b += b_to_c;
				x_c += c_to_a;
			}

			while(--y_b >= 0) 
			{
				drawFlatTexturedScanline(DrawingArea.pixels, y_c, k1, x_a >> 16, x_b >> 16, z_c, depth_slope);
				z_c += depth_increment;
				x_b += b_to_c;
				x_a += a_to_b;
				y_c += DrawingArea.width;
			}
			return;
		}
		x_a = x_c <<= 16;
		if(y_c < 0)
		{
			x_a -= b_to_c * y_c;
			x_c -= c_to_a * y_c;
			z_c -= depth_increment * y_c;
			y_c = 0;
		}
		x_b <<= 16;
		if(y_b < 0)
		{
			x_b -= a_to_b * y_b;
			y_b = 0;
		}
		if(b_to_c < c_to_a)
		{
			y_a -= y_b;
			y_b -= y_c;
			for(y_c = anIntArray1472[y_c]; --y_b >= 0; y_c += DrawingArea.width)
			{
				drawFlatTexturedScanline(DrawingArea.pixels, y_c, k1, x_a >> 16, x_c >> 16, z_c, depth_slope);
				z_c += depth_increment;
				x_a += b_to_c;
				x_c += c_to_a;
			}

			while(--y_a >= 0) 
			{
				drawFlatTexturedScanline(DrawingArea.pixels, y_c, k1, x_b >> 16, x_c >> 16, z_c, depth_slope);
				z_c += depth_increment;
				x_b += a_to_b;
				x_c += c_to_a;
				y_c += DrawingArea.width;
			}
			return;
		}
		y_a -= y_b;
		y_b -= y_c;
		for(y_c = anIntArray1472[y_c]; --y_b >= 0; y_c += DrawingArea.width)
		{
			drawFlatTexturedScanline(DrawingArea.pixels, y_c, k1, x_c >> 16, x_a >> 16, z_c, depth_slope);
			z_c += depth_increment;
			x_a += b_to_c;
			x_c += c_to_a;
		}

		while(--y_a >= 0) 
		{
			drawFlatTexturedScanline(DrawingArea.pixels, y_c, k1, x_c >> 16, x_b >> 16, z_c, depth_slope);
			z_c += depth_increment;
			x_b += a_to_b;
			x_c += c_to_a;
			y_c += DrawingArea.width;
		}
	}

	private static void drawFlatTexturedScanline(int dest[], int dest_off, int loops, int start_x, int end_x, float depth, float depth_slope) {
		int rgb;
		if(aBoolean1462) {
			if(end_x > DrawingArea.centerX)
				end_x = DrawingArea.centerX;
			if(start_x < 0)
				start_x = 0;
		}
		if(start_x >= end_x)
			return;
		dest_off += start_x;
		rgb = end_x - start_x >> 2;
		depth += depth_slope * (float) start_x;
		if(anInt1465 == 0)
		{
			while(--rgb >= 0) 
			{
				for (int i = 0; i < 4; i++) {
					if (true) {
						dest[dest_off] = loops;
						DrawingArea.depthBuffer[dest_off] = depth;
					}
					dest_off++;
					depth += depth_slope;
				}
			}
			for(rgb = end_x - start_x & 3; --rgb >= 0;) {
				if (true) {
					dest[dest_off] = loops;
					DrawingArea.depthBuffer[dest_off] = depth;
				}
				dest_off++;
				depth += depth_slope;
			}
			return;
		}
		int dest_alpha = anInt1465;
		int src_alpha = 256 - anInt1465;
		loops = ((loops & 0xff00ff) * src_alpha >> 8 & 0xff00ff) + ((loops & 0xff00) * src_alpha >> 8 & 0xff00);
		while(--rgb >= 0) 
		{
			for (int i = 0; i < 4; i++) {
				if (true) {
					dest[dest_off] = loops + ((dest[dest_off] & 0xff00ff) * dest_alpha >> 8 & 0xff00ff) + ((dest[dest_off] & 0xff00) * dest_alpha >> 8 & 0xff00);
					DrawingArea.depthBuffer[dest_off] = depth;
				}
				dest_off++;
				depth += depth_slope;
			}
		}
		for(rgb = end_x - start_x & 3; --rgb >= 0;) {
			if (true) {
				dest[dest_off] = loops + ((dest[dest_off] & 0xff00ff) * dest_alpha >> 8 & 0xff00ff) + ((dest[dest_off] & 0xff00) * dest_alpha >> 8 & 0xff00);
				DrawingArea.depthBuffer[dest_off] = depth;
			}
			dest_off++;
			depth += depth_slope;
		}
	}
	
	public static void drawTexturedTriangle(int y_a, int y_b, int y_c, int x_a, int x_b, int x_c, int k1, int l1, int i2, int Px, int Mx, int Nx, int Pz, int Mz, int Nz,  int Py, int My, int Ny, int k4, float z_a, float z_b, float z_c) {
		if (z_a < 0 || z_b < 0 || z_c < 0) 
			return;
		int texture[] = getTexturePixels(k4);
		aBoolean1463 = !aBooleanArray1475[k4];
		Mx = Px - Mx;
		Mz = Pz - Mz;
		My = Py - My;
		Nx -= Px;
		Nz -= Pz;
		Ny -= Py;
		int Oa = Nx * Pz - Nz * Px << (WorldController.viewDistance == 9 ? 14 : 15);
		int Ha = Nz * Py - Ny * Pz << 8;
		int Va = Ny * Px - Nx * Py << 5;
		int Ob = Mx * Pz - Mz * Px << (WorldController.viewDistance == 9 ? 14 : 15);
		int Hb = Mz * Py - My * Pz << 8;
		int Vb = My * Px - Mx * Py << 5;
		int Oc = Mz * Nx - Mx * Nz << (WorldController.viewDistance == 9 ? 14 : 15);
		int Hc = My * Nz - Mz * Ny << 8;
		int Vc = Mx * Ny - My * Nx << 5;
		int a_to_b = 0;
		int grad_a_off = 0;
		if(y_b != y_a)
		{
			a_to_b = (x_b - x_a << 16) / (y_b - y_a);
			grad_a_off = (l1 - k1 << 16) / (y_b - y_a);
		}
		int b_to_c = 0;
		int grad_b_off = 0;
		if(y_c != y_b)
		{
			b_to_c = (x_c - x_b << 16) / (y_c - y_b);
			grad_b_off = (i2 - l1 << 16) / (y_c - y_b);
		}
		int c_to_a = 0;
		int grad_c_off = 0;
		if(y_c != y_a)
		{
			c_to_a = (x_a - x_c << 16) / (y_a - y_c);
			grad_c_off = (k1 - i2 << 16) / (y_a - y_c);
		}
		float b_aX = x_b - x_a;
		float b_aY = y_b - y_a;
		float c_aX = x_c - x_a;
		float c_aY = y_c - y_a;
		float b_aZ = z_b - z_a;
		float c_aZ = z_c - z_a;

		float div = b_aX * c_aY - c_aX * b_aY;
		float depth_slope = (b_aZ * c_aY - c_aZ * b_aY) / div;
		float depth_increment = (c_aZ * b_aX - b_aZ * c_aX) / div;
		if(y_a <= y_b && y_a <= y_c)
		{
			if(y_a >= DrawingArea.bottomY)
				return;
			if(y_b > DrawingArea.bottomY)
				y_b = DrawingArea.bottomY;
			if(y_c > DrawingArea.bottomY)
				y_c = DrawingArea.bottomY;
			z_a = z_a - depth_slope * x_a + depth_slope;
			if(y_b < y_c)
			{
				x_c = x_a <<= 16;
				i2 = k1 <<= 16;
				if(y_a < 0)
				{
					x_c -= c_to_a * y_a;
					x_a -= a_to_b * y_a;
					z_a -= depth_increment * y_a;
					i2 -= grad_c_off * y_a;
					k1 -= grad_a_off * y_a;
					y_a = 0;
				}
				x_b <<= 16;
				l1 <<= 16;
				if(y_b < 0)
				{
					x_b -= b_to_c * y_b;
					l1 -= grad_b_off * y_b;
					y_b = 0;
				}
				int k8 = y_a - textureInt2;
				Oa += Va * k8;
				Ob += Vb * k8;
				Oc += Vc * k8;
				if(y_a != y_b && c_to_a < a_to_b || y_a == y_b && c_to_a > b_to_c)
				{
					y_c -= y_b;
					y_b -= y_a;
					y_a = anIntArray1472[y_a];
					while(--y_b >= 0) 
					{
						drawTexturedScanline(DrawingArea.pixels, texture, y_a, x_c >> 16, x_a >> 16, i2 >> 8, k1 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_a, depth_slope);
						x_c += c_to_a;
						x_a += a_to_b;
						z_a += depth_increment;
						i2 += grad_c_off;
						k1 += grad_a_off;
						y_a += DrawingArea.width;
						Oa += Va;
						Ob += Vb;
						Oc += Vc;
					}
					while(--y_c >= 0) 
					{
						drawTexturedScanline(DrawingArea.pixels, texture, y_a, x_c >> 16, x_b >> 16, i2 >> 8, l1 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_a, depth_slope);
						x_c += c_to_a;
						x_b += b_to_c;
						z_a += depth_increment;
						i2 += grad_c_off;
						l1 += grad_b_off;
						y_a += DrawingArea.width;
						Oa += Va;
						Ob += Vb;
						Oc += Vc;
					}
					return;
				}
				y_c -= y_b;
				y_b -= y_a;
				y_a = anIntArray1472[y_a];
				while(--y_b >= 0) 
				{
					drawTexturedScanline(DrawingArea.pixels, texture, y_a, x_a >> 16, x_c >> 16, k1 >> 8, i2 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_a, depth_slope);
					x_c += c_to_a;
					x_a += a_to_b;
					z_a += depth_increment;
					i2 += grad_c_off;
					k1 += grad_a_off;
					y_a += DrawingArea.width;
					Oa += Va;
					Ob += Vb;
					Oc += Vc;
				}
				while(--y_c >= 0) 
				{
					drawTexturedScanline(DrawingArea.pixels, texture, y_a, x_b >> 16, x_c >> 16, l1 >> 8, i2 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_a, depth_slope);
					x_c += c_to_a;
					x_b += b_to_c;
					z_a += depth_increment;
					i2 += grad_c_off;
					l1 += grad_b_off;
					y_a += DrawingArea.width;
					Oa += Va;
					Ob += Vb;
					Oc += Vc;
				}
				return;
			}
			x_b = x_a <<= 16;
			l1 = k1 <<= 16;
			if(y_a < 0)
			{
				x_b -= c_to_a * y_a;
				x_a -= a_to_b * y_a;
				z_a -= depth_increment * y_a;
				l1 -= grad_c_off * y_a;
				k1 -= grad_a_off * y_a;
				y_a = 0;
			}
			x_c <<= 16;
			i2 <<= 16;
			if(y_c < 0)
			{
				x_c -= b_to_c * y_c;
				i2 -= grad_b_off * y_c;
				y_c = 0;
			}
			int l8 = y_a - textureInt2;
			Oa += Va * l8;
			Ob += Vb * l8;
			Oc += Vc * l8;
			if(y_a != y_c && c_to_a < a_to_b || y_a == y_c && b_to_c > a_to_b)
			{
				y_b -= y_c;
				y_c -= y_a;
				y_a = anIntArray1472[y_a];
				while(--y_c >= 0) 
				{
					drawTexturedScanline(DrawingArea.pixels, texture, y_a, x_b >> 16, x_a >> 16, l1 >> 8, k1 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_a, depth_slope);
					x_b += c_to_a;
					x_a += a_to_b;
					l1 += grad_c_off;
					k1 += grad_a_off;
					z_a += depth_increment;
					y_a += DrawingArea.width;
					Oa += Va;
					Ob += Vb;
					Oc += Vc;
				}
				while(--y_b >= 0) 
				{
					drawTexturedScanline(DrawingArea.pixels, texture, y_a, x_c >> 16, x_a >> 16, i2 >> 8, k1 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_a, depth_slope);
					x_c += b_to_c;
					x_a += a_to_b;
					i2 += grad_b_off;
					k1 += grad_a_off;
					z_a += depth_increment;
					y_a += DrawingArea.width;
					Oa += Va;
					Ob += Vb;
					Oc += Vc;
				}
				return;
			}
			y_b -= y_c;
			y_c -= y_a;
			y_a = anIntArray1472[y_a];
			while(--y_c >= 0) 
			{
				drawTexturedScanline(DrawingArea.pixels, texture, y_a, x_a >> 16, x_b >> 16, k1 >> 8, l1 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_a, depth_slope);
				x_b += c_to_a;
				x_a += a_to_b;
				l1 += grad_c_off;
				k1 += grad_a_off;
				z_a += depth_increment;
				y_a += DrawingArea.width;
				Oa += Va;
				Ob += Vb;
				Oc += Vc;
			}
			while(--y_b >= 0) 
			{
				drawTexturedScanline(DrawingArea.pixels, texture, y_a, x_a >> 16, x_c >> 16, k1 >> 8, i2 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_a, depth_slope);
				x_c += b_to_c;
				x_a += a_to_b;
				i2 += grad_b_off;
				k1 += grad_a_off;
				z_a += depth_increment;
				y_a += DrawingArea.width;
				Oa += Va;
				Ob += Vb;
				Oc += Vc;
			}
			return;
		}
		if(y_b <= y_c)
		{
			if(y_b >= DrawingArea.bottomY)
				return;
			if(y_c > DrawingArea.bottomY)
				y_c = DrawingArea.bottomY;
			if(y_a > DrawingArea.bottomY)
				y_a = DrawingArea.bottomY;
			z_b = z_b - depth_slope * x_b + depth_slope;
			if(y_c < y_a)
			{
				x_a = x_b <<= 16;
				k1 = l1 <<= 16;
				if(y_b < 0)
				{
					x_a -= a_to_b * y_b;
					x_b -= b_to_c * y_b;
					z_b -= depth_increment * y_b;
					k1 -= grad_a_off * y_b;
					l1 -= grad_b_off * y_b;
					y_b = 0;
				}
				x_c <<= 16;
				i2 <<= 16;
				if(y_c < 0)
				{
					x_c -= c_to_a * y_c;
					i2 -= grad_c_off * y_c;
					y_c = 0;
				}
				int i9 = y_b - textureInt2;
				Oa += Va * i9;
				Ob += Vb * i9;
				Oc += Vc * i9;
				if(y_b != y_c && a_to_b < b_to_c || y_b == y_c && a_to_b > c_to_a)
				{
					y_a -= y_c;
					y_c -= y_b;
					y_b = anIntArray1472[y_b];
					while(--y_c >= 0) 
					{
						drawTexturedScanline(DrawingArea.pixels, texture, y_b, x_a >> 16, x_b >> 16, k1 >> 8, l1 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_b, depth_slope);
						x_a += a_to_b;
						x_b += b_to_c;
						k1 += grad_a_off;
						l1 += grad_b_off;
						z_b += depth_increment;
						y_b += DrawingArea.width;
						Oa += Va;
						Ob += Vb;
						Oc += Vc;
					}
					while(--y_a >= 0) 
					{
						drawTexturedScanline(DrawingArea.pixels, texture, y_b, x_a >> 16, x_c >> 16, k1 >> 8, i2 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_b, depth_slope);
						x_a += a_to_b;
						x_c += c_to_a;
						k1 += grad_a_off;
						i2 += grad_c_off;
						z_b += depth_increment;
						y_b += DrawingArea.width;
						Oa += Va;
						Ob += Vb;
						Oc += Vc;
					}
					return;
				}
				y_a -= y_c;
				y_c -= y_b;
				y_b = anIntArray1472[y_b];
				while(--y_c >= 0) 
				{
					drawTexturedScanline(DrawingArea.pixels, texture, y_b, x_b >> 16, x_a >> 16, l1 >> 8, k1 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_b, depth_slope);
					x_a += a_to_b;
					x_b += b_to_c;
					k1 += grad_a_off;
					l1 += grad_b_off;
					z_b += depth_increment;
					y_b += DrawingArea.width;
					Oa += Va;
					Ob += Vb;
					Oc += Vc;
				}
				while(--y_a >= 0) 
				{
					drawTexturedScanline(DrawingArea.pixels, texture, y_b, x_c >> 16, x_a >> 16, i2 >> 8, k1 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_b, depth_slope);
					x_a += a_to_b;
					x_c += c_to_a;
					k1 += grad_a_off;
					i2 += grad_c_off;
					z_b += depth_increment;
					y_b += DrawingArea.width;
					Oa += Va;
					Ob += Vb;
					Oc += Vc;
				}
				return;
			}
			x_c = x_b <<= 16;
			i2 = l1 <<= 16;
			if(y_b < 0)
			{
				x_c -= a_to_b * y_b;
				x_b -= b_to_c * y_b;
				z_b -= depth_increment * y_b;
				i2 -= grad_a_off * y_b;
				l1 -= grad_b_off * y_b;
				y_b = 0;
			}
			x_a <<= 16;
			k1 <<= 16;
			if(y_a < 0)
			{
				x_a -= c_to_a * y_a;
				k1 -= grad_c_off * y_a;
				y_a = 0;
			}
			int j9 = y_b - textureInt2;
			Oa += Va * j9;
			Ob += Vb * j9;
			Oc += Vc * j9;
			if(a_to_b < b_to_c)
			{
				y_c -= y_a;
				y_a -= y_b;
				y_b = anIntArray1472[y_b];
				while(--y_a >= 0) 
				{
					drawTexturedScanline(DrawingArea.pixels, texture, y_b, x_c >> 16, x_b >> 16, i2 >> 8, l1 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_b, depth_slope);
					x_c += a_to_b;
					x_b += b_to_c;
					i2 += grad_a_off;
					l1 += grad_b_off;
					z_b += depth_increment;
					y_b += DrawingArea.width;
					Oa += Va;
					Ob += Vb;
					Oc += Vc;
				}
				while(--y_c >= 0) 
				{
					drawTexturedScanline(DrawingArea.pixels, texture, y_b, x_a >> 16, x_b >> 16, k1 >> 8, l1 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_b, depth_slope);
					x_a += c_to_a;
					x_b += b_to_c;
					k1 += grad_c_off;
					l1 += grad_b_off;
					z_b += depth_increment;
					y_b += DrawingArea.width;
					Oa += Va;
					Ob += Vb;
					Oc += Vc;
				}
				return;
			}
			y_c -= y_a;
			y_a -= y_b;
			y_b = anIntArray1472[y_b];
			while(--y_a >= 0) 
			{
				drawTexturedScanline(DrawingArea.pixels, texture, y_b, x_b >> 16, x_c >> 16, l1 >> 8, i2 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_b, depth_slope);
				x_c += a_to_b;
				x_b += b_to_c;
				i2 += grad_a_off;
				l1 += grad_b_off;
				z_b += depth_increment;
				y_b += DrawingArea.width;
				Oa += Va;
				Ob += Vb;
				Oc += Vc;
			}
			while(--y_c >= 0) 
			{
				drawTexturedScanline(DrawingArea.pixels, texture, y_b, x_b >> 16, x_a >> 16, l1 >> 8, k1 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_b, depth_slope);
				x_a += c_to_a;
				x_b += b_to_c;
				k1 += grad_c_off;
				l1 += grad_b_off;
				z_b += depth_increment;
				y_b += DrawingArea.width;
				Oa += Va;
				Ob += Vb;
				Oc += Vc;
			}
			return;
		}
		if(y_c >= DrawingArea.bottomY)
			return;
		if(y_a > DrawingArea.bottomY)
			y_a = DrawingArea.bottomY;
		if(y_b > DrawingArea.bottomY)
			y_b = DrawingArea.bottomY;
		z_c = z_c - depth_slope * x_c + depth_slope;
		if(y_a < y_b)
		{
			x_b = x_c <<= 16;
			l1 = i2 <<= 16;
			if(y_c < 0)
			{
				x_b -= b_to_c * y_c;
				x_c -= c_to_a * y_c;
				z_c -= depth_increment * y_c;
				l1 -= grad_b_off * y_c;
				i2 -= grad_c_off * y_c;
				y_c = 0;
			}
			x_a <<= 16;
			k1 <<= 16;
			if(y_a < 0)
			{
				x_a -= a_to_b * y_a;
				k1 -= grad_a_off * y_a;
				y_a = 0;
			}
			int k9 = y_c - textureInt2;
			Oa += Va * k9;
			Ob += Vb * k9;
			Oc += Vc * k9;
			if(b_to_c < c_to_a)
			{
				y_b -= y_a;
				y_a -= y_c;
				y_c = anIntArray1472[y_c];
				while(--y_a >= 0) 
				{
					drawTexturedScanline(DrawingArea.pixels, texture, y_c, x_b >> 16, x_c >> 16, l1 >> 8, i2 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_c, depth_slope);
					x_b += b_to_c;
					x_c += c_to_a;
					l1 += grad_b_off;
					i2 += grad_c_off;
					z_c += depth_increment;
					y_c += DrawingArea.width;
					Oa += Va;
					Ob += Vb;
					Oc += Vc;
				}
				while(--y_b >= 0) 
				{
					drawTexturedScanline(DrawingArea.pixels, texture, y_c, x_b >> 16, x_a >> 16, l1 >> 8, k1 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_c, depth_slope);
					x_b += b_to_c;
					x_a += a_to_b;
					l1 += grad_b_off;
					k1 += grad_a_off;
					z_c += depth_increment;
					y_c += DrawingArea.width;
					Oa += Va;
					Ob += Vb;
					Oc += Vc;
				}
				return;
			}
			y_b -= y_a;
			y_a -= y_c;
			y_c = anIntArray1472[y_c];
			while(--y_a >= 0) 
			{
				drawTexturedScanline(DrawingArea.pixels, texture, y_c, x_c >> 16, x_b >> 16, i2 >> 8, l1 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_c, depth_slope);
				x_b += b_to_c;
				x_c += c_to_a;
				l1 += grad_b_off;
				i2 += grad_c_off;
				z_c += depth_increment;
				y_c += DrawingArea.width;
				Oa += Va;
				Ob += Vb;
				Oc += Vc;
			}
			while(--y_b >= 0) 
			{
				drawTexturedScanline(DrawingArea.pixels, texture, y_c, x_a >> 16, x_b >> 16, k1 >> 8, l1 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_c, depth_slope);
				x_b += b_to_c;
				x_a += a_to_b;
				l1 += grad_b_off;
				k1 += grad_a_off;
				z_c += depth_increment;
				y_c += DrawingArea.width;
				Oa += Va;
				Ob += Vb;
				Oc += Vc;
			}
			return;
		}
		x_a = x_c <<= 16;
		k1 = i2 <<= 16;
		if(y_c < 0)
		{
			x_a -= b_to_c * y_c;
			x_c -= c_to_a * y_c;
			z_c -= depth_increment * y_c;
			k1 -= grad_b_off * y_c;
			i2 -= grad_c_off * y_c;
			y_c = 0;
		}
		x_b <<= 16;
		l1 <<= 16;
		if(y_b < 0)
		{
			x_b -= a_to_b * y_b;
			l1 -= grad_a_off * y_b;
			y_b = 0;
		}
		int l9 = y_c - textureInt2;
		Oa += Va * l9;
		Ob += Vb * l9;
		Oc += Vc * l9;
		if(b_to_c < c_to_a)
		{
			y_a -= y_b;
			y_b -= y_c;
			y_c = anIntArray1472[y_c];
			while(--y_b >= 0) 
			{
				drawTexturedScanline(DrawingArea.pixels, texture, y_c, x_a >> 16, x_c >> 16, k1 >> 8, i2 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_c, depth_slope);
				x_a += b_to_c;
				x_c += c_to_a;
				k1 += grad_b_off;
				i2 += grad_c_off;
				z_c += depth_increment;
				y_c += DrawingArea.width;
				Oa += Va;
				Ob += Vb;
				Oc += Vc;
			}
			while(--y_a >= 0) 
			{
				drawTexturedScanline(DrawingArea.pixels, texture, y_c, x_b >> 16, x_c >> 16, l1 >> 8, i2 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_c, depth_slope);
				x_b += a_to_b;
				x_c += c_to_a;
				l1 += grad_a_off;
				i2 += grad_c_off;
				z_c += depth_increment;
				y_c += DrawingArea.width;
				Oa += Va;
				Ob += Vb;
				Oc += Vc;
			}
			return;
		}
		y_a -= y_b;
		y_b -= y_c;
		y_c = anIntArray1472[y_c];
		while(--y_b >= 0) 
		{
			drawTexturedScanline(DrawingArea.pixels, texture, y_c, x_c >> 16, x_a >> 16, i2 >> 8, k1 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_c, depth_slope);
			x_a += b_to_c;
			x_c += c_to_a;
			k1 += grad_b_off;
			i2 += grad_c_off;
			z_c += depth_increment;
			y_c += DrawingArea.width;
			Oa += Va;
			Ob += Vb;
			Oc += Vc;
		}
		while(--y_a >= 0) 
		{
			drawTexturedScanline(DrawingArea.pixels, texture, y_c, x_c >> 16, x_b >> 16, i2 >> 8, l1 >> 8, Oa, Ob, Oc, Ha, Hb, Hc, z_c, depth_slope);
			x_b += a_to_b;
			x_c += c_to_a;
			l1 += grad_a_off;
			i2 += grad_c_off;
			z_c += depth_increment;
			y_c += DrawingArea.width;
			Oa += Va;
			Ob += Vb;
			Oc += Vc;
		}
	}
	
	public static void drawTexturedScanline(int dest[], int texture[], int dest_off, int start_x, int end_x, int shadeValue, int gradient, int l1, int i2, int j2, int k2, int l2, int i3, float depth, float depth_slope) {
	    int rgb = 0;
	    int loops = 0;
	    if (start_x >= end_x)
	        return;
	    int j3;
	    int k3;
	    if (aBoolean1462) {
	        j3 = (gradient - shadeValue) / (end_x - start_x);
	        if (end_x > DrawingArea.centerX)
	            end_x = DrawingArea.centerX;
	        if (start_x < 0) {
	            shadeValue -= start_x * j3;
	            start_x = 0;
	        }
	        if (start_x >= end_x)
	            return;
	        k3 = end_x - start_x >> 3;
	        j3 <<= 12;
	        shadeValue <<= 9;
	    } else {
	        if (end_x - start_x > 7) {
	            k3 = end_x - start_x >> 3;
	            j3 = (gradient - shadeValue) * anIntArray1468[k3] >> 6;
	        } else {
	            k3 = 0;
	            j3 = 0;
	        }
	        shadeValue <<= 9;
	    }
	    dest_off += start_x;
	    depth += depth_slope * (float) start_x;
	    if (lowMem) {
	        int i4 = 0;
	        int k4 = 0;
	        int k6 = start_x - textureInt1;
	        l1 += (k2 >> 3) * k6;
	        i2 += (l2 >> 3) * k6;
	        j2 += (i3 >> 3) * k6;
	        int i5 = j2 >> 12;
	        if (i5 != 0) {
	            rgb = l1 / i5;
	            loops = i2 / i5;
	            if (rgb < 0)
	                rgb = 0;
	            else
	            if (rgb > 4032)
	                rgb = 4032;
	        }
	        l1 += k2;
	        i2 += l2;
	        j2 += i3;
	        i5 = j2 >> 12;
	        if (i5 != 0) {
	            i4 = l1 / i5;
	            k4 = i2 / i5;
	            if (i4 < 7)
	                i4 = 7;
	            else
	            if (i4 > 4032)
	                i4 = 4032;
	        }
	        int i7 = i4 - rgb >> 3;
	        int k7 = k4 - loops >> 3;
	        rgb += (shadeValue & 0x600000) >> 3;
	        int i8 = shadeValue >> 23;
	        if (aBoolean1463) {
	            while (k3-- > 0) {
	            	for (int i = 0; i < 8; i++) {
		            	if (true) {
		            		dest[dest_off] = texture[(loops & 0xfc0) + (rgb >> 6)] >>> i8;
		            		DrawingArea.depthBuffer[dest_off] = depth;
		            	}
		                dest_off++;
		                depth += depth_slope;
		                rgb += i7;
		                loops += k7;
	            	}
	                rgb = i4;
	                loops = k4;
	                l1 += k2;
	                i2 += l2;
	                j2 += i3;
	                int j5 = j2 >> 12;
	                if (j5 != 0) {
	                    i4 = l1 / j5;
	                    k4 = i2 / j5;
	                    if (i4 < 7)
	                        i4 = 7;
	                    else
	                    if (i4 > 4032)
	                        i4 = 4032;
	                }
	                i7 = i4 - rgb >> 3;
	                k7 = k4 - loops >> 3;
	                shadeValue += j3;
	                rgb += (shadeValue & 0x600000) >> 3;
	                i8 = shadeValue >> 23;
	            }
	            for (k3 = end_x - start_x & 7; k3-- > 0;) {
	            	if (true) {
	            		dest[dest_off] = texture[(loops & 0xfc0) + (rgb >> 6)] >>> i8;
	            		DrawingArea.depthBuffer[dest_off] = depth;
	            	}
	            	dest_off++;
	            	depth += depth_slope;
	                rgb += i7;
	                loops += k7;
	            }

	            return;
	        }
	        while (k3-- > 0) {
	            int k8;
	            for (int i = 0; i < 8; i++) {
		            if ((k8 = texture[(loops & 0xfc0) + (rgb >> 6)] >>> i8) != 0 && true) {
		                dest[dest_off] = k8;
		                DrawingArea.depthBuffer[dest_off] = depth;
		            }
		            dest_off++;
		            depth += depth_slope;
		            rgb += i7;
		            loops += k7;
	            }
	            
	            rgb = i4;
	            loops = k4;
	            l1 += k2;
	            i2 += l2;
	            j2 += i3;
	            int k5 = j2 >> 12;
	            if (k5 != 0) {
	                i4 = l1 / k5;
	                k4 = i2 / k5;
	                if (i4 < 7)
	                    i4 = 7;
	                else
	                if (i4 > 4032)
	                    i4 = 4032;
	            }
	            i7 = i4 - rgb >> 3;
	            k7 = k4 - loops >> 3;
	            shadeValue += j3;
	            rgb += (shadeValue & 0x600000) >> 3;
	            i8 = shadeValue >> 23;
	        }
	        for (k3 = end_x - start_x & 7; k3-- > 0;) {
	            int l8;
	            if ((l8 = texture[(loops & 0xfc0) + (rgb >> 6)] >>> i8) != 0 && true) {
	                dest[dest_off] = l8;
	                DrawingArea.depthBuffer[dest_off] = depth;
	            }
	            dest_off++;
	            depth += depth_slope;
	            rgb += i7;
	            loops += k7;
	        }

	        return;
	    }
	    int j4 = 0;
	    int l4 = 0;
	    int l6 = start_x - textureInt1;
	    l1 += (k2 >> 3) * l6;
	    i2 += (l2 >> 3) * l6;
	    j2 += (i3 >> 3) * l6;
	    int l5 = j2 >> 14;
	    if (l5 != 0) {
	        rgb = l1 / l5;
	        loops = i2 / l5;
	        if (rgb < 0)
	            rgb = 0;
	        else
	        if (rgb > 16256)
	            rgb = 16256;
	    }
	    l1 += k2;
	    i2 += l2;
	    j2 += i3;
	    l5 = j2 >> 14;
	    if (l5 != 0) {
	        j4 = l1 / l5;
	        l4 = i2 / l5;
	        if (j4 < 7)
	            j4 = 7;
	        else
	        if (j4 > 16256)
	            j4 = 16256;
	    }
	    int j7 = j4 - rgb >> 3;
	    int l7 = l4 - loops >> 3;
	    rgb += shadeValue & 0x600000;
	    int j8 = shadeValue >> 23;
	    if (aBoolean1463) {
	        while (k3-- > 0) {
	        	for (int i = 0; i < 8; i++) {
		        	if (true) {
		        		dest[dest_off] = texture[(loops & 0x3f80) + (rgb >> 7)] >>> j8;
		        		DrawingArea.depthBuffer[dest_off] = depth;
		        	}
		        	depth += depth_slope;
		            dest_off++;
		            rgb += j7;
		            loops += l7;
	        	}
	            rgb = j4;
	            loops = l4;
	            l1 += k2;
	            i2 += l2;
	            j2 += i3;
	            int i6 = j2 >> 14;
	            if (i6 != 0) {
	                j4 = l1 / i6;
	                l4 = i2 / i6;
	                if (j4 < 7)
	                    j4 = 7;
	                else
	                if (j4 > 16256)
	                    j4 = 16256;
	            }
	            j7 = j4 - rgb >> 3;
	            l7 = l4 - loops >> 3;
	            shadeValue += j3;
	            rgb += shadeValue & 0x600000;
	            j8 = shadeValue >> 23;
	        }
	        for (k3 = end_x - start_x & 7; k3-- > 0;) {
	        	if (true) {
	        		dest[dest_off] = texture[(loops & 0x3f80) + (rgb >> 7)] >>> j8;
	        		DrawingArea.depthBuffer[dest_off] = depth;
	        	}
	            dest_off++;
	            depth += depth_slope;
	            rgb += j7;
	            loops += l7;
	        }

	        return;
	    }
	    while (k3-- > 0) {
	        int i9;
	        for (int i = 0; i < 8; i++) {
		        if ((i9 = texture[(loops & 0x3f80) + (rgb >> 7)] >>> j8) != 0 && true) {
		            dest[dest_off] = i9;
		            DrawingArea.depthBuffer[dest_off] = depth;
		        }
		        dest_off++;
		        depth += depth_slope;
		        rgb += j7;
		        loops += l7;
	        }
	        rgb = j4;
	        loops = l4;
	        l1 += k2;
	        i2 += l2;
	        j2 += i3;
	        int j6 = j2 >> 14;
	        if (j6 != 0) {
	            j4 = l1 / j6;
	            l4 = i2 / j6;
	            if (j4 < 7)
	                j4 = 7;
	            else
	            if (j4 > 16256)
	                j4 = 16256;
	        }
	        j7 = j4 - rgb >> 3;
	        l7 = l4 - loops >> 3;
	        shadeValue += j3;
	        rgb += shadeValue & 0x600000;
	        j8 = shadeValue >> 23;
	    }
	    for (int l3 = end_x - start_x & 7; l3-- > 0;) {
	        int j9;
	        if ((j9 = texture[(loops & 0x3f80) + (rgb >> 7)] >>> j8) != 0 && true) {
	            dest[dest_off] = j9;
	            DrawingArea.depthBuffer[dest_off] = depth;
	        }
	        depth += depth_slope;
	        dest_off++;
	        rgb += j7;
	        loops += l7;
	    }
	}
	
	public static void drawDepthTriangle(int x_a, int x_b, int x_c, int y_a, int y_b, int y_c, float z_a, float z_b, float z_c) {
		int a_to_b = 0;
		if (y_b != y_a) {
			a_to_b = (x_b - x_a << 16) / (y_b - y_a);
		}
		int b_to_c = 0;
		if (y_c != y_b) {
			b_to_c = (x_c - x_b << 16) / (y_c - y_b);
		}
		int c_to_a = 0;
		if (y_c != y_a) {
			c_to_a = (x_a - x_c << 16) / (y_a - y_c);
		}

		float b_aX = x_b - x_a;
		float b_aY = y_b - y_a;
		float c_aX = x_c - x_a;
		float c_aY = y_c - y_a;
		float b_aZ = z_b - z_a;
		float c_aZ = z_c - z_a;

		float div = b_aX * c_aY - c_aX * b_aY;
		float depth_slope = (b_aZ * c_aY - c_aZ * b_aY) / div;
		float depth_increment = (c_aZ * b_aX - b_aZ * c_aX) / div;
		if (y_a <= y_b && y_a <= y_c) {
			if (y_a < DrawingArea.bottomY) {
				if (y_b > DrawingArea.bottomY)
					y_b = DrawingArea.bottomY;
				if (y_c > DrawingArea.bottomY)
					y_c = DrawingArea.bottomY;
				z_a = z_a - depth_slope * x_a + depth_slope;
				if (y_b < y_c) {
					x_c = x_a <<= 16;
					if (y_a < 0) {
						x_c -= c_to_a * y_a;
						x_a -= a_to_b * y_a;
						z_a -= depth_increment * y_a;
						y_a = 0;
					}
					x_b <<= 16;
					if (y_b < 0) {
						x_b -= b_to_c * y_b;
						y_b = 0;
					}
					if (y_a != y_b && c_to_a < a_to_b || y_a == y_b && c_to_a > b_to_c) {
						y_c -= y_b;
						y_b -= y_a;
						y_a = anIntArray1472[y_a];
						while (--y_b >= 0) {
							drawDepthTriangleScanline(y_a, x_c >> 16, x_a >> 16, z_a, depth_slope);
							x_c += c_to_a;
							x_a += a_to_b;
							z_a += depth_increment;
							y_a += DrawingArea.width;
						}
						while (--y_c >= 0) {
							drawDepthTriangleScanline(y_a, x_c >> 16, x_b >> 16, z_a, depth_slope);
							x_c += c_to_a;
							x_b += b_to_c;
							z_a += depth_increment;
							y_a += DrawingArea.width;
						}
					} else {
						y_c -= y_b;
						y_b -= y_a;
						y_a = anIntArray1472[y_a];
						while (--y_b >= 0) {
							drawDepthTriangleScanline(y_a, x_a >> 16, x_c >> 16, z_a, depth_slope);
							x_c += c_to_a;
							x_a += a_to_b;
							z_a += depth_increment;
							y_a += DrawingArea.width;
						}
						while (--y_c >= 0) {
							drawDepthTriangleScanline(y_a, x_b >> 16, x_c >> 16, z_a, depth_slope);
							x_c += c_to_a;
							x_b += b_to_c;
							z_a += depth_increment;
							y_a += DrawingArea.width;
						}
					}
				} else {
					x_b = x_a <<= 16;
					if (y_a < 0) {
						x_b -= c_to_a * y_a;
						x_a -= a_to_b * y_a;
						z_a -= depth_increment * y_a;
						y_a = 0;
					}
					x_c <<= 16;
					if (y_c < 0) {
						x_c -= b_to_c * y_c;
						y_c = 0;
					}
					if (y_a != y_c && c_to_a < a_to_b || y_a == y_c && b_to_c > a_to_b) {
						y_b -= y_c;
						y_c -= y_a;
						y_a = anIntArray1472[y_a];
						while (--y_c >= 0) {
							drawDepthTriangleScanline(y_a, x_b >> 16, x_a >> 16, z_a, depth_slope);
							x_b += c_to_a;
							x_a += a_to_b;
							z_a += depth_increment;
							y_a += DrawingArea.width;
						}
						while (--y_b >= 0) {
							drawDepthTriangleScanline(y_a, x_c >> 16, x_a >> 16, z_a, depth_slope);
							x_c += b_to_c;
							x_a += a_to_b;
							z_a += depth_increment;
							y_a += DrawingArea.width;
						}
					} else {
						y_b -= y_c;
						y_c -= y_a;
						y_a = anIntArray1472[y_a];
						while (--y_c >= 0) {
							drawDepthTriangleScanline(y_a, x_a >> 16, x_b >> 16, z_a, depth_slope);
							x_b += c_to_a;
							x_a += a_to_b;
							z_a += depth_increment;
							y_a += DrawingArea.width;
						}
						while (--y_b >= 0) {
							drawDepthTriangleScanline(y_a, x_a >> 16, x_c >> 16, z_a, depth_slope);
							x_c += b_to_c;
							x_a += a_to_b;
							z_a += depth_increment;
							y_a += DrawingArea.width;
						}
					}
				}
			}
		} else if (y_b <= y_c) {
			if (y_b < DrawingArea.bottomY) {
				if (y_c > DrawingArea.bottomY)
					y_c = DrawingArea.bottomY;
				if (y_a > DrawingArea.bottomY)
					y_a = DrawingArea.bottomY;
				z_b = z_b - depth_slope * x_b + depth_slope;
				if (y_c < y_a) {
					x_a = x_b <<= 16;
					if (y_b < 0) {
						x_a -= a_to_b * y_b;
						x_b -= b_to_c * y_b;
						z_b -= depth_increment * y_b;
						y_b = 0;
					}
					x_c <<= 16;
					if (y_c < 0) {
						x_c -= c_to_a * y_c;
						y_c = 0;
					}
					if (y_b != y_c && a_to_b < b_to_c || y_b == y_c && a_to_b > c_to_a) {
						y_a -= y_c;
						y_c -= y_b;
						y_b = anIntArray1472[y_b];
						while (--y_c >= 0) {
							drawDepthTriangleScanline(y_b, x_a >> 16, x_b >> 16, z_b, depth_slope);
							x_a += a_to_b;
							x_b += b_to_c;
							z_b += depth_increment;
							y_b += DrawingArea.width;
						}
						while (--y_a >= 0) {
							drawDepthTriangleScanline(y_b, x_a >> 16, x_c >> 16, z_b, depth_slope);
							x_a += a_to_b;
							x_c += c_to_a;
							z_b += depth_increment;
							y_b += DrawingArea.width;
						}
					} else {
						y_a -= y_c;
						y_c -= y_b;
						y_b = anIntArray1472[y_b];
						while (--y_c >= 0) {
							drawDepthTriangleScanline(y_b, x_b >> 16, x_a >> 16, z_b, depth_slope);
							x_a += a_to_b;
							x_b += b_to_c;
							z_b += depth_increment;
							y_b += DrawingArea.width;
						}
						while (--y_a >= 0) {
							drawDepthTriangleScanline(y_b, x_c >> 16, x_a >> 16, z_b, depth_slope);
							x_a += a_to_b;
							x_c += c_to_a;
							z_b += depth_increment;
							y_b += DrawingArea.width;
						}
					}
				} else {
					x_c = x_b <<= 16;
					if (y_b < 0) {
						x_c -= a_to_b * y_b;
						x_b -= b_to_c * y_b;
						z_b -= depth_increment * y_b;
						y_b = 0;
					}
					x_a <<= 16;
					if (y_a < 0) {
						x_a -= c_to_a * y_a;
						y_a = 0;
					}
					if (a_to_b < b_to_c) {
						y_c -= y_a;
						y_a -= y_b;
						y_b = anIntArray1472[y_b];
						while (--y_a >= 0) {
							drawDepthTriangleScanline(y_b, x_c >> 16, x_b >> 16, z_b, depth_slope);
							x_c += a_to_b;
							x_b += b_to_c;
							z_b += depth_increment;
							y_b += DrawingArea.width;
						}
						while (--y_c >= 0) {
							drawDepthTriangleScanline(y_b, x_a >> 16, x_b >> 16, z_b, depth_slope);
							x_a += c_to_a;
							x_b += b_to_c;
							z_b += depth_increment;
							y_b += DrawingArea.width;
						}
					} else {
						y_c -= y_a;
						y_a -= y_b;
						y_b = anIntArray1472[y_b];
						while (--y_a >= 0) {
							drawDepthTriangleScanline(y_b, x_b >> 16, x_c >> 16, z_b, depth_slope);
							x_c += a_to_b;
							x_b += b_to_c;
							z_b += depth_increment;
							y_b += DrawingArea.width;
						}
						while (--y_c >= 0) {
							drawDepthTriangleScanline(y_b, x_b >> 16, x_a >> 16, z_b, depth_slope);
							x_a += c_to_a;
							x_b += b_to_c;
							z_b += depth_increment;
							y_b += DrawingArea.width;
						}
					}
				}
			}
		} else if (y_c < DrawingArea.bottomY) {
			if (y_a > DrawingArea.bottomY)
				y_a = DrawingArea.bottomY;
			if (y_b > DrawingArea.bottomY)
				y_b = DrawingArea.bottomY;
			z_c = z_c - depth_slope * x_c + depth_slope;
			if (y_a < y_b) {
				x_b = x_c <<= 16;
				if (y_c < 0) {
					x_b -= b_to_c * y_c;
					x_c -= c_to_a * y_c;
					z_c -= depth_increment * y_c;
					y_c = 0;
				}
				x_a <<= 16;
				if (y_a < 0) {
					x_a -= a_to_b * y_a;
					y_a = 0;
				}
				if (b_to_c < c_to_a) {
					y_b -= y_a;
					y_a -= y_c;
					y_c = anIntArray1472[y_c];
					while (--y_a >= 0) {
						drawDepthTriangleScanline(y_c, x_b >> 16, x_c >> 16, z_c, depth_slope);
						x_b += b_to_c;
						x_c += c_to_a;
						z_c += depth_increment;
						y_c += DrawingArea.width;
					}
					while (--y_b >= 0) {
						drawDepthTriangleScanline(y_c, x_b >> 16, x_a >> 16, z_c, depth_slope);
						x_b += b_to_c;
						x_a += a_to_b;
						z_c += depth_increment;
						y_c += DrawingArea.width;
					}
				} else {
					y_b -= y_a;
					y_a -= y_c;
					y_c = anIntArray1472[y_c];
					while (--y_a >= 0) {
						drawDepthTriangleScanline(y_c, x_c >> 16, x_b >> 16, z_c, depth_slope);
						x_b += b_to_c;
						x_c += c_to_a;
						z_c += depth_increment;
						y_c += DrawingArea.width;
					}
					while (--y_b >= 0) {
						drawDepthTriangleScanline(y_c, x_a >> 16, x_b >> 16, z_c, depth_slope);
						x_b += b_to_c;
						x_a += a_to_b;
						z_c += depth_increment;
						y_c += DrawingArea.width;
					}
				}
			} else {
				x_a = x_c <<= 16;
				if (y_c < 0) {
					x_a -= b_to_c * y_c;
					x_c -= c_to_a * y_c;
					z_c -= depth_increment * y_c;
					y_c = 0;
				}
				x_b <<= 16;
				if (y_b < 0) {
					x_b -= a_to_b * y_b;
					y_b = 0;
				}
				if (b_to_c < c_to_a) {
					y_a -= y_b;
					y_b -= y_c;
					y_c = anIntArray1472[y_c];
					while (--y_b >= 0) {
						drawDepthTriangleScanline(y_c, x_a >> 16, x_c >> 16, z_c, depth_slope);
						x_a += b_to_c;
						x_c += c_to_a;
						z_c += depth_increment;
						y_c += DrawingArea.width;
					}
					while (--y_a >= 0) {
						drawDepthTriangleScanline(y_c, x_b >> 16, x_c >> 16, z_c, depth_slope);
						x_b += a_to_b;
						x_c += c_to_a;
						z_c += depth_increment;
						y_c += DrawingArea.width;
					}
				} else {
					y_a -= y_b;
					y_b -= y_c;
					y_c = anIntArray1472[y_c];
					while (--y_b >= 0) {
						drawDepthTriangleScanline(y_c, x_c >> 16, x_a >> 16, z_c, depth_slope);
						x_a += b_to_c;
						x_c += c_to_a;
						z_c += depth_increment;
						y_c += DrawingArea.width;
					}
					while (--y_a >= 0) {
						drawDepthTriangleScanline(y_c, x_c >> 16, x_b >> 16, z_c, depth_slope);
						x_b += a_to_b;
						x_c += c_to_a;
						z_c += depth_increment;
						y_c += DrawingArea.width;
					}
				}
			}
		}
	}

	private static void drawDepthTriangleScanline(int dest_off, int start_x, int end_x, float depth, float depth_slope) {
		int dbl = DrawingArea.depthBuffer.length;
		if (aBoolean1462) {
			if (end_x > DrawingArea.width) {
				end_x = DrawingArea.width;
			}
			if (start_x < 0) {
				start_x = 0;
			}
		}
		if (start_x >= end_x) {
			return;
		}
		dest_off += start_x - 1;
		int loops = end_x - start_x >> 2;
		depth += depth_slope * (float) start_x;
		if (anInt1465 == 0) {
			while (--loops >= 0) {
				dest_off++;
				if (dest_off >= 0 && dest_off < dbl && true) {
					DrawingArea.depthBuffer[dest_off] = depth;
				}
				depth += depth_slope;
				dest_off++;
				if (dest_off >= 0 && dest_off < dbl && true) {
					DrawingArea.depthBuffer[dest_off] = depth;
				}
				depth += depth_slope;
				dest_off++;
				if (dest_off >= 0 && dest_off < dbl && true) {
					DrawingArea.depthBuffer[dest_off] = depth;
				}
				depth += depth_slope;
				dest_off++;
				if (dest_off >= 0 && dest_off < dbl && true) {
					DrawingArea.depthBuffer[dest_off] = depth;
				}
				depth += depth_slope;
			}
			for (loops = end_x - start_x & 3; --loops >= 0;) {
				dest_off++;
				if (dest_off >= 0 && dest_off < dbl && true) {
					DrawingArea.depthBuffer[dest_off] = depth;
				}
				depth += depth_slope;
			}
			return;
		}
		while (--loops >= 0) {
			dest_off++;
			if (dest_off >= 0 && dest_off < dbl && true) {
				DrawingArea.depthBuffer[dest_off] = depth;
			}
			depth += depth_slope;
			dest_off++;
			if (dest_off >= 0 && dest_off < dbl && true) {
				DrawingArea.depthBuffer[dest_off] = depth;
			}
			depth += depth_slope;
			dest_off++;
			if (dest_off >= 0 && dest_off < dbl && true) {
				DrawingArea.depthBuffer[dest_off] = depth;
			}
			depth += depth_slope;
			dest_off++;
			if (dest_off >= 0 && dest_off < dbl && true) {
				DrawingArea.depthBuffer[dest_off] = depth;
			}
			depth += depth_slope;
		}
		for (loops = end_x - start_x & 3; --loops >= 0;) {
			dest_off++;
			if (dest_off >= 0 && dest_off < dbl && true) {
				DrawingArea.depthBuffer[dest_off] = depth;
			}
			depth += depth_slope;
		}
	}

	public static boolean lowMem = true;
	public static boolean aBoolean1462;
	private static boolean aBoolean1463;
	public static boolean aBoolean1464 = true;
	public static int anInt1465;
	public static int textureInt1;
	public static int textureInt2;
	private static int[] anIntArray1468;
	public static final int[] anIntArray1469;
	public static int anIntArray1470[];
	public static int anIntArray1471[];
	public static int anIntArray1472[];
	private static int anInt1473;
	public static Background aBackgroundArray1474s[] = new Background[51];
	private static boolean[] aBooleanArray1475 = new boolean[51];
	private static int[] anIntArray1476 = new int[51];
	private static int anInt1477;
	private static int[][] anIntArrayArray1478;
	private static int[][] anIntArrayArray1479 = new int[51][];
	public static int anIntArray1480[] = new int[51];
	public static int anInt1481;
	public static int anIntArray1482[] = new int[0x10000];
	private static int[][] anIntArrayArray1483 = new int[51][];

	static {
		anIntArray1468 = new int[512];
		anIntArray1469 = new int[2048];
		anIntArray1470 = new int[2048];
		anIntArray1471 = new int[2048];
		for(int i = 1; i < 512; i++) {
			anIntArray1468[i] = 32768 / i;
		}
		for(int j = 1; j < 2048; j++) {
			anIntArray1469[j] = 0x10000 / j;
		}
		for(int k = 0; k < 2048; k++) {
			anIntArray1470[k] = (int)(65536D * Math.sin((double)k * 0.0030679614999999999D));
			anIntArray1471[k] = (int)(65536D * Math.cos((double)k * 0.0030679614999999999D));
		}
	}
}