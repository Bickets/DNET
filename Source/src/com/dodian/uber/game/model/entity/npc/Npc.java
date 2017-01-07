package com.dodian.uber.game.model.entity.npc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.dodian.uber.game.Server;
import com.dodian.uber.game.model.Position;
import com.dodian.uber.game.model.UpdateFlag;
import com.dodian.uber.game.model.YellSystem;
import com.dodian.uber.game.model.entity.Entity;
import com.dodian.uber.game.model.entity.player.Client;
import com.dodian.uber.game.model.entity.player.Player;
import com.dodian.uber.game.model.entity.player.PlayerHandler;
import com.dodian.uber.game.model.item.Ground;
import com.dodian.uber.game.model.item.GroundItem;
import com.dodian.uber.game.model.player.packets.outgoing.SendMessage;
import com.dodian.uber.game.model.player.skills.Skill;
import com.dodian.uber.game.model.player.skills.slayer.SlayerTask;
import com.dodian.uber.game.security.DropLog;
import com.dodian.utilities.Utils;

/**
 * @author Owner
 * 
 */
public class Npc extends Entity {
  private int id, currentHealth, maxHealth, taskId = -1, respawn, combat, maxHit;
  public boolean alive = true, visible = true;
  private long deathTime = 0, lastAttack = 0;
  private int moveX = 0, moveY = 0, direction = -1, viewX, viewY;
  private int damageDealt = 0;
  private String text = "";
  private int deathEmote;
  private NpcData data;
  private boolean fighting = false;
  private Map<Integer, Client> enemies = new HashMap<Integer, Client>();
  private final int TIME_ON_FLOOR = 2400;
  private int[] level = new int[7];
  private boolean walking = false;
  private long lastChatMessage;

  public Npc(int slot, int id, Position position) {
    super(position.copy(), slot, Entity.Type.NPC);
    this.id = id;
    data = Server.npcManager.getData(id);
    if (data != null) {
      deathEmote = data.getDeathEmote();
      respawn = data.getRespawn();
      combat = data.getCombat();
      for (int i = 0; i < data.getLevel().length; i++) {
        level[i] = data.getLevel()[i];
      }
      CalculateMaxHit();

      taskId = data.getTaskId();
      this.currentHealth = data.getHP();
      this.maxHealth = data.getHP();
    }
    alive = true;
  }

  private ArrayList<Client> magicList = new ArrayList<Client>();

  public void CalculateMaxHit() {
    double MaxHit = 0;
    int Strength = getStrength(); // Strength
    MaxHit += (double) (Strength * 0.12);
    maxHit = (int) Math.floor(MaxHit);
  }

  public Client getClient(int index) {
    return ((Client) PlayerHandler.players[index]);
  }

  public boolean validClient(int index) {
    Client p = (Client) PlayerHandler.players[index];
    if (p != null && !p.disconnected && p.dbId > 0) {
      return true;
    }
    return false;
  }

  public void bossAttack() {
    Client enemy = getTarget();
    if (enemy == null) {
      fighting = false;
      return;
    }
    if (enemy.deathStage > 0) {
      fighting = false;
      return;
    }
    requestAnim(data.getAttackEmote(), 0);
    setFocus(enemy.getPosition().getX(), enemy.getPosition().getY());
    getUpdateFlags().setRequired(UpdateFlag.FACE_COORDINATE, true);
    for (int i = 0; i < PlayerHandler.getPlayerCount() + 1; i++) {
      Client c = getClient(i);
      if (!validClient(i))
        continue;
      if ((c.selectedNpc != null && c.selectedNpc.getSlot() == getSlot() && c.attackingNpc)
          || (!magicList.isEmpty() && magicList.contains(c))) {
        int damage = Utils.random(maxHit);
        c.dealDamage(damage, false);
      }
    }
    magicList.clear();
    lastAttack = System.currentTimeMillis();
  }

  public void attack_new() {
    requestAnim(data.getAttackEmote(), 0);
    Client enemy = getTarget();
    if (enemy == null) {
      fighting = false;
      return;
    }
    if (enemy.deathStage > 0) {
      fighting = false;
      return;
    }
    setFocus(enemy.getPosition().getX(), enemy.getPosition().getY());
    getUpdateFlags().setRequired(UpdateFlag.FACE_COORDINATE, true);
    int def = enemy.playerBonus[6];
    double blocked = Utils.dRandom(def / 17);
    int hitDiff = Utils.random2(maxHit);
    int hitChance = (int) ((combat * 1.5) - enemy.getLevel(Skill.DEFENCE) * 1.3); // Chance
    // to
    // hit
    // ;)
    if (hitChance < 15)
      hitChance = 15;
    if (hitChance > 70)
      hitChance = 70;
    double roll = Math.random() * 100;
    if (roll <= hitChance) {
    } else {
      hitDiff = 0;
    }
    hitDiff -= (int) blocked;
    if (hitDiff < 0)
      hitDiff = 0;
    enemy.send(new SendMessage("npc's % of hit = " + hitChance + ", hit = " + hitDiff));
    enemy.dealDamage(hitDiff, false);
    lastAttack = System.currentTimeMillis();
  }

  public boolean isAttackable() {
    if (maxHealth > 0)
      return true;
    return false;
  }

  public int getId() {
    return id;
  }

  public void clearUpdateFlags() {
    getUpdateFlags().clear();
    crit = false;
    direction = -1;
  }

  public void setText(String text) {
    this.text = text;
    getUpdateFlags().setRequired(UpdateFlag.FORCED_CHAT, true);
  }

  public static int getCurrentHP(int i, int i1, int i2) {
    double x = (double) i / (double) i1;
    return (int) Math.round(x * i2);
  }
  
  public int getViewX() {
    return this.viewX;
  }
  
  public int getViewY() {
    return this.viewY;
  }
  
  public int getDirection() {
    return this.direction;
  }
  
  public void setDirection(int direction) {
    this.direction = direction;
  }
  
  public boolean isWalking() {
    return this.walking;
  }

  private boolean crit;

  public void dealDamage(Client client, int hitDiff, boolean crit) {
    if (maxHealth < 1 || !validClient(client) || !alive)
      return;
    int id = client.dbId;
    if (enemies.containsKey(id)) {
      getDamage().put(id, getDamage().get(id) + hitDiff);
    } else {
      enemies.put(id, client);
      getDamage().put(id, hitDiff);
    }
    if (hitDiff >= currentHealth)
      hitDiff = currentHealth;
    fighting = true;
    damageDealt = hitDiff;
    currentHealth -= hitDiff;
    if (currentHealth < 0)
      currentHealth = 0;
    if (currentHealth <= 0 && maxHealth > 0) {
      alive = false;
      die();
    }
    this.crit = crit;
    getUpdateFlags().setRequired(UpdateFlag.HIT, true);
  }

  public void attack() {
    requestAnim(data.getAttackEmote(), 0);
    Client enemy = getTarget();
    if (enemy == null) {
      fighting = false;
      return;
    }
    setFocus(enemy.getPosition().getX(), enemy.getPosition().getY());
    getUpdateFlags().setRequired(UpdateFlag.FACE_COORDINATE, true);
    int def_bonus = enemy.playerBonus[6];
    int def = enemy.getLevel(Skill.DEFENCE);
    int rand = Utils.random(def + (def_bonus / 5));
    int rand_npc = Utils.random(getAttack());
    double blocked = (0.08 * (double) def_bonus) / 100;
    int hitDiff = 0;
    if (rand_npc > rand) {
      int new_max_hit = (int) Math.ceil(maxHit * (1 - blocked));
      hitDiff = Utils.random(new_max_hit);
    }
    if (hitDiff < 0)
      hitDiff = 0;
    enemy.dealDamage(hitDiff, false);
    lastAttack = System.currentTimeMillis();
  }

  public void addBossCount(Player p, int ID) {
    for (int i = 0; i < p.boss_name.length; i++) {
      if (npcName().toLowerCase().equals(p.boss_name[i].replace("_", " ").toLowerCase())) {
        if (p.boss_amount[i] >= 100000)
          return;
        p.boss_amount[i] += 1;
      }
    }
  }

  public void die() {
    alive = false;
    fighting = false;
    deathTime = System.currentTimeMillis();
    requestAnim(deathEmote, 0);
    Client p = getTarget();
    if (p == null)
      return;
    if (id == 1125) {
      addBossCount(p, id);
      YellSystem.bossYell("Dad has been slain by " + p.getPlayerName() + " (level-" + p.determineCombatLevel() + ")");
    } else if (id == 221) {
      addBossCount(p, id);
      YellSystem
          .bossYell("The Black Knight Titan has been slain  by " + p.getPlayerName() + " (level-" + p.determineCombatLevel() + ")");
    } else if (id == 936) {
      addBossCount(p, id);
      YellSystem.bossYell("San Tojalon has been slain by " + p.getPlayerName() + " (level-" + p.determineCombatLevel() + ")");
    } else if (id == 1613) {
      addBossCount(p, id);
      YellSystem.bossYell("Nechryael has been slain by " + p.getPlayerName() + " (level-" + p.determineCombatLevel() + ")");
    } else if (id == 795) {
      addBossCount(p, id);
      YellSystem.bossYell("The Ice Queen has been slain by " + p.getPlayerName() + " (level-" + p.determineCombatLevel() + ")");
    } else if (id == 929) {
      addBossCount(p, id);
      YellSystem.bossYell("Ungadulu has been slain by " + p.getPlayerName() + " (level-" + p.determineCombatLevel() + ")");
    } else if (id == 3066) {
      addBossCount(p, id);
      YellSystem
          .bossYell("The Zombie Champion has been slain by " + p.getPlayerName() + " (level-" + p.determineCombatLevel() + ")");
    } else if (id == 2264) {
      addBossCount(p, id);
    } else if (id == 50) {
      addBossCount(p, id);
      YellSystem
          .bossYell("The King Black Dragon has been slain by " + p.getPlayerName() + " (level-" + p.determineCombatLevel() + ")");
    } else if (id == 3375) {
        addBossCount(p, id);
        YellSystem
            .bossYell("The Most Evil Chicken has been slain by " + p.getPlayerName() + " (level-" + p.determineCombatLevel() + ")");
    } else if (id == 84) {
        addBossCount(p, id);
        YellSystem
            .bossYell("The Devilish Black Demon has been slain by " + p.getPlayerName() + " (level-" + p.determineCombatLevel() + ")");
    } else if (id == 2745) {
        addBossCount(p, id);
        YellSystem
            .bossYell("The Tzhaar Beast has been slain by " + p.getPlayerName() + " (level-" + p.determineCombatLevel() + ")");
    } else if (id == 2373) {
      addBossCount(p, id);
    }

    SlayerTask task = SlayerTask.getSlayerNpc(id);
    if (task != null) {
      if (task.getTaskId() == p.taskId && p.taskAmt < p.taskTotal) {
        p.taskAmt++;
        if (p.taskAmt >= p.taskTotal) {
          p.taskId = -1;
          p.send(new SendMessage("You have completed your slayer task."));
        }
        p.giveExperience(maxHealth * 10, Skill.SLAYER);
      }
    }
  }

  public void drop() {
    for (NpcDrop drop : data.getDrops()) {
      if (drop != null && drop.drop()) { // user won the roll
        Client target = getTarget();
        if (target == null) {
          continue;
        }
        int pid = target.getSlot();
        GroundItem item = new GroundItem(getPosition().getX(), getPosition().getY(), drop.getId(), drop.getAmount(),
            pid, true);

        if (drop.rareShout()) {
          YellSystem.bossYell("<col=292BA3>" + target.getPlayerName() + " has recieved a "
              + target.GetItemName(item.id).toLowerCase() + " from " + npcName().toLowerCase());
        }

        Ground.items.add(item);
        DropLog.recordDrop(target, item, Server.npcManager.getName(id));
      }
    }
  }

  public void respawn() {
    getPosition().moveTo(getOriginalPosition().getX(), getOriginalPosition().getY());
    alive = true;
    visible = true;
    currentHealth = maxHealth;
    getDamage().clear();
    enemies.clear();
  }

  public Client getTarget() {
    for (int i = 0; i < 5; i++) {
      int maxDmg = 0, maxId = 0;
      if (getDamage().isEmpty()) {
        return null;
      }
      for (int id : getDamage().keySet()) {
        if (getDamage().get(id) > maxDmg && validClient(enemies.get(id))) {
          maxDmg = getDamage().get(id);
          maxId = id;
        }
      }
      if (!getDamage().containsKey(maxId))
        continue;
      Client enemy = enemies.get(maxId);
      if (!validClient(enemy) || distanceToPlayer(enemy) > 5) {
        getDamage().remove(maxId);
        enemies.remove(maxId);
      }
      return enemy;
    }
    return null;
  }

  public int getNextWalkingDirection() {
    int dir;
    dir = Utils.direction(getPosition().getX(), getPosition().getY(), (getPosition().getX() + moveX),
        (getPosition().getY() + moveY));
    if (dir == -1) {
      System.out.println("returning -1");
      return -1;
    }
    dir >>= 1;
    return dir;
  }

  /**
   * @return the alive
   */
  public boolean isAlive() {
    return alive;
  }

  /**
   * @param alive
   *          the alive to set
   */
  public void setAlive(boolean alive) {
    this.alive = alive;
  }

  /**
   * @return the currentHealth
   */
  public int getCurrentHealth() {
    return currentHealth;
  }

  /**
   * @return the taskId
   */
  public int getTaskId() {
    return taskId;
  }

  /**
   * @return the visible
   */
  public boolean isVisible() {
    return visible;
  }

  /**
   * @return the deathtime
   */
  public long getDeathTime() {
    return deathTime;
  }

  /**
   * @param visible
   *          the visible to set
   */
  public void setVisible(boolean visible) {
    this.visible = visible;
  }

  /**
   * @return the respawn
   */
  public int getRespawn() {
    return respawn;
  }

  /**
   * @return the fighting
   */
  public boolean isFighting() {
    return fighting;
  }

  public boolean validClient(Client c) {
    if (c != null && !c.disconnected && c.dbId > 0)
      return true;
    return false;
  }

  /**
   * @return the combat
   */
  public int getCombatLevel() {
    return combat;
  }

  /**
   * @return the lastAttack
   */
  public long getLastAttack() {
    return lastAttack;
  }

  public void setLastAttack(long lastAttack) {
    this.lastAttack = lastAttack;
  }

  public int distanceToPlayer(Client p) {
    return (int) Math.sqrt(Math.pow(p.getPosition().getX() - getPosition().getX(), 2)
        + Math.pow(p.getPosition().getY() - getPosition().getY(), 2));
  }

  public void removeEnemy(Client enemy) {
    int id = enemy.dbId;
    if (getDamage().containsKey(id)) {
      getDamage().remove(id);
    }
    if (enemies.containsKey(id)) {
      enemies.remove(id);
    }
  }

  public int getHealth() {
    return maxHealth;
  }

  public int getAttack() {
    return level[1];
  }

  public int getStrength() {
    return level[2];
  }

  public int getDefence() {
    return level[0];
  }

  public int getRange() {
    return level[4];
  }

  public int getMagic() {
    return level[6];
  }

  public String npcName() {
    return Server.npcManager.getName(id).replace("_", " ");
  }
  
  public String getText() {
    return this.text;
  }
  
  public int getDamageDealt() {
    return this.damageDealt;
  }
  
  public boolean isCrit() {
    return this.crit;
  }
  
  public int getMaxHealth() {
    return this.maxHealth;
  }

  public void addMagicHit(Client client) {
    synchronized (magicList) {
      magicList.add(client);
    }
  }
  
  public long getLastChatMessage() {
    return this.lastChatMessage;
  }
  
  public void setLastChatMessage() {
    lastChatMessage = System.currentTimeMillis();
  }
  
  public int getTimeOnFloor() { 
    return this.TIME_ON_FLOOR;
  }
  
}