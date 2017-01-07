package com.dodian.uber.game.model.player.packets.outgoing;

import com.dodian.uber.game.model.entity.player.Client;
import com.dodian.uber.game.model.player.packets.OutgoingPacket;

public class RemoveInterfaces implements OutgoingPacket {

  @Override
  public void send(Client client) {
    client.IsBanking = false;
    client.currentSkill = -1;
    client.getOutputStream().createFrame(219);
    client.flushOutStream();
  }

}
