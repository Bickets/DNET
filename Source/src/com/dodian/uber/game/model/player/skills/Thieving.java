package com.dodian.uber.game.model.player.skills;

import com.dodian.uber.game.event.Event;
import com.dodian.uber.game.event.EventManager;
import com.dodian.uber.game.model.entity.player.Client;
import com.dodian.uber.game.model.entity.player.PlayerHandler;
import com.dodian.uber.game.model.player.packets.outgoing.SendMessage;
import com.dodian.uber.game.model.player.packets.outgoing.Sound;
import com.dodian.utilities.Utils;

public class Thieving {

  @SuppressWarnings("unused")
  private Client player;

  private void thievingReward(Client player, int emote, int exp, int item, int amount, String text) {
    player.requestAnim(emote, 0);
    EventManager.getInstance().registerEvent(new Event(600) {

      @Override
      public void execute() {
        if (player == null || player.disconnected) {
          this.stop();
          return;
        }

        player.giveExperience(exp, Skill.THIEVING);
        player.addItem(item, amount);
        player.send(new SendMessage(text));
        player.send(new Sound(356));
        this.stop();
      }

    });
  }

  public void startThieving(Client player, int thieve) {

    this.player = player;
    long now = System.currentTimeMillis();
    if (Utils.random(100) == 1) {
      player.triggerRandom();
      return;
    }
    if (!player.hasSpace()) {
      player.send(new SendMessage("Your inventory is full!"));
      return;
    }
    switch (thieve) {
    case 7:
      if (now - player.lastAction < 1500 + Utils.random(500)) {
        return;
      }
      if (player.getLevel(Skill.THIEVING) < 10) {
        player.send(new SendMessage("You need 10 theiving to pickpocket farmers."));
        return;
      }
      thievingReward(player, 881, 800, 314, Utils.random(5) + 2, "You pickpocket the farmer.");
      player.lastAction = now;
      break;
    case 2235:
      if (now - player.lastAction < 2500 + Utils.random(500)) {
        return;
      }
      if (player.getLevel(Skill.THIEVING) < 70) {
        player.send(new SendMessage("You need 70 theiving to pickpocket the master farmer."));
        return;
      }
      thievingReward(player, 881, 1200, 314, Utils.random(10) + 4, "You pickpocket the master farmer.");
      player.lastAction = now;
      break;
    case 6836:
      if (now - player.lastAction < 1500) {
        return;
      }
      thievingReward(player, 881, 150, 995, Utils.random(10), "You steal some cash from the cage.");
      player.lastAction = now;
      break;
    case 2560:
      if (now - player.lastAction < 4000) {
        return;
      }
      if (System.currentTimeMillis() - PlayerHandler.lasthide >= 10000 + Utils.random(5000)) {
        PlayerHandler.lasthide = System.currentTimeMillis();
        return;
      }
      if (player.getLevel(Skill.THIEVING) < 40) {
        player.send(new SendMessage("You need a theiving level of 40 to steal from this stall."));
        return;
      }
      double[] chances2 = { 100, 30, 20, 10, .05 };
      int[] hide = { 1739, 1751, 1753, 1749, 1747 };
      double roll = Math.random() * 100;
      int item = -1;
      for (int i = 0; i < hide.length; i++) {
        if (roll < chances2[i])
          item = hide[i];
      }
      thievingReward(player, 881, 2000, item, Utils.random(3), "You steal a " + player.GetItemName(item).toLowerCase());
      player.lastAction = now;
    case 2565:
      if (now - player.lastAction < 4000) {
        return;
      }
      if (System.currentTimeMillis() - PlayerHandler.lastbar >= 10000 + Utils.random(5000)) {
        PlayerHandler.lastbar = System.currentTimeMillis();
        return;
      }
      if (!player.premium) {
        player.send(new SendMessage("You must be premium to steal from here!"));
        return;
      }
      if (player.getLevel(Skill.THIEVING) < 65) {
        player.send(new SendMessage("You need a theiving level of 65 to steal from the bars stall."));
        return;
      }
      double[] chances = { 100, 55, 35, 20, 10 };
      int[] bars = { 2349, 2351, 2353, 2359, 2361 };
      double roll8 = Math.random() * 100;
      item = -1;
      for (int i = 0; i < bars.length; i++) {
        if (roll8 < chances[i])
          item = bars[i];
      }
      thievingReward(player, 881, 2000, item, Utils.random(3), "You steal a " + player.GetItemName(item).toLowerCase());
      player.lastAction = now;
      break;
    case 2561:
      if (now - player.lastAction < 6000) {
        return;
      }
      if (player.getLevel(Skill.THIEVING) < 10) {
        player.send(new SendMessage("You need a theiving level of 10 to steal from the baker's stall."));
        return;
      }
      thievingReward(player, 881, 800, 2309, 1, "You steal a " + player.GetItemName(2309).toLowerCase());
      player.lastAction = now;
      break;
    case 1318:
        if (now - player.lastAction < 250000) {
          return;
        }
        thievingReward(player, 881, 0, 4084, 1, "You steal a " + player.GetItemName(4084).toLowerCase());
        player.lastAction = now;
        break;
    case 2564:
      if (now - player.lastAction < 8000) {
        return;
      }
      if (System.currentTimeMillis() - PlayerHandler.lastherb >= 10000 + Utils.random(5000)) {
        PlayerHandler.lastherb = System.currentTimeMillis();
        return;
      }
      if (!player.premium) {
        player.send(new SendMessage("You must be premium to steal from here!"));
        return;
      }
      if (player.getLevel(Skill.THIEVING) < 80) {
        player.send(new SendMessage("You need a theiving level of 80 to steal from the spice stall."));
        return;
      }
      double[] chances1 = { 100, 55, 35, 20, 10, 5 };
      int[] herbs = { 249, 253, 257, 259, 263, 265 };
      double roll1 = Math.random() * 100;
      item = -1;
      for (int i = 0; i < herbs.length; i++) {
        if (roll1 < chances1[i])
          item = herbs[i];
      }
      thievingReward(player, 881, 5000, item, 1, "You steal a " + player.GetItemName(item).toLowerCase());
      player.lastAction = now;
      break;
    case 2563:
      if (now - player.lastAction < 3000) {
        return;
      }
      item = 314;
      thievingReward(player, 881, 600, item, Utils.random(20), "You steal a " + player.GetItemName(item).toLowerCase());
      player.lastAction = now;
      break;
    case 2562:
      if (now - player.lastAction < 25000) {
        return;
      }
      if (System.currentTimeMillis() - PlayerHandler.LastGem >= 10000 + Utils.random(5000)) {
        PlayerHandler.LastGem = System.currentTimeMillis();
        return;
      }
      if (player.getLevel(Skill.THIEVING) < 92) {
        player.send(new SendMessage("You need a theiving level of 92 to steal from the gem stall."));
        return;
      }
      double[] chances3 = { 100, 60, 30, 5, 0.03 };
      int[] gems = { 1623, 1621, 1619, 1617/*, 1631*/ };
      double roll2 = Math.random() * 100;
      item = -1;
      for (int i2 = 0; i2 < gems.length; i2++) {
        if (roll2 < chances3[i2])
          item = gems[i2];
      }
      thievingReward(player, 881, 10000, item, 1, "You steal a " + player.GetItemName(item).toLowerCase());
      player.lastAction = now;
      break;
    }
  }
}