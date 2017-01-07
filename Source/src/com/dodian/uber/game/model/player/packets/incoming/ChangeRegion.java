package com.dodian.uber.game.model.player.packets.incoming;

import com.dodian.uber.game.model.entity.player.Client;
import com.dodian.uber.game.model.entity.player.PlayerHandler;
import com.dodian.uber.game.model.player.packets.Packet;

public class ChangeRegion implements Packet {

  @Override
  public void ProcessPacket(Client client, int packetType, int packetSize) {
    client.replaceDoors();
    if (client.getPosition().getZ() == 0) {
      client.ReplaceObject2(2613, 3084, 3994, -3, 11);
      client.ReplaceObject2(2628, 3151, 2104, -3, 11);
      client.ReplaceObject2(2629, 3151, 2105, -3, 11);
      client.ReplaceObject2(2733, 3374, 6420, -1, 11);
      client.ReplaceObject2(2626, 3116, 2486, -1, 11);
      client.ReplaceObject2(2595, 3409, 133, -1, 10); // Dragon lair
      client.ReplaceObject2(3107, 3487, 2098, -1, 10); // Gold ore edgeville
      client.ReplaceObject2(3106, 3486, 2099, -1, 10); // Gold ore edgeville
      client.ReplaceObject2(2687, 3472, 2490, -1, 11);
      client.ReplaceObject2(2863, 3427, 3828, 0, 10); //Kalphite lair entrance!

      client.ReplaceObject2(2669, 3316, -1, -1, 11); // Remove door?
      /*
       * Danno: Box off new area from noobs =]
       */
      client.ReplaceObject2(2893, 9792, 2343, 0, 10);
      client.ReplaceObject2(2894, 9792, 2343, 0, 10);
      client.ReplaceObject2(2895, 9792, 2343, 0, 10);

      client.ReplaceObject2(2998, 3931, 6951, 0, 0);
      client.ReplaceObject2(2904, 9678, 6951, 0, 10);
      // slayer update
      // ReplaceObject2(2904, 9678, -1, -1, 11);
      // ReplaceObject2(2691, 9774, 2107, 0, 11);

      // Ancient slayer dunegon
      client.ReplaceObject(2661, 9815, 2391, 0, 0);
      client.ReplaceObject(2662, 9815, 2392, -2, 0);
    }
    for (int a = 0; a < PlayerHandler.players.length; a++) {
      Client o = client.getClient(a);
      if (a != client.getSlot() && client.validClient(a) && o.dbId > 0 && o.dbId == client.dbId) {
        System.out.println("logging out?");
        client.logout();
      }
    }
    if (client.inWildy() || client.duelFight) {
      client.getOutputStream().createFrameVarSize(104);
      client.getOutputStream().writeByteC(3);
      client.getOutputStream().writeByteA(1);
      client.getOutputStream().writeString("Attack");
      client.getOutputStream().endFrameVarSize();
    } else {
      client.getOutputStream().createFrameVarSize(104);
      client.getOutputStream().writeByteC(3);
      client.getOutputStream().writeByteA(0);
      client.getOutputStream().writeString("null");
      client.getOutputStream().endFrameVarSize();
    }
    client.updatePlayerDisplay();
    int wild = client.getWildLevel();
    if (wild > 0) {
      client.setWildLevel(wild);
    } else {
      client.updatePlayerDisplay();
    }
    if (!client.pLoaded) {
      client.pLoaded = true;
    }
    if (!client.IsPMLoaded) {
      client.refreshFriends();
      client.IsPMLoaded = true;
    }
  }

}
