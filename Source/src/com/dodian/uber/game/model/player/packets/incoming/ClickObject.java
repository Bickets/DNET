package com.dodian.uber.game.model.player.packets.incoming;

import com.dodian.uber.game.Constants;
import com.dodian.uber.game.Server;
import com.dodian.uber.game.event.Event;
import com.dodian.uber.game.event.EventManager;
import com.dodian.uber.game.model.Position;
import com.dodian.uber.game.model.UpdateFlag;
import com.dodian.uber.game.model.WalkToTask;
import com.dodian.uber.game.model.entity.player.Client;
import com.dodian.uber.game.model.entity.player.PlayerHandler;
import com.dodian.uber.game.model.item.Equipment;
import com.dodian.uber.game.model.object.DoorHandler;
import com.dodian.uber.game.model.object.RS2Object;
import com.dodian.uber.game.model.player.packets.Packet;
import com.dodian.uber.game.model.player.packets.outgoing.SendMessage;
import com.dodian.uber.game.model.player.packets.outgoing.Sound;
import com.dodian.uber.game.model.player.skills.Agility;
import com.dodian.uber.game.model.player.skills.Skill;
import com.dodian.uber.game.model.player.skills.Thieving2;
import com.dodian.utilities.Misc;
import com.dodian.utilities.Utils;

public class ClickObject implements Packet {

  @Override
  public void ProcessPacket(Client client, int packetType, int packetSize) {
    int objectX = client.getInputStream().readSignedWordBigEndianA();
    int objectID = client.getInputStream().readUnsignedWord();
    int objectY = client.getInputStream().readUnsignedWordA();

    final WalkToTask task = new WalkToTask(WalkToTask.Action.OBJECT_FIRST_CLICK, objectID,
        new Position(objectX, objectY));
    // GameObjectDef def = Misc.getObject(objectID, objectX, objectY,
    // client.getPosition().getZ());
    // GameObjectData object = GameObjectData.forId(task.getWalkToId());
    client.setWalkToTask(task);
    if (client.randomed) {
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
        Position objectPosition = Misc.goodDistanceObject(task.getWalkToPosition().getX(),
            task.getWalkToPosition().getY(), client.getPosition().getX(), client.getPosition().getY(), objectX, objectY,
            client.getPosition().getZ());
        // Position objectPosition = Misc.goodDistanceObject(objectX, objectY,
        // client.getPosition().getX(), client.getPosition().getY(),
        // task.getWalkToPosition().getX(), task.getWalkToPosition().getY(),
        // client.getPosition().getZ());
        // Position objectPosition =
        // Misc.goodDistanceObject(task.getWalkToPosition().getX(),
        // task.getWalkToPosition().getY(), client.getPosition().getX(),
        // client.getPosition().getY(), object.getSizeX(def.getFace()),
        // object.getSizeY(def.getFace()), client.getPosition().getZ());
        if (objectPosition == null)
          return;

        atObject(client, task.getWalkToId(), task.getWalkToPosition());
        client.setWalkToTask(null);
        this.stop();
      }

    });
  }

  public void atObject(Client client, int objectID, Position objectPosition) {
    if (!client.validClient || client.randomed) {
      return;
    }
    int xDiff = Math.abs(client.getPosition().getX() - objectPosition.getX());
    int yDiff = Math.abs(client.getPosition().getY() - objectPosition.getY());

    if (client.adding) {
      client.objects.add(new RS2Object(objectID, objectPosition.getX(), objectPosition.getY(), 1));
    }

    client.resetAction(false);
    client.setFocus(objectPosition.getX(), objectPosition.getY());
    client.getUpdateFlags().setRequired(UpdateFlag.APPEARANCE, true);
    if (xDiff > 5 || yDiff > 5) {
      return;
    }
    System.out.println("object: " + objectID);
    if (Utils.random(100) == 1) {
      client.triggerRandom();
      return;
    }
    
    switch (objectID) {
    case 6836:
    	Thieving2.attemptSteal(client, objectID, objectPosition);
    	break;
    
    }
    if (objectID == 6847) {
    	Thieving2.attemptSteal(client, objectID, objectPosition);
   // 	client.addItem(4084, 1);
    }
    if (objectID == 133) { // new dragon teleport?
      client.teleportToX = 3235;
      client.teleportToY = 9366;
      client.send(new SendMessage("Welcome to the dragon lair!"));
    }
    if (objectID == 2309 && objectPosition.getX() == 2998 && objectPosition.getY() == 3917) {
      if (client.getLevel(Skill.AGILITY) < 75) {
        client.send(new SendMessage("You need at least 75 agility to enter!"));
        return;
      }
      client.ReplaceObject(2998, 3917, 2309, 2, 0);
      return;
    }
    if (objectID == 1516 && objectPosition.getX() == 2908 && objectPosition.getY() == 9698) {
      if (!client.playerHasItem(989)) {
        client.send(new SendMessage("You need a crystal key to open this door."));
        return;
      }
      if (client.getLevel(Skill.SLAYER) < 90) {
        client.send(new SendMessage("You need at least 90 slayer to enter!"));
        return;
      }
      client.ReplaceObject(2908, 9698, -1, 0, 0);
      client.ReplaceObject(2907, 9698, -1, 0, 0);
      client.ReplaceObject(2908, 9697, 1516, 2, 0);
      client.ReplaceObject(2907, 9697, 1519, 0, 0);
      return;
    }
    if (objectID == 1519 && objectPosition.getX() == 2907 && objectPosition.getY() == 9698) {
      if (!client.playerHasItem(989)) {
        client.send(new SendMessage("You need a crystal key to open this door."));
        return;
      }
      if (client.getLevel(Skill.SLAYER) < 90) {
        client.send(new SendMessage("You need at least 90 slayer to enter!"));
        return;
      }
      client.ReplaceObject(2908, 9698, -1, 0, 0);
      client.ReplaceObject(2907, 9698, -1, 0, 0);
      client.ReplaceObject(2908, 9697, 1516, 2, 0);
      client.ReplaceObject(2907, 9697, 1519, 0, 0);
      return;
    }
    if (objectID == 2623) {
      if (client.playerHasItem(989)) {
        client.ReplaceObject(2924, 9803, 2623, -3, 0);
      } else {
        client.send(new SendMessage("You need the crystal key to enter"));
        client.send(new SendMessage("The crystal key is made from 2 crystal pieces"));
      }
    }
    if (objectID == 1759 && objectPosition.getX() == 2884 && objectPosition.getY() == 3397) {
      if (client.getLevel(Skill.SLAYER) >= 50) {
        client.teleportToX = 2884;
        client.teleportToY = 9798;
      } else {
        client.send(new SendMessage("You need 50 slayer to enter the Taverly Dungeon"));
      }
    }
    if (objectID == 410 && objectPosition.getX() == 2925 && objectPosition.getY() == 3483) {
      client.requestAnim(645, 0);
      client.triggerTele(2162, 4833, 0, false);
      return;
    }
    if (objectID == 1725) {
      client.stairs = "legendsUp".hashCode();
      client.skillX = objectPosition.getX();
      client.setSkillY(objectPosition.getY());
      client.stairDistance = 1;
    }
    if (objectID == 1725 && objectPosition.getX() == 2732 && objectPosition.getY() == 3377) {
      if (Utils.getDistance(client.getPosition().getX(), client.getPosition().getY(), objectPosition.getX(),
          objectPosition.getY()) > 2) {
        return;
      }
      if (client.premium) {
        client.teleportToX = 2732;
        client.teleportToY = 3380;
        client.getPosition().setZ(1);
      }
    }
    if (objectID == 1726 && objectPosition.getX() == 2732 && objectPosition.getY() == 3378) {
      if (Utils.getDistance(client.getPosition().getX(), client.getPosition().getY(), objectPosition.getX(),
          objectPosition.getY()) > 2) {
        return;
      }
      if (client.premium) {
        client.teleportToX = 2732;
        client.teleportToY = 3376;
        client.getPosition().setZ(0);
      }
    }
    if (objectID == 1726) {
      client.stairs = "legendsDown".hashCode();
      client.skillX = objectPosition.getX();
      client.setSkillY(objectPosition.getY());
      client.stairDistance = 1;
    }
    if (objectID == 2295) {
      client.GnomeLog();
      return;
    }
    if (objectID == 2285 && client.distanceToPoint(objectPosition.getX(), objectPosition.getY()) < 2) {
      client.GnomeNet1();
      return;
    }
    if (objectID == 2313) {
      client.GnomeTree1();
      return;
    }
    if (objectID == 2312) {
      client.GnomeRope();
      return;
    }
    if (objectID == 2314) {
      client.GnomeTreebranch2();
      return;
    }
    if (objectID == 2286 && client.distanceToPoint(objectPosition.getX(), objectPosition.getY()) < 3) {
      client.GnomeNet2();
      return;
    }
    if (objectID == 154 && client.distanceToPoint(objectPosition.getX(), objectPosition.getY()) < 2) {
      client.GnomePipe1();
      return;
    }
    if (objectID == 2288) {
      Agility.WildyPipe(client);
      return;
    }
    if (objectID == 2283) {
      Agility.WildyRope(client);
      return;
    }
    if (objectID == 2311) {
      Agility.WildyStones(client);
      return;
    }
    if (objectID == 2297) {
      Agility.WildyLog(client);
      return;
    }
    if (objectID == 2328) {
      Agility.WildyClimb(client);
      return;
    }
    if (objectID == 1558 || objectID == 1557 && client.distanceToPoint(2758, 3482) < 5 && client.playerRights > 0) {
      client.ReplaceObject(2758, 3482, 1558, -2, 0);
      client.ReplaceObject(2757, 3482, 1557, 0, 0);
      client.send(new SendMessage("Welcome to the Castle"));
    }
    if (objectID == 2104) {
      objectID = 2105;
    }
    if (objectID == 2102) {
      objectID = 2103;
    }
    if (objectID == 14859 && objectID == 14860) {
    	return;
    }
    for (int r = 0; r < Utils.rocks.length; r++) {
      if (objectID == Utils.rocks[r]) {
        if (client.getLevel(Skill.MINING) < Utils.rockLevels[r]) {
          client.send(new SendMessage("You need a mining level of " + Utils.rockLevels[r] + " to mine this rock"));
          return;
        }
        boolean hasPick = false;
        int pickaxe = -1;
        pickaxe = client.findPick();
        if (pickaxe < 0) {
          client.minePick = -1;
          client.resetAction();
          client.send(new SendMessage("You do not have an pickaxe that you can use."));
          return;
        } else {
          client.minePick = pickaxe;
          hasPick = true;
        }
        if (hasPick) {
          client.mineIndex = r;
          client.mining = true;
          client.lastAction = System.currentTimeMillis() + client.getMiningSpeed();
          client.lastPickAction = System.currentTimeMillis() + 1200;
          client.requestAnim(client.getMiningEmote(Utils.picks[pickaxe]), 0);
          client.send(new SendMessage("You swing your pick at the rock..."));
        } else {
          client.resetAction();
          client.send(new SendMessage("You need a pickaxe to mine this rock"));
        }
        return;
      }
    }
    if (client.mining) {
      return;
    }
    if (objectID == 2634 && objectPosition.getX() == 2838 && objectPosition.getY() == 3517) { //2838, 3517
    	client.teleportToX = 2840;
        client.teleportToY = 3517;
        client.send(new SendMessage("You jump to the other side of the rubble"));
//      if (client.getLevel(Skill.MINING) < 40) {
//        client.send(new SendMessage("You need 40 mining to clear this rubble"));
//        return;
//      }
//      client.requestAnim(client.getMiningEmote(624), 0);
//      client.animationReset = System.currentTimeMillis() + 2000;
//      client.ReplaceObject2(2838, 3517, -1, -1, 11);
//      client.send(new SendMessage("You clear the rubble"));
    }
    if (objectID == 1759) {
      int[] x = { 2845, 2848, 2848 };
      int[] y = { 3516, 3513, 3519 };
      for (int c = 0; c < x.length; c++) {
        if (objectPosition.getX() == x[c] && objectPosition.getY() == y[c]) {
          client.teleportToX = 2868;
          client.teleportToY = 9945;
        }
      }
    }
    if (objectID == 2107) {
      if (System.currentTimeMillis() - Server.lastRunite < 60000) {
        client.println("invalid timer");
        return;
      }
    }
    if (objectID == 2492) {
      client.teleportToX = 2591;
      client.teleportToY = 3087;
      client.getPosition().setZ(0);
      return;
    }
    if (objectID == 2486) {
      int count = 0;
      for (int c = 0; c < client.playerItems.length; c++) {
        if (client.playerItems[c] == 1437 && client.playerItemsN[c] > 0) {
          count++;
          client.deleteItem(1436, 1);
        }
      }
      client.send(new SendMessage("You craft " + count + " nature runes"));
      client.addItem(561, count);
      client.giveExperience((55 * count), Skill.RUNECRAFTING);
      return;
    }
    if (objectID == 2490) {
      if (client.getLevel(Skill.RUNECRAFTING) < 50) {
        client.send(new SendMessage("You must have 50 runecrafting to craft blood runes"));
        return;
      }
      int count = 0;
      for (int c = 0; c < client.playerItems.length; c++) {
        if (client.playerItems[c] == 1437 && client.playerItemsN[c] > 0) {
          count++;
          client.deleteItem(1436, 1);
        }
      }
      client.send(new SendMessage("You craft " + count + " blood runes"));
      client.addItem(565, count);
      client.giveExperience((75 * count), Skill.RUNECRAFTING);
      return;
    }
    if (objectID == 2484) {
      if (client.getLevel(Skill.RUNECRAFTING) < 75) {
        client.send(new SendMessage("You must have 75 runecrafting to craft cosmic runes"));
        return;
      }
      int count = 0;
      for (int c = 0; c < client.playerItems.length; c++) {
        if (client.playerItems[c] == 1437 && client.playerItemsN[c] > 0) {
          count++;
          client.deleteItem(1436, 1);
        }
      }
      client.send(new SendMessage("You craft " + count + " cosmic runes"));
      client.addItem(564, count);
      client.giveExperience((105 * count), Skill.RUNECRAFTING);
      return;
    }
    if (objectID == 2158 || objectID == 2156) {
      client.triggerTele(2921, 4844, 0, false);
      return;
    }
    if (objectID == 1733 && objectPosition.getX() == 2724 && objectPosition.getY() == 3374) {
      if (!client.premium) {
        client.resetPos();
      }
      client.teleportToX = 2727;
      client.teleportToY = 9774;
      client.getPosition().setZ(0);
      return;
    }
    if (objectID == 1734) {
      if (objectPosition.getX() == 2724 && objectPosition.getY() == 9774) {
        if (!client.premium) {
          client.resetPos();
        }
        client.teleportToX = 2723;
        client.teleportToY = 3375;
        client.getPosition().setZ(0);
      }
      if (objectPosition.getX() == 2603 && objectPosition.getY() == 9478) {
        client.teleportToX = 2606;
        client.teleportToY = 3079;
        client.getPosition().setZ(0);
      }
      return;
    }

//    if (objectID == 377 && client.playerHasItem(605)) {
//      {
//
//        double roll = Math.random() * 100;
//        if (roll < 100) {
//          int[] items = { 4708, 4710, 4712, 4714, 4716, 4718, 4720, 4722, 4724, 4726, 4728, 4730, 4732, 4734, 4736,
//              4738, 4745, 4747, 4749, 4751, 4753, 4755, 4757, 4759 };
//          int r = (int) (Math.random() * items.length);
//          client.send(new SendMessage("You have recieved a " + client.GetItemName(items[r]) + "!"));
//          client.addItem(items[r], 1);
//          client.deleteItem(605, 1);
//          client.yell("[Server] - " + client.getPlayerName() + " has just received from the BKT chest a  "
//              + client.GetItemName(items[r]));
//        } else {
//          int coins = Utils.random(7000);
//          client.send(new SendMessage("You find " + coins + " coins inside the BKT chest"));
//          client.addItem(995, coins);
//        }
//
//      }
//      for (int p = 0; p < Constants.maxPlayers; p++) {
//        Client player = (Client) PlayerHandler.players[p];
//        if (player == null) {
//          continue;
//        }
//        if (player.getPlayerName() != null && player.getPosition().getZ() == client.getPosition().getZ()
//            && !player.disconnected && Math.abs(player.getPosition().getY() - client.getPosition().getY()) < 30
//            && Math.abs(player.getPosition().getX() - client.getPosition().getX()) < 30 && player.dbId > 0) {
//          player.stillgfx(444, objectPosition.getY(), objectPosition.getX());
//        }
//      }
//    }

    if (objectID == 375 && ((objectPosition.getX() == 2593 && objectPosition.getY() == 3108)
        || (objectPosition.getX() == 2590 && objectPosition.getY() == 3106))) {
      if (System.currentTimeMillis() - client.lastAction < (2000 + Utils.random(200))) {
        client.send(new SendMessage("You can't try that often!"));
        client.lastAction = System.currentTimeMillis();
        return;
      }
      if (client.getLevel(Skill.THIEVING) < 70) {
        client.send(new SendMessage("You must be level 70 thieving to open this chest"));
        return;
      }
      if (Utils.random(100) == 1) {
        client.triggerRandom();
        return;
      }
      client.lastAction = System.currentTimeMillis();
      if (System.currentTimeMillis() - PlayerHandler.lastChest >= 23000) {
        PlayerHandler.lastChest = System.currentTimeMillis();
        double roll = Math.random() * 100;
        if (roll < 0.2) {
          int[] items = { 2577, 2579, 2631 };
          int r = (int) (Math.random() * items.length);
          client.send(new SendMessage("You have recieved a " + client.GetItemName(items[r]) + "!"));
          client.addItem(items[r], 1);
          client.yell("[Server] - " + client.getPlayerName() + " has just received from the chest a  "
              + client.GetItemName(items[r]));
        } else {
          int coins = 1200 + Utils.random(5000);
          client.send(new SendMessage("You find " + coins + " coins inside the chest"));
          client.addItem(995, coins);
        }
        if(client.getEquipment()[Equipment.Slot.HEAD.getId()] == 2631)
        	client.giveExperience(150, Skill.THIEVING);
        for (int p = 0; p < Constants.maxPlayers; p++) {
          Client player = (Client) PlayerHandler.players[p];
          if (player == null) {
            continue;
          }
          if (player.getPlayerName() != null && player.getPosition().getZ() == client.getPosition().getZ()
              && !player.disconnected && Math.abs(player.getPosition().getY() - client.getPosition().getY()) < 30
              && Math.abs(player.getPosition().getX() - client.getPosition().getX()) < 30 && player.dbId > 0) {
            player.stillgfx(444, objectPosition.getY(), objectPosition.getX());
          }
        }
      } else {
        client.send(new SendMessage("The chest is empty!"));
      }
    }
    if (objectID == 6420 && client.premium) {
      if (System.currentTimeMillis() - client.lastAction < (2500 + Utils.random(200))) {
        client.send(new SendMessage("You can't try that often!"));
        client.lastAction = System.currentTimeMillis();
        return;
      }
      if (client.getLevel(Skill.THIEVING) < 85) {
        client.send(new SendMessage("You must be level 85 thieving to open this chest"));
        return;
      }
      if (!client.premium) {
        client.resetPos();
      }
      if (Utils.random(100) == 1) {
        client.triggerRandom();
        return;
      }
      client.lastAction = System.currentTimeMillis();
      if (System.currentTimeMillis() - PlayerHandler.lastChest2 >= 20000) {
        PlayerHandler.lastChest2 = System.currentTimeMillis();
        double roll = Math.random() * 100;
        if (roll < 0.2) {
          int[] items = { 1050, 2581, 2631 };
          int r = (int) (Math.random() * items.length);
          client.send(new SendMessage("You have recieved a " + client.GetItemName(items[r]) + "!"));
          client.addItem(items[r], 1);
          client.yell("[Server] - " + client.getPlayerName() + " has just received from the premium chest a  "
              + client.GetItemName(items[r]));
        } else {
          int coins = 3000 + Utils.random(9000);
          client.send(new SendMessage("You find " + coins + " coins inside the chest"));
          client.addItem(995, coins);
        }
        if(client.getEquipment()[Equipment.Slot.HEAD.getId()] == 2631)
        	client.giveExperience(300, Skill.THIEVING);
        for (int p = 0; p < Constants.maxPlayers; p++) {
          Client player = (Client) PlayerHandler.players[p];
          if (player == null) {
            continue;
          }
          if (player.getPlayerName() != null && player.getPosition().getZ() == client.getPosition().getZ()
              && !player.disconnected && Math.abs(player.getPosition().getY() - client.getPosition().getY()) < 30
              && Math.abs(player.getPosition().getX() - client.getPosition().getX()) < 30 && player.dbId > 0) {
            player.stillgfx(444, objectPosition.getY(), objectPosition.getX());
          }
        }
      } else {
        client.send(new SendMessage("The chest is empty!"));
      }
    }
    
//    if (objectID == 3377 && client.premium && ((objectPosition.getX() == 3248 && objectPosition.getY() == 9364))) {
//        if (System.currentTimeMillis() - client.lastAction < (2500 + Utils.random(200))) {
//          client.send(new SendMessage("You can't try that often!"));
//          client.lastAction = System.currentTimeMillis();
//          return;
//        }
//        if (client.getLevel(Skill.THIEVING) < 85) {
//          client.send(new SendMessage("You must be level 85 thieving to open this chest"));
//          return;
//        }
//        if (!client.premium) {
//          client.resetPos();
//        }
//        if (Utils.random(100) == 1) {
//          client.triggerRandom();
//          return;
//        }
//        client.lastAction = System.currentTimeMillis();
//        if (System.currentTimeMillis() - PlayerHandler.lastChest2 >= 20000) {
//          PlayerHandler.lastChest2 = System.currentTimeMillis();
//          double roll = Math.random() * 100;
//          if (roll < 0.2) {
//            int[] items = { 1050, 2581, 2631 };
//            int r = (int) (Math.random() * items.length);
//            client.send(new SendMessage("You have recieved a " + client.GetItemName(items[r]) + "!"));
//            client.addItem(items[r], 1);
//            client.yell("[Server] - " + client.getPlayerName() + " has just received from the premium chest a  "
//                + client.GetItemName(items[r]));
//          } else {
//            int coins = 3000 + Utils.random(9000);
//            client.send(new SendMessage("You find " + coins + " coins inside the chest"));
//            client.addItem(995, coins);
//          }
//          if(client.getEquipment()[Equipment.Slot.HEAD.getId()] == 2631)
//          	client.giveExperience(300, Skill.THIEVING);
//          for (int p = 0; p < Constants.maxPlayers; p++) {
//            Client player = (Client) PlayerHandler.players[p];
//            if (player == null) {
//              continue;
//            }
//            if (player.getPlayerName() != null && player.getPosition().getZ() == client.getPosition().getZ()
//                && !player.disconnected && Math.abs(player.getPosition().getY() - client.getPosition().getY()) < 30
//                && Math.abs(player.getPosition().getX() - client.getPosition().getX()) < 30 && player.dbId > 0) {
//              player.stillgfx(444, objectPosition.getY(), objectPosition.getX());
//            }
//          }
//        } else {
//          client.send(new SendMessage("The chest is empty!"));
//        }
//      }

    if (System.currentTimeMillis() - client.lastDoor > 1000) {
      client.lastDoor = System.currentTimeMillis();
      for (int d = 0; d < DoorHandler.doorX.length; d++) {
        if (objectID == DoorHandler.doorId[d] && objectPosition.getX() == DoorHandler.doorX[d]
            && objectPosition.getY() == DoorHandler.doorY[d]) {
          int newFace = -3;
          if (DoorHandler.doorState[d] == 0) { // closed
            newFace = DoorHandler.doorFaceOpen[d];
            DoorHandler.doorState[d] = 1;
            DoorHandler.doorFace[d] = newFace;
          } else {
            newFace = DoorHandler.doorFaceClosed[d];
            DoorHandler.doorState[d] = 0;
            DoorHandler.doorFace[d] = newFace;
          }
          client.send(new Sound(326));
          for (int p = 0; p < Constants.maxPlayers; p++) {
            Client player = (Client) PlayerHandler.players[p];
            if (player == null) {
              continue;
            }
            if (player.getPlayerName() != null && player.getPosition().getZ() == client.getPosition().getZ()
                && !player.disconnected && player.getPosition().getY() > 0 && player.getPosition().getX() > 0
                && player.dbId > 0) {
              player.ReplaceObject(DoorHandler.doorX[d], DoorHandler.doorY[d], DoorHandler.doorId[d], newFace, 0);
            }
          }
        }
      }
    }
    if (objectID == 2290) {
      if (objectPosition.getX() == 2576 && objectPosition.getY() == 9506) {
        client.teleportToX = 2572;
        client.teleportToY = 9507;
      } else if (objectPosition.getX() == 2573 && objectPosition.getY() == 9506) {
        client.teleportToX = 2578;
        client.teleportToY = 9506;
      }
    }
    if (objectID == 2321) {
      if (client.getPosition().getY() <= 9488) {
        if (client.playerHasItem(1544)) {
          client.teleportToX = 2598;
          client.teleportToY = 9495;
        } else {
          client.send(new SendMessage("You need an orange key to cross"));
        }
      } else {
        client.teleportToX = 2598;
        client.teleportToY = 9488;
      }
    }
    if (objectID == 2318) {
      client.teleportToX = 2621;
      client.teleportToY = 9496;
    }
    if (objectID == 1728) {
      client.teleportToX = 2614;
      client.teleportToY = 9505;
    }
    //if (objectID == 6836) {
    //  client.skillX = objectPosition.getX();
    //  client.setSkillY(objectPosition.getY());
    //  client.WanneThieve = 6836;
   // }
    if (objectID == 881) {
      client.getPosition().setZ(client.getPosition().getZ() - 1);
    }
    if (objectID == 1591 && objectPosition.getX() == 3268 && objectPosition.getY() == 3435) {
      if (client.determineCombatLevel() >= 80) {
        client.teleportToX = 2540;
        client.teleportToY = 4716;
      } else {
        client.send(new SendMessage("You need to be level 80 or above to enter the mage arena."));
        client.send(new SendMessage("The skeletons at the varrock castle are a good place until then."));
      }
    }
    if (objectID == 5960 && objectPosition.getX() == 2539 && objectPosition.getY() == 4712) {
      client.teleportToX = 3105;
      client.teleportToY = 3933;
    }

    // Wo0t Tzhaar Objects

    if (objectID == 9369 && (objectPosition.getX() == 2399) && (objectPosition.getY() == 5176)) {
      if (client.getPosition().getY() == 5177) {
        client.teleportToX = 2399;
        client.teleportToY = 5175;

      }
    }
    if (objectID == 9369 && (objectPosition.getX() == 2399) && (objectPosition.getY() == 5176)) {
      if (client.getPosition().getY() == 5175) {
        client.teleportToX = 2399;
        client.teleportToY = 5177;

      }
    }

    if (objectID == 9368 && (objectPosition.getX() == 2399) && (objectPosition.getY() == 5168)) {
      if (client.getPosition().getY() == 5169) {
        client.teleportToX = 2399;
        client.teleportToY = 5167;

      }
    }
    if (objectID == 9368 && (objectPosition.getX() == 2399) && (objectPosition.getY() == 5168)) {
      if (client.getPosition().getY() == 5167) {
        client.teleportToX = 2399;
        client.teleportToY = 5169;

      }
    }
    if (objectID == 9391 && (objectPosition.getX() == 2399) && (objectPosition.getY() == 5172)) // Tzhaar
    // Fight
    // bank
    {
      client.openUpBank();
    }
    if (objectID == 9356 && (objectPosition.getX() == 2437) && (objectPosition.getY() == 5166)) // Tzhaar
    // Jad
    // Cave
    // Enterance
    {
      client.teleportToX = 2413;
      client.teleportToY = 5117;
      client.send(new SendMessage("You have entered the Jad Cave."));
    }
    if (objectID == 9357 && (objectPosition.getX() == 2412) && (objectPosition.getY() == 5118)) // Tzhaar
    // Jad
    // Cave
    // Exit
    {
      client.teleportToX = 2438;
      client.teleportToY = 5168;
      client.send(new SendMessage("You have left the Jad Cave."));
    }

    // End of Tzhaar Objects

	if ((objectID == 2213) || (objectID == 2214) || (objectID == 3045) || (objectID == 5276)
			|| (objectID == 6084)) {
		//System.out.println("Banking..");
		client.skillX = objectPosition.getX();
		client.setSkillY(objectPosition.getY());
		client.WanneBank = 1;
		client.WanneShop = -1;
	}
    // woodCutting
    // mining
    // if (actionTimer == 0) {
    if (client.CheckObjectSkill(objectID) == true) {
      client.skillX = objectPosition.getX();
      client.setSkillY(objectPosition.getY());
    }
    // }
    // go upstairs
    if (true) {
      if (objectID == 1747 || objectID == 1750) {
        client.stairs = 1;
        client.skillX = objectPosition.getX();
        client.setSkillY(objectPosition.getY());
        client.stairDistance = 1;
      } else if (objectID == 1738) {
        client.stairs = 1;
        client.skillX = objectPosition.getX();
        client.setSkillY(objectPosition.getY());
        client.stairDistance = 2;
      } else if (objectID == 1722) {
        client.stairs = 21;
        client.skillX = objectPosition.getX();
        client.setSkillY(objectPosition.getY());
        if (objectPosition.getX() == 2590 && objectPosition.getY() == 3089) {
          client.stairs = 69;
          client.stairDistance = 2590;
          client.stairDistanceAdd = 3092;
        } else if (objectPosition.getX() == 2769 && objectPosition.getY() == 3404) {
          client.stairs = 0;
        } else {
          client.stairDistance = 2;
          client.stairDistanceAdd = 2;
        }
      } else if (objectID == 1734) {
        client.stairs = 10;
        client.skillX = objectPosition.getX();
        client.setSkillY(objectPosition.getY());
        client.stairDistance = 3;
        client.stairDistanceAdd = 1;
      } else if (objectID == 55) {
        client.stairs = 15;
        client.skillX = objectPosition.getX();
        client.setSkillY(objectPosition.getY());
        client.stairDistance = 3;
        client.stairDistanceAdd = 1;
      } else if (objectID == 57) {
        client.stairs = 15;
        client.skillX = objectPosition.getX();
        client.setSkillY(objectPosition.getY());
        client.stairDistance = 3;
      } else if (objectID == 1755 || objectID == 5946 || objectID == 1757) {
        client.stairs = 4;
        client.skillX = objectPosition.getX();
        client.setSkillY(objectPosition.getY());
        client.stairDistance = 2;
      } else if (objectID == 1764) {
        client.stairs = 12;
        client.skillX = objectPosition.getX();
        client.setSkillY(objectPosition.getY());
        client.stairDistance = 1;
      } else if (objectID == 2148) {
        client.stairs = 8;
        client.skillX = objectPosition.getX();
        client.setSkillY(objectPosition.getY());
        client.stairDistance = 1;
      } else if (objectID == 3608) {
        client.stairs = 13;
        client.skillX = objectPosition.getX();
        client.setSkillY(objectPosition.getY());
        client.stairDistance = 1;
      } else if (objectID == 2408) {
        client.stairs = 16;
        client.skillX = objectPosition.getX();
        client.setSkillY(objectPosition.getY());
        client.stairDistance = 1;
      } else if (objectID == 5055) {
        client.stairs = 18;
        client.skillX = objectPosition.getX();
        client.setSkillY(objectPosition.getY());
        client.stairDistance = 1;
      } else if (objectID == 5131) {
        client.stairs = 20;
        client.skillX = objectPosition.getX();
        client.setSkillY(objectPosition.getY());
        client.stairDistance = 1;
      } else if (objectID == 9359) {
        client.stairs = 24;
        client.skillX = objectPosition.getX();
        client.setSkillY(objectPosition.getY());
        client.stairDistance = 1;
        client.stairDistance = 1;
      } else if (objectID == 2406) { /* Lost City Door */
        if (client.getEquipment()[Equipment.Slot.WEAPON.getId()] == 772) { // Dramen
                                                                            // Staff
          client.stairs = 27;
          client.skillX = objectPosition.getX();
          client.setSkillY(objectPosition.getY());
          client.stairDistance = 1;
        } else {// Open Door
        }
      }
      // go downstairs
      if (objectID == 1746 || objectID == 1749) {
        client.stairs = 2;
        client.skillX = objectPosition.getX();
        client.setSkillY(objectPosition.getY());
        client.stairDistance = 1;
      } else if (objectID == 1740) {
        client.stairs = 2;
        client.skillX = objectPosition.getX();
        client.setSkillY(objectPosition.getY());
        client.stairDistance = 1;
      } else if (objectID == 1723) {
        client.stairs = 22;
        client.skillX = objectPosition.getX();
        client.setSkillY(objectPosition.getY());
        client.stairDistance = 2;
        client.stairDistanceAdd = 2;
      } else if (objectID == 1733) {
        if (client.playerHasItem(1543)) {
          client.teleportToX = 2602;
          client.teleportToY = 9479;
          client.getPosition().setZ(0);
        } else {
          client.send(new SendMessage("You need a red key to go down these stairs"));
          return;
        }

      } else if (objectID == 1992 && objectPosition.getX() == 2558 && objectPosition.getY() == 3444) {
//        if (client.playerHasItem(537, 50)) {
//          client.deleteItem(537, client.getItemSlot(537), 50);
//          client.send(new SendMessage("The gods accept your offer!"));
          client.teleportToX = 2717;
          client.teleportToY = 9820;
          client.getPosition().setZ(0);
//        } else {
//          client.send(new SendMessage("The gods require 50 dragon bones as a sacrifice!"));
//          return;
//        }
      } else if (objectID == 2303) {
        if (client.playerHasItem(1545)) {
          client.teleportToX = 2580;
          client.teleportToY = 9520;
          client.getPosition().setZ(0);
        } else {
          client.send(new SendMessage("You need a yellow key to cross this ledge"));
          return;
        }
      } else if (objectID == 69) {
        if (client.playerHasItem(621)) {
          client.teleportToX = 3691;
          client.teleportToY = 3513;
          client.getPosition().setZ(0);
        } else {
          client.send(new SendMessage("You need a shipping ticket to board this ship."));
          return;
        }
      } else if (objectID == 54) {
        client.stairs = 14;
        client.skillX = objectPosition.getX();
        client.setSkillY(objectPosition.getY());
        client.stairDistance = 3;
        client.stairDistanceAdd = 1;
      } else if (objectID == 56) {
        client.stairs = 14;
        client.skillX = objectPosition.getX();
        client.setSkillY(objectPosition.getY());
        client.stairDistance = 3;
      } else if (objectID == 1568 || objectID == 5947 || objectID == 6434
          || /* objectID == 1759 || */objectID == 1754 || objectID == 1570) {
        if (objectPosition.getX() == 2594 && objectPosition.getY() == 3085)
          return;
        client.stairs = 3;
        client.skillX = objectPosition.getX();
        client.setSkillY(objectPosition.getY());
        client.stairDistance = 1;
      } else if (objectID == 2113) { // Mining guild stairs
        if (client.getLevel(Skill.MINING) >= 60) {
          client.stairs = 3;
          client.skillX = objectPosition.getX();
          client.setSkillY(objectPosition.getY());
          client.stairDistance = 1;
        } else {
          client.send(new SendMessage("You need 60 mining to enter the mining guild."));
        }
      } else if (objectID == 492) {
        client.stairs = 11;
        client.skillX = objectPosition.getX();
        client.setSkillY(objectPosition.getY());
        client.stairDistance = 2;
      } else if (objectID == 2147) {
        client.stairs = 7;
        client.skillX = objectPosition.getX();
        client.setSkillY(objectPosition.getY());
        client.stairDistance = 1;
      } else if (objectID == 5054) {
        client.stairs = 17;
        client.skillX = objectPosition.getX();
        client.setSkillY(objectPosition.getY());
        client.stairDistance = 1;
      } else if (objectID == 5130) {
        client.stairs = 19;
        client.skillX = objectPosition.getX();
        client.setSkillY(objectPosition.getY());
        client.stairDistance = 1;
      } else if (objectID == 9358) {
        client.stairs = 23;
        client.skillX = objectPosition.getX();
        client.setSkillY(objectPosition.getY());
        client.stairDistance = 1;
      } else if (objectID == 5488) {
        client.stairs = 28;
        client.setSkillX(objectPosition.getX());
        client.setSkillY(objectPosition.getY());
        client.stairDistance = 1;
      } else if (objectID == 9294) {
        if (objectPosition.getX() == 2879 && objectPosition.getY() == 9813) {
          client.stairs = "trap".hashCode();
          client.stairDistance = 1;
          client.setSkillX(objectPosition.getX());
          client.setSkillY(objectPosition.getY());
        }
      }
    }
  }

}