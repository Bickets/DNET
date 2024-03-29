/**
 * 
 */
package com.dodian.uber.game.model.entity.npc;

import java.sql.ResultSet;
import java.util.ArrayList;

/**
 * @author Owner
 *
 */
public class NpcData {
  
  private ArrayList<NpcDrop> drops = new ArrayList<NpcDrop>();
  private String name = "";
  private int attackEmote, deathEmote, respawn, combat, taskId, size;
  private int[] level = new int[7];

  public NpcData(ResultSet row) {
    try {
      attackEmote = row.getInt("attackEmote");
      deathEmote = row.getInt("deathEmote");
      name = row.getString("name");
      respawn = row.getInt("respawn");
      combat = row.getInt("combat");
      taskId = row.getInt("taskid");
      size = row.getInt("size");
      level[0] = row.getInt("defence");
      level[1] = row.getInt("attack");
      level[2] = row.getInt("strength");
      level[3] = row.getInt("hitpoints");
      level[4] = row.getInt("ranged");
      level[5] = 0; // a npc got no prayer!
      level[6] = row.getInt("magic");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void addDrop(int id, int amount, double percent, boolean rareShout) {
    drops.add(new NpcDrop(id, amount, percent, rareShout));
  }

  /**
   * @return the drops
   */
  public ArrayList<NpcDrop> getDrops() {
    return drops;
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @return the attackEmote
   */
  public int getAttackEmote() {
    return attackEmote;
  }

  public void setAttackEmote(int animationId) {
    this.attackEmote = animationId;
  }

  /**
   * @return the deathEmote
   */
  public int getDeathEmote() {
    return deathEmote;
  }

  /**
   * @return the respawn
   */
  public int getRespawn() {
    return respawn;
  }

  /**
   * @return the combat
   */
  public int getCombat() {
    return combat;
  }

  /**
   * @return the health
   */
  public int getHP() {
    return level[3];
  }

  /**
   * @return the taskId NEEDS UPDATING, NOT IN SQL TABLES. RETURNS -1 UNTIL
   *         FIXED.
   */
  public int getTaskId() {
    return taskId;
  }

  /**
   * Returns the amount of tiles that the npc occupies on the game map.
   * 
   * @return
   */
  public int getSize() {
    return size;
  }

  public int[] getLevel() {
    return level;
  }
}
