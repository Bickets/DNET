package com.dodian.uber.game.model.player.skills.prayer;

import java.util.HashMap;

import com.dodian.uber.game.model.entity.player.Client;
import com.dodian.uber.game.model.entity.player.Player;
import com.dodian.uber.game.model.player.packets.outgoing.SendMessage;
import com.dodian.uber.game.model.player.packets.outgoing.Sound;
import com.dodian.uber.game.model.player.skills.Skill;

public class Prayers {

  /**
   * Prayer statuses
   */
  private boolean[] prayerstatus = new boolean[Prayer.values().length];

  /**
   * The Player this manager belongs to
   */
  private Player player;
  private Client c;

  /**
   * Create a prayermanager instance for a player
   *
   * @param player
   *          The player to create for
   */
  public Prayers(Player player, Client c) {
    this.player = player;
    this.c = c;
  }

  /**
   * Prayer masks
   */
  private static final int OVERHEAD_PRAYER = 1;
  private static final int ATTACK_PRAYER = 2;
  private static final int STRENGTH_PRAYER = 4;
  private static final int RANGE_PRAYER = 8;
  private static final int MAGIC_PRAYER = 16;
  private static final int DEFENCE_PRAYER = 32;

  public enum Prayer {
    /**
     * Low level prayers
     */
    THICK_SKIN(1, 83, 21233, DEFENCE_PRAYER), BURST_OF_STRENGTH(4, 84, 21234, STRENGTH_PRAYER), CLARITY_OF_THOUGHT(7,
        85, 21235, ATTACK_PRAYER), SHARP_EYE(8, 101, -1, RANGE_PRAYER | ATTACK_PRAYER | STRENGTH_PRAYER), MYSTIC_WILL(9,
            701, -1, MAGIC_PRAYER | ATTACK_PRAYER | STRENGTH_PRAYER),

    /**
     * Medium level prayers
     */
    ROCK_SKIN(10, 86, 21236, DEFENCE_PRAYER), SUPERHUMAN_STRENGTH(13, 87, 21237, STRENGTH_PRAYER), IMPROVED_REFLEXES(16,
        88, 21238, ATTACK_PRAYER),

    /**
     * Misc prayers like protect item
     */
    RAPID_RESTORE(17, 89, 21239), RAPID_HEAL(22, 90, 21240), PROTECT_ITEM(25, 91, 21241),

    /**
     * Medium level prayers cont
     */
    HAWK_EYE(26, 702, -1, RANGE_PRAYER | ATTACK_PRAYER | STRENGTH_PRAYER), MYSTIC_LORE(27, 703, -1,
        MAGIC_PRAYER | ATTACK_PRAYER | STRENGTH_PRAYER),

    /**
     * High level prayers
     */
    STEEL_SKIN(28, 92, 21242, DEFENCE_PRAYER), ULTIMATE_STRENGTH(31, 93, 21243,
        STRENGTH_PRAYER), INCREDIBLE_REFLEXES(34, 94, 21244, ATTACK_PRAYER),

    /**
     * Protect prayers
     */
    PROTECT_MAGIC(37, 95, 21245, OVERHEAD_PRAYER, HeadIcon.PROTECT_MAGIC), PROTECT_RANGE(40, 96, 21246, OVERHEAD_PRAYER,
        HeadIcon.PROTECT_MISSLES), PROTECT_MELEE(43, 97, 21247, OVERHEAD_PRAYER, HeadIcon.PROTECT_MELEE),

    /**
     * More high level prayers cont
     */
    EAGLE_EYE(44, 704, -1, RANGE_PRAYER | ATTACK_PRAYER | STRENGTH_PRAYER), MYSTIC_MIGHT(45, 705, -1,
        MAGIC_PRAYER | ATTACK_PRAYER | STRENGTH_PRAYER),

    /**
     * Damage dealing/stat recovering/prayer "stealing" prayers
     */
    RETRIBUTION(46, 98, 2171, OVERHEAD_PRAYER, HeadIcon.RETRIBUTION), REDEMPTION(49, 99, 2172, OVERHEAD_PRAYER,
        HeadIcon.REDEMPTION), SMITE(52, 100, 2173, OVERHEAD_PRAYER, HeadIcon.SMITE),

    /**
     * Highest level prayers available
     */
    CHIVALRY(60, 706, -1, ATTACK_PRAYER | STRENGTH_PRAYER | DEFENCE_PRAYER), PIETY(70, 707, -1,
        ATTACK_PRAYER | STRENGTH_PRAYER | DEFENCE_PRAYER);

    /**
     * A map of Buttonid -> prayer
     */
    private static HashMap<Integer, Prayer> prayers = new HashMap<Integer, Prayer>();

    static {
      for (Prayer prayer : Prayer.values()) {
        prayers.put(prayer.getButtonId(), prayer);
      }
    }

    private int levelreq;
    private int configId;
    private int buttonId;
    private int prayMask;
    private HeadIcon headIcon;

    private Prayer(int praylevelreq, int configId, int buttonId) {
      this.levelreq = praylevelreq;
      this.configId = configId;
      this.buttonId = buttonId;
    }

    private Prayer(int praylevelreq, int configId, int buttonId, int prayMask) {
      this.levelreq = praylevelreq;
      this.configId = configId;
      this.buttonId = buttonId;
      this.prayMask = prayMask;
    }

    private Prayer(int praylevelreq, int configId, int buttonId, int prayMask, HeadIcon headIcon) {
      this.levelreq = praylevelreq;
      this.configId = configId;
      this.buttonId = buttonId;
      this.prayMask = prayMask;
      this.headIcon = headIcon;
    }

    public int getPrayerLevel() {
      return levelreq;
    }

    public int getConfigId() {
      return configId;
    }

    public int getButtonId() {
      return buttonId;
    }

    public int getMask() {
      return prayMask;
    }

    public HeadIcon getHeadIcon() {
      return headIcon;
    }

    public static Prayer forButton(int button) {
      return prayers.get(button);
    }
  }

  /**
   * Set all configs on
   */
  public void turnAllOn() {
    for (Prayer prayer : Prayer.values()) {
      c.frame87(prayer.getConfigId(), 1);
    }
  }

  /**
   * Set all configs off
   */
  public void turnAllOff() {
    for (Prayer prayer : Prayer.values()) {
      c.frame87(prayer.getConfigId(), 0);
    }
  }

  /**
   * Toggle a prayer, setting the headicon and checking level if turning on
   *
   * @param prayer
   *          The prayer to toggle
   */
  public void togglePrayer(Prayer prayer) {
    if (c.getLevel(Skill.PRAYER) < prayer.getPrayerLevel()) {
      c.send(new SendMessage(
          "You need a Prayer level of at least " + prayer.getPrayerLevel() + " to use " + formatEnum(prayer)));
      c.frame87(prayer.getConfigId(), 0);
      c.send(new Sound(447));
      return;
    }
    if (isPrayerOn(prayer)) {
      set(prayer, false);
      c.frame87(prayer.getConfigId(), 0);
      // System.out.println(ifCheck2()+" = "+ifCheck(prayer));
      if (!ifCheck())
        player.setHeadIcon(HeadIcon.NONE.asInt());
    } else {
      set(prayer, true);
      if (prayer.getHeadIcon() != null) {
        player.setHeadIcon(prayer.getHeadIcon().asInt());
      }
      checkExtraPrayers(prayer);
    }
    if (c.playerGroup == 6 && prayerstatus[prayer.ordinal()] == true)
      c.send(new SendMessage("Prayer toggled: " + prayer));
  }

  /**
   * Set a prayer on/off
   *
   * @param prayer
   *          The prayer to set
   * @param on
   *          true if on, false if off
   */
  public void set(Prayer prayer, boolean on) {
    prayerstatus[prayer.ordinal()] = on;
  }

  /**
   * Clear prayers/curses
   */
  public void reset() {
    for (int i = 0; i < prayerstatus.length; i++) {
      prayerstatus[i] = false;
    }
  }

  /**
   * Check if a prayer is on
   *
   * @param prayer
   *          The prayer to check
   * @return If the prayer is on, true
   */
  public boolean isPrayerOn(Prayer prayer) {
    return prayerstatus[prayer.ordinal()];
  }

  public boolean ifCheck() {
    for (Prayer prayer : Prayer.values()) {
      if (prayer.getMask() == 1 && prayerstatus[prayer.ordinal()] == true)
        return true;
    }
    return false;
  }

  /**
   * Format an enum object or other object from all uppercase to first
   * uppercase.
   * 
   * @param object
   *          The object to format
   * @return The formatted name
   */
  public static String formatEnum(Object object) {
    String s = object.toString().toLowerCase();
    return Character.toUpperCase(s.charAt(0)) + (s.substring(1).replaceAll("_", " "));
  }

  /**
   * Check for the extra prayers on, such as turning on Piety turns off all
   * other strength boosting
   *
   * @param prayer
   *          The prayer toggled
   */
  public void checkExtraPrayers(Prayer prayer) {
    if (prayer.getMask() == -1) {
      return;
    }
    boolean overheadPrayer = (prayer.getMask() & OVERHEAD_PRAYER) != 0;
    boolean attackPrayer = (prayer.getMask() & ATTACK_PRAYER) != 0;
    boolean strengthPrayer = (prayer.getMask() & STRENGTH_PRAYER) != 0;
    boolean defencePrayer = (prayer.getMask() & DEFENCE_PRAYER) != 0;
    boolean rangePrayer = (prayer.getMask() & RANGE_PRAYER) != 0;
    boolean magicPrayer = (prayer.getMask() & MAGIC_PRAYER) != 0;
    for (Prayer p : Prayer.values()) {
      if (!isPrayerOn(p) || p == prayer) {
        continue;
      }
      if (p.getMask() == -1)
        continue;
      if ((p.getMask() & OVERHEAD_PRAYER) != 0 && overheadPrayer || (p.getMask() & ATTACK_PRAYER) != 0 && attackPrayer
          || (p.getMask() & STRENGTH_PRAYER) != 0 && strengthPrayer
          || (p.getMask() & DEFENCE_PRAYER) != 0 && defencePrayer || (p.getMask() & RANGE_PRAYER) != 0 && rangePrayer
          || (p.getMask() & MAGIC_PRAYER) != 0 && magicPrayer) {
        togglePrayer(p);
      }
    }
  }
}
