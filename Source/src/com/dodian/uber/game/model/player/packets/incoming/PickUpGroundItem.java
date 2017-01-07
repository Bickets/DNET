package com.dodian.uber.game.model.player.packets.incoming;

import com.dodian.uber.game.model.entity.player.Client;
import com.dodian.uber.game.model.player.packets.Packet;
import com.dodian.uber.game.model.player.packets.outgoing.SendMessage;

public class PickUpGroundItem implements Packet {

  @Override
  public void ProcessPacket(Client client, int packetType, int packetSize) {
    int itemY = client.getInputStream().readSignedWordBigEndian();
    int itemID = client.getInputStream().readUnsignedWord();
    int itemX = client.getInputStream().readSignedWordBigEndian();
    if (!client.hasSpace()) {
      client.send(new SendMessage("Your inventory is full!"));
      return;
    }
    if (System.currentTimeMillis() - client.lastAction < 500) {
      return;
    }
    client.lastAction = System.currentTimeMillis();
    if (client.getPosition().getX() != itemX || client.getPosition().getY() != itemY) {
      client.pickupWanted = true;
      client.pickX = itemX;
      client.pickY = itemY;
      client.pickId = itemID;
      client.pickTries = 100;
    } else {
      client.pickUpItem(itemID, itemX, itemY);
    }
  }

}
