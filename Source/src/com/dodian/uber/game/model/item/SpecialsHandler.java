package com.dodian.uber.game.model.item;

import com.dodian.uber.game.model.entity.player.Client;
import com.dodian.utilities.Utils;

public class SpecialsHandler {

  public static void specAction(Client player, int weapon) {
    if (weapon == 4151) {
      player.bonusSpec = 4 + Utils.random(2);
      if (player.getCurrentHealth() > player.bonusSpec) {
        // player.currentHealth = player.currentHealth - player.bonusSpec;
        player.bonusSpec = 2 * player.bonusSpec;
      } else {
        player.bonusSpec = 1;
      }
      player.emoteSpec = player.getStandAnim();
      player.animationSpec = 341;
    } else if (weapon == 7158) {
      player.bonusSpec = 5 + Utils.random(5);
      if (player.getCurrentHealth() + player.bonusSpec < 99) {
        player.setCurrentHealth(player.getCurrentHealth() + player.bonusSpec);
      } else {
        player.setCurrentHealth(player.maxHealth);
      }
      player.emoteSpec = 2890;
      player.animationSpec = 377;
    }
  }
}
