package com.dodian;

/**
 * 
 * @author Fabrice L
 *
 */
public class Config {

  private static int port = 43594;
  public static int worldId = 1;
  public static String customClientVersion = "56";

  private static int experienceMultiplier = 1;

  private static String mysqlDatabase = "dodiannet";
  private static String mysqlUser = "lmg";
  private static String mysqlPass = "MzHyn7eHeFnm9atH"; // MzHyn7eHeFnm9atH
  private static String mysqlUrl = "jdbc:mysql://srv.dodian.net/";

  public static int getPort() {
    return port;
  }

  public static int getWorldId() {
    return worldId;
  }

  public static String getMysqlDatabase() {
    return mysqlDatabase;
  }

  public static String getMysqlUser() {
    return mysqlUser;
  }

  public static String getMysqlPass() {
    return mysqlPass;
  }

  public static String getMysqlUrl() {
    return mysqlUrl;
  }

  public static int getExperienceMultiplier() {
    return experienceMultiplier;
  }

}
