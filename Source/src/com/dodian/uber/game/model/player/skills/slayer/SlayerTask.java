package com.dodian.uber.game.model.player.skills.slayer;

import com.dodian.utilities.Range;

public enum SlayerTask {

  CRAWLING_HAND(1, 1649, "Crawling Hands", new Range(20, 60), new Range(1, 40), true), PYREFIENDS(2, 1633, "Pyrefiends",
      new Range(20, 60), new Range(20, 60), true), DEATH_SPAWN(3, 1614, "Death Spawns", new Range(20, 60),
          new Range(30, 60), true), JELLY(4, 1637, "Jellies", new Range(30, 70), new Range(40, 75),
              true), HEAD_MOURNER(5, 2373, "Head Mourners", new Range(5, 15), new Range(50, 99), false),

//  DAD(15, 1125, "Dad", new Range(5, 15), new Range(120, 140), false), GUARDIAN(16, 2264, "Abyssal Guardian",
//      new Range(5, 15), new Range(120, 140), false),

  HILL_GIANT(6, 117, "Hill Giants", new Range(20, 40), new Range(1, 50), false), CHAOS_DWARF(18, 119, "Chaos Dwarves", new Range(20, 40), new Range(50, 99), false), LESSER_DEMON(20, 82, "Lesser Demon", new Range(20, 40), new Range(50, 99), false),
  /* MOSS_GIANT(7, 112, "Moss Giants",
      new Range(20, 40), new Range(20, 55),
      false),*/ ICE_GIANT(8, 111, "Ice Giants", new Range(30, 50), new Range(30, 60), false), DRUID(10, 14, "Druids", new Range(20, 35), new Range(1, 30), false),
          GREATER_DEMON(21, 83, "Greater Demon", new Range(20, 40), new Range(70, 99), false),/* BLACK_DEMON(22, 84, "Black Demon", new Range(20, 40), new Range(50, 99), false),*/

  BERSERK_BARBARIAN_SPIRIT(12, 751, "Berserk Barbarian Spirits", new Range(40, 80), new Range(70, 99),
      true), MITHRIL_DRAGON(13, 5363, "Mithril Dragons", new Range(50, 110), new Range(90, 99), true), SKELE_HELLHOUNDS(19, 1575, "Skeleton HellHound", new Range(20, 40), new Range(50, 99), false);

  private int taskId;
  private int npcId;
  private String textRepresentation;
  private Range taskAmount;
  private Range levelAssign;
  private boolean slayerOnly;

  SlayerTask(int taskId, int npcId, String textRepresentation, Range taskAmount, Range levelAssign,
      boolean slayerOnly) {
    this.taskId = taskId;
    this.npcId = npcId;
    this.textRepresentation = textRepresentation;
    this.taskAmount = taskAmount;
    this.levelAssign = levelAssign;
    this.slayerOnly = slayerOnly;
  }
  
    public int getNpcId() {
    return this.npcId;
  }

  public int getTaskId() {
    return this.taskId;
  }

  public String getTextRepresentation() {
    return this.textRepresentation;
  }

  public Range getAssignedAmountRange() {
    return this.taskAmount;
  }

  public Range getAssignedLevelRange() {
    return this.levelAssign;
  }

  public boolean isSlayerOnly() {
    return this.slayerOnly;
  }

  public static SlayerTask getSlayerNpc(int npcId) {
    for (SlayerTask task : values()) {
      if (task.getNpcId() == npcId) {
        return task;
      }
    }
    return null;
  }

  public static boolean isSlayerNpc(int npcId) {
    return getSlayerNpc(npcId) != null;
  }

}
