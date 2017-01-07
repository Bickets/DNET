
public final class NEDrawingArea extends DrawingArea {

	public static void drawRectNE(int x, int y, int w, int h, int color) {
		drawHorizontalLineNE(x, y, w, color);
		drawHorizontalLineNE(x, y + h - 1, w, color);
		drawVerticalLineNE(x, y, h, color);
		drawVerticalLineNE(x + w - 1, y, h, color);
	}

	public static void drawHorizontalLineNE(int x, int y, int lineWidth, int color) {
		if (y >= topY && y < bottomY) {
			if (x < topX) {
				lineWidth -= topX - x;
				x = topX;
			}
			if (x + lineWidth > bottomX) {
				lineWidth = bottomX - x;
			}
			int var4 = x + y * width;
			for (int widthPixel = 0; widthPixel < lineWidth; ++widthPixel) {
				pixels[var4 + widthPixel] = color;
			}
		}
	}

	static void drawVerticalLineNE(int x, int y, int lineHeight, int color) {
		if (x >= topX && x < bottomX) {
			if (y < topY) {
				lineHeight -= topY - y;
				y = topY;
			}
			if (y + lineHeight > bottomY) {
				lineHeight = bottomY - y;
			}
			int var4 = x + y * width;
			for (int var5 = 0; var5 < lineHeight; ++var5) {
				pixels[var4 + var5 * width] = color;
			}
		}
	}

	static void fillRectAlphaNE(int x, int y, int width, int height, int var4, int var5) {
		if (x < topX) {
			width -= topX - x;
			x = topX;
		}
		if (y < topY) {
			height -= topY - y;
			y = topY;
		}
		if (x + width > bottomX) {
			width = bottomX - x;
		}
		if (y + height > bottomY) {
			height = bottomY - y;
		}
		var4 = ((var4 & 16711935) * var5 >> 8 & 16711935) + ((var4 & '\uff00') * var5 >> 8 & '\uff00');
		int var6 = 256 - var5;
		int var7 = NEDrawingArea.width - width;
		int var8 = x + y * NEDrawingArea.width;

		for (int var9 = 0; var9 < height; ++var9) {
			for (int var10 = -width; var10 < 0; ++var10) {
				int var11 = pixels[var8];
				var11 = ((var11 & 16711935) * var6 >> 8 & 16711935) + ((var11 & '\uff00') * var6 >> 8 & '\uff00');
				pixels[var8++] = var4 + var11;
			}

			var8 += var7;
		}
	}

	static void drawRectAlphaNE(int var0, int var1, int var2, int var3, int var4, int var5) {
		drawHorizontalLineAlphaNE(var0, var1, var2, var4, var5);
		drawHorizontalLineAlphaNE(var0, var1 + var3 - 1, var2, var4, var5);
		if (var3 >= 3) {
			drawVerticalLineAlphaNE(var0, var1 + 1, var3 - 2, var4, var5);
			drawVerticalLineAlphaNE(var0 + var2 - 1, var1 + 1, var3 - 2, var4, var5);
		}
	}

	public static final void drawHorizontalLineAlphaNE(int x, int y, int lineWidth, int color, int alpha) {
		if (y >= topY && y < bottomY) {
			if (x < topX) {
				lineWidth -= topX - x;
				x = topX;
			}
			if (x + lineWidth > bottomX) {
				lineWidth = bottomX - x;
			}
			int var5 = 256 - alpha;
			int var6 = (color >> 16 & 255) * alpha;
			int var7 = (color >> 8 & 255) * alpha;
			int var8 = (color & 255) * alpha;
			int var12 = x + y * width;
			for (int var13 = 0; var13 < lineWidth; ++var13) {
				int var9 = (pixels[var12] >> 16 & 255) * var5;
				int var10 = (pixels[var12] >> 8 & 255) * var5;
				int var11 = (pixels[var12] & 255) * var5;
				int var14 = (var6 + var9 >> 8 << 16) + (var7 + var10 >> 8 << 8) + (var8 + var11 >> 8);
				pixels[var12++] = var14;
			}
		}
	}

	public static void drawVerticalLineAlphaNE(int x, int y, int lineHeight, int color, int alpha) {
		if (x >= topX && x < bottomX) {
			if (y < topY) {
				lineHeight -= topY - y;
				y = topY;
			}
			if (y + lineHeight > bottomY) {
				lineHeight = bottomY - y;
			}
			int var5 = 256 - alpha;
			int var6 = (color >> 16 & 255) * alpha;
			int var7 = (color >> 8 & 255) * alpha;
			int var8 = (color & 255) * alpha;
			int var12 = x + y * width;
			for (int var13 = 0; var13 < lineHeight; ++var13) {
				int var9 = (pixels[var12] >> 16 & 255) * var5;
				int var10 = (pixels[var12] >> 8 & 255) * var5;
				int var11 = (pixels[var12] & 255) * var5;
				int var14 = (var6 + var9 >> 8 << 16) + (var7 + var10 >> 8 << 8) + (var8 + var11 >> 8);
				pixels[var12] = var14;
				var12 += width;
			}
		}
	}

	static final void setDimensions(int[] dimen) {
		topX = dimen[0];
		topY = dimen[1];
		bottomX = dimen[2];
		bottomY = dimen[3];
	}

	static final void saveDimensions(int[] var0) {
		var0[0] = topX;
		var0[1] = topY;
		var0[2] = bottomX;
		var0[3] = bottomY;
	}

	static final void setDrawingAreaNE(int[] pixels, int w, int h) {
		NEDrawingArea.pixels = pixels;
		width = w;
		height = h;
		setDrawingAreaSizeNE(0, 0, w, h);
	}

	static final void setDrawingAreaSizeNE(int x, int y, int w, int h) {
		if (x < 0) {
			x = 0;
		}
		if (y < 0) {
			y = 0;
		}
		if (w > width) {
			w = width;
		}
		if (h > height) {
			h = height;
		}
		topX = x;
		topY = y;
		bottomX = w;
		bottomY = h;
	}

	static final void resizeNE(int var0, int var1, int var2, int var3) {
		if (topX < var0) {
			topX = var0;
		}
		if (topY < var1) {
			topY = var1;
		}
		if (bottomX > var2) {
			bottomX = var2;
		}
		if (bottomY > var3) {
			bottomY = var3;
		}
	}

	static final void clearNE() {
		int var0 = 0;
		int pixeltoclear;
		for (pixeltoclear = width * height - 7; var0 < pixeltoclear; pixels[var0++] = 0) {
			pixels[var0++] = 0;
			pixels[var0++] = 0;
			pixels[var0++] = 0;
			pixels[var0++] = 0;
			pixels[var0++] = 0;
			pixels[var0++] = 0;
			pixels[var0++] = 0;
		}
		for (pixeltoclear += 7; var0 < pixeltoclear; pixels[var0++] = 0) {
			;
		}
	}

	public static void destroyNE() {
		pixels = null;
	}

	static final void fillRectNE(int x, int y, int rectWidth, int rectHeight, int color) {
		if (x < topX) {
			rectWidth -= topX - x;
			x = topX;
		}
		if (y < topY) {
			rectHeight -= topY - y;
			y = topY;
		}
		if (x + rectWidth > bottomX) {
			rectWidth = bottomX - x;
		}
		if (y + rectHeight > bottomY) {
			rectHeight = bottomY - y;
		}
		int var5 = width - rectWidth;
		int nextPixel = x + y * width;
		for (int var7 = -rectHeight; var7 < 0; ++var7) {
			for (int var8 = -rectWidth; var8 < 0; ++var8) {
				pixels[nextPixel++] = color;
			}
			nextPixel += var5;
		}
	}

	static final void drawDiagonalLineNE(int x, int y, int DestX, int destY, int linecolor) {
		DestX -= x;
		destY -= y;
		if (destY == 0) {
			if (DestX >= 0) {
				drawHorizontalLineNE(x, y, DestX + 1, linecolor);
			} else {
				drawHorizontalLineNE(x + DestX, y, -DestX + 1, linecolor);
			}
		} else if (DestX == 0) {
			if (destY >= 0) {
				drawVerticalLineNE(x, y, destY + 1, linecolor);
			} else {
				drawVerticalLineNE(x, y + destY, -destY + 1, linecolor);
			}
		} else {
			if (DestX + destY < 0) {
				x += DestX;
				DestX = -DestX;
				y += destY;
				destY = -destY;
			}
			int var5;
			int var6;
			if (DestX > destY) {
				y <<= 16;
				y += '\u8000';
				destY <<= 16;
				var5 = (int) Math.floor((double) destY / (double) DestX + 0.5D);
				DestX += x;
				if (x < topX) {
					y += var5 * (topX - x);
					x = topX;
				}
				if (DestX >= bottomX) {
					DestX = bottomX - 1;
				}
				while (x <= DestX) {
					var6 = y >> 16;
					if (var6 >= topY && var6 < bottomY) {
						pixels[x + var6 * width] = linecolor;
					}
					y += var5;
					++x;
				}
			} else {
				x <<= 16;
				x += '\u8000';
				DestX <<= 16;
				var5 = (int) Math.floor((double) DestX / (double) destY + 0.5D);
				destY += y;
				if (y < topY) {
					x += var5 * (topY - y);
					y = topY;
				}
				if (destY >= bottomY) {
					destY = bottomY - 1;
				}
				while (y <= destY) {
					var6 = x >> 16;
					if (var6 >= topX && var6 < bottomX) {
						pixels[var6 + y * width] = linecolor;
					}
					x += var5;
					++y;
				}
			}
		}
	}

	public static final void drawCircleNE(int x, int y, int radius, int color) {
		if (radius == 0) {
			drawPixelNE(x, y, color);
		} else {
			if (radius < 0) {
				radius = -radius;
			}
			int var4 = y - radius;
			if (var4 < topY) {
				var4 = topY;
			}
			int var5 = y + radius + 1;
			if (var5 > bottomY) {
				var5 = bottomY;
			}
			int var6 = var4;
			int var7 = radius * radius;
			int var8 = 0;
			int var9 = y - var4;
			int var10 = var9 * var9;
			int var11 = var10 - var9;
			if (y > var5) {
				y = var5;
			}
			int var12;
			int var13;
			int var14;
			int var15;
			while (var6 < y) {
				while (var11 <= var7 || var10 <= var7) {
					var10 += var8 + var8;
					var11 += var8++ + var8;
				}
				var12 = x - var8 + 1;
				if (var12 < topX) {
					var12 = topX;
				}
				var13 = x + var8;
				if (var13 > bottomX) {
					var13 = bottomX;
				}
				var14 = var12 + var6 * width;
				for (var15 = var12; var15 < var13; ++var15) {
					pixels[var14++] = color;
				}
				++var6;
				var10 -= var9-- + var9;
				var11 -= var9 + var9;
			}
			var8 = radius;
			var9 = var6 - y;
			var11 = var9 * var9 + var7;
			var10 = var11 - radius;
			for (var11 -= var9; var6 < var5; var10 += var9++ + var9) {
				while (var11 > var7 && var10 > var7) {
					var11 -= var8-- + var8;
					var10 -= var8 + var8;
				}
				var12 = x - var8;
				if (var12 < topX) {
					var12 = topX;
				}
				var13 = x + var8;
				if (var13 > bottomX - 1) {
					var13 = bottomX - 1;
				}
				var14 = var12 + var6 * width;
				for (var15 = var12; var15 <= var13; ++var15) {
					pixels[var14++] = color;
				}
				++var6;
				var11 += var9 + var9;
			}
		}
	}

	static final void drawCircleAlphaNE(int x, int y, int radius, int color, int alpha) {
		if (alpha != 0) {
			if (alpha == 256) {
				drawCircleNE(x, y, radius, color);
			} else {
				if (radius < 0) {
					radius = -radius;
				}
				int var5 = 256 - alpha;
				int var6 = (color >> 16 & 255) * alpha;
				int var7 = (color >> 8 & 255) * alpha;
				int var8 = (color & 255) * alpha;
				int var12 = y - radius;
				if (var12 < topY) {
					var12 = topY;
				}
				int var13 = y + radius + 1;
				if (var13 > bottomY) {
					var13 = bottomY;
				}
				int var14 = var12;
				int var15 = radius * radius;
				int var16 = 0;
				int var17 = y - var12;
				int var18 = var17 * var17;
				int var19 = var18 - var17;
				if (y > var13) {
					y = var13;
				}
				int var9;
				int var10;
				int var11;
				int var21;
				int var20;
				int var23;
				int var22;
				int var24;
				while (var14 < y) {
					while (var19 <= var15 || var18 <= var15) {
						var18 += var16 + var16;
						var19 += var16++ + var16;
					}
					var20 = x - var16 + 1;
					if (var20 < topX) {
						var20 = topX;
					}
					var21 = x + var16;
					if (var21 > bottomX) {
						var21 = bottomX;
					}
					var22 = var20 + var14 * width;
					for (var23 = var20; var23 < var21; ++var23) {
						var9 = (pixels[var22] >> 16 & 255) * var5;
						var10 = (pixels[var22] >> 8 & 255) * var5;
						var11 = (pixels[var22] & 255) * var5;
						var24 = (var6 + var9 >> 8 << 16) + (var7 + var10 >> 8 << 8) + (var8 + var11 >> 8);
						pixels[var22++] = var24;
					}
					++var14;
					var18 -= var17-- + var17;
					var19 -= var17 + var17;
				}
				var16 = radius;
				var17 = -var17;
				var19 = var17 * var17 + var15;
				var18 = var19 - radius;
				for (var19 -= var17; var14 < var13; var18 += var17++ + var17) {
					while (var19 > var15 && var18 > var15) {
						var19 -= var16-- + var16;
						var18 -= var16 + var16;
					}
					var20 = x - var16;
					if (var20 < topX) {
						var20 = topX;
					}
					var21 = x + var16;
					if (var21 > bottomX - 1) {
						var21 = bottomX - 1;
					}
					var22 = var20 + var14 * width;
					for (var23 = var20; var23 <= var21; ++var23) {
						var9 = (pixels[var22] >> 16 & 255) * var5;
						var10 = (pixels[var22] >> 8 & 255) * var5;
						var11 = (pixels[var22] & 255) * var5;
						var24 = (var6 + var9 >> 8 << 16) + (var7 + var10 >> 8 << 8) + (var8 + var11 >> 8);
						pixels[var22++] = var24;
					}
					++var14;
					var19 += var17 + var17;
				}
			}
		}
	}

	private static final void drawPixelNE(int x, int y, int color) {
		if (x >= topX && y >= topY && x < bottomX && y < bottomY) {
			pixels[x + y * width] = color;
		}
	}
}
