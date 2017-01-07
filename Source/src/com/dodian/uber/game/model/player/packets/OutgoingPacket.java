package com.dodian.uber.game.model.player.packets;

import com.dodian.uber.game.model.entity.player.Client;

public interface OutgoingPacket {

  public abstract void send(Client client);

}
