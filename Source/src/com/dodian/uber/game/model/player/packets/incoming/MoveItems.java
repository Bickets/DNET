package com.dodian.uber.game.model.player.packets.incoming;

import com.dodian.uber.game.model.entity.player.Client;
import com.dodian.uber.game.model.player.packets.Packet;

public class MoveItems implements Packet {

  @Override
  public void ProcessPacket(Client client, int packetType, int packetSize) {
    int somejunk = client.getInputStream().readUnsignedWordA(); // junk
    int itemFrom = client.getInputStream().readUnsignedWordA(); // slot1
    int itemTo = (client.getInputStream().readUnsignedWordA() - 128); // slot2

    client.moveItems(itemFrom, itemTo, somejunk);
  }

}
