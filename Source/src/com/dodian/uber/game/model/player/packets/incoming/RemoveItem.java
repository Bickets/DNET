package com.dodian.uber.game.model.player.packets.incoming;

import com.dodian.uber.game.Server;
import com.dodian.uber.game.model.ShopHandler;
import com.dodian.uber.game.model.entity.player.Client;
import com.dodian.uber.game.model.player.packets.Packet;
import com.dodian.uber.game.model.player.packets.outgoing.RemoveInterfaces;
import com.dodian.uber.game.model.player.packets.outgoing.SendMessage;

public class RemoveItem implements Packet {

  @Override
  public void ProcessPacket(Client client, int packetType, int packetSize) {
    int interfaceID = client.getInputStream().readUnsignedWordA();
    int removeSlot = client.getInputStream().readUnsignedWordA();
    int removeID = client.getInputStream().readUnsignedWordA();
    if (client.playerRights == 2) {
      client.println_debug("RemoveItem: " + removeID + " InterID: " + interfaceID + " slot: " + removeSlot);
    }
    if (interfaceID == 3322 && client.inDuel) { // remove from bag to duel
      // window
      client.stakeItem(removeID, removeSlot, 1);
    } else if (interfaceID == 6669 && client.inDuel) { // remove from duel
      // window
      client.fromDuel(removeID, removeSlot, 1);
    } else if (interfaceID == 1688) {
      if (client.getEquipment()[removeSlot] > 0) {
        client.remove(removeID, removeSlot, false);
      }
    } else if (interfaceID == 5064) { // remove from bag to bank
      client.bankItem(removeID, removeSlot, 1);
    } else if (interfaceID == 5382) { // remove from bank
      client.fromBank(removeID, removeSlot, 1);
    } else if (interfaceID == 3322 && client.inTrade) { // remove from bag to
                                                        // trade
      // window
      client.tradeItem(removeID, removeSlot, 1);
    } else if (interfaceID == 3415 && client.inTrade) { // remove from trade
                                                        // window
      client.fromTrade(removeID, removeSlot, 1);
    } else if (interfaceID >= 4233 && interfaceID <= 4245) {
      client.startGoldCrafting(interfaceID, removeSlot, 1);
    } else if (interfaceID == 3823) { // Show value to sell items
      boolean IsIn = false;
      if(Server.itemManager.getShopBuyValue(removeID) < 1) {
    	  client.send(
    	            new SendMessage("You cannot sell " + client.GetItemName(removeID).toLowerCase() + " in this store."));
          return;	
      }
      if (ShopHandler.ShopSModifier[client.MyShopID] > 1) {
        for (int j = 0; j <= ShopHandler.ShopItemsStandard[client.MyShopID]; j++) {
        	if (removeID == (ShopHandler.ShopItems[client.MyShopID][j] - 1)) {
            IsIn = true;
            break;
          }
        }
      } else {
        IsIn = true;
      }
      if (IsIn == false) {
        client.send(new SendMessage("You cannot sell " + client.GetItemName(removeID).toLowerCase() + " in this store."));
      } else {
        int ShopValue = (int) Math.floor(client.GetShopBuyValue(removeID, 1, removeSlot));
        String ShopAdd = "";

        if (ShopValue >= 1000 && ShopValue < 1000000) {
          ShopAdd = " (" + (ShopValue / 1000) + "K)";
        } else if (ShopValue >= 1000000) {
          ShopAdd = " (" + (ShopValue / 1000000) + " million)";
        }
        client.send(
            new SendMessage(client.GetItemName(removeID) + ": shop will buy for " + ShopValue + " coins" + ShopAdd));
      }
    } else if (interfaceID == 3900) { // Show value to buy items
      int ShopValue = (int) Math.floor(client.GetShopSellValue(removeID, 0, removeSlot));
      String ShopAdd = "";

      if (ShopValue >= 1000 && ShopValue < 1000000) {
        ShopAdd = " (" + (ShopValue / 1000) + "K)";
      } else if (ShopValue >= 1000000) {
        ShopAdd = " (" + (ShopValue / 1000000) + " million)";
      }
      client
          .send(new SendMessage(client.GetItemName(removeID) + ": currently costs " + ShopValue + " coins" + ShopAdd));
    } else if (interfaceID >= 1119 && interfaceID <= 1123) { // Smithing
      if (client.smithing[2] > 0) {
        client.smithing[4] = removeID;
        client.smithing[0] = 1;
        client.smithing[5] = 1;
        client.send(new RemoveInterfaces());
      } else {
        client.send(new SendMessage("Illigal Smithing !"));
      }
    }
    client.CheckGear();
  }

}
