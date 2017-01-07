package com.dodian.uber.comm;

import com.dodian.Config;
import com.dodian.uber.game.model.Login;
import com.dodian.uber.game.model.entity.player.Client;
import com.dodian.uber.game.model.entity.player.Friend;
import com.dodian.uber.game.model.entity.player.PlayerHandler;
import com.dodian.uber.game.model.item.Equipment;
import com.dodian.uber.game.model.player.packets.outgoing.SendMessage;
import com.dodian.uber.game.model.player.skills.Skill;
import com.dodian.uber.game.model.player.skills.Skills;
import com.dodian.utilities.Database;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

public class LoginManager {

  public int loadCharacterGame(Client p, String playerName, String playerPass) {
    try {
      String query = "SELECT * FROM user WHERE username = '" + playerName + "'";
      ResultSet results = Database.conn.createStatement().executeQuery(query);
      if (results.next()) {
        p.dbId = results.getInt("userid");
        System.out.println("dbid: "+results.getInt("userid")+", "+p.dbId);
        if (results.getString("username").equals(playerName)
            || results.getString("username").equalsIgnoreCase(playerName)) {
          String playerSalt = results.getString("salt");
          String md5pass = Client.passHash(playerPass, playerSalt);
          if (!md5pass.equals(results.getString("password"))) {
        	  System.out.println("Hello!");
            return 3;
          }
          p.playerGroup = results.getInt("usergroupid");
          p.otherGroups = results.getString("membergroupids").split(",");
          p.newPms = (results.getInt("pmunread"));
        } else {
          return 12;
        }
      } else {
    	  return 12;
      }
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("Failed to load player: " + playerName);
      return 13;
    }
    return 0;
  }
  
  public static String UUID;
  public static String customClientVersion;

  public int loadgame(Client p, String playerName, String playerPass) {
    System.out.println("name: "+playerName);
	  System.out.println("test: " + loadCharacterGame(p, playerName, playerPass));
    if (playerName.length() < 1) {
      return 3;
    }
    if (PlayerHandler.isPlayerOn(playerName)) {
    	System.out.println("Detected same char login.");
        return 5;
    } else {
    	System.out.println("Continuing with login procedue, online size is " + PlayerHandler.playersOnline.size());
    	for (Client onlineClient : PlayerHandler.playersOnline.values()) {
    		System.out.println("OnlinePlayer[" + onlineClient.getPlayerName() + "] == [" + p.getPlayerName() + "] ? " + onlineClient.equals(p));
    	}
    }
    if (loadCharacterGame(p, playerName, playerPass) > 0)
      return loadCharacterGame(p, playerName, playerPass);
    long start = System.currentTimeMillis();
    try {
      String query = "select * from characters where id = '" + p.dbId + "'";
      ResultSet results =
      Database.conn.createStatement().executeQuery(query);
      if (results.next()) {
        if (isBanned(p.dbId)) {
          return 4;
        }
        if (Config.customClientVersion != "56") {
        	return 6;
        }
//        if (isSibling(p.dbId)) {
//        	return 23;
//        }
//        if(Login.isUidBanned(LoginManager.UUID)) {
//			return 22;
//		}
        p.setLastVote(results.getLong("lastvote"));
        Client.isSibling = results.getInt("sibling");
        p.UUID = results.getString("uuid");
        p.moveTo(results.getInt("x"), results.getInt("y"), results.getInt("height"));
        if (p.getPosition().getX() == -1 || p.getPosition().getY() == -1) {
          p.moveTo(2606, 3102, 0);
        }
        int health = (results.getInt("health"));
        p.mutedTill = results.getInt("unmutetime");
        Date now = new Date();
        p.rightNow = now.getTime();
        System.out.println(p.mutedTill > p.rightNow);
        System.out.println(p.mutedTill * 1000 + " : " + p.rightNow);
        if (p.mutedTill * 1000 > p.rightNow) {
          p.muted = true;
        }
        long lastOn = (results.getLong("lastlogin"));
        if (lastOn == 0) {
        if (!Login.hasRecieved1stStarter(PlayerHandler.players[p.getSlot()].connectedFrom)) {
          p.getEquipment()[Equipment.Slot.WEAPON.getId()] = 1277;
          p.getEquipment()[Equipment.Slot.SHIELD.getId()] = 1171;
          p.getEquipmentN()[Equipment.Slot.WEAPON.getId()] = 1;
          p.getEquipmentN()[Equipment.Slot.SHIELD.getId()] = 1;
          p.addItem(995, 10000);
          p.addItem(1856, 1);
          p.lookNeeded = true;
          Login.addIpToStarterList1(PlayerHandler.players[p.getSlot()].connectedFrom);
          Login.addIpToStarter1(PlayerHandler.players[p.getSlot()].connectedFrom);
          p.send(new SendMessage(("You have recieved 1 out of 2 starter packages on this IP address.")));
        } else if (Login.hasRecieved1stStarter(PlayerHandler.players[p.getSlot()].connectedFrom) && !Login.hasRecieved2ndStarter(PlayerHandler.players[p.getSlot()].connectedFrom)) {
            p.getEquipment()[Equipment.Slot.WEAPON.getId()] = 1277;
            p.getEquipment()[Equipment.Slot.SHIELD.getId()] = 1171;
            p.getEquipmentN()[Equipment.Slot.WEAPON.getId()] = 1;
            p.getEquipmentN()[Equipment.Slot.SHIELD.getId()] = 1;
            p.addItem(995, 10000);
            p.addItem(1856, 1);
            p.lookNeeded = true;
            p.send(new SendMessage(("You have recieved 2 out of 2 starter packages on this IP address.")));
			Login.addIpToStarterList2(PlayerHandler.players[p.getSlot()].connectedFrom);
			Login.addIpToStarter2(PlayerHandler.players[p.getSlot()].connectedFrom);
		} else if (Login.hasRecieved1stStarter(PlayerHandler.players[p.getSlot()].connectedFrom) && Login.hasRecieved2ndStarter(PlayerHandler.players[p.getSlot()].connectedFrom)) {
			p.send(new SendMessage(("You have already recieved 2 starters!")));
        		}
        	}
        p.taskId = (Integer) (results.getInt("taskid"));
        int Style = (Integer) (results.getInt("fightStyle"));
        p.FightType = Style;
        p.CalculateMaxHit();
        p.taskAmt = (Integer) (results.getInt("taskamt"));
        p.taskTotal = (Integer) (results.getInt("tasktotal"));

        String inventory = (results.getString("inventory").trim());
        String[] parse = inventory.split(" ");
        for (int i = 0; i < parse.length; i++) {
          String[] parse2 = parse[i].split("-");
          if (parse2.length > 0) {
            try {
              int slot = Integer.parseInt(parse2[0]);
              if (Integer.parseInt(parse2[1]) < 66000) {
                p.playerItems[slot] = Integer.parseInt(parse2[1]) + 1;
                p.playerItemsN[slot] = Integer.parseInt(parse2[2]);
              }
            } catch (Exception e) {
            }
          }
        }
        String equip = (results.getString("equipment")).trim();
        parse = equip.split(" ");
        for (int i = 0; i < parse.length; i++) {
          String[] parse2 = parse[i].split("-");
          if (parse2.length > 0) {
            try {
              int slot = Integer.parseInt(parse2[0]);
              if (Integer.parseInt(parse2[1]) < 66000) {
                p.getEquipment()[slot] = Integer.parseInt(parse2[1]);
                p.getEquipmentN()[slot] = Integer.parseInt(parse2[2]);
              }
            } catch (Exception e) {
            }
          }
        }

        String bank = (results.getString("bank")).trim();
        parse = bank.split(" ");
        for (int i = 0; i < parse.length; i++) {
          String[] parse2 = parse[i].split("-");
          if (parse2.length > 0) {
            try {
              int slot = Integer.parseInt(parse2[0]);
              if (Integer.parseInt(parse2[1]) < 66600) {
                p.bankItems[slot] = Integer.parseInt(parse2[1]) + 1;
                p.bankItemsN[slot] = Integer.parseInt(parse2[2]);
              }
            } catch (Exception e) {
            }
          }
        }

        String[] look = results.getString("look").split(" ");
        for (int i = 0; i < look.length; i++) {
          if (look[i].length() > 0) {
            p.playerLooks[i] = Integer.parseInt(look[i]);
          }
        }

        String[] songUnlocked = results.getString("songUnlocked").split(" ");
        for (int i = 0; i < songUnlocked.length; i++) {
          if (songUnlocked[i] == "")
            continue;
          p.setSongUnlocked(i, Integer.parseInt(songUnlocked[i]) == 1 ? true : false);
        }

        String[] friends = results.getString("friends").split(" ");
        for (int i = 0; i < friends.length; i++) {
          if (friends[i].length() > 0) {
            p.friends.add(new Friend(Long.parseLong(friends[i]), true));
          }
        }
        String Boss = results.getString("Boss_Log");
        if (Boss == null) {
          for (int i = 0; i < p.boss_name.length; i++)
            p.boss_amount[i] = 0;
        } else {
          String[] lines = Boss.split(" ");
          for (int i = 0; i < lines.length; i++) {
            String[] parts = lines[i].split(":");
            int amount = Integer.parseInt(parts[1]);
            p.boss_amount[i] = amount;
          }
        }
        String query2 = "select * from character_stats where uid = '" + p.dbId + "'";
        ResultSet results2 = Database.conn.createStatement().executeQuery(query2);
        if (results2.next()) {
          for (int i = 0; i < 21; i++) {
            // p.playerXP[i] = (Integer)(results.getInt("skill" + i));
            p.setExperience(results2.getInt(Skill.getSkill(i).getName()), Skill.getSkill(i));
            // p.playerLevel[i] = p.getLevelForXP(p.playerXP[i]);
            p.setLevel(Skills.getLevelForExperience(p.getExperience(Skill.getSkill(i))), Skill.getSkill(i));
            if (health == 0 && i == 3) {
              p.setCurrentHealth(p.getLevel(Skill.HITPOINTS));
            } else if (health > 0) {
              p.setCurrentHealth(health);
            }
            if (i != 3)
              p.refreshSkill(Skill.getSkill(i));
            // p.setSkillLevel(i, p.playerLevel[i], p.playerXP[i]);
            else {
              p.refreshSkill(Skill.getSkill(i));
              // p.setSkillLevel(i, p.currentHealth, p.playerXP[i]);
              p.maxHealth = p.getLevel(Skill.HITPOINTS);
            }
          }
        }

        p.lastSave = System.currentTimeMillis();
        long elapsed = System.currentTimeMillis() - start;
        p.loadingDone = true;
        PlayerHandler.playersOnline.put(p.longName, p);
        p.println("Loading Process Completed  [" + p.playerRights + ", " + p.dbId + ", " + elapsed + "]");
        return 0;
      } else {
        Statement statement = Database.conn.createStatement();
        String newAccount = "INSERT INTO characters(id, name, equipment, inventory, bank, friends, songUnlocked)" + " VALUES ('"
            + p.dbId + "', '" + playerName + "', '', '', '', '', '0')";
        String newAccountStats = "INSERT INTO character_stats(uid) VALUES ('" + p.dbId + "')";
        statement.executeUpdate(newAccount);
        statement.executeUpdate(newAccountStats);
        return loadgame(p, playerName, playerPass);
      }
    } catch (Exception e) {
      e.printStackTrace();
      return 13;
    }
    // return 13;
  }
  
  public boolean isBanned(int id) throws SQLException {
    String query = "select * from characters where id = '" + id + "'";
    ResultSet results = Database.conn.createStatement().executeQuery(query);
    if (results.next()) {
      if (results.getInt("banned") != 0) {
        return true;
      }
    }
    return false;
  }

}
