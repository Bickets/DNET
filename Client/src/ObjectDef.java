import java.io.File;
import sign.signlink;

// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

public final class ObjectDef {
	
	private static final int SIZE_289 = 7389;

	public static ObjectDef forID(int i) {
		for (int j = 0; j < 20; j++)
			if (cache[j].type == i)
				return cache[j];

		cacheIndex = (cacheIndex + 1) % 20;
		ObjectDef objectDef = cache[cacheIndex];
		if(i < SIZE_289 && i != 202 && i != 205 && i != 683 && i != 6836) {
			stream289.currentOffset = streamIndices289[i];
			objectDef.type = i;
			objectDef.setDefaults();
			objectDef.readValues289(stream289);
		} else {
			stream377.currentOffset = streamIndices377[i];
			objectDef.type = i;
			objectDef.setDefaults();
			objectDef.readValues377(stream377);			
		}
		switch (i) {
		case 2218:
			objectDef.actions = new String[5];
			objectDef.actions[0] = "Descend";
			break;
		}
		return objectDef;
	}

	private void setDefaults() {
		anIntArray773 = null;
		anIntArray776 = null;
		name = null;
		description = null;
		modifiedModelColors = null;
		originalModelColors = null;
		anInt744 = 1;
		anInt761 = 1;
		aBoolean767 = true;
		aBoolean757 = true;
		hasActions = false;
		aBoolean762 = false;
		aBoolean769 = false;
		aBoolean764 = false;
		anInt781 = -1;
		anInt775 = 16;
		aByte737 = 0;
		aByte742 = 0;
		actions = null;
		anInt746 = -1;
		anInt758 = -1;
		aBoolean751 = false;
		aBoolean779 = true;
		anInt748 = 128;
		anInt772 = 128;
		anInt740 = 128;
		anInt768 = 0;
		anInt738 = 0;
		anInt745 = 0;
		anInt783 = 0;
		aBoolean736 = false;
		aBoolean766 = false;
		anInt760 = -1;
		anInt774 = -1;
		anInt749 = -1;
		childrenIDs = null;
	}

	public void method574(OnDemandFetcher class42_sub1) {
		if (anIntArray773 == null)
			return;
		for (int j = 0; j < anIntArray773.length; j++)
			class42_sub1.method560(anIntArray773[j] & 0xffff, 0);
	}

	public static void nullLoader() {
		mruNodes1 = null;
		mruNodes2 = null;
		streamIndices377 = null;
		streamIndices289 = null;
		cache = null;
		stream377 = null;
		stream289 = null;
	}

	public static void unpackConfig(StreamLoader streamLoader) {
		stream377 = new Stream(streamLoader.getDataForName("loc.dat"));
		stream289 = new Stream(StreamLoader.read(new File(signlink.findcachedir() + "loc289.dat")));
		
		
		Stream streamIdx377 = new Stream(streamLoader.getDataForName("loc.idx"));
		int totalObjects = streamIdx377.readUnsignedWord();
		int i = 2;
		for (int j = 0; j < totalObjects; j++) {
			streamIndices377[j] = i;
			i += streamIdx377.readUnsignedWord();
		}
		
		Stream streamIdx289 = new Stream(StreamLoader.read(new File(signlink.findcachedir() + "loc289.idx")));
		totalObjects = streamIdx289.readUnsignedWord();
		i = 2;
		for (int j = 0; j < totalObjects; j++) {
			streamIndices289[j] = i;
			i += streamIdx289.readUnsignedWord();
		}

		cache = new ObjectDef[20];
		for (int k = 0; k < 20; k++)
			cache[k] = new ObjectDef();

	}

	public boolean method577(int i) {
		if (anIntArray776 == null) {
			if (anIntArray773 == null)
				return true;
			if (i != 10)
				return true;
			boolean flag1 = true;
			for (int k = 0; k < anIntArray773.length; k++)
				flag1 &= Model.method463(anIntArray773[k] & 0xffff);

			return flag1;
		}
		for (int j = 0; j < anIntArray776.length; j++)
			if (anIntArray776[j] == i)
				return Model.method463(anIntArray773[j] & 0xffff);

		return true;
	}

	public Model method578(int i, int j, int k, int l, int i1, int j1, int k1) {
		Model model = method581(i, k1, j);
		if (model == null)
			return null;
		if (aBoolean762 || aBoolean769)
			model = new Model(aBoolean762, aBoolean769, model);
		if (aBoolean762) {
			int l1 = (k + l + i1 + j1) / 4;
			for (int i2 = 0; i2 < model.numberOfVerticeCoordinates; i2++) {
				int j2 = model.verticesXCoordinate[i2];
				int k2 = model.verticesZCoordinate[i2];
				int l2 = k + ((l - k) * (j2 + 64)) / 128;
				int i3 = j1 + ((i1 - j1) * (j2 + 64)) / 128;
				int j3 = l2 + ((i3 - l2) * (k2 + 64)) / 128;
				model.verticesYCoordinate[i2] += j3 - l1;
			}

			model.normalise();
		}
		return model;
	}

	public boolean method579() {
		if (anIntArray773 == null)
			return true;
		boolean flag1 = true;
		for (int i = 0; i < anIntArray773.length; i++)
			flag1 &= Model.method463(anIntArray773[i] & 0xffff);
		return flag1;
	}

	public ObjectDef method580() {
		int i = -1;
		if (anInt774 != -1) {
			VarBit varBit = VarBit.cache[anInt774];
			int j = varBit.anInt648;
			int k = varBit.anInt649;
			int l = varBit.anInt650;
			int i1 = Client.anIntArray1232[l - k];
			i = clientInstance.variousSettings[j] >> k & i1;
		} else if (anInt749 != -1)
			i = clientInstance.variousSettings[anInt749];
		if (i < 0 || i >= childrenIDs.length || childrenIDs[i] == -1)
			return null;
		else
			return forID(childrenIDs[i]);
	}

	private Model method581(int j, int k, int l) {
		Model model = null;
		long l1;
		if (anIntArray776 == null) {
			if (j != 10)
				return null;
			l1 = (long) ((type << 6) + l) + ((long) (k + 1) << 32);
			Model model_1 = (Model) mruNodes2.insertFromCache(l1);
			if (model_1 != null)
				return model_1;
			if (anIntArray773 == null)
				return null;
			boolean flag1 = aBoolean751 ^ (l > 3);
			int k1 = anIntArray773.length;
			for (int i2 = 0; i2 < k1; i2++) {
				int l2 = anIntArray773[i2];
				if (flag1)
					l2 += 0x10000;
				model = (Model) mruNodes1.insertFromCache(l2);
				if (model == null) {
					model = Model.getModel(l2 & 0xffff);
					if (model == null)
						return null;
					if (flag1)
						model.mirrorModel();
					mruNodes1.removeFromCache(model, l2);
				}
				if (k1 > 1)
					aModelArray741s[i2] = model;
			}

			if (k1 > 1)
				model = new Model(k1, aModelArray741s);
		} else {
			int i1 = -1;
			for (int j1 = 0; j1 < anIntArray776.length; j1++) {
				if (anIntArray776[j1] != j)
					continue;
				i1 = j1;
				break;
			}

			if (i1 == -1)
				return null;
			l1 = (long) ((type << 6) + (i1 << 3) + l) + ((long) (k + 1) << 32);
			Model model_2 = (Model) mruNodes2.insertFromCache(l1);
			if (model_2 != null)
				return model_2;
			int j2 = anIntArray773[i1];
			boolean flag3 = aBoolean751 ^ (l > 3);
			if (flag3)
				j2 += 0x10000;
			model = (Model) mruNodes1.insertFromCache(j2);
			if (model == null) {
				model = Model.getModel(j2 & 0xffff);
				if (model == null)
					return null;
				if (flag3)
					model.mirrorModel();
				mruNodes1.removeFromCache(model, j2);
			}
		}
		boolean flag;
		flag = anInt748 != 128 || anInt772 != 128 || anInt740 != 128;
		boolean flag2;
		flag2 = anInt738 != 0 || anInt745 != 0 || anInt783 != 0;
		Model model_3 = new Model(modifiedModelColors == null, Class36.method532(k),
				l == 0 && k == -1 && !flag && !flag2, model);
		if (k != -1) {
			model_3.createBones();
			model_3.applyAnimation(k);
			model_3.triangleSkin = null;
			model_3.vertexSkin = null;
		}
		while (l-- > 0)
			model_3.rotateBy90();
		if (modifiedModelColors != null) {
			for (int k2 = 0; k2 < modifiedModelColors.length; k2++)
				model_3.setColor(modifiedModelColors[k2], originalModelColors[k2]);

		}
		if (flag)
			model_3.scale(anInt748, anInt740, anInt772);
		if (flag2)
			model_3.translate(anInt738, anInt745, anInt783);
		model_3.setLighting(64 + aByte737, 768 + aByte742 * 5, -50, -10, -50, !aBoolean769);
		if (anInt760 == 1)
			model_3.anInt1654 = model_3.modelHeight;
		mruNodes2.removeFromCache(model_3, l1);
		return model_3;
	}
	
	private void readValues289(Stream stream) {
		int i = -1;
		label0: do {
			int j;
			do {
				j = stream.readUnsignedByte();
				if (j == 0)
					break label0;
				if (j == 1) {
					int k = stream.readUnsignedByte();
					if (k > 0)
						if (anIntArray773 == null || lowMem) {
							anIntArray776 = new int[k];
							anIntArray773 = new int[k];
							for (int k1 = 0; k1 < k; k1++) {
								anIntArray773[k1] = stream.readUnsignedWord();
								anIntArray776[k1] = stream.readUnsignedByte();
							}

						} else {
							stream.currentOffset += k * 3;
						}
				} else if (j == 2)
					name = stream.readString();
				else if (j == 3)
					description = stream.readBytes();
				else if (j == 5) {
					int l = stream.readUnsignedByte();
					if (l > 0)
						if (anIntArray773 == null || lowMem) {
							anIntArray776 = null;
							anIntArray773 = new int[l];
							for (int l1 = 0; l1 < l; l1++)
								anIntArray773[l1] = stream.readUnsignedWord();

						} else {
							stream.currentOffset += l * 2;
						}
				} else if (j == 14)
					anInt744 = stream.readUnsignedByte();
				else if (j == 15)
					anInt761 = stream.readUnsignedByte();
				else if (j == 17)
					aBoolean767 = false;
				else if (j == 18)
					aBoolean757 = false;
				else if (j == 19) {
					i = stream.readUnsignedByte();
					if (i == 1)
						hasActions = true;
				} else if (j == 21)
					aBoolean762 = true;
				else if (j == 22)
					aBoolean769 = true;
				else if (j == 23)
					aBoolean764 = true;
				else if (j == 24) {
					anInt781 = stream.readUnsignedWord();
					if (anInt781 == 65535)
						anInt781 = -1;
				} else if (j == 28)
					anInt775 = stream.readUnsignedByte();
				else if (j == 29)
					aByte737 = stream.readSignedByte();
				else if (j == 39)
					aByte742 = stream.readSignedByte();
				else if (j >= 30 && j < 39) {
					if (actions == null)
						actions = new String[5];
					actions[j - 30] = stream.readString();
					if (actions[j - 30].equalsIgnoreCase("hidden"))
						actions[j - 30] = null;
				} else if (j == 40) {
					int i1 = stream.readUnsignedByte();
					modifiedModelColors = new int[i1];
					originalModelColors = new int[i1];
					for (int i2 = 0; i2 < i1; i2++) {
						modifiedModelColors[i2] = stream.readUnsignedWord();
						originalModelColors[i2] = stream.readUnsignedWord();
					}

				} else if (j == 60)
					anInt746 = stream.readUnsignedWord();
				else if (j == 62)
					aBoolean751 = true;
				else if (j == 64)
					aBoolean779 = false;
				else if (j == 65)
					anInt748 = stream.readUnsignedWord();
				else if (j == 66)
					anInt772 = stream.readUnsignedWord();
				else if (j == 67)
					anInt740 = stream.readUnsignedWord();
				else if (j == 68)
					anInt758 = stream.readUnsignedWord();
				else if (j == 69)
					anInt768 = stream.readUnsignedByte();
				else if (j == 70)
					anInt738 = stream.readSignedWord();
				else if (j == 71)
					anInt745 = stream.readSignedWord();
				else if (j == 72)
					anInt783 = stream.readSignedWord();
				else if (j == 73)
					aBoolean736 = true;
				else if (j == 74) {
					aBoolean766 = true;
				} else {
					if (j != 75)
						continue;
					anInt760 = stream.readUnsignedByte();
				}
				continue label0;
			} while (j != 77);
			anInt774 = stream.readUnsignedWord();
			if (anInt774 == 65535)
				anInt774 = -1;
			anInt749 = stream.readUnsignedWord();
			if (anInt749 == 65535)
				anInt749 = -1;
			int j1 = stream.readUnsignedByte();
			childrenIDs = new int[j1 + 1];
			for (int j2 = 0; j2 <= j1; j2++) {
				childrenIDs[j2] = stream.readUnsignedWord();
				if (childrenIDs[j2] == 65535)
					childrenIDs[j2] = -1;
			}

		} while (true);
		if (i == -1) {
			hasActions = anIntArray773 != null && (anIntArray776 == null || anIntArray776[0] == 10);
			if (actions != null)
				hasActions = true;
		}
		if (aBoolean766) {
			aBoolean767 = false;
			aBoolean757 = false;
		}
		if (anInt760 == -1)
			anInt760 = aBoolean767 ? 1 : 0;
	}

	private void readValues377(Stream stream) {
		int i = -1;
		label0: do {
			int j;
			do {
				j = stream.readUnsignedByte();
				if (j == 0)
					break label0;
				if (j == 1) {
					int k = stream.readUnsignedByte();
					if (k > 0)
						if (anIntArray773 == null || lowMem) {
							anIntArray776 = new int[k];
							anIntArray773 = new int[k];
							for (int k1 = 0; k1 < k; k1++) {
								anIntArray773[k1] = stream.readUnsignedWord();
								anIntArray776[k1] = stream.readUnsignedByte();
							}

						} else {
							stream.currentOffset += k * 3;
						}
				} else if (j == 2)
					name = stream.readString();
				else if (j == 3)
					description = stream.readBytes();
				else if (j == 5) {
					int l = stream.readUnsignedByte();
					if (l > 0)
						if (anIntArray773 == null || lowMem) {
							anIntArray776 = null;
							anIntArray773 = new int[l];
							for (int l1 = 0; l1 < l; l1++)
								anIntArray773[l1] = stream.readUnsignedWord();

						} else {
							stream.currentOffset += l * 2;
						}
				} else if (j == 14)
					anInt744 = stream.readUnsignedByte();
				else if (j == 15)
					anInt761 = stream.readUnsignedByte();
				else if (j == 17)
					aBoolean767 = false;
				else if (j == 18)
					aBoolean757 = false;
				else if (j == 19) {
					i = stream.readUnsignedByte();
					if (i == 1)
						hasActions = true;
				} else if (j == 21)
					aBoolean762 = true;
				else if (j == 22)
					aBoolean769 = true;
				else if (j == 23)
					aBoolean764 = true;
				else if (j == 24) {
					anInt781 = stream.readUnsignedWord();
					if (anInt781 == 65535)
						anInt781 = -1;
				} else if (j == 28)
					anInt775 = stream.readUnsignedByte();
				else if (j == 29)
					aByte737 = stream.readSignedByte();
				else if (j == 39)
					aByte742 = stream.readSignedByte();
				else if (j >= 30 && j < 39) {
					String action = stream.readString();
						if (actions == null)
							actions = new String[5];
						actions[j - 30] = action;
						if (actions[j - 30].equalsIgnoreCase("hidden"))
							actions[j - 30] = null;
				} else if (j == 40) {
					int i1 = stream.readUnsignedByte();
					modifiedModelColors = new int[i1];
					originalModelColors = new int[i1];
					for (int i2 = 0; i2 < i1; i2++) {
						modifiedModelColors[i2] = stream.readUnsignedWord();
						originalModelColors[i2] = stream.readUnsignedWord();
					}

				} else if (j == 60)
					anInt746 = stream.readUnsignedWord();
				else if (j == 62)
					aBoolean751 = true;
				else if (j == 64)
					aBoolean779 = false;
				else if (j == 65)
					anInt748 = stream.readUnsignedWord();
				else if (j == 66)
					anInt772 = stream.readUnsignedWord();
				else if (j == 67)
					anInt740 = stream.readUnsignedWord();
				else if (j == 68)
					anInt758 = stream.readUnsignedWord();
				else if (j == 69)
					anInt768 = stream.readUnsignedByte();
				else if (j == 70)
					anInt738 = stream.readSignedWord();
				else if (j == 71)
					anInt745 = stream.readSignedWord();
				else if (j == 72)
					anInt783 = stream.readSignedWord();
				else if (j == 73)
					aBoolean736 = true;
				else if (j == 74) {
					aBoolean766 = true;
				} else {
					if (j != 75)
						continue;
					anInt760 = stream.readUnsignedByte();
				}
				continue label0;
			} while (j != 77);
			anInt774 = stream.readUnsignedWord();
			if (anInt774 == 65535)
				anInt774 = -1;
			anInt749 = stream.readUnsignedWord();
			if (anInt749 == 65535)
				anInt749 = -1;
			int j1 = stream.readUnsignedByte();
			childrenIDs = new int[j1 + 1];
			for (int j2 = 0; j2 <= j1; j2++) {
				childrenIDs[j2] = stream.readUnsignedWord();
				if (childrenIDs[j2] == 65535)
					childrenIDs[j2] = -1;
			}

		} while (true);
		if (i == -1) {
			hasActions = anIntArray773 != null && (anIntArray776 == null || anIntArray776[0] == 10);
			if (actions != null)
				hasActions = true;
		}
		if (aBoolean766) {
			aBoolean767 = false;
			aBoolean757 = false;
		}
		if (anInt760 == -1)
			anInt760 = aBoolean767 ? 1 : 0;
	}

	private ObjectDef() {
		type = -1;
	}

	public boolean aBoolean736;
	private byte aByte737;
	private int anInt738;
	public String name;
	private int anInt740;
	private static final Model[] aModelArray741s = new Model[4];
	private byte aByte742;
	public int anInt744;
	private int anInt745;
	public int anInt746;
	private int[] originalModelColors;
	private int anInt748;
	public int anInt749;
	private boolean aBoolean751;
	public static boolean lowMem;
	private static Stream stream377;
	private static Stream stream289;
	public int type;
	private static int[] streamIndices377 = new int[14974]; // 377 cache object size
	private static int[] streamIndices289 = new int[SIZE_289];
	public boolean aBoolean757;
	public int anInt758;
	public int childrenIDs[];
	private int anInt760;
	public int anInt761;
	public boolean aBoolean762;
	public boolean aBoolean764;
	public static Client clientInstance;
	private boolean aBoolean766;
	public boolean aBoolean767;
	public int anInt768;
	private boolean aBoolean769;
	private static int cacheIndex;
	private int anInt772;
	private int[] anIntArray773;
	public int anInt774;
	public int anInt775;
	private int[] anIntArray776;
	public byte description[];
	public boolean hasActions;
	public boolean aBoolean779;
	public static MRUNodes mruNodes2 = new MRUNodes(30);
	public int anInt781;
	private static ObjectDef[] cache;
	private int anInt783;
	private int[] modifiedModelColors;
	public static MRUNodes mruNodes1 = new MRUNodes(500);
	public String actions[];

}
