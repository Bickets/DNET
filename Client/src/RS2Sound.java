/**
 * Handles in game sound effects activated by actions sent by the server
 * 
 * @author Dashboard
 *
 */
public class RS2Sound {
  
 /* public static void onAnimation(Client client, int animation) {
    AnimationSounds sound = AnimationSounds.getSound(animation);
    if (sound != null) {
      client.playSound(sound.getSoundId(), sound.getDelay());
    }    
  }
  
  public static void onHeadIcon(Client client, int headIcon) {
    HeadIconSounds sound = HeadIconSounds.getSound(headIcon);
    if (sound != null) {
      client.playSound(sound.getSoundId(), sound.getDelay());
    }    
  }
  
  public static void onGfx(Client client, int gfx) {
    GfxSounds sound = GfxSounds.getSound(gfx);
    if (sound != null) {
      client.playSound(sound.getSoundId(), sound.getDelay());
    }    
  }
  
  public static void onInterfaceOpen(Client client, int interfaceId) {
    if (InterfaceSounds.getSound(interfaceId) != 0) {
      client.playSound(InterfaceSounds.getSound(interfaceId), 0);
    }
  }

}

enum InterfaceSounds {
  BANK(5292, 1465);
  
  private int interfaceId, soundId;
  
  InterfaceSounds(int interfaceId, int soundId) {
    this.interfaceId = interfaceId;
    this.soundId = soundId;
  }
  
  public int getInterfaceId() {
    return this.interfaceId;
  }
  
  public int getSoundId() {
    return this.soundId;
  }
  
  public static int getSound(int interfaceId) {
    for (InterfaceSounds sound : values()) {
      if (sound.getInterfaceId() == interfaceId) {
        return sound.getSoundId();
      }
    }
    return 0;
  }
  
}

enum AnimationSounds {
  HIGH_ALCH(713, 223),
  EAT(829, 317),
  TELEPORT(1816, 202),
  HARPOON_FISHING(618, 289), LOBSTER_FISHING(619, 289), NET_FISHING(621, 289),
  BURY_BONES(827, 380),
  PUNCH(806, 417, 600),
  NECHRAYEL_ATTACK(1528, new int[] { 1829, 1830, 1831, 1832, 1831, 1830 }, 600), NECHRAYEL_DYING(1530, 1208),
  WHIP(1658, 1080, 200),
  GRANITE_MAUL(1665, 1079),
  GIANT_RAT_ATTACK(4933, 17), GIANT_RAT_DYING(4935, 15, 600),
  GIANT_ATTACK(4652, 56), ICE_GIANT_ATTACK(4672, 56), 
  MOSS_GIANT_DYING(4673, 55, 600),
  HILL_GIANT_DYING(4653, 55, 600),
  DRAGON_ATTACKING(91, 115), DARGON_DYING(92, 118, 600),
  CHICLEN_ATTACKING(5387, 26), CHIKCEN_DYING(5389, 25, 600),
  WOLF_ATTACKING(6559, 36), WOLF_DYING(6558, 35, 600),
  BASIC_SWORD_HIT(451, 396, 200),
  BOW(426, 361, 600),
  MAN_DYING(2304, 70, 600);
  
  private int animationId, delay;
  private int[] soundId;
  private int index;
  
  AnimationSounds(int animationId, int[] soundId, int delay) {
    this.animationId = animationId;
    this.soundId = soundId;
    this.delay = delay;
  }
  
  AnimationSounds(int animationId, int soundId[]) {
    this(animationId, soundId, 0);
  }
  
  AnimationSounds(int animationId, int soundId, int delay) {
    this(animationId, new int[] { soundId }, delay);
  }
  
  AnimationSounds(int animationId, int soundId) {
    this(animationId, new int[] { soundId });
  }
  
  public int getAnimationId() {
    return this.animationId;
  }
  
  public int getSoundId() {
    return this.soundId[ (index ++) % this.soundId.length ];
  }
  
  public int getDelay() {
    return this.delay;
  }
  
  public static AnimationSounds getSound(int animationId) {
    for (AnimationSounds sound : values()) {
      if (sound.getAnimationId() == animationId) {
        return sound;
      }
    }
    return null;
  }
  
}

enum HeadIconSounds {
  
  PRAYER_OFF(255, 435), 
  PROTECT_FROM_MELEE(0, 433),
  PROTECT_FROM_RAGNED(1, 444),
  PROTECT_FROM_MAGIC(2, 438);
  
  private int headIconId, delay;
  private int[] soundId;
  private int index;
  
  HeadIconSounds(int headIconId, int[] soundId, int delay) {
    this.headIconId = headIconId;
    this.soundId = soundId;
    this.delay = delay;
  }
  
  HeadIconSounds(int headIconId, int soundId[]) {
    this(headIconId, soundId, 0);
  }
  
  HeadIconSounds(int headIconId, int soundId, int delay) {
    this(headIconId, new int[] { soundId }, delay);
  }
  
  HeadIconSounds(int headIconId, int soundId) {
    this(headIconId, new int[] { soundId });
  }
  
  public int getHeadIconId() {
    return this.headIconId;
  }
  
  public int getSoundId() {
    return this.soundId[ (index ++) % this.soundId.length ];
  }
  
  public int getDelay() {
    return this.delay;
  }
  
  public static HeadIconSounds getSound(int headIconId) {
    for (HeadIconSounds sound : values()) {
      if (sound.getHeadIconId() == headIconId) {
        return sound;
      }
    }
    return null;
  }
  
}

enum GfxSounds {
  
  CHEST(444, 384);
  
  int gfxId, delay;
  private int[] soundId;
  private int index;
  
  GfxSounds(int gfxId, int[] soundId, int delay) {
    this.gfxId = gfxId;
    this.soundId = soundId;
    this.delay = delay;
  }
  
  GfxSounds(int gfxId, int soundId[]) {
    this(gfxId, soundId, 0);
  }
  
  GfxSounds(int gfxId, int soundId, int delay) {
    this(gfxId, new int[] { soundId }, delay);
  }
  
  GfxSounds(int gfxId, int soundId) {
    this(gfxId, new int[] { soundId });
  }
  
  public int getGfxId() {
    return this.gfxId;
  }
  
  public int getSoundId() {
    return this.soundId[ (index ++) % this.soundId.length ];
  }
  
  public int getDelay() {
    return this.delay;
  }
  
  public static GfxSounds getSound(int headIconId) {
    for (GfxSounds sound : values()) {
      if (sound.getGfxId() == headIconId) {
        return sound;
      }
    }
    return null;
  }
  
}

enum VolumeModifications {
  
  HIGH_ALCHEMY(223, 0.35),
  CHEST(384, 0.20);
  
  private int soundId;
  private double modification;
  
  VolumeModifications(int soundId, double modification) {
    this.soundId = soundId;
    this.modification = modification;
  }
  
  public int getSoundId() {
    return this.soundId;
  }
  
  public double getModification() {
    return this.modification;
  }
  
  public static VolumeModifications getModification(int soundId) {
    for (VolumeModifications modification : values()) {
      if (modification.getSoundId() == soundId) {
        return modification;
      }
    }
    return null;
  }
  */
}
