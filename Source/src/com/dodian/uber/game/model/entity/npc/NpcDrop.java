/**
 * 
 */
package com.dodian.uber.game.model.entity.npc;

/**
 * @author Owner
 *
 */
public class NpcDrop {

  private int id, amount;
  private double percent;
  private boolean rareShout = false;

  public NpcDrop(int id, int amount, double percent, boolean shout) {
    this.id = id;
    this.amount = amount;
    this.percent = percent;
    this.rareShout = shout;
  }

  /**
   * Will this item drop?
   * 
   * @return dropping or not
   */
  public boolean drop() {
    return (Math.random() * 100) <= percent;
  }

  /**
   * @return the id
   */
  public int getId() {
    return id;
  }

  /**
   * @return the amount
   */
  public int getAmount() {
    return amount;
  }

  public boolean rareShout() {
    return rareShout;
  }

}
