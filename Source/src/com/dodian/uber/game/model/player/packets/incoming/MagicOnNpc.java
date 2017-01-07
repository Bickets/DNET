package com.dodian.uber.game.model.player.packets.incoming;

import com.dodian.uber.game.Server;
import com.dodian.uber.game.model.UpdateFlag;
import com.dodian.uber.game.model.entity.npc.Npc;
import com.dodian.uber.game.model.entity.player.Client;
import com.dodian.uber.game.model.player.packets.Packet;
import com.dodian.uber.game.model.player.packets.outgoing.SendMessage;
import com.dodian.uber.game.model.player.skills.Skill;
import com.dodian.uber.game.model.player.skills.slayer.SlayerTask;
import com.dodian.utilities.Utils;

public class MagicOnNpc implements Packet {

  @Override
  public void ProcessPacket(Client client, int packetType, int packetSize) {
    int npcIndex = client.getInputStream().readSignedWordBigEndianA();
    Npc tempNpc = Server.npcManager.getNpc(npcIndex);
    if (tempNpc == null)
      return;
    int magicID = client.getInputStream().readSignedWordA();
    int EnemyX2 = tempNpc.getPosition().getX();
    int EnemyY2 = tempNpc.getPosition().getY();
    int EnemyHP2 = tempNpc.getCurrentHealth();
    int hitDiff = 0;
    client.resetWalkingQueue();
    {
      try {
        if (EnemyHP2 < 1 || client.deathTimer > 0) {
          client.send(new SendMessage("That monster has already been killed!"));
          return;
        }
        int type = tempNpc.getId();

        SlayerTask slayerTask = SlayerTask.getSlayerNpc(type);
        if (slayerTask != null && slayerTask.isSlayerOnly()
            && (slayerTask.getTaskId() != client.taskId || client.taskAmt >= client.taskTotal)) {
          client.send(new SendMessage("You need a Slayer task to kill this monster."));
          return;
        }
        if (type == SlayerTask.HEAD_MOURNER.getNpcId()
            && (client.taskId != SlayerTask.HEAD_MOURNER.getTaskId() || client.taskAmt >= client.taskTotal)
            && client.getLevel(Skill.SLAYER) < 70) {
          client.send(new SendMessage("You need a Slayer level of at least 70 to kill this without a task."));
          return;
        }

        if (type == 1125) {
          if (client.determineCombatLevel() < 70) {
            client.send(new SendMessage("You must be level 70 or higher to attack Dad"));
            return;
          }
        }
        if (type == 3066) {
          if (!client.playerHasItem(1545)) {
            client.resetPos();
            return;
          }
        }
        if (type == 221 || type == 1961) {
          if (!client.playerHasItem(1544)) {
            client.resetPos();
            return;
          }
        }

        int[] prem = { 1643, 158, 49, 1613 };
        for (int p = 0; p < prem.length; p++) {
          if (prem[p] == type && !client.premium) {
            client.resetPos();
            break;
          }
        }
        for (int i2 = 0; i2 < client.ancientId.length; i2++) {
          if (magicID == client.ancientId[i2]) {
            if (!client.runeCheck(magicID)) {
              client.send(new SendMessage("You are missing some of the runes required by this spell"));
              break;
            }
            client.deleteItem(565, 1);
            if (System.currentTimeMillis() - client.lastAttack < client.coolDown[client.coolDownGroup[i2]]) {
              break;
            }
            client.setInCombat(true);
            client.setLastCombat(System.currentTimeMillis());
            client.lastAttack = client.getLastCombat();
            if (client.getLevel(Skill.MAGIC) >= client.requiredLevel[i2]) {
              int dmg = client.baseDamage[i2] + (int) Math.ceil(client.playerBonus[11] * 0.5);
              double hit = Utils.random(dmg);
              if (hit >= EnemyHP2)
                hit = EnemyHP2;
              hitDiff = (int) hit;
              tempNpc.dealDamage(client, (int) hit, false);
              if (hit > 0 && tempNpc.getId() == 3200)
                tempNpc.addMagicHit(client);
              client.requestAnim(1979, 0);
              client.teleportToX = client.getPosition().getX();
              client.teleportToY = client.getPosition().getY();
              if (client.ancientType[i2] == 3) {
                // coolDown[coolDownGroup[i2]] = 35;
                // server.npcHandler.npcs[npcIndex].effects[0]
                // = 15;
                client.stillgfx(369, EnemyY2, EnemyX2);
              } else if (client.ancientType[i2] == 2) {
                client.stillgfx(377, EnemyY2, EnemyX2);
                // coolDown[coolDownGroup[i2]] = 12;
                client.setCurrentHealth(client.getCurrentHealth() + (int) (hit / 5));
                if (client.getCurrentHealth() > client.getLevel(Skill.HITPOINTS)) {
                  client.setCurrentHealth(client.getLevel(Skill.HITPOINTS));
                }
              } else {
                client.animation(78, EnemyY2, EnemyX2);
              }
            } else {
              client.send(new SendMessage("You need a magic level of " + client.requiredLevel[i2]));
            }
          }
        }
        client.setFocus(EnemyX2, EnemyY2);
        client.giveExperience(40 * hitDiff, Skill.MAGIC);
        client.giveExperience(hitDiff * 15, Skill.HITPOINTS);
        client.getUpdateFlags().setRequired(UpdateFlag.APPEARANCE, true);
      } catch (Exception e) {
        e.printStackTrace();
      }

    }
  }

}