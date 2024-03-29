package com.dodian.uber.game.model.player.packets.incoming;

import com.dodian.uber.game.Server;
import com.dodian.uber.game.model.entity.player.Client;
import com.dodian.uber.game.model.entity.player.Player;
import com.dodian.uber.game.model.entity.player.PlayerHandler;
import com.dodian.uber.game.model.player.packets.Packet;
import com.dodian.uber.game.model.player.packets.outgoing.SendMessage;
import com.dodian.uber.game.model.player.skills.Skill;
import com.dodian.utilities.Utils;

public class MagicOnPlayer implements Packet {

  @Override
  public void ProcessPacket(Client client, int packetType, int packetSize) {
    int playerIndexx = client.getInputStream().readSignedWordA();
    if (!(playerIndexx >= 0 && playerIndexx < PlayerHandler.players.length)
        || PlayerHandler.players[playerIndexx] == null) {
      return;
    }
    if (!client.canAttack) {
      client.send(new SendMessage("You cannot attack your oppenent yet!"));
      return;
    }
    int EnemyX3 = PlayerHandler.players[playerIndexx].getPosition().getX();
    int EnemyY3 = PlayerHandler.players[playerIndexx].getPosition().getY();
    Player pl2 = PlayerHandler.players[playerIndexx];
    Client castOnPlayer = (Client) PlayerHandler.players[playerIndexx];
    client.resetWalkingQueue();
    int spellID = client.getInputStream().readSignedWordBigEndian();
    if (pl2 == null) {
      return;
    }
    int diff = Math.abs(castOnPlayer.determineCombatLevel() - client.determineCombatLevel());
    if (!((castOnPlayer.inWildy() && diff <= client.wildyLevel && diff <= castOnPlayer.wildyLevel)
        || client.duelFight && client.duel_with == castOnPlayer.getSlot()) || castOnPlayer.saving) {
      client.send(new SendMessage("You can't attack that player"));
      return;
    }
    if (!(client.duelFight && client.duel_with == playerIndexx) && !Server.pking) {
      client.send(new SendMessage("Pking has been disabled"));
      return;
    }
    if (castOnPlayer.immune) {
      client.send(new SendMessage("That player is immune"));
      return;
    }
    client.setHitDiff(0);
    if (client.duelFight && client.duelRule[2]) {
      client.send(new SendMessage("Magic has been disabled for this duel!"));
      return;
    }
    System.currentTimeMillis();

    int wildLevel = client.getWildLevel();
    if ((playerIndexx == client.duel_with && client.duelFight) || wildLevel > 0) {
      for (int i2 = 0; i2 < client.ancientId.length; i2++) {
        if (spellID == client.ancientId[i2]) {
          if (System.currentTimeMillis() - client.lastAttack < client.coolDown[client.coolDownGroup[i2]]) {
            // send(new SendMessage("You must wait before casting this
            // kind of spell again");
            break;
          }
          if (!client.runeCheck(spellID)) {
            client.send(new SendMessage("You are missing some of the runes required by this spell"));
            break;
          }
          client.deleteItem(565, 1);
          client.setInCombat(true);
          client.lastPlayerCombat = System.currentTimeMillis();
          if (client.getLevel(Skill.MAGIC) >= client.requiredLevel[i2]) {
            client.setFocus(EnemyX3, EnemyY3);
            Utils.random(100);
            double hit = Utils.random(client.baseDamage[i2] + client.playerBonus[11]) * 0.85;
            client.requestAnim(1979, 0);
            client.AnimationReset = true;
            client.lastAttack = System.currentTimeMillis();
            int EnemyX = castOnPlayer.getPosition().getX();
            int EnemyY = castOnPlayer.getPosition().getY();
            if (spellID == 1572) {
              if (!client.playerHasItem(555, 3) || !client.playerHasItem(557, 3) || !client.playerHasItem(561, 2)) {
                client.send(new SendMessage("You are missing runes required for this spell"));
                break;
              }
              castOnPlayer.setSnared(5000);
              client.deleteItem(555, 3);
              client.deleteItem(557, 3);
              client.deleteItem(561, 2);
            } else if (spellID == 1582) {
              if (!client.playerHasItem(555, 4) || !client.playerHasItem(557, 4) || !client.playerHasItem(561, 3)) {
                client.send(new SendMessage("You are missing runes required for this spell"));
                break;
              }
              castOnPlayer.setSnared(10000);
              client.deleteItem(555, 4);
              client.deleteItem(557, 4);
              client.deleteItem(561, 3);
            }
            if (client.ancientType[i2] == 4) {
              client.stillgfx(617, EnemyY, EnemyX);
            } else if (client.ancientType[i2] == 3) {
              // coolDown[coolDownGroup[i2]] = 35;
              castOnPlayer.effects[0] = 15;
              client.stillgfx(369, EnemyY, EnemyX);
            } else if (client.ancientType[i2] == 2) {
              // coolDown[coolDownGroup[i2]] = 12;
              client.stillgfx(377, EnemyY, EnemyX);
              client.setCurrentHealth(client.getCurrentHealth()+ (int) (hit / 10));
              if (client.getCurrentHealth() > client.getLevel(Skill.HITPOINTS)) {
                client.setCurrentHealth(client.getLevel(Skill.HITPOINTS));
              }
            } else {
              client.animation(78, EnemyY, EnemyX);
            }
            castOnPlayer.send(new SendMessage(client.getPlayerName() + " is shooting you!"));
            castOnPlayer.receieveDamage(client.getSlot(), (int) hit, false);
            client.resetWalkingQueue();
            break;
          } else {
            client.send(new SendMessage("You need a magic level of " + client.requiredLevel[i2]));
          }
        }
      }
    } else {
      client.send(new SendMessage("You can't attack here!"));
    }
  }

}