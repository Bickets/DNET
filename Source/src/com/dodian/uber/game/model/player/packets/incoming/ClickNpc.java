package com.dodian.uber.game.model.player.packets.incoming;

import com.dodian.uber.game.Server;
import com.dodian.uber.game.event.Event;
import com.dodian.uber.game.event.EventManager;
import com.dodian.uber.game.model.WalkToTask;
import com.dodian.uber.game.model.entity.npc.Npc;
import com.dodian.uber.game.model.entity.player.Client;
import com.dodian.uber.game.model.entity.player.Player;
import com.dodian.uber.game.model.entity.player.PlayerHandler;
import com.dodian.uber.game.model.player.packets.Packet;
import com.dodian.uber.game.model.player.packets.outgoing.NpcDialogueHead;
import com.dodian.uber.game.model.player.packets.outgoing.SendMessage;
import com.dodian.uber.game.model.player.packets.outgoing.SendString;

public class ClickNpc implements Packet {

  @Override
  public void ProcessPacket(Client client, int packetType, int packetSize) {
    int npcIndex = client.getInputStream().readSignedWordBigEndian();
    Npc tempNpc = Server.npcManager.getNpc(npcIndex);
    if (tempNpc == null)
      return;
    int NPCID = tempNpc.getId();

    final WalkToTask task = new WalkToTask(WalkToTask.Action.NPC_FIRST_CLICK, NPCID, tempNpc.getPosition());
    client.setWalkToTask(task);
    if (client.randomed) {
      return;
    }
    if (NPCID == 43) {
      if (client.playerHasItem(1735)) {
        client.addItem(1737, 1);
      } else {
        client.send(new SendMessage("You need some shears to shear this sheep!"));
      }
      return;
    }
    EventManager.getInstance().registerEvent(new Event(600) {

      @Override
      public void execute() {

        if (client == null || client.disconnected) {
          this.stop();
          return;
        }

        if (client.getWalkToTask() != task) {
          this.stop();
          return;
        }

        if (!client.goodDistanceEntity(tempNpc, 1)) {
          return;
        }

        clickNpc(client, tempNpc);
        client.setWalkToTask(null);
        this.stop();
      }

    });
  }

  public void clickNpc(Client client, Npc tempNpc) {
    int NPCID = tempNpc.getId();
    client.faceNPC(tempNpc.getSlot());
    // TurnPlayerTo(tempNpc.getX(), tempNpc.getY());
    client.skillX = tempNpc.getPosition().getX();
    client.setSkillY(tempNpc.getPosition().getY());
    if (NPCID == 804) {
      client.NpcWanneTalk = 804;
      // openTan();
    }
    client.startFishing(NPCID, 1);
    if (NPCID == 494 || NPCID == 495) { /* Banking */
      client.NpcWanneTalk = 1;
      client.convoId = 0;
    } else if (NPCID == 553) { /* Aubury */
      client.NpcWanneTalk = 3;
      client.convoId = 3;
    } else if (NPCID == 1597) {
      client.NpcWanneTalk = 18;
    } else if (NPCID == 2676) {
      client.NpcWanneTalk = 21;
    } else if (NPCID == 398) {
      client.NpcWanneTalk = 398;
      client.sendFrame200(4883, 398);
      client.send(new SendString(client.GetNpcName(398), 4884));
      if (client.premium) {
        client.send(new SendString("Welcome to the Guild of Legends", 4885));
      } else {
        client.send(new SendString("You must be a premium member to enter", 4885));
        client.send(new SendString("Visit Dodian.net to subscribe", 4886));
      }
      client.send(new NpcDialogueHead(398, 4883));
      client.sendFrame164(4882);
      client.NpcDialogueSend = true;
      if (client.premium) {
        client.ReplaceObject(2728, 3349, 2391, 0, 0);
        client.ReplaceObject(2729, 3349, 2392, -2, 0);
      }
    } else if (NPCID == 376 && client.playerRights == 2) {
      client.triggerTele(2772, 3234, 0, false);
    } else if (NPCID == 162) {
      client.NpcWanneTalk = 163;
      client.convoId = 162;
    } else if (NPCID == 659) {
      client.NpcWanneTalk = 1000;
      client.convoId = 1001;
    } else if (NPCID == 2825) {
      client.NpcWanneTalk = 1002;
      client.convoId = -1;
    } else if (NPCID == 1596) {
      if (client.determineCombatLevel() < 10) {
        client.send(new SendMessage("You need to be level 10 combat or higher for slayer!"));
        return;
      }
      client.NpcWanneTalk = 11;
      client.convoId = 1;
    } else if (NPCID == 587) {
      client.NpcWanneTalk = 16;
      client.convoId = 2;
    } else if (NPCID == 520) {
      client.NpcWanneTalk = 19;
      client.convoId = 4;
    } else if (NPCID == 943) {
      int num = 0;
      for (Player p : PlayerHandler.players) {
        if (p != null && p.wildyLevel > 0)
          num++;
      }
      tempNpc.setText("There are currently " + num + " people in the wilderness");
    } else {
      client.println_debug("atNPC 1: " + NPCID);
    }
  }

}
