package com.dodian.uber.game.model.player.packets.incoming;

import com.dodian.uber.game.model.entity.player.Client;
import com.dodian.uber.game.model.entity.player.PlayerHandler;
import com.dodian.uber.game.model.player.packets.Packet;
import com.dodian.uber.game.model.player.packets.outgoing.SendMessage;

public class AttackPlayer implements Packet {

  @Override
  public void ProcessPacket(Client client, int packetType, int packetSize) {
    int victim = client.getInputStream().readSignedWordBigEndian();
    // client.getCombat().initialize(PlayerHandler.players[victim]);
    if (client.actionTimer == 0) {
      client.AttackingOn = victim;
      if (!client.canAttack) {
        client.send(new SendMessage("You cannot attack your oppenent yet!"));
        return;
      }
      client.faceNPC(32768 + client.AttackingOn);
      if (client.AttackingOn >= PlayerHandler.players.length || client.AttackingOn < 1) {
        client.AttackingOn = -1;
        client.IsAttacking = false;
        return;
      }
      client.IsAttacking = true;
      client.actionTimer = 6;
    }
  }

}
