package com.dodian.uber.game.model.player.packets.incoming;

import com.dodian.uber.game.model.entity.player.Client;
import com.dodian.uber.game.model.player.packets.Packet;
import com.dodian.utilities.Utils;

public class ClickingStuff implements Packet {

  @Override
  public void ProcessPacket(Client client, int packetType, int packetSize) {
    int interfaceID = client.getInputStream().readSignedByte();
    // if(actionButtonId == 26018) {
    if (client.inDuel && !client.duelFight) {
      client.declineDuel();
    }
    // }
    /*
     * if (duelFight && duelWin) { acceptDuelWon(); } else { // sendMessage(
     * "You didn't win the duel!"); }
     */
    if (client.inTrade && System.currentTimeMillis() - client.lastButton > 1000) {
      client.lastButton = System.currentTimeMillis();
      client.declineTrade();
    }
    if (client.IsShopping == true) {
      client.IsShopping = false;
      client.MyShopID = 0;
      client.UpdateShop = false;
    }
    if (client.IsBanking == true) {
      client.IsBanking = false;
    }

    if (Utils.HexToInt(client.getInputStream().buffer, 0, packetSize) != 63363
        && Utils.HexToInt(client.getInputStream().buffer, 0, packetSize) != 0) {
      client.println_debug("handled packet [" + packetType + ", InterFaceId: " + interfaceID + ", size=" + packetSize
          + "]: ]" + Utils.Hex(client.getInputStream().buffer, 1, packetSize) + "[");
      client.println_debug("Action Button: " + Utils.HexToInt(client.getInputStream().buffer, 0, packetSize));
    }
  }

}
