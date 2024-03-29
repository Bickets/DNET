package com.dodian.uber.game.model.entity.player;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

import com.dodian.uber.comm.LoginManager;
import com.dodian.uber.game.Constants;
import com.dodian.uber.game.Server;
import com.dodian.uber.game.model.Position;
import com.dodian.uber.game.model.UpdateFlag;
import com.dodian.uber.game.model.WalkToTask;
import com.dodian.uber.game.model.entity.Entity;
import com.dodian.uber.game.model.entity.npc.Npc;
import com.dodian.uber.game.model.music.RegionSong;
import com.dodian.uber.game.model.player.skills.Skill;
import com.dodian.uber.game.model.player.skills.prayer.Prayers;
import com.dodian.utilities.Stream;
import com.dodian.utilities.Utils;

public abstract class Player extends Entity {
  public boolean yellOn = true;
  public boolean saving = false;
  public long disconnectAt = 0, longName = 0;
  public int wildyLevel = 0;
  public long lastAction = 0;
  public long lastPickAction = 0;
  public long lastTeleport = 0;
  private int playerNpc = 0;
  public int dbId = -1, violations = 0;
  public boolean premium = false,
		  randomed = false;
  public int playerGroup = 3;
  public long lastPacket = 0;
  public int[] playerLooks = new int[18];
  public boolean saveNeeded = true, lookNeeded = false;
  private boolean inCombat = false;
  private long lastCombat = 0;
  public long lastPlayerCombat = 0;
  public static int id = -1;// dbId = -1; //mysql userid
  public static int localId = -1;
  public int[] killers = new int[Constants.maxPlayers];
  public boolean busy = false;
  public boolean invis = false;
  public String[] boss_name = { "Dad", "Black_Knight_Titan", "San_Tojalon", "Nechryael", "Ice_Queen", "Ungadulu",
      "Abyssal_Guardian", "Head_Mourners", "King_Black_Dragon", "Evil_Chicken", "Black_Demon", "Jad" };
  public int[] boss_amount = new int[boss_name.length];
  // dueling
  public int duelStatus = -1; // 0 = Requesting duel, 1 = in duel screen, 2 =
  // waiting for other player to accept, 3 = in
  // duel, 4 = won
  public int duelChatTimer = -1;
  public boolean startDuel = false;
  public String forcedChat = "";
  private int headIcon = -1;
  private int skullIcon = -1;
  private WalkToTask walkToTask;
  public boolean IsPMLoaded = false;
  public int playerIsMember;
  public int playerEnergy;
  public int playerEnergyGian;
  private int playerLook[] = new int[6];
  public int playerBonus[] = new int[12];
  public int FightType = 1;
  public int playerMaxHit = 0;
  private int playerSE = 0x328; // SE = Standard Emotion
  private int playerSEW = 0x333; // SEW = Standard Emotion Walking
  private int playerSER = 0x338; // SER = Standard Emotion Run
  public boolean IsCutting = false;
  public boolean isFiremaking = false;
  public boolean IsAttacking = false, attackingNpc = false;
  public int attacknpc = -1;
  public int Essence;
  public boolean IsShopping = false;
  public int MyShopID = 0;
  public boolean UpdateShop = false;
  public int NpcDialogue = 0;
  public int NpcTalkTo = 0;
  public boolean NpcDialogueSend = false;
  public int NpcWanneTalk = 0;
  public boolean IsBanking = false;
  public boolean debug = false;
  private boolean crit;
  private boolean isNpc;
  public boolean initialized = false, disconnected = false;
  public boolean isActive = false;
  public boolean isKicked = false;
  public int actionTimer = 0;
  public int actionAmount = 0;
  public String connectedFrom = "";
  public static String connectedMac = "";
  public static String connectedISP = "";
  public String UUID = "";
  public static InetAddress connectedLocal;
  public String globalMessage = "";
  public boolean takeAsNote = false;
  private String playerName = null; // name of the connecting client
  public String playerPass = null; // name of the connecting client
  public int playerRights; // 0=normal player, 1=player mod, 2=real mod,
  public PlayerHandler handler = null;
  public int maxItemAmount = 2147483647;
  public int[] playerItems = new int[28];
  public int[] playerItemsN = new int[28];
  public int playerBankSize = 350;
  public int[] bankItems = new int[800];
  public int[] bankItemsN = new int[800];
  private int pGender;
  public int pHairC;
  public int pTorsoC;
  public int pLegsC;
  public int pFeetC;
  public int pSkinC;
  private int pHead;
  private int pTorso;
  private int pArms;
  private int pHands;
  private int pLegs;
  private int pFeet;
  private int pBeard;
  private int[] playerEquipment = new int[14];
  private int[] playerEquipmentN = new int[14];
  private int[] playerLevel = new int[21];
  private int[] playerXP = new int[21];
  private int currentHealth = getLevel(Skill.HITPOINTS);
  public int maxHealth = getLevel(Skill.HITPOINTS);
  public final static int maxPlayerListSize = Constants.maxPlayers;
  public Player playerList[] = new Player[maxPlayerListSize]; // To remove -Dashboard
  public int playerListSize = 0;
  public ArrayList<Player> playersUpdating = new ArrayList<Player>();
  private final Set<Npc> localNpcs = new LinkedHashSet<>(255);
  public boolean loaded = false;
  private boolean songUnlocked[] = new boolean[RegionSong.values().length];
  private int faceNPC = -1;
  public int newWalkCmdX[] = new int[WALKING_QUEUE_SIZE];
  public int newWalkCmdY[] = new int[WALKING_QUEUE_SIZE];
  public int tmpNWCX[] = new int[WALKING_QUEUE_SIZE];
  public int tmpNWCY[] = new int[WALKING_QUEUE_SIZE];
  public int newWalkCmdSteps = 0;
  public boolean newWalkCmdIsRunning = false;
  public int travelBackX[] = new int[WALKING_QUEUE_SIZE];
  public int travelBackY[] = new int[WALKING_QUEUE_SIZE];
  public int numTravelBackSteps = 0;
  private int graphicId = 0;
  private int graphicHeight = 0;
  public int m4001 = 0;
  public int m4002 = 0;
  public int m4003 = 0;
  public int m4004 = 0;
  public int m4005 = 0;
  public int m4006 = 0;
  public int m4007 = 0;
  

  public Player(int slot) {
    super(new Position(-1, -1, 0), slot, Entity.Type.PLAYER);
    playerRights = 0; // player rights
    lastPacket = System.currentTimeMillis();
    for (int i = 0; i < playerItems.length; i++) { // Setting player items
      playerItems[i] = 0;
    }
    for (int i = 0; i < playerItemsN.length; i++) { // Setting Item amounts
      playerItemsN[i] = 0;
    }

    for (int i = 0; i < playerLevel.length; i++) { // Setting Levels
      if (i == 3) {
        playerLevel[i] = 10;
        playerXP[i] = 1155;
      } else {
        playerLevel[i] = 1;
        playerXP[i] = 0;
      }
    }

    for (int i = 0; i < playerBankSize; i++) { // Setting bank items
      bankItems[i] = 0;
    }

    for (int i = 0; i < playerBankSize; i++) { // Setting bank item amounts
      bankItemsN[i] = 0;
    }

    playerIsMember = 1;
    //songUnlocked[RegionSong.THE_LONG_JOURNEY_HOME.getSongId()] = true;

    // Setting player standard look
    playerLook[0] = 2;
    playerLook[1] = 6;
    playerLook[2] = 7;
    playerLook[3] = 10;
    playerLook[4] = 5;
    playerLook[5] = 0;

    // Giving the player an unique look

    /*
     * 0-9: male head 10-17: male beard 18-25: male torso 26-32: male arms
     * 33-35: male hands 36-41: male legs 42-44: male feet
     * 
     * 45-55: fem head 56-60: fem torso 61-66: fem arms 67-69: fem hands 70-78:
     * fem legs 79-81: fem feet
     */

    pHead = 7;
    pTorso = 25;
    pArms = 29;
    pHands = 35;
    pLegs = 39;
    pFeet = 44;
    pBeard = 14;

    // the first call to updateThisPlayerMovement() will craft the proper
    // initialization packet
    teleportToX = 2611;// 3072;
    teleportToY = 3093;// 3312;

    mapRegionX = mapRegionY = -1;
    currentX = currentY = 0;
    resetWalkingQueue();
  }

  void destruct() {
    playerListSize = 0;
    for (int i = 0; i < maxPlayerListSize; i++)
      playerList[i] = null;
    if (PlayerHandler.playersOnline.containsKey(longName)) {
      PlayerHandler.playersOnline.remove(longName);
      PlayerHandler.allOnline.remove(longName);
    }
    getPosition().moveTo(-1, -1);
    mapRegionX = mapRegionY = -1;
    currentX = currentY = 0;
    resetWalkingQueue();
  }
  
  public abstract void initialize();

  public abstract void update();

  public void receieveDamage(int id, int amount, boolean crit) {
    if (getDamage().containsKey(id)) {
      getDamage().put(id, getDamage().get(id) + amount);
    } else {
      getDamage().put(id, amount);
    }
    dealDamage(amount, crit);
  }

  public void println_debug(String str) {
    System.out.println("[player-" + getSlot() + "]: " + str);
  }

  public void println(String str) {
    System.out.println("[player-" + getSlot() + "]: " + str);
  }

  public String getSongUnlockedSaveText() {
    String out = "";
    for (int i = 0; i < songUnlocked.length; i++) {
      out += (songUnlocked[i] ? 1 : 0) + " ";
    }
    return out;
  }

  public boolean withinDistance(Player otherPlr) {
    if (!otherPlr.isActive)
      return false;
    if (getPosition().getZ() != otherPlr.getPosition().getZ())
      return false;
    int deltaX = otherPlr.getPosition().getX() - getPosition().getX(),
        deltaY = otherPlr.getPosition().getY() - getPosition().getY();
    return deltaX <= 15 && deltaX >= -16 && deltaY <= 15 && deltaY >= -16;
  }

  public boolean withinDistance(Npc npc) {
    if (getPosition().getZ() != npc.getPosition().getZ())
      return false;
    if (!npc.visible)
      return false;
    int deltaX = npc.getPosition().getX() - getPosition().getX(),
        deltaY = npc.getPosition().getY() - getPosition().getY();
    return deltaX <= 15 && deltaX >= -16 && deltaY <= 15 && deltaY >= -16;
  }

  /**
   * @return The username of the player.
   */
  public String getPlayerName() {
    return this.playerName;
  }

  public void setPlayerName(String playerName) {
    this.playerName = playerName;
  }

  public WalkToTask getWalkToTask() {
    return this.walkToTask;
  }

  public void setWalkToTask(WalkToTask walkToTask) {
    this.walkToTask = walkToTask;
  }

  public int mapRegionX, mapRegionY; // the map region the player is
  // currently in
  private int currentX, currentY; // relative x/y coordinates (to map region)
  // Note that mapRegionX*8+currentX yields absX

  public static final int WALKING_QUEUE_SIZE = 50;
  public int walkingQueueX[] = new int[WALKING_QUEUE_SIZE], walkingQueueY[] = new int[WALKING_QUEUE_SIZE];
  public int wQueueReadPtr = 0; // points to slot for reading from queue
  public int wQueueWritePtr = 0; // points to (first free) slot for writing
  public boolean isRunning = false;
  public int teleportToX = -1, teleportToY = -1; // contain absolute x/y

  public void resetWalkingQueue() {
    wQueueReadPtr = wQueueWritePtr = 0;
    // properly initialize this to make the "travel back" algorithm work
    for (int i = 0; i < WALKING_QUEUE_SIZE; i++) {
      walkingQueueX[i] = currentX;
      walkingQueueY[i] = currentY;
    }
  }

  public void addToWalkingQueue(int x, int y) {
    int next = (wQueueWritePtr + 1) % WALKING_QUEUE_SIZE;
    if (next == wQueueWritePtr)
      return; // walking queue full, silently discard the data
    walkingQueueX[wQueueWritePtr] = x;
    walkingQueueY[wQueueWritePtr] = y;
    wQueueWritePtr = next;
  }

  // returns 0-7 for next walking direction or -1, if we're not moving
  public int getNextWalkingDirection() {
    if (wQueueReadPtr == wQueueWritePtr)
      return -1; // walking queue empty
    int dir;
    do {
      dir = Utils.direction(currentX, currentY, walkingQueueX[wQueueReadPtr], walkingQueueY[wQueueReadPtr]);
      if (dir == -1)
        wQueueReadPtr = (wQueueReadPtr + 1) % WALKING_QUEUE_SIZE;
      else if ((dir & 1) != 0) {
        println_debug("Invalid waypoint in walking queue!");
        resetWalkingQueue();
        return -1;
      }
    } while (dir == -1 && wQueueReadPtr != wQueueWritePtr);
    if (dir == -1)
      return -1;
    dir >>= 1;
    currentX += Utils.directionDeltaX[dir];
    currentY += Utils.directionDeltaY[dir];
    getPosition().moveTo(getPosition().getX() + Utils.directionDeltaX[dir],
        getPosition().getY() + Utils.directionDeltaY[dir]);
    return dir;
  }

  // calculates directions of player movement, or the new coordinates when
  // teleporting
  private boolean didTeleport = false; // set to true if char did teleport in
  // this cycle
  private boolean mapRegionDidChange = false;
  private int primaryDirection = -1, secondaryDirection = -1; // direction char is going in this cycle

  public void getNextPlayerMovement() {
    Client temp = (Client) this;
    mapRegionDidChange = false;
    didTeleport = false;
    primaryDirection = secondaryDirection = -1;

    if (teleportToX != -1 && teleportToY != -1) {
      mapRegionDidChange = true;
      if (mapRegionX != -1 && mapRegionY != -1) {
        // check, whether destination is within current map region
        int relX = teleportToX - mapRegionX * 8, relY = teleportToY - mapRegionY * 8;
        if (relX >= 2 * 8 && relX < 11 * 8 && relY >= 2 * 8 && relY < 11 * 8)
          mapRegionDidChange = false;
      }
      if (mapRegionDidChange) {
        // after map region change the relative coordinates range
        // between 48 - 55
        mapRegionX = (teleportToX >> 3) - 6;
        mapRegionY = (teleportToY >> 3) - 6;

        // playerListSize = 0; // completely rebuild playerList after
        // teleport AND map region change
      }

      currentX = teleportToX - 8 * mapRegionX;
      currentY = teleportToY - 8 * mapRegionY;
      getPosition().moveTo(teleportToX, teleportToY);
      resetWalkingQueue();
      teleportToX = teleportToY = -1;
      didTeleport = true;
    } else {
       primaryDirection = getNextWalkingDirection();
      if (primaryDirection == -1)
        return; // standing
      if (isRunning && !temp.UsingAgility2) {
         secondaryDirection = getNextWalkingDirection();
      }

      // check, if we're required to change the map region
      int deltaX = 0, deltaY = 0;
      if (currentX < 2 * 8) {
        deltaX = 4 * 8;
        mapRegionX -= 4;
        mapRegionDidChange = true;
      } else if (currentX >= 11 * 8) {
        deltaX = -4 * 8;
        mapRegionX += 4;
        mapRegionDidChange = true;
      }
      if (currentY < 2 * 8) {
        deltaY = 4 * 8;
        mapRegionY -= 4;
        mapRegionDidChange = true;
      } else if (currentY >= 11 * 8) {
        deltaY = -4 * 8;
        mapRegionY += 4;
        mapRegionDidChange = true;
      }

      if (mapRegionDidChange) {
        // have to adjust all relative coordinates
        currentX += deltaX;
        currentY += deltaY;
        for (int i = 0; i < WALKING_QUEUE_SIZE; i++) {
          walkingQueueX[i] += deltaX;
          walkingQueueY[i] += deltaY;
        }
      }

    }
  }

  public int getLevel(Skill skill) {
    return playerLevel[skill.getId()];
  }

  public int getExperience(Skill skill) {
    return playerXP[skill.getId()];
  }

  public void addExperience(int experience, Skill skill) {
    playerXP[skill.getId()] += experience;
  }

  public void setLevel(int level, Skill skill) {
    playerLevel[skill.getId()] = level;
  }

  public void setExperience(int experience, Skill skill) {
    playerXP[skill.getId()] = experience;
  }

  // handles anything related to character position basically walking, running
  // and standing
  // applies to only to "non-thisPlayer" characters
  public void updatePlayerMovement(Stream str) {
    if (primaryDirection == -1) {
      // don't have to update the character position, because the char is
      // just standing
      if (getUpdateFlags().isUpdateRequired()) {
        // tell client there's an update block appended at the end
        str.writeBits(1, 1);
        str.writeBits(2, 0);
      } else
        str.writeBits(1, 0);
    } else if (secondaryDirection == -1) {
      // send "walking packet"
      str.writeBits(1, 1);
      str.writeBits(2, 1);
      str.writeBits(3, Utils.xlateDirectionToClient[primaryDirection]);
      str.writeBits(1, getUpdateFlags().isUpdateRequired() ? 1 : 0);
    } else {
      // send "running packet"
      str.writeBits(1, 1);
      str.writeBits(2, 2);
      str.writeBits(3, Utils.xlateDirectionToClient[primaryDirection]);
      str.writeBits(3, Utils.xlateDirectionToClient[secondaryDirection]);
      str.writeBits(1, getUpdateFlags().isUpdateRequired() ? 1 : 0);
    }

  }

  public void addNewPlayer(Player plr, Stream str, Stream updateBlock) {
    int id = plr.getSlot();
    playerList[playerListSize++] = plr;
    playersUpdating.add(plr);
    str.writeBits(11, id);
    str.writeBits(1, 1);// Requires update?
    boolean savedFlag = plr.getUpdateFlags().isRequired(UpdateFlag.APPEARANCE);
    plr.getUpdateFlags().setRequired(UpdateFlag.APPEARANCE, true);
    PlayerUpdating.getInstance().appendBlockUpdate(plr, updateBlock);
    plr.getUpdateFlags().setRequired(UpdateFlag.APPEARANCE, savedFlag);
    str.writeBits(1, 1); // set to true, if we want to discard the
    // (clientside) walking queue
    // no idea what this might be useful for yet
    int z = plr.getPosition().getY() - getPosition().getY();
    if (z < 0)
      z += 32;
    str.writeBits(5, z); // y coordinate relative to thisPlayer
    z = plr.getPosition().getX() - getPosition().getX();
    if (z < 0)
      z += 32;
    str.writeBits(5, z); // x coordinate relative to thisPlayer
  }

  private byte chatText[] = new byte[4096], chatTextSize = 0;
  private int chatTextEffects = 0, chatTextColor = 0;
  
  public byte[] getChatText() {
    return this.chatText;
  }
  
  public byte getChatTextSize() {
    return this.chatTextSize;
  }
  
  public void setChatTextSize(byte chatTextSize) {
    this.chatTextSize = chatTextSize;
  }
  
  public int getChatTextEffects() {
    return this.chatTextEffects;
  }
  
  public void setChatTextEffects(int chatTextEffects) {
    this.chatTextEffects = chatTextEffects;
  }
  
  public int getChatTextColor() {
    return this.chatTextColor;
  }
  
  public void setChatTextColor(int chatTextColor) {
    this.chatTextColor = chatTextColor;
  }

  public void clearUpdateFlags() {
    getUpdateFlags().clear();
    IsStair = false;
    faceNPC = 65535;

  }

  public void faceNPC(int index) {
    faceNPC = index;
    getUpdateFlags().setRequired(UpdateFlag.FACE_CHARACTER, true);
  }

  public void gfx0(int gfx) {
    graphicId = gfx;
    graphicHeight = 65536;
    getUpdateFlags().setRequired(UpdateFlag.GRAPHICS, true);
  }
  
  public int getFaceNpc() {
    return this.faceNPC;
  }
  
  public void setFaceNpc(int faceNpc) {
    this.faceNPC = faceNpc;
  }

  public void preProcessing() {
    // newWalkCmdSteps = 0;
  }

  // is being called regularily every 500ms - do any automatic player actions
  // herein
  public abstract boolean process();

  public abstract boolean packetProcess();

  public void postProcessing() {
    if (newWalkCmdSteps > 0) {
      int firstX = newWalkCmdX[0], firstY = newWalkCmdY[0]; // the point

      // travel backwards to find a proper connection vertex
      int lastDir = 0;
      boolean found = false;
      numTravelBackSteps = 0;
      int ptr = wQueueReadPtr;
      int dir = Utils.direction(currentX, currentY, firstX, firstY);
      if (dir != -1 && (dir & 1) != 0) {
        // we can't connect first and current directly
        do {
          lastDir = dir;
          if (--ptr < 0)
            ptr = WALKING_QUEUE_SIZE - 1;

          travelBackX[numTravelBackSteps] = walkingQueueX[ptr];
          travelBackY[numTravelBackSteps++] = walkingQueueY[ptr];
          dir = Utils.direction(walkingQueueX[ptr], walkingQueueY[ptr], firstX, firstY);
          if (lastDir != dir) {
            found = true;
            break; // either of those two, or a vertex between
            // those is a candidate
          }

        } while (ptr != wQueueWritePtr);
      } else
        found = true; // we didn't need to go back in time because the
      // current position
      // already can be connected to first

      if (!found) {
        println_debug("Fatal: couldn't find connection vertex! Dropping packet.");
        Client temp = (Client) this;
        temp.saveStats(true);
        disconnected = true;
      } else {
        wQueueWritePtr = wQueueReadPtr; // discard any yet unprocessed
        // waypoints from queue

        addToWalkingQueue(currentX, currentY); // have to add this in
        // order to keep
        // consistency in the
        // queue

        if (dir != -1 && (dir & 1) != 0) {

          for (int i = 0; i < numTravelBackSteps - 1; i++) {
            addToWalkingQueue(travelBackX[i], travelBackY[i]);
          }
          int wayPointX2 = travelBackX[numTravelBackSteps - 1], wayPointY2 = travelBackY[numTravelBackSteps - 1];
          int wayPointX1, wayPointY1;
          if (numTravelBackSteps == 1) {
            wayPointX1 = currentX;
            wayPointY1 = currentY;
          } else {
            wayPointX1 = travelBackX[numTravelBackSteps - 2];
            wayPointY1 = travelBackY[numTravelBackSteps - 2];
          }

          dir = Utils.direction(wayPointX1, wayPointY1, wayPointX2, wayPointY2);
          if (dir == -1 || (dir & 1) != 0) {
            println_debug("Fatal: The walking queue is corrupt! wp1=(" + wayPointX1 + ", " + wayPointY1 + "), "
                + "wp2=(" + wayPointX2 + ", " + wayPointY2 + ")");
          } else {
            dir >>= 1;
            found = false;
            int x = wayPointX1, y = wayPointY1;
            while (x != wayPointX2 || y != wayPointY2) {
              x += Utils.directionDeltaX[dir];
              y += Utils.directionDeltaY[dir];
              if ((Utils.direction(x, y, firstX, firstY) & 1) == 0) {
                found = true;
                break;
              }
            }
            if (!found) {
              println_debug("Fatal: Internal error: unable to determine connection vertex!" + "  wp1=(" + wayPointX1
                  + ", " + wayPointY1 + "), wp2=(" + wayPointX2 + ", " + wayPointY2 + "), " + "first=(" + firstX + ", "
                  + firstY + ")");
            } else
              addToWalkingQueue(wayPointX1, wayPointY1);
          }
        } else {
          for (int i = 0; i < numTravelBackSteps; i++) {
            addToWalkingQueue(travelBackX[i], travelBackY[i]);
          }
        }

        for (int i = 0; i < newWalkCmdSteps; i++) {
          addToWalkingQueue(newWalkCmdX[i], newWalkCmdY[i]);
        }

      }
      isRunning = (newWalkCmdIsRunning || buttonOnRun);
    }
    newWalkCmdSteps = 0;
  }

  public boolean buttonOnRun = true;

  public void kick() {
    isKicked = true;
  }

  private int hitDiff = 0;
  protected boolean IsStair = false;
  public int deathStage = 0;
  public long deathTimer = 0;

  public void appendMask400Update(Stream str) { // Xerozcheez: Something to
    str.writeByteA(m4001);
    str.writeByteA(m4002);
    str.writeByteA(m4003);
    str.writeByteA(m4004);
    str.writeWordA(m4005);
    str.writeWordBigEndianA(m4006);
    str.writeByteA(m4007); // direction
  }

  // PM Stuff
  public abstract void loadpm(long l, int world);

  public int Privatechat = 0;

  public abstract void sendpm(long name, int rights, byte[] chatmessage, int messagesize);

  public void dealDamage(int amt, boolean crit) {
    ((Client) this).debug("Dealing " + amt + " damage to you (hp=" + currentHealth + ")");
    currentHealth -= amt;
    hitDiff = amt;
    this.crit = crit;
    getUpdateFlags().setRequired(UpdateFlag.HIT, true);
    if (currentHealth <= 0) {
      ((Client) this).debug("Triggering death timer");
      currentHealth = 0;
      deathStage = 1;
      deathTimer = System.currentTimeMillis();
    }
  }

  public void sendAnimation(int id) {
    this.setAnimationId(id);
    getUpdateFlags().setRequired(UpdateFlag.ANIM, true);
  }

  public void teleportTo(int x, int y, int z) {
    teleportToX = x;
    teleportToY = y;
    super.moveTo(getPosition().getX(), getPosition().getY(), z);
    getUpdateFlags().setRequired(UpdateFlag.APPEARANCE, true);
  }

  Prayers prayers = new Prayers(this, (Client) this);
  
  public void setSkullIcon(int id) {
    skullIcon = id;
  }

  public boolean isSongUnlocked(int songId) {
    return this.songUnlocked[songId];
  }

  public boolean areAllSongsUnlocked() {
    for (boolean unlocked : songUnlocked) {
      if (!unlocked)
        return false;
    }
    return true;
  }

  public void setSongUnlocked(int songId, boolean unlocked) {
    this.songUnlocked[songId] = unlocked;
  }
  
  /**
   * Gets the hash collection of the local npcs.
   *
   * @return the local npcs.
   */
  public Set<Npc> getLocalNpcs() {
      return localNpcs;
  }
  
  public boolean didTeleport() {
    return this.didTeleport;
  }
  
  public boolean didMapRegionChange() {
    return this.mapRegionDidChange;
  }
  
  public int getPrimaryDirection() {
    return this.primaryDirection;
  }
  
  public int getSecondaryDirection() {
    return this.secondaryDirection;
  }
  
  public int getCurrentX() {
    return this.currentX;
  }
  
  public int getCurrentY() {
    return this.currentY;
  }
  
  public void setGraphic(int graphicId, int graphicHeight) {
   this.graphicId = graphicId;
   this.graphicHeight = graphicHeight;
  }
  
  public int getGraphicId() {
    return this.graphicId;
  }
  
  public int getGraphicHeight() {
    return this.graphicHeight;
  }
  
  public String getForcedChat() {
    return this.forcedChat;
  }
  
  public int[] getEquipment() {
    return this.playerEquipment;
  }
  
  public int[] getEquipmentN() {
    return this.playerEquipmentN;
  }
  
  public int getGender() {
    return this.pGender;
  }
  
  public void setGender(int pGender) {
    this.pGender = pGender;
  }
  
  public int getTorso() {
    return this.pTorso;
  }
  
  public void setTorso(int pTorso) {
    this.pTorso = pTorso;
  }
  
  public int getArms() {
    return this.pArms;
  }
  
  public void setArms(int pArms) {
    this.pArms = pArms;
  }
  
  public int getLegs() {
    return this.pLegs;
  }
  
  public void setLegs(int pLegs) {
    this.pLegs = pLegs;
  }
  
  public int getHands() {
    return this.pHands;
  }
  
  public void setHands(int pHands) {
    this.pHands = pHands;
  }
  
  public int getFeet() {
    return this.pFeet;
  }
  
  public void setFeet(int pFeet) {
    this.pFeet = pFeet;
  }
  
  public int getBeard() {
    return this.pBeard;
  }
  
  public void setBeard(int pBeard) {
    this.pBeard = pBeard;
  }
  
  public int getHead() {
    return this.pHead;
  }
  
  public void setHead(int pHead) {
    this.pHead = pHead;
  }
  
  public int[] getPlayerLook() {
    return this.playerLook;
  }
  
  public int getHeadIcon() {
    return this.headIcon;
  }
  
  public void setHeadIcon(int headIcon) {
    this.headIcon = headIcon;
  }
  
  public int getSkullIcon() {
    return this.skullIcon;
  }
  
  public int getStandAnim() {
    return this.playerSE;
  }
  
  public void setStandAnim(int playerSE) {
    this.playerSE = playerSE;
  }
  
  public int getWalkAnim() {
    return this.playerSEW;
  }
  
  public void setWalkAnim(int playerSEW) {
    this.playerSEW = playerSEW;
  }
  
  public int getRunAnim() {
    return this.playerSER;
  }
  
  public void setRunAnim(int playerSER) {
    this.playerSER = playerSER;
  }
  
  public boolean isNpc() {
    return this.isNpc;
  }
  
  public void setNpcMode(boolean isNpc) {
    this.isNpc = isNpc;
  }
  
  public int getPlayerNpc() {
    return this.playerNpc;
  }
  
  public void setPlayerNpc(int playerNpc) {
    this.playerNpc = playerNpc;
  }
  
  public int getHitDiff() {
    return this.hitDiff;
  }
  
  public void setHitDiff(int hitDiff) {
    this.hitDiff = hitDiff;
  }
  
  public boolean isCrit() {
    return this.crit;
  }
  
  public int getCurrentHealth() {
    return this.currentHealth;
  }
  
  public void setCurrentHealth(int currentHealth) {
    this.currentHealth = currentHealth;
  }
  
  public int getMaxHealth() {
    return this.maxHealth;
  }
  
  public void setCrit(boolean crit) {
    this.crit = crit;
  }
  
  public boolean isInCombat() {
    return this.inCombat;
  }
  
  public void setInCombat(boolean inCombat) {
    this.inCombat = inCombat;
  }
  
  public long getLastCombat() {
    return this.lastCombat;
  }
  
  public void setLastCombat(long lastCombat) {
    this.lastCombat = lastCombat;
  }
  
  /**
   * Calculates and returns the combat level for this player.
   *
   * @return the combat level.
   */
  public int determineCombatLevel() {
      int magLvl = getLevel(Skill.MAGIC);
      int ranLvl = getLevel(Skill.RANGED);
      int attLvl = getLevel(Skill.ATTACK);
      int strLvl = getLevel(Skill.STRENGTH);
      int defLvl = getLevel(Skill.DEFENCE);
      int hitLvl = getLevel(Skill.HITPOINTS);
      int prayLvl = getLevel(Skill.PRAYER);
      double mag = magLvl * 1.5;
      double ran = ranLvl * 1.5;
      double attstr = attLvl + strLvl;
      double combatLevel = 0;
      if (ran > attstr && ran > mag) { // player is ranged class
          combatLevel = ((defLvl) * 0.25) + ((hitLvl) * 0.25) + ((prayLvl / 2) * 0.25) + ((ranLvl) * 0.4875);
      } else if (mag > attstr) { // player is mage class
          combatLevel = (((defLvl) * 0.25) + ((hitLvl) * 0.25) + ((prayLvl / 2) * 0.25) + ((magLvl) * 0.4875));
      } else {
          combatLevel = (((defLvl) * 0.25) + ((hitLvl) * 0.25) + ((prayLvl / 2) * 0.25) + ((attLvl) * 0.325) + ((strLvl) * 0.325));
      }
      return (int) combatLevel;
  }
  
  public LoginManager getLoginManager() {
	  return Server.loginManager;
  }

}