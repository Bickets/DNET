package com.dodian.uber.game.model.item;

import com.dodian.uber.game.Server;
import com.dodian.uber.game.model.entity.player.Client;
import com.dodian.uber.game.model.entity.player.PlayerHandler;

import java.util.concurrent.CopyOnWriteArrayList;

public class Ground {
  public static CopyOnWriteArrayList<GroundItem> items = new CopyOnWriteArrayList<GroundItem>();

  public static void deleteItem(GroundItem item, int dbId) {
    for (int i = 0; i < PlayerHandler.players.length; i++) {
      Client p = Server.playerHandler.getClient(i);
      if (Server.playerHandler.validClient(i)) {
        p.removeGroundItem(item.x, item.y, item.id);
      }
    }
    items.remove(item);
  }

}