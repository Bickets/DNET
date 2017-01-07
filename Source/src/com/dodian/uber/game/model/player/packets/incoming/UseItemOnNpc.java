package com.dodian.uber.game.model.player.packets.incoming;

import com.dodian.uber.game.model.entity.player.Client;
import com.dodian.uber.game.model.player.packets.Packet;

public class UseItemOnNpc implements Packet {

  @Override
  public void ProcessPacket(Client client, int packetType, int packetSize) {
    client.getInputStream().readSignedWordBigEndianA();
    client.getInputStream().readSignedWordBigEndianA();
    client.getInputStream().readSignedWordBigEndian();
    client.getInputStream().readSignedWordBigEndianA();
  }

}
