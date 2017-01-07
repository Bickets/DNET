package com.dodian.uber.game.model.player.packets.incoming;

import com.dodian.uber.game.model.entity.player.Client;
import com.dodian.uber.game.model.player.packets.Packet;
import com.dodian.uber.game.model.player.packets.outgoing.SendMessage;

public class UseItemOnPlayer implements Packet {

  @Override
  public void ProcessPacket(Client client, int packetType, int packetSize) {
    client.getInputStream().readSignedWordBigEndianA();
    client.getInputStream().readSignedWordBigEndian();
    client.getInputStream().readUnsignedWordA();
    
    int CrackerSlot = client.getInputStream().readSignedWordBigEndian();
    int CrackerID = client.playerItems[CrackerSlot];

    CrackerID -= 1; // Only to fix the ID !
    if (CrackerID == 962 && client.playerHasItem(962)) {
      client.send(new SendMessage("Christmas crackers are no longer usable.  Contact a mod"));
      for (int c = 0; c < 5; c++) {
        client.modYell("PLAYER " + client.getPlayerName() + " IS ATTEMPTING TO USE CRACKER");
      }
    }
  }

}
