package com.dodian.uber.game.security;

import com.dodian.uber.game.model.YellSystem;
import com.dodian.utilities.Database;

import java.sql.Statement;
import java.util.logging.Logger;

/**
 * Saves all the chat logs to the 'chat_log' database.
 * 
 * @author Stephen
 */
public class ChatLog extends LogEntry {

  /**
   * The logger for the class.
   */
  private static final Logger logger = Logger.getLogger(ChatLog.class.getName());

  /**
   * Inserts a new entry into the 'chat_log' table.
   * 
   * @param player
   *          The player sending the message.
   * @param message
   *          The message sent.
   */
  public static void recordChat(String player, String message) {
    try {
      Statement statement = Database.conn.createStatement();
      String query = "INSERT INTO chat_log(username, message, timestamp) VALUES ('" + player + "', '" + message + "', '"
          + getTimeStamp() + "')";
      statement.executeUpdate(query);
      statement.close();
    } catch (Exception e) {
      logger.severe("Unable to record chat!");
      e.printStackTrace();
      YellSystem.alertStaff("Unable to record chat logs, please contact an admin.");
    }
  }
}
