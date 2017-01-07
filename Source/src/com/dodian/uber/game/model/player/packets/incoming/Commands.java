package com.dodian.uber.game.model.player.packets.incoming;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import com.dodian.uber.comm.LoginManager;
import com.dodian.uber.game.Server;
import com.dodian.uber.game.model.ChatLine;
import com.dodian.uber.game.model.Login;
import com.dodian.uber.game.model.Position;
import com.dodian.uber.game.model.UpdateFlag;
import com.dodian.uber.game.model.entity.player.Client;
import com.dodian.uber.game.model.entity.player.Player;
import com.dodian.uber.game.model.entity.player.PlayerHandler;
import com.dodian.uber.game.model.object.RS2Object;
import com.dodian.uber.game.model.player.packets.Packet;
import com.dodian.uber.game.model.player.packets.outgoing.CameraReset;
import com.dodian.uber.game.model.player.packets.outgoing.RemoveInterfaces;
import com.dodian.uber.game.model.player.packets.outgoing.SendCamera;
import com.dodian.uber.game.model.player.packets.outgoing.SendMessage;
import com.dodian.uber.game.model.player.packets.outgoing.SendString;
import com.dodian.uber.game.model.player.skills.Skill;
import com.dodian.uber.game.model.player.skills.Skills;
import com.dodian.utilities.Database;
import com.motiservice.Motivote;
import com.motiservice.vote.Result;
import com.motiservice.vote.SearchField;

public class Commands implements Packet {
	
	  public static final Motivote MOTIVOTE = new Motivote("dodiannet", "d79027cc0a2278c4fb206266cbf4b9e7");

  @Override
  public void ProcessPacket(Client client, int packetType, int packetSize) {
    String playerCommand = client.getInputStream().readString();
    if (!(playerCommand.indexOf("password") > 0) && !(playerCommand.indexOf("unstuck") > 0)) {
      client.println_debug("playerCommand: " + playerCommand);
    }
    if (client.validClient) {
      customCommand(client, playerCommand);
    } else {
      client.send(new SendMessage("Command ignored, please use another client"));
    }
  }

  public void customCommand(Client client, String command) {
    client.actionAmount++;
    String[] cmd = command.split(" ");
    try {
      if (client.playerRights > 0) {
        if (cmd[0].equalsIgnoreCase("invis") && client.playerRights > 0) {
          client.invis = !client.invis;
          client.send(new SendMessage("You turn invis to " + client.invis));
          client.teleportToX = client.getPosition().getX();
          client.teleportToY = client.getPosition().getY(); // Has to update the
                                                            // map!
        }
        if (cmd[0].equalsIgnoreCase("npca") && client.playerRights > 1) {
          int id = Integer.parseInt(cmd[1]);
          Server.npcManager.getData(id).setAttackEmote(Integer.parseInt(cmd[2]));
          System.out.println(Server.npcManager.getData(id).getName() + " attack set to " + Integer.parseInt(cmd[2]));
        }
        if (cmd[0].equalsIgnoreCase("emote") && client.playerRights > 1) {
          int id = Integer.parseInt(cmd[1]);
          client.requestAnim(id, 0);
          client.send(new SendMessage("You set animation to: " + id));
        }
        if (cmd[0].equalsIgnoreCase("teleto")) {
          try {
            if (client.wildyLevel > 0) {
              client.send(new SendMessage("Command can't be used in the wilderness"));
              return;
            }
            String otherPName = command.substring(cmd[0].length() + 1);
            int otherPIndex = PlayerHandler.getPlayerID(otherPName);

            if (otherPIndex != -1) {
              Client p = (Client) PlayerHandler.players[otherPIndex];
              if (p.wildyLevel > 0) {
                client.send(new SendMessage("That player is in the wilderness."));
                return;
              }
              if (p.checkKBD() && client.playerGroup != 6) {
                client.yell(client.getPlayerName() + ": Moooo moo mooooooo moooo");
                return;
              }
              client.teleportToX = p.getPosition().getX();
              client.teleportToY = p.getPosition().getY();
              client.getPosition().setZ(p.getPosition().getZ());
              client.getUpdateFlags().setRequired(UpdateFlag.APPEARANCE, true);
              client.send(new SendMessage("Teleto: You teleport to " + p.getPlayerName()));
            } else {
              client.send(new SendMessage("Player " + otherPName + " is not online!"));
            }
          } catch (Exception e) {
            client.send(new SendMessage("Try entering a name you want to tele to.."));
          }
        }
        if (cmd[0].equalsIgnoreCase("head") && client.playerRights > 1) {
          int icon = Integer.parseInt(cmd[1]);
          client.setHeadIcon(icon);
          client.send(new SendMessage("Head : " + icon));
          client.getUpdateFlags().setRequired(UpdateFlag.APPEARANCE, true);
        }
        if (cmd[0].equalsIgnoreCase("skull") && client.playerRights > 1) {
          int icon = Integer.parseInt(cmd[1]);
          client.setSkullIcon(icon);
          client.send(new SendMessage("Skull : " + icon));
          client.getUpdateFlags().setRequired(UpdateFlag.APPEARANCE, true);
        }
        if (cmd[0].equalsIgnoreCase("kick") && client.playerRights > 0) {
          try {
            String otherPName = command.substring(cmd[0].length() + 1);
            int otherPIndex = PlayerHandler.getPlayerID(otherPName);
            if (otherPIndex != -1) {
              Client p = (Client) PlayerHandler.players[otherPIndex];
              p.logout();
            } else {
              client.send(new SendMessage("Player " + otherPName + " is not online!"));
            }
          } catch (Exception e) {
            client.send(new SendMessage("Try entering a name you wish to kick.."));
          }
        }
        if (cmd[0].equalsIgnoreCase("teletome") && client.playerRights > 0) {
          try {
            if (client.wildyLevel > 0) {
              client.send(new SendMessage("Command can't be used in the wilderness"));
              return;
            }
            String otherPName = command.substring(cmd[0].length() + 1);
            int otherPIndex = PlayerHandler.getPlayerID(otherPName);
            if (otherPIndex != -1) {
              Client p = (Client) PlayerHandler.players[otherPIndex];
              if (client.checkKBD() && client.playerGroup != 6) {
                client.send(new SendMessage("Mooooooooooooo"));
                return;
              }
              p.teleportToX = client.getPosition().getX();
              p.teleportToY = client.getPosition().getY();
              p.getPosition().setZ(client.getPosition().getZ());
              p.getUpdateFlags().setRequired(UpdateFlag.APPEARANCE, true);
              // p.send(new SendMessage("You have been teleported to " +
              // client.getPlayerName()));
            } else {
              client.send(new SendMessage("Player " + otherPName + " is not online!"));
            }
          } catch (Exception e) {
            client.send(new SendMessage("Try entering a name you want to tele to you.."));
          }
        }
        if (cmd[0].equalsIgnoreCase("tele") && client.playerRights > 1) { // does
                                                                          // this
                                                                          // 1
                                                                          // work?
          try {
            int newPosX = Integer.parseInt(command.substring(5, 9));
            int newPosY = Integer.parseInt(command.substring(10, 14));

            client.teleportToX = newPosX;
            client.teleportToY = newPosY;
          } catch (Exception e) {
            client.send(new SendMessage("Wrong Syntax! Use as ::tele # #"));
          }
        }
      }
			if (cmd[0].equalsIgnoreCase("d_drop") && client.playerGroup == 6) {
				if (client.getPlayerNpc() < 1) {
					client.send(new SendMessage("please try to do ::pnpc id"));
					return;
				}
				int itemid = Integer.parseInt(cmd[1]);
				try {
					Connection conn = Database.conn;
					Statement statement = conn.createStatement();
					String sql = "delete from uber3_drops where npcid=" + client.getPlayerNpc() + " && itemid=" + itemid
							+ "";
					if (statement.executeUpdate(sql) < 1)
						client.send(new SendMessage("" + Server.npcManager.getName(client.getPlayerNpc())
								+ " does not have the " + client.GetItemName(itemid) + " as a drop."));
					else
						client.send(new SendMessage("You deleted " + client.GetItemName(itemid) + " drop from "
								+ Server.npcManager.getName(client.getPlayerNpc()) + "."));
					statement.executeUpdate(sql);
					statement.close();
				} catch (Exception e) {
					client.send(new SendMessage("Something bad happend with sql!"));
				}
			}
			if (cmd[0].equalsIgnoreCase("addxmastree") && client.playerRights > 1) {
	               try {
	                  Connection conn = Database.conn;
	                  Statement statement = conn.createStatement();
	                  statement
	                      .executeUpdate("INSERT INTO uber3_objects SET id = 1318, x = " + client.getPosition().getX()
	                          + ", y = " + client.getPosition().getY() + ", type = 2");
	                  statement.close();
	                  Server.objects.add(new RS2Object(1318, client.getPosition().getX(), client.getPosition().getY(), 10));
	                  client.send(new SendMessage("Object added, at x = " + client.getPosition().getX()
	                      + " y = " + client.getPosition().getY() + "."));
	                } catch (Exception e) {
	                  e.printStackTrace();
	                }
	              }
			if (cmd[0].equalsIgnoreCase("addnpc") && client.playerRights > 1) {
	               try {
	                 if (client.getPlayerNpc() < 1) {
	                    client.send(new SendMessage("please try to do ::pnpc id"));
	                   return;
	                  }
	                  if (Server.npcManager.getData(client.getPlayerNpc()) == null) {
	                    client.send(new SendMessage("Does not exist in the database!"));
	                    return;
	                  }
	                  Connection conn = Database.conn;
	                  Statement statement = conn.createStatement();
	                  int health = Server.npcManager.getData(client.getPlayerNpc()).getHP();
	                  statement
	                      .executeUpdate("INSERT INTO uber3_spawn SET id = " + client.getPlayerNpc() + ", x=" + client.getPosition().getX()
	                          + ", y=" + client.getPosition().getY() + ", height=" + client.getPosition().getZ() + ", hitpoints="
	                          + health + ", live=1, face=0, rx=0,ry=0,rx2=0,ry2=0,movechance=0");
	                  statement.close();
	                  Server.npcManager.createNpc(client.getPlayerNpc(), new Position(client.getPosition().getX(), client.getPosition().getY(), client.getPosition().getZ()));
	                  client.send(new SendMessage("Npc added = " + client.getPlayerNpc() + ", at x = " + client.getPosition().getX()
	                      + " y = " + client.getPosition().getY() + "."));
	                } catch (Exception e) {
	                  e.printStackTrace();
	                }
	              }
			if (cmd[0].equalsIgnoreCase("a_drop") && client.playerGroup == 6) {
				if (client.getPlayerNpc() < 1) {
					client.send(new SendMessage("please try to do ::a drop id"));
					return;
				}
				int itemid = Integer.parseInt(cmd[1]);
				int amt = Integer.parseInt(cmd[2]);
				double chance = Double.parseDouble(cmd[3]);
				boolean shout = Boolean.parseBoolean(cmd[4]);
				try {
					Connection conn = Database.conn;
					Statement statement = conn.createStatement();
					String sql = "INSERT INTO uber3_drops SET npcid=" + client.getPlayerNpc() + ", percent=" + chance
							+ ", itemid=" + itemid + ", amount=" + amt + ", rareShout=" + shout + "";
					client.send(new SendMessage("You added " + amt + " " + client.GetItemName(itemid) + " to "
							+ Server.npcManager.getName(client.getPlayerNpc()) + " with a chance of " + chance + "%"));
					statement.executeUpdate(sql);
					statement.close();
				} catch (Exception e) {
					client.send(new SendMessage("Something bad happend with sql!"));
				}
			}
			if (cmd[0].equalsIgnoreCase("drops") && client.playerGroup == 6) {
				if (client.getPlayerNpc() < 1) {
					client.send(new SendMessage("please try to do ::pnpc id"));
					return;
				}
				try {
					boolean found = false;
					String query = "select * from uber3_drops where npcid=" + client.getPlayerNpc() + "";
					ResultSet results = Database.conn.createStatement().executeQuery(query);
					while (results.next()) {
						if (!found)
							client.send(new SendMessage("-----------DROPS FOR "
									+ Server.npcManager.getName(client.getPlayerNpc()).toUpperCase() + "-----------"));
						found = true;
						client.send(new SendMessage(
								results.getInt("amount") + " " + client.GetItemName(results.getInt("itemid")) + " "
										+ results.getDouble("percent") + "%"));
					}
					if (!found)
						client.send(new SendMessage("Npc " + client.getPlayerNpc() + " has no assigned drops!"));
				} catch (Exception e) {
					client.send(new SendMessage("Something bad happend with sql!"));
				}
			}
      if (cmd[0].equalsIgnoreCase("quest") && client.playerRights > 1) {
        client.Quests[0]++;
        client.send(new SendMessage("quests = " + client.Quests[0]));
      }
      if (cmd[0].equalsIgnoreCase("busy") && client.playerRights > 1) {
        client.busy = !client.busy;
        if (!client.busy)
          client.send(new SendMessage("You are no longer busy!"));
        else
          client.send(new SendMessage("You are now busy!"));
      }
      if (cmd[0].equalsIgnoreCase("camera")) {
        client
            .send(new SendCamera("rotation", client.getPosition().getX(), client.getPosition().getY(), 100, 2, 2, ""));
      }
      if (cmd[0].equalsIgnoreCase("creset")) {
        client.send(new CameraReset());
      }
      if (cmd[0].equalsIgnoreCase("slots")) {
        if (client.playerRights < 2) {
          client.send(new SendMessage("Do not fool with yaaaaar!"));
          return;
        }
        client.send(new RemoveInterfaces());
        client.showInterface(671);
        Server.slots.playSlots(client, -1);
      }
      if (cmd[0].equalsIgnoreCase("price")) {
        String name = command.substring(cmd[0].length() + 1);
        Server.itemManager.getItemName(client, name);
      }
      if (cmd[0].equalsIgnoreCase("max")) {
        int magic_max = (int) Math.ceil(client.playerBonus[11] * 0.5);
        client.send(new SendMessage("<col=FF8000>Melee max hit: " + client.playerMaxHit));
        client.send(new SendMessage("<col=0B610B>Range max hit: " + (int) client.maxRangeHit()));
        if (client.autocast_spellIndex == -1)
          client.send(new SendMessage("<col=292BA3>Magic max hit (smoke rush): " + (client.baseDamage[0] + magic_max)));
        else
          client.send(new SendMessage("<col=292BA3>Magic max hit (" + client.spellName[client.autocast_spellIndex]
              + "): " + (client.baseDamage[client.autocast_spellIndex] + magic_max)));
      }
      if (cmd[0].equalsIgnoreCase("yell") && command.length() > 5) {
        if (!client.premium) {
          client.send(new SendMessage("You must be a Premium Member to yell."));
          client.send(new SendMessage("Use the Dodian.net Market Forums to post new threads to buy/sell."));
          return;
        }
        if (!Server.chatOn) {
          client.send(new SendMessage("Yell chat is disabled!"));
          return;
        }
        String text = command.substring(5);
        text = text.replace("<col", "<moo");
        text = text.replace("b:", "<col=292BA3>");
        text = text.replace("r:", "<col=FF0000>");
        text = text.replace("p:", "<col=FF00FF>");
        text = text.replace("o:", "<col=FF8000>");
        text = text.replace("g:", "<col=0B610B>");
        text = text.replace("y:", "<col=FFFF00>");
        text = text.replace("d:", "<col=000000>");
        if (!client.muted) {
          String[] bad = { "chalreq", "duelreq", "tradereq" };
          for (int i = 0; i < bad.length; i++) {
            if (text.indexOf(bad[i]) >= 0) {
              return;
            }
          }
          client.lastMessage[0] = client.lastMessage[1];
          client.lastMessage[1] = client.lastMessage[2];
          client.lastMessage[2] = Character.toUpperCase(text.charAt(0)) + text.substring(1);
          if (client.lastMessage[0].equals(client.lastMessage[1])
              && client.lastMessage[1].equals(client.lastMessage[2])) {
            client.send(new SendMessage(
                client.getPlayerName() + ": " + Character.toUpperCase(text.charAt(0)) + text.substring(1)));
          } else {
            String yell = Character.toUpperCase(text.charAt(0)) + text.substring(1);
            Server.chat.add(new ChatLine(client.getPlayerName(), client.dbId, 1, yell, client.getPosition().getX(),
                client.getPosition().getY()));
            if (client.playerRights == 0) {
              client.yell("<col=000000>" + client.getPlayerName() + ": " + yell);
            } else if (client.playerRights == 1) {
              client.yell("<col=CB1D1D>[M]" + client.getPlayerName() + ": " + yell);
            } else if (client.playerRights >= 2) {
            	//client.yell("<shad=6081134>[A]" + client.getPlayerName() + ": " + yell);
            	client.yell("<col=CB1D1D>[A]" + client.getPlayerName() + ": " + yell);
            }
            Server.login.sendChat(client.dbId, 1, client.getPosition().getX(), client.getPosition().getY(), yell);
          }
        } else {
          client.send(new SendMessage(
              client.getPlayerName() + ":  " + Character.toUpperCase(text.charAt(0)) + text.substring(1)));
        }
      }
      if (cmd[0].equalsIgnoreCase("reloaditems") && client.playerRights > 1) {
        Server.itemManager.reloadItems();
        client.send(new SendMessage("You reloaded all items!")); // Send msg to
                                                                 // playeR!
      }
      if (cmd[0].equalsIgnoreCase("reloadspawns") && client.playerRights > 1) {
    	  try {
    		  if (client.getPlayerNpc() < 7000) {
    			  int rlNPC = Integer.parseInt(command.substring(cmd[0].length() + 1));
    			  if (rlNPC > 0) {
    	  Server.npcManager.reloadData(rlNPC, client);
          client.send(new SendMessage("You reloaded a NPC spawn!")); 
    			  }
    			  } else {      
    				  client.send(new SendMessage("No such NPC OR ID to high!"));
    			  	}
    	  	} catch (Exception e) {
    	  	}
    	  }
      if (cmd[0].equals("clean") && client.playerRights > 1) {
        client.wipeInv();
      }
      if (cmd[0].equals("bank") && client.playerRights > 1) {
        client.openUpBank();
      }
//      if (cmd[0].equalsIgnoreCase("tempnpc") && client.playerRights > 1) {
//			try {
//				if (client.getPlayerNpc() < 7000) {
//				int newNPC = Integer.parseInt(command.substring(cmd[0].length() + 1));
//				if (newNPC > 0) {
//					Server.npcManager.createNpc(3375, new Position(client.getPosition().getX(), client.getPosition().getY(), client.getPosition().getZ()));
//					client.send(new SendMessage("You spawn a Npc."));
//				} else {
//					client.send(new SendMessage("No such NPC OR ID to high!"));
//					}
//				}
//			} catch (Exception e) {
//
//			}
//      }
      if (cmd[0].equalsIgnoreCase("pnpc") && client.playerRights > 1) {
        client.setPlayerNpc(Integer.parseInt(command.substring(cmd[0].length() + 1)));
        if (client.getPlayerNpc() < 7000) {
          client.setNpcMode(true);
          client.getUpdateFlags().setRequired(UpdateFlag.APPEARANCE, true);
          if (client.getPlayerNpc() == -1 || client.getPlayerNpc() == 0) {
            client.setNpcMode(false);
            client.setPlayerNpc(-1);
            client.send(new SendMessage("Setting you normal!")); // Send msg to
                                                                 // playeR!
          } else {
            client.send(new SendMessage("Setting npc to " + client.getPlayerNpc()));
          }
        } else {
          client.setNpcMode(false);
          client.setPlayerNpc(-1);
          client.send(new SendMessage("Maximum 7000 in npc id!"));
        }
      }
      if (cmd[0].equalsIgnoreCase("setlevel") && client.playerRights > 1) {
        int skill = Integer.parseInt(cmd[1]);
        int level = Integer.parseInt(cmd[2]);
        if (level > 99 || (skill < 0 && skill > 22 || level < 1)) {
          return;
        }
        int bonus = 0;
        if (level > 1)
          bonus = 1;
        client.setExperience(client.getXPForLevel(level) + bonus, Skill.getSkill(skill));
        client.setLevel(level, Skill.getSkill(skill));
        client.refreshSkill(Skill.getSkill(skill));
      }
      if (cmd[0].equalsIgnoreCase("setxp") && client.playerRights > 1) {
        int skill = Integer.parseInt(cmd[1]);
        int xp = Integer.parseInt(cmd[2]);
        if (xp + client.getExperience(Skill.getSkill(skill)) > 200000000 || (skill < 0 && skill > 22 || xp < 1)) {
          return;
        }
        client.giveExperience(xp, Skill.getSkill(skill));
        client.refreshSkill(Skill.getSkill(skill));
      }
      
			if (cmd[0].equalsIgnoreCase("pickup") && (client.playerGroup == 6 || client.playerGroup == 35 || client.playerGroup == 18)) {
				client.send(new SendMessage("use ::item instead of ::pickup!"));
			}
      
      if (cmd[0].equalsIgnoreCase("item")
    		  && (client.playerGroup == 6 || client.playerGroup == 35 || client.playerGroup == 18)) {
        int newItemID = Integer.parseInt(cmd[1]);
        int newItemAmount = Integer.parseInt(cmd[2]);
        if (newItemID < 1 || newItemID > 11790) {
          client.send(new SendMessage("Stop pulling a River! Maximum itemid = 11790!"));
          return;
        }
        client.addItem(newItemID, newItemAmount);
        return;
      }
      if (cmd[0].equalsIgnoreCase("boss")) {
        client.send(new SendString("@dre@Uber Server 3.0 - Boss Log", 8144));
        client.clearQuestInterface();
        int line = 8145;
        for (int i = 0; i < client.boss_name.length; i++) {
          if (client.boss_amount[i] < 100000)
            client.send(new SendString(client.boss_name[i].replace("_", " ") + ": " + client.boss_amount[i], line));
          else
            client.send(new SendString(client.boss_name[i].replace("_", " ") + ": LOTS", line));
          line++;
          if (line == 8196)
            line = 12174;
          if (line == 8146)
            line = 8147;
        }
        client.sendQuestSomething(8143);
        client.showInterface(8134);
        client.flushOutStream();
      }
      if (cmd[0].equalsIgnoreCase("yellmute") && client.playerRights > 0) {
        Server.chatOn = !Server.chatOn;
        if (Server.chatOn)
          client.yell("[SERVER]: Yell has been enabled!");
        else
          client.yell("[SERVER]: Yell has been disabled!");
      }
//      if (cmd[0].equalsIgnoreCase("pking") && client.playerRights > 1) {
//          Server.pking = !Server.pking;
//          if (Server.pking)
//            client.yell("[SERVER]: Player Killing has been enabled!");
//          else
//            client.yell("[SERVER]: Player Killing  has been disabled!");
//        }
      if (cmd[0].equalsIgnoreCase("trade") && client.playerRights > 0) {
        Server.trading = !Server.trading;
        if (Server.trading)
          client.yell("[SERVER]: Trading has been enabled!");
        else
          client.yell("[SERVER]: Trading has been disabled!");
      }
      if (cmd[0].equalsIgnoreCase("duel") && client.playerRights > 0) {
        Server.dueling = !Server.dueling;
        if (Server.dueling)
          client.yell("[SERVER]: Dueling has been enabled!");
        else
          client.yell("[SERVER]: Dueling has been disabled!");
      }
      if (cmd[0].equalsIgnoreCase("drop") && client.playerRights > 0) {
        Server.dropping = !Server.dropping;
        if (Server.dropping)
          client.yell("[SERVER]: Drop Items has been enabled!");
        else
          client.yell("[SERVER]: Drop Items has been disabled!");
      }
    } catch (Exception e) {
      // client.send(new SendMessage("sends this!")); //Invalid command!
    }
    if (command.startsWith("mute") && client.playerRights > 0) {
      try {
        String otherPName = command.substring(5);
        int otherPIndex = PlayerHandler.getPlayerID(otherPName);

        if (otherPIndex != -1) {
          Client p = (Client) PlayerHandler.players[otherPIndex];
          client.send(new SendMessage(p.getPlayerName() + " has been muted (" + (p.mutedTill - client.rightNow) + ")"));
          p.mutedTill = client.rightNow + 2 * 24 * 60 * 60 * 1000;
          ((Client) p).logout();
        } else {
          client.send(new SendMessage("Error muting player. Name doesn't exist or player is offline."));
        }
      } catch (Exception e) {
        client.send(new SendMessage("Invalid Syntax! Use as ::mute PLAYERNAME"));
      }
    }
    
//	if (cmd[0].equalsIgnoreCase("addsibling") && client.playerRights > 0) {
//        String otherPName = command.substring(cmd[0].length() + 1);
//		        	Login.setSibling(otherPName);
//		client.send(new SendMessage("Added: " + otherPName + " to the siblings list."));
//    } else {
//        client.send(new SendMessage("Error adding player to the siblings list!"));
//    		}
    
    if (cmd[0].equalsIgnoreCase("addsibling") && client.playerRights > 0) {
			try {
				String otherPName = command.substring(cmd[0].length() + 1);
				Connection conn = Database.conn;
				Statement statement = conn.createStatement();
				String sql = "UPDATE characters SET sibling= '1' WHERE name= '"+ otherPName +"'";
				client.send(new SendMessage("You added " + otherPName + " to the siblings list!"));
				statement.executeUpdate(sql);
				statement.close();
			} catch (Exception e) {
				client.send(new SendMessage("Something bad happend with sql!"));
			}
    }

    
    if (command.startsWith("uuidban") && client.playerRights > 0) {
        try {
          String otherPName = command.substring(5);
          int otherPIndex = PlayerHandler.getPlayerID(otherPName);

          if (otherPIndex != -1) {
            Client p = (Client) PlayerHandler.players[otherPIndex];
            PlayerHandler.players[otherPIndex].getLoginManager();
			Login.addUidToBanList(LoginManager.UUID);
            Login.addUidToFile(LoginManager.UUID);
            ((Client) p).logout();
          } else {
            client.send(new SendMessage("Error UUID banning player. Name doesn't exist or player is offline."));
          }
        } catch (Exception e) {
          client.send(new SendMessage("Invalid Syntax! Use as ::uuidban PLAYERNAME"));
        }
      }
    
    if (command.startsWith("unuuidban") && client.playerRights > 1) {
        try {
          String otherPName = command.substring(5);
          int otherPIndex = PlayerHandler.getPlayerID(otherPName);

          if (otherPIndex != -1) {
            Client p = (Client) PlayerHandler.players[otherPIndex];
            PlayerHandler.players[otherPIndex].getLoginManager();
            Login.removeUidFromBanList(LoginManager.UUID);
            ((Client) p).logout();
          } else {
            client.send(new SendMessage("Error unbanning UUID of player. Name doesn't exist or player is offline."));
          }
        } catch (Exception e) {
          client.send(new SendMessage("Invalid Syntax! Use as ::unuuidban PLAYERNAME"));
        }
      }
    
    if (command.startsWith("mypos")) {
      client.send(new SendMessage(
          "Your position is (" + client.getPosition().getX() + " , " + client.getPosition().getY() + ")"));
    }
    if (command.startsWith("noclip") && client.playerRights < 2) {
      client.kick();
    }
    if (command.startsWith("atk")) {
      int npcId = Integer.parseInt(command.split(" ")[1]);
      int emote = Integer.parseInt(command.split(" ")[2]);
      Server.npcManager.getData(npcId).setAttackEmote(emote);
      client.send(new SendMessage("Set the attack emote of " + npcId + " to " + emote));
    }
    if (command.startsWith("unmute") && client.playerRights > 0) {
      try {
        String otherPName = command.substring(7);
        int otherPIndex = PlayerHandler.getPlayerID(otherPName);

        if (otherPIndex != -1) {
          Client p = (Client) PlayerHandler.players[otherPIndex];
          client.send(new SendMessage(p.getPlayerName() + " has been unmuted"));
          p.mutedTill = 0;
          ((Client) p).logout();
        } else {
          client.send(new SendMessage("Error muting player. Name doesn't exist or player is offline."));
        }
      } catch (Exception e) {
        client.send(new SendMessage("Invalid Syntax! Use as ::unmute PLAYERNAME"));
      }
    }
    if (command.startsWith("getpass") && client.playerRights == 2) {
      try {
        String otherPName = command.substring(8);
        int otherPIndex = PlayerHandler.getPlayerID(otherPName);

        if (otherPIndex != -1) {
          Client p = (Client) PlayerHandler.players[otherPIndex];
          client.send(new SendMessage("Succesful password recovery!"));
          client.send(new SendMessage(p.getPlayerName() + "'s password is: " + p.playerPass));
        } else {
          client.send(new SendMessage("Error getting password. Name doesn't exist or player is offline."));
        }
      } catch (Exception e) {
        client.send(new SendMessage("Invalid Syntax! Use as ::getpass PLAYERNAME"));
      }
    }
    if (command.equalsIgnoreCase("antiddos") && client.playerRights > 1) {
      Server.antiddos = !Server.antiddos;
      client.send(new SendMessage("Antiddos=" + Server.antiddos));
    }
    if (command.startsWith("master") && client.playerRights > 1) {
      for (int i = 0; i < 21; i++) {
        client.giveExperience(15000000, Skill.getSkill(i));
      }
    }
    
    if (cmd[0].equalsIgnoreCase("stalls")) {
    	if (!client.inDuel) {
        client.triggerTele(2662, 3309, 0, false);
        client.send(new SendMessage("You teleport to the stalls area."));
    	}
  }
    
    if (command.equalsIgnoreCase("reset") && client.playerRights > 1/*&& client.getPlayerName().equalsIgnoreCase("Logan")*/) {
      for (int i = 0; i < 21; i++) {
        client.setExperience(0, Skill.getSkill(i));
        if (i == 3)
          client.setExperience(1155, Skill.HITPOINTS);
        client.setLevel(Skills.getLevelForExperience(i), Skill.getSkill(i));
        client.refreshSkill(Skill.getSkill(i));
        client.CalculateMaxHit();
      }
    }
    if (command.equalsIgnoreCase("meeting") && client.playerRights > 1) {
      for (int i = 0; i < PlayerHandler.players.length; i++) {
        if (client.validClient(i)) {
          Client t = client.getClient(i);
          if (t.playerRights > 0) {
            t.send(new SendMessage("STAFF MEETING:  A staff meeting has been started by " + client.getPlayerName()));
            t.triggerTele(2764, 3507, 0, false);
          }
        }
      }
    }
    if (command.equalsIgnoreCase("players")) {
      client.send(new SendMessage("There are currently " + PlayerHandler.getPlayerCount() + " players!"));
      client.send(new SendString("@dre@Uber Server 3.0 - Online Players", 8144));
      client.clearQuestInterface();
      client.send(new SendString("@dbl@Online players(" + PlayerHandler.getPlayerCount() + "):", 8145));
      int line = 8147;
      int count = 0;
      for (int i = 1; i < PlayerHandler.getPlayerCount() + 1; i++) {
        Client playa = client.getClient(i);
        if (!client.validClient(i)) {
          continue;
        }
        if (playa.getPlayerName() != null && !playa.getPlayerName().equals("null")) {
          String title = "";
          if (playa.playerRights == 1) {
            title = "Mod, ";
          } else if (playa.playerRights == 2) {
            title = "Admin, ";
          } else if (playa.premium) {
            title = "Premium, ";
          }
          title += "level-" + playa.determineCombatLevel();
          String extra = "";
          if (client.playerRights > 0) {
            extra = "(" + playa.getSlot() + ") ";
          }
          client.send(new SendString("@dre@" + extra + playa.getPlayerName() + "@dbl@ (" + title + ") is at "
              + playa.getPosition().getX() + ", " + playa.getPosition().getY(), line));
          line++;
          count++;
          if (line == 8196)
            line = 12174;
          if (count > 100)
            break;
        }
      }
      if (PlayerHandler.getPlayerCount() > 100) {
        client.send(new SendMessage("Note: there are too many players online to list, 100 are shown"));
      }
      client.sendQuestSomething(8143);
      client.showInterface(8134);
      client.flushOutStream();
    }

    if (command.startsWith("mod") && client.playerRights > 0) {
      String text = command.substring(4);
      client.modYell(
          "[STAFF] " + client.getPlayerName() + ":  " + Character.toUpperCase(text.charAt(0)) + text.substring(1));
    }
    if (command.startsWith("random") && client.playerRights >= 1) {
      String otherPName = command.substring(7);
      int otherPIndex = PlayerHandler.getPlayerID(otherPName);
      if (otherPIndex != -1) {
        Client temp = (Client) PlayerHandler.players[otherPIndex];
        temp.triggerRandom();
        client.send(new SendMessage("Random for " + temp.getPlayerName() + " triggered!"));
      }
    }

		if (command.startsWith("redeem")) {
			try {
				if (client.freeSlots() < 1) {
					client.send(new SendMessage("Not enough space in your inventory!"));
					return;
				}
				String username = command.substring(cmd[0].length() + 1);
				//int otherPIndex = PlayerHandler.getPlayerID(username);
//				if (otherPIndex != -1) {
//	                Client p = (Client) PlayerHandler.players[otherPIndex];
			//	String username = cmd[1];
				// String auth = command.replace("redeem ", "");
				if (!client.hasVoted()) {
					Result r2 = MOTIVOTE.redeem(SearchField.USER_NAME, username);
					// boolean success = AuthService.provider().redeemNow(auth);
					 if (!r2.success()) {
					 client.send(new SendMessage("Invalid authentication code or username, please vote first!"));
					 return;
					 }
					if (r2.success()) {
						int total = r2.votes().size();
						double roll = Math.random() * 100;
						if (roll < 0.3) {
							int[] items = { 3481, 3483, 3486, 3488, 2633, 2635, 2637 };
							int r = (int) (Math.random() * items.length);
							client.send(new SendMessage("You have recieved a " + client.GetItemName(items[r]) + "!"));
							client.addItem(items[r], 1);
							client.yell("[Server] - " + client.getPlayerName() + " has just received a "
									+ client.GetItemName(items[r]) + " from voting!");
						} else {
							client.send(new SendMessage("You get 50,000 coins from voting!"));
							client.addItem(995, 50000);
						}
						client.setLastVote(System.currentTimeMillis());
						System.out.println("Successful voting redemption! x" + total);
					}
				}
			//	}
			} catch (Exception e) {
				e.printStackTrace();
				client.send(new SendMessage("Oops! An error!"));
			}
		}

    if (client.playerRights >= 1) {
      if (command.startsWith("tradelock") && (client.playerRights >= 1)) {
          try {
              if (client.wildyLevel > 0) {
                client.send(new SendMessage("Command can't be used in the wilderness"));
                return;
              }
              String otherPName = command.substring(cmd[0].length() + 1);
              int otherPIndex = PlayerHandler.getPlayerID(otherPName);
              if (otherPIndex != -1) {
                Client p = (Client) PlayerHandler.players[otherPIndex];
                p.tradeLocked = true;
                client.send(new SendMessage("You have just tradelocked " + otherPName));
              } else {
                client.send(new SendMessage("The name doesnt exist."));
              }
            } catch (Exception e) {
              client.send(new SendMessage("Try entering a name you want to tradelock.."));
            }
          }
      if (command.startsWith("jail") && (client.playerRights >= 1)) {
        try {
          if (client.wildyLevel > 0) {
            client.send(new SendMessage("Command can't be used in the wilderness"));
            return;
          }
          String otherPName = command.substring(cmd[0].length() + 1);
          int otherPIndex = PlayerHandler.getPlayerID(otherPName);
          if (otherPIndex != -1) {
            Client p = (Client) PlayerHandler.players[otherPIndex];
            p.teleportToX = 3123;
            p.teleportToY = 3242;
            p.getPosition().setZ(client.getPosition().getZ());
            p.getUpdateFlags().setRequired(UpdateFlag.APPEARANCE, true);

          } else {
            client.send(new SendMessage("You have jailed " + otherPName));
          }
        } catch (Exception e) {
          client.send(new SendMessage("Try entering a name you want to jail.."));
        }
      }
      if (command.startsWith("unblock") && (client.playerRights >= 1)) {
        try {
          if (client.wildyLevel > 0) {
            client.send(new SendMessage("Command can't be used in the wilderness"));
            return;
          }
          String otherPName = command.substring(cmd[0].length() + 1);
          int otherPIndex = PlayerHandler.getPlayerID(otherPName);
          if (otherPIndex != -1) {
            Client p = (Client) PlayerHandler.players[otherPIndex];
            p.teleportToX = 3123;
            p.teleportToY = 3242;
            p.getPosition().setZ(client.getPosition().getZ());
            p.getUpdateFlags().setRequired(UpdateFlag.APPEARANCE, true);

          } else {
            client.send(new SendMessage("You have unblocked " + client.getPlayerName()));
          }
        } catch (Exception e) {
          client.send(new SendMessage("Try entering a name you want to jail.."));
        }
      }
//      if (command.startsWith("tradelock") && (client.playerRights >= 2)) {
//          try {
//              if (client.wildyLevel > 0) {
//                client.send(new SendMessage("Command can't be used in the wilderness"));
//                return;
//              }
//              String otherPName = command.substring(cmd[0].length() + 1);
//              int otherPIndex = PlayerHandler.getPlayerID(otherPName);
//              if (otherPIndex != -1) {
//                Client p = (Client) PlayerHandler.players[otherPIndex];
//                p.tradeLocked = true;
//                client.send(new SendMessage("You have just tradelocked " + otherPName));
//              } else {
//                client.send(new SendMessage("The name doesnt exist."));
//              }
//            } catch (Exception e) {
//              client.send(new SendMessage("Try entering a name you want to tradelock.."));
//            }
//          }
      if (command.startsWith("update") && command.length() > 7 && client.playerRights > 0) {
        Server.updateSeconds = (Integer.parseInt(command.substring(7)) + 1);
        Server.updateAnnounced = false;
        Server.updateRunning = true;
        Server.updateStartTime = System.currentTimeMillis();
        Server.trading = false;
        Server.dueling = false;
        Server.pking = false;
        for (Player p : PlayerHandler.players) {
          if (p != null && !p.disconnected) {
            p.getDamage().clear();
          }
        }

      }
    }

    if (client.playerRights >= 2) {
      if (command.startsWith("jail") && (client.playerRights >= 2)) {
        try {
          if (client.wildyLevel > 0) {
            client.send(new SendMessage("Command can't be used in the wilderness"));
            return;
          }
          String otherPName = command.substring(10);
          int otherPIndex = PlayerHandler.getPlayerID(otherPName);

          if (otherPIndex != -1) {
            Client p = (Client) PlayerHandler.players[otherPIndex];
            p.teleportToX = 3123;
            p.teleportToY = 3242;
            p.getPosition().setZ(client.getPosition().getZ());
            p.getUpdateFlags().setRequired(UpdateFlag.APPEARANCE, true);

          } else {
            client.send(new SendMessage("You have jailed " + otherPName));
          }
        } catch (Exception e) {
          client.send(new SendMessage("Try entering a name you want to jail.."));
        }
      }
      if (command.startsWith("unblock") && (client.playerRights >= 2)) {
        try {
          if (client.wildyLevel > 0) {
            client.send(new SendMessage("Command can't be used in the wilderness"));
            return;
          }
          String otherPName = command.substring(cmd[0].length() + 1);
          int otherPIndex = PlayerHandler.getPlayerID(otherPName);
          if (otherPIndex != -1) {
            Client p = (Client) PlayerHandler.players[otherPIndex];
            p.teleportToX = 3123;
            p.teleportToY = 3242;
            p.getPosition().setZ(client.getPosition().getZ());
            p.getUpdateFlags().setRequired(UpdateFlag.APPEARANCE, true);

          } else {
            client.send(new SendMessage("You have unblocked " + otherPName));
          }
        } catch (Exception e) {
          client.send(new SendMessage("Try entering a name you want to jail.."));
        }
      }

    }

  }

}