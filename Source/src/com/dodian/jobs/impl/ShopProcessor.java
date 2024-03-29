package com.dodian.jobs.impl;

import com.dodian.uber.game.model.ShopHandler;
import com.dodian.uber.game.model.entity.player.PlayerHandler;
import com.dodian.uber.game.Constants;
import com.dodian.uber.game.Server;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

@DisallowConcurrentExecution

public class ShopProcessor implements Job {
  public void execute(JobExecutionContext context) throws JobExecutionException {
    boolean DidUpdate = false;
    for (int i = 1; i <= ShopHandler.MaxShops; i++) {
      for (int j = 0; j < ShopHandler.MaxShopItems; j++) {
        if (ShopHandler.ShopItems[i][j] > 0) {
          if (ShopHandler.ShopItemsDelay[i][j] >= ShopHandler.MaxShowDelay) {
            if (j <= ShopHandler.ShopItemsStandard[i]
                && ShopHandler.ShopItemsN[i][j] <= ShopHandler.ShopItemsSN[i][j]) {
              if (ShopHandler.ShopItemsN[i][j] < ShopHandler.ShopItemsSN[i][j]) {
                ShopHandler.ShopItemsN[i][j] += 1; // if amount lower then
                                                   // standard, increase it !
              }
            } else {
              Server.shopHandler.DiscountItem(i, j);
            }
            ShopHandler.ShopItemsDelay[i][j] = 0;
            DidUpdate = true;
          }
          ShopHandler.ShopItemsDelay[i][j]++;
        }
      }
      if (DidUpdate == true) {
        for (int k = 1; k < Constants.maxPlayers; k++) {
          if (PlayerHandler.players[k] != null) {
            if (PlayerHandler.players[k].IsShopping == true && PlayerHandler.players[k].MyShopID == i) {
              PlayerHandler.players[k].UpdateShop = true;
            }
          }
        }
        DidUpdate = false;
      }
    }

  }

}