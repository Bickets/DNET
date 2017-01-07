package com.dodian.uber.game.model.player.skills.prayer;

/**
 * @author Dashboard
 */
public enum Bones {

  BONES(526, 45), BIG_BONES(532, 150), DRAGON_BONES(536, 720), JOGRE_BONES(3125, 300), OURG_BONES(4834, 920);

  private int itemId, experience;

  Bones(int itemId, int experience) {
    this.itemId = itemId;
    this.experience = experience;
  }

  public int getItemId() {
    return this.itemId;
  }

  public int getExperience() {
    return this.experience;
  }

  public static Bones getBone(int itemId) {
    for (Bones bone : values()) {
      if (bone.getItemId() == itemId) {
        return bone;
      }
    }
    return null;
  }

}
