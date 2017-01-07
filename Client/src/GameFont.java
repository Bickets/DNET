import java.awt.*;
import java.util.Random;

public class GameFont extends DrawingArea {

	public GameFont(boolean monospace, String s, StreamLoader archive) {
		glyphPixels = new byte[256][];
		glyphWidth = new int[256];
		glyphHeight = new int[256];
		horizontalKerning = new int[256];
		verticalKerning = new int[256];
		glyphDisplayWidth = new int[256];
		Stream data = new Stream(archive.getDataForName(s + ".dat"));
		Stream index = new Stream(archive.getDataForName("index.dat"));
		index.currentOffset = data.readUnsignedWord() + 4;
		int k = index.readUnsignedByte();
		if (k > 0) {
			index.currentOffset += 3 * (k - 1);
		}
		for (int character = 0; character < 256; character++) {
			horizontalKerning[character] = index.readUnsignedByte();
			verticalKerning[character] = index.readUnsignedByte();
			int width = glyphWidth[character] = index.readUnsignedWord();
			int height = glyphHeight[character] = index.readUnsignedWord();
			int shape = index.readUnsignedByte();
			int imageSize = width * height;
			glyphPixels[character] = new byte[imageSize];
			if (shape == 0) {
				for (int p = 0; p < imageSize; p++) {
					glyphPixels[character][p] = data.readSignedByte();
				}

			} else if (shape == 1) {
				for (int w = 0; w < width; w++) {
					for (int h = 0; h < height; h++) {
						glyphPixels[character][w + h * width] = data.readSignedByte();
					}

				}

			}
			if (height > baseCharHeight && character < 128) {
				baseCharHeight = height;
			}
			horizontalKerning[character] = 1;
			glyphDisplayWidth[character] = width + 2;
			int k2 = 0;
			for (int i3 = height / 7; i3 < height; i3++) {
				k2 += glyphPixels[character][i3 * width];
			}

			if (k2 <= height / 7) {
				glyphDisplayWidth[character]--;
				horizontalKerning[character] = 0;
			}
			k2 = 0;
			for (int j3 = height / 7; j3 < height; j3++) {
				k2 += glyphPixels[character][(width - 1) + j3 * width];
			}

			if (k2 <= height / 7) {
				glyphDisplayWidth[character]--;
			}
		}

		if (monospace) {
			glyphDisplayWidth[32] = glyphDisplayWidth[73];
		} else {
			glyphDisplayWidth[32] = glyphDisplayWidth[105];
		}
	}

	private final void setEffectsAlpha(int color, int shadow, int opac) {
		strikethroughColor = -1;
		underlineColor = -1;
		defaultShadowColor = shadow;
		shadowColor = shadow;
		defaultTextColor = color;
		textColor = color;
		defaultOpacity = opac;
		opacity = opac;
		whiteSpace = 0;
		anInt3748 = 0;
	}

	public final void drawBasicString(String string, int x, int y) {
		y -= baseCharHeight;
		int effectIndex = -1;
		int textLength = string.length();

		for (int character = 0; character < textLength; ++character) {
			int c = string.charAt(character);
			if (c > 255) {
				c = 32;
			}
			if (c == 60) {
				effectIndex = character;
			} else {
				if (c == 62 && effectIndex != -1) {
					String effectString = string.substring(effectIndex + 1, character);
					effectIndex = -1;
					if (effectString.equals(lessThan)) {
						c = 60;
					} else if (effectString.equals(greaterThan)) {
						c = 62;
					} else if (effectString.equals(nonBreakingSpace)) {
						c = 160;
					} else if (effectString.equals(softHyphen)) {
						c = 173;
					} else if (effectString.equals(multiplicationSymbol)) {
						c = 215;
					} else if (effectString.equals(euroSymbol)) {
						c = 128;
					} else if (effectString.equals(copyright)) {
						c = 169;
					} else {
						if (!effectString.equals(registeredTrademark)) {
							if (effectString.startsWith(image, 0)) {
								try {
									int icon = Integer.valueOf(effectString.substring(4));
									Sprite modIcon = modIcons[icon];
									int imageHeight = imageWidths != null ? imageWidths[icon] : modIcon.anInt1445;
									if (opacity == 256) {
										modIcon.drawSprite(x, y + baseCharHeight - imageHeight);
									} else {
										modIcon.drawSprite1(x, y + baseCharHeight - imageHeight, opacity);
									}

									x += modIcon.anInt1444;
								} catch (Exception e) {

								}
							} else {
								parseStringForEffects(effectString);
							}
							continue;
						}
						c = 174;
					}
				}

				if (effectIndex == -1) {
					int width = glyphWidth[c];
					int height = glyphHeight[c];
					if (c != 32) {
						if (opacity == 256) {
							if (shadowColor != -1) {
								drawCharacter(c, x + horizontalKerning[c] + 1, y + verticalKerning[c] + 1, width,
										height, shadowColor, true);
							}
							drawCharacter(c, x + horizontalKerning[c], y + verticalKerning[c], width, height, textColor,
									false);
						} else {
							if (shadowColor != -1) {
								drawCharacterAlpha(c, x + horizontalKerning[c] + 1, y + verticalKerning[c] + 1, width,
										height, shadowColor, opacity, true);
							}
							drawCharacterAlpha(c, x + horizontalKerning[c], y + verticalKerning[c], width, height,
									textColor, opacity, false);
						}
					} else if (whiteSpace > 0) {
						anInt3748 += whiteSpace;
						x += anInt3748 >> 8;
						anInt3748 &= 255;
					}

					int charWidth = glyphDisplayWidth[c];
					if (strikethroughColor != -1) {
						NEDrawingArea.drawHorizontalLineNE(x, y + (int) ((double) baseCharHeight * 0.7D), charWidth,
								strikethroughColor);
					}
					if (underlineColor != -1) {
						NEDrawingArea.drawHorizontalLineNE(x, y + baseCharHeight + 1, charWidth, underlineColor);
					}
					x += charWidth;
				}
			}
		}

	}

	public final int getDisplayedWidth(String string) {
		if (string == null) {
			return 0;
		}
		int index = -1;
		int width = 0;
		int length = string.length();

		for (int idx = 0; idx < length; ++idx) {
			int character = string.charAt(idx);
			if (character == 60) {

				index = idx;
			} else {
				if (character == 62 && index != -1) {
					String effect = string.substring(index + 1, idx);
					index = -1;
					if (effect.equals(lessThan)) {
						character = 60;
					} else if (effect.equals(greaterThan)) {
						character = 62;
					} else if (effect.equals(nonBreakingSpace)) {
						character = 160;
					} else if (effect.equals(softHyphen)) {
						character = 173;
					} else if (effect.equals(multiplicationSymbol)) {
						character = 215;
					} else if (effect.equals(euroSymbol)) {
						character = 128;
					} else if (effect.equals(copyright)) {
						character = 169;
					} else {
						if (!effect.equals(registeredTrademark)) {
							if (effect.startsWith(image, 0)) {
								try {
									int icon = Integer.parseInt(effect.substring(4));
									width += modIcons[icon].anInt1444;
								} catch (Exception var10) {

								}
							}
							continue;
						}

						character = 174;
					}
				}

				if (index == -1) {
					width += glyphDisplayWidth[character];
				}
			}
		}

		return width;

	}

	public void parseStringForEffects(String string) {
		do {
			try {
				if (string.startsWith(startColor)) {
					String color = string.substring(4);
					textColor = color.length() < 6 ? Color.decode(color).getRGB() : Integer.parseInt(color, 16);

				} else if (string.equals(endColor)) {
					textColor = defaultTextColor;
				} else if (string.startsWith(startTrans)) {
					opacity = Integer.valueOf(string.substring(6));
				} else if (string.equals(endTrans)) {
					opacity = defaultOpacity;
				} else if (string.startsWith(startStrikethrough)) {
					String color = string.substring(4);
					strikethroughColor = color.length() < 6 ? Color.decode(color).getRGB()
							: Integer.parseInt(color, 16);
				} else if (string.equals(startDefaultStrikeThrough)) {
					strikethroughColor = 8388608;
				} else if (string.equals(endStrikeThrough)) {
					strikethroughColor = -1;
				} else if (string.startsWith(startUnderline)) {
					String color = string.substring(2);
					underlineColor = color.length() < 6 ? Color.decode(color).getRGB() : Integer.parseInt(color, 16);
				} else if (string.equals(startDefaultUnderline)) {
					underlineColor = 0;
				} else if (string.equals(endUnderline)) {
					underlineColor = -1;
				} else if (string.startsWith(startShadow)) {
					String color = string.substring(5);
					shadowColor = color.length() < 6 ? Color.decode(color).getRGB() : Integer.parseInt(color, 16);
				} else if (string.equals(startDefaultShadow)) {
					shadowColor = 0;
				} else if (string.equals(endShadow)) {
					shadowColor = defaultShadowColor;
				} else {
					if (!string.equals(lineBreak)) {
						break;
					}
					setEffectsAlpha(defaultTextColor, defaultShadowColor, defaultOpacity);
				}
			} catch (Exception exception) {
				break;
			}
			break;
		} while (false);
	}

	public static void createTransparentCharacterPixels(int[] is, byte[] is_0_, int i, int i_1_, int i_2_, int i_3_,
			int i_4_, int i_5_, int i_6_, int i_7_) {
		i = ((i & 0xff00ff) * i_7_ & ~0xff00ff) + ((i & 0xff00) * i_7_ & 0xff0000) >> 8;
		i_7_ = 256 - i_7_;
		for (int i_8_ = -i_4_; i_8_ < 0; i_8_++) {
			for (int i_9_ = -i_3_; i_9_ < 0; i_9_++) {
				if (is_0_[i_1_++] != 0) {
					int i_10_ = is[i_2_];
					is[i_2_++] = ((((i_10_ & 0xff00ff) * i_7_ & ~0xff00ff) + ((i_10_ & 0xff00) * i_7_ & 0xff0000)) >> 8)
							+ i;
				} else {
					i_2_++;
				}
			}
			i_2_ += i_5_;
			i_1_ += i_6_;
		}
	}

	public void drawCharacterAlpha(int i, int i_11_, int i_12_, int i_13_, int i_14_, int i_15_, int i_16_,
			boolean bool) {
		int i_17_ = i_11_ + i_12_ * DrawingArea.width;
		int i_18_ = DrawingArea.width - i_13_;
		int i_19_ = 0;
		int i_20_ = 0;
		if (i_12_ < DrawingArea.topY) {
			int i_21_ = DrawingArea.topY - i_12_;
			i_14_ -= i_21_;
			i_12_ = DrawingArea.topY;
			i_20_ += i_21_ * i_13_;
			i_17_ += i_21_ * DrawingArea.width;
		}
		if (i_12_ + i_14_ > DrawingArea.bottomY) {
			i_14_ -= i_12_ + i_14_ - DrawingArea.bottomY;
		}
		if (i_11_ < DrawingArea.topX) {
			int i_22_ = DrawingArea.topX - i_11_;
			i_13_ -= i_22_;
			i_11_ = DrawingArea.topX;
			i_20_ += i_22_;
			i_17_ += i_22_;
			i_19_ += i_22_;
			i_18_ += i_22_;
		}
		if (i_11_ + i_13_ > DrawingArea.bottomX) {
			int i_23_ = i_11_ + i_13_ - DrawingArea.bottomX;
			i_13_ -= i_23_;
			i_19_ += i_23_;
			i_18_ += i_23_;
		}
		if (i_13_ > 0 && i_14_ > 0) {
			createTransparentCharacterPixels(DrawingArea.pixels, glyphPixels[i], i_15_, i_20_, i_17_, i_13_, i_14_,
					i_18_, i_19_, i_16_);
		}
	}

	public static void createCharacterPixels(int[] is, byte[] is_24_, int i, int i_25_, int i_26_, int i_27_, int i_28_,
			int i_29_, int i_30_) {
		int i_31_ = -(i_27_ >> 2);
		i_27_ = -(i_27_ & 0x3);
		for (int i_32_ = -i_28_; i_32_ < 0; i_32_++) {
			for (int i_33_ = i_31_; i_33_ < 0; i_33_++) {
				if (is_24_[i_25_++] != 0) {
					is[i_26_++] = i;
				} else {
					i_26_++;
				}
				if (is_24_[i_25_++] != 0) {
					is[i_26_++] = i;
				} else {
					i_26_++;
				}
				if (is_24_[i_25_++] != 0) {
					is[i_26_++] = i;
				} else {
					i_26_++;
				}
				if (is_24_[i_25_++] != 0) {
					is[i_26_++] = i;
				} else {
					i_26_++;
				}
			}
			for (int i_34_ = i_27_; i_34_ < 0; i_34_++) {
				if (is_24_[i_25_++] != 0) {
					is[i_26_++] = i;
				} else {
					i_26_++;
				}
			}
			i_26_ += i_29_;
			i_25_ += i_30_;
		}
	}

	public void drawCharacter(int character, int i_35_, int i_36_, int i_37_, int i_38_, int i_39_, boolean bool) {
		int i_40_ = i_35_ + i_36_ * DrawingArea.width;
		int i_41_ = DrawingArea.width - i_37_;
		int i_42_ = 0;
		int i_43_ = 0;
		if (i_36_ < DrawingArea.topY) {
			int i_44_ = DrawingArea.topY - i_36_;
			i_38_ -= i_44_;
			i_36_ = DrawingArea.topY;
			i_43_ += i_44_ * i_37_;
			i_40_ += i_44_ * DrawingArea.width;
		}
		if (i_36_ + i_38_ > DrawingArea.bottomY) {
			i_38_ -= i_36_ + i_38_ - DrawingArea.bottomY;
		}
		if (i_35_ < DrawingArea.topX) {
			int i_45_ = DrawingArea.topX - i_35_;
			i_37_ -= i_45_;
			i_35_ = DrawingArea.topX;
			i_43_ += i_45_;
			i_40_ += i_45_;
			i_42_ += i_45_;
			i_41_ += i_45_;
		}
		if (i_35_ + i_37_ > DrawingArea.bottomX) {
			int i_46_ = i_35_ + i_37_ - DrawingArea.bottomX;
			i_37_ -= i_46_;
			i_42_ += i_46_;
			i_41_ += i_46_;
		}
		if (i_37_ > 0 && i_38_ > 0) {
			createCharacterPixels(DrawingArea.pixels, glyphPixels[character], i_39_, i_43_, i_40_, i_37_, i_38_, i_41_,
					i_42_);

		}
	}

	public void drawString(String string, int x, int y, int color, int shadow) {
		if (string != null) {
			setEffects(color, shadow);
			drawBasicString(string, x, y);
		}
	}

	private void setEffects(int color, int shadow) {
		strikethroughColor = -1;
		underlineColor = -1;
		defaultShadowColor = shadow;
		shadowColor = shadow;
		defaultTextColor = color;
		textColor = color;
		defaultOpacity = 256;
		opacity = 256;
		whiteSpace = 0;
		anInt3748 = 0;
	}

	public int getDrawnStringBaseX(String string, int x, int y, int color, int shadow, Random random, int seed) {
		if (string == null) {
			return 0;
		} else {
			random.setSeed((long) seed);
			setEffectsAlpha(color, shadow, 192 + (random.nextInt() & 31));// random
																			// opacity
																			// ?
			int length = string.length();
			int[] xMod = new int[length];
			int modValue = 0;

			for (int pos = 0; pos < length; ++pos) {
				xMod[pos] = modValue;
				if ((random.nextInt() & 3) == 0) {
					++modValue;
				}
			}

			drawBasicStringXYMods(string, x, y, xMod, (int[]) null);
			return modValue;
		}
	}

	public final void drawBasicStringXYMods(String var1, int drawX, int drawY, int[] xmodifiers, int[] ymodifiers) {
		drawY -= baseCharHeight;
		int effect = -1;
		int modifier = 0;
		int length = var1.length();

		for (int pos = 0; pos < length; ++pos) {
			int character = var1.charAt(pos);
			if (character == 60) {
				effect = pos;
			} else {
				int xOff;
				int yOffset;
				int symbolWidth;
				if (character == 62 && effect != -1) {
					String symbol = var1.substring(pos, effect + 1);
					effect = -1;
					if (symbol.equals(lessThan)) {
						character = 60;
					} else if (symbol.equals(greaterThan)) {
						character = 62;
					} else if (symbol.equals(nonBreakingSpace)) {
						character = 160;
					} else if (symbol.equals(softHyphen)) {
						character = 173;
					} else if (symbol.equals(multiplicationSymbol)) {
						character = 215;
					} else if (symbol.equals(euroSymbol)) {
						character = 128;
					} else if (symbol.equals(copyright)) {
						character = 169;
					} else {
						if (!symbol.equals(registeredTrademark)) {
							if (symbol.startsWith(image, 0)) {
								try {
									if (xmodifiers != null) {
										xOff = xmodifiers[modifier];
									} else {
										xOff = 0;
									}

									if (ymodifiers != null) {
										yOffset = ymodifiers[modifier];
									} else {
										yOffset = 0;
									}

									++modifier;
									symbolWidth = Integer.parseInt(symbol.substring(4));
									Sprite image = modIcons[symbolWidth];
									int imageHeight = imageWidths != null ? imageWidths[symbolWidth] : image.anInt1444;
									if (opacity == 256) {
										image.drawSprite(drawX + xOff, drawY + baseCharHeight - imageHeight + yOffset);
									} else {
										image.drawSprite1(drawX + xOff, drawY + baseCharHeight - imageHeight + yOffset,
												opacity);
									}

									drawX += image.anInt1444;
								} catch (Exception var18) {
									;
								}
							} else {
								parseStringForEffects(symbol);
							}
							continue;
						}

						character = 174;
					}
				}

				if (effect == -1) {
					int cWidth = glyphWidth[character];
					xOff = glyphHeight[character];
					if (xmodifiers != null) {
						yOffset = xmodifiers[modifier];
					} else {
						yOffset = 0;
					}

					if (ymodifiers != null) {
						symbolWidth = ymodifiers[modifier];
					} else {
						symbolWidth = 0;
					}

					++modifier;
					if (character != 32) {
						if (opacity == 256) {
							if (shadowColor != -1) {
								drawCharacter(character, drawX + horizontalKerning[character] + 1 + yOffset,
										drawY + verticalKerning[character] + 1 + symbolWidth, cWidth, xOff, shadowColor,
										true);
							}

							drawCharacter(character, drawX + horizontalKerning[character] + yOffset,
									drawY + verticalKerning[character] + symbolWidth, cWidth, xOff, textColor, false);
						} else {
							if (shadowColor != -1) {
								drawCharacterAlpha(character, drawX + horizontalKerning[character] + 1 + yOffset,
										drawY + verticalKerning[character] + 1 + symbolWidth, cWidth, xOff, shadowColor,
										opacity, true);
							}

							drawCharacterAlpha(character, drawX + horizontalKerning[character] + yOffset,
									drawY + verticalKerning[character] + symbolWidth, cWidth, xOff, textColor, opacity,
									false);
						}
					} else if (whiteSpace > 0) {
						anInt3748 += whiteSpace;
						drawX += anInt3748 >> 8;
						anInt3748 &= 255;
					}

					int charWidth = glyphDisplayWidth[character];
					if (strikethroughColor != -1) {
						if (opacity > 255) {
							NEDrawingArea.drawHorizontalLineNE(drawX, drawY + (int) ((double) baseCharHeight * 0.7D),
									charWidth, strikethroughColor);
						} else {
							NEDrawingArea.drawHorizontalLineAlphaNE(drawX,
									drawY + (int) ((double) baseCharHeight * 0.7D), charWidth, strikethroughColor,
									opacity);

						}
					}

					if (underlineColor != -1) {
						if (opacity > 255) {
							NEDrawingArea.drawHorizontalLineNE(drawX, drawY + baseCharHeight, charWidth,
									underlineColor);

						} else {

							NEDrawingArea.drawHorizontalLineAlphaNE(drawX, drawY + baseCharHeight, charWidth,
									underlineColor, opacity);
						}
					}

					drawX += charWidth;
				}
			}
		}

	}

	public void drawStringAlignedLeft(String string, int x, int y, int color, int shadow) {
		if (string != null) {
			setEffects(color, shadow);
			drawBasicString(string, x - getDisplayedWidth(string), y);
		}
	}

	public void drawCenteredStringXMod(String var1, int var2, int var3, int var4, int var5, int var6, int var7) {
		if (var1 != null) {
			setEffects(var4, var5);
			double amplitude = 7.0D - (double) var7 / 8.0D;
			if (amplitude < 0.0D) {
				amplitude = 0.0D;
			}

			int length = var1.length();
			int[] xmod = new int[length];

			for (int pos = 0; pos < length; ++pos) {
				xmod[pos] = (int) (Math.sin((double) pos / 1.5D + (double) var6 / 1.0D) * amplitude);
			}

			drawBasicStringXYMods(var1, var2 - getDisplayedWidth(var1) / 2, var3, (int[]) null, xmod);
		}
	}

	public void drawStringWave(String string, int x, int y, int color, int shadow, int var6) {
		if (string != null) {
			setEffects(color, shadow);
			int length = string.length();
			int[] var8 = new int[length];
			int[] var9 = new int[length];

			for (int pos = 0; pos < length; ++pos) {
				var8[pos] = (int) (Math.sin((double) pos / 5.0D + (double) var6 / 5.0D) * 5.0D);
				var9[pos] = (int) (Math.sin((double) pos / 3.0D + (double) var6 / 5.0D) * 5.0D);
			}

			drawBasicStringXYMods(string, x - getDisplayedWidth(string) / 2, y, var8, var9);
		}
	}

	public void drawStringWaveY(String string, int x, int y, int color, int shadow, int tick) {
		if (string != null) {
			setEffects(color, shadow);
			int var7 = string.length();
			int[] vertWaveOffset = new int[var7];

			for (int whichChar = 0; whichChar < var7; ++whichChar) {
				vertWaveOffset[whichChar] = (int) (Math.sin((double) whichChar / 2.0D + (double) tick / 5.0D) * 5.0D);
			}

			drawBasicStringXYMods(string, x - getDisplayedWidth(string) / 2, y, (int[]) null, vertWaveOffset);
		}
	}

	public void setmodIcons(Sprite[] images, int[] widths) {
		if (widths != null && widths.length != images.length) {
			throw new IllegalArgumentException();
		} else {
			modIcons = images;
			imageWidths = widths;
		}
	}

	public void drawCenteredString(String text, int x, int y, int color, int shadow) {
		if (text != null) {
			setEffects(color, shadow);
			drawBasicString(text, x - getDisplayedWidth(text) / 2, y);
		}
	}

	public static void nullLoader() {
		lessThan = null;
		greaterThan = null;
		nonBreakingSpace = null;
		softHyphen = null;
		multiplicationSymbol = null;
		euroSymbol = null;
		copyright = null;
		registeredTrademark = null;
		image = null;
		lineBreak = null;
		startColor = null;
		endColor = null;
		startTrans = null;
		endTrans = null;
		startUnderline = null;
		startDefaultUnderline = null;
		endUnderline = null;
		startShadow = null;
		startDefaultShadow = null;
		endShadow = null;
		startStrikethrough = null;
		startDefaultStrikeThrough = null;
		endStrikeThrough = null;
	}

	private int[] horizontalKerning;
	private byte[][] glyphPixels;
	private int[] verticalKerning;
	private int[] glyphDisplayWidth;
	private Sprite[] modIcons;
	private int[] imageWidths;
	int[] glyphWidth;
	int[] glyphHeight;
	public int baseCharHeight = 0;
	private static String greaterThan = "gt";
	private static String lessThan = "lt";
	private static String euroSymbol = "euro";
	private static String startShadow = "shad=";
	private static String softHyphen = "shy";
	private static String startTrans = "trans=";
	private static String startUnderline = "u=";
	private static String startStrikethrough = "str=";
	private static String endColor = "/col";
	private static String endShadow = "/shad";
	private static String endTrans = "/trans";
	private static String endUnderline = "/u";
	private static String endStrikeThrough = "/str";
	private static String startDefaultUnderline = "u";
	private static String startDefaultStrikeThrough = "str";
	private static String startDefaultShadow = "shad";
	private static String startColor = "col=";
	private static String multiplicationSymbol = "times";
	private static String nonBreakingSpace = "nbsp";
	private static String image = "img=";
	private static String copyright = "copy";
	private static String lineBreak = "br";
	private static String registeredTrademark = "reg";
	private static int strikethroughColor = -1;
	private static int underlineColor = -1;
	private static int anInt3748 = 0;
	private static int defaultTextColor = 0;
	private static int opacity = 256;
	private static int defaultOpacity = 256;
	private static int defaultShadowColor = -1;
	private static int shadowColor = -1;
	private static int textColor = 0xff00ff;
	private static int whiteSpace = 0;

}
