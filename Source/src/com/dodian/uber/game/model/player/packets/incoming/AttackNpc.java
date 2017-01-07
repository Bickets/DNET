package com.dodian.uber.game.model.player.packets.incoming;

import com.dodian.uber.game.Server;
import com.dodian.uber.game.event.Event;
import com.dodian.uber.game.event.EventManager;
import com.dodian.uber.game.model.WalkToTask;
import com.dodian.uber.game.model.entity.npc.Npc;
import com.dodian.uber.game.model.entity.player.Client;
import com.dodian.uber.game.model.player.packets.Packet;

public class AttackNpc implements Packet {

  @Override
  public void ProcessPacket(Client client, int packetType, int packetSize) {
    if (client.deathStage < 1) {
      int npcIndex = client.getInputStream().readUnsignedWordA();
      Npc tempNpc = Server.npcManager.getNpc(npcIndex);
      if (tempNpc == null)
        return;
      int NPCID = tempNpc.getId();

      final WalkToTask task = new WalkToTask(WalkToTask.Action.ATTACK_NPC, NPCID, tempNpc.getPosition());
      client.setWalkToTask(task);
      EventManager.getInstance().registerEvent(new Event(600) {

        @Override
        public void execute() {

          if (client == null || client.disconnected) {
            this.stop();
            return;
          }

          if (client.getWalkToTask() != task) {
            this.stop();
            return;
          }

          if (!client.goodDistanceEntity(tempNpc, 1)) {
            return;
          }
          client.startAttackNpc(npcIndex);
          client.setWalkToTask(null);
          this.stop();
        }

      });
    }
  }

}
