package com.dodian.uber.game.model.player.packets.incoming;

import com.dodian.uber.game.model.entity.player.Client;
import com.dodian.uber.game.model.player.packets.Packet;
import com.dodian.utilities.Utils;

public class ItemOnObject implements Packet {

  @Override
  public void ProcessPacket(Client client, int packetType, int packetSize) {
    client.getInputStream().readSignedWordBigEndianA();
    int UsedOnObjectID = client.getInputStream().readUnsignedWordBigEndian();
    int UsedOnY = client.getInputStream().readSignedWordBigEndianA();
    int ItemSlot = client.getInputStream().readSignedWordBigEndianA() - 128;
    int UsedOnX = client.getInputStream().readUnsignedWordBigEndianA();
    int ItemID = client.getInputStream().readUnsignedWord();
    if (!client.playerHasItem(ItemID)) {
      return;
    }
    if (client.distanceToPoint(UsedOnX, UsedOnY) > 2) {
      return;
    }

    if (ItemID == 229 && UsedOnObjectID == 879) {
      client.filling = true;
    }
    if (ItemID == 1925 && UsedOnObjectID == 8689) {
      client.setFocus(UsedOnX, UsedOnY);
      client.deleteItem(ItemID, 1);
      client.addItem(ItemID + 2, 1);
    }
    if (UsedOnObjectID == 3994 || UsedOnObjectID == 11666) {
      if (ItemID == 2357) { // 2357 = gold
        client.showItemsGold();
        client.showInterface(4161);
      } else {
        for (int fi = 0; fi < Utils.smelt_frame.length; fi++) {
          client.sendFrame246(Utils.smelt_frame[fi], 150, Utils.smelt_bars[fi][0]);
        }
        client.sendFrame164(2400);
      }
    }
    if (UsedOnObjectID == 2781 || UsedOnObjectID == 2728) { // Cooking range!
      client.skillX = UsedOnX;
      client.setSkillY(UsedOnY);
      client.startCooking(ItemID);
    } else if (UsedOnObjectID == 2783) { // anvil
      int Type = client.CheckSmithing(ItemID, ItemSlot);

      if (Type != -1) {
        client.skillX = UsedOnX;
        client.setSkillY(UsedOnY);
        client.OpenSmithingFrame(Type);
      }
    } else {
      client.println_debug(
          "Item: " + ItemID + " - Used On Object: " + UsedOnObjectID + " -  X: " + UsedOnX + " - Y: " + UsedOnY);
    }
  }

}
