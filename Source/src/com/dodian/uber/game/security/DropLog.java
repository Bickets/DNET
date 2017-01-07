package com.dodian.uber.game.security;

import com.dodian.uber.game.model.YellSystem;
import com.dodian.uber.game.model.entity.player.Player;
import com.dodian.uber.game.model.item.GroundItem;
import com.dodian.utilities.Database;

import java.sql.Statement;
import java.util.logging.Logger;

/**
 * Saves information about every player dropped item on the server. Contains
 * (Username, the item, the amount).
 * 
 * @author Stephen
 */
public class DropLog extends LogEntry {

  /**
   * The logger for the class.
   */
  private final static Logger logger = Logger.getLogger(DropLog.class.getName());

  /**
   * Adds a drop record to the drop table.
   * 
   * @param player
   *          The player dropping the item.
   * @param item
   *          The item being dropped.
   */
  public static void recordDrop(Player player, GroundItem item, String type) {
    try {
      Statement statement = Database.conn.createStatement();
      String query = "INSERT INTO drop_log(username, item, amount, type, timestamp) VALUES ('" + player.getPlayerName()
          + "', '" + item.id + "', '" + item.amount + "', '" + type + "', '" + getTimeStamp() + "')";
      statement.executeUpdate(query);
      statement.close();
    } catch (Exception e) {
      logger.severe("Unable to record dropped item!");
      e.printStackTrace();
      YellSystem.alertStaff("Unable to record dropped items, please contact an admin.");
    }
  }

}
