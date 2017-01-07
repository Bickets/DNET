package com.dodian.uber.game.model.player.packets.incoming;

import com.dodian.uber.game.model.entity.player.Client;
import com.dodian.uber.game.model.player.packets.Packet;
import com.dodian.uber.game.model.player.packets.outgoing.RemoveInterfaces;
import com.dodian.uber.game.model.player.packets.outgoing.SendMessage;

public class Bank5 implements Packet {

  @Override
  public void ProcessPacket(Client client, int packetType, int packetSize) {
    int interfaceID = client.getInputStream().readSignedWordBigEndianA();
    int removeID = client.getInputStream().readSignedWordBigEndianA();
    int removeSlot = client.getInputStream().readSignedWordBigEndian();
    client.println_debug("RemoveItem 5: " + removeID + " InterID: " + interfaceID + " slot: " + removeSlot);
    if (interfaceID == 3322 && client.inDuel) { // remove from bag to duel
      // window
      client.stakeItem(removeID, removeSlot, 5);
    } else if (interfaceID == 6669) { // remove from duel window
      client.fromDuel(removeID, removeSlot, 5);
    } else if (interfaceID == 5064) { // remove from bag to bank
      client.bankItem(removeID, removeSlot, 5);
    } else if (interfaceID == 5382) { // remove from bank
      client.fromBank(removeID, removeSlot, 5);
    } else if (interfaceID == 3322 && client.inTrade) { // remove from bag to
                                                        // trade
      // window
      System.out.println("removeId: " + removeID);
      client.tradeItem(removeID, removeSlot, 5);
    } else if (interfaceID == 3415 && client.inTrade) { // remove from trade
                                                        // window
      client.fromTrade(removeID, removeSlot, 5);
    } else if (interfaceID >= 4233 && interfaceID <= 4245) {
      client.startGoldCrafting(interfaceID, removeSlot, 5);
    } else if (interfaceID == 3823) { // Show value to sell items
      client.sellItem(removeID, removeSlot, 1);
    } else if (interfaceID == 3900) { // Show value to buy items
      client.buyItem(removeID, removeSlot, 1);
    } else if (interfaceID >= 1119 && interfaceID <= 1123) { // Smithing
      if (client.smithing[2] > 0) {
        client.smithing[4] = removeID;
        client.smithing[0] = 1;
        client.smithing[5] = 5;
        client.send(new RemoveInterfaces());
      } else {
        client.send(new SendMessage("Illigal Smithing !"));
        client.println_debug("Illigal Smithing !");
      }
    }
  }

}
