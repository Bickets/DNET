package com.dodian.uber.game.model.player.packets.outgoing;

import com.dodian.uber.game.model.entity.player.Client;
import com.dodian.uber.game.model.player.packets.OutgoingPacket;

public class CameraReset implements OutgoingPacket {

  @Override
  public void send(Client client) {
    client.getOutputStream().createFrame(107);
    client.flushOutStream();
  }

}
