package com.dodian.uber.game.model.player.packets.outgoing;

import com.dodian.uber.game.model.entity.player.Client;
import com.dodian.uber.game.model.player.packets.OutgoingPacket;

public class SendSideTab implements OutgoingPacket {

  private int tabId;

  public SendSideTab(int tabId) {
    this.tabId = tabId;
  }

  @Override
  public void send(Client client) {
    client.getOutputStream().createFrame(106);
    client.getOutputStream().writeByteC(tabId);
    client.flushOutStream();
  }

}
