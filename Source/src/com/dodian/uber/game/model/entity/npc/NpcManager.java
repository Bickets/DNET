/**
 * 
 */
package com.dodian.uber.game.model.entity.npc;

/**
 * @author Owner
 *
 */

import com.dodian.jobs.JobScheduler;
import com.dodian.jobs.impl.NpcProcessor;
import com.dodian.uber.game.model.Position;
import com.dodian.uber.game.model.entity.player.Client;
import com.dodian.uber.game.model.player.packets.outgoing.SendMessage;
import com.dodian.utilities.Database;

import org.quartz.SchedulerException;

import java.sql.ResultSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class NpcManager extends Thread {
  Map<Integer, Npc> npcs = new HashMap<Integer, Npc>();
  Map<Integer, NpcData> data = new HashMap<Integer, NpcData>();
  int nextIndex = 1;

  public void run() {
    loadData();
    loadSpawns();
    try {
      JobScheduler.ScheduleStaticRepeatForeverJob(600, NpcProcessor.class);
    } catch (SchedulerException e) {
      e.printStackTrace();
    }
  }

  public Collection<Npc> getNpcs() {
    return npcs.values();
  }

  public void loadSpawns() {
    try {
      int amount = 0;
      ResultSet results = Database.statement.executeQuery("SELECT * FROM uber3_spawn");
      while (results.next()) {
        amount++;
        createNpc(results.getInt("id"), new Position(results.getInt("x"), results.getInt("y"), results.getInt("height")));

      }
      System.out.println("Loaded " + amount + " Npc Spawns");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

	public void reloadData(int npcID, Client c)
    {    
		data.clear();
		loadData();
		loadSpawns();
		c.send(new SendMessage("Reloaded (npc: "+npcID+")"));
    }
  
  public void loadData() {
    try {
      int amount = 0;
      ResultSet results = Database.statement.executeQuery("SELECT * FROM uber3_npcs");
      while (results.next()) {
        amount++;
        data.put(results.getInt("id"), new NpcData(results));
      }
      System.out.println("Loaded " + amount + " Npc Definitions");
      amount = 0;
      results = Database.statement.executeQuery("SELECT * FROM uber3_drops");
      while (results.next()) {
        if (results.getInt("npcid") > 0) {
          amount++;
          if (data.containsKey(results.getInt("npcid"))) {
            data.get(results.getInt("npcid")).addDrop(results.getInt("itemid"), results.getInt("amount"),
                results.getDouble("percent"), results.getBoolean("rareShout"));
          } else {
            System.out.println("Invalid key: " + results.getInt("npcid"));
          }
        }
      }
      System.out.println("Loaded " + amount + " Npc Drops");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void createNpc(int id, Position position) {
    npcs.put(nextIndex, new Npc(nextIndex, id, position));
    nextIndex++;
  }

  public Npc getNpc(int index) {
    if (index > 0 && index < nextIndex && npcs.get(index) != null) {
      return npcs.get(index);
    } else {
      return null;
    }
  }

  public String getName(int id) {
    return data.get(id).getName();
  }

  public NpcData getData(int id) {
    return data.get(id);
  }
}