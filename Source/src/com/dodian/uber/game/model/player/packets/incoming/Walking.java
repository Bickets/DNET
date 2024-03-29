package com.dodian.uber.game.model.player.packets.incoming;

import com.dodian.uber.game.model.UpdateFlag;
import com.dodian.uber.game.model.entity.player.Client;
import com.dodian.uber.game.model.entity.player.Player;
import com.dodian.uber.game.model.player.packets.Packet;
import com.dodian.uber.game.model.player.packets.outgoing.RemoveInterfaces;
import com.dodian.uber.game.model.player.packets.outgoing.SendMessage;
import com.dodian.utilities.Utils;

public class Walking implements Packet {

  @Override
  public void ProcessPacket(Client client, int packetType, int packetSize) {
    if (packetType == 248)
      packetSize -= 14;

    if (packetType != 98) {
      client.setWalkToTask(null);
    }

    if (client.randomed) {
      return;
    }
    if (!client.pLoaded) {
      return;
    }
    if (client.inTrade) {
      return;
    }
    if (client.NpcDialogue == 1001)
      client.setInterfaceWalkable(-1);
    client.convoId = -1;
    client.resetAction();
    long currentTime = System.currentTimeMillis();
    if (currentTime < client.snaredUntil) {
      client.send(new SendMessage("You are ensnared!"));
      return;
    }
    if (!client.validClient) {
      client.send(new SendMessage("You can't move on this account"));
      return;
    }
    client.resetAttackNpc();
    client.send(new RemoveInterfaces());
    client.rerequestAnim();
    if (client.deathStage == 0) {
      client.newWalkCmdSteps = packetSize - 5;
      if (client.inDuel/* && (duelRule[5] || duelRule[9]) */) {
        if (client.newWalkCmdSteps > 0)
          client.send(new SendMessage("You cannot move during this duel!"));
        client.newWalkCmdSteps = 0;
        return;
      }
      if (client.newWalkCmdSteps % 2 != 0) {
        client.println_debug("Warning: walkTo(" + packetType + ") command malformed: "
            + Utils.Hex(client.getInputStream().buffer, 0, packetSize));
      }
      client.newWalkCmdSteps /= 2;
      if (++client.newWalkCmdSteps > Player.WALKING_QUEUE_SIZE) {
        client.newWalkCmdSteps = 0;
        return;
      }
      int firstStepX = client.getInputStream().readSignedWordBigEndianA();
      firstStepX -= client.mapRegionX * 8;
      for (int i = 1; i < client.newWalkCmdSteps; i++) {
        client.newWalkCmdX[i] = client.getInputStream().readSignedByte();
        client.newWalkCmdY[i] = client.getInputStream().readSignedByte();
        client.tmpNWCX[i] = client.newWalkCmdX[i];
        client.tmpNWCY[i] = client.newWalkCmdY[i];
      }
      client.newWalkCmdX[0] = client.newWalkCmdY[0] = client.tmpNWCX[0] = client.tmpNWCY[0] = 0;
      int firstStepY = client.getInputStream().readSignedWordBigEndian();
      firstStepY -= client.mapRegionY * 8;
      client.newWalkCmdIsRunning = client.getInputStream().readSignedByteC() == 1;
      for (int i = 0; i < client.newWalkCmdSteps; i++) {
        client.newWalkCmdX[i] += firstStepX;
        client.newWalkCmdY[i] += firstStepY;
      }
      // stairs check
      if (client.stairs > 0) {
        client.resetStairs();
      }
      // woodcutting check
      if (client.woodcuttingIndex >= 0) {
        client.rerequestAnim();
        client.resetWC();
      }
      // attack check
      if (client.IsAttacking == true) {
        client.ResetAttack();
      }
      // smithing check
      if (client.smithing[0] > 0) {
        client.getUpdateFlags().setRequired(UpdateFlag.APPEARANCE, true);
        client.rerequestAnim();
        client.resetSM();
        client.send(new RemoveInterfaces());
      }
      // Npc Talking
      if (client.NpcDialogue > 0) {
        client.NpcDialogue = 0;
        client.NpcTalkTo = 0;
        client.NpcDialogueSend = false;
        client.send(new RemoveInterfaces());
      }
      // banking
      if (client.IsBanking == true) {
        client.send(new RemoveInterfaces());
      }
      // shopping
      if (client.IsShopping == true) {
        client.IsShopping = false;
        client.MyShopID = 0;
        client.UpdateShop = false;
        client.send(new RemoveInterfaces());
      }
      // trading

    }
  }

}