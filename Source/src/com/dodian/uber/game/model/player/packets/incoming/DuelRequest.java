package com.dodian.uber.game.model.player.packets.incoming;

import com.dodian.uber.game.model.entity.player.Client;
import com.dodian.uber.game.model.player.packets.Packet;
import com.dodian.uber.game.model.player.packets.outgoing.SendMessage;
import com.dodian.utilities.Utils;

public class DuelRequest implements Packet {

  @Override
  public void ProcessPacket(Client client, int packetType, int packetSize) {
    int PID = (Utils.HexToInt(client.getInputStream().buffer, 0, packetSize) / 1000);
    client.getClient(PID);
    if (!client.validClient(PID)) {
      return;
    }
    if (client.inTrade || client.inDuel || (client.duelFight && client.duel_with != PID)) {
      client.send(new SendMessage("You are busy at the moment"));
      return;
    }
    client.duelReq(PID);
  }

}
