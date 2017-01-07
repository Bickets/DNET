package com.dodian.uber.game.model.item;

import com.dodian.uber.game.Server;
import com.dodian.uber.game.model.Position;
import com.dodian.uber.game.model.player.packets.outgoing.CreateGroundItem;

public class GroundItem {
  public int x, y, id, amount, dropper, dropperId = -1;
  public long dropped = 0;
  public boolean visible = false;

  public GroundItem(int x, int y, int id, int amount, int dropper, boolean npc) {
    this.x = x;
    this.y = y;
    this.id = id;
    this.amount = amount;
    this.dropper = dropper;
    dropped = System.currentTimeMillis();
    if (dropper > 0 && Server.playerHandler.validClient(dropper)) {
      Server.playerHandler.getClient(dropper).send(new CreateGroundItem(new GameItem(id, amount), new Position(x, y)));
      dropperId = Server.playerHandler.getClient(dropper).dbId;
    }
  }

}
