package com.dodian.uber.game.security;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Master class for all log classes.
 * 
 * @author Stephen
 */
public class LogEntry {

  /**
   * Date format for more accurate logs.
   */
  private static DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

  /**
   * Get the current time of the action.
   * 
   * @return The time.
   */
  protected static String getTimeStamp() {
    Date date = new Date();
    return dateFormat.format(date);
  }
}
