package com.dodian.uber.game.model.player.packets.incoming;

import com.dodian.uber.game.Server;
import com.dodian.uber.game.model.entity.player.Client;
import com.dodian.uber.game.model.player.packets.Packet;

public class MouseClicks implements Packet {

  @Override
  public void ProcessPacket(Client client, int packetType, int packetSize) {
    int in = client.getInputStream().readDWord();
    if (Server.world == 6)
      System.out.println("click " + in);
    client.updatePlayerDisplay();
  }

}
