package com.dodian.uber.game.model.player.packets;

import com.dodian.uber.game.model.entity.player.Client;

public interface Packet {

  public void ProcessPacket(Client client, int packetType, int packetSize);

}
