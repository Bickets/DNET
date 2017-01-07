package com.dodian.uber.game;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import com.dodian.Config;
import com.dodian.cache.Cache;
import com.dodian.cache.object.GameObjectData;
import com.dodian.cache.object.ObjectDef;
import com.dodian.cache.object.ObjectLoader;
import com.dodian.cache.region.Region;
import com.dodian.jobs.JobScheduler;
import com.dodian.jobs.impl.GroundItemProcessor;
import com.dodian.jobs.impl.ItemProcessor;
import com.dodian.jobs.impl.PlayerProcessor;
import com.dodian.jobs.impl.ShopProcessor;
import com.dodian.uber.comm.ConnectionList;
import com.dodian.uber.comm.LoginManager;
import com.dodian.uber.comm.Memory;
import com.dodian.uber.game.event.EventManager;
import com.dodian.uber.game.model.ChatLine;
import com.dodian.uber.game.model.ChatProcess;
import com.dodian.uber.game.model.Login;
import com.dodian.uber.game.model.ShopHandler;
import com.dodian.uber.game.model.entity.npc.NpcManager;
import com.dodian.uber.game.model.entity.player.Client;
import com.dodian.uber.game.model.entity.player.Player;
import com.dodian.uber.game.model.entity.player.PlayerHandler;
import com.dodian.uber.game.model.item.ItemManager;
import com.dodian.uber.game.model.object.DoorHandler;
import com.dodian.uber.game.model.object.RS2Object;
import com.dodian.uber.game.model.player.casino.SlotMachine;
import com.dodian.uber.game.model.player.skills.Thieving;
import com.dodian.utilities.Database;
import com.dodian.utilities.Rangable;
import com.dodian.utilities.Utils;

/**
 * Testing..
 * 
 * @author Arch337
 *
 */
public class Server implements Runnable {

  private static List<Integer> excludedBeardItems = new ArrayList<Integer>();
  public static boolean trading = true, dueling = true, chatOn = true, pking = false, dropping = true;
  private static int delay = 0;
  public static long lastRunite = 0;
  public static int world = 5;

  public static boolean updateAnnounced;
  public static boolean updateRunning;
  public static int updateSeconds;
  public static long updateStartTime;
  public Player player;
  public Client c;
  
  private static String MySQLDataBase = "vBulletin";
  public static String MySQLURL = "";
  public static String MySQLUser = "";
  public static String MySQLPassword = "";
  public static ArrayList<String> connections = new ArrayList<String>();
  public static ArrayList<String> banned = new ArrayList<String>();
  public static ArrayList<RS2Object> objects = new ArrayList<RS2Object>();
  public static CopyOnWriteArrayList<ChatLine> chat = new CopyOnWriteArrayList<ChatLine>();
  private static ChatProcess chatprocess = null;
  public static int nullConnections = 0;
  public static Login login = null;
  public static CopyOnWriteArrayList<ChatLine> yell = new CopyOnWriteArrayList<ChatLine>();
  public static ItemManager itemManager = null;
  public static NpcManager npcManager = null;
  public static SlotMachine slots = new SlotMachine();
  public static Map<String, Long> tempConns = new HashMap<String, Long>();
  public static com.dodian.uber.comm.LoginManager loginManager = null;

  public static void main(java.lang.String args[]) throws Exception {
    System.out.println();
    System.out.println("    ____            ___               ");
    System.out.println("   / __ \\____  ____/ (_)___ _____    ");
    System.out.println("  / / / / __ \\/ __  / / __ `/ __ \\  ");
    System.out.println(" / /_/ / /_/ / /_/ / / /_/ / / / /    ");
    System.out.println("/_____/\\____/\\____/_/\\____/_/ /_/  ");
    System.out.println();

    world = Config.getWorldId();
    MySQLUser = Config.getMysqlUser();
    MySQLPassword = Config.getMysqlPass();
    MySQLDataBase = Config.getMysqlDatabase();
    MySQLURL = Config.getMysqlUrl() + MySQLDataBase;

    //new Motivote<>(new RewardHandler(), "http://srv.dodian.net/voting/", "e18b9362").start();
    //AuthService.setProvider(new Motivote("dodiannet", "d79027cc0a2278c4fb206266cbf4b9e7"));
    Database.init();
    new JobScheduler();
    ConnectionList.getInstance();
    playerHandler = new PlayerHandler();
    loginManager = new LoginManager();
    shopHandler = new ShopHandler();
    thieving = new Thieving();
    Memory.getSingleton().process();

    Cache.load();
    // Load regions
    ObjectDef.loadConfig();
    Region.load();
    Rangable.load();

    // Load objects
    ObjectLoader objectLoader = new ObjectLoader();
    objectLoader.load();

    GameObjectData.init();
    loadObjects();

    login = new Login();
    new Thread(login).start();
    chatprocess = new ChatProcess();
    new Thread(chatprocess).start();
    itemManager = new ItemManager();
    new DoorHandler();
    //new Thread(new VotingIncentiveManager()).start();

    JobScheduler.ScheduleStaticRepeatForeverJob(600, PlayerProcessor.class);
    JobScheduler.ScheduleStaticRepeatForeverJob(600, ItemProcessor.class);
    JobScheduler.ScheduleStaticRepeatForeverJob(600, ShopProcessor.class);
    JobScheduler.ScheduleStaticRepeatForeverJob(600, GroundItemProcessor.class);

    npcManager = new NpcManager();
    new Thread(EventManager.getInstance()).start();
    new Thread(npcManager).start();
    clientHandler = new Server();
    (new Thread(clientHandler)).start(); // launch server listener
    System.gc();
    excludedBeardItems.add(6109);
    excludedBeardItems.add(1149);
    System.out.println("done!");
  }

  public static Server clientHandler = null; // handles all the clients
  public static java.net.ServerSocket clientListener = null;
  public static boolean shutdownServer = false; // set this to true in order to
                                                // shut down and kill the server
  public static boolean shutdownClientHandler; // signals ClientHandler to shut
                                               // down
  public static int serverlistenerPort = Config.getPort(); // 43594=default 8888
  public static PlayerHandler playerHandler = null;
  public static Thieving thieving = null;
  public static ShopHandler shopHandler = null;
  public static boolean antiddos = false;
  
  public void run() {
	    // setup the listener
	    try {
	      shutdownClientHandler = false;
	      clientListener = new java.net.ServerSocket(serverlistenerPort, 1, null);
	      while (true) {
	        try {
	          if (clientListener == null)
	            continue;
	          java.net.Socket s = clientListener.accept();
	          if (s == null)
	            continue;
	          s.setTcpNoDelay(true);
	          String connectingHost = "" + s.getRemoteSocketAddress();
	          connectingHost = connectingHost.substring(1, connectingHost.indexOf(":"));
	          if (!ConnectionList.getInstance().filter(s.getInetAddress())) {
	            Utils.println(
	                "ClientHandler: Rejected " + connectingHost + ":" + s.getPort() + "(n=" + nullConnections + ")");
	            s.close();
	          } else {
	        	  ConnectionList.getInstance().addConnection(s.getInetAddress());
	        	  tempConns.remove(connectingHost);
	        	  connections.add(connectingHost);
	            if (checkHost(connectingHost)) {
	              nullConnections++;
	              playerHandler.newPlayerClient(s, connectingHost);
	            } else {
	              s.close();
	            }
	          }
	          Thread.sleep(delay);
	        } catch (Exception e) {
	          e.printStackTrace();
	        }
	      }
	    } catch (java.io.IOException ioe) {
	      if (!shutdownClientHandler) {
	        Utils.println("Server is already in use.");
	      } else {
	        Utils.println("ClientHandler was shut down.");
	      }
	    }
	  }

  public static void logError(String message) {
    Utils.println(message);
  }

  public boolean checkHost(String host) {
    for (String h : banned) {
      if (h.equals(host))
        return false;
    }
    int num = 0;
    for (String h : connections) {
      if (host.equals(h)) {
        num++;
      }
    }
    if (num > 5) {
      banHost(host, num);
      return false;
    }
    return true;
  }

  public void banHost(String host, int num) {
    try {
      Utils.println("BANNING HOST " + host + " (flooding)");
      banned.add(host);
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("Error banning host " + host);
    }
  }

  public static void loadObjects() {
    try {
      Statement statement = Database.conn.createStatement();
      ResultSet results = statement.executeQuery("SELECT * from uber3_objects");
      while (results.next()) {
        objects.add(new RS2Object(results.getInt("id"), results.getInt("x"), results.getInt("y"), results.getInt("type")));
      }
      statement.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static int EnergyRegian = 60;

}
