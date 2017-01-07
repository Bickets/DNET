package com.dodian.uber.game.model.player.packets.incoming;

import com.dodian.uber.game.model.entity.player.Client;
import com.dodian.uber.game.model.player.packets.Packet;

public class RemoveIgnore implements Packet {

  @Override
  public void ProcessPacket(Client client, int packetType, int packetSize) {
    long igtorem = client.getInputStream().readQWord();
    client.removeIgnore(igtorem);
  }

}
