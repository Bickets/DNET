package com.dodian.uber.game.model.player.packets.incoming;

import com.dodian.uber.game.Server;
import com.dodian.uber.game.model.entity.player.Client;
import com.dodian.uber.game.model.entity.player.Player;
import com.dodian.uber.game.model.player.packets.Packet;

public class BankAll implements Packet {

  @Override
  public void ProcessPacket(Client client, int packetType, int packetSize) {
    int removeSlot = client.getInputStream().readUnsignedWordA();
    int interfaceID = client.getInputStream().readUnsignedWord();
    int removeID = client.getInputStream().readUnsignedWordA();

    if (interfaceID == 5064) { // remove from bag to bank
      if (Server.itemManager.isStackable(Player.id)) {
        client.bankItem(client.playerItems[removeSlot] - 1, removeSlot, client.playerItemsN[removeSlot]);
      } else {
        client.bankItem(client.playerItems[removeSlot] - 1, removeSlot,
            client.itemAmount(client.playerItems[removeSlot]));
      }
    } else if (interfaceID == 5382) { // remove from bank
      client.fromBank(client.bankItems[removeSlot], removeSlot, client.bankItemsN[removeSlot]);
    } else if (interfaceID == 3322 && client.inTrade) { // remove from bag
      // to trade window
      if (Server.itemManager.isStackable(removeID)) {
        client.tradeItem(removeID, removeSlot, client.playerItemsN[removeSlot]);
      } else {
        client.tradeItem(removeID, removeSlot, 28);
      }
    } else if (interfaceID == 3322 && client.inDuel) { // remove from bag to
      // duel window
      if (Server.itemManager.isStackable(removeID) || Server.itemManager.isNote(removeID)) {
        client.stakeItem(removeID, removeSlot, client.playerItemsN[removeSlot]);
      } else {
        client.stakeItem(removeID, removeSlot, 28);
      }
    } else if (interfaceID == 6669 && client.inDuel) { // remove from duel
      // window
      client.fromDuel(removeID, removeSlot, client.offeredItems.get(removeSlot).getAmount());
    } else if (interfaceID == 3415 && client.inTrade) { // remove from trade
                                                        // window
      if (Server.itemManager.isStackable(removeID)) {
        client.fromTrade(removeID, removeSlot, client.offeredItems.get(removeSlot).getAmount());
      } else {
        client.fromTrade(removeID, removeSlot, 28);
      }
    } else if (interfaceID == 3823) { // Show value to sell items
      client.sellItem(removeID, removeSlot, 10);
    } else if (interfaceID == 3900) { // Show value to buy items
      client.buyItem(removeID, removeSlot, 10);
    }

  }

}
