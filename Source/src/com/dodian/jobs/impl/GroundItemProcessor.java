package com.dodian.jobs.impl;

import com.dodian.uber.game.model.Position;
import com.dodian.uber.game.model.entity.player.Client;
import com.dodian.uber.game.model.entity.player.PlayerHandler;
import com.dodian.uber.game.model.item.GameItem;
import com.dodian.uber.game.model.item.Ground;
import com.dodian.uber.game.model.item.GroundItem;
import com.dodian.uber.game.model.player.packets.outgoing.CreateGroundItem;
import com.dodian.uber.game.Server;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

@DisallowConcurrentExecution

public class GroundItemProcessor implements Job {
  public void execute(JobExecutionContext context) throws JobExecutionException {

    if (Ground.items.size() < 0) {
      return;
    }
    long now = System.currentTimeMillis();
    for (GroundItem item : Ground.items) {
      if (!item.visible && now - item.dropped >= 30000) {
        for (int i = 0; i < PlayerHandler.players.length; i++) {
          Client p = Server.playerHandler.getClient(i);
          if (Server.playerHandler.validClient(i) && i != item.dropper && Math.abs(p.getPosition().getX() - item.x) < 30
              && Math.abs(p.getPosition().getY() - item.y) < 30) {
            p.send(new CreateGroundItem(new GameItem(item.id, item.amount), new Position(item.x, item.y)));
          }
        }
        item.visible = true;
      }
      if (item.visible && now - item.dropped >= 120000) {
        Ground.deleteItem(item, -1);
      }
    }

  }

}