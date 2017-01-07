package com.dodian.uber.game.model.entity.player;

import com.dodian.Config;
import com.dodian.uber.comm.ConnectionList;
import com.dodian.uber.comm.LoginManager;
import com.dodian.uber.game.Constants;
import com.dodian.uber.game.event.Event;
import com.dodian.uber.game.event.EventManager;
import com.dodian.uber.game.model.ChatLine;
import com.dodian.uber.game.model.Login;
import com.dodian.uber.game.model.ShopHandler;
import com.dodian.uber.game.model.UpdateFlag;
import com.dodian.uber.game.model.combat.impl.CombatStyleHandler;
import com.dodian.uber.game.model.entity.npc.Npc;
import com.dodian.uber.game.model.entity.npc.NpcUpdating;
import com.dodian.uber.game.model.item.*;
import com.dodian.uber.game.model.object.DoorHandler;
import com.dodian.uber.game.model.object.RS2Object;
import com.dodian.uber.game.model.player.content.Skillcape;
import com.dodian.uber.game.model.player.packets.OutgoingPacket;
import com.dodian.uber.game.model.player.packets.PacketHandler;
import com.dodian.uber.game.model.player.packets.outgoing.Frame171;
import com.dodian.uber.game.model.player.packets.outgoing.RemoveInterfaces;
import com.dodian.uber.game.model.player.packets.outgoing.InventoryInterface;
import com.dodian.uber.game.model.player.packets.outgoing.SendMessage;
import com.dodian.uber.game.model.player.packets.outgoing.NpcDialogueHead;
import com.dodian.uber.game.model.player.packets.outgoing.SendString;
import com.dodian.uber.game.model.player.packets.outgoing.Sound;
import com.dodian.uber.game.model.player.quests.QuestSend;
import com.dodian.uber.game.model.player.skills.Skill;
import com.dodian.uber.game.model.player.skills.Skills;
import com.dodian.uber.game.model.player.skills.fletching.Fletching;
import com.dodian.uber.game.model.player.skills.prayer.Prayers;
import com.dodian.uber.game.model.player.skills.slayer.SlayerTask;
import com.dodian.uber.game.security.DropLog;
import com.dodian.uber.game.security.DuelLog;
import com.dodian.uber.game.security.PmLog;
import com.dodian.uber.game.Server;
import com.dodian.utilities.*;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import org.apache.mina.core.session.IoSession;

public class Client extends Player implements Runnable {

  public Fletching fletching = new Fletching();	
  public boolean immune = false, loggingOut = false, loadingDone = false;
  public boolean canPreformAction = true;
  long lastBar = 0;
  public long lastSave, snaredUntil = 0;
  public boolean checkTime = false;
  public Npc selectedNpc = null;
  int otherdbId = -1;
  public int convoId = -1, nextDiag = -1, npcFace = 591, taskId = -1, taskAmt = 0, taskTotal = 0;
  public boolean pLoaded = false;
  public int maxQuests = QuestSend.values().length;
  public int Quests[] = new int[maxQuests];
  public String failer = "";
  Date now = new Date();
  public long mutedHours;
  public long mutedTill;
  public long rightNow = now.getTime();
  public boolean mining = false;
  public boolean stringing = false;
  public boolean filling = false;
  public int mineIndex = 0, minePick = 0;
  public double attackPot = 0.0, defensePot = 0.0, strengthPot = 0.0, rangePot = 0.0;
  public long potionUpdate = 0, lastDoor = 0;
  public int clientPid = -1;
  public long session_start = 0;
  public boolean pickupWanted = false, duelWin = false;
  public int pickX, pickY, pickId, pickTries;
  public long lastYell = 0;
  public CopyOnWriteArrayList<Friend> friends = new CopyOnWriteArrayList<Friend>();
  public CopyOnWriteArrayList<Friend> ignores = new CopyOnWriteArrayList<Friend>();
  public int currentButton = 0, currentStatus = 0;
  public boolean spamButton = false, tradeLocked = false;
  /*
   * Danno: Last row all disabled. As none have effect.
   */
  public int[] duelButtons = { 26069, 26070, 26071, 30136,
      2158 /*
            * , 26065, 26072, 26073, 26074, 26066, 26076
            */ };
  public String[] duelNames = { "No Ranged", "No Melee", "No Magic", "No Gear Change", "Fun Weapons", "No Retreat",
      "No Drinks", "No Food", "No prayer", "No Movement", "Obstacles" };
  /*
   * Danno: Last row all disabled. As none have effect.
   */
  public boolean[] duelRule = { false, false, false, false, false, true, true, true, true, true, true };

  /*
   * Danno: Testing for armor restriction rules.
   */
  private boolean duelBodyRules[] = new boolean[11];

  private int[] trueSlots = { 0, 1, 2, 13, 3, 4, 5, 7, 12, 10, 9 };
  private int[] falseSlots = { 0, 1, 2, 4, 5, 6, -1, 7, -1, 10, 9, -1, 9, 3 };
  private int[] stakeConfigId = new int[23];
  public int[] duelLine = { 6698, 6699, 6697, 7817, 669, 6696, 6701, 6702, 6703, 6704, 6731 };
  public boolean duelRequested = false, inDuel = false, duelConfirmed = false, duelConfirmed2 = false,
      duelFight = false;
  public int duel_with = 0;
  public boolean tradeRequested = false, inTrade = false, canOffer = true, tradeConfirmed = false,
      tradeConfirmed2 = false, tradeResetNeeded = false;
  public int trade_reqId = 0;
  public CopyOnWriteArrayList<GameItem> offeredItems = new CopyOnWriteArrayList<GameItem>();
  public CopyOnWriteArrayList<GameItem> otherOfferedItems = new CopyOnWriteArrayList<GameItem>();
  public boolean adding = false;
  public ArrayList<RS2Object> objects = new ArrayList<RS2Object>();
  public String[] lastMessage = new String[3];
  public long animationReset = 0, lastButton = 0;
  public int cookAmount = 0, cookIndex = 0, enterAmountId = 0;
  public boolean cooking = false;
  // Dodian: fishing
  int fishId, fishIndex;
  boolean fishing = false;
  // Dodian: teleports
  int tX = 0, tY = 0, tStage = 0, tH = 0, tEmote = 0;
  long tTime = 0;
  // Dodian: crafting
  boolean crafting = false;
  int cItem = -1;
  int cAmount = 0;
  int cLevel = 1;
  int cExp = 0;
  public int cSelected = -1, cIndex = -1;;
  public String dMsg = "";

  public boolean spinning = false;
  public int dialogInterface = 2459;
	public boolean fletchings = false, fletchingOther = false;
	public int fletchId = -1, fletchAmount = -1, fletchLog = -1,
			originalW = -1, originalS = -1, fletchExp = 0;
	public int fletchOtherId1 = -1, fletchOtherId2 = -1, fletchOtherId3 = -1, 
			fletchOtherAmount = -1, fletchOtherAmt = -1, fletchOtherXp = -1;
	public long fletchOtherTime = 0;
  public boolean smelting = false;
  public int smelt_id, smeltCount, smeltExperience;

  public boolean shafting = false;
  public int random_skill = -1;
  public String[] otherGroups = new String[10];
  public int autocast_spellIndex = -1;
  public int loginDelay = 0;
  public boolean validClient = true, muted = false;
  public int newPms = 0;

  public int[] requiredLevel = { 1, 10, 25, 38, 50, 60, 62, 64, 66, 68, 70, 72, 74, 76, 78, 80, 82, 84, 86, 88, 90, 92,
      94, 96, 20, 50 };

  public int[] baseDamage = { 1, 2, 0, 3, 4, 0, 5, 6, 0, 7, 8, 0, 9, 10, 0, 11, 12, 0, 13, 14, 0, 15, 16, 0, 0, 0 };
  public String[] spellName = { "Smoke Rush", "Shadow Rush", "", "Blood Rush", "Ice Rush", "", "Smoke Burst",
      "Shadow Burst", "", "Blood Burst", "Ice Burst", "", "Smoke Blitz", "Shadow Blitz", "", "Blood Blitz", "Ice Blitz",
      "", "Smoke Barrage", "Shadow Barrage", "", "Blood Barrage", "Ice Barrage", "", "", "" };
  public int[] ancientId = { 12939, 12987, 0, 12901, 12861, 0, 12963, 13011, 0, 12919, 12881, 0, 12951, 12999, 0, 12911,
      12871, 0, 12975, 13023, 0, 12929, 12891, 0, 1572, 1582 };
  public int[] ancientType = { 0, 0, 1, 2, 3, 1, 0, 0, 1, 2, 3, 1, 0, 0, 1, 2, 3, 1, 0, 0, 1, 2, 3, 1, 4, 4 };
  public int[] ancientButton = { 51133, 51185, -1, 51091, 24018, -1, 51159, 51211, -1, 51111, 51069, -1, 51146, 51198,
      -1, 51102, 51058, -1, 51172, 51224, -1, 51122, 51080, -1, -1, -1 };
  public int[] noTrade = { 1543, 1544, 1545, 2382, 2383, 989 };
  public int[] coolDownGroup = { 2, 2, 1, 2, 3, 1, 2, 2, 1, 2, 3, 1, 2, 2, 1, 2, 3, 1, 2, 2, 1, 2, 3, 1, 4, 4 };
  public long[] coolDown = { 5000, 5000, 2500, 5000, 15000 };
  public int[] effects = new int[10];
  public String properName = "";
  public int actionButtonId = 0;
  public long lastAttack = 0;
  public long[] globalCooldown = new long[10];
  public boolean validLogin = false;
  private boolean[] gnomeCourse = new boolean[7];

  public void ReplaceObject2(int objectX, int objectY, int NewObjectID, int Face, int ObjectType) {
    /*
     * Danno: Fix. So these objects don't pop up in random places.
     */
    if (!withinDistance(new int[] { objectX, objectY, 60 }))
      return;

    getOutputStream().createFrame(85);
    getOutputStream().writeByteC(objectY - (mapRegionY * 8));
    getOutputStream().writeByteC(objectX - (mapRegionX * 8));

    getOutputStream().createFrame(101);
    getOutputStream().writeByteC((ObjectType << 2) + (Face & 3));
    getOutputStream().writeByte(0);

    if (NewObjectID != -1) {
      getOutputStream().createFrame(151);
      getOutputStream().writeByteS(0);
      getOutputStream().writeWordBigEndian(NewObjectID);
      getOutputStream().writeByteS((ObjectType << 2) + (Face & 3));
    }
  }

  /**
   * @param 0
   *          = X
   * @param 1
   *          = Y
   * @param 2
   *          = Distance allowed.
   * 
   */
  private boolean withinDistance(int[] o) {
    int dist = o[2];
    int deltaX = o[0] - getPosition().getX(), deltaY = o[1] - getPosition().getY();
    return (deltaX <= (dist - 1) && deltaX >= -dist && deltaY <= (dist - 1) && deltaY >= -dist);
  }

  public boolean wearing = false;

  public void CalculateRange() {
    double MaxHit = 0;
    int RangeBonus = playerBonus[3] / 2; // Range Bonus
    int Range = getLevel(Skill.RANGED); // Range
    {
      MaxHit += (double) (1.05 + (double) ((double) (RangeBonus * Range) * 0.00175));
    }
    MaxHit += (double) (Range * 0.2);
    playerMaxHit = (int) Math.floor(MaxHit);
  }

  public int resetanim = 8;

  public void refreshSkill(Skill skill) {
    try {
      int out = getLevel(skill);
      if (skill == Skill.HITPOINTS) {
        out = getCurrentHealth();
      } else if (skill == Skill.ATTACK) {
        out = (int) ((1 + (attackPot / 100)) * getLevel(skill));
      } else if (skill == Skill.DEFENCE) {
        out = (int) ((1 + (defensePot / 100)) * getLevel(skill));
      } else if (skill == Skill.STRENGTH) {
        out = (int) ((1 + (strengthPot / 100)) * getLevel(skill));
      } else if (skill == Skill.RANGED) {
        out = (int) ((1 + (rangePot / 100)) * getLevel(skill));
      }
      setSkillLevel(skill.getId(), out, getExperience(skill));
      getOutputStream().createFrame(134);
      getOutputStream().writeByte(skill.getId());
      getOutputStream().writeDWord_v1(getExperience(skill));
      if (skill == Skill.HITPOINTS)
        getOutputStream().writeByte(getCurrentHealth());
      else
        getOutputStream().writeByte(out);
      getUpdateFlags().setRequired(UpdateFlag.APPEARANCE, true);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public int getbattleTimer(int weapon) {
    String wep = GetItemName(weapon).toLowerCase();
    int wepPlainTime = 7;
    if (wep.contains("dart") || wep.contains("knife")) {
      wepPlainTime = 6;
    } else
      if (wep.contains("dagger")
          || wep.contains("sword") && !wep.contains("godsword") && !wep.contains("longsword")
              && !wep.contains("2h sword")
          || wep.contains("scimitar") || wep.contains("claws") || wep.contains("zamorakian spear")
          || wep.contains("toktz-xil-ak") || wep.contains("toktz-xil-ek") || wep.contains("zamorak staff")
          || wep.contains("saradomin staff") || wep.contains("guthix staff") || wep.contains("slayer staff")
          || wep.contains("rapier") || wep.contains("waraxe") || wep.contains("ancient staff")
          || wep.contains("new crystal bow") || wep.contains("shortbow") || wep.contains("hunter's crossbow")
          || wep.contains("karil's crossbow") || wep.contains("toktz-xil-ul")) {
      wepPlainTime = 6;
    } else if (wep.contains("longsword") || wep.contains("mace") || wep.contains("axe") && !wep.contains("dharok")
        || wep.contains("spear") || wep.contains("tzhaar-ket-em") || wep.contains("torag") || wep.contains("guthan")
        || wep.contains("verac") || wep.contains("staff") && !wep.contains("ahrim") || wep.contains("composite")
        || wep.contains("seercull") || wep.contains("thrownaxe")) {
      wepPlainTime = 5;
    } else if (wep.contains("battleaxe") || wep.contains("warhammer") || wep.contains("godsword")
        || wep.contains("barrelchest") || wep.contains("ahrim") || wep.contains("toktz-mej-tal")
        || wep.contains("longbow") || wep.contains("dorgeshuun") || wep.contains("crossbow")
        || wep.contains("hand cannon") || wep.contains("javelin")) {
      wepPlainTime = 4;
    } else if (wep.contains("2h sword") || wep.contains("halberd") || wep.contains("maul") || wep.contains("balmung")
        || wep.contains("tzhaar-ket-om") || wep.contains("dharok")) {
      wepPlainTime = 5;
    } else if (wep.contains("ogre bow")) {
      wepPlainTime = 2;
    } else {
      wepPlainTime = 6;
    }
    return (int) (6 - 0.6 * (wepPlainTime)) * 1000;

  }

  public void CheckGear() {
    for (int a = 0; a < staffs.length; a++) {
      if (getEquipment()[Equipment.Slot.WEAPON.getId()] != staffs[a]) {
        autocast_spellIndex = -1;
      }
    }
    checkBow();
    getbattleTimer(getEquipment()[Equipment.Slot.WEAPON.getId()]);
  }

  public int distanceToPoint(int pointX, int pointY) {
    return (int) Math.sqrt(Math.pow(getPosition().getX() - pointX, 2) + Math.pow(getPosition().getY() - pointY, 2));
  }

  public void animation(int id, int Y, int X) {
    for (int i = 0; i < PlayerHandler.players.length; i++) {
      Player p = PlayerHandler.players[i];
      if (p != null) {
        Client person = (Client) p;

        if ((person.getPlayerName() != null || person.getPlayerName() != "null")) {
          if (person.distanceToPoint(X, Y) <= 60) {
            person.animation2(id, Y, X);
          }
        }
      }
    }
  }

  public void animation2(int id, int Y, int X) {
    getOutputStream().createFrame(85);
    getOutputStream().writeByteC(Y - (mapRegionY * 8));
    getOutputStream().writeByteC(X - (mapRegionX * 8));
    getOutputStream().createFrame(4);
    getOutputStream().writeByte(0);
    getOutputStream().writeWord(id); 
    getOutputStream().writeByte(0); 
    getOutputStream().writeWord(0);
  }

  public void stillgfx(int id, int Y, int X, int height, int time) {
    // for (Player p : server.playerHandler.players) {
    for (int i = 0; i < PlayerHandler.players.length; i++) {
      Player p = PlayerHandler.players[i];
      if (p != null) {
        Client person = (Client) p;

        if ((person.getPlayerName() != null || person.getPlayerName() != "null") && person.dbId > 0) {
          if (person.distanceToPoint(X, Y) <= 60) {
            person.stillgfx2(id, Y, X, height, time);
          }
        }
      }
    }
  }

  public void stillgfx(int id, int y, int x) {
    stillgfx(id, y, x, 0, 0);
  }

  public void stillgfx2(int id, int Y, int X, int height, int time) {
    getOutputStream().createFrame(85);
    getOutputStream().writeByteC(Y - (mapRegionY * 8));
    getOutputStream().writeByteC(X - (mapRegionX * 8));
    getOutputStream().createFrame(4);
    getOutputStream().writeByte(0); // Tiles away (X >> 4 + Y & 7)
    getOutputStream().writeWord(id); // Graphic id
    getOutputStream().writeByte(height); // height of the spell above it's basic
    // place, i think it's written in pixels
    // 100 pixels higher
    getOutputStream().writeWord(time); // Time before casting the graphic
  }

  public boolean AnimationReset; // Resets Animations With The Use Of The
  // ActionTimer

  public void createProjectile(int casterY, int casterX, int offsetY, int offsetX, int angle, int speed, int gfxMoving,
      int startHeight, int endHeight, int MageAttackIndex) {
    try {
      getOutputStream().createFrame(85);
      getOutputStream().writeByteC((casterY - (mapRegionY * 8)) - 2);
      getOutputStream().writeByteC((casterX - (mapRegionX * 8)) - 3);
      getOutputStream().createFrame(117);
      getOutputStream().writeByte(angle); // Starting place of the projectile
      getOutputStream().writeByte(offsetY); // Distance between caster and enemy
      // Y
      getOutputStream().writeByte(offsetX); // Distance between caster and enemy
      // X
      getOutputStream().writeWord(MageAttackIndex); // The NPC the missle is
      // locked on to
      getOutputStream().writeWord(gfxMoving); // The moving graphic ID
      getOutputStream().writeByte(startHeight); // The starting height
      getOutputStream().writeByte(endHeight); // Destination height
      getOutputStream().writeWord(51); // Time the missle is created
      getOutputStream().writeWord(speed); // Speed minus the distance making it
      // set
      getOutputStream().writeByte(16); // Initial slope
      getOutputStream().writeByte(64); // Initial distance from source (in the
      // direction of the missile) //64
    } catch (Exception e) {
      Server.logError(e.getMessage());
    }
  }

  public void println_debug(String str) {
    System.out.println("[client-" + getSlot() + "-" + getPlayerName() + "]: " + str);
  }

  public void println(String str) {
    System.out.println("[client-" + getSlot() + "-" + getPlayerName() + "]: " + str);
  }

  public void rerequestAnim() {
    requestAnim(-1, 0);
    getUpdateFlags().setRequired(UpdateFlag.APPEARANCE, true);
  }

  public void sendFrame200(int MainFrame, int SubFrame) {
    getOutputStream().createFrame(200);
    getOutputStream().writeWord(MainFrame);
    getOutputStream().writeWord(SubFrame);
    flushOutStream();
  }
  
  public void sendFrame160(int objectX, int objectY, int tileObjectType, int orientation, int animationID) {
    getOutputStream().createFrame(85);
    getOutputStream().writeByteC(objectY - (mapRegionY * 8));
    getOutputStream().writeByteC(objectX - (mapRegionX * 8));
    getOutputStream().createFrame(160);
    getOutputStream().writeByteS(0);
    getOutputStream().writeByteS((tileObjectType << 2) + (orientation & 3));
    getOutputStream().writeWordA(animationID);
  }

  public void sendFrame164(int Frame) {
    getOutputStream().createFrame(164);
    getOutputStream().writeWordBigEndian_dup(Frame);
    flushOutStream();
  }

  public void sendFrame246(int MainFrame, int SubFrame, int SubFrame2) {
    getOutputStream().createFrame(246);
    getOutputStream().writeWordBigEndian(MainFrame);
    getOutputStream().writeWord(SubFrame);
    getOutputStream().writeWord(SubFrame2);
    flushOutStream();
  }

  public void sendFrame185(int Frame) {
    getOutputStream().createFrame(185);
    getOutputStream().writeWordBigEndianA(Frame);
    flushOutStream();
  }

  public void sendQuestSomething(int id) {
    getOutputStream().createFrame(79);
    getOutputStream().writeWordBigEndian(id);
    getOutputStream().writeWordA(0);
    flushOutStream();
  }

  public void clearQuestInterface() {
    for (int x = 0; x < QuestInterface.length; x++) {
      send(new SendString("", QuestInterface[x]));
    }
  }

  public void showInterface(int interfaceid) {
    resetAction();
    getOutputStream().createFrame(97);
    getOutputStream().writeWord(interfaceid);
    flushOutStream();
  }

  public int ancients = 1;
  public boolean ancientstele = false;
  public boolean teleport = false;
  public int teletimer = 0;
  public int teleX = 0;
  public int teleY = 0;
  public int newheightLevel = 0;

  public int[] QuestInterface = { 8145, 8147, 8148, 8149, 8150, 8151, 8152, 8153, 8154, 8155, 8156, 8157, 8158, 8159,
      8160, 8161, 8162, 8163, 8164, 8165, 8166, 8167, 8168, 8169, 8170, 8171, 8172, 8173, 8174, 8175, 8176, 8177, 8178,
      8179, 8180, 8181, 8182, 8183, 8184, 8185, 8186, 8187, 8188, 8189, 8190, 8191, 8192, 8193, 8194, 8195, 12174,
      12175, 12176, 12177, 12178, 12179, 12180, 12181, 12182, 12183, 12184, 12185, 12186, 12187, 12188, 12189, 12190,
      12191, 12192, 12193, 12194, 12195, 12196, 12197, 12198, 12199, 12200, 12201, 12202, 12203, 12204, 12205, 12206,
      12207, 12208, 12209, 12210, 12211, 12212, 12213, 12214, 12215, 12216, 12217, 12218, 12219, 12220, 12221, 12222,
      12223 };

  public int bonusSpec = 0, animationSpec = 0, emoteSpec = 0;
  public boolean specsOn = true;

  public int[] statId = { 10252, 11000, 10253, 11001, 10254, 11002, 10255, 11011, 11013, 11014, 11010, 11012, 11006,
      11009, 11008, 11004, 11003, 11005, 47002, 54090, 11007 };
  public String BonusName[] = { "Stab", "Slash", "Crush", "Magic", "Range", "Stab", "Slash", "Crush", "Magic", "Range",
      "Str", "Spell Dmg" };

  // public int pGender;
  public int i;
  // public int gender;

  public int XremoveSlot = 0;
  public int XinterfaceID = 0;
  public int XremoveID = 0;

  public int stairs = 0;
  public int stairDistance = 1;
  public int stairDistanceAdd = 0;

  public int woodcutting[] = { 0, 0, 0, 1, -1, 3 };
  public int smithing[] = { 0, 0, 0, 1, -1, 0 };

  public int skillX = -1;
  public int skillY = -1;
  public int CombatExpRate = 1;

  public int WanneBank = 0;
  public int WanneShop = 0;
  public int WanneThieve = 0;

  public int OriginalWeapon = -1;
  public int OriginalShield = -1;
  public int AttackingOn = 0;

  public static final int bufferSize = 1000000;
  private java.net.Socket mySock;
  private java.io.InputStream in;
  private java.io.OutputStream out;
  public byte buffer[] = null;
  public int readPtr, writePtr;

  private Stream inputStream = null;
  private Stream outputStream = null;

  public Cryption inStreamDecryption = null, outStreamDecryption = null;

  public int timeOutCounter = 0; // to detect timeouts on the connection to
  // the client

  public int returnCode = 2; // Tells the client if the login was successfull

  public Client(java.net.Socket s, int _playerId) {
    super(_playerId);

    mySock = s;
    try {
      in = s.getInputStream();
      out = s.getOutputStream();
    } catch (java.io.IOException ioe) {
      Utils.println("Uber Server (1): Exception!");
      Server.logError(ioe.getMessage());
    }

    setOuputStream(new Stream(new byte[bufferSize]));
    getOutputStream().currentOffset = 0;
    setInputStream(new Stream(new byte[bufferSize]));
    getInputStream().currentOffset = 0;

    readPtr = writePtr = 0;
    this.buffer = new byte[bufferSize];
  }

  public void shutdownError(String errorMessage) {
    Utils.println(": " + errorMessage);
    destruct();
  }

  public void destruct() {
    if (mySock == null) {
      return;
    } // already shutdown
    try {
      Utils.println("ClientHandler: Client " + getPlayerName() + " disconnected (" + connectedFrom + ")");
      ConnectionList.getInstance().remove(mySock.getInetAddress());
      disconnected = true;
      if (saveNeeded) {
          saveStats(true);
        }
      if (in != null) {
        in.close();
      }
      if (out != null) {
        out.close();
      }
      mySock.close();
      mySock = null;
      in = null;
      out = null;
      setInputStream(null);
      setOuputStream(null);
      isActive = false;
      synchronized (this) {
        notify();
      } // make sure this threads gets control so it can terminate
      buffer = null;
    } catch (java.io.IOException ioe) {
      ioe.printStackTrace();
    }
    super.destruct();
  }

  public Stream getInputStream() {
    return this.inputStream;
  }

  public void setInputStream(Stream stream) {
    this.inputStream = stream;
  }

  public Stream getOutputStream() {
    return this.outputStream;
  }

  public void setOuputStream(Stream stream) {
    this.outputStream = stream;
  }

  public void send(OutgoingPacket packet) {
    packet.send(this);
  }

  // writes any data in outStream to the relaying buffer
  public void flushOutStream() {
    if (disconnected || getOutputStream().currentOffset == 0) {
      return;
    }

    synchronized (this) {
      int maxWritePtr = (readPtr + bufferSize - 2) % bufferSize;

      for (int i = 0; i < getOutputStream().currentOffset; i++) {
        buffer[writePtr] = getOutputStream().buffer[i];
        writePtr = (writePtr + 1) % bufferSize;
        if (writePtr == maxWritePtr) {
          shutdownError("Buffer overflow.");
          disconnected = true;
          return;
        }
      }
      getOutputStream().currentOffset = 0;

      notify();
    }
  }

  // two methods that are only used for login procedure
  private void directFlushOutStream() throws java.io.IOException {
    out.write(getOutputStream().buffer, 0, getOutputStream().currentOffset);
    getOutputStream().currentOffset = 0; // reset
  }

  // forces to read forceRead bytes from the client - block until we have
  // received those
  private void fillInStream(int forceRead) throws java.io.IOException {
    getInputStream().currentOffset = 0;
    in.read(getInputStream().buffer, 0, forceRead);
  }

  public void run() {
    // we just accepted a new connection - handle the login stuff
    isActive = false;
    long serverSessionKey = 0, clientSessionKey = 0;
    
//	if (!KeyServer.verifiedKeys()){
//		System.out.println("User rejected due to unverified client.");
//		disconnected = true;
//		returnCode = 4;
//	}

    // randomize server part of the session key
    serverSessionKey = ((long) (java.lang.Math.random() * 99999999D) << 32)
        + (long) (java.lang.Math.random() * 99999999D);

    try {
      returnCode = 2;
      fillInStream(2);
      if (getInputStream().readUnsignedByte() != 14) {
        shutdownError("Expected login Id 14 from client.");
        disconnected = true;
        return;
      }
      getInputStream().readUnsignedByte();
      for (int i = 0; i < 8; i++) {
        // out.write(9 + server.world);
        out.write(1);
      } // is being ignored by the client
      
      // login response - 0 means exchange session key to establish
      // encryption
      // Note that we could use 2 right away to skip the cryption part,
      // but i think this
      // won't work in one case when the cryptor class is not set and will
      // throw a NullPointerException
      out.write(0);

      // send the server part of the session Id used (client+server part
      // together are used as cryption key)
      // println("serverSessionKey=" + serverSessionKey);
      getOutputStream().writeQWord(serverSessionKey);
      directFlushOutStream();
      fillInStream(2);
      int loginType = getInputStream().readUnsignedByte(); // this is either 16
      // (new login) or 18
      // (reconnect after
      // lost connection)
      if (loginType != 16 && loginType != 18) {
        shutdownError("Unexpected login type " + loginType);
        return;
      }
      // if (loginType == 18) {
      // reconnect = true;
      // }
      int loginPacketSize = getInputStream().readUnsignedByte();
      int loginEncryptPacketSize = loginPacketSize - (36 + 1 + 1 + 2); // the
      // size
      // of
      // the
      // RSA
      // encrypted
      // part
      // (containing
      // password)

      // misc.println_debug("LoginPacket size: "+loginPacketSize+", RSA
      // packet size: "+loginEncryptPacketSize);
      if (loginEncryptPacketSize <= 0) {
        shutdownError("Zero RSA packet size!");
        return;
      }
      fillInStream(loginPacketSize);
      if (getInputStream().readUnsignedByte() != 255 || getInputStream().readUnsignedWord() != 317) {
        println("invalid code");
        returnCode = 6;
        // return;
      }
      getInputStream().readUnsignedByte();
      // misc.println_debug("Client type: "+((lowMemoryVersion==1) ? "low"
      // : "high")+" memory version");
      for (int i = 0; i < 9; i++) {
        Integer.toHexString(getInputStream().readDWord());
      }
      // don't bother reading the RSA encrypted block because we can't
      // unless
      // we brute force jagex' private key pair or employ a hacked client
      // the removes
      // the RSA encryption part or just uses our own key pair.
      // Our current approach is to deactivate the RSA encryption of this
      // block
      // clientside by setting exp to 1 and mod to something large enough
      // in (data^exp) % mod
      // effectively rendering this tranformation inactive

      loginEncryptPacketSize--; // don't count length byte
      int tmp = getInputStream().readUnsignedByte();
      if (loginEncryptPacketSize != tmp) {
        shutdownError("Encrypted packet data length (" + loginEncryptPacketSize
            + ") different from length byte thereof (" + tmp + ")");
        return;
      }
      tmp = getInputStream().readUnsignedByte();
      if (tmp != 10) {
        shutdownError("Encrypted packet Id was " + tmp + " but expected 10");
        return;
      }
      clientSessionKey = getInputStream().readQWord();
      serverSessionKey = getInputStream().readQWord();
      
//		int uid = getInputStream().readUnsignedByte();
//		
//		if(uid == 0 || uid == 99735086) {
//			disconnected = true;
//			return;
//		}
      
      LoginManager.customClientVersion = getInputStream().readString();
      System.out.println("Client version: " + Config.customClientVersion);
      //LoginManager.UUID = getInputStream().readString();
      //System.out.println("UUID: " + LoginManager.UUID);
      clientPid = getInputStream().readDWord();
      setPlayerName(getInputStream().readString());
      if (getPlayerName() == null || getPlayerName().length() == 0) {
        setPlayerName("player" + getSlot());
      }
      playerPass = getInputStream().readString();
      String playerServer = "";
      try {
        playerServer = getInputStream().readString();
      } catch (Exception e) {
        playerServer = "srv.dodian.net";
      }

      setPlayerName(getPlayerName().toLowerCase());
      // playerPass = playerPass.toLowerCase();
      // System.out.println("valid chars");
      char[] validChars = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
          's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
          'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0',
          '_', ' ' };
      setPlayerName(getPlayerName().trim());
      int sessionKey[] = new int[4];

      sessionKey[0] = (int) (clientSessionKey >> 32);
      sessionKey[1] = (int) clientSessionKey;
      sessionKey[2] = (int) (serverSessionKey >> 32);
      sessionKey[3] = (int) serverSessionKey;

      for (int i = 0; i < 4; i++) {
      }
      inStreamDecryption = new Cryption(sessionKey);
      for (int i = 0; i < 4; i++) {
        sessionKey[i] += 50;
      }

      for (int i = 0; i < 4; i++) {
      }
      outStreamDecryption = new Cryption(sessionKey);
      getOutputStream().packetEncryption = outStreamDecryption;

      int letters = 0;
      for (int i = 0; i < getPlayerName().length(); i++) {
        boolean valid = false;
        for (int i1 = 0; i1 < validChars.length; i1++) {
          if (getPlayerName().charAt(i) == validChars[i1]) {
            valid = true;
            // break;
          }
          if (valid && getPlayerName().charAt(i) != '_' && getPlayerName().charAt(i) != ' ') {
            letters++;
          }
        }
        if (!valid) {
          returnCode = 4;
          disconnected = true;
        }
      }
      if (letters < 1) {
        returnCode = 3;
        disconnected = true;
      }
      char first = getPlayerName().charAt(0);
      properName = Character.toUpperCase(first) + getPlayerName().substring(1, getPlayerName().length()).toLowerCase();
      setPlayerName(properName.replace("_", " "));
      longName = Utils.playerNameToInt64(getPlayerName());
      if (Server.updateRunning) {
        returnCode = 14;
        disconnected = true;
        println_debug(getPlayerName() + " refused - update is running !");
      }
      int loadgame = 0;
      if (returnCode == 6) {
        setPlayerName("_");
        disconnected = true;
        teleportToX = 0;
        teleportToY = 0;
      } else {
        loadgame = Server.loginManager.loadgame(this, getPlayerName(), playerPass);
        switch (playerGroup) {
        case 6: // root admin
          playerRights = 2;
          premium = true;
          break;
        case 18: // root admin
            playerRights = 2;
            premium = true;
            break;
        case 10: // content dev
          playerRights = 2;
          premium = true;
          break;
        case 9: // player moderator
        case 5: // global mod
          playerRights = 1;
          premium = true;
          break;
        // case 10:
        case 11:
        case 7:
        case 27:
          premium = true;
          break;
        default:
          premium = false;
          playerRights = 0;
        }
        for (int a = 0; a < otherGroups.length; a++) {
          if (otherGroups[a] == null) {
            continue;
          }
          String temp = otherGroups[a].trim();
          if (temp != null && temp.length() > 0) {
            int group = Integer.parseInt(temp);
            switch (group) {
            case 14:
              premium = true;
              break;
            case 3:
            case 19:
              playerRights = 1;
              break;
            }
          }
        }
        for (int i = 0; i < getEquipment().length; i++) {
          if (getEquipment()[i] == 0) {
            getEquipment()[i] = -1;
            getEquipmentN()[i] = 0;
          }
        }
        if (loadgame == 0 && returnCode != 6) {
          validLogin = true;
          if (getPosition().getX() > 0 && getPosition().getY() > 0) {
            teleportToX = getPosition().getX();
            teleportToY = getPosition().getY();
          }
        } else {
          if (returnCode != 6 && returnCode != 5)
            returnCode = loadgame;
          setPlayerName("_");
          disconnected = true;
          teleportToX = 0;
          teleportToY = 0;
        }

      }
      if (getSlot() == -1) {
        out.write(7); // "This world is full."
      } else if (playerServer.equals("INVALID")) {
        out.write(10);
      } else {
        out.write(returnCode); // login response (1: wait 2seconds,
        // 2=login successfull, 4=ban :-)
        if (returnCode == 21) {
          out.write(loginDelay);
          // if(returnCode == 4 && officialClient)
          // out.write(bannedHours);
        }

      }
      out.write(playerRights); // mod level
      out.write(0); // no log
      getUpdateFlags().setRequired(UpdateFlag.APPEARANCE, true);
    } catch (java.lang.Exception __ex) {
      __ex.printStackTrace();
      destruct();
      return;
    }
    // }
    isActive = true;
    if (getSlot() == -1 || returnCode != 2) {
      return;
    }
    packetSize = 0;
    packetType = -1;
    readPtr = 0;
    writePtr = 0;

    int numBytesInBuffer, offset;

    while (!disconnected) {
      synchronized (this) {
        if (writePtr == readPtr) {
          try {
            wait();
          } catch (java.lang.InterruptedException _ex) {
          }
        }

        if (disconnected) {
          return;
        }

        offset = readPtr;
        if (writePtr >= readPtr) {
          numBytesInBuffer = writePtr - readPtr;
        } else {
          numBytesInBuffer = bufferSize - readPtr;
        }
      }
      if (numBytesInBuffer > 0 && disconnectAt == 0) {
        try {
          out.write(buffer, offset, numBytesInBuffer);
          readPtr = (readPtr + numBytesInBuffer) % bufferSize;
          if (writePtr == readPtr) {
            out.flush();
          }
        } catch (java.net.SocketException e) {
          if (loggingOut) {
            disconnected = true;
          } else {
            disconnectAt = System.currentTimeMillis() + 45000;
            lagOut();
          }
        } catch (java.lang.Exception __ex) {
          __ex.printStackTrace();
          disconnectAt = System.currentTimeMillis() + 45000;
        }
      }
    }
  }

	public void setSidebarInterface(int menuId, int form) {
    getOutputStream().createFrame(71);
    getOutputStream().writeWord(form);
    getOutputStream().writeByteA(menuId);
  }

  public void setSkillLevel(int skillNum, int currentLevel, int XP) {
    if (skillNum == 0) {
      send(new SendString("" + currentLevel + "", 4004));
      send(new SendString("" + Skills.getLevelForExperience(XP) + "", 4005));
    }
    if (skillNum == 2) {
      send(new SendString("" + currentLevel + "", 4006));
      send(new SendString("" + Skills.getLevelForExperience(XP) + "", 4007));
    }
    if (skillNum == 1) {
      send(new SendString("" + currentLevel + "", 4008));
      send(new SendString("" + Skills.getLevelForExperience(XP) + "", 4009));
    }
    if (skillNum == 4) {
      send(new SendString("" + currentLevel + "", 4010));
      send(new SendString("" + Skills.getLevelForExperience(XP) + "", 4011));
    }
    if (skillNum == 5) {
      send(new SendString("" + currentLevel + "", 4012));
      send(new SendString("" + Skills.getLevelForExperience(XP) + "", 4013));
    }
    if (skillNum == 6) {
      send(new SendString("" + currentLevel + "", 4014));
      send(new SendString("" + Skills.getLevelForExperience(XP) + "", 4015));
    }
    if (skillNum == 3) {
      send(new SendString("" + currentLevel + "", 4016));
      send(new SendString("" + Skills.getLevelForExperience(XP) + "", 4017));
    }
    if (skillNum == 16) {
      send(new SendString("" + currentLevel + "", 4018));
      send(new SendString("" + Skills.getLevelForExperience(XP) + "", 4019));
    }
    if (skillNum == 15) {
      send(new SendString("" + currentLevel + "", 4020));
      send(new SendString("" + Skills.getLevelForExperience(XP) + "", 4021));
    }
    if (skillNum == 17) {
      send(new SendString("" + currentLevel + "", 4022));
      send(new SendString("" + Skills.getLevelForExperience(XP) + "", 4023));
    }
    if (skillNum == 12) {
      send(new SendString("" + currentLevel + "", 4024));
      send(new SendString("" + Skills.getLevelForExperience(XP) + "", 4025));
    }
    if (skillNum == 9) {
      send(new SendString("" + currentLevel + "", 4026));
      send(new SendString("" + Skills.getLevelForExperience(XP) + "", 4027));
    }
    if (skillNum == 14) {
      send(new SendString("" + currentLevel + "", 4028));
      send(new SendString("" + Skills.getLevelForExperience(XP) + "", 4029));
    }
    if (skillNum == 13) {
      send(new SendString("" + currentLevel + "", 4030));
      send(new SendString("" + Skills.getLevelForExperience(XP) + "", 4031));
    }
    if (skillNum == 10) {
      send(new SendString("" + currentLevel + "", 4032));
      send(new SendString("" + Skills.getLevelForExperience(XP) + "", 4033));
    }
    if (skillNum == 7) {
      send(new SendString("" + currentLevel + "", 4034));
      send(new SendString("" + Skills.getLevelForExperience(XP) + "", 4035));
    }
    if (skillNum == 11) {
      send(new SendString("" + currentLevel + "", 4036));
      send(new SendString("" + Skills.getLevelForExperience(XP) + "", 4037));
    }
    if (skillNum == 8) {
      send(new SendString("" + currentLevel + "", 4038));
      send(new SendString("" + Skills.getLevelForExperience(XP) + "", 4039));
    }
    if (skillNum == 20) {
      send(new SendString("" + currentLevel + "", 4152));
      send(new SendString("" + Skills.getLevelForExperience(XP) + "", 4153));
    }
    if (skillNum == 18) {
      send(new SendString("" + currentLevel + "", 12166));
      send(new SendString("" + Skills.getLevelForExperience(XP) + "", 12167));
    }
    if (skillNum == 19) {
      send(new SendString("" + currentLevel + "", 13926));
      send(new SendString("" + Skills.getLevelForExperience(XP) + "", 13927));
    }
  }

  public void logout() {
    // declineDuel();
    if (!saveNeeded) {
      return;
    }
    if (!validClient) {
      return;
    }
    saveNeeded = false;
    send(new SendMessage("Please wait... logging out may take time"));
    send(new SendString("     Please wait...", 2458));
    saveStats(true);
    send(new SendString("Click here to logout", 2458));
    getOutputStream().createFrame(109);
    loggingOut = true;
  }

  /*
   * public void saveStats(boolean logout){ server.login.saveStats(this,
   * logout); if(logout){ long elapsed = System.currentTimeMillis() -
   * session_start; server.login.sendSession(dbId, clientPid, elapsed,
   * connectedFrom); } }
   */

  public void saveStats(boolean logout) {
    if (!loadingDone)
      return;
    if (loginDelay > 0) {
      println("Incomplete login, aborting save");
      return;
    }
    long start = System.currentTimeMillis();
    if (!validLogin) {
      return;
    }
    if (getPlayerName() == null || getPlayerName().equals("null") || dbId < 1) {
      saveNeeded = false;
      return;
    }
    if (getPlayerName().indexOf("'") > 0 || playerPass.indexOf("`") > 0) {
      println_debug("Invalid player name");
      return;
    }
    if (logout) {
      saving = true;
      for (Player p : PlayerHandler.players) {
        if (p != null && !p.disconnected && p.dbId > 0) {
          if (p.getDamage().containsKey(getSlot())) {
            p.getDamage().put(getSlot(), 0);
          }
        }
      }
      long elapsed = System.currentTimeMillis() - start;
      Server.login.sendSession(dbId, clientPid, elapsed, connectedFrom);
      PlayerHandler.playersOnline.remove(longName);
      // handler.allOnline.remove(longName);
      for (Client c : PlayerHandler.playersOnline.values()) {
        if (c.hasFriend(longName)) {
          c.refreshFriends();
        }
      }

    }
    if (logout && inTrade) {
      System.out.println("declining");
      declineTrade();
    }
    if (logout && inDuel && !duelFight) {
      declineDuel();
    }
    if (logout && duel_with > 0 && validClient(duel_with) && inDuel && duelFight) {
      Client p = getClient(duel_with);
      p.duelWin = true;
      p.DuelVictory();
    }
    try {
      Statement statement = Database.conn.createStatement();
      long allxp = 0;
      for (int i = 0; i < 21; i++) {
        if (i != 18) {
          allxp += getExperience(Skill.getSkill(i));
        }
      }
      int totallvl = 0;
      for (int i = 0; i < 21; i++) {
        totallvl += getLevel(Skill.getSkill(i));
      }
      int combatLevel = (int) ((double) getLevel(Skill.ATTACK) * 0.32707 + (double) getLevel(Skill.DEFENCE) * 0.249
          + (double) getLevel(Skill.STRENGTH) * 0.324 + (double) getLevel(Skill.HITPOINTS) * 0.25
          + (double) getLevel(Skill.PRAYER) * 0.124);
      String query = "UPDATE character_stats SET total=" + totallvl + ", combat=" + combatLevel + ", ";
      for (int i = 0; i < 21; i++) {
        query += Skill.getSkill(i).getName() + "=" + getExperience(Skill.getSkill(i)) + ", ";
      }
      query += "totalxp=" + allxp + " WHERE uid=" + dbId;
      statement.executeUpdate(query);
      System.currentTimeMillis();
      String inventory = "", equipment = "", bank = "", list = "", boss_log = "";
      for (int i = 0; i < playerItems.length; i++) {
        if (playerItems[i] > 0) {
          inventory += i + "-" + (playerItems[i] - 1) + "-" + playerItemsN[i] + " ";
        }
      }
      for (int i = 0; i < bankItems.length; i++) {
        if (bankItems[i] > 0) {
          bank += i + "-" + (bankItems[i] - 1) + "-" + bankItemsN[i] + " ";
        }
      }
      for (int i = 0; i < getEquipment().length; i++) {
        if (getEquipment()[i] > 0) {
          equipment += i + "-" + (getEquipment()[i]) + "-" + getEquipmentN()[i] + " ";
        }
      }
      for (int i = 0; i < boss_name.length; i++) {
        if (boss_amount[i] >= 0) {
          boss_log += boss_name[i] + ":" + boss_amount[i] + " ";
        }
      }
      int num = 0;
      for (Friend f : friends) {
        if (f.name > 0 && num < 200) {
          list += f.name + " ";
          num++;
        }
      }
      if (!logout) {
      } else {
        saveNeeded = false;
      }
      String last = "";
      long elapsed = System.currentTimeMillis() - session_start;
      if (elapsed > 10000) {
        last = ", lastlogin = '" + System.currentTimeMillis() + "'";
      }
      statement.executeUpdate("UPDATE characters SET sibling= '" + isSibling + "', uuid= '" + LoginManager.UUID + "', lastvote=" + lastVoted + ", pkrating=" + 1500 + ", health="
          + getCurrentHealth() + ", equipment='" + equipment + "', inventory='" + inventory + "', bank='" + bank
          + "', friends='" + list + "', taskid = " + taskId + ", fightStyle = " + FightType + ", taskamt=" + taskAmt
          + ", tasktotal=" + taskTotal + ", height = " + getPosition().getZ() + ", x = " + getPosition().getX()
          + ", y = " + getPosition().getY() + ", lastlogin = '" + System.currentTimeMillis() + "', Boss_Log='"
          + boss_log + "', songUnlocked='" + getSongUnlockedSaveText() + "', look='" + getLook() + "'" + last
          + " WHERE id = " + dbId);
      statement.close();
      println_debug("Save:  " + getPlayerName() + " (" + (System.currentTimeMillis() - start) + "ms)");
    } catch (Exception e) {
      e.printStackTrace();
      println_debug("Save Exception: " + getSlot() + ", " + getPlayerName());
      return;
    }
  }

  public void fromBank(int itemID, int fromSlot, int amount) {
    if (!IsBanking) {
      send(new RemoveInterfaces());
      return;
    }
    if (amount > 0) {
      if (bankItems[fromSlot] > 0) {
        if (!takeAsNote) {
          // if (Item.itemStackable[bankItems[fromSlot] - 1]) {
          if (Server.itemManager.isStackable(bankItems[fromSlot] - 1)) {
            if (bankItemsN[fromSlot] > amount) {
              if (addItem((bankItems[fromSlot] - 1), amount)) {
                bankItemsN[fromSlot] -= amount;
                resetBank();
                resetItems(5064);
              }
            } else {
              if (addItem((bankItems[fromSlot] - 1), bankItemsN[fromSlot])) {
                bankItems[fromSlot] = 0;
                bankItemsN[fromSlot] = 0;
                resetBank();
                resetItems(5064);
              }
            }
          } else {
            while (amount > 0) {
              if (bankItemsN[fromSlot] > 0) {
                if (addItem((bankItems[fromSlot] - 1), 1)) {
                  bankItemsN[fromSlot] += -1;
                  amount--;
                } else {
                  amount = 0;
                }
              } else {
                amount = 0;
              }
            }
            resetBank();
            resetItems(5064);
          }
        } else if (takeAsNote && Server.itemManager.isNote(bankItems[fromSlot])) {
          // if (Item.itemStackable[bankItems[fromSlot]+1])
          // {
          if (bankItemsN[fromSlot] > amount) {
            if (addItem(bankItems[fromSlot], amount)) {
              bankItemsN[fromSlot] -= amount;
              resetBank();
              resetItems(5064);
            }
          } else {
            if (addItem(bankItems[fromSlot], bankItemsN[fromSlot])) {
              bankItems[fromSlot] = 0;
              bankItemsN[fromSlot] = 0;
              resetBank();
              resetItems(5064);
            }
          }
        } else {
          send(new SendMessage("Item can't be drawn as note."));
          // if (Item.itemStackable[bankItems[fromSlot] - 1]) {
          if (Server.itemManager.isStackable(bankItems[fromSlot] - 1)) {
            if (bankItemsN[fromSlot] > amount) {
              if (addItem((bankItems[fromSlot] - 1), amount)) {
                bankItemsN[fromSlot] -= amount;
                resetBank();
                resetItems(5064);
              }
            } else {
              if (addItem((bankItems[fromSlot] - 1), bankItemsN[fromSlot])) {
                bankItems[fromSlot] = 0;
                bankItemsN[fromSlot] = 0;
                resetBank();
                resetItems(5064);
              }
            }
          } else {
            while (amount > 0) {
              if (bankItemsN[fromSlot] > 0) {
                if (addItem((bankItems[fromSlot] - 1), 1)) {
                  bankItemsN[fromSlot] += -1;
                  amount--;
                } else {
                  amount = 0;
                }
              } else {
                amount = 0;
              }
            }
            resetBank();
            resetItems(5064);
          }
        }
      }
    }
  }

  /**
   * Gets the item slot of the specified id.
   * 
   * @param itemID
   *          The item to look for.
   * @return The slot if found, otherwise -1.
   */
  public int getItemSlot(int itemID) {
    for (int slot = 0; slot < playerItems.length; slot++) {
      if (playerItems[slot] == (itemID + 1)) {
        return slot;
      }
    }

    return -1;
  }
  
	 public int UsedSlots(){
		int freeS=0;
     for (int i= 0; i < playerBankSize; i++) {
			if (bankItems[i] <= 0) {
				freeS++;
			}
		}
		return playerBankSize - freeS;
	}

  public boolean giveExperience(int amount, Skill skill) {
    if (amount < 1)
      return false;
    if (randomed) {
      send(new SendMessage("You must answer the genie before you can gain experience!"));
      return false;
    }

    int oldLevel = Skills.getLevelForExperience(getExperience(skill));
    addExperience(amount * Config.getExperienceMultiplier(), skill);
    if (oldLevel < Skills.getLevelForExperience(getExperience(skill))) {
      animation(199, getPosition().getY(), getPosition().getX());
      setLevel(Skills.getLevelForExperience(getExperience(skill)), skill);
      getUpdateFlags().setRequired(UpdateFlag.APPEARANCE, true);
      if (skill == Skill.ATTACK || skill == Skill.AGILITY) {
        send(new SendMessage("Congratulations, you just advanced an " + skill.getName() + " level."));
      } else {
        send(new SendMessage("Congratulations, you just advanced a " + skill.getName() + " level."));
      }
      if (getLevel(skill) > 90) {
        publicyell(getPlayerName() + "'s " + skill.getName() + " level is now " + getLevel(skill) + "!");
      }
    }
    refreshSkill(skill);
    if (skill == Skill.STRENGTH) {
      CalculateMaxHit();
    }
    return true;

  }

  public boolean bankItem(int itemID, int fromSlot, int amount) {
    if (!IsBanking) {
      return false;
    }
    if (playerItemsN[fromSlot] <= 0) {
      return false;
    }
    if (playerItems[fromSlot] <= 0){
		return false;
	}
    // if (!Item.itemIsNote[playerItems[fromSlot] - 1]) {
    if (!Server.itemManager.isNote(itemID)) {
      if (playerItems[fromSlot] <= 0) {
        return false;
      }
      if (Server.itemManager.isStackable(itemID) || playerItemsN[fromSlot] > 1) {
        int toBankSlot = 0;
        boolean alreadyInBank = false;

        for (int i = 0; i < playerBankSize; i++) {
          if (bankItems[i] == playerItems[fromSlot]) {
            if (playerItemsN[fromSlot] < amount) {
              amount = playerItemsN[fromSlot];
            }
            alreadyInBank = true;
            toBankSlot = i;
            i = playerBankSize + 1;
          }
        }

        if (!alreadyInBank && freeBankSlots() > 0) {
          for (int i = 0; i < playerBankSize; i++) {
            if (bankItems[i] <= 0) {
              toBankSlot = i;
              i = playerBankSize + 1;
            }
          }
          bankItems[toBankSlot] = playerItems[fromSlot];
          System.out.println("Amount: " + amount);
          if (playerItemsN[fromSlot] < amount) {
            amount = playerItemsN[fromSlot];
          }
          if ((bankItemsN[toBankSlot] + amount) <= maxItemAmount && (bankItemsN[toBankSlot] + amount) > -1) {
            bankItemsN[toBankSlot] += amount;
          } else {
            send(new SendMessage("Bank full!"));
            return false;
          }
          deleteItem((playerItems[fromSlot] - 1), fromSlot, amount);
          resetItems(5064);
          resetBank();
          return true;
        } else if (alreadyInBank) {
          if ((bankItemsN[toBankSlot] + amount) <= maxItemAmount && (bankItemsN[toBankSlot] + amount) > -1) {
            bankItemsN[toBankSlot] += amount;
          } else {
            send(new SendMessage("Bank full!"));
            return false;
          }
          deleteItem((playerItems[fromSlot] - 1), fromSlot, amount);
          resetItems(5064);
          resetBank();
          return true;
        } else {
          send(new SendMessage("Bank full!"));
          return false;
        }
      } else {
        itemID = playerItems[fromSlot];
        int toBankSlot = 0;
        boolean alreadyInBank = false;

        for (int i = 0; i < playerBankSize; i++) {
          if (bankItems[i] == playerItems[fromSlot]) {
            alreadyInBank = true;
            toBankSlot = i;
            i = playerBankSize + 1;
          }
        }
        if (!alreadyInBank && freeBankSlots() > 0) {
          for (int i = 0; i < playerBankSize; i++) {
            if (bankItems[i] <= 0) {
              toBankSlot = i;
              i = playerBankSize + 1;
            }
          }
          int firstPossibleSlot = 0;
          boolean itemExists = false;

          while (amount > 0) {
            itemExists = false;
            for (int i = firstPossibleSlot; i < playerItems.length; i++) {
              if ((playerItems[i]) == itemID) {
                firstPossibleSlot = i;
                itemExists = true;
                i = 30;
              }
            }
            if (itemExists) {
              bankItems[toBankSlot] = playerItems[firstPossibleSlot];
              bankItemsN[toBankSlot] += 1;
              deleteItem((playerItems[firstPossibleSlot] - 1), firstPossibleSlot, 1);
              amount--;
            } else {
              amount = 0;
            }
          }
          resetItems(5064);
          resetBank();
          return true;
        } else if (alreadyInBank) {
          int firstPossibleSlot = 0;
          boolean itemExists = false;

          while (amount > 0) {
            itemExists = false;
            for (int i = firstPossibleSlot; i < playerItems.length; i++) {
              if ((playerItems[i]) == itemID) {
                firstPossibleSlot = i;
                itemExists = true;
                i = 30;
              }
            }
            if (itemExists) {
              bankItemsN[toBankSlot] += 1;
              deleteItem((playerItems[firstPossibleSlot] - 1), firstPossibleSlot, 1);
              amount--;
            } else {
              amount = 0;
            }
          }
          resetItems(5064);
          resetBank();
          return true;
        } else {
          send(new SendMessage("Bank full!"));
          return false;
        }
      }
    } else if (Server.itemManager.isNote(playerItems[fromSlot] - 1)
        && !Server.itemManager.isNote(playerItems[fromSlot] - 2)) {
      if (playerItems[fromSlot] <= 0) {
        return false;
      }
      if (Server.itemManager.isStackable(playerItems[fromSlot] - 1) || playerItemsN[fromSlot] > 1) {
        int toBankSlot = 0;
        boolean alreadyInBank = false;

        for (int i = 0; i < playerBankSize; i++) {
          if (bankItems[i] == playerItems[fromSlot] - 1) {
            if (playerItemsN[fromSlot] < amount) {
              amount = playerItemsN[fromSlot];
            }
            alreadyInBank = true;
            toBankSlot = i;
            i = playerBankSize + 1;
          }
        }
        if (!alreadyInBank && freeBankSlots() > 0) {
          for (int i = 0; i < playerBankSize; i++) {
            if (bankItems[i] <= 0) {
              toBankSlot = i;
              i = playerBankSize + 1;
            }
          }
          bankItems[toBankSlot] = playerItems[fromSlot] - 1;
          if (playerItemsN[fromSlot] < amount) {
            amount = playerItemsN[fromSlot];
          }
          if ((bankItemsN[toBankSlot] + amount) <= maxItemAmount && (bankItemsN[toBankSlot] + amount) > -1) {
            bankItemsN[toBankSlot] += amount;
          } else {
            return false;
          }
          deleteItem((playerItems[fromSlot] - 1), fromSlot, amount);
          resetItems(5064);
          resetBank();
          return true;
        } else if (alreadyInBank) {
          if ((bankItemsN[toBankSlot] + amount) <= maxItemAmount && (bankItemsN[toBankSlot] + amount) > -1) {
            bankItemsN[toBankSlot] += amount;
          } else {
            return false;
          }
          deleteItem((playerItems[fromSlot] - 1), fromSlot, amount);
          resetItems(5064);
          resetBank();
          return true;
        } else {
          send(new SendMessage("Bank full!"));
          return false;
        }
      } else {
        itemID = playerItems[fromSlot];
        int toBankSlot = 0;
        boolean alreadyInBank = false;

        for (int i = 0; i < playerBankSize; i++) {
          if (bankItems[i] == (playerItems[fromSlot] - 1)) {
            alreadyInBank = true;
            toBankSlot = i;
            i = playerBankSize + 1;
          }
        }
        if (!alreadyInBank && freeBankSlots() > 0) {
          for (int i = 0; i < playerBankSize; i++) {
            if (bankItems[i] <= 0) {
              toBankSlot = i;
              i = playerBankSize + 1;
            }
          }
          int firstPossibleSlot = 0;
          boolean itemExists = false;

          while (amount > 0) {
            itemExists = false;
            for (int i = firstPossibleSlot; i < playerItems.length; i++) {
              if ((playerItems[i]) == itemID) {
                firstPossibleSlot = i;
                itemExists = true;
                i = 30;
              }
            }
            if (itemExists) {
              bankItems[toBankSlot] = (playerItems[firstPossibleSlot] - 1);
              bankItemsN[toBankSlot] += 1;
              deleteItem((playerItems[firstPossibleSlot] - 1), firstPossibleSlot, 1);
              amount--;
            } else {
              amount = 0;
            }
          }
          resetItems(5064);
          resetBank();
          return true;
        } else if (alreadyInBank) {
          int firstPossibleSlot = 0;
          boolean itemExists = false;

          while (amount > 0) {
            itemExists = false;
            for (int i = firstPossibleSlot; i < playerItems.length; i++) {
              if ((playerItems[i]) == itemID) {
                firstPossibleSlot = i;
                itemExists = true;
                i = 30;
              }
            }
            if (itemExists) {
              bankItemsN[toBankSlot] += 1;
              deleteItem((playerItems[firstPossibleSlot] - 1), firstPossibleSlot, 1);
              amount--;
            } else {
              amount = 0;
            }
          }
          resetItems(5064);
          resetBank();
          return true;
        } else {
          send(new SendMessage("Bank full!"));
          return false;
        }
      }
    } else {
      send(new SendMessage("Item not supported " + (playerItems[fromSlot] - 1)));
      return false;
    }
  }

  public void resetItems(int WriteFrame) {
    getOutputStream().createFrameVarSizeWord(53);
    getOutputStream().writeWord(WriteFrame);
    getOutputStream().writeWord(playerItems.length);
    for (int i = 0; i < playerItems.length; i++) {
      if (playerItemsN[i] > 254) {
        getOutputStream().writeByte(255); // item's stack count. if over 254,
        // write byte 255
        getOutputStream().writeDWord_v2(playerItemsN[i]); // and then the real
        // value with
        // writeDWord_v2
      } else {
        getOutputStream().writeByte(playerItemsN[i]);
      }
      if (playerItems[i] < 0) {
        playerItems[i] = -1;
      }
      getOutputStream().writeWordBigEndianA(playerItems[i]); // item id
    }
    getOutputStream().endFrameVarSizeWord();
  }

  public void SetSmithing(int WriteFrame) {
    getOutputStream().createFrameVarSizeWord(53);
    getOutputStream().writeWord(WriteFrame);
    getOutputStream().writeWord(Constants.SmithingItems.length);
    for (int i = 0; i < Constants.SmithingItems.length; i++) {
      Constants.SmithingItems[i][0] += 1;
      if (Constants.SmithingItems[i][1] > 254) {
        getOutputStream().writeByte(255); // item's stack count. if over 254,
        // write byte 255
        getOutputStream().writeDWord_v2(Constants.SmithingItems[i][1]); // and
                                                                        // then
        // the real
        // value
        // with
        // writeDWord_v2
      } else {
        getOutputStream().writeByte(Constants.SmithingItems[i][1]);
      }
      if (Constants.SmithingItems[i][0] < 0) {
        playerItems[i] = 7500;
      }
      getOutputStream().writeWordBigEndianA(Constants.SmithingItems[i][0]); // item
      // id
    }
    getOutputStream().endFrameVarSizeWord();
  }

  public void resetOTItems(int WriteFrame) {
    Client other = getClient(trade_reqId);
    if (!validClient(trade_reqId)) {
      return;
    }
    getOutputStream().createFrameVarSizeWord(53);
    getOutputStream().writeWord(WriteFrame);
    int len = other.offeredItems.toArray().length;
    int current = 0;
    getOutputStream().writeWord(len);
    for (GameItem item : other.offeredItems) {
      if (item.getAmount() > 254) {
        getOutputStream().writeByte(255); // item's stack count. if over 254,
        // write byte 255
        getOutputStream().writeDWord_v2(item.getAmount()); // and then the real
        // value with
        // writeDWord_v2
      } else {
        getOutputStream().writeByte(item.getAmount());
      }
      getOutputStream().writeWordBigEndianA(item.getId() + 1); // item id
      current++;
    }
    if (current < 27) {
      for (int i = current; i < 28; i++) {
        getOutputStream().writeByte(1);
        getOutputStream().writeWordBigEndianA(-1);
      }
    }
    getOutputStream().endFrameVarSizeWord();
  }

  public void resetTItems(int WriteFrame) {
    getOutputStream().createFrameVarSizeWord(53);
    getOutputStream().writeWord(WriteFrame);
    int len = offeredItems.toArray().length;
    int current = 0;
    getOutputStream().writeWord(len);
    for (GameItem item : offeredItems) {
      if (item.getAmount() > 254) {
        getOutputStream().writeByte(255); // item's stack count. if over 254,
        // write byte 255
        getOutputStream().writeDWord_v2(item.getAmount()); // and then the real
        // value with
        // writeDWord_v2
      } else {
        getOutputStream().writeByte(item.getAmount());
      }
      getOutputStream().writeWordBigEndianA(item.getId() + 1); // item id
      current++;
    }
    if (current < 27) {
      for (int i = current; i < 28; i++) {
        getOutputStream().writeByte(1);
        getOutputStream().writeWordBigEndianA(-1);
      }
    }
    getOutputStream().endFrameVarSizeWord();
  }

  public void resetShop(int ShopID) {
    int TotalItems = 0;

    for (int i = 0; i < ShopHandler.MaxShopItems; i++) {
      if (ShopHandler.ShopItems[ShopID][i] > 0) {
        TotalItems++;
      }
    }
    if (TotalItems > ShopHandler.MaxShopItems) {
      TotalItems = ShopHandler.MaxShopItems;
    }
    getOutputStream().createFrameVarSizeWord(53);
    getOutputStream().writeWord(3900);
    getOutputStream().writeWord(TotalItems);
    int TotalCount = 0;

    for (int i = 0; i < ShopHandler.ShopItems.length; i++) {
      if (ShopHandler.ShopItems[ShopID][i] > 0 || i <= ShopHandler.ShopItemsStandard[ShopID]) {
        if (ShopHandler.ShopItemsN[ShopID][i] > 254) {
          getOutputStream().writeByte(255); // item's stack count. if over
          // 254, write byte 255
          getOutputStream().writeDWord_v2(ShopHandler.ShopItemsN[ShopID][i]); // and
          // then
          // the
          // real
          // value
          // with
          // writeDWord_v2
        } else {
          getOutputStream().writeByte(ShopHandler.ShopItemsN[ShopID][i]);
        }
        if (ShopHandler.ShopItems[ShopID][i] < 0) {
          ShopHandler.ShopItems[ShopID][i] = 7500;
        }
        getOutputStream().writeWordBigEndianA(ShopHandler.ShopItems[ShopID][i]); // item
        // id
        TotalCount++;
      }
      if (TotalCount > TotalItems) {
        break;
      }
    }
    getOutputStream().endFrameVarSizeWord();
  }

  public void resetBank() {
    getOutputStream().createFrameVarSizeWord(53);
    getOutputStream().writeWord(5382); // bank
    getOutputStream().writeWord(playerBankSize); // number of items
    for (int i = 0; i < playerBankSize; i++) {
      if (bankItemsN[i] > 254) {
        getOutputStream().writeByte(255);
        getOutputStream().writeDWord_v2(bankItemsN[i]);
      } else {
        getOutputStream().writeByte(bankItemsN[i]); // amount
      }
      if (bankItemsN[i] < 1) {
        bankItems[i] = 0;
      }
      if (bankItems[i] < 0) {
        bankItems[i] = 7500;
      }
      getOutputStream().writeWordBigEndianA(bankItems[i]); // itemID
    }
    getOutputStream().endFrameVarSizeWord();
  }

  public void moveItems(int from, int to, int moveWindow) {
    if (moveWindow == 3724) {
      int tempI;
      int tempN;

      tempI = playerItems[from];
      tempN = playerItemsN[from];

      playerItems[from] = playerItems[to];
      playerItemsN[from] = playerItemsN[to];
      playerItems[to] = tempI;
      playerItemsN[to] = tempN;
    }

    if (moveWindow == 34453 && from >= 0 && to >= 0 && from < playerBankSize && to < playerBankSize) {
      int tempI;
      int tempN;

      tempI = bankItems[from];
      tempN = bankItemsN[from];

      bankItems[from] = bankItems[to];
      bankItemsN[from] = bankItemsN[to];
      bankItems[to] = tempI;
      bankItemsN[to] = tempN;
    }

    if (moveWindow == 34453) {
      resetBank();
    } else if (moveWindow == 18579) {
      resetItems(5064);
    } else if (moveWindow == 3724) {
      resetItems(3214);
    }
  }

  public int itemAmount(int itemID) {
    int tempAmount = 0;

    for (int i = 0; i < playerItems.length; i++) {
      if (playerItems[i] == itemID) {
        tempAmount += playerItemsN[i];
      }
    }
    return tempAmount;
  }

  public int freeBankSlots() {
    int freeS = 0;

    for (int i = 0; i < playerBankSize; i++) {
      if (bankItems[i] <= 0) {
        freeS++;
      }
    }
    return freeS;
  }

  public int freeSlots() {
    int freeS = 0;

    for (int i = 0; i < playerItems.length; i++) {
      if (playerItems[i] <= 0) {
        freeS++;
      }
    }
    return freeS;
  }

  public void pickUpItem(int id, int x, int y) {
    for (GroundItem item : Ground.items) {
      if (item.id == id && getPosition().getX() == x && getPosition().getY() == y
          && (item.visible || getSlot() == item.dropper) && playerHasItem(-1)) {
        if (getPosition().getX() == item.x && getPosition().getY() == item.y) {
          if (premiumItem(item.id) && !premium) {
            send(new SendMessage("You must be a premium member to use this item"));
            return;
          }
          send(new Sound(356));
          addItem(item.id, item.amount);
          Ground.deleteItem(item, dbId);
          break;
        }
      }
    }
  }

  public void openUpBank() {
    resetAction(true);
    send(new InventoryInterface(5292, 5063)); // 23000
    resetItems(5064);
    IsBanking = true;
  }

  public void openUpShop(int ShopID) {
    if (ShopID == 20 || ShopID == 34) {
      if (!premium) {
        send(new SendMessage("You need to be a premium member to access this shop."));
        return;
      }
    }
    send(new SendString(ShopHandler.ShopName[ShopID], 3901));
    send(new InventoryInterface(3824, 3822));
    resetItems(3823);
    resetShop(ShopID);
    IsShopping = true;
    MyShopID = ShopID;
  }

  public boolean addItem(int item, int amount) {
    if (item < 0 || amount < 1) {
      return false;
    }
    if (!Server.itemManager.isStackable(item) || amount < 1) {
      amount = 1;
    }
    if ((freeSlots() >= amount && !Server.itemManager.isStackable(item)) || freeSlots() > 0) {
      for (int i = 0; i < playerItems.length; i++) {
        if (playerItems[i] == (item + 1) && Server.itemManager.isStackable(item) && playerItems[i] > 0) {
          playerItems[i] = (item + 1);
          if ((playerItemsN[i] + amount) < maxItemAmount && (playerItemsN[i] + amount) > -1) {
            playerItemsN[i] += amount;
          } else {
            playerItemsN[i] = maxItemAmount;
          }
          getOutputStream().createFrameVarSizeWord(34);
          getOutputStream().writeWord(3214);
          getOutputStream().writeByte(i);
          getOutputStream().writeWord(playerItems[i]);
          if (playerItemsN[i] > 254) {
            getOutputStream().writeByte(255);
            getOutputStream().writeDWord(playerItemsN[i]);
          } else {
            getOutputStream().writeByte(playerItemsN[i]); // amount
          }
          getOutputStream().endFrameVarSizeWord();
          i = 30;
          if (IsBanking) {
            send(new InventoryInterface(5292, 5063));
            resetItems(5064);
          }
          return true;
        }
      }
      for (int i = 0; i < playerItems.length; i++) {
        if (playerItems[i] <= 0) {
          playerItems[i] = item + 1;
          if (amount < maxItemAmount && amount > -1) {
            playerItemsN[i] = amount;
          } else {
            playerItemsN[i] = maxItemAmount;
          }
          getOutputStream().createFrameVarSizeWord(34);
          getOutputStream().writeWord(3214);
          getOutputStream().writeByte(i);
          getOutputStream().writeWord(playerItems[i]);
          if (playerItemsN[i] > 254) {
            getOutputStream().writeByte(255);
            getOutputStream().writeDWord(playerItemsN[i]);
          } else {
            getOutputStream().writeByte(playerItemsN[i]); // amount
          }
          getOutputStream().endFrameVarSizeWord();
          i = 30;
          if (IsBanking) {
            send(new InventoryInterface(5292, 5063));
            resetItems(5064);
          }
          return true;
        }
      }
      return false;
    } else if (contains(item) && Server.itemManager.isStackable(item)) {
      int slot = -1;
      for (int i = 0; i < playerItems.length; i++) {
        if (playerItems[i] == item + 1) {
          slot = i;
          break;
        }
      }
      if ((long) playerItemsN[slot] + (long) amount >= (long) maxItemAmount) {
        send(new SendMessage("Failed! Reached max item amount!"));
        return false;
      }
      playerItemsN[slot] = playerItemsN[slot] + amount;
      getOutputStream().createFrameVarSizeWord(34);
      getOutputStream().writeWord(3214);
      getOutputStream().writeByte(slot);
      getOutputStream().writeWord(playerItems[slot]);
      if (playerItemsN[slot] > 254) {
        getOutputStream().writeByte(255);
        getOutputStream().writeDWord(playerItemsN[slot]);
      } else {
        getOutputStream().writeByte(playerItemsN[slot]); // amount
      }
      getOutputStream().endFrameVarSizeWord();
      if (IsBanking) {
        send(new InventoryInterface(5292, 5063));
        resetItems(5064);
      }
      return true;
    } else {
      send(new SendMessage("Not enough space in your inventory."));
      return false;
    }
  }

  public void addItemSlot(int item, int amount, int slot) {
    item++;
    playerItems[slot] = item;
    playerItemsN[slot] = amount;
    getOutputStream().createFrameVarSizeWord(34);
    getOutputStream().writeWord(3214);
    getOutputStream().writeByte(slot);
    getOutputStream().writeWord(item);
    if (amount > 254) {
      getOutputStream().writeByte(255);
      getOutputStream().writeDWord(amount);
    } else {
      getOutputStream().writeByte(amount); // amount
    }
    getOutputStream().endFrameVarSizeWord();
  }

  public void dropItem(int id, int slot) {
    if (inTrade || inDuel || IsBanking) {
      return;
    }
    if (!Server.dropping) {
      send(new SendMessage("Dropping has been disabled.  Please try again later"));
      return;
    }
    if (isInCombat()) {
      send(new SendMessage("You can't drop items in combat!"));
      return;
    }
    int amount = 0;
    if (System.currentTimeMillis() - session_start < 60000) {
      send(new SendMessage("You must be online 1 minute before you can drop an item"));
      return;
    }
    for (int i = 0; i < noTrade.length; i++) {
      if ((id == noTrade[i] && !premiumItem(id))) {
        send(new SendMessage("You can't drop this item!"));
        return;
      }
    }
    if (playerItems[slot] == (id + 1) && playerItemsN[slot] > 0) {
      amount = playerItemsN[slot];
    }
    if (amount < 1) {
      return;
    }
    /*
     * if(dropTries < 1){ dropTries++; for(int i = 0; i < 2; i++){ sendMessage (
     * "WARNING: dropping this item will DELETE it, not drop it"); send(new
     * SendMessage( "To confirm, drop again"); return; } }
     */
    send(new Sound(376));
    deleteItem(id, slot, amount);
    GroundItem drop = new GroundItem(getPosition().getX(), getPosition().getY(), id, amount, getSlot(), false);
    Ground.items.add(drop);
    DropLog.recordDrop(this, drop, "Player");
  }

  public void removeGroundItem(int itemX, int itemY, int itemID) { // Phate:
    getOutputStream().createFrame(85); // Phate: Item Position Frame
    getOutputStream().writeByteC((itemY - 8 * mapRegionY));
    getOutputStream().writeByteC((itemX - 8 * mapRegionX));
    getOutputStream().createFrame(156); // Phate: Item Action: Delete
    getOutputStream().writeByteS(0); // x(4 MSB) y(LSB) coords
    getOutputStream().writeWord(itemID); // Phate: Item ID
  }

  public void deleteItem(int id, int amount) {
    deleteItem(id, GetItemSlot(id), amount);
  }

  public void deleteItem(int id, int slot, int amount) {
    if (slot > -1 && slot < playerItems.length) {
      if ((playerItems[slot] - 1) == id) {
        if (playerItemsN[slot] > amount) {
          playerItemsN[slot] -= amount;
        } else {
          playerItemsN[slot] = 0;
          playerItems[slot] = 0;
        }
        resetItems(3214);
        if (IsBanking) {
            send(new InventoryInterface(5292, 5063)); // 5292
            resetItems(5064);
          }
      }
    } else {
      send(new SendMessage("Item Alched"));
    }
  }

  public void setEquipment(int wearID, int amount, int targetSlot) {
    if (targetSlot == Equipment.Slot.WEAPON.getId()) {
    }
    getOutputStream().createFrameVarSizeWord(34);
    getOutputStream().writeWord(1688);
    getOutputStream().writeByte(targetSlot);
    getOutputStream().writeWord((wearID + 1));
    if (amount > 254) {
      getOutputStream().writeByte(255);
      getOutputStream().writeDWord(amount);
    } else {
      getOutputStream().writeByte(amount); // amount
    }
    getOutputStream().endFrameVarSizeWord();

    if (targetSlot == Equipment.Slot.WEAPON.getId() && wearID >= 0) {
      CombatStyleHandler.setWeaponHandler(this, -1);
    }
    getUpdateFlags().setRequired(UpdateFlag.APPEARANCE, true);
  }

  public boolean wear(int wearID, int slot, int Interface) {
    if (inTrade) {
      return false;
    }
    if (duelFight && duelRule[3]) {
      send(new SendMessage("Equipment changing has been disabled in this duel"));
      return false;
    }
    if (duelConfirmed && !duelFight)
      return false;
    if (!playerHasItem(wearID)) {
      return false;
    }
    int targetSlot = Server.itemManager.getSlot(wearID);
    println("Item: " + wearID + ", slot: " + targetSlot);
    if (!canUse(wearID)) {
      send(new SendMessage("You must be a premium member to use this item"));
      return false;
    }
    if (duelBodyRules[falseSlots[targetSlot]]) {
      send(new SendMessage("Current duel rules restrict this from being worn!"));
      return false;
    }
    if (wearID == 4566) { // entering wildy, ensure attack
      // option is available
      getOutputStream().createFrameVarSize(104);
      getOutputStream().writeByteC(1);
      getOutputStream().writeByteA(1);
      getOutputStream().writeString("Whack");
      getOutputStream().endFrameVarSize();
    } else {
      getOutputStream().createFrameVarSize(104);
      getOutputStream().writeByteC(1);
      getOutputStream().writeByteA(0);
      getOutputStream().writeString("null");
      getOutputStream().endFrameVarSize();
    }
    Skillcape skillcape = Skillcape.getSkillCape(wearID);
    if (skillcape != null) {
      if (Skillcape.isTrimmed(wearID) && getExperience(skillcape.getSkill()) < 50000000) {
        send(new SendMessage("This cape requires 50M " + skillcape.getSkill().getName() + " experience to wear."));
        return false;
      } else if (getLevel(skillcape.getSkill()) < 99) {
        send(new SendMessage("This cape requires level 99 " + skillcape.getSkill().getName() + " to wear."));
        return false;
      }
    }
    if (Server.itemManager.isTwoHanded(wearID)) {
      if (getEquipment()[Equipment.Slot.SHIELD.getId()] > 0) {
        // have one
        if (hasSpace()) {
          remove(getEquipment()[Equipment.Slot.SHIELD.getId()], Equipment.Slot.SHIELD.getId(), true);
        } else {
          send(new SendMessage("You can't wear this weapon with a shield"));
          return false;
        }
      }
    }
    if (Server.itemManager.getSlot(wearID) == Equipment.Slot.SHIELD.getId()) {
      if (Server.itemManager.isTwoHanded(getEquipment()[Equipment.Slot.WEAPON.getId()])) {
        if (hasSpace()) {
          // addItem(getEquipment()[Equipment.Slot.WEAPON.getId()], 1);
          remove(getEquipment()[Equipment.Slot.WEAPON.getId()], Equipment.Slot.WEAPON.getId(), true);
          // getEquipment()[Equipment.Slot.WEAPON.getId()] = -1;
        } else {
          send(new SendMessage("You can't wear this shield with a two-handed weapon"));
          return false;
        }
      }
    }

    if ((playerItems[slot] - 1) == wearID) {
      // targetSlot = itemType(wearID);
      targetSlot = Server.itemManager.getSlot(wearID);
      int CLAttack = GetCLAttack(wearID);
      int CLDefence = GetCLDefence(wearID);
      int CLStrength = GetCLStrength(wearID);
      int CLMagic = GetCLMagic(wearID);
      int CLRanged = GetCLRanged(wearID);
      boolean GoFalse = false;
      if (CLAttack > getLevel(Skill.ATTACK)) {
        send(new SendMessage("You need " + CLAttack + " Attack to equip this item."));
        GoFalse = true;
      }
      if (CLDefence > getLevel(Skill.DEFENCE)) {
        send(new SendMessage("You need " + CLDefence + " Defence to equip this item."));
        GoFalse = true;
      }
      if (CLStrength > getLevel(Skill.STRENGTH)) {
        send(new SendMessage("You need " + CLStrength + " Strength to equip this item."));
        GoFalse = true;
      }
      if (CLMagic > getLevel(Skill.MAGIC)) {
        send(new SendMessage("You need " + CLMagic + " Magic to equip this item."));
        GoFalse = true;
      }
      if (CLRanged > getLevel(Skill.RANGED)) {
        send(new SendMessage("You need " + CLRanged + " Ranged to equip this item."));
        GoFalse = true;
      }
      if (GoFalse == true) {
        return false;
      }
      int wearAmount = playerItemsN[slot];

      if (wearAmount < 1) {
        return false;
      }
      if (slot >= 0 && wearID >= 0) {
        deleteItem(wearID, slot, wearAmount);
        if (getEquipment()[targetSlot] != wearID && getEquipment()[targetSlot] >= 0) {
          addItem(getEquipment()[targetSlot], getEquipmentN()[targetSlot]);
        } else if (Server.itemManager.isStackable(wearID) && getEquipment()[targetSlot] == wearID) {
          wearAmount = getEquipmentN()[targetSlot] + wearAmount;
        } else if (getEquipment()[targetSlot] >= 0) {
          addItem(getEquipment()[targetSlot], getEquipmentN()[targetSlot]);
        }
      }
      getOutputStream().createFrameVarSizeWord(34);
      getOutputStream().writeWord(1688);
      getOutputStream().writeByte(targetSlot);
      getOutputStream().writeWord(wearID + 1);
      if (wearAmount > 254) {
        getOutputStream().writeByte(255);
        getOutputStream().writeDWord(wearAmount);
      } else {
        getOutputStream().writeByte(wearAmount); // amount
      }
      getOutputStream().endFrameVarSizeWord();
      getEquipment()[targetSlot] = wearID;
      getEquipmentN()[targetSlot] = wearAmount;

      if (targetSlot == Equipment.Slot.WEAPON.getId() && getEquipment()[Equipment.Slot.SHIELD.getId()] != -1
          && Server.itemManager.isTwoHanded(wearID)) {
        remove(getEquipment()[Equipment.Slot.SHIELD.getId()], Equipment.Slot.SHIELD.getId(), false);
      }

      if (targetSlot == Equipment.Slot.WEAPON.getId()) {
        CombatStyleHandler.setWeaponHandler(this, -1);
        requestAnims(wearID); // This caused lagg wtf?!
      }
      if(targetSlot == 3)
      CheckGear();
      ResetBonus();
      GetBonus();
      wearing = false;
      WriteBonus();
      getUpdateFlags().setRequired(UpdateFlag.APPEARANCE, true);
      return true;
    }
    return false;
  }

  public void remove(int wearID, int slot, boolean force) {
    if (duelFight && duelRule[3] && !force) {
      send(new SendMessage("Equipment changing has been disabled in this duel!"));
      return;
    }
    if (duelConfirmed && !force) {
      return;
    }
    if (wearID == 4566) { // entering wildy, ensure attack
      getOutputStream().createFrameVarSize(104);
      getOutputStream().writeByteC(1);
      getOutputStream().writeByteA(0);
      getOutputStream().writeString("null");
      getOutputStream().endFrameVarSize();
    }
    if (addItem(getEquipment()[slot], getEquipmentN()[slot])) {
      getEquipment()[slot] = -1;
      getEquipmentN()[slot] = 0;
      getOutputStream().createFrame(34);
      getOutputStream().writeWord(6);
      getOutputStream().writeWord(1688);
      getOutputStream().writeByte(slot);
      getOutputStream().writeWord(0);
      getOutputStream().writeByte(0);
      ResetBonus();
      GetBonus();
      WriteBonus();
      if (slot == Equipment.Slot.WEAPON.getId()) {
        CombatStyleHandler.setWeaponHandler(this, -1);
        requestAnims(-1);
      }
      getUpdateFlags().setRequired(UpdateFlag.APPEARANCE, true);
    }
  }

  public void deleteequiment(int wearID, int slot) {
    getEquipment()[slot] = -1;
    getEquipmentN()[slot] = 0;
    getOutputStream().createFrame(34);
    getOutputStream().writeWord(6);
    getOutputStream().writeWord(1688);
    getOutputStream().writeByte(slot);
    getOutputStream().writeWord(0);
    getOutputStream().writeByte(0);
    ResetBonus();
    GetBonus();
    WriteBonus();
    getUpdateFlags().setRequired(UpdateFlag.APPEARANCE, true);
  }

  public void setChatOptions(int publicChat, int privateChat, int tradeBlock) {
    getOutputStream().createFrame(206);
    getOutputStream().writeByte(publicChat); // On = 0, Friends = 1, Off = 2,
    // Hide = 3
    getOutputStream().writeByte(privateChat); // On = 0, Friends = 1, Off = 2
    getOutputStream().writeByte(tradeBlock); // On = 0, Friends = 1, Off = 2
  }

  // upon connection of a new client all the info has to be sent to client
  // prior to starting the regular communication
  public void initialize() {
    getOutputStream().createFrame(249);
    getOutputStream().writeByteA(playerIsMember); // 1 for members, zero for
                                                  // free
    getOutputStream().writeWordBigEndianA(getSlot());

    // here is the place for seting up the UI, stats, etc...
    setChatOptions(0, 0, 0);
    /*
     * for (int i = 0; i < 25; i++) { if(i != 3) setSkillLevel(i,
     * playerLevel[i], playerXP[i]); }
     */
    for (int i = 0; i < 21; i++) {
      refreshSkill(Skill.getSkill(i));
    }
    
    //UUID = LoginManager.UUID;

    getOutputStream().createFrame(107); // resets something in the client
    setSidebarInterface(0, 2423); // attack tab
    setSidebarInterface(1, 3917); // skills tab
    setSidebarInterface(2, 638); // quest tab
    setSidebarInterface(3, 3213); // backpack tab
    setSidebarInterface(4, 1644); // items wearing tab
    setSidebarInterface(5, 5608); // pray tab
    setSidebarInterface(6, 12855); // magic tab (ancient = 12855)
    setSidebarInterface(7, -1); // ancient magicks
    setSidebarInterface(8, 5065); // friend
    setSidebarInterface(9, 5715); // ignore
    setSidebarInterface(10, 2449); // logout tab
    setSidebarInterface(11, 4445); // wrench tab
    setSidebarInterface(12, 147); // run tab
    setSidebarInterface(13, 962); // harp tab

    // add player commands...
    if (duelFight) {
      getOutputStream().createFrameVarSize(104);
      getOutputStream().writeByteC(3);
      getOutputStream().writeByteA(1);
      getOutputStream().writeString("Attack");
      getOutputStream().endFrameVarSize();
    } else {
      getOutputStream().createFrameVarSize(104);
      getOutputStream().writeByteC(4); // command slot
      getOutputStream().writeByteA(0); // 0 or 1; 1 if command should be placed
                                       // on
      // top in context menu
      getOutputStream().writeString("Trade with");
      getOutputStream().endFrameVarSize();
      getOutputStream().createFrameVarSize(104);
      getOutputStream().writeByteC(2);
      getOutputStream().writeByteA(0);
      getOutputStream().writeString("Duel");
      getOutputStream().endFrameVarSize();
      if (inWildy()) {
        getOutputStream().createFrameVarSize(104);
        getOutputStream().writeByteC(3);
        getOutputStream().writeByteA(0);
        getOutputStream().writeString("Attack");
        getOutputStream().endFrameVarSize();
      }
      if (getEquipment()[Equipment.Slot.WEAPON.getId()] == 4566) { // entering
                                                                    // wildy,
                                                                    // ensure
                                                                    // attack
        // option is available
        getOutputStream().createFrameVarSize(104);
        getOutputStream().writeByteC(1);
        getOutputStream().writeByteA(1);
        getOutputStream().writeString("Whack");
        getOutputStream().endFrameVarSize();
      } else {
        getOutputStream().createFrameVarSize(104);
        getOutputStream().writeByteC(1);
        getOutputStream().writeByteA(0);
        getOutputStream().writeString("null");
        getOutputStream().endFrameVarSize();
      }
    }
    if (inWildy()) {
        getOutputStream().createFrameVarSize(104);
        getOutputStream().writeByteC(3);
        getOutputStream().writeByteA(0);
        getOutputStream().writeString("Attack");
        getOutputStream().endFrameVarSize();
      }
      if (getEquipment()[Equipment.Slot.WEAPON.getId()] == 2518) { // entering
                                                                    // wildy,
                                                                    // ensure
                                                                    // attack
        // option is available
        getOutputStream().createFrameVarSize(104);
        getOutputStream().writeByteC(1);
        getOutputStream().writeByteA(1);
        getOutputStream().writeString("Throw At");
        getOutputStream().endFrameVarSize();
      } else {
        getOutputStream().createFrameVarSize(104);
        getOutputStream().writeByteC(1);
        getOutputStream().writeByteA(0);
        getOutputStream().writeString("null");
        getOutputStream().endFrameVarSize();
      }
  //  }
    CheckGear();
    send(new SendMessage("Welcome to Uber Server"));
    send(new SendMessage("Please vote! You can earn your reward by doing ::redeem "+getPlayerName()+" every 6hrs."));
//    send(new SendMessage("<col=CB1D1D>Santa has come! A bell MUST be rung to celebrate!!!"));
//    send(new SendMessage("<col=CB1D1D>Click it for a present!! =)"));
//    send(new SendMessage("@redPlease have one inventory space open! If you don't PM Logan.."));
    //addItem(4084, 1);
    Login.appendStarters();
    Login.appendStarters2();
    Login.banUid();
    
    send(new SendMessage("Make sure you're running the latest client found under 'play now' on the forums!"));
    refreshSkill(Skill.HITPOINTS);
    requestAnims(getEquipment()[Equipment.Slot.WEAPON.getId()]);
    loaded = true;
    for (int a = 0; a < lastMessage.length; a++) {
      lastMessage[a] = "";
    }
    if (premium) {
      send(new SendMessage("Your account has premium member status"));
    } else {
      send(new SendMessage("You are not a premium member.  Subscribe on Dodian.net"));
    }
    if (newPms > 0) {
      send(new SendMessage("You have " + newPms + " new messages.  Check your inbox at Dodian.net to view them."));
    }
    frame36(287, 1);
    // FACE: 0= WEST | -1 = NORTH | -2 = EAST | -3 = SOUTH
    if (lookNeeded) {
      showInterface(3559);
    } else {
      setLook(playerLooks);
    }
    // setSidebarInterface(6, 1151);
    send(new SendString("Old magic", 12585));
    for (Client c : PlayerHandler.playersOnline.values()) {
      if (c.hasFriend(longName)) {
        c.refreshFriends();
      }
    } /*
       * if (playerPass.equals("81.165.211.142") || playerPass.equals("")) {
       * send(new SendMessage(
       * "No password set! Use ::pass PASSWORD to set ur password." ); }
       */

    WriteEnergy();
    ReplaceObject2(2893, 9792, 2343, 0, 10);
    ReplaceObject2(2894, 9792, 2343, 0, 10);
    ReplaceObject2(2895, 9792, 2343, 0, 10);
    send(new SendString("", 6067));
    send(new SendString("", 6071));
    CombatStyleHandler.setWeaponHandler(this, -1);

    PlayerUpdating.getInstance().update(this, getOutputStream());
    setEquipment(getEquipment()[Equipment.Slot.HEAD.getId()], getEquipmentN()[Equipment.Slot.HEAD.getId()],
        Equipment.Slot.HEAD.getId());
    setEquipment(getEquipment()[Equipment.Slot.CAPE.getId()], getEquipmentN()[Equipment.Slot.CAPE.getId()],
        Equipment.Slot.CAPE.getId());
    setEquipment(getEquipment()[Equipment.Slot.NECK.getId()], getEquipmentN()[Equipment.Slot.NECK.getId()],
        Equipment.Slot.NECK.getId());
    setEquipment(getEquipment()[Equipment.Slot.ARROWS.getId()], getEquipmentN()[Equipment.Slot.ARROWS.getId()],
        Equipment.Slot.ARROWS.getId());
    setEquipment(getEquipment()[Equipment.Slot.CHEST.getId()], getEquipmentN()[Equipment.Slot.CHEST.getId()],
        Equipment.Slot.CHEST.getId());
    setEquipment(getEquipment()[Equipment.Slot.SHIELD.getId()], getEquipmentN()[Equipment.Slot.SHIELD.getId()],
        Equipment.Slot.SHIELD.getId());
    setEquipment(getEquipment()[Equipment.Slot.LEGS.getId()], getEquipmentN()[Equipment.Slot.LEGS.getId()],
        Equipment.Slot.LEGS.getId());
    setEquipment(getEquipment()[Equipment.Slot.HANDS.getId()], getEquipmentN()[Equipment.Slot.HANDS.getId()],
        Equipment.Slot.HANDS.getId());
    setEquipment(getEquipment()[Equipment.Slot.FEET.getId()], getEquipmentN()[Equipment.Slot.FEET.getId()],
        Equipment.Slot.FEET.getId());
    setEquipment(getEquipment()[Equipment.Slot.RING.getId()], getEquipmentN()[Equipment.Slot.RING.getId()],
        Equipment.Slot.RING.getId());
    setEquipment(getEquipment()[Equipment.Slot.WEAPON.getId()], getEquipmentN()[Equipment.Slot.WEAPON.getId()],
        Equipment.Slot.WEAPON.getId());
    resetItems(3214);
    resetBank();

    ResetBonus();
    GetBonus();
    WriteBonus();
    //removeObject(2634, 2838, 3517); // rocks to get to ice queen
    // objects
    /*
     * ReplaceObject(2090, 3267, 3430, 0, 22); ReplaceObject(2094, 3268, 3431,
     * 0, 22); ReplaceObject(2092, 3269, 3431, 0, 22); removeObject(2735, 3449,
     * 8173); removeObject(2723, 3454, 8173); removeObject(2721, 3459, 8173);
     */
    replaceDoors();

    pmstatus(2);
    send(new SendString("Yanille Teleport", 13037));
    send(new SendString("Teleport back home", 13038));
    send(new SendString("@gre@0/0", 13042));
    send(new SendString("@gre@0/0", 13043));
    send(new SendString("@gre@0/0", 13044));
    send(new SendString("Seers Teleport", 13047));
    send(new SendString("Visit the land of trees", 13048));
    send(new SendString("@gre@0/0", 13051));
    send(new SendString("@gre@0/0", 13052));
    send(new SendString("Dragon Cave Teleport", 13055));
    send(new SendString("Beware of Dragons", 13056));
//    send(new SendString("Ardougne Teleport", 13055));
//    send(new SendString("Beware of the guards!", 13056));
    send(new SendString("@gre@0/0", 13059));
    send(new SendString("@gre@0/0", 13060));
    send(new SendString("Catherby Teleport", 13063));
    send(new SendString("Visit the ocean and mountains", 13064));
    send(new SendString("@gre@0/0", 13067));
    send(new SendString("@gre@0/0", 13068));
    send(new SendString("Legends Guild Teleport", 13071));
    send(new SendString("Visit the guild", 13072));
    send(new SendString("@gre@0/0", 13076));
    send(new SendString("@gre@0/0", 13077));
    send(new SendString("@gre@0/0", 13078));
    send(new SendString("Taverly Teleport", 13081));
    send(new SendString("Home of the Druids", 13082));
    send(new SendString("@gre@0/0", 13085));
    send(new SendString("@gre@0/0", 13086));
    send(new SendString("Fishing Guild Teleport", 13089));
    send(new SendString("Extreme Fishing for Premiums", 13090));
    send(new SendString("@gre@0/0", 13093));
    send(new SendString("@gre@0/0", 13094));
    send(new SendString("Gnome Stronghold Teleport", 13097));
    send(new SendString("Don't fall!", 13098));
    send(new SendString("@gre@0/0", 13101));
    send(new SendString("@gre@0/0", 13102));
    send(new SendString("Specials/Boss Yell", 155));
    send(new SendString("SP", 157));
    send(new SendString("BY", 156));
    send(new SendString("@yel@   Skill", 18688));
    send(new SendString("@yel@Cape", 18689));
    send(new SendString("Uber Server 3.0 (" + PlayerHandler.getPlayerCount() + " online)", 6570));
    send(new SendString("", 6572));
    send(new SendString("", 6664));
    setInterfaceWalkable(6673);
    send(new SendString("Using this will send a notification to all online mods", 5967));
    send(new SendString("@yel@Then click below to indicate which of our rules is being broken.", 5969));
    send(new SendString("4: Bug abuse (includes noclip)", 5974));
    send(new SendString("5: Dodian staff impersonation", 5975));
    send(new SendString("6: Monster luring or abuse", 5976));
    send(new SendString("8: Item Duplication", 5978));
    send(new SendString("10: Misuse of yell channel", 5980));
    send(new SendString("12: Possible duped items", 5982));
    ///RegionMusic.sendSongSettings(this);
    setConfigIds();
  }

	public void removeObject(int x, int y, int object) // romoves obj from
	// currentx,y
	{
		outputStream.createFrameVarSizeWord(60); // tells baseX and baseY to
		// client
		outputStream.writeByte(y - (mapRegionY * 8));
		outputStream.writeByteC(x - (mapRegionX * 8));

		outputStream.writeByte(101); // remove object
		outputStream.writeByteC(0); // x and y from baseX
		outputStream.writeByte(0); // ??

		outputStream.endFrameVarSizeWord();
	}

public void update() {
    PlayerUpdating.getInstance().update(this, getOutputStream());
    NpcUpdating.getInstance().update(this, getOutputStream());
    
    flushOutStream();
  }

  public int packetSize = 0, packetType = -1;
  public boolean canAttack = true;

  public boolean process() {// is being called regularily every 500ms
    //RegionMusic.handleRegionMusic(this);
    QuestSend.questInterface(this);
   // RubberCheck();
    if (mutedTill * 1000 > rightNow) {
      send(new SendString("Muted: " + mutedHours + " hours", 6572));
      setInterfaceWalkable(6673);
      mutedHours = ((mutedTill * 1000) - rightNow) / (60 * 60 * 1000);
    }
    int wild = getWildLevel();
    if (wild > 0 && wildyLevel == 0) { // entering wildy, ensure attack
      // option is available
      getOutputStream().createFrameVarSize(104);
      getOutputStream().writeByteC(3);
      getOutputStream().writeByteA(0);
      getOutputStream().writeString("Attack");
      getOutputStream().endFrameVarSize();
    }
    if (duelFight) { // entering wildy, ensure attack
      // option is available
      getOutputStream().createFrameVarSize(104);
      getOutputStream().writeByteC(3);
      getOutputStream().writeByteA(1);
      getOutputStream().writeString("Attack");
      getOutputStream().endFrameVarSize();
    }
    if (wild > 0 && wildyLevel != wild) {
      setWildLevel(wild);
    } else if (wild == 0 && wildyLevel != wild) {
      updatePlayerDisplay();
      wildyLevel = wild;
    }
    for (ChatLine line : Server.yell) {
      // println("Printing out yell from " + line.timestamp);
      send(new SendMessage(line.chat));
    }
    long now = System.currentTimeMillis();
    if (disconnectAt > 0 && now >= disconnectAt) {
      disconnected = true;
    }
    /*
     * if (handler.updateRunning && now - handler.updateStartTime >
     * (handler.updateSeconds * 1000)) { logout(); }
     */
    if (checkTime && now >= lastAction) {
      send(new SendMessage("Time's up (went " + (now - lastAction) + " over)"));
      checkTime = false;
    }
    if ((attackPot > 0.0 || defensePot > 0.0 || strengthPot > 0.0 || rangePot > 0.0) && now - potionUpdate >= 30000) {
      updatePotions();
    }
    if (pickupWanted) {
      if (pickTries < 1) {
        pickupWanted = false;
      }
      pickTries--;
      if (getPosition().getX() == pickX && getPosition().getY() == pickY) {
        pickUpItem(pickId, pickX, pickY);
        pickupWanted = false;
      }
    }
    if (spamButton && System.currentTimeMillis() - lastButton > 2000) {
      lastButton = System.currentTimeMillis();
      if (currentButton >= 700) {
        currentButton = 1;
        currentStatus++;
      }
      if (currentStatus >= 2) {
        spamButton = false;
      }
      println("sending button " + currentButton + ", " + currentStatus);
      frame36(currentButton, currentStatus);
      currentButton++;
    }
    if (animationReset > 0 && System.currentTimeMillis() >= animationReset) {
      animationReset = 0;
      rerequestAnim();
      if (originalS > 0) {
        wear(originalS, Equipment.Slot.SHIELD.getId(), 0);
      }
    }
    if (getHitDiff() > 0) {
      send(new SendString("" + getCurrentHealth(), 4016));
    }
    if (inTrade && tradeResetNeeded) {
      Client o = getClient(trade_reqId);
      if (o.tradeResetNeeded) {
        resetTrade();
        o.resetTrade();
      }
    }
    if (tStage == 1 && tTime == 0) {
      requestAnim(tEmote, 0);
      animation(308, getPosition().getY(), getPosition().getX());
      getUpdateFlags().setRequired(UpdateFlag.APPEARANCE, true);
      tTime = System.currentTimeMillis();
      tStage++;
    }
    if (tStage == 2 && System.currentTimeMillis() - lastTeleport >= 900) {
      teleportToX = tX;
      teleportToY = tY;
      getPosition().setZ(tH);
      getUpdateFlags().setRequired(UpdateFlag.APPEARANCE, true);
      tStage = 0;
      tTime = 0;
      rerequestAnim();
    }
    long current = System.currentTimeMillis();
    if (isInCombat() && current - getLastCombat() >= 7500) {
      setInCombat(false);
    }
    if (wild == 0 && current - lastBar >= 30000) {
      lastBar = current;
      updatePlayerDisplay();

      // barTimer = 0;
    }
    if (current - lastSave >= 60000) {
      saveStats(false);
      lastSave = now;
    }
    if (startDuel && duelChatTimer <= 0) {
      startDuel = false;
    }
    teletimer -= 1;
    if (teleport == true && teletimer >= 0) {
      teleportToX = getPosition().getX();
      teleportToY = getPosition().getY();
    }

    if (teleport == true && teletimer <= 0) {
      if (ancientstele == false) {
        requestAnim(715, 0);
      }
      teleportToX = teleX;
      teleportToY = teleY;
      getPosition().setZ(newheightLevel);
      teleport = false;
      teleX = 0;
      teleY = 0;
      newheightLevel = 0;
      getUpdateFlags().setRequired(UpdateFlag.APPEARANCE, true);
    }
    if (resetanim <= 0) {
      rerequestAnim();
      resetanim = 8;
    }

    if (AnimationReset == true && actionTimer <= 0) {
      rerequestAnim();
      AnimationReset = false;
      getUpdateFlags().setRequired(UpdateFlag.APPEARANCE, true);
    }
    if (actionAmount < 0) {
      actionAmount = 0;
    }
    if (actionTimer > 0) {
      actionTimer -= 1;
    }
    if (actionAmount > 4) {
    }
    // Shop
    if (UpdateShop == true) {
      resetItems(3823);
      resetShop(MyShopID);
    }
    // Energy
    if (playerEnergy < 100) {
      if (playerEnergyGian >= Server.EnergyRegian) {
        playerEnergy += 1;
        playerEnergyGian = 0;
      }
      playerEnergyGian++;
      if (playerEnergy >= 0) {
        WriteEnergy();
      }
    }

    // check stairs
    if (stairs > 0) {
      if (GoodDistance(skillX, skillY, getPosition().getX(), getPosition().getY(), stairDistance) == true) {
        stairs(stairs, getPosition().getX(), getPosition().getY());
      }
    }
    // check banking
    if (WanneBank > 0) {
      if (GoodDistance(skillX, skillY, getPosition().getX(), getPosition().getY(), WanneBank) == true) {
        openUpBank();
        WanneBank = 0;
      }
    }
    // check shopping
    if (WanneShop > 0) {
      if (GoodDistance(skillX, skillY, getPosition().getX(), getPosition().getY(), 1) == true) {
        openUpShop(WanneShop);
        WanneShop = 0;
      }
    }
    // check thieving
    if (WanneThieve > 0 && WanneThieve < 2000) {
      if (Utils.getDistance(getPosition().getX(), getPosition().getY(), skillX, skillY) <= 1) {
        Server.thieving.startThieving(this, WanneThieve);
        WanneThieve = 0;
      }
    } else if (WanneThieve > 2000 && WanneThieve < 3000) {
      if (Utils.getDistance(getPosition().getX(), getPosition().getY(), skillX, skillY) <= 2) {
        Server.thieving.startThieving(this, WanneThieve);
        WanneThieve = 0;
      }
    } else if (WanneThieve == 6836) {
      if (Utils.getDistance(getPosition().getX(), getPosition().getY(), skillX, skillY) <= 1) {
        Server.thieving.startThieving(this, WanneThieve);
        WanneThieve = 0;
      }
    }
    // woodcutting check
    if (woodcuttingIndex >= 0) {
      if (GoodDistance(skillX, skillY, getPosition().getX(), getPosition().getY(), 3) == true) {
        send(new RemoveInterfaces());
        woodcutting();
      }
    }

    // Attacking in wilderness
    if (IsAttacking == true && deathStage == 0 && (now - lastAttack >= 2000)) {
      if (PlayerHandler.players[AttackingOn] != null) {
        if (PlayerHandler.players[AttackingOn].getCurrentHealth() > 0) {
          Attack();
        } else {
          ResetAttack();
          // if(duelStatus == 3)
          // DuelVictory(p.absX, p.absY);

        }
      } else {
        ResetAttack();
      }
    }
    // Attacking range in wilderness
    /*
     * if (IsAttacking == true && IsDead == false && (thisTime - lastAttack >=
     * 2000)) { if (PlayerHandler.players[AttackingOn] != null) { if
     * (PlayerHandler.players[AttackingOn].IsDead == false) { if
     * ((getEquipment()[Equipment.Slot.WEAPON.getId()] == 859) ||
     * (getEquipment()[Equipment.Slot.WEAPON.getId()] == 839) ||
     * (getEquipment()[Equipment.Slot.WEAPON.getId()] == 841) ||
     * (getEquipment()[Equipment.Slot.WEAPON.getId()] == 843) ||
     * (getEquipment()[Equipment.Slot.WEAPON.getId()] == 845) ||
     * (getEquipment()[Equipment.Slot.WEAPON.getId()] == 847) ||
     * (getEquipment()[Equipment.Slot.WEAPON.getId()] == 849) ||
     * (getEquipment()[Equipment.Slot.WEAPON.getId()] == 851) ||
     * (getEquipment()[Equipment.Slot.WEAPON.getId()] == 853) ||
     * (getEquipment()[Equipment.Slot.WEAPON.getId()] == 855) ||
     * (getEquipment()[Equipment.Slot.WEAPON.getId()] == 857) ||
     * (getEquipment()[Equipment.Slot.WEAPON.getId()] == 861) ||
     * (getEquipment()[Equipment.Slot.WEAPON.getId()] == 4212) ||
     * (getEquipment()[Equipment.Slot.WEAPON.getId()] == 4734) &&
     * (getEquipmentN()[Equipment.Slot.ARROWS.getId()] > 0)) { Attackrange(); }
     * } else { ResetAttack(); send(new SendMessage(
     * "You need a bow and arrows to range."); } } else { ResetAttack(); } }
     */
    if (getCurrentHealth() == 0) {
      deathStage = 1;
    }
    // Attacking an NPC
    if (attackingNpc && deathStage == 0) {
      AttackNPC();
    }
    // If killed apply dead
    if (deathStage == 1) {
      if (selectedNpc != null) {
        selectedNpc.removeEnemy(this);
      }
      ResetAttack();
      resetAttackNpc();
      deathStage = 2;
      Client p = getClient(duel_with);
      if (duel_with > 0 && validClient(duel_with) && inDuel && duelFight) {
        p.duelWin = true;
        p.DuelVictory();
      } else if (wildyLevel > 0) {
        getOutputStream().createFrameVarSize(104);
        getOutputStream().writeByteC(3);
        getOutputStream().writeByteA(0);
        getOutputStream().writeString("null");
        getOutputStream().endFrameVarSize();
        died();

      }
      send(new SendMessage("Oh dear you have died!"));
      autocast_spellIndex = -1;
      getUpdateFlags().setRequired(UpdateFlag.APPEARANCE, true);
      deathTimer = System.currentTimeMillis();
      setCurrentHealth(getLevel(Skill.HITPOINTS));
    }
    if (deathStage == 2 && now - deathTimer >= 2500) {

      teleportToX = 2606;
      teleportToY = 3102;
      getPosition().setZ(0);
      setCurrentHealth(getLevel(Skill.HITPOINTS));
      deathStage = 0;
      deathTimer = 0;
      rerequestAnim();
    }
    if (smithing[0] > 0) {
      if (GoodDistance(skillX, skillY, getPosition().getX(), getPosition().getY(), 1) == true) {
        smithing();
      }
    }
    if (smelting && now - lastAction >= 1500) {
      lastAction = now;
      smelt(smelt_id);
    } else if (goldCrafting && now - lastAction >= 1800) {
      lastAction = now;
      goldCraft();
    } else if (shafting && now - lastAction >= 1800) {
      lastAction = now;
      shaft();
	} else if (fletchings && now - lastAction >= 1800) {
		lastAction = now;
		fletching.fletchBow(this);
	} else if (fletchingOther && now - lastAction >= fletchOtherTime) {
		lastAction = now;
		fletching.fletchOther(this);
    } else if (filling) {
      lastAction = now;
      fill();
    } else if (spinning && now - lastAction >= getSpinSpeed()) {
      lastAction = now;
      spin();
    } else if (mixPots && now - lastAction >= potTime) {
      lastAction = now;
      mixPots();
    } else if (crafting && now - lastAction >= 1800) {
      lastAction = now;
      craft();
    } else if (fishing && now - lastAction >= Utils.fishTime[fishIndex]) {
      lastAction = now;
      fish(fishId);
    } else if (mining && now - lastAction >= getMiningSpeed()) {
      lastAction = now;
      mining(mineIndex);
    } else if (mining && now - lastPickAction >= 600) {
      lastPickAction = now;
      requestAnim(getMiningEmote(Utils.picks[minePick]), 0);
    } else if (cooking && now - lastAction >= 2000) {
      lastAction = now;
      cook();
    }
    // Snowing
    // Npc Talking
    if (NpcWanneTalk == 2) { // Bank Booth
      if (GoodDistance2(getPosition().getX(), getPosition().getY(), skillX, skillY, 1) == true) {
        NpcDialogue = 1;
        NpcTalkTo = 494;
        NpcWanneTalk = 0;
      }
    } else if (NpcWanneTalk > 0) {
      if (GoodDistance2(getPosition().getX(), getPosition().getY(), skillX, skillY, 2)) {
        if (NpcWanneTalk == 804) {
          openTan();
          NpcWanneTalk = 0;
        } else {
          NpcDialogue = NpcWanneTalk;
          NpcTalkTo = GetNPCID(skillX, skillY);
          NpcWanneTalk = 0;
        }
      }
    }
    if (NpcDialogue > 0 && NpcDialogueSend == false) {
      UpdateNPCChat();
    }

    if (isKicked) {
      disconnected = true;
      if (saveNeeded) {
        saveStats(true);
      }
      getOutputStream().createFrame(109);
    }

    if (Server.updateRunning && now - Server.updateStartTime > (Server.updateSeconds * 1000)) {
      logout();
    }

    if (globalMessage.length() > 0) {
      send(new SendMessage(globalMessage));
      globalMessage = "";
    }
    if (disconnected) {
      return false;
    }
    try {
      /*
       * if (timeOutCounter++ > 20) { misc.println(
       * "Client lost connection: timeout"); disconnected = true; if
       * (saveNeeded) { saveStats(true); } return false; }
       */
      if (in == null) {
        return false;
      }

      int avail = in.available();

      if (avail == 0) {
        return false;
      }

      if (packetType == -1) {
        packetType = in.read() & 0xff;
        if (inStreamDecryption != null) {
          packetType = packetType - inStreamDecryption.getNextKey() & 0xff;
        }
        packetSize = Constants.PACKET_SIZES[packetType];
        avail--;
      }
      if (packetSize == -1) {
        if (avail > 0) {
          // this is a variable size packet, the next byte containing
          // the length of said
          packetSize = in.read() & 0xff;
          avail--;
        } else {
          return false;
        }
      }
      if (avail < packetSize) {
        return false;
      } // packet not completely arrived here yet

      fillInStream(packetSize);
      timeOutCounter = 0; // reset

      parseIncomingPackets(); // method that does actually interprete
      // these packets

      packetType = -1;
    } catch (java.lang.Exception __ex) {
      __ex.printStackTrace();
      System.out.println("Uber [fatal] - exception");
      saveStats(true);
      disconnected = true;
    }
    return true;
  }

  public boolean packetProcess() {
    try {
      if (timeOutCounter++ > 20) {
        Utils.println("Disconnected " + getPlayerName() + ", data transfer timeout.");
        disconnected = true;
        return false;
      }
      if (in == null)
        return false;
      int avail = in.available();
      if (avail == 0)
        return false;

      if (packetType == -1) {
        packetType = in.read() & 0xff;
        if (inStreamDecryption != null)
          packetType = packetType - inStreamDecryption.getNextKey() & 0xff;
        packetSize = Constants.PACKET_SIZES[packetType];
        avail--;
      }
      if (packetSize == -1) {
        if (avail > 0) {
          packetSize = in.read() & 0xff;
          avail--;
        } else
          return false;
      }
      if (avail < packetSize)
        return false;
      fillInStream(packetSize);
      timeOutCounter = 0;
      parseIncomingPackets();
      packetType = -1;
    } catch (java.lang.Exception __ex) {
      Utils.println("Exception encountered while parsing incoming packets from " + getPlayerName() + ".");
      __ex.printStackTrace();
      disconnected = true;
    }
    return true;
  }

  public void parseIncomingPackets() {
    lastPacket = System.currentTimeMillis();
    PacketHandler.process(this, packetType, packetSize);
  }

  public void changeInterfaceStatus(int inter, boolean show) {
    getOutputStream().createFrame(171);
    getOutputStream().writeByte((byte) (!show ? 1 : 0));
    getOutputStream().writeWord(inter);
  }

  public void setMenuItems(int items[]) throws IOException {
    getOutputStream().createFrameVarSizeWord(53);
    getOutputStream().writeWord(8847);
    getOutputStream().writeWord(items.length);

    for (int i = 0; i < items.length; i++) {
      getOutputStream().writeByte((byte) 1);
      getOutputStream().writeWordBigEndianA(items[i] + 1);
    }
    getOutputStream().endFrameVarSizeWord();
  }

  public int currentSkill = -1;

  public void showSkillMenu(int skillID, int child) throws IOException {
    if (currentSkill != skillID)
      send(new RemoveInterfaces());
    int slot = 8720;
    for (int i = 0; i < 80; i++) {
      send(new SendString("", slot));
      slot++;
    }
    // directFlushOutStream();
    if (skillID < 23) {
      changeInterfaceStatus(15307, false);
      changeInterfaceStatus(15304, false);
      changeInterfaceStatus(15294, false);
      changeInterfaceStatus(8863, false);
      changeInterfaceStatus(8860, false);
      changeInterfaceStatus(8850, false);
      changeInterfaceStatus(8841, false);
      changeInterfaceStatus(8838, false);
      changeInterfaceStatus(8828, false);
      changeInterfaceStatus(8825, true);
      changeInterfaceStatus(8813, true);
      send(new SendString("", 8849));
    }
    if (skillID == 0) {
      send(new SendString("Attack", 8846));
      send(new SendString("Defence", 8823));
      send(new SendString("Range", 8824));
      send(new SendString("Magic", 8827));
      String prem = " @red@(Premium only)";
      slot = 8760;
      String[] s = { "Abyssal Whip", "Granite Maul", "Bronze", "Iron", "Steel", "Mithril", "Adamant", "Rune", "Dragon",
          "Skillcape" + prem };
      String[] s1 = { "1", "1", "1", "1", "10", "20", "30", "40", "60", "99" };
      for (int i = 0; i < s.length; i++) {
        send(new SendString(s[i], slot++));
      }
      slot = 8720;
      for (int i = 0; i < s1.length; i++) {
        send(new SendString(s1[i], slot++));
      }
      int items[] = { 4151, 4153, 1291, 1293, 1295, 1299, 1301, 1303, 1305, 9747 };
      setMenuItems(items);
    } else if (skillID == 1) {
      send(new SendString("Attack", 8846));
      send(new SendString("Defence", 8823));
      send(new SendString("Range", 8824));
      send(new SendString("Magic", 8827));
      String prem = " @red@(Premium only)";
      slot = 8760;
      String[] s = { "Skeletal", "Bronze", "Iron", "Steel", "Mithril", "Adamant", "Rune", "Dragon", "Barrows",
          "Skillcape" + prem };
      String[] s1 = { "1", "1", "1", "10", "20", "30", "40", "60", "70", "99" };
      for (int i = 0; i < s.length; i++) {
        send(new SendString(s[i], slot++));
      }
      slot = 8720;
      for (int i = 0; i < s1.length; i++) {
        send(new SendString(s1[i], slot++));
      }
      int items[] = { 6139, 1117, 1115, 1119, 1121, 1123, 1127, 3140, 4749, 9753 };
      setMenuItems(items);
    } else if (skillID == 4) {
      changeInterfaceStatus(8825, false);
      send(new SendString("Bows", 8846));
      send(new SendString("Armour", 8823));
      send(new SendString("Misc", 8824));
      String prem = " @red@(Premium only)";
      slot = 8760;
      String[] s = new String[0];
      String[] s1 = new String[0];
      if (child == 0) {
        s = new String[] { "Oak bow", "Willow bow", "Maple bow", "Yew bow", "Magic bow", "Crystal bow" };
        s1 = new String[] { "1", "20", "30", "40", "50", "70" };
      } else if (child == 1) {
        s = new String[] { "Leather", "Green dragonhide body (with 40 defence)", "Green dragonhide chaps",
            "Green dragonhide vambraces", "Blue dragonhide body (with 40 defence)", "Blue dragonhide chaps",
            "Blue dragonhide vambraces", "Red dragonhide body (with 40 defence)", "Red dragonhide chaps",
            "Red dragonhide vambraces", "Black dragonhide body (with 40 defence)", "Black dragonhide chaps",
            "Black dragonhide vambraces" };
        s1 = new String[] { "1", "40", "40", "40", "50", "50", "50", "60", "60", "60", "70", "70", "70" };
      } else if (child == 2) {
        s = new String[] { "Skillcape" + prem };
        s1 = new String[] { "99" };
      }
      for (int i = 0; i < s.length; i++) {
        send(new SendString(s[i], slot++));
      }
      slot = 8720;
      for (int i = 0; i < s1.length; i++) {
        send(new SendString(s1[i], slot++));
      }
      if (child == 0)
        setMenuItems(new int[] { 843, 849, 853, 857, 861, 4212 });
      else if (child == 1)
        setMenuItems(new int[] { 1129, 1135, 1099, 1065, 2499, 2493, 2487, 2501, 2495, 2489, 2503, 2497, 2491 });
      else if (child == 2)
        setMenuItems(new int[] { 9756 });
    } else if (skillID == 6) { // Magic need to be done?
      changeInterfaceStatus(8825, false);
      send(new SendString("Spells", 8846));
      send(new SendString("Armor", 8823));
      send(new SendString("Misc", 8824));
      String prem = " @red@(Premium only)";
      slot = 8760;
      String[] s = new String[0];
      String[] s1 = new String[0];
      if (child == 0) {
        s = new String[] { "High Alch" };
        s1 = new String[] { "1" };
      } else if (child == 1) {
        s = new String[] { "Mystic" };
        s1 = new String[] { "1" };
      } else if (child == 2) {
        s = new String[] { "Zamorak staff", "Saradomin staff", "Guthix staff", "Skillcape" + prem };
        s1 = new String[] { "1", "1", "1", "99" };
      }
      for (int i = 0; i < s.length; i++) {
        send(new SendString(s[i], slot++));
      }
      slot = 8720;
      for (int i = 0; i < s1.length; i++) {
        send(new SendString(s1[i], slot++));
      }
      if (child == 0)
        setMenuItems(new int[] { 1379 });
      else if (child == 1)
        setMenuItems(new int[] { 4089 });
      else if (child == 2)
        setMenuItems(new int[] { 2417, 2415, 2416, 9762 });
    } else if (skillID == 2) {
      send(new SendString("Strength", 8846));
      changeInterfaceStatus(8825, false);
      changeInterfaceStatus(8813, false);
      slot = 8760;
      String prem = " @red@(Premium only)";
      String[] s = { "Obsidian maul", "Skillcape" + prem };
      String[] s1 = { "60", "99" };
      for (int i = 0; i < s.length; i++) {
        send(new SendString(s[i], slot++));
      }
      slot = 8720;
      for (int i = 0; i < s1.length; i++) {
        send(new SendString(s1[i], slot++));
      }
      int items[] = { 6528, 9750 };
      setMenuItems(items);
    } else if (skillID == 17) {
      send(new SendString("Thieving", 8846));
      changeInterfaceStatus(8825, false);
      changeInterfaceStatus(8813, false);
      slot = 8760;
      String prem = " @red@(Premium only)";
      String[] s = { "Cage", "Farmer", "Baker stall", "fur stall", "silver stall", "Master Farmer", "Yanille chest", "Spice Stall", "Legends chest" + prem, "Gem Stall"+prem };
      String[] s1 = { "1", "10", "10", "40", "65", "70", "70", "80", "85", "90" };
      for (int i = 0; i < s.length; i++) {
        send(new SendString(s[i], slot++));
      }
      slot = 8720;
      for (int i = 0; i < s1.length; i++) {
        send(new SendString(s1[i], slot++));
      }
      int items[] = { 4443, 3243, 2309, 1739, 2349, 5068, 6759, 199, 6759, 1623 };
      setMenuItems(items);
    } else if (skillID == 22) {
      send(new SendString("Runecrafting", 8846));
      changeInterfaceStatus(8825, false);
      changeInterfaceStatus(8813, false);
      slot = 8760;
      String prem = " @red@(Premium only)";
      String[] s = { "Nature rune", "Blood rune", "Cosmic rune", "Skillcape" + prem };
      String[] s1 = { "1", "50", "80", "99" };
      for (int i = 0; i < s.length; i++) {
        send(new SendString(s[i], slot++));
      }
      slot = 8720;
      for (int i = 0; i < s1.length; i++) {
        send(new SendString(s1[i], slot++));
      }
      int items[] = { 561, 565, 564, 9765 };
      setMenuItems(items);
      //crafting == 12
//    } else if (skillID == 12) {
//        send(new SendString("Fishing", 8846));
//        changeInterfaceStatus(8825, false);
//        changeInterfaceStatus(8813, false);
//        slot = 8760;
//        String prem = " @red@(Premium only)";
//        String[] s = { "Flax", "Trout", "Salmon", "Lobster", "Swordfish", "Monkfish" + prem, "Shark",
//            "Sea Turtle" + prem, "Manta Ray" + prem, "" };
//        String[] s1 = { "1", "20", "30", "40", "50", "60", "70", "85", "95" };
//        for (int i = 0; i < s.length; i++) {
//          send(new SendString(s[i], slot++));
//        }
//        slot = 8720;
//        for (int i = 0; i < s1.length; i++) {
//          send(new SendString(s1[i], slot++));
//        }
//        int items[] = { 317, 335, 331, 377, 371, 7944, 383, 395, 389 };
//        setMenuItems(items);
    } else if (skillID == 9) {
      send(new SendString("Fishing", 8846));
      changeInterfaceStatus(8825, false);
      changeInterfaceStatus(8813, false);
      slot = 8760;
      String prem = " @red@(Premium only)";
      String[] s = { "Shrimps", "Trout", "Salmon", "Lobster", "Swordfish", "Monkfish" + prem, "Shark",
          "Sea Turtle" + prem, "Manta Ray" + prem, "" };
      String[] s1 = { "1", "20", "30", "40", "50", "60", "70", "85", "95" };
      for (int i = 0; i < s.length; i++) {
        send(new SendString(s[i], slot++));
      }
      slot = 8720;
      for (int i = 0; i < s1.length; i++) {
        send(new SendString(s1[i], slot++));
      }
      int items[] = { 317, 335, 331, 377, 371, 7944, 383, 395, 389 };
      setMenuItems(items);
    } else if (skillID == 10) {
      send(new SendString("Cooking", 8846));
      changeInterfaceStatus(8825, false);
      changeInterfaceStatus(8813, false);
      slot = 8760;
      String prem = " @red@(Premium only)";
      String[] s = { "Shrimps", "Trout", "Salmon", "Lobster", "Swordfish", "Monkfish" + prem, "Shark",
          "Sea Turtle" + prem, "Manta Ray" + prem };
      String[] s1 = { "1", "20", "30", "40", "50", "60", "70", "85", "95" };
      for (int i = 0; i < s.length; i++) {
        send(new SendString(s[i], slot++));
      }
      slot = 8720;
      for (int i = 0; i < s1.length; i++) {
        send(new SendString(s1[i], slot++));
      }
      int items[] = { 315, 333, 329, 379, 373, 7946, 385, 397, 391 };
      setMenuItems(items);
    } else if (skillID == 16) {
      send(new SendString("Agility", 8846));
      changeInterfaceStatus(8825, false);
      changeInterfaceStatus(8813, false);
      slot = 8760;
      String prem = " @red@(Premium only)";
      String[] s = { "Gnome Course", "Red key boss shortcut", "Orange key boss shortcut", "Skillcape" + prem };
      String[] s1 = { "1", "50", "70", "99" };
      for (int i = 0; i < s.length; i++) {
        send(new SendString(s[i], slot++));
      }
      slot = 8720;
      for (int i = 0; i < s1.length; i++) {
        send(new SendString(s1[i], slot++));
      }
      int items[] = { 2996, 1543, 1544, 9771 };
      setMenuItems(items);
    } else if (skillID == 7) {
      send(new SendString("Axes", 8846));
      send(new SendString("Logs", 8823));
      send(new SendString("Misc", 8824));
      changeInterfaceStatus(8825, false);
      String prem = " @red@(Premium only)";
      slot = 8760;
      String[] s = new String[0];
      String[] s1 = new String[0];
      if (child == 0) {
        s = new String[] { "Bronze Axe", "Iron Axe", "Steel Axe", "Mithril Axe", "Adamant Axe", "Rune Axe",
            "Dragon Axe" };
        s1 = new String[] { "1", "1", "6", "21", "31", "41", "61" };
      } else if (child == 1) {
        s = new String[] { "Logs", "Oak logs", "Willow logs", "Maple logs", "Yew logs", "Magic logs" };
        s1 = new String[] { "1", "15", "30", "45", "60", "75" };
      } else if (child == 2) {
        s = new String[] { "Skillcape" + prem };
        s1 = new String[] { "99" };
      }
      for (int i = 0; i < s.length; i++) {
        send(new SendString(s[i], slot++));
      }
      slot = 8720;
      for (int i = 0; i < s1.length; i++) {
        send(new SendString(s1[i], slot++));
      }
      if (child == 0)
        setMenuItems(new int[] { 1351, 1349, 1353, 1355, 1357, 1359, 6739 });
      else if (child == 1)
        setMenuItems(new int[] { 1511, 1521, 1519, 1517, 1515, 1513 });
      else if (child == 2)
        setMenuItems(new int[] { 9807 });
    } else if (skillID == 13) {
      send(new SendString("Pickaxes", 8846));
      send(new SendString("Ores", 8823));
      send(new SendString("Misc", 8824));
      changeInterfaceStatus(8825, false);
      String prem = " @red@(Premium only)";
      slot = 8760;
      String[] s = new String[0];
      String[] s1 = new String[0];
      if (child == 0) {
        s = new String[] { "Bronze Pickaxe", "Iron Pickaxe", "Steel Pickaxe", "Mithril Pickaxe", "Adamant Pickaxe",
            "Rune Pickaxe" };
        s1 = new String[] { "1", "1", "6", "21", "31", "41" };
      } else if (child == 1) {
        s = new String[] { "Copper ore", "Tin ore", "Iron ore", "Rune essence", "Coal", "Gold ore", "Mithril ore",
            "Adamant ore", "Runite ore" };
        s1 = new String[] { "1", "1", "15", "30", "30", "40", "55", "70", "85" };
      } else if (child == 2) {
        s = new String[] { "Skillcape" + prem };
        s1 = new String[] { "99" };
      }
      for (int i = 0; i < s.length; i++) {
        send(new SendString(s[i], slot++));
      }
      slot = 8720;
      for (int i = 0; i < s1.length; i++) {
        send(new SendString(s1[i], slot++));
      }
      if (child == 0)
        setMenuItems(new int[] { 1265, 1267, 1269, 1273, 1271, 1275 });
      else if (child == 1)
        setMenuItems(new int[] { 436, 438, 440, 1436, 453, 444, 447, 449, 451 });
      else if (child == 2)
        setMenuItems(new int[] { 9792 });
    } else if (skillID == 18) {
      send(new SendString("Master", 8846));
      send(new SendString("Monsters", 8823));
      send(new SendString("Misc", 8824));
      changeInterfaceStatus(8825, false);
      String prem = " @red@(Premium only)";
      slot = 8760;
      String[] s = new String[0];
      String[] s1 = new String[0];
      if (child == 0) {
        s = new String[] { "Mazchna" };
        s1 = new String[] { "1" };
      } else if (child == 1) {
        s = new String[] { "Berserker Spirit", "Mithril Dragon" };
        s1 = new String[] { "70", "90" };
      } else if (child == 2) {
        s = new String[] { "Skillcape" + prem };
        s1 = new String[] { "99" };
      }
      for (int i = 0; i < s.length; i++) {
        send(new SendString(s[i], slot++));
      }
      slot = 8720;
      for (int i = 0; i < s1.length; i++) {
        send(new SendString(s1[i], slot++));
      }
      if (child == 0)
        setMenuItems(new int[] { 4155 });
      else if (child == 1)
        setMenuItems(new int[] { -1 });
      else if (child == 2)
        setMenuItems(new int[] { 9786 });
    } else if (skillID == 8) {
      send(new SendString("Firemaking", 8846));
      changeInterfaceStatus(8825, false);
      changeInterfaceStatus(8813, false);
      changeInterfaceStatus(8825, false);
      String prem = " @red@(Premium only)";
      slot = 8760;
      String[] s = new String[0];
      String[] s1 = new String[0];
      s = new String[] { "Logs", "Oak logs", "Willow logs", "Maple logs", "Yew logs", "Magic logs",
          "Skillcape" + prem };
      s1 = new String[] { "1", "15", "30", "45", "60", "75", "99" };
      for (int i = 0; i < s.length; i++) {
        send(new SendString(s[i], slot++));
      }
      slot = 8720;
      for (int i = 0; i < s1.length; i++) {
        send(new SendString(s1[i], slot++));
      }
      setMenuItems(new int[] { 1511, 1521, 1519, 1517, 1515, 1513, 9804 });
    } else if (skillID == 14) {
      send(new SendString("Potions", 8846));
      send(new SendString("Herbs", 8823));
      send(new SendString("Misc", 8824));
      changeInterfaceStatus(8825, false);
      String prem = " @red@(Premium only)";
      slot = 8760;
      String[] s = new String[0];
      String[] s1 = new String[0];
      if (child == 0) {
        s = new String[] { "Attack Potion", "Strength Potion", "Defence Potion", "Super Attack Potion" + prem,
            "Super Strength Potion" + prem, "Super Defence Potion" + prem };
        s1 = new String[] { "1", "10", "30", "45", "55", "65" };
      } else if (child == 1) {
        s = new String[] { "Guam", "Tarromin", "Ranarr", "Irit", "Kwuarm", "Cadantine" };
        s1 = new String[] { "1", "10", "30", "45", "55", "65" };
      } else if (child == 2) {
        s = new String[] { "Skillcape" + prem };
        s1 = new String[] { "99" };
      }
      for (int i = 0; i < s.length; i++) {
        send(new SendString(s[i], slot++));
      }
      slot = 8720;
      for (int i = 0; i < s1.length; i++) {
        send(new SendString(s1[i], slot++));
      }
      if (child == 0)
        setMenuItems(new int[] { 121, 115, 133, 157, 145, 163 });
      else if (child == 1)
        setMenuItems(new int[] { 249, 253, 257, 263, 259, 265 });
      else if (child == 2)
        setMenuItems(new int[] { 9774 });
    } else if (skillID == 11) {
      send(new SendString("Fletching", 8846));
      changeInterfaceStatus(8825, false);
      changeInterfaceStatus(8813, false);
      slot = 8760;
      String[] s = { "Arrow Shafts", "Oak Shortbow", "Oak Longbow", "Willow Shortbow", "Willow Longbow",
          "Maple Shortbow", "Maple Longbow", "Yew Shortbow", "Yew Longbow", "Magic Shortbow", "Magic Longbow" };
      String[] s1 = { "1", "20", "25", "35", "40", "50", "55", "65", "70", "80", "85" };
      for (int i = 0; i < s.length; i++) {
        send(new SendString(s[i], slot++));
      }
      slot = 8720;
      for (int i = 0; i < s1.length; i++) {
        send(new SendString(s1[i], slot++));
      }
      int items[] = { 52, 54, 56, 60, 58, 64, 62, 68, 66, 72, 70 };
      setMenuItems(items);
    }
    /*
     * if (skillID == 0) { send(new SendString("Attack", 8846)); send(new
     * SendString("Defence", 8823)); send(new SendString("Range", 8824));
     * send(new SendString("Magic", 8827)); slot = 8760; String[] s = {
     * "Abyssal Whip", "Bronze", "Iron", "Steel", "Mithril", "Adamant", "Rune",
     * "Dragon", "Decorative Sword" }; String[] s1 = { "1", "1", "1", "10",
     * "20", "30", "40", "60", "80" }; for (int i = 0; i < s.length; i++) {
     * send(new SendString(s[i], slot++)); } slot = 8720; for (int i = 0; i <
     * s1.length; i++) { send(new SendString(s1[i], slot++)); } int items[] = {
     * 4151, 1291, 1293, 1295, 1299, 1301, 1303, 1305, 4068 };
     * setMenuItems(items); } else if (skillID == 1) { send(new
     * SendString("Attack", 8846)); send(new SendString("Defence", 8823));
     * send(new SendString("Range", 8824)); send(new SendString("Magic", 8827));
     * slot = 8760; String[] s = { "Skeletal", "Bronze", "Iron", "Steel",
     * "Mithril", "Adamant", "Rune", "Dragon", "Barrows" }; String[] s1 = { "1",
     * "1", "1", "10", "20", "30", "40", "60", "90" }; for (int i = 0; i <
     * s.length; i++) { send(new SendString(s[i], slot++)); } slot = 8720; for
     * (int i = 0; i < s1.length; i++) { send(new SendString(s1[i], slot++)); }
     * int items[] = { 6139, 1117, 1115, 1119, 1121, 1123, 1127, 3140, 4964 };
     * setMenuItems(items); } else if (skillID == 4) {
     * changeInterfaceStatus(8825, false); send(new SendString("Bows", 8846));
     * send(new SendString("Armour", 8823)); send(new SendString("Misc", 8824));
     * slot = 8760; String[] s = new String[0]; String[] s1 = new String[0]; if
     * (child == 0) { s = new String[] { "Oak bow", "Willow bow", "Maple bow",
     * "Yew bow", "Magic bow", "Crystal bow" }; s1 = new String[] { "1", "20",
     * "30", "40", "50", "70" }; } else if (child == 1) { s = new String[] {
     * "Leather", "Green dragonhide body (with 40 defence)",
     * "Green dragonhide chaps", "Green dragonhide vambraces",
     * "Blue dragonhide body (with 40 defence)", "Blue dragonhide chaps",
     * "Blue dragonhide vambraces", "Red dragonhide body (with 40 defence)",
     * "Red dragonhide chaps", "Red dragonhide vambraces",
     * "Black dragonhide body (with 40 defence)", "Black dragonhide chaps",
     * "Black dragonhide vambraces", "Snakeskin body (with 60 defence)",
     * "Snakeskin chaps (with 60 defence)", "Snakeskin boots",
     * "Snakeskin vambraces" }; s1 = new String[] { "1", "40", "40", "40", "50",
     * "50", "50", "60", "60", "60", "70", "70", "70", "80", "80", "80", "80" };
     * } for (int i = 0; i < s.length; i++) { send(new SendString(s[i],
     * slot++)); } slot = 8720; for (int i = 0; i < s1.length; i++) { send(new
     * SendString(s1[i], slot++)); } if (child == 0) setMenuItems(new int[] {
     * 843, 849, 853, 857, 861, 4212 }); else if (child == 1) setMenuItems(new
     * int[] { 1129, 1135, 1099, 1065, 2499, 2493, 2487, 2501, 2495, 2489, 2503,
     * 2497, 2491, 6322, 6324, 6328, 6330 }); } else if (skillID == 17) {
     * send(new SendString("Thieving", 8846)); changeInterfaceStatus(8825,
     * false); changeInterfaceStatus(8813, false); slot = 8760; String[] s = {
     * "Cages", "Bakers stall", "Fur Stall", "Silk Stall", "Spice Stall",
     * "Gem Stall" }; String[] s1 = { "1", "10", "10", "40", "80", "92" }; for
     * (int i = 0; i < s.length; i++) { send(new SendString(s[i], slot++)); }
     * slot = 8720; for (int i = 0; i < s1.length; i++) { send(new
     * SendString(s1[i], slot++)); } int items[] = { 4443, 2309, 314, 950, 253,
     * 1631 }; setMenuItems(items); }
     */
    getOutputStream().createFrame(79);
    getOutputStream().writeWordBigEndian(8717);
    getOutputStream().writeWordA(0);
    if (currentSkill != skillID)
      showInterface(8714);
    currentSkill = skillID;
  }
  
  public static void publicyell(String message) {
    for (Player p : PlayerHandler.players) {
      if (p == null || !p.isActive) {
        continue;
      }
      Client temp = (Client) p;
      if (temp.getPosition().getX() > 0 && temp.getPosition().getY() > 0) {
        if (temp != null && !temp.disconnected && p.isActive) {
          temp.send(new SendMessage(message));
        }
      }
    }
  }

  public void yell(String message) {
    if (System.currentTimeMillis() - lastYell < 10000 && playerRights < 2) {
      send(new SendMessage("You must wait " + (((lastYell + 10000) - System.currentTimeMillis()) / 1000)
          + " more seconds before yelling again"));
      send(new SendMessage("Use the yell channel to congratulate members, buy and sell items,"));
      send(new SendMessage("ask questions about the server or to announce an event you are holding."));
      send(new SendMessage("Misuse of the yell channel is grounds for a 24 hour mute."));
      return;
    }
    lastYell = System.currentTimeMillis();
    if (message.indexOf("tradereq") > 0 || message.indexOf("duelreq") > 0) {
      return;
    }
    // server.yell.add(new ChatLine(playerName, dbId, 1, message, absX,
    // absY));
    for (Player p : PlayerHandler.players) {
      if (message.indexOf("tradereq") > 0 || message.indexOf("duelreq") > 0) {
        return;
      }
      if (p == null || !p.isActive) {
        continue;
      }
      Client temp = (Client) p;
      if (temp.getPosition().getX() > 0 && temp.getPosition().getY() > 0) {
        if (temp != null && !temp.disconnected && p.isActive) {
          temp.send(new SendMessage(message));
        }
      }
    }
  }

  public int EssenceMineX[] = { 2893, 2921, 2911, 2926, 2899 };
  public int EssenceMineY[] = { 4846, 4846, 4832, 4817, 4817 };

  /*
   * [0] North West [1] North East [2] Center [3] South East [4] South West
   */
  public int EssenceMineRX[] = { 3253, 3105, 2681, 2591 };
  public int EssenceMineRY[] = { 3401, 9571, 3325, 3086 };

  /*
   * [0] Varrock [1] Wizard Tower [2] Ardougne [3] Magic Guild
   */
  private long stairBlock = 0;

  public boolean stairs(int stairs, int teleX, int teleY) {
    if (stairBlock > System.currentTimeMillis()) {
      resetStairs();
      System.out.println(getPlayerName() + " stair blocked!");
      return false;
    }
    stairBlock = System.currentTimeMillis() + 1000;
    if (IsStair == false) {
      IsStair = true;
      if (stairs == 1) {
        if (skillX == 2715 && skillY == 3470) {
          if (getPosition().getY() < 3470 || getPosition().getX() < 2715) {
            // resetStairs();
            return false;
          } else {
            getPosition().setZ(1);
            teleportToX = teleX;
            teleportToY = teleY;
            resetStairs();
            return true;
          }
        }
      }
      if (stairs == 2) {
        if (skillX == 2715 && skillY == 3470) {
          getPosition().setZ(0);
          teleportToX = teleX;
          teleportToY = teleY;
          resetStairs();
          return true;
        }
      }
      if (stairs == "legendsUp".hashCode()) {
        if (skillX == 2732 && skillY == 3377) {
          getPosition().setZ(1);
          teleportToX = 2732;
          teleportToY = 3380;
          resetStairs();
          return true;
        }
      }
      if (stairs == "legendsDown".hashCode()) {
        if (skillX == 2732 && skillY == 3378) {
          getPosition().setZ(0);
          teleportToX = 2732;
          teleportToY = 3376;
          resetStairs();
          return true;
        }
      }
      if (stairs == 1) {
        getPosition().setZ(getPosition().getZ() + 1);
      } else if (stairs == 2) {
        getPosition().setZ(getPosition().getZ() - 1);
      } else if (stairs == 21) {
        getPosition().setZ(getPosition().getZ() + 1);
      } else if (stairs == 22) {
        getPosition().setZ(getPosition().getZ() - 1);
      } else if (stairs == 69)
        getPosition().setZ(getPosition().getZ() + 1);
      teleportToX = teleX;
      teleportToY = teleY;
      if (stairs == 3 || stairs == 5 || stairs == 9) {
        teleportToY += 6400;
      } else if (stairs == 4 || stairs == 6 || stairs == 10) {
        teleportToY -= 6400;
      } else if (stairs == 7) {
        teleportToX = 3104;
        teleportToY = 9576;
      } else if (stairs == 8) {
        teleportToX = 3105;
        teleportToY = 3162;
      } else if (stairs == 11) {
        teleportToX = 2856;
        teleportToY = 9570;
      } else if (stairs == 12) {
        teleportToX = 2857;
        teleportToY = 3167;
      } else if (stairs == 13) {
        getPosition().setZ(getPosition().getZ() + 3);
        teleportToX = skillX;
        teleportToY = skillY;
      } else if (stairs == 15) {
        teleportToY += (6400 - (stairDistance + stairDistanceAdd));
      } else if (stairs == 14) {
        teleportToY -= (6400 - (stairDistance + stairDistanceAdd));
      } else if (stairs == 16) {
        teleportToX = 2828;
        teleportToY = 9772;
      } else if (stairs == 17) {
        teleportToX = 3494;
        teleportToY = 3465;
      } else if (stairs == 18) {
        teleportToX = 3477;
        teleportToY = 9845;
      } else if (stairs == 19) {
        teleportToX = 3543;
        teleportToY = 3463;
      } else if (stairs == 20) {
        teleportToX = 3549;
        teleportToY = 9865;
      } else if (stairs == 21) {
        teleportToY += (stairDistance + stairDistanceAdd);
      } else if (stairs == 69) {
        teleportToY = stairDistanceAdd;
        teleportToX = stairDistance;
      } else if (stairs == 22) {
        teleportToY -= (stairDistance + stairDistanceAdd);
      } else if (stairs == 23) {
        teleportToX = 2480;
        teleportToY = 5175;
      } else if (stairs == 24) {
        teleportToX = 2862;
        teleportToY = 9572;
      } else if (stairs == 25) {
        Essence = (getPosition().getZ() / 4);
        getPosition().setZ(0);
        teleportToX = EssenceMineRX[Essence];
        teleportToY = EssenceMineRY[Essence];
      } else if (stairs == 26) {
        int EssenceRnd = Utils.random3(EssenceMineX.length);

        teleportToX = EssenceMineX[EssenceRnd];
        teleportToY = EssenceMineY[EssenceRnd];
        getPosition().setZ((Essence * 4));
      } else if (stairs == 27) {
        teleportToX = 2453;
        teleportToY = 4468;
      } else if (stairs == 28) {
        teleportToX = 3201;
        teleportToY = 3169;
      }
      if (stairs == 5 || stairs == 10) {
        teleportToX += (stairDistance + stairDistanceAdd);
        teleportToY = getPosition().getY();
        getPosition().setZ(0);
      }
      if (stairs == 6 || stairs == 9) {
        teleportToX -= (stairDistance - stairDistanceAdd);
      }
    }
    resetStairs();
    return true;
  }

  public boolean resetStairs() {
    stairs = 0;
    skillX = -1;
    setSkillY(-1);
    stairDistance = 1;
    stairDistanceAdd = 0;
    resetWalkingQueue();
    final Client p = this;
    EventManager.getInstance().registerEvent(new Event(500) {

      @Override
      public void execute() {
        p.resetWalkingQueue();
        stop();
      }

    });
    return true;
  }

  public boolean UseBow = false;

  // pk: 2726 9193
  public boolean Attack() {
    if (!(AttackingOn > 0) || !(AttackingOn < PlayerHandler.players.length)) {
      ResetAttack();
      return false;
    }
    if (getSlot() < 1) {
      send(new SendMessage("Error:  Your player id is invalid.  Please try again or logout and back in"));
    }
    if (AttackingOn > 0 && !(duelFight && duel_with == AttackingOn) && !Server.pking) {
      send(new SendMessage("Pking has been disabled"));
      ResetAttack();
      return false;
    }
    Client temp = getClient(AttackingOn);
    if (!validClient(AttackingOn)) {
      send(new SendMessage("Invalid player"));
      ResetAttack();
      return false;
    }
    if (temp.immune) {
      send(new SendMessage("That player is immune"));
      ResetAttack();
      return false;
    }
    if (UseBow) {
      if (!duelRule[0]) {
        teleportToX = getPosition().getX();
        teleportToY = getPosition().getY();
        setFocus(temp.getPosition().getX(), temp.getPosition().getY());
        CalculateRange();
        setHitDiff(Utils.random(playerMaxHit));
      } else {
        send(new SendMessage("You can't range in this duel!"));
        return false;
      }
    }
    if (!UseBow && duelRule[1]) {
      send(new SendMessage("You can't melee in this duel!"));
      ResetAttack();
      return false;
    }
    int wildLevel = getWildLevel();
    if (!(duelFight && AttackingOn == duel_with) && wildyLevel == 0) {
      send(new SendMessage("You can't fight here!"));
      ResetAttack();
      return false;
    }
    if (!(duelFight && temp.getSlot() == duel_with)
        && (wildyLevel > 0 && temp.wildyLevel > 0 && Math.abs(temp.determineCombatLevel() - determineCombatLevel()) > wildLevel
            || Math.abs(temp.determineCombatLevel() - determineCombatLevel()) > temp.wildyLevel)) {
      send(new SendMessage("You are too low in the wilderness to fight that player"));
      ResetAttack();
      return false;
    }
    int EnemyHP = PlayerHandler.players[AttackingOn].getLevel(Skill.HITPOINTS);
    Client AttackingOn2 = (Client) PlayerHandler.players[AttackingOn];

    getUpdateFlags().setRequired(UpdateFlag.APPEARANCE, true);
    if (PlayerHandler.players[AttackingOn].getEquipment()[Equipment.Slot.RING.getId()] == 2570) {
      // RingOfLife = true;
    }
    int hitDiff = 0;
    int aBonus = 0;
    if (UseBow)
      hitDiff = (int) maxRangeHit();
    else
      hitDiff = playerMaxHit;
    int rand_att = Utils.random(getLevel(Skill.ATTACK));
    if (attackPot > 0.0) {
      rand_att = Utils.random((int) ((1 + (attackPot / 100)) * getLevel(Skill.ATTACK)));
    }
    int rand_def = (int) (0.65 * Utils.random(AttackingOn2.getLevel(Skill.DEFENCE)));
    if (FightType == 1) {
      aBonus += (int) (playerBonus[1] / 20);
    }
    if (FightType == 2) {
      hitDiff = (int) (hitDiff * 1.20);
    }
    int random_u = Utils.random(playerBonus[1] + aBonus) * 2;
    int dBonus = 0;
    if (AttackingOn2.FightType == 4) {
      dBonus += (int) (AttackingOn2.playerBonus[6] / 20);
    }
    int random_def = Utils.random(AttackingOn2.playerBonus[6] + dBonus);
    if (AttackingOn2.defensePot > 0.0) {
      random_def = (int) ((1 + (AttackingOn2.defensePot / 100)) * AttackingOn2.getLevel(Skill.DEFENCE));
    }
    if (random_u >= random_def && rand_att > rand_def) {
      hitDiff = Utils.random(hitDiff);
    } else {
      hitDiff = 0;
    }
    long thisAttack = System.currentTimeMillis();
    if (!UseBow) {
      if (thisAttack - lastAttack >= 2000) {
        setInCombat(true);
        lastPlayerCombat = System.currentTimeMillis();
        if (PlayerHandler.players[AttackingOn].deathStage > 0) {
          ResetAttack();
          send(new SendMessage("That player is dead!"));
        } else {

          actionAmount++;
          requestAnim(Server.itemManager.getAttackAnim(getEquipment()[Equipment.Slot.WEAPON.getId()]), 0);
          PlayerHandler.players[AttackingOn].getUpdateFlags().setRequired(UpdateFlag.APPEARANCE, true);
          if ((EnemyHP - hitDiff) < 0) {
            hitDiff = EnemyHP;
          }
          PlayerHandler.players[AttackingOn].receieveDamage(getSlot(), hitDiff, false);
          PlayerHandler.players[AttackingOn].killers[localId] += hitDiff;
          lastAttack = System.currentTimeMillis();
        }
        return true;
      }
    }
    if (UseBow) {
      // if (GoodDistance(EnemyX, EnemyY, absX, absY, 1) == false) {
      if (thisAttack - lastAttack >= 2000) {
        if (PlayerHandler.players[AttackingOn].deathStage > 0) {
          ResetAttack();
        } else if (UseBow) {
          setInCombat(true);
          lastPlayerCombat = System.currentTimeMillis();
          if (DeleteArrow()) {
            // CalculateRange();
            // hitDiff = misc.random(maxRangeHit());
            actionAmount++;
            // requestAnim(playerSEA);
            requestAnim(426, 0);
            PlayerHandler.players[AttackingOn].getUpdateFlags().setRequired(UpdateFlag.APPEARANCE, true);
            if ((EnemyHP - hitDiff) < 0) {
              hitDiff = EnemyHP;
            }
            PlayerHandler.players[AttackingOn].receieveDamage(getSlot(), hitDiff, false);
            lastAttack = System.currentTimeMillis();
          } else {
            send(new SendMessage("You are out of arrows!"));
            ResetAttack();
          }
        }
        return true;
      }
    }
    return false;

  }

  public boolean ResetAttack() {
    IsAttacking = false;
    AttackingOn = 0;
    rerequestAnim();
    faceNPC(65535);
    return true;
  }

  public boolean IsItemInBag(int ItemID) {
    for (int i = 0; i < playerItems.length; i++) {
      if ((playerItems[i] - 1) == ItemID) {
        return true;
      }
    }
    return false;
  }

  public boolean AreXItemsInBag(int ItemID, int Amount) {
    int ItemCount = 0;

    for (int i = 0; i < playerItems.length; i++) {
      if ((playerItems[i] - 1) == ItemID) {
        ItemCount++;
      }
      if (ItemCount == Amount) {
        return true;
      }
    }
    return false;
  }

  public int GetItemSlot(int ItemID) {
    for (int i = 0; i < playerItems.length; i++) {
      if ((playerItems[i] - 1) == ItemID) {
        return i;
      }
    }
    return -1;
  }

  public int GetXItemsInBag(int ItemID) {
    int ItemCount = 0;

    for (int i = 0; i < playerItems.length; i++) {
      if ((playerItems[i] - 1) == ItemID) {
        ItemCount++;
      }
    }
    return ItemCount;
  }

  public int wildyStage = 0;
  public boolean UsingAgility2 = false;
  public boolean randomed2;
  // private int setLastVote = 0;

  public void pmstatus(int status) { // status: loading = 0 connecting = 1
    // fine = 2
    getOutputStream().createFrame(221);
    getOutputStream().writeByte(status);
  }

  public boolean playerHasItem(int itemID) {
    itemID++;
    for (int i = 0; i < playerItems.length; i++) {
      if (playerItems[i] == itemID) {
        return true;
      }
    }
    return false;
  }

  public void wipeInv() {
    for (int i = 0; i < playerItems.length; i++) {
      deleteItem(playerItems[i] - 1, i, playerItemsN[i]);
    }
  }

  public boolean checkItem(int itemID) {
    itemID++;
    for (int i = 0; i < playerItems.length; i++) {
      if (playerItems[i] == itemID) {
        return true;
      }
    }
    for (int i = 0; i < getEquipment().length; i++) {
      if (getEquipment()[i] == itemID) {
        return true;
      }
    }
    for (int i = 0; i < bankItems.length; i++) {
      if (bankItems[i] == itemID) {
        return true;
      }
    }
    return false;
  }

  public boolean playerHasItem(int itemID, int amt) {
    itemID++;
    int found = 0;
    for (int i = 0; i < playerItems.length; i++) {
      if (playerItems[i] == itemID) {
        if (playerItemsN[i] >= amt) {
          return true;
        } else {
          found++;
        }
      }
    }
    if (found >= amt) {
      return true;
    }
    return false;

  }

  public void sendpm(long name, int rights, byte[] chatmessage, int messagesize) {
    getOutputStream().createFrameVarSize(196);
    getOutputStream().writeQWord(name);
    getOutputStream().writeDWord(handler.lastchatid++); // must be different for
    // each message
    getOutputStream().writeByte(rights);
    getOutputStream().writeBytes(chatmessage, messagesize, 0);
    getOutputStream().endFrameVarSize();
  }

  public void loadpm(long name, int world) {
    /*
     * if(world != 0) { world += 9; } else if(world == 0){ world += 1; }
     */
    if (world != 0) {
      world += 9;
    }
    getOutputStream().createFrame(50);
    getOutputStream().writeQWord(name);
    getOutputStream().writeByte(world);
  }

  public int[] staffs = { 2415, 2416, 2417, 4675, 4710 };

  public boolean AttackNPC() {
    int EnemyX = selectedNpc.getPosition().getX();
    int EnemyY = selectedNpc.getPosition().getY();
    int EnemyHP = selectedNpc.getCurrentHealth();
    int hitDiff = 0;
    // TurnPlayerTo(EnemyX, EnemyY);
    // faceNPC(selectedNpc.getIndex());
    if (EnemyHP < 1 || deathTimer > 0) {
      // println("Monster is dead");
      resetAttackNpc();
      return false;
    }
    int type = selectedNpc.getId();
    int[] arrowIds = { 882, 884, 886, 888, 890, 892 };
    int[] arrowGfx = { 10, 9, 11, 12, 13, 15 };
    int[] prem = { 1643, 158, 49, 1613 };
    for (int i = 0; i < prem.length; i++) {
      if (prem[i] == type && !premium) {
        resetPos();
        return false;
      }
    }

    SlayerTask slayerTask = SlayerTask.getSlayerNpc(type);
    if (slayerTask != null && slayerTask.isSlayerOnly() && (slayerTask.getTaskId() != taskId || taskAmt >= taskTotal)) {
      send(new SendMessage("You need a Slayer task to kill this monster."));
      resetAttackNpc();
      return false;
    }
    if (type == SlayerTask.HEAD_MOURNER.getNpcId()
        && (taskId != SlayerTask.HEAD_MOURNER.getTaskId() || taskAmt >= taskTotal) && getLevel(Skill.SLAYER) < 70) {
      send(new SendMessage("You need a Slayer level of at least 70 to kill this without a task."));
      resetAttackNpc();
      return false;
    }
    
//    if (taskId == 21 && !checkItem(989) && getLevel(Skill.SLAYER) < 50) {
//        taskId = 19;
//        taskTotal = 5 + Misc.random(10);
//        taskAmt = 0;
//        send(new SendMessage("You need a crystal key in order to kill this, so we've reset your task."));
//    }
    
//    if (type == SlayerTask.GREATER_DEMON.getNpcId()
//            && (taskId != SlayerTask.GREATER_DEMON.getTaskId() || taskAmt >= taskTotal) && !playerHasItem(989)) {
//          send(new SendMessage("You need a crystal key in order to kill this, so we've reset your task."));
//          resetAttackNpc();
//          return false;
//        }

    if (type == 1125) {
      if (determineCombatLevel() < 60) {
        send(new SendMessage("You must be level 60 combat or higher to attack Dad!"));
        resetAttackNpc();
        return false;
      }
    }

    if (type == 110 || type == 936) {
      if (!playerHasItem(1543)) {
        resetPos();
        resetAttackNpc();
        return false;
      }
    }
    if (type == 221 || type == 1961) {
      if (!playerHasItem(1544)) {
        resetPos();
        resetAttackNpc();
        return false;
      }
    }
    if ((type == 941 || type == 55) && !premium) {
      resetPos();
      return false;
    }
    for (int a = 0; a < staffs.length; a++) {
      if (getEquipment()[Equipment.Slot.WEAPON.getId()] == staffs[a] && autocast_spellIndex >= 0) {
        if (System.currentTimeMillis() - lastAttack < coolDown[coolDownGroup[autocast_spellIndex]]) {
          return false;
        }
        setInCombat(true);
        setLastCombat(System.currentTimeMillis());
        lastAttack = System.currentTimeMillis();
        if (getLevel(Skill.MAGIC) >= requiredLevel[autocast_spellIndex]) {
          if (!runeCheck(autocast_spellIndex)) {
            ResetAttack();
            return false;
          }
          deleteItem(565, 1);
          int dmg = baseDamage[autocast_spellIndex] + (int) Math.ceil(playerBonus[11] * 0.5);
          double hit = Utils.random(dmg);
          if (hit >= EnemyHP)
            hit = EnemyHP;
          hitDiff = (int) hit;
          requestAnim(1979, 0);
          // AnimationReset = true;
          teleportToX = getPosition().getX();
          teleportToY = getPosition().getY();
          resetWalkingQueue();
          if (ancientType[autocast_spellIndex] == 3) {
            // coolDown[coolDownGroup[autocast_spellIndex]] = 35;
            // server.npcHandler.npcs[attacknpc].effects[0] = 15;
            stillgfx(369, EnemyY, EnemyX);
          } else if (ancientType[autocast_spellIndex] == 2) {
            stillgfx(377, EnemyY, EnemyX);
            // coolDown[coolDownGroup[autocast_spellIndex]] = 12;
            setCurrentHealth(getCurrentHealth() + (int) (hit / 5));
            if (getCurrentHealth() > getLevel(Skill.HITPOINTS)) {
              setCurrentHealth(getLevel(Skill.HITPOINTS));
            }
          } else {
            animation(78, EnemyY, EnemyX);
          }
        } else {
          send(new SendMessage("You need a magic level of " + requiredLevel[autocast_spellIndex]));
        }
        // coolDown[coolDownGroup[autocast_spellIndex]] = 12;
        setFocus(EnemyX, EnemyY);
        giveExperience(40 * hitDiff, Skill.MAGIC);
        giveExperience(hitDiff * 15, Skill.HITPOINTS);
        getUpdateFlags().setRequired(UpdateFlag.APPEARANCE, true);
        selectedNpc.dealDamage(this, hitDiff, false);
        return true;
      }
    }
    long thisTime = System.currentTimeMillis();
    hitDiff = Utils.random(playerMaxHit);
    if (FightType == 2) {
      hitDiff = (int) (hitDiff * 1.20);
    }

    int arrowgfx = 10;
    for (int i1 = 0; i1 < arrowIds.length; i1++) {
      if (getEquipment()[Equipment.Slot.ARROWS.getId()] == arrowIds[i1]) {
        arrowgfx = arrowGfx[i1];
      }
    }
    if (UseBow && Utils.getDistance(getPosition().getX(), getPosition().getY(), selectedNpc.getPosition().getX(),
        selectedNpc.getPosition().getY()) > 5)
      return false;
    if (thisTime - lastAttack >= getbattleTimer(getEquipment()[Equipment.Slot.WEAPON.getId()]) && UseBow == true) {
      resetWalkingQueue();
      CalculateRange();
      hitDiff = Utils.random((int) maxRangeHit());
      if (DeleteArrow()) {
        int offsetX = (getPosition().getY() - EnemyY) * -1;
        int offsetY = (getPosition().getX() - EnemyX) * -1;
        for (int a = 0; a < Constants.maxPlayers; a++) {
          Client temp = (Client) PlayerHandler.players[a];
          if (temp != null && temp.dbId > 0 && temp.getPosition().getX() > 0 && !temp.disconnected
              && Math.abs(getPosition().getX() - temp.getPosition().getX()) < 60
              && Math.abs(getPosition().getY() - temp.getPosition().getY()) < 60) {
            temp.createProjectile(getPosition().getY(), getPosition().getX(), offsetY, offsetX, 50, 90, arrowgfx, 43,
                35, attacknpc + 1);
          }
        }
        getUpdateFlags().setRequired(UpdateFlag.APPEARANCE, true);
      } else {
        resetAttackNpc();
        send(new SendMessage("You're out of arrows!"));
        return false;
      }
    }
    if (thisTime - lastAttack >= getbattleTimer(getEquipment()[Equipment.Slot.WEAPON.getId()])) {
      setInCombat(true);
      setLastCombat(System.currentTimeMillis());
    } else {
      return false;
    }
    int emote = Server.itemManager.getAttackAnim(getEquipment()[Equipment.Slot.WEAPON.getId()]);
    if (UseBow)
      emote = 426;
    if (UseBow || GoodDistance(EnemyX, EnemyY, getPosition().getX(), getPosition().getY(), 1) == true) {
      if (!selectedNpc.isAlive()) {
        resetAttackNpc();
      } else {
        if (specsOn == true) {
          int chance = Utils.random(8);
          if (chance == 1) {
            if (getEquipment()[Equipment.Slot.WEAPON.getId()] == 4151) {
              SpecialsHandler.specAction(this, getEquipment()[Equipment.Slot.WEAPON.getId()]);
              hitDiff = hitDiff + bonusSpec;
              requestAnim(emoteSpec, 0);
              animation(animationSpec, EnemyY, EnemyX);
            } else if (getEquipment()[Equipment.Slot.WEAPON.getId()] == 7158) {
              SpecialsHandler.specAction(this, getEquipment()[Equipment.Slot.WEAPON.getId()]);
              hitDiff = hitDiff + bonusSpec;
              requestAnim(emoteSpec, 0);
              animation(animationSpec, EnemyY, EnemyX);
            }
          } else {
            requestAnim(emote, 0);
          }
        } else {
          requestAnim(emote, 0);
        }
        setFocus(EnemyX, EnemyY);
        getUpdateFlags().setRequired(UpdateFlag.APPEARANCE, true);
        actionAmount++;
        if ((EnemyHP - hitDiff) < 0) {
          hitDiff = EnemyHP;
        }
        double critChance = getLevel(Skill.AGILITY) / 9;
        double extra = getLevel(Skill.STRENGTH) * 0.195;
        if (UseBow)
          extra = getLevel(Skill.RANGED) * 0.195;
        if (Math.random() * 100 <= critChance)
          selectedNpc.dealDamage(this, hitDiff + (int) Utils.dRandom2((extra)), true);
        else
          selectedNpc.dealDamage(this, hitDiff, false);
        double TotalExp = 0;
        if (!UseBow) {
          // animationReset = System.currentTimeMillis() + 1200;
        }
        if (type != 1472) {
          if (UseBow) {
            TotalExp = (double) (FightType != 3 ? 40 * hitDiff : 20 * hitDiff);
            TotalExp = (double) (TotalExp * CombatExpRate);
            giveExperience((int) (TotalExp), Skill.RANGED);
            if (FightType == 3) {
              giveExperience((int) TotalExp, Skill.DEFENCE);
            }
          } else if (FightType != 3) {
            TotalExp = (double) (40 * hitDiff);
            TotalExp = (double) (TotalExp * CombatExpRate);
            giveExperience((int) (TotalExp), Skill.getSkill(FightType));
          } else {
            TotalExp = (double) (15 * hitDiff);
            TotalExp = (double) (TotalExp * CombatExpRate);
            giveExperience((int) (TotalExp), Skill.ATTACK);
            giveExperience((int) (TotalExp), Skill.DEFENCE);
            giveExperience((int) (TotalExp), Skill.STRENGTH);
          }
          TotalExp = (double) (15 * hitDiff);
          TotalExp = (double) (TotalExp * CombatExpRate);
          giveExperience((int) (TotalExp), Skill.HITPOINTS);
          if (debug) {
            send(new SendMessage("hitDiff=" + hitDiff + ", elapsed=" + (thisTime - lastAttack)));
          }
          lastAttack = System.currentTimeMillis();
        }
        return true;

      }
    }
    // addSkillXP(80 * hitDiff, playerSlayer);
    return false;
  }

  public boolean DeleteArrow() {
    if (getEquipmentN()[Equipment.Slot.ARROWS.getId()] == 0) {
      deleteequiment(getEquipment()[Equipment.Slot.ARROWS.getId()], Equipment.Slot.ARROWS.getId());
      return false;
    }
    if (/*
         * getEquipment()[Equipment.Slot.WEAPON.getId()] != 4212 &&
         */getEquipmentN()[Equipment.Slot.ARROWS.getId()] > 0) {
      getOutputStream().createFrameVarSizeWord(34);
      getOutputStream().writeWord(1688);
      getOutputStream().writeByte(Equipment.Slot.ARROWS.getId());
      getOutputStream().writeWord(getEquipment()[Equipment.Slot.ARROWS.getId()] + 1);
      if (getEquipmentN()[Equipment.Slot.ARROWS.getId()] - 1 > 254) {
        getOutputStream().writeByte(255);
        getOutputStream().writeDWord(getEquipmentN()[Equipment.Slot.ARROWS.getId()] - 1);
      } else {
        getOutputStream().writeByte(getEquipmentN()[Equipment.Slot.ARROWS.getId()] - 1); // amount
      }
      getOutputStream().endFrameVarSizeWord();
      getEquipmentN()[Equipment.Slot.ARROWS.getId()] -= 1;
    }
    getUpdateFlags().setRequired(UpdateFlag.APPEARANCE, true);
    return true;
  }

  public void ReplaceObject(int objectX, int objectY, int NewObjectID, int Face, int ObjectType) {
    getOutputStream().createFrame(85);
    getOutputStream().writeByteC(objectY - (mapRegionY * 8));
    getOutputStream().writeByteC(objectX - (mapRegionX * 8));

    getOutputStream().createFrame(101);
    getOutputStream().writeByteC((ObjectType << 2) + (Face & 3));
    getOutputStream().writeByte(0);

    if (NewObjectID != -1) {
      getOutputStream().createFrame(151);
      getOutputStream().writeByteS(0);
      getOutputStream().writeWordBigEndian(NewObjectID);
      getOutputStream().writeByteS((ObjectType << 2) + (Face & 3));
    }
  }

  public int GetNPCID(int coordX, int coordY) {
    for (Npc n : Server.npcManager.getNpcs()) {
      if (n.getPosition().getX() == coordX && n.getPosition().getY() == coordY) {
        return n.getId();
      }
    }
    return 1;
  }

  public String GetNpcName(int NpcID) {
    return Server.npcManager.getName(NpcID);
  }

  public String GetItemName(int ItemID) {
    return Server.itemManager.getName(ItemID);
  }

  public double GetShopSellValue(int ItemID, int Type, int fromSlot) {
    return Server.itemManager.getShopSellValue(ItemID);
  }

  public double GetShopBuyValue(int ItemID, int Type, int fromSlot) {
    return Server.itemManager.getShopBuyValue(ItemID);
  }

  public int GetUnnotedItem(int ItemID) {
    String NotedName = Server.itemManager.getName(ItemID);
    for (Item item : Server.itemManager.items.values()) {
      if (item.getName().equals(NotedName) && !item.getDescription().startsWith("Swap this note at any bank for a")) {
        return item.getId();
      }
    }
    return 0;
  }

  public void WriteEnergy() {
    playerEnergy = 100;
    send(new SendString(playerEnergy + "%", 149));
  }

  public void ResetBonus() {
    for (int i = 0; i < playerBonus.length; i++) {
      playerBonus[i] = 0;
    }
  }

  public void GetBonus() {
    for (int element : getEquipment()) {
      if (element > -1) {
        for (int j = 0; j < 12000; j++) {
          if (Server.itemManager.items.get(j) != null) {
            Item i = Server.itemManager.items.get(element);
            if (Server.itemManager.items.get(j) == i) {
              for (int k = 0; k < playerBonus.length; k++) {
                playerBonus[k] += Server.itemManager.getBonus(element, k);
              }
              break;
            }
          }
        }
      }
    }
    for (int i = 0; i < 5; i++) {
      playerBonus[i] += (int) (getLevel(Skill.ATTACK) / 10);
    }
    playerBonus[5] += (int) (getLevel(Skill.DEFENCE) / 5);
    playerBonus[6] += (int) (getLevel(Skill.DEFENCE) / 5);
    playerBonus[7] += (int) (getLevel(Skill.DEFENCE) / 5);
    playerBonus[8] += (int) (getLevel(Skill.DEFENCE) / 5);
    playerBonus[9] += (int) (getLevel(Skill.DEFENCE) / 5);

    playerBonus[10] += (int) (getLevel(Skill.STRENGTH) / 5);
    // maxHealth = playerLevel[3];
  }

  public void WriteBonus() {
    int offset = 0;
    String send = "";

    for (int i = 0; i < playerBonus.length; i++) {
      if (playerBonus[i] >= 0) {
        send = BonusName[i] + ": +" + playerBonus[i];
      } else {
        send = BonusName[i] + ": -" + java.lang.Math.abs(playerBonus[i]);
      }

      if (i == 10) {
        offset = 1;
      }
      if (i == 11) {
        send = "Spell Dmg:  +" + playerBonus[i] + "";
      }
      send(new SendString(send, (1675 + i + offset)));
    }
    CalculateMaxHit();

    /*
     * for (int i = 4000; i <= 7000; i++) { send(new SendString("T"+i, i);
     * println_debug("Sended: Test"+i); }
     */// USED FOR TESTING INTERFACE NUMBERS !
  }

  public void CalculateMaxHit() {
    double MaxHit = 0;
    int StrBonus = playerBonus[10]; // Strength Bonus
    int Strength = getLevel(Skill.STRENGTH); // Strength
    int RngBonus = playerBonus[4]; // Ranged Bonus
    int Range = getLevel(Skill.RANGED); // Ranged
    if (strengthPot > 0.0) {
      Strength = (int) ((1 + (strengthPot / 100)) * getLevel(Skill.STRENGTH));
    }
    if (FightType == 0 || FightType == 1) { // Accurate & Defensive
      MaxHit += (double) (1.05 + (double) ((double) (StrBonus * Strength) * 0.00175));
    } else if (FightType == 2) { // Aggresive
      MaxHit += (double) (1.35 + (double) ((double) (StrBonus * Strength) * 0.00175));
    } else if (FightType == 3) { // Controlled
      MaxHit += (double) (1.15 + (double) ((double) (StrBonus * Strength) * 0.00175));
    }
    MaxHit += (double) (Strength * 0.1);

    /*
     * if (StrPrayer == 1) { // Burst Of Strength MaxHit += (double) (Strength *
     * 0.005); } else if (StrPrayer == 2) { // Super Human Strength MaxHit +=
     * (double) (Strength * 0.01); } else if (StrPrayer == 3) { // Ultimate
     * Strength MaxHit += (double) (Strength * 0.015); }
     */
    if (UseBow) {
      if (FightType == 1 || FightType == 3) { // Accurate and Longranged
        MaxHit += (double) (1.05 + (double) ((double) (RngBonus * Range) * 0.00075));
      } else if (FightType < 3) { // Rapid
        MaxHit += (double) (1.35 + (double) ((double) (RngBonus) * 0.00025));
      }
    }
    // MaxHit += (double) (Range * 0.03);
    playerMaxHit = (int) Math.floor(MaxHit);
  }

  public boolean GoodDistance2(int objectX, int objectY, int playerX, int playerY, int distance) {
    for (int i = 0; i <= distance; i++) {
      for (int j = 0; j <= distance; j++) {
        if (objectX == playerX && ((objectY + j) == playerY || (objectY - j) == playerY || objectY == playerY)) {
          return true;
        } else if (objectY == playerY && ((objectX + j) == playerX || (objectX - j) == playerX || objectX == playerX)) {
          return true;
        }
      }
    }
    return false;
  }

  private int[] woodcuttingDelays = { 1200, 1800, 3000, 4200, 5400, 7200 };
  private int[] woodcuttingLevels = { 1, 15, 30, 45, 60, 75 };
  private int[] woodcuttingLogs = { 1511, 1521, 1519, 1517, 1515, 1513 };
  private int[] woodcuttingExp = { 80, 152, 272, 400, 700, 1000 };
  public int woodcuttingIndex = -1;

  public boolean CheckObjectSkill(int objectID) {
    boolean GoFalse = false;

    switch (objectID) {

    /*
     * 
     * WOODCUTTING
     * 
     */
    case 1276:
    case 1277:
    case 1278:
    case 1279:
    case 1280:
    case 1330:
    case 1332:
    case 2409:
    case 3033:
    case 3034:
    case 3035:
    case 3036:
    case 3879:
    case 3881:
    case 3882:
    case 3883: // Normal Tree
    case 1315:
    case 1316:
    case 1318:
    case 1319: // Evergreen
    case 1282:
    case 1283:
    case 1284:
    case 1285:
    case 1286:
    case 1287:
    case 1289:
    case 1290:
    case 1291:
    case 1365:
    case 1383:
    case 1384:
    case 5902:
    case 5903:
    case 5904: // Dead Tree
      // if(distanceToPoint(skillX, skillY) > 5)
      // return false;
      woodcuttingIndex = 0;
      // startWoodcutting();
      break;

    case 1281:
    case 3037: // Oak Tree
      woodcuttingIndex = 1;
      // startWoodcutting();
      break;

    case 1308:
    case 5551:
    case 5552:
    case 5553: // Willow Tree
      woodcuttingIndex = 2;
      // startWoodcutting();
      break;

    case 1307:
    case 4674: // Maple Tree
      woodcuttingIndex = 3;
      // startWoodcutting();
      break;

    case 1309: // Yew Tree
      woodcuttingIndex = 4;
      // startWoodcutting();
      break;

    case 1306: // Magic Tree
      woodcuttingIndex = 5;
      // startWoodcutting();
      break;

    default:
      GoFalse = true;
      break;
    }
    if (GoFalse == true) {
      return false;
    }
    return true;
  }

  public int CheckSmithing(int ItemID, int ItemSlot) {
    boolean GoFalse = false;
    int Type = -1;
    if (IsItemInBag(2347) == false) {
      send(new SendMessage("You need a " + GetItemName(2347) + " to hammer bars."));
      return -1;
    }
    /*
     * if (getEquipment()[Equipment.Slot.WEAPON.getId()] > 0) { send(new
     * SendMessage("You must remove your weapon to smith")); return -1; }
     */
    switch (ItemID) {
    case 2349: // Bronze Bar
      Type = 1;
      break;

    case 2351: // Iron Bar
      Type = 2;
      break;

    case 2353: // Steel Bar
      Type = 3;
      break;

    case 2359: // Mithril Bar
      Type = 4;
      break;

    case 2361: // Adamantite Bar
      Type = 5;
      break;

    case 2363: // Runite Bar
      Type = 6;
      break;

    default:
      send(new SendMessage("You cannot smith this item."));
      GoFalse = true;
      break;
    }
    if (GoFalse == true) {
      return -1;
    }
    return Type;
  }

  public void OpenSmithingFrame(int Type) {
    int Type2 = Type - 1;
    int Length = 22;

    if (Type == 1 || Type == 2) {
      Length += 1;
    } else if (Type == 3) {
      Length += 2;
    }
    // Sending amount of bars + make text green if lvl is highenough
    send(new SendString("", 1132)); // Wire
    send(new SendString("", 1096));
    send(new SendString("", 11459)); // Lantern
    send(new SendString("", 11461));
    send(new SendString("", 1135)); // Studs
    send(new SendString("", 1134));
    String bar, color, color2, name = "";

    if (Type == 1) {
      name = "Bronze ";
    } else if (Type == 2) {
      name = "Iron ";
    } else if (Type == 3) {
      name = "Steel ";
    } else if (Type == 4) {
      name = "Mithril ";
    } else if (Type == 5) {
      name = "Adamant ";
    } else if (Type == 6) {
      name = "Rune ";
    }
    for (int i = 0; i < Length; i++) {
      bar = "bar";
      color = "@red@";
      color2 = "@bla@";
      if (Constants.smithing_frame[Type2][i][3] > 1) {
        bar = bar + "s";
      }
      if (getLevel(Skill.SMITHING) >= Constants.smithing_frame[Type2][i][2]) {
        color2 = "@whi@";
      }
      int Type3 = Type2;

      if (Type2 >= 3) {
        Type3 = (Type2 + 2);
      }
      if (AreXItemsInBag((2349 + (Type3 * 2)), Constants.smithing_frame[Type2][i][3]) == true) {
        color = "@gre@";
      }
      send(new SendString(color + "" + Constants.smithing_frame[Type2][i][3] + "" + bar,
          Constants.smithing_frame[Type2][i][4]));
      String linux_hack = GetItemName(Constants.smithing_frame[Type2][i][0]);
      int index = GetItemName(Constants.smithing_frame[Type2][i][0]).indexOf(name);
      if (index > 0) {
        linux_hack = linux_hack.substring(index + 1);
        send(new SendString(linux_hack, Constants.smithing_frame[Type2][i][5]));
      }
      // send(new SendString(
      // color2 + ""
      // + GetItemName(Constants.smithing_frame[Type2][i][0]).replace(name,
      // ""),
      // Constants.smithing_frame[Type2][i][5]);
    }
    Constants.SmithingItems[0][0] = Constants.smithing_frame[Type2][0][0]; // Dagger
    Constants.SmithingItems[0][1] = Constants.smithing_frame[Type2][0][1];
    Constants.SmithingItems[1][0] = Constants.smithing_frame[Type2][4][0]; // Sword
    Constants.SmithingItems[1][1] = Constants.smithing_frame[Type2][4][1];
    Constants.SmithingItems[2][0] = Constants.smithing_frame[Type2][8][0]; // Scimitar
    Constants.SmithingItems[2][1] = Constants.smithing_frame[Type2][8][1];
    Constants.SmithingItems[3][0] = Constants.smithing_frame[Type2][9][0]; // Long
    // Sword
    Constants.SmithingItems[3][1] = Constants.smithing_frame[Type2][9][1];
    Constants.SmithingItems[4][0] = Constants.smithing_frame[Type2][18][0]; // 2
    // hand
    // sword
    Constants.SmithingItems[4][1] = Constants.smithing_frame[Type2][18][1];
    SetSmithing(1119);
    Constants.SmithingItems[0][0] = Constants.smithing_frame[Type2][1][0]; // Axe
    Constants.SmithingItems[0][1] = Constants.smithing_frame[Type2][1][1];
    Constants.SmithingItems[1][0] = Constants.smithing_frame[Type2][2][0]; // Mace
    Constants.SmithingItems[1][1] = Constants.smithing_frame[Type2][2][1];
    Constants.SmithingItems[2][0] = Constants.smithing_frame[Type2][13][0]; // Warhammer
    Constants.SmithingItems[2][1] = Constants.smithing_frame[Type2][13][1];
    Constants.SmithingItems[3][0] = Constants.smithing_frame[Type2][14][0]; // Battle
    // axe
    Constants.SmithingItems[3][1] = Constants.smithing_frame[Type2][14][1];
    Constants.SmithingItems[4][0] = Constants.smithing_frame[Type2][17][0]; // Claws
    Constants.SmithingItems[4][1] = Constants.smithing_frame[Type2][17][1];
    SetSmithing(1120);
    Constants.SmithingItems[0][0] = Constants.smithing_frame[Type2][15][0]; // Chain
    // body
    Constants.SmithingItems[0][1] = Constants.smithing_frame[Type2][15][1];
    Constants.SmithingItems[1][0] = Constants.smithing_frame[Type2][20][0]; // Plate
    // legs
    Constants.SmithingItems[1][1] = Constants.smithing_frame[Type2][20][1];
    Constants.SmithingItems[2][0] = Constants.smithing_frame[Type2][19][0]; // Plate
    // skirt
    Constants.SmithingItems[2][1] = Constants.smithing_frame[Type2][19][1];
    Constants.SmithingItems[3][0] = Constants.smithing_frame[Type2][21][0]; // Plate
    // body
    Constants.SmithingItems[3][1] = Constants.smithing_frame[Type2][21][1];
    Constants.SmithingItems[4][0] = -1; // Lantern
    Constants.SmithingItems[4][1] = 0;
    if (Type == 2 || Type == 3) {
      color2 = "@bla@";
      if (getLevel(Skill.SMITHING) >= Constants.smithing_frame[Type2][22][2]) {
        color2 = "@whi@";
      }
      Constants.SmithingItems[4][0] = Constants.smithing_frame[Type2][22][0]; // Lantern
      Constants.SmithingItems[4][1] = Constants.smithing_frame[Type2][22][1];
      send(new SendString(color2 + "" + GetItemName(Constants.smithing_frame[Type2][22][0]), 11461));
    }
    SetSmithing(1121);
    Constants.SmithingItems[0][0] = Constants.smithing_frame[Type2][3][0]; // Medium
    Constants.SmithingItems[0][1] = Constants.smithing_frame[Type2][3][1];
    Constants.SmithingItems[1][0] = Constants.smithing_frame[Type2][10][0]; // Full
    // Helm
    Constants.SmithingItems[1][1] = Constants.smithing_frame[Type2][10][1];
    Constants.SmithingItems[2][0] = Constants.smithing_frame[Type2][12][0]; // Square
    Constants.SmithingItems[2][1] = Constants.smithing_frame[Type2][12][1];
    Constants.SmithingItems[3][0] = Constants.smithing_frame[Type2][16][0]; // Kite
    Constants.SmithingItems[3][1] = Constants.smithing_frame[Type2][16][1];
    Constants.SmithingItems[4][0] = Constants.smithing_frame[Type2][6][0]; // Nails
    Constants.SmithingItems[4][1] = Constants.smithing_frame[Type2][6][1];
    SetSmithing(1122);
    Constants.SmithingItems[0][0] = Constants.smithing_frame[Type2][5][0]; // Dart
    // Tips
    Constants.SmithingItems[0][1] = Constants.smithing_frame[Type2][5][1];
    Constants.SmithingItems[1][0] = Constants.smithing_frame[Type2][7][0]; // Arrow
    // Heads
    Constants.SmithingItems[1][1] = Constants.smithing_frame[Type2][7][1];
    Constants.SmithingItems[2][0] = Constants.smithing_frame[Type2][11][0]; // Knives
    Constants.SmithingItems[2][1] = Constants.smithing_frame[Type2][11][1];
    Constants.SmithingItems[3][0] = -1; // Wire
    Constants.SmithingItems[3][1] = 0;
    if (Type == 1) {
      color2 = "@bla@";
      if (getLevel(Skill.SMITHING) >= Constants.smithing_frame[Type2][22][2]) {
        color2 = "@whi@";
      }
      Constants.SmithingItems[3][0] = Constants.smithing_frame[Type2][22][0]; // Wire
      Constants.SmithingItems[3][1] = Constants.smithing_frame[Type2][22][1];
      send(new SendString(color2 + "" + GetItemName(Constants.smithing_frame[Type2][22][0]), 1096));
    }
    Constants.SmithingItems[4][0] = -1; // Studs
    Constants.SmithingItems[4][1] = 0;
    if (Type == 3) {
      color2 = "@bla@";
      if (getLevel(Skill.SMITHING) >= Constants.smithing_frame[Type2][23][2]) {
        color2 = "@whi@";
      }
      Constants.SmithingItems[4][0] = Constants.smithing_frame[Type2][23][0]; // Studs
      Constants.SmithingItems[4][1] = Constants.smithing_frame[Type2][23][1];
      send(new SendString(color2 + "" + GetItemName(Constants.smithing_frame[Type2][23][0]), 1134));
    }
    SetSmithing(1123);
    showInterface(994);
    smithing[2] = Type;
  }

  public boolean smithing() {
    if (IsItemInBag(2347) == true) {
      if (!smithCheck(smithing[4])) {
        resetSM();
        return false;
      }
      /*
       * for(int i = 0; i < Constants.smithing_frame.length; i++){ for(int i1 =
       * 0;
       */
      int bars = 0;
      int Length = 22;
      int barid = 0;
      int xp = 0;
      int ItemN = 1;

      if (smithing[2] >= 4) {
        barid = (2349 + ((smithing[2] + 1) * 2));
      } else {
        barid = (2349 + ((smithing[2] - 1) * 2));
      }
      if (smithing[2] == 1 || smithing[2] == 2) {
        Length += 1;
      } else if (smithing[2] == 3) {
        Length += 2;
      }
      // println("id="+ Constants.smithing_frame[(smithing[2] - 1)][i][0]);
      int[] possibleBars = { 2349, 2351, 2353, 2359, 2361, 2363 };
      int[] bar_xp = { 13, 25, 38, 50, 63, 75 };
      for (int i = 0; i < Constants.smithing_frame.length; i++) {
        for (int i1 = 0; i1 < Constants.smithing_frame[i].length; i1++) {
          for (int i2 = 0; i2 < Constants.smithing_frame[i][i1].length; i2++) {
            if (Constants.smithing_frame[i][i1][0] == smithing[4]) {
              // println("needs " + Constants.smithing_frame[i][i1][3]
              // + " bars, row " + i);
              if (!AreXItemsInBag(possibleBars[i], Constants.smithing_frame[i][i1][3])) {
                send(new SendMessage("You are missing bars needed to smith this!"));
                resetSM();
                return false;
              }
              xp = bar_xp[i];
            }
            // println("smithing[" + i + "][" + i1 + "][" + i2 + "]:
            // " + Constants.smithing_frame[i][i1][i2]);
          }
        }
      }
      for (int i = 0; i < Length; i++) {
        if (Constants.smithing_frame[(smithing[2] - 1)][i][0] == smithing[4]) {
          bars = Constants.smithing_frame[(smithing[2] - 1)][i][3];
          if (smithing[1] == 0) {
            smithing[1] = Constants.smithing_frame[(smithing[2] - 1)][i][2];
          }
          ItemN = Constants.smithing_frame[(smithing[2] - 1)][i][1];
          // send(new SendMessage("bars=" + bars + ", smithing[1]=" +
          // smithing[1] + ", itemN=" + ItemN);
        }
      }
      if (getLevel(Skill.SMITHING) >= smithing[1]) {
        if (AreXItemsInBag(barid, bars) == true) {
          if (freeSlots() > 0) {
            if (actionTimer == 0 && smithing[0] == 1) {
              actionAmount++;
              /*
               * OriginalWeapon =
               * getEquipment()[Equipment.Slot.WEAPON.getId()];
               * getEquipment()[Equipment.Slot.WEAPON.getId()] = 2347; //
               * Hammer OriginalShield =
               * getEquipment()[Equipment.Slot.SHIELD.getId()];
               * getEquipment()[Equipment.Slot.SHIELD.getId()] = -1;
               */
              send(new SendMessage("You start hammering the bar..."));
              actionTimer = 2; // smithing timer fix?
              requestAnim(0x382, 0);
              smithing[0] = 2;
            }
            if (actionTimer == 0 && smithing[0] == 2) {
              for (int i = 0; i < bars; i++) {
                deleteItem(barid, GetItemSlot(barid), playerItemsN[GetItemSlot(barid)]);
              }
              // was 150
              giveExperience(((int) (xp * bars * 30)), Skill.SMITHING);
              addItem(smithing[4], ItemN);
              send(new SendMessage("You smith a " + GetItemName(smithing[4]) + "."));
              rerequestAnim();
              if (smithing[5] <= 1) {
                resetSM();
              } else {
                actionTimer = 5;
                smithing[5] -= 1;
                smithing[0] = 1;
              }
            }
          } else {
            send(new SendMessage("Not enough space in your inventory."));
            resetSM();
            return false;
          }
        } else {
          send(new SendMessage(
              "You need " + bars + " " + GetItemName(barid) + " to smith a " + GetItemName(smithing[4])));
          rerequestAnim();
          resetSM();
        }
      } else {
        send(new SendMessage("You need " + smithing[1] + " Smithing to smith a " + GetItemName(smithing[4])));
        resetSM();
        return false;
      }
    } else {
      send(new SendMessage("You need a " + GetItemName(2347) + " to hammer bars."));
      resetSM();
      return false;
    }
    return true;
  }

  public boolean resetSM() {
    if (OriginalWeapon > -1) {
      getEquipment()[Equipment.Slot.WEAPON.getId()] = OriginalWeapon;
      OriginalWeapon = -1;
      getEquipment()[Equipment.Slot.SHIELD.getId()] = OriginalShield;
      OriginalShield = -1;
    }
    smithing[0] = 0;
    smithing[1] = 0;
    smithing[2] = 0;
    smithing[4] = -1;
    smithing[5] = 0;
    skillX = -1;
    setSkillY(-1);
    return true;
  }

  /* WOODCUTTING */

  public boolean woodcutting() {
    if (randomed || fletchings || isFiremaking || shafting) {
      return false;
    }
    if (woodcuttingIndex < 0) {
      resetAction();
      return false;
    }

    int WCAxe = findAxe();
    if (WCAxe < 0) {
      send(new SendMessage("You need a axe in which you got the required woodcutting level for."));
      resetWC();
      return false;
    }
    if (woodcuttingLevels[woodcuttingIndex] > getLevel(Skill.WOODCUTTING)) {
      resetAction();
      send(new SendMessage(
          "You need a woodcutting level of " + woodcuttingLevels[woodcuttingIndex] + " to cut this tree."));
      resetWC();
      return false;
    }
    if (freeSlots() < 1) {
      resetWC();
      resetAction(true);
      resetAction();
      return false;
    }
    if (System.currentTimeMillis() - lastAction >= 600 && IsCutting == false) {
      lastAction = System.currentTimeMillis();
      send(new SendMessage("You swing your axe at the tree..."));
      requestAnim(getWoodcuttingEmote(Utils.axes[WCAxe]), 0);
      IsCutting = true;
    }
    if (IsCutting == true)
      requestAnim(getWoodcuttingEmote(Utils.axes[WCAxe]), 0);
    if (System.currentTimeMillis() - lastAction >= getWoodcuttingSpeed() && IsCutting == true) {
      lastAction = System.currentTimeMillis();
      if (Utils.random(100) == 1) {
        triggerRandom();
        resetWC();
        resetAction(true);
        resetAction();
        return false;
      }
      giveExperience(woodcuttingExp[woodcuttingIndex], Skill.WOODCUTTING);
      send(new SendMessage("You cut some " + GetItemName(woodcuttingLogs[woodcuttingIndex]).toLowerCase() + "."));
      addItem(woodcuttingLogs[woodcuttingIndex], 1);
    }
    return true;
  }

  public int getWoodcuttingEmote(int item) {
    switch (item) {
    case 1351:
      return 879; // Bronze
    case 1349:
      return 877; // Iron
    case 1353:
      return 875; // Steel
    case 1355:
      return 871; // Mith
    case 1357:
      return 869; // addy
    case 1359:
      return 867; // rune
    case 6739: // Dragon
      return 2846;
    default:
      send(new SendMessage("Could not find wc anim for axe id: " + getEquipment()[Equipment.Slot.WEAPON.getId()]));
    }
    return 875;
  }

  public int getMiningEmote(int item) {
    switch (item) {
    case 1275:
      return 624;
    case 1271:
      return 628;
    case 1273:
      return 629;
    case 1269:
      return 627;
    case 1267:
      return 626;
    case 1265:
      return 625;
    }
    return 625;
  }

  public long getMiningSpeed() {
    double pickBonus = Utils.pickBonus[minePick];
    double level = (double) getLevel(Skill.MINING) / 600;
    double random = (double) Misc.random(150) / 100;
    double bonus = 1 + pickBonus * random + level;
    double time = Utils.mineTimes[mineIndex] / bonus;
    // System.out.println("Time = "+(long) time);
    return (long) time;
  }

  public long getWoodcuttingSpeed() {
    double axeBonus = Utils.axeBonus[findAxe()];
    double level = (double) getLevel(Skill.WOODCUTTING) / 600;
    double random = (double) Misc.random(150) / 100;
    double bonus = 1 + axeBonus * random + level;
    double time = woodcuttingDelays[woodcuttingIndex] / bonus;
    // System.out.println("Time = "+(long) time);
    return (long) time;
  }

  public boolean resetWC() {
    woodcutting[0] = 0;
    woodcutting[1] = 0;
    woodcutting[2] = 0;
    woodcutting[4] = 0;
    skillX = -1;
    setSkillY(-1);
    woodcuttingIndex = -1;
    IsCutting = false;
    rerequestAnim();
    return true;
  }

  public boolean fromTrade(int itemID, int fromSlot, int amount) {
    if (System.currentTimeMillis() - lastButton > 800) {
      lastButton = System.currentTimeMillis();
    } else {
      return false;
    }
    try {
      Client other = getClient(trade_reqId);
      if (!inTrade || !validClient(trade_reqId) || !canOffer) {
        System.out.println("declining in fromtrade");
        declineTrade();
        return false;
      }
      if (!Server.itemManager.isStackable(itemID) && amount > 1) {
        for (int a = 1; a <= amount; a++) {
          int slot = findItem(itemID, playerItems, playerItemsN);
          if (slot >= 0) {
            fromTrade(itemID, 0, 1);
          }
        }
      }
      boolean found = false;
      for (GameItem item : offeredItems) {
        if (item.getId() == itemID) {
          if (!item.isStackable()) {
            offeredItems.remove(item);
            found = true;
          } else {
            if (item.getAmount() > amount) {
              item.removeAmount(amount);
              found = true;
            } else {
              amount = item.getAmount();
              found = true;
              offeredItems.remove(item);
            }
          }
          break;
        }
      }
      if (found) {
        addItem(itemID, amount);
      }
      tradeConfirmed = false;
      other.tradeConfirmed = false;
      resetItems(3322);
      resetTItems(3415);
      other.resetOTItems(3416);
      send(new SendString("", 3431));
      other.send(new SendString("", 3431));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return true;
  }

  public boolean tradeItem(int itemID, int fromSlot, int amount) {
    if (System.currentTimeMillis() - lastButton > 200) {
      lastButton = System.currentTimeMillis();
    } else {
      return false;
    }
    System.out.println("amt: " + amount);
    if (!Server.itemManager.isStackable(itemID) && amount > 1) {
      for (int a = 1; a <= amount; a++) {
        int slot = findItem(itemID, playerItems, playerItemsN);
        if (slot >= 0) {
          tradeItem(itemID, slot, 1);
        }
      }
    }
    Client other = getClient(trade_reqId);
    if (!inTrade || !validClient(trade_reqId) || !canOffer) {
      System.out.println("declining in tradeItem()");
      declineTrade();
      return false;
    }
    if (!playerHasItem(itemID, amount)) {
      return false;
    }
    for (int i = 0; i < noTrade.length; i++) {
      if (itemID == noTrade[i] || itemID == noTrade[i] + 1 || (premiumItem(itemID) && !other.premium)) {
        send(new SendMessage("You can't trade that item"));
        declineTrade();
        return false;
      }
    }
    if (playerItems[fromSlot] != (itemID + 1) || playerItemsN[fromSlot] < amount) {
      return false;
    }
    if (Server.itemManager.isStackable(itemID) || Server.itemManager.isNote(itemID)) {
      boolean inTrade = false;
      for (GameItem item : offeredItems) {
        if (item.getId() == itemID) {
          inTrade = true;
          item.addAmount(amount);
          break;
        }
      }
      if (!inTrade) {
        offeredItems.add(new GameItem(itemID, amount));
      }
    } else {
      offeredItems.add(new GameItem(itemID, 1));
    }
    deleteItem(itemID, fromSlot, amount);
    resetItems(3322);
    resetTItems(3415);
    other.resetOTItems(3416);
    send(new SendString("", 3431));
    other.send(new SendString("", 3431));
    return true;
  }

  /* Shops */
  public boolean sellItem(int itemID, int fromSlot, int amount) {
    if (!Server.trading || tradeLocked) {
      send(new SendMessage("Selling has been disabled"));
      return false;
    }
    if(Server.itemManager.getShopBuyValue(itemID) < 1) {
    	send(new SendMessage("You cannot sell " + GetItemName(itemID).toLowerCase() + " in this store."));
    	return false;
    }
    for (int i = 0; i < noTrade.length; i++) {
      if ((id == noTrade[i] && !premiumItem(id))) {
        send(new SendMessage("You can't sell that item"));
        return false;
      }
    }
    if (amount > 0 && itemID == (playerItems[fromSlot] - 1)) {
      if (ShopHandler.ShopSModifier[MyShopID] > 1) {
        boolean IsIn = false;

        for (int i = 0; i <= ShopHandler.ShopItemsStandard[MyShopID]; i++) {
          if (itemID == (ShopHandler.ShopItems[MyShopID][i] - 1)) {
            IsIn = true;
            break;
          }
        }
        if (IsIn == false) {
          send(new SendMessage("You cannot sell " + GetItemName(itemID).toLowerCase() + " in this store."));
          return false;
        }
      }
      /*
       * if (Item.itemSellable[(playerItems[fromSlot] - 1)] == false) { send(new
       * SendMessage("I cannot sell " + GetItemName(itemID) + "."); return
       * false; }
       */
      Server.itemManager.isNote(playerItems[fromSlot] - 1);
      if (amount > playerItemsN[fromSlot] && (Server.itemManager.isNote(playerItems[fromSlot] - 1)
          || Server.itemManager.isStackable(playerItems[fromSlot] - 1))) {
        amount = playerItemsN[fromSlot];
      } else if (amount > GetXItemsInBag(itemID) && !Server.itemManager.isNote(playerItems[fromSlot] - 1)
          && !Server.itemManager.isStackable(playerItems[fromSlot] - 1)) {
        amount = GetXItemsInBag(itemID);
      }
      int TotPrice2;
      for (int i = amount; i > 0; i--) {
        TotPrice2 = (int) Math.floor(GetShopBuyValue(itemID, 1, fromSlot));
        if (freeSlots() > 0) {
          if (!Server.itemManager.isNote(itemID)) {
            deleteItem(itemID, GetItemSlot(itemID), 1);
          } else {
            deleteItem(itemID, fromSlot, 1);
          }
          addItem(995, TotPrice2);
          addShopItem(itemID, 1);
        } else {
          send(new SendMessage("Not enough space in your inventory."));
          break;
        }
      }
      resetItems(3823);
      resetShop(MyShopID);
      UpdatePlayerShop();
      return true;
    }
    return true;
  }

  public boolean buyItem(int itemID, int fromSlot, int amount) {
    if (amount > 0 && itemID == (ShopHandler.ShopItems[MyShopID][fromSlot] - 1)) {
      if (amount > ShopHandler.ShopItemsN[MyShopID][fromSlot]) {
        amount = ShopHandler.ShopItemsN[MyShopID][fromSlot];
      }
      int TotPrice2;
      int Slot = 0;
      if (!canUse(itemID)) {
        send(new SendMessage("You must be a premium member to buy this item"));
        send(new SendMessage("Visit Dodian.net to subscribe"));
        return false;
      }
      for (int i = amount; i > 0; i--) {
        TotPrice2 = (int) Math.floor(GetShopSellValue(itemID, 0, fromSlot));
        Slot = GetItemSlot(995);
        if (Slot == -1) {
          send(new SendMessage("You don't have enough coins."));
          break;
        }
        if (playerItemsN[Slot] >= TotPrice2) {
          if (freeSlots() > 0) {
            deleteItem(995, GetItemSlot(995), TotPrice2);
            addItem(itemID, 1);
            ShopHandler.ShopItemsN[MyShopID][fromSlot] -= 1;
            ShopHandler.ShopItemsDelay[MyShopID][fromSlot] = 0;
            if ((fromSlot + 1) > ShopHandler.ShopItemsStandard[MyShopID]) {
              ShopHandler.ShopItems[MyShopID][fromSlot] = 0;
            }
          } else {
            send(new SendMessage("Not enough space in your inventory."));
            break;
          }
        } else {
          send(new SendMessage("You don't have enough coins."));
          break;
        }
      }
      resetItems(3823);
      resetShop(MyShopID);
      UpdatePlayerShop();
      return true;
    }
    return false;
  }

  public void UpdatePlayerShop() {
    for (int i = 1; i < Constants.maxPlayers; i++) {
      if (PlayerHandler.players[i] != null) {
        if (PlayerHandler.players[i].IsShopping == true && PlayerHandler.players[i].MyShopID == MyShopID
            && i != getSlot()) {
          PlayerHandler.players[i].UpdateShop = true;
        }
      }
    }
  }

  public boolean addShopItem(int itemID, int amount) {
    boolean Added = false;

    if (amount <= 0) {
      return false;
    }
    // if (Item.itemIsNote[itemID] == true) {
    if (Server.itemManager.isNote(itemID)) {
      itemID = GetUnnotedItem(itemID);
    }
    for (int i = 0; i < ShopHandler.ShopItems.length; i++) {
      if ((ShopHandler.ShopItems[MyShopID][i] - 1) == itemID) {
        ShopHandler.ShopItemsN[MyShopID][i] += amount;
        Added = true;
      }
    }
    if (Added == false) {
      for (int i = 0; i < ShopHandler.ShopItems.length; i++) {
        if (ShopHandler.ShopItems[MyShopID][i] == 0) {
          ShopHandler.ShopItems[MyShopID][i] = (itemID + 1);
          ShopHandler.ShopItemsN[MyShopID][i] = amount;
          ShopHandler.ShopItemsDelay[MyShopID][i] = 0;
          break;
        }
      }
    }
    return true;
  }

  /* NPC Talking */
  public void UpdateNPCChat() {

    /*
     * send(new SendString("", 4902); send(new SendString("", 4903); send(new
     * SendString("", 4904); send(new SendString("", 4905); send(new
     * SendString("", 4906);
     */
    send(new SendString("", 976));
    switch (NpcDialogue) {
    case 1:

      /*
       * sendFrame200(4901, 554); send(new SendString(GetNpcName(NpcTalkTo),
       * 4902); send(new SendString("Good day, how can I help you?", 4904);
       * send(new SendNpcDialogueHead(NpcTalkTo, 4901); sendFrame164(4900);
       */
      sendFrame200(4883, 591);
      send(new SendString(GetNpcName(NpcTalkTo), 4884));
      send(new SendString("Good day, how can I help you?", 4885));
      send(new NpcDialogueHead(NpcTalkTo, 4883));
      sendFrame164(4882);
      NpcDialogueSend = true;
      break;

    case 2:
      send(new Frame171(1, 2465));
      send(new Frame171(0, 2468));
      send(new SendString("What would you like to say?", 2460));
      send(new SendString("I'd like to access my bank account, please.", 2461));
      send(new SendString("I'd like to check my PIN settings.", 2462));
      sendFrame164(2459);
      NpcDialogueSend = true;
      break;

    case 3:
      sendFrame200(4883, 591);
      send(new SendString(GetNpcName(NpcTalkTo), 4884));
      send(new SendString("Do you want to buy some runes?", 4885));
      send(new NpcDialogueHead(NpcTalkTo, 4883));
      sendFrame164(4882);
      NpcDialogueSend = true;
      nextDiag = 4;
      break;

    case 4:
      send(new Frame171(1, 2465));
      send(new Frame171(0, 2468));
      send(new SendString("Select an Option", 2460));
      send(new SendString("Yes please!", 2461));
      send(new SendString("Oh it's a rune shop. No thank you, then.", 2462));
      sendFrame164(2459);
      NpcDialogueSend = true;
      break;

    case 5:
      sendFrame200(969, 974);
      send(new SendString(getPlayerName(), 970));
      send(new SendString("Oh it's a rune shop. No thank you, then.", 971));
      send(new SendString("Click here to continue", 972));
      sendFrame185(969);
      sendFrame164(968);
      NpcDialogueSend = true;
      break;

    case 6:
      sendFrame200(4888, 592);
      send(new SendString(GetNpcName(NpcTalkTo), 4889));
      send(new SendString("Well, if you find somone who does want runes, please", 4890));
      send(new SendString("send them my way.", 4891));
      send(new SendString("Click here to continue", 4892));
      send(new NpcDialogueHead(NpcTalkTo, 4888));
      sendFrame164(4887);
      NpcDialogueSend = true;
      break;

    case 7: /* NEED TO CHANGE FOR GUARD */
      sendFrame200(4883, 591);
      send(new SendString(GetNpcName(NpcTalkTo), 4884));
      send(new SendString("Well, if you find somone who does want runes, please", 4885));
      send(new SendString("send them my way.", 4886));
      send(new NpcDialogueHead(NpcTalkTo, 4883));
      sendFrame164(4882);
      NpcDialogueSend = true;
      break;
    case 8:
      sendFrame200(4883, 591);
      send(new SendString(GetNpcName(NpcTalkTo), 4884));
      send(new SendString("Pins have not been implemented yet", 4885));
      send(new NpcDialogueHead(NpcTalkTo, 4883));
      sendFrame164(4882);
      NpcDialogueSend = true;
      break;
    case 9:
      sendFrame200(4883, 1597);
      send(new SendString(GetNpcName(NpcTalkTo), 4884));
      send(new SendString("Select an Option", 2460));
      send(new SendString("Can you teleport me to the mage arena?", 2461));
      send(new SendString("Whats at the mage arena?", 2462));
      sendFrame164(2459);
      NpcDialogueSend = true;
      break;
    case 10:
      sendFrame200(4883, 804);
      send(new SendString(GetNpcName(804), 4884));
      send(new SendString(dMsg, 4885));
      send(new NpcDialogueHead(804, 4883));
      sendFrame164(4882);
      NpcDialogueSend = true;
      break;
    case 11:
        if (taskId == 21 && !checkItem(989)) {
          taskId = 19;
          taskTotal = 20 + Misc.random(12);
          taskAmt = 0;
          sendFrame200(4883, npcFace);
          send(new SendString(GetNpcName(NpcTalkTo), 4884));
          send(new SendString("Hi noob I replaced your Greater Demons to " + taskTotal + " Skele Hounds", 4885));
          send(new NpcDialogueHead(NpcTalkTo, 4883));
          sendFrame164(4882);
          NpcDialogueSend = true;
//    case 11:
//      if (taskId == 9 && !checkItem(1543)) {
//        taskId = 16;
//        taskTotal = 5 + Misc.random(10);
//        taskAmt = 0;
//        sendFrame200(4883, npcFace);
//        send(new SendString(GetNpcName(NpcTalkTo), 4884));
//        send(new SendString("Hi there noob I replaced your Fire Giant to " + taskTotal + " Abyssal Guardians", 4885));
//        send(new NpcDialogueHead(NpcTalkTo, 4883));
//        sendFrame164(4882);
//        NpcDialogueSend = true;
      } else {
        sendFrame200(4883, npcFace);
        send(new SendString(GetNpcName(NpcTalkTo), 4884));
        send(new SendString("Hi there noob, what do you want?", 4885));
        send(new NpcDialogueHead(NpcTalkTo, 4883));
        sendFrame164(4882);
        NpcDialogueSend = true;
        nextDiag = 12;
      }
      break;
    case 12:
      send(new Frame171(1, 2465));
      send(new Frame171(0, 2468));
      send(new SendString("What would you like to say?", 2460));
      send(new SendString("I'd like a task please", 2461));
      send(new SendString("Can you teleport me to west ardougne?", 2462));
      sendFrame164(2459);
      NpcDialogueSend = true;
      break;
    case 1000:
      sendFrame200(4883, npcFace);
      send(new SendString(GetNpcName(NpcTalkTo).replace("_", " "), 4884));
      send(new SendString("Hi there, what would you like to do?", 4885));
      send(new NpcDialogueHead(NpcTalkTo, 4883));
      sendFrame164(4882);
      NpcDialogueSend = true;
      nextDiag = 1001;
      break;
    case 1001:
      send(new Frame171(1, 2465));
      send(new Frame171(0, 2468));
      send(new SendString("What would you like to do?", 2460));
      send(new SendString("Gamble", 2461));
      send(new SendString("Nothing", 2462));
      sendFrame164(2459);
      NpcDialogueSend = true;
      break;
    case 1002:
      sendFrame200(4883, npcFace);
      send(new SendString(GetNpcName(NpcTalkTo).replace("_", " "), 4884));
      send(new SendString("Cant talk right now!", 4885));
      send(new NpcDialogueHead(NpcTalkTo, 4883));
      sendFrame164(4882);
      NpcDialogueSend = true;
      break;
    case 13:
      // int[] possible = new int[count];
      ArrayList<Integer> possible = new ArrayList<Integer>();
      // count = 0;
      for (SlayerTask task : SlayerTask.values()) {
        if (task.getAssignedLevelRange().getFloor() <= getLevel(Skill.SLAYER)
            && getLevel(Skill.SLAYER) <= task.getAssignedLevelRange().getCeiling()) {
          possible.add(new Integer(task.getTaskId()));
          println("Task " + task.getTaskId() + " ok");
        } else {
          println("Task " + task.getTaskId() + " no");
          continue;
        }
      }
      Random randomGenerator = new Random();
      int newTaskId = possible.get(randomGenerator.nextInt(possible.size()));
      SlayerTask task = null;
      for (SlayerTask tasks : SlayerTask.values()) {
        if (tasks.getTaskId() == newTaskId) {
          task = tasks;
        if (task.getTaskId() == 9 && !checkItem(1543)) {
 //       	if (getLevel(Skill.SLAYER) >= 40)
          task = SlayerTask.HILL_GIANT;
          send(new SendMessage("You don't have a red key in your inventory, so your task has been reset.")); 
        }
//        else
//          task = SlayerTask.FIRE_GIANT;
////          if (task.getTaskId() == 9 && !checkItem(1543)) {
////            if (determineCombatLevel() >= 70)
////              task = SlayerTask.DAD;
////            else
////              task = SlayerTask.GUARDIAN;
////          }
        }
     }
      System.out.println("task:" + task.getTaskId() + " - " + task.getTextRepresentation());
      int amt = task.getAssignedAmountRange().getValue();
      if (taskId < 0) {
        sendFrame200(4901, npcFace);
        send(new SendString(GetNpcName(NpcTalkTo), 4902));
        send(new SendString("You must go out and kill " + amt + " " + task.getTextRepresentation() + "", 4903));
        send(new SendString("If you want a new task that's too bad", 4904));
        send(new SendString("Visit Dodian.net for a slayer guide", 4905));
        send(new SendString("", 4906));
        send(new NpcDialogueHead(NpcTalkTo, 4901));
        sendFrame164(4900);
        NpcDialogueSend = true;
        taskAmt = 0;
        taskId = task.getTaskId();
        taskTotal = amt;
        if (!playerHasItem(4155)) {
          addItem(4155, 1);
        }
      } else {
        sendFrame200(4883, npcFace);
        send(new SendString(GetNpcName(NpcTalkTo), 4884));
        send(new SendString("You already have a task!", 4885));
        send(new NpcDialogueHead(NpcTalkTo, 4883));
        sendFrame164(4882);
        NpcDialogueSend = true;
      }
      break;
    case 14:
      sendFrame200(4883, npcFace);
      send(new SendString(GetNpcName(NpcTalkTo), 4884));
      send(new SendString("Be careful out there!", 4885));
      send(new NpcDialogueHead(NpcTalkTo, 4883));
      sendFrame164(4882);
      NpcDialogueSend = true;
      triggerTele(2542, 3306, 0, false);
      break;
    case 15:

      String out = "Talk to me to get a new task!";
      System.out.println("taskId = " + taskTotal);
      if (taskId >= 0) {
        for (SlayerTask task1 : SlayerTask.values()) {
          if (taskId == task1.getTaskId()) {
            out = "You need to kill " + (taskTotal - taskAmt) + " more " + task1.getTextRepresentation();
          }
        }
      }
      sendFrame200(4883, npcFace);
      send(new SendString(GetNpcName(1596), 4884));
      send(new SendString(out, 4885));
      send(new NpcDialogueHead(1596, 4883));
      sendFrame164(4882);
      NpcDialogueSend = true;
      break;
    case 16:
      sendFrame200(4883, npcFace);
      send(new SendString(GetNpcName(NpcTalkTo), 4884));
      send(new SendString("Would you like to buy some herblore supplies?", 4885));
      send(new NpcDialogueHead(NpcTalkTo, 4883));
      sendFrame164(4882);
      NpcDialogueSend = true;
      nextDiag = 17;
      break;

    case 17:
      send(new Frame171(1, 2465));
      send(new Frame171(0, 2468));
      send(new SendString("Would you like to buy herblore supplies?", 2460));
      send(new SendString("Sure, what do you have?", 2461));
      send(new SendString("No thanks", 2462));
      sendFrame164(2459);
      NpcDialogueSend = true;
      break;
    case 19:
      sendFrame200(4883, 591);
      send(new SendString(GetNpcName(NpcTalkTo), 4884));
      send(new SendString("Would you like to buy some supplies?", 4885));
      send(new NpcDialogueHead(NpcTalkTo, 4883));
      sendFrame164(4882);
      NpcDialogueSend = true;
      nextDiag = 20;
      break;
    case 20:
      send(new Frame171(1, 2465));
      send(new Frame171(0, 2468));
      send(new SendString("Would you like to buy some supplies?", 2460));
      send(new SendString("Sure, what do you have?", 2461));
      send(new SendString("No thanks", 2462));
      sendFrame164(2459);
      NpcDialogueSend = true;
      break;
    case 21:
      sendFrame200(4888, 592);
      send(new SendString(GetNpcName(NpcTalkTo), 4889));
      send(new SendString("Hello there, would you like to change your looks?", 4890));
      send(new SendString("If so, it will be 3000 coins.", 4891));
      send(new SendString("Click here to continue", 4892));
      send(new NpcDialogueHead(NpcTalkTo, 4888));
      sendFrame164(4887);
      NpcDialogueSend = true;
      break;
    case 22:
      send(new Frame171(1, 2465));
      send(new Frame171(0, 2468));
      send(new SendString("Would you like to change your looks?", 2460));
      send(new SendString("Sure", 2461));
      send(new SendString("No thanks", 2462));
      sendFrame164(2459);
      NpcDialogueSend = true;
      break;
    case 23:
      sendFrame200(969, 974);
      send(new SendString(getPlayerName(), 970));
      send(new SendString("I would love that.", 971));
      send(new SendString("Click here to continue", 972));
      sendFrame185(969);
      sendFrame164(968);
      NpcDialogueSend = true;
      break;
    case 24:
      sendFrame200(969, 974);
      send(new SendString(getPlayerName(), 970));
      send(new SendString("Not at the moment.", 971));
      send(new SendString("Click here to continue", 972));
      sendFrame185(969);
      sendFrame164(968);
      NpcDialogueSend = true;
      break;
    case 25:
      if (playerHasItem(995, 3000)) {
        deleteItem(995, 3000);
        showInterface(3559);
        NpcDialogue = 0;
        NpcDialogueSend = false;
      } else {
        sendFrame200(4883, 591);
        send(new SendString(GetNpcName(NpcTalkTo), 4884));
        send(new SendString("You don't have 3000 coins for my services!", 4885));
        send(new NpcDialogueHead(NpcTalkTo, 4883));
        sendFrame164(4882);
        NpcDialogueSend = true;
      }
      break;
    case 26:
      send(new Frame171(1, 2465));
      send(new Frame171(0, 2468));
      send(new SendString("What would you like to do?", 2460));
      send(new SendString("Enable specials", 2461));
      send(new SendString("Disable specials", 2462));
      sendFrame164(2459);
      NpcDialogueSend = true;
      break;
    case 27:
      send(new Frame171(1, 2465));
      send(new Frame171(0, 2468));
      send(new SendString("What would you like to do?", 2460));
      send(new SendString("Enable boss yell messages", 2461));
      send(new SendString("Disable boss yell messages", 2462));
      sendFrame164(2459);
      NpcDialogueSend = true;
      break;
    case 163:
      send(new Frame171(1, 2465));
      send(new Frame171(0, 2468));
      send(new SendString("Trade in tickets? Or teleport to wilderness agility?", 2460));
      send(new SendString("Trade in tickets.", 2461));
      send(new SendString("Teleport please.", 2462));
      sendFrame164(2459);
      NpcDialogueSend = true;
      break;
    }
  }

  /* Equipment level checking */
  public int GetCLAttack(int ItemID) {
    if (ItemID == -1) {
      return 1;
    }
    String ItemName = GetItemName(ItemID);
    String ItemName2 = ItemName.replaceAll("Bronze", "");

    ItemName2 = ItemName2.replaceAll("Iron", "");
    ItemName2 = ItemName2.replaceAll("Steel", "");
    ItemName2 = ItemName2.replaceAll("Black", "");
    ItemName2 = ItemName2.replaceAll("Mithril", "");
    ItemName2 = ItemName2.replaceAll("Adamant", "");
    ItemName2 = ItemName2.replaceAll("Rune", "");
    ItemName2 = ItemName2.replaceAll("Granite", "");
    ItemName2 = ItemName2.replaceAll("Dragon", "");
    ItemName2 = ItemName2.replaceAll("Crystal", "");
    ItemName2 = ItemName2.trim();
    if (ItemName2.startsWith("claws") || ItemName2.startsWith("dagger") || ItemName2.startsWith("sword")
        || ItemName2.startsWith("scimitar") || ItemName2.startsWith("mace") || ItemName2.startsWith("longsword")
        || ItemName2.startsWith("battleaxe") || ItemName2.startsWith("warhammer") || ItemName2.startsWith("2h sword")
        || ItemName2.startsWith("harlberd")) {
      if (ItemName.startsWith("Bronze")) {
        return 1;
      } else if (ItemName.startsWith("Iron")) {
        return 1;
      } else if (ItemName.startsWith("Steel")) {
        return 10;
      } else if (ItemName.startsWith("Black")) {
        return 10;
      } else if (ItemName.startsWith("Mithril")) {
        return 20;
      } else if (ItemName.startsWith("Adamant")) {
        return 30;
      } else if (ItemName.startsWith("Rune")) {
        return 40;
      } else if (ItemName.startsWith("Dragon")) {
        return 60;
      }
    }
    return 1;
  }

  public int GetCLDefence(int ItemID) {
    if (ItemID == -1) {
      return 1;
    }
    String ItemName = GetItemName(ItemID);
    String ItemName2 = ItemName.replaceAll("Bronze", "");

    ItemName2 = ItemName2.replaceAll("Iron", "");
    ItemName2 = ItemName2.replaceAll("Steel", "");
    ItemName2 = ItemName2.replaceAll("Black", "");
    ItemName2 = ItemName2.replaceAll("Mithril", "");
    ItemName2 = ItemName2.replaceAll("Adamant", "");
    ItemName2 = ItemName2.replaceAll("Rune", "");
    ItemName2 = ItemName2.replaceAll("Granite", "");
    ItemName2 = ItemName2.replaceAll("Dragon", "");
    ItemName2 = ItemName2.replaceAll("Crystal", "");
    ItemName2 = ItemName2.trim();
    if (ItemName2.startsWith("claws") || ItemName2.startsWith("dagger") || ItemName2.startsWith("sword")
        || ItemName2.startsWith("scimitar") || ItemName2.startsWith("mace") || ItemName2.startsWith("longsword")
        || ItemName2.startsWith("battleaxe") || ItemName2.startsWith("warhammer") || ItemName2.startsWith("2h sword")
        || ItemName2.startsWith("harlberd")) {// It's a weapon,
      // weapons don't
      // required defence !
    } else if (ItemName.startsWith("Ahrims") || ItemName.startsWith("Karil") || ItemName.startsWith("Torag")
        || ItemName.startsWith("Verac") || ItemName.endsWith("Guthan") || ItemName.endsWith("Dharok")) {
      if (ItemName.endsWith("staff") || ItemName.endsWith("crossbow") || ItemName.endsWith("hammers")
          || ItemName.endsWith("flail") || ItemName.endsWith("warspear") || ItemName.endsWith("greataxe")) {// No
                                                                                                            // defence
                                                                                                            // for
                                                                                                            // the
        // barrow weapons
      } else {
        return 1;
      }
    } else {
      if (ItemName.startsWith("Saradomin") || ItemName.startsWith("Zamorak") || ItemName.startsWith("Guthix")
          && ItemName.toLowerCase().contains("staff") && ItemName.toLowerCase().contains("cape"))
        return 1;
      if (ItemName.startsWith("Bronze")) {
        return 1;
      } else if (ItemName.startsWith("Iron")) {
        return 1;
      } else if (ItemName.startsWith("Steel") && !ItemName.contains("arrow")) {
        return 5;
      } else if (ItemName.startsWith("Black") && !ItemName.contains("hide") && !ItemName.contains("cavalier")) {
        return 10;
      } else if (ItemName.startsWith("Mithril") && !ItemName.contains("arrow")) {
        return 20;
      } else if (ItemName.startsWith("Adamant") && !ItemName.contains("arrow")) {
        return 30;
      } else if (ItemName.startsWith("Rune") && !ItemName.endsWith("cape") && !ItemName.contains("arrow")) {
        return 40;
      } else if (ItemName.startsWith("Dragon") && !ItemName.contains("hide")) {
        return 60;
      }
    }
    if (ItemName.toLowerCase().contains("ghostly"))
      return 70;
    if (ItemName.startsWith("Skeletal"))
      return 1;
    if (ItemName.startsWith("Snakeskin body") || ItemName.startsWith("Snakeskin chaps"))
      return 60;
    if (ItemID == 1135)
      return 40;
    if (ItemID == 2499)
      return 40;
    if (ItemID == 2501)
      return 40;
    if (ItemID == 2503)
      return 40;
    return 1;
  }

  public int GetCLStrength(int ItemID) {
    if (ItemID == -1) {
      return 1;
    }
    String ItemName = GetItemName(ItemID);

    if (ItemName.startsWith("Granite")) {
      return 50;
    } else if (ItemName.startsWith("Torags hammers") || ItemName.endsWith("Dharoks greataxe")) {
      return 1;
    }
    return 1;
  }

  public int GetCLMagic(int ItemID) {
    if (ItemID == -1) {
      return 1;
    }
    String ItemName = GetItemName(ItemID);
    if (ItemName.toLowerCase().contains("ghostly"))
      return 70;
    if (ItemName.startsWith("Ahrim")) {
      return 90;
    }
    return 1;
  }

  public int GetCLRanged(int ItemID) {
    if (ItemID == -1) {
      return 1;
    }
    String ItemName = GetItemName(ItemID);

    if (ItemName.startsWith("Karil")) {
      return 90;
    }
    if (ItemName.startsWith("Snakeskin")) {
      return 80;
    }
    if (ItemName.startsWith("New crystal bow")) {
      return 70;
    }
    if (ItemName.startsWith("Oak")) {
      return 1;
    }
    if (ItemName.startsWith("Willow")) {
      return 20;
    }
    if (ItemName.startsWith("Maple")) {
      return 30;
    }
    if (ItemName.startsWith("Yew")) {
      return 40;
    }
    if (ItemName.startsWith("Magic") && !ItemName.toLowerCase().contains("cape")) {
      return 50;
    }
    if (ItemName.startsWith("Green d")) {
      return 40;
    }
    if (ItemName.startsWith("Blue d")) {
      return 50;
    }
    if (ItemName.startsWith("Red d")) {
      return 60;
    }
    if (ItemName.startsWith("Black d")) {
      return 70;
    }
    return 1;
  }

  public void setInterfaceWalkable(int ID) {
    getOutputStream().createFrame(208);
    getOutputStream().writeWordBigEndian_dup(ID);
    flushOutStream();
  }

  public void RefreshDuelRules() {
    /*
     * Danno: Testing ticks/armour blocks.
     */

    // "No Ranged", "No Melee", "No Magic",
    // "No Gear Change", "Fun Weapons", "No Retreat", "No Drinks",
    // "No Food", "No prayer", "No Movement", "Obstacles" };
    int configValue = 0;
    for (int i = 0; i < duelLine.length; i++) {
      if (duelRule[i]) {
        send(new SendString(/* "@red@" + */duelNames[i], duelLine[i]));
        configValue += stakeConfigId[i + 11];
      } else {
        send(new SendString(/* "@gre@" + */duelNames[i], duelLine[i]));
      }
    }
    for (int i = 0; i < duelBodyRules.length; i++) {
      if (duelBodyRules[i])
        configValue += stakeConfigId[i];
    }
    frame87(286, configValue);
  }

  public void DuelVictory() {
    Client other = getClient(duel_with);
    if (validClient(duel_with)) {
      send(new SendMessage("You have defeated " + other.getPlayerName() + "!"));
      send(new SendString("" + other.determineCombatLevel(), 6839));
      send(new SendString(other.getPlayerName(), 6840));
    }
    boolean stake = false;
    String playerStake = "";
    for (GameItem item : offeredItems) {
      if (item.getId() > 0 && item.getAmount() > 0) {
        playerStake += "(" + item.getId() + ", " + item.getAmount() + ")";
        stake = true;
      }
    }
    String opponentStake = "";
    for (GameItem item : otherOfferedItems) {
      if (item.getId() > 0 && item.getAmount() > 0) {
        opponentStake += "(" + item.getId() + ", " + item.getAmount() + ")";
        stake = true;
      }
    }
    ResetAttack();

    if (stake) {
      DuelLog.recordDuel(this.getPlayerName(), other.getPlayerName(), playerStake, opponentStake, this.getPlayerName());
      itemsToVScreen_old();
      acceptDuelWon();
      if (other != null) {
        other.resetDuel();
      }
    } else {
      if (validClient(duel_with))
        other.resetDuel();
      resetDuel();
      // duelStatus = -1;
    }
    if (stake) {
      showInterface(6733);
    }

    /*
     * Danno: Reset health.
     */
    setCurrentHealth(getLevel(Skill.HITPOINTS));
    refreshSkill(Skill.HITPOINTS);
    getUpdateFlags().setRequired(UpdateFlag.APPEARANCE, true);

  }

  public void itemsToVScreen_old() {
    if (disconnectAt > 0) {
      acceptDuelWon();
      return;
    }
    getOutputStream().createFrameVarSizeWord(53);
    getOutputStream().writeWord(6822);
    getOutputStream().writeWord(otherOfferedItems.toArray().length);
    for (GameItem item : otherOfferedItems) {
      if (item.getAmount() > 254) {
        getOutputStream().writeByte(255); // item's stack count. if over 254,
        // write byte 255
        getOutputStream().writeDWord_v2(item.getAmount()); // and then the real
        // value with
        // writeDWord_v2
      } else {
        getOutputStream().writeByte(item.getAmount());
      }
      getOutputStream().writeWordBigEndianA(item.getId() + 1); // item id
    }
    getOutputStream().endFrameVarSizeWord();
  }

  public void refreshDuelScreen() {
    Client other = getClient(duel_with);
    if (!validClient(duel_with)) {
      return;
    }
    getOutputStream().createFrameVarSizeWord(53);
    getOutputStream().writeWord(6669);
    getOutputStream().writeWord(offeredItems.toArray().length);
    int current = 0;
    for (GameItem item : offeredItems) {
      if (item.getAmount() > 254) {
        getOutputStream().writeByte(255); // item's stack count. if over 254,
        // write byte 255
        getOutputStream().writeDWord_v2(item.getAmount()); // and then the real
        // value with
        // writeDWord_v2
      } else {
        getOutputStream().writeByte(item.getAmount());
      }
      getOutputStream().writeWordBigEndianA(item.getId() + 1); // item id
      current++;
    }
    if (current < 27) {
      for (int i = current; i < 28; i++) {
        getOutputStream().writeByte(1);
        getOutputStream().writeWordBigEndianA(-1);
      }
    }
    getOutputStream().endFrameVarSizeWord();
    getOutputStream().createFrameVarSizeWord(53);
    getOutputStream().writeWord(6670);
    getOutputStream().writeWord(other.offeredItems.toArray().length);
    current = 0;
    for (GameItem item : other.offeredItems) {
      if (item.getAmount() > 254) {
        getOutputStream().writeByte(255); // item's stack count. if over 254,
        // write byte 255
        getOutputStream().writeDWord_v2(item.getAmount()); // and then the real
        // value with
        // writeDWord_v2
      } else {
        getOutputStream().writeByte(item.getAmount());
      }
      getOutputStream().writeWordBigEndianA(item.getId() + 1); // item id
      current++;
    }
    if (current < 27) {
      for (int i = current; i < 28; i++) {
        getOutputStream().writeByte(1);
        getOutputStream().writeWordBigEndianA(-1);
      }
    }
    getOutputStream().endFrameVarSizeWord();
  }

  public boolean stakeItem(int itemID, int fromSlot, int amount) {
    if (System.currentTimeMillis() - lastButton < 800) {
      return false;
    }
    lastButton = System.currentTimeMillis();
    if (!Server.itemManager.isStackable(itemID) && !Server.itemManager.isNote(itemID) && amount > 1) {
      for (int a = 1; a <= amount; a++) {
        int slot = findItem(itemID, playerItems, playerItemsN);
        if (slot >= 0) {
          stakeItem(itemID, slot, 1);
        }
      }
    }
    for (int i = 0; i < noTrade.length; i++) {
      if (itemID == noTrade[i] || itemID == noTrade[i] + 1 || premiumItem(itemID)) {
        send(new SendMessage("You can't trade that item"));
        // declineDuel();
        return false;
      }
    }
    if (!Server.itemManager.isTradable(itemID)) {
      send(new SendMessage("You can't trade that item"));
      return false;
    }
    Client other = getClient(duel_with);
    if (!inDuel || !validClient(duel_with)) {
      declineDuel();
      return false;
    }
    if (!canOffer) {
      declineDuel();
      return false;
    }
    if (!playerHasItem(itemID, amount)) {
      return false;
    }
    if (playerItems[fromSlot] != (itemID + 1) || playerItemsN[fromSlot] < amount) {
      return false;
    }
    if (Server.itemManager.isStackable(itemID) || Server.itemManager.isNote(itemID)) {
      boolean inTrade = false;
      for (GameItem item : offeredItems) {
        if (item.getId() == itemID) {
          inTrade = true;
          item.addAmount(amount);
          break;
        }
      }
      if (!inTrade) {
        offeredItems.add(new GameItem(itemID, amount));
      }
    } else {
      offeredItems.add(new GameItem(itemID, 1));
    }
    deleteItem(itemID, fromSlot, amount);
    resetItems(3214);
    resetItems(3322);
    other.resetItems(3214);
    other.resetItems(3322);
    refreshDuelScreen();
    other.refreshDuelScreen();
    send(new SendString("", 6684));
    other.send(new SendString("", 6684));
    return true;
  }

  public boolean fromDuel(int itemID, int fromSlot, int amount) {
    if (System.currentTimeMillis() - lastButton < 800) {
      return false;
    }
    lastButton = System.currentTimeMillis();
    Client other = getClient(duel_with);
    if (!inDuel || !validClient(duel_with)) {
      declineDuel();
      return false;
    }
    if (!canOffer) {
      return false;
    }
    if (!Server.itemManager.isStackable(itemID) && amount > 1) {
      for (int a = 1; a <= amount; a++) {
        int slot = findItem(itemID, playerItems, playerItemsN);
        if (slot >= 0) {
          fromDuel(itemID, 0, 1);
        }
      }
    }
    boolean found = false;
    for (GameItem item : offeredItems) {
      if (item.getId() == itemID) {
        if (!item.isStackable()) {
          // if (!item.stackable) {
          offeredItems.remove(item);
          found = true;
        } else {
          if (item.getAmount() > amount) {
            item.removeAmount(amount);
            found = true;
          } else {
            amount = item.getAmount();
            found = true;
            offeredItems.remove(item);
          }
        }
        break;
      }
    }
    if (found) {
      addItem(itemID, amount);
    }
    duelConfirmed = false;
    other.duelConfirmed = false;
    resetItems(3214);
    other.resetItems(3214);
    resetItems(3322);
    other.resetItems(3322);
    refreshDuelScreen();
    other.refreshDuelScreen();
    other.send(new SendString("", 6684));

    return true;
  }

  public static String passHash(String in, String salt) {
    String passM = new MD5(in).compute();
    return new MD5(passM + salt).compute();
  }

  public String getLook() {
    int[] body = { getPlayerLook()[0], getHead(), getBeard(), getTorso(), getArms(), getHands(), getLegs(), getFeet(), pHairC, pTorsoC, pLegsC, pFeetC,
        pSkinC, getPlayerLook()[1], getPlayerLook()[2], getPlayerLook()[3], getPlayerLook()[4], getPlayerLook()[5] };
    String out = "";
    for (int i = 0; i < body.length; i++) {
      out += body[i] + " ";
    }
    return out;
    // apset = true;
    // appearanceUpdateRequired = true;
    // lookUpdate = true;
  }

  public void setLook(int[] parts) {
    if (parts.length != 18) {
      println("setLook:  Invalid array length!");
      return;
    }
    setGender(parts[0]);
    getPlayerLook()[0] = parts[0];
    setHead(parts[1]);
    setBeard(parts[2]);
    setTorso(parts[3]);
    setArms(parts[4]);
    setHands(parts[5]);
    setLegs(parts[6]);
    setFeet(parts[7]);
    pHairC = parts[8];
    pTorsoC = parts[9];
    pLegsC = parts[10];
    pFeetC = parts[11];
    pSkinC = parts[12];
    getPlayerLook()[1] = parts[13];
    getPlayerLook()[2] = parts[14];
    getPlayerLook()[3] = parts[15];
    getPlayerLook()[4] = parts[16];
    getPlayerLook()[5] = parts[17];
    getUpdateFlags().setRequired(UpdateFlag.APPEARANCE, true);
  }

  public double maxRangeHit() {
    int range = getLevel(Skill.RANGED);
    if (rangePot > 0.0) {
      range = (int) ((1 + (rangePot / 100)) * getLevel(Skill.RANGED));
    }
    int hit = (int) ((playerBonus[4] / 15) + (range / 4));
    return hit;
  }

  public boolean runeCheck(int spell) {
    if (playerHasItem(565)) {
      return true;
    }
    send(new SendMessage("This spell requires 1 blood rune"));
    return false;
  }

  public void resetPos() {
    teleportToX = 2606;
    teleportToY = 3102;
    send(new SendMessage("Welcome to Yanille"));
  }

  public boolean canUse(int id) {
    if (!premium && premiumItem(id)) {
      return false;
    }
    return true;
  }

  public boolean premiumItem(int id) {
    return Server.itemManager.isPremium(id);
  }

  public void debug(String text) {
    if (debug) {
      send(new SendMessage(text));
    }
  }

  public void triggerRandom() {
    if (!randomed || !randomed2) {
      random_skill = Utils.random(20);
      send(new SendString("Click the @or1@" + Skill.getSkill(random_skill).getName() + " @yel@button", 2810));
      send(new SendString("", 2811));
      send(new SendString("", 2831));
      randomed = true;
      showInterface(2808);
    }
  }

  public int findItem(int id, int[] items, int[] amounts) {
    for (int i = 0; i < playerItems.length; i++) {
      if ((items[i] - 1) == id && amounts[i] > 0) {
        return i;
      }
    }
    return -1;
  }

  public boolean hasSpace() {
    for (int i = 0; i < playerItems.length; i++) {
      if (playerItems[i] == -1 || playerItems[i] == 0) {
        return true;
      }
    }
    return false;
  }

  public int getFreeSpace() {
    int spaces = 0;
    for (int i = 0; i < playerItems.length; i++) {
      if (playerItems[i] == -1 || playerItems[i] == 0) {
        spaces += 1;
      }
    }
    return spaces;
  }

  @SuppressWarnings("unused")
  public void smelt(int id) {
    requestAnim(0x383, 0);
    smelt_id = id;
    smelting = true;
    int smelt_barId = -1;
    ArrayList<Integer> removed = new ArrayList<Integer>();
    if (smeltCount < 1) {
      resetAction(true);
      return;
    }
    smeltCount--;
    switch (id) {
    case 2349: // bronze
      if (playerHasItem(436) && playerHasItem(438)) {
        smelt_barId = 2349;
        removed.add(436);
        removed.add(438);
      }
      break;
    case 2351: // iron ore
      if (playerHasItem(440)) {
        int ran = Utils.random(3);
        if (ran == 1 || ran == 2) {
          smelt_barId = 2351;
          removed.add(440);
        } else {
          smelt_barId = 0;
          removed.add(440);
          send(new SendMessage("You fail to refine the iron"));
        }
      }
      break;
    case 2353:
      if (playerHasItem(440) && playerHasItem(453, 2)) {
        smelt_barId = 2353;
        removed.add(440);
        removed.add(453);
        removed.add(453);
      }
      break;
    case 2357:
      if (playerHasItem(444, 1)) {
        smelt_barId = 2357;
        removed.add(444);
      }
      break;
    case 2359:
      if (playerHasItem(447) && playerHasItem(453, 3)) {
        smelt_barId = 2359;
        removed.add(447);
        removed.add(453);
        removed.add(453);
        removed.add(453);
      }
      break;
    case 2361:
      if (playerHasItem(449) && playerHasItem(453, 4)) {
        smelt_barId = 2361;
        removed.add(449);
        removed.add(453);
        removed.add(453);
        removed.add(453);
        removed.add(453);
      }
      break;
    case 2363:
      if (playerHasItem(451) && playerHasItem(453, 8)) {
        smelt_barId = 2363;
        removed.add(451);
        for (int i = 0; i < 8; i++)
          removed.add(453);
      }
      break;
    default:
      println("Unknown smelt: " + id);
      break;
    }
    if (smelt_barId == -1) {
      resetAction();
      return;
    }
    if (true) {
      for (Integer intId : removed) {
        int removeId = intId.intValue();
        deleteItem(removeId, 1);
      }
      if (smelt_barId > 0) {
        addItem(smelt_barId, 1);
        giveExperience(smeltExperience, Skill.SMITHING);
      }
    } else {
      send(new SendMessage("Your inventory is full!"));
      resetAction();
    }
  }

  public void resetAction(boolean full) {
    smelting = false;
    smelt_id = -1;
    shafting = false;
    spinning = false;
    crafting = false;
    fishing = false;
    stringing = false;
    mining = false;
    cooking = false;
    filling = false;
    mixPots = false;
    goldCrafting = false;
    goldIndex = -1;
    goldSlot = -1;
    if (fletchings || fletchingOther) {
      getUpdateFlags().setRequired(UpdateFlag.APPEARANCE, true);
    }
    fletchings = false;
    fletchingOther = false;
    if (full) {
      rerequestAnim();
    }
  }

  public void resetAction() {
    resetAction(true);
  }

  public void shaft() {
    if (IsCutting || isFiremaking) {
    	IsCutting = false;
    	isFiremaking = false;
    }

    send(new RemoveInterfaces());
    if (playerHasItem(1511)) {
      deleteItem(1511, 1);
      addItem(52, 15);
      requestAnim(1248, 0);
      giveExperience(50, Skill.FLETCHING);
    } else {
      resetAction();
    }
  }

  public void fill() {
    if (playerHasItem(229)) {
      deleteItem(229, 1);
      addItem(227, 1);
      requestAnim(832, 0);
      animationReset = System.currentTimeMillis() + 600;
    } else {
      resetAction(true);
      resetAction();
    }
  }

  public long getSpinSpeed() {
    if (premium)
      return 600;
    return 1200;
  }

  public void spin() {
    if (playerHasItem(1779)) {
      deleteItem(1779, 1);
      addItem(1777, 1);
      lastAction = System.currentTimeMillis();
      giveExperience(60, Skill.CRAFTING);
    } else if (playerHasItem(1737)) {
      deleteItem(1737, 1);
      addItem(1759, 1);
      lastAction = System.currentTimeMillis();
      giveExperience(60, Skill.CRAFTING);
    } else {
      resetAction(true);
    }
  }

  public void replaceDoors() {
    for (int d = 0; d < DoorHandler.doorX.length; d++) {
      if (DoorHandler.doorX[d] > 0 && DoorHandler.doorHeight[d] == getPosition().getZ()
          && Math.abs(DoorHandler.doorX[d] - getPosition().getX()) <= 120
          && Math.abs(DoorHandler.doorY[d] - getPosition().getY()) <= 120) {
        if (distanceToPoint(DoorHandler.doorX[d], DoorHandler.doorY[d]) < 50) {
          ReplaceObject(DoorHandler.doorX[d], DoorHandler.doorY[d], DoorHandler.doorId[d], DoorHandler.doorFace[d], 0);
        }
      }
    }
  }

  public void openTan() {
    send(new SendString("Regular Leather", 14777));
    send(new SendString("50gp", 14785));
    send(new SendString("Hard Leather", 14781));
    send(new SendString("100gp", 14789));
    send(new SendString("", 14778));
    send(new SendString("", 14786));
    send(new SendString("", 14782));
    send(new SendString("", 14790));
    int[] soon = { 14779, 14787, 14783, 14791, 14780, 14788, 14784, 14792 };
    String[] dhide = { "Green", "Red", "Blue", "Black" };
    String[] cost = { "1,000gp", "5,000gp", "2,000gp", "10,000gp" };
    int type = 0;
    for (int i = 0; i < soon.length; i++) {
      if (type == 0) {
        send(new SendString(dhide[(int) (i / 2)], soon[i]));
        type = 1;
      } else {
        send(new SendString(cost[(int) (i / 2)], soon[i]));
        type = 0;
      }
    }
    sendFrame246(14769, 250, 1741);
    sendFrame246(14773, 250, 1743);
    sendFrame246(14771, 250, 1753);
    sendFrame246(14772, 250, 1751);
    sendFrame246(14775, 250, 1749);
    sendFrame246(14776, 250, 1747);
    showInterface(14670);

  }

  public void startTan(int amount, int type) {
    int done = 0;
    int[] hide = { 1739, 1739, 1753, 1751, 1749, 1747 };
    int[] leather = { 1741, 1741, 1745, 2505, 2507, 2509 };
    int[] charge = { 50, 100, 1000, 2000, 5000, 10000 };
    while (done < amount && playerHasItem(hide[type]) && playerHasItem(995, charge[type])) {
      deleteItem(hide[type], 1);
      deleteItem(995, charge[type]);
      addItem(leather[type], 1);
      done++;
    }
  }

  public void startCraft(int actionbutton) {
    send(new RemoveInterfaces());
    int[] buttons = { 33187, 33186, 33185, 33190, 33189, 33188, 33193, 33192, 33191, 33196, 33195, 33194, 33199, 33198,
        33197, 33202, 33201, 33200, 33205, 33204, 33203 };
    int[] amounts = { 1, 5, 10, 1, 5, 10, 1, 5, 10, 1, 5, 10, 1, 5, 10, 1, 5, 10, 1, 5, 10 };
    int[] ids = { 1129, 1129, 1129, 1059, 1059, 1059, 1061, 1061, 1061, 1063, 1063, 1063, 1095, 1095, 1095, 1169, 1169,
        1169, 1167, 1167, 1167 };
    int[] levels = { 14, 1, 7, 11, 18, 38, 9 };
    int[] exp = { 27, 14, 16, 22, 27, 37, 19 };
    int amount = 0, id = -1;
    int index = 0;
    for (int i = 0; i < buttons.length; i++) {
      if (actionbutton == buttons[i]) {
        amount = amounts[i];
        id = ids[i];
        // index = i % 3; Dafuq is this shiet?
        index = (int) (i / 3);
        break;
      }
    }
    if (getLevel(Skill.CRAFTING) >= levels[index]) {
      crafting = true;
      cItem = id;
      cAmount = amount;
      cLevel = levels[index];
      cExp = Math.round(exp[index] * 10);
      cSelected = 1741;
    } else {
      send(new SendMessage("Requires level " + levels[index]));
    }
  }

  public void craft() {
    if (getLevel(Skill.CRAFTING) < cLevel) {
      send(new SendMessage("You need " + cLevel + " crafting to make a " + GetItemName(cItem)));
      resetAction(true);
      return;
    }
    getUpdateFlags().setRequired(UpdateFlag.APPEARANCE, true);
    if (playerHasItem(cSelected, 1) && playerHasItem(1734) && cAmount > 0) {
      deleteItem(cSelected, 1);
      deleteItem(1734, 1);
      send(new SendMessage("You make some " + GetItemName(cItem)));
      addItem(cItem, 1);
      giveExperience(cExp, Skill.CRAFTING);
      cAmount--;
      if (cAmount < 1) {
        resetAction(true);
      }
    } else {
      resetAction(true);
    }
  }

  public void craftMenu(int i) {
    send(new SendString("What would you like to make?", 8898));
    send(new SendString("Vambraces", 8889));
    send(new SendString("Chaps", 8893));
    send(new SendString("Body", 8897));
    sendFrame246(8883, 250, Constants.gloves[i]);
    sendFrame246(8884, 250, Constants.legs[i]);
    sendFrame246(8885, 250, Constants.chests[i]);
    sendFrame164(8880);
  }

  public void startHideCraft(int b) {
    int[] buttons = { 34185, 34184, 34183, 34182, 34189, 34188, 34187, 34186, 34193, 34192, 34191, 34190 };
    int[] amounts = { 1, 5, 10, 27 };
    int index = 0;
    int index2 = 0;
    for (int i = 0; i < buttons.length; i++) {
      if (buttons[i] == b) {
        index = i % 4;
        index2 = (int) (i / 4);
        break;
      }
    }
    cAmount = amounts[index];
    cSelected = Constants.leathers[cIndex];
    int required = 99;
    if (index2 == 0) {
      required = Constants.gloveLevels[cIndex];
      cItem = Constants.gloves[cIndex];
      cExp = Constants.gloveExp[cIndex];
    } else if (index2 == 1) {
      required = Constants.legLevels[cIndex];
      cItem = Constants.legs[cIndex];
      cExp = Constants.legExp[cIndex];
    } else {
      required = Constants.chestLevels[cIndex];
      cItem = Constants.chests[cIndex];
      cExp = Constants.chestExp[cIndex];
    }
    if (getLevel(Skill.CRAFTING) >= required) {
      cExp = (int) (cExp * 8);
      crafting = true;
      send(new RemoveInterfaces());
    } else {
      send(new SendMessage("Requires level " + required));
    }
  }

  public void modYell(String msg) {
    for (int i = 0; i < PlayerHandler.players.length; i++) {
      Client p = (Client) PlayerHandler.players[i];
      if (p != null && !p.disconnected && p.getPosition().getX() > 0 && p.dbId > 0 && p.playerRights > 0) {
        p.send(new SendMessage(msg));
      }
    }
  }

  public void triggerTele(int x, int y, int height, boolean prem) {
    triggerTele(x, y, height, prem, 1816);
  }

  public void triggerTele(int x, int y, int height, boolean prem, int emote) {
    if (inDuel) {
      return;
    }
    if (randomed2) {
      send(new SendMessage("You can't teleport out of here!"));
      return;
    }
    if (wildyLevel > 20) {
      send(new SendMessage("You can't teleport out of here!"));
      return;
    }
    if (System.currentTimeMillis() - lastTeleport >= 3000) {
      lastTeleport = System.currentTimeMillis();
      resetAction();
      resetWalkingQueue();
      if (prem && !premium) {
        send(new SendMessage("This spell is only available to premium members, visit Dodian.net for info"));
        return;
      }
      if (duelStatus == 3) {
        return;
      }

      tX = x;
      tY = y;
      tH = height;
      tStage = 1;
      tTime = 0;
      tEmote = emote;
    }
  }

  public void startSmelt(int id) {
    int[] amounts = { 1, 5, 10, 27 };
    int index = 0, index2 = 0;
    for (int i = 0; i < Utils.buttons_smelting.length; i++) {
      if (id == Utils.buttons_smelting[i]) {
        index = i % 4;
        index2 = (int) (i / 4);
      }
    }
    smelt_id = Utils.smelt_bars[index2][0];
    smeltCount = amounts[index];
    smeltExperience = Utils.smelt_bars[index2][1] * 4;
    smelting = true;
    send(new RemoveInterfaces());
  }

  public void startFishing(int object, int click) {
    boolean valid = false;
    for (int i = 0; i < Utils.fishSpots.length; i++) {
      if (Utils.fishSpots[i] == object) {
        if (click == 1 && (i == 0 || i == 2 || i == 4 || i == 6)) {
          valid = true;
          fishIndex = i;
          break;
        } else if (click == 2 && (i == 1 || i == 3 || i == 5 || i == 7)) {
          valid = true;
          fishIndex = i;
          break;
        }
      }
    }
    if (!valid) {
      resetAction(true);
      return;
    }
    if (!playerHasItem(-1)) {
      send(new SendMessage("Not enough inventory space!"));
      resetAction(true);
      return;
    }
    if ((fishIndex == 4 || fishIndex >= 6) && !premium) {
      send(new SendMessage("You need to be premium to fish from this spot!"));
      resetAction(true);
      return;
    }
    if (!playerHasItem(314) && fishIndex == 1) {
      send(new SendMessage("You do not have any feathers!"));
      resetAction(true);
      return;
    }
    if (getLevel(Skill.FISHING) < Utils.fishReq[fishIndex]) {
      send(new SendMessage("You need " + Utils.fishReq[fishIndex] + " fishing to fish here"));
      resetAction(true);
      return;
    }
    if (!playerHasItem(Utils.fishTool[fishIndex])) {
      send(new SendMessage("You need a " + GetItemName(Utils.fishTool[fishIndex]) + " to fish here"));
      resetAction(true);
      return;
    }
    lastAction = System.currentTimeMillis() + Utils.fishTime[fishIndex];
    requestAnim(Utils.fishAnim[fishIndex], 0);
    fishing = true;
  }

  public void fish(int id) {
    lastAction = System.currentTimeMillis();
    if (!playerHasItem(-1)) {
      send(new SendMessage("Not enough inventory space!"));
      resetAction(true);
      return;
    }
    if (!playerHasItem(314) && fishIndex == 1) {
      send(new SendMessage("You do not have any feathers!"));
      resetAction(true);
      return;
    }
    if (fishIndex == 1) {
      deleteItem(314, 1);
      int random = Misc.random(6);
      System.out.println("random: " + random);
      if (getLevel(Skill.FISHING) >= 30 && random < 3) {
        addItem(331, 1);
        giveExperience(Utils.fishExp[fishIndex] + 100, Skill.FISHING);
        send(new SendMessage("You fish some salmon."));
      } else {
        giveExperience(Utils.fishExp[fishIndex], Skill.FISHING);
        addItem(Utils.fishId[fishIndex], 1);
        send(new SendMessage("You fish some trout."));
      }
    } else {
      giveExperience(Utils.fishExp[fishIndex], Skill.FISHING);
      addItem(Utils.fishId[fishIndex], 1);
      send(new SendMessage("You fish some " + GetItemName(Utils.fishId[fishIndex]).toLowerCase() + "."));
    }
    requestAnim(Utils.fishAnim[fishIndex], 0);
    if (Utils.random(50) == 5) {
      send(new SendMessage("You take a rest"));
      resetAction(true);
      return;
    }
  }

  public void startCooking(int id) {
    boolean valid = false;
    for (int i = 0; i < Utils.cookIds.length; i++) {
      if (id == Utils.cookIds[i]) {
        cookIndex = i;
        valid = true;
      }
    }
    if (valid) {
      getOutputStream().createFrame(27);
      enterAmountId = 1; // cooking
    }

  }

  public void cook() {
    if (inTrade || inDuel) {
      resetAction(true);
      return;
    }
    if (!playerHasItem(Utils.cookIds[cookIndex])) {
      send(new SendMessage("You are out of fish"));
      resetAction(true);
      return;
    }
    if (cookAmount < 1) {
      resetAction(true);
      return;
    }
    cookAmount--;
    int id = Utils.cookIds[cookIndex];
    int ran = 0, index = 0;
    for (int i = 0; i < Utils.cookIds.length; i++) {
      if (id == Utils.cookIds[i]) {
        index = i;
      }
    }
    if (getLevel(Skill.COOKING) < Utils.cookLevel[index]) {
      send(new SendMessage("You need " + Utils.cookLevel[index] + " cooking to cook this"));
      resetAction(true);
      return;
    }
    switch (id) {
    case 317:
      ran = 30 - getLevel(Skill.COOKING);
      break;
    case 335:
      ran = 40 - getLevel(Skill.COOKING);
      break;
    case 331:
      ran = 55 - getLevel(Skill.COOKING);
      break;
    case 377:
      ran = 70 - getLevel(Skill.COOKING);
      break;
    case 371:
      ran = 80 - getLevel(Skill.COOKING);
      break;
    case 7944:
      ran = 90 - getLevel(Skill.COOKING);
      break;
    case 383:
      ran = 95 - getLevel(Skill.COOKING);
      break;
    case 395:
      ran = 110 - getLevel(Skill.COOKING);
      break;
    case 389:
      ran = 125 - getLevel(Skill.COOKING);
      break;
    }
    if (getEquipment()[Equipment.Slot.HANDS.getId()] == 775)
      ran -= 5;
    if (getEquipment()[Equipment.Slot.HEAD.getId()] == 1949)
      ran -= 5;
    if (getEquipment()[Equipment.Slot.HEAD.getId()] == 1949 && getEquipment()[Equipment.Slot.HANDS.getId()] == 775)
      ran -= 5;
    if (ran < 0) {
      ran = 0;
    }
    boolean success = true;
    if (Utils.random(100) < ran) {
      success = false;
    }
    if (Utils.cookExp[index] > 0) {
      deleteItem(id, 1);

      if (success) {
        addItem(Utils.cookedIds[index], 1);
        send(new SendMessage("You cook the " + GetItemName(id)));
        giveExperience(Utils.cookExp[index], Skill.COOKING);
      } else {
        addItem(Utils.burnId[index], 1);
        send(new SendMessage("You burn the " + GetItemName(id)));
      }
    }
  }

  public boolean inWildy() {
    // if(absX > 2600 && absY > 8900) return true;
    if (wildyLevel > 0)
      return true;
    return false;
  }

  public void openTrade() {
    if (TradeDupeFix.contains(this, (Client) PlayerHandler.players[trade_reqId])) {
      System.out.println("dupe prevented");
      return;
    }
    TradeDupeFix.add(this, (Client) PlayerHandler.players[trade_reqId]);
    send(new InventoryInterface(3323, 3321)); // trading window + bag
    inTrade = true;
    tradeRequested = false;
    resetItems(3322);
    resetTItems(3415);
    resetOTItems(3416);
    String out = PlayerHandler.players[trade_reqId].getPlayerName();
    if (PlayerHandler.players[trade_reqId].playerRights == 1) {
      out = "@cr1@" + out;
    } else if (PlayerHandler.players[trade_reqId].playerRights == 2) {
      out = "@cr2@" + out;
    }
    send(new SendString("Trading With: " + PlayerHandler.players[trade_reqId].getPlayerName(), 3417));
    send(new SendString("", 3431));
    send(new SendString("Are you sure you want to make this trade?", 3535));
  }

  public void declineTrade() {
    declineTrade(true);
  }

  public void declineTrade(boolean tellOther) {
    send(new RemoveInterfaces());
    Client other = getClient(trade_reqId);
    TradeDupeFix.remove(this, other);
    TradeDupeFix.remove(other, this);
    if (tellOther && validClient(trade_reqId)) {
      // other.send(new SendMessage(playerName + " declined the trade");
      other.declineTrade(false);
    }

    for (GameItem item : offeredItems) {
      if (item.getAmount() < 1) {
        continue;
      }
      println("returning item " + item.getId() + ", " + item.getAmount());
      if (Server.itemManager.isStackable(item.getId())) {
        // if (item.stackable) {
        addItem(item.getId(), item.getAmount());
      } else {
        for (int i = 0; i < item.getAmount(); i++) {
          addItem(item.getId(), 1);
        }
      }
    }
    canOffer = true;
    tradeConfirmed = false;
    tradeConfirmed2 = false;
    offeredItems.clear();
    inTrade = false;
    trade_reqId = 0;
  }

  public boolean validClient(int index) {
    Client p = (Client) PlayerHandler.players[index];
    if (p != null && !p.disconnected && p.dbId > 0) {
      return true;
    }
    return false;
  }

  public Client getClient(int index) {
    return ((Client) PlayerHandler.players[index]);
  }

  public void tradeReq(int id) {
    // followPlayer(id);
    faceNPC(32768 + id);
    if (!Server.trading) {
      send(new SendMessage("Trading has been temporarily disabled"));
      return;
    }
    if (wildyLevel > 0 && wildyLevel <= 10) {
      send(new SendMessage("You can't trade in the first 10 levels of the wilderness"));
      return;
    }
    for (int a = 0; a < PlayerHandler.players.length; a++) {
      Client o = getClient(a);
      if (a != getSlot() && validClient(a) && o.dbId > 0 && o.dbId == dbId) {
        logout();
      }
    }
    Client other = (Client) PlayerHandler.players[id];
    if (validClient(trade_reqId)) {
      setFocus(other.getPosition().getX(), other.getPosition().getY());
      if (inTrade || inDuel || other.inTrade || other.inDuel) {
        send(new SendMessage("That player is busy at the moment"));
        trade_reqId = 0;
        return;
      }
      if (tradeLocked && other.playerRights < 1) {
        return;
      }
    }
    if (dbId == other.dbId) {
      return;
    }
    /*
     * if(other.connectedFrom.equals(connectedFrom) &&
     * !connectedFrom.equals("127.0.0.1")){ tradeRequested = false; return; }
     */
    if (validClient(trade_reqId) && !inTrade && other.tradeRequested && other.trade_reqId == getSlot()) {
      openTrade();
      other.openTrade();
    } else if (validClient(trade_reqId) && !inTrade && System.currentTimeMillis() - lastButton > 1000) {
      lastButton = System.currentTimeMillis();
      tradeRequested = true;
      trade_reqId = id;
      send(new SendMessage("Sending trade request..."));
      other.send(new SendMessage(getPlayerName() + ":tradereq:"));
    }
  }

  public void confirmScreen() {
    canOffer = false;
    send(new InventoryInterface(3443, 3213)); // trade confirm + normal bag
    inTrade = true;
    resetItems(3214);
    String SendTrade = "Absolutely nothing!";
    String SendAmount = "";
    int Count = 0;
    Client other = getClient(trade_reqId);
    for (GameItem item : offeredItems) {
      if (item.getId() > 0) {
        if (item.getAmount() >= 1000 && item.getAmount() < 1000000) {
          SendAmount = "@cya@" + (item.getAmount() / 1000) + "K @whi@(" + Utils.format(item.getAmount()) + ")";
        } else if (item.getAmount() >= 1000000) {
          SendAmount = "@gre@" + (item.getAmount() / 1000000) + " million @whi@(" + Utils.format(item.getAmount())
              + ")";
        } else {
          SendAmount = "" + Utils.format(item.getAmount());
        }
        if (Count == 0) {
          SendTrade = GetItemName(item.getId());
        } else {
          SendTrade = SendTrade + "\\n" + GetItemName(item.getId());
        }
        if (item.isStackable()) {
          SendTrade = SendTrade + " x " + SendAmount;
        }
        Count++;
      }
    }
    send(new SendString(SendTrade, 3557));
    SendTrade = "Absolutely nothing!";
    SendAmount = "";
    Count = 0;
    for (GameItem item : other.offeredItems) {
      if (item.getId() > 0) {
        if (item.getAmount() >= 1000 && item.getAmount() < 1000000) {
          SendAmount = "@cya@" + (item.getAmount() / 1000) + "K @whi@(" + Utils.format(item.getAmount()) + ")";
        } else if (item.getAmount() >= 1000000) {
          SendAmount = "@gre@" + (item.getAmount() / 1000000) + " million @whi@(" + Utils.format(item.getAmount())
              + ")";
        } else {
          SendAmount = "" + Utils.format(item.getAmount());
        }
        // SendAmount = SendAmount;
        if (Count == 0) {
          SendTrade = GetItemName(item.getId());
        } else {
          SendTrade = SendTrade + "\\n" + GetItemName(item.getId());
        }
        if (Server.itemManager.isStackable(item.getId())) {
          // if (item.stackable) {
          SendTrade = SendTrade + " x " + SendAmount;
        }
        Count++;
      }
    }
    send(new SendString(SendTrade, 3558));
  }

  private boolean tradeSuccessful = false;

  public void giveItems() {
    Client other = getClient(trade_reqId);
    tradeSuccessful = true;
    TradeDupeFix.remove(this, other);
    TradeDupeFix.remove(other, this);
    if (validClient(trade_reqId)) {
      try {
        CopyOnWriteArrayList<GameItem> offerCopy = new CopyOnWriteArrayList<GameItem>();
        CopyOnWriteArrayList<GameItem> otherOfferCopy = new CopyOnWriteArrayList<GameItem>();
        for (GameItem item : other.offeredItems) {
          otherOfferCopy.add(new GameItem(item.getId(), item.getAmount()));
        }
        for (GameItem item : offeredItems) {
          offerCopy.add(new GameItem(item.getId(), item.getAmount()));
        }
        for (GameItem item : other.offeredItems) {
          if (item.getId() > 0) {
            addItem(item.getId(), item.getAmount());
            println("TradeConfirmed, item=" + item.getId());
          }
        }

        if (this.dbId > other.dbId) {
          Server.login.logTrade(dbId, other.dbId, offerCopy, otherOfferCopy, true);
        }
        send(new RemoveInterfaces());
        tradeResetNeeded = true;
        System.out.println("trade succesful");
        saveStats(false);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public void resetTrade() {
    offeredItems.clear();
    inTrade = false;
    trade_reqId = 0;
    canOffer = true;
    tradeConfirmed = false;
    tradeConfirmed2 = false;
    send(new RemoveInterfaces());
    tradeResetNeeded = false;
    send(new SendString("Are you sure you want to make this trade?", 3535));
  }

  public void duelReq(int pid) {
    faceNPC(32768 + pid);
    if (wildyLevel > 0) {
      send(new SendMessage("You can't duel out here!"));
      return;
    }
    if (!Server.dueling) {
      send(new SendMessage("Dueling has been temporarily disabled"));
      return;
    }
    for (int a = 0; a < PlayerHandler.players.length; a++) {
      Client o = getClient(a);
      if (a != getSlot() && validClient(a) && o.dbId > 0 && o.dbId == dbId) {
        logout();
      }
    }
    duel_with = pid;
    duelRequested = true;
    if (!validClient(duel_with)) {
      return;
    }
    Client other = getClient(duel_with);
    setFocus(other.getPosition().getX(), other.getPosition().getY());
    if (inTrade || inDuel || other.inDuel || other.inTrade || other.duelFight || other.duelConfirmed
        || other.duelConfirmed2) {
      send(new SendMessage("Other player is busy at the moment"));
      duelRequested = false;
      return;
    }
    if (tradeLocked && other.playerRights < 1) {
      return;
    }
    if (other.connectedFrom.equals(connectedFrom)) {
      // duelRequested = false;
      // return;
    }
    if (duelRequested && other.duelRequested && duel_with == other.getSlot() && other.duel_with == getSlot()) {
      openDuel();
      other.openDuel();
    } else {
      send(new SendMessage("Sending duel request..."));
      other.send(new SendMessage(getPlayerName() + ":duelreq:"));
    }
  }

  public void openDuel() {
    RefreshDuelRules();
    refreshDuelScreen();
    inDuel = true;
    Client other = getClient(duel_with);
    send(new SendString("Dueling with: " + other.getPlayerName() + " (level-" + other.determineCombatLevel() + ")", 6671));
    send(new SendString("", 6684));
    send(new InventoryInterface(6575, 3321));
    resetItems(3322);
    sendArmour();
  }

  public void declineDuel() {
    Client other = getClient(duel_with);
    inDuel = false;
    if (validClient(duel_with) && other.inDuel) {
      other.declineDuel();
    }
    send(new RemoveInterfaces());
    canOffer = true;
    duel_with = 0;
    duelRequested = false;
    duelConfirmed = false;
    duelConfirmed2 = false;
    duelFight = false;
    for (GameItem item : offeredItems) {
      if (item.getAmount() < 1) {
        continue;
      }
      println("adding " + item.getId() + ", " + item.getAmount());
      if (Server.itemManager.isStackable(item.getId()) || Server.itemManager.isNote(item.getId())) {
        addItem(item.getId(), item.getAmount());
      } else {
        addItem(item.getId(), 1);
      }
    }
    offeredItems.clear();
    /*
     * Danno: Reset's duel options when duel declined to stop scammers.
     */
    resetDuel();
    RefreshDuelRules();
    failer = "";
  }

  public void confirmDuel() {
    Client other = getClient(duel_with);
    if (!validClient(duel_with)) {
      declineDuel();
    }
    String out = "";
    for (GameItem item : offeredItems) {
      if (Server.itemManager.isStackable(item.getId()) || Server.itemManager.isNote(item.getId())) {
        out += GetItemName(item.getId()) + " x " + Utils.format(item.getAmount()) + ", ";
      } else {
        out += GetItemName(item.getId()) + ", ";
      }
    }
    send(new SendString(out, 6516));
    out = "";
    for (GameItem item : other.offeredItems) {
      if (Server.itemManager.isStackable(item.getId()) || Server.itemManager.isNote(item.getId())) {
        out += GetItemName(item.getId()) + " x " + Utils.format(item.getAmount()) + ", ";
      } else {
        out += GetItemName(item.getId()) + ", ";
      }
    }
    send(new SendString(out, 6517));
    send(new SendString("Movement will be disabled", 8242));
    for (int i = 8243; i <= 8253; i++) {
      send(new SendString("", i));
    }
    send(new SendString("Hitpoints will be restored", 8250));
    send(new SendString("", 6571));
    showInterface(6412);
  }

  public void startDuel() {
    canAttack = false;
    // canAttack = true;
    canOffer = false;
    send(new RemoveInterfaces());
    duelFight = true;
    if (attackPot > 0.0 || defensePot > 0.0 || strengthPot > 0.0) {
      attackPot = 0.0;
      defensePot = 0.0;
      strengthPot = 0.0;
      rangePot = 0.0;
      updatePotions();
    }
    Client other = getClient(duel_with);
    for (GameItem item : other.offeredItems) {
      otherOfferedItems.add(new GameItem(item.getId(), item.getAmount()));
    }
    otherdbId = other.dbId;

    final Client player = this;
    EventManager.getInstance().registerEvent(new Event(1000) {
      long start = System.currentTimeMillis();

      public void execute() {
        long now = System.currentTimeMillis();
        if (now - start >= 4000) {
          player.requestForceChat("Fight!");
          player.canAttack = true;
          stop();
          return;
        } else if (now - start >= 3000) {
          player.requestForceChat("1");
        } else if (now - start >= 2000) {
          player.requestForceChat("2");
        } else if (now - start >= 1000) {
          player.requestForceChat("3");

        }
      }
    });
  }

  /*
   * Danno: Edited for new duel rules, for future use.
   */
  public void resetDuel() {
    send(new RemoveInterfaces());
    duelWin = false;
    canOffer = true;
    duel_with = 0;
    duelRequested = false;
    duelConfirmed = false;
    duelConfirmed2 = false;
    offeredItems.clear();
    otherOfferedItems.clear();
    duelFight = false;
    canAttack = true;
    inDuel = false;
    duelRule = new boolean[] { false, false, false, false, false, true, true, true, true, true, true };
    for (int i = 0; i < duelBodyRules.length; i++) {
      duelBodyRules[i] = false;
    }
    otherdbId = -1;
    if (!inWildy()) {
      getOutputStream().createFrameVarSize(104);
      getOutputStream().writeByteC(3);
      getOutputStream().writeByteA(0);
      getOutputStream().writeString("null");
      getOutputStream().endFrameVarSize();
    }
    if (duelFight) {
      getOutputStream().createFrameVarSize(104);
      getOutputStream().writeByteC(3);
      getOutputStream().writeByteA(1);
      getOutputStream().writeString("null");
      getOutputStream().endFrameVarSize();
    }
  }

  public void frame36(int Interface, int Status) {
    getOutputStream().createFrame(36);
    getOutputStream().writeWordBigEndian(Interface); // The button
    getOutputStream().writeByte(Status); // The Status of the button
  }

  public void frame87(int Interface, int Status) {
    getOutputStream().createFrame(87);
    getOutputStream().writeWordBigEndian(Interface); // The button
    getOutputStream().writeDWord_v1(Status); // The Status of the button
  }

  public boolean duelButton(int button) {
    Client other = getClient(duel_with);
    boolean found = false;
    if (System.currentTimeMillis() - lastButton < 800) {
      return false;
    }
    if (inDuel && !duelFight && !duelConfirmed2 && !other.duelConfirmed2 && !(duelConfirmed && other.duelConfirmed)) {
      // duelConfirmed = false;
      // other.duelConfirmed = false;
      for (int i = 0; i < duelButtons.length; i++) {
        if (button == duelButtons[i]) {
          found = true;
          if (duelRule[i]) {
            duelRule[i] = false;
            other.duelRule[i] = false;
          } else {
            duelRule[i] = true;
            other.duelRule[i] = true;
          }
        }
      }
      if (found) {
        lastButton = System.currentTimeMillis();
        duelConfirmed = false;
        other.duelConfirmed = false;
        send(new SendString("", 6684));
        other.send(new SendString("", 6684));

        other.duelRule[i] = duelRule[i];
        RefreshDuelRules();
        other.RefreshDuelRules();
      }
    }
    return found;
  }

  public boolean duelButton2(int button) {
    Client other = getClient(duel_with);
    /*
     * Danno: Null check :p
     */
    if (other == null)
      return false;
    boolean found = false;
    if (System.currentTimeMillis() - lastButton < 400) {
      return false;
    }
    if (inDuel && !duelFight && !duelConfirmed2 && !other.duelConfirmed2 && !(duelConfirmed && other.duelConfirmed)) {
      if (duelBodyRules[button]) {
        duelBodyRules[button] = false;
        other.duelBodyRules[button] = false;
      } else {
        duelBodyRules[button] = true;
        other.duelBodyRules[button] = true;
      }
      lastButton = System.currentTimeMillis();
      duelConfirmed = false;
      other.duelConfirmed = false;
      send(new SendString("", 6684));
      other.send(new SendString("", 6684));
      other.duelBodyRules[i] = duelBodyRules[i];
      RefreshDuelRules();
      other.RefreshDuelRules();
    }
    return found;
  }

  public void addFriend(long name) {
    // On = 0, Friends = 1, Off = 2
    for (Friend f : friends) {
      if (f.name == name) {
        send(new SendMessage(Utils.longToPlayerName(name) + " is already on your friends list"));
        return;
      }
    }
    friends.add(new Friend(name, true));
    for (Client c : PlayerHandler.playersOnline.values()) {
      if (c.hasFriend(longName)) {
        c.refreshFriends();
      }
    }
    refreshFriends();
  }

  public void sendPmMessage(long friend, byte[] pmchatText, int pmchatTextSize) {
    if (muted) {
      return;
    }
    boolean found = false;
    for (Friend f : friends) {
      if (f.name == friend) {
        found = true;
        break;
      }
    }
    if (!found) {
      send(new SendMessage("That player is not on your friends list"));
      return;
    }
    for (int i = 0; i < pmchatTextSize; i++) {
    }
    if (PlayerHandler.playersOnline.containsKey(friend)) {
      Client to = PlayerHandler.playersOnline.get(friend);
      if (to.busy && to.playerGroup == 6 && playerRights < 1) {
        send(new SendMessage("<col=FF0000>This player is busy and did not receive your message."));
        // send(new SendMessage("Please only report glitch/bugs to him/her on
        // the forums"));
        return;
      }
      if (to.Privatechat == 0 || (to.Privatechat == 1 && to.hasFriend(longName))/* || (to.busy && playerGroup == 6)*/) {
        to.sendpm(longName, playerRights, pmchatText, pmchatTextSize);
        PmLog.recordPm(this.getPlayerName(), to.getPlayerName(), Utils.textUnpack(pmchatText, pmchatTextSize));
      } else {
        send(new SendMessage("That player is not available"));
      }
    } else {
      send(new SendMessage("That player is not online"));
    }
    /*
     * for (int i1 = 0; i1 < handler.players.length; i1++) { client to =
     * getClient(i1); if (validClient(i1)) { if (validClient(i1) && to.dbId > 0
     * && misc.playerNameToInt64(to.playerName) == friend) { if (to.Privatechat
     * == 0 || (to.Privatechat == 1 && to.hasFriend(misc
     * .playerNameToInt64(playerName)))) { // server.login.sendChat(dbId,
     * to.dbId, 3, absX, absY, // ); if (to.officialClient || to.okClient) {
     * to.sendpm(misc.playerNameToInt64(playerName), playerRights, pmchatText,
     * pmchatTextSize); } else { to .send(new SendMessage(
     * "A player has tried to send you a private message"); to .send(new
     * SendMessage(
     * "For technical reasons, you must use the dodian client for friends list features"
     * ); } sent = true; break; } } } } if (!sent) { send(new SendMessage(
     * "Could not find player"); }
     */
  }

  public boolean hasFriend(long name) {
    for (Friend f : friends) {
      if (f.name == name) {
        return true;
      }
    }
    return false;
  }

  public void refreshFriends() {
    for (Friend f : friends) {
      if (PlayerHandler.playersOnline.containsKey(f.name)) {
        loadpm(f.name, Server.world);
      } else if (PlayerHandler.allOnline.containsKey(f.name)) {
        loadpm(f.name, PlayerHandler.allOnline.get(f.name).intValue());
      } else {
        loadpm(f.name, 0);
      }
    }
  }

  public void removeFriend(long name) {
    for (Friend f : friends) {
      if (f.name == name) {
        friends.remove(f);
        refreshFriends();
        return;
      }
    }
  }

  public void removeIgnore(long name) {
    for (Friend f : ignores) {
      if (f.name == name) {
        ignores.remove(f);
        refreshFriends();
        return;
      }
    }
  }

  public void addIgnore(long name) {
  }

  public void triggerChat(int button) {
    if (convoId == 0) {
      if (button == 1) {
        openUpBank();
      } else {
        nextDiag = 8;
      }
    }
    if (convoId == 1) {
      if (button == 1) {
        nextDiag = 13;
      } else {
        nextDiag = 14;
      }
    }
    if (convoId == 2) {
      if (button == 1) {
        WanneShop = 39;
      } else {
        send(new RemoveInterfaces());
      }
    }
    if (convoId == 3) {
      if (button == 1) {
        WanneShop = 9;
      } else {
        send(new RemoveInterfaces());
      }
    }
    if (convoId == 4) {
      if (button == 1) {
        WanneShop = 22;
      } else {
        send(new RemoveInterfaces());
      }
    }
    /*
     * if (convoId == 1001) { if (button == 1) { //send(new RemoveInterfaces());
     * getOutputStream().createFrame(27); } else { send(new RemoveInterfaces());
     * } }
     */
    if (convoId == 162) {
      if (button == 1) {
        spendTickets();
      } else {
        if (premium) {
          teleportToX = 2998;
          teleportToY = 3913;
        } else {
          send(new SendMessage("You must be premium to teleport there."));
        }
      }
      send(new RemoveInterfaces());
    }
    if (nextDiag > 0) {
      NpcDialogue = nextDiag;
      NpcDialogueSend = false;
      nextDiag = -1;
    }
  }

  public boolean smithCheck(int id) {
    for (int i = 0; i < Constants.smithing_frame.length; i++) {
      for (int i1 = 0; i1 < Constants.smithing_frame[i].length; i1++) {
        if (id == Constants.smithing_frame[i][i1][0]) {
          return true;
        }
      }
    }
    send(new SendMessage("Client hack detected!"));
    return false;
  }

  public int findPick() {
    int Eaxe = -1;
    int Iaxe = -1;
    int weapon = getEquipment()[Equipment.Slot.WEAPON.getId()];
    for (int i = 0; i < Utils.picks.length; i++) {
      if (Utils.picks[i] == weapon) {
        if (getLevel(Skill.MINING) >= Utils.pickReq[i])
          Eaxe = i;
      }
      for (int ii = 0; ii < playerItems.length; ii++) {
        if (Utils.picks[i] == playerItems[ii] - 1) {
          if (getLevel(Skill.MINING) >= Utils.pickReq[i]) {
            Iaxe = i;
          }
        }
      }
    }
    if (Eaxe >= Iaxe)
      return Eaxe;
    if (Iaxe >= Eaxe)
      return Iaxe;
    return -1;
  }

  public int findAxe() {
    int Eaxe = -1;
    int Iaxe = -1;
    int weapon = getEquipment()[Equipment.Slot.WEAPON.getId()];
    for (int i = 0; i < Utils.axes.length; i++) {
      if (Utils.axes[i] == weapon) {
        if (getLevel(Skill.WOODCUTTING) >= Utils.axeReq[i])
          Eaxe = i;
      }
      for (int ii = 0; ii < playerItems.length; ii++) {
        if (Utils.axes[i] == playerItems[ii] - 1) {
          if (getLevel(Skill.WOODCUTTING) >= Utils.axeReq[i]) {
            Iaxe = i;
          }
        }
      }
    }
    if (Eaxe >= Iaxe)
      return Eaxe;
    if (Iaxe >= Eaxe)
      return Iaxe;
    return -1;
  }

  public void mining(int index) {
    /*
     * if (getEquipment()[Equipment.Slot.WEAPON.getId()] !=
     * Utils.picks[minePick]) { send(new SendMessage(
     * "You must have a pickaxe wielded to mine")); resetAction(true); return; }
     */
    boolean hasPick = false;
    int pickaxe = -1;
    // for (int p = 0; p < misc.picks.length; p++) {
    // if (myEquipment.getId(3) == misc.picks[p]) {
    // minePick = p;
    // hasPick = true;
    // }
    // }
    pickaxe = findPick();
    if (pickaxe < 0) {
      minePick = -1;
      resetAction();
      send(new SendMessage("You do not have an pickaxe that you can use."));
      return;
    } else {
      minePick = pickaxe;
      hasPick = true;
    }
    if (hasPick) {
      requestAnim(getMiningEmote(Utils.picks[pickaxe]), 0);
    } else {
      resetAction();
      send(new SendMessage("You need a pickaxe to mine this rock"));
    }
    if (!playerHasItem(-1)) {
      send(new SendMessage("Your inventory is full!"));

      resetAction(true);
      return;
    }
    if (Utils.random(50) == 5) {
      send(new SendMessage("You take a rest"));
      resetAction(true);
      return;
    }
    if (index != 6) {
      send(new SendMessage("You mine some " + GetItemName(Utils.ore[index]).toLowerCase() + "."));
    }
    addItem(Utils.ore[index], 1);
    giveExperience(Utils.oreExp[index], Skill.MINING);
    requestAnim(getMiningEmote(Utils.picks[pickaxe]), 0);
  }

  public void CallGFXMask(int id, int height) {
    setGraphic(id, height == 0 ? 65536 : 6553600);
    getUpdateFlags().setRequired(UpdateFlag.GRAPHICS, true);
  }

  public void AddToCords(int X, int Y) {
    newWalkCmdSteps = Math.abs(X + Y);
    if (newWalkCmdSteps % 2 != 0) {
      newWalkCmdSteps /= 2;
    }
    if (++newWalkCmdSteps > 50) {
      newWalkCmdSteps = 0;
    }
    int l = getPosition().getX();
    l -= mapRegionX * 8;
    for (i = 1; i < newWalkCmdSteps; i++) {
      newWalkCmdX[i] = X;
      newWalkCmdY[i] = Y;
      tmpNWCX[i] = newWalkCmdX[i];
      tmpNWCY[i] = newWalkCmdY[i];
    }

    newWalkCmdX[0] = newWalkCmdY[0] = tmpNWCX[0] = tmpNWCY[0] = 0;
    int j1 = getPosition().getY();
    j1 -= mapRegionY * 8;
    newWalkCmdIsRunning = false;
    for (i = 0; i < newWalkCmdSteps; i++) {
      newWalkCmdX[i] += l;
      newWalkCmdY[i] += j1;
    }
  }

  public void GnomeLog() {
    if (getPosition().getX() == 2474 && getPosition().getY() == 3436) {
      AddToCords(0, -7);
      gnomeCourse[0] = true;
    }
  }

  public void GnomeNet1() {
    requestAnim(828, 0);
    EventManager.getInstance().registerEvent(new Event(600) {

      @Override
      public void execute() {
        teleportTo(2473, 3424, 1);
        this.stop();
      }

    });
    gnomeCourse[1] = true;
  }

  public void GnomeTree1() {
    requestAnim(828, 0);
    EventManager.getInstance().registerEvent(new Event(600) {

      @Override
      public void execute() {
        teleportTo(2474, 3421, 2);
        this.stop();
      }

    });
    gnomeCourse[2] = true;
  }

  public void GnomeRope() {
    if (getPosition().getX() != 2477 || getPosition().getY() != 3420) {
      return;
    }
    AddToCords(6, 0);
    gnomeCourse[3] = true;
  }

  public void GnomeTreebranch2() {
    requestAnim(828, 0);
    EventManager.getInstance().registerEvent(new Event(600) {

      @Override
      public void execute() {
        teleportTo(2485, 3421, 0);
        this.stop();
      }

    });
    gnomeCourse[4] = true;
  }

  public void GnomeNet2() {
    requestAnim(828, 0);
    EventManager.getInstance().registerEvent(new Event(600) {

      @Override
      public void execute() {
        teleportTo(getPosition().getX(), getPosition().getY() + 2, 0);
        this.stop();
      }

    });
    gnomeCourse[5] = true;
  }

  public void GnomePipe1() {
    AddToCords(0, 7);
    gnomeCourse[6] = true;
    boolean done = true;
    for (int i = 0; i < gnomeCourse.length; i++) {
      if (!gnomeCourse[i]) {
        done = false;
        break;
      }
    }
    if (done) {
      send(new SendMessage("You gain experience!"));
      giveExperience(2000, Skill.AGILITY);
      addItem(2996, 1);
      for (int i = 0; i < gnomeCourse.length; i++) {
        gnomeCourse[i] = false;
      }
    }
  }

  public void startAttackNpc(int npcIndex) {
    Npc npc = Server.npcManager.getNpc(npcIndex);
    if (npc != null) {
      if (npc.isAttackable()) {
        selectedNpc = npc;
        attackingNpc = true;
        faceNPC(npcIndex);
      } else {
        send(new SendMessage("You can't attack that!"));
      }
    }
  }

  public void resetAttackNpc() {
    rerequestAnim();
    attackingNpc = false;
    selectedNpc = null;
    setFaceNpc(65535);
    getUpdateFlags().setRequired(UpdateFlag.FACE_CHARACTER, true);

  }

  private void requestAnims(int wearID) {
    setStandAnim(Server.itemManager.getStandAnim(getEquipment()[Equipment.Slot.WEAPON.getId()]));
    setWalkAnim(Server.itemManager.getWalkAnim(getEquipment()[Equipment.Slot.WEAPON.getId()]));
    setRunAnim(Server.itemManager.getRunAnim(getEquipment()[Equipment.Slot.WEAPON.getId()]));
  }

  public int getWildLevel() {
    // if(dbId > 0) return 0;
    // 2954-3327
    if (getPosition().getY() >= 3520 && getPosition().getY() < 3904 && getPosition().getX() >= 2954
        && getPosition().getX() <= 3327) {
      int lvl = ((int) ((getPosition().getY() - 3520) / 8)) + 1;
      return lvl;
    } else
      return 0;
  }

  public void setWildLevel(int level) {
    wildyLevel = level;
    getOutputStream().createFrame(208);
    getOutputStream().writeWordBigEndian_dup(197);
    send(new SendString("Level: " + wildyLevel, 199));
  }

  public void updatePotions() {
    int attack = (int) ((1 + (attackPot / 100)) * getLevel(Skill.ATTACK));
    int defense = (int) ((1 + (defensePot / 100)) * getLevel(Skill.DEFENCE));
    int strength = (int) ((1 + (strengthPot / 100)) * getLevel(Skill.STRENGTH));
    int range = (int) ((1 + (rangePot / 100)) * getLevel(Skill.RANGED));
    send(new SendString(String.valueOf(attack), 4004));
    send(new SendString(String.valueOf(strength), 4006));
    send(new SendString(String.valueOf(defense), 4008));
    send(new SendString(String.valueOf(range), 4010));
    potionUpdate = System.currentTimeMillis();
    if (attackPot > 0.0) {
      attackPot -= 1;
    }
    if (defensePot > 0.0) {
      defensePot -= 1;
    }
    if (strengthPot > 0.0) {
      strengthPot -= 1;
    }
    if (rangePot > 0.0) {
      rangePot -= 1;
    }
    if (attackPot < 0.0) {
      attackPot = 0.0;
    }
    if (defensePot < 0.0) {
      defensePot = 0.0;
    }
    if (strengthPot < 0.0) {
      strengthPot = 0.0;
    }
    if (rangePot < 0.0) {
      rangePot = 0.0;
    }
    CalculateMaxHit();
  }

  public void updatePlayerDisplay() {
    send(new SendString("Uber Server 3.0 (" + PlayerHandler.getPlayerCount() + " online)", 6570));
    setInterfaceWalkable(6673);
  }

  public void playerKilled(Client other) {
    setHeadIcon(2);
  }

  public void setSnared(int time) {
    snaredUntil = System.currentTimeMillis() + time;
    stillgfx(617, getPosition().getX(), getPosition().getY());
    send(new SendMessage("You have been snared!"));
    resetWalkingQueue();
  }

  public void died() {
    int maxDmg = 0, maxId = 0;
    for (int id : getDamage().keySet()) {
      if (getDamage().get(id) > maxDmg) {
        maxDmg = getDamage().get(id);
        maxId = id;
      }
    }
    for (int i = 0; i < getEquipment().length; i++) {
      if (getEquipment()[i] > 0) {
        if (Server.itemManager.isTradable(getEquipment()[i]))
          Ground.items.add(new GroundItem(getPosition().getX(), getPosition().getY(), getEquipment()[i],
              getEquipmentN()[i], maxId, false));
        else
          Ground.items.add(new GroundItem(getPosition().getX(), getPosition().getY(), getEquipment()[i],
              getEquipmentN()[i], getSlot(), false));
      }
      getEquipment()[i] = -1;
      getEquipmentN()[i] = 0;
      deleteequiment(0, i);
    }
    for (int i = 0; i < playerItems.length; i++) {
      if (playerItems[i] > 0) {
        if (Server.itemManager.isTradable((playerItems[i] - 1)))
          Ground.items.add(new GroundItem(getPosition().getX(), getPosition().getY(), (playerItems[i] - 1),
              playerItemsN[i], maxId, false));
        else
          Ground.items.add(new GroundItem(getPosition().getX(), getPosition().getY(), (playerItems[i] - 1),
              playerItemsN[i], getSlot(), false));
      }
      deleteItem((playerItems[i] - 1), i, playerItemsN[i]);
    }
    Ground.items.add(new GroundItem(getPosition().getX(), getPosition().getY(), 526, 1, maxId, false));
    getDamage().clear();
    if (validClient(maxId)) {
      getClient(maxId).send(new SendMessage("You have defeated " + getPlayerName() + "!"));
      Client other = getClient(maxId);
      other.playerKilled(this);

    }
  }

  public void acceptDuelWon() {
    if (duelFight && duelWin) {
      duelWin = false;
      if (System.currentTimeMillis() - lastButton < 1000) {
        lastButton = System.currentTimeMillis();
        return;
      } else {
        lastButton = System.currentTimeMillis();
      }
      Client other = getClient(duel_with);
      CopyOnWriteArrayList<GameItem> offerCopy = new CopyOnWriteArrayList<GameItem>();
      CopyOnWriteArrayList<GameItem> otherOfferCopy = new CopyOnWriteArrayList<GameItem>();
      for (GameItem item : otherOfferedItems) {
        otherOfferCopy.add(new GameItem(item.getId(), item.getAmount()));
      }
      for (GameItem item : offeredItems) {
        offerCopy.add(new GameItem(item.getId(), item.getAmount()));
      }
      Server.login.logTrade(dbId, otherdbId, offerCopy, otherOfferCopy, false);

      for (GameItem item : otherOfferedItems) {
        if (item.getId() > 0 && item.getAmount() > 0) {
          if (Server.itemManager.isStackable(item.getId())) {
            addItem(item.getId(), item.getAmount());
          } else {
            addItem(item.getId(), 1);
          }
        }
      }
      for (GameItem item : offeredItems) {
        if (item.getId() > 0 && item.getAmount() > 0) {
          addItem(item.getId(), item.getAmount());
        }
      }
      resetDuel();
      saveStats(false);
      if (validClient(duel_with)) {
        other.resetDuel();
        other.saveStats(false);
      }
    }
  }

  public void lagOut() {
    if (inTrade && !tradeSuccessful) {
      System.out.println("Declining in lagout()");
      declineTrade();
    }
    if (inDuel && !duelFight) {
      declineDuel();
    }
  }

  public boolean contains(int item) {
    for (int i = 0; i < playerItems.length; i++) {
      if (playerItems[i] == item + 1)
        return true;
    }
    return false;
  }

  public void setConfigIds() {
    stakeConfigId[0] = 16384; // No head armour
    stakeConfigId[1] = 32768; // No capes
    stakeConfigId[2] = 65536; // No amulets
    stakeConfigId[3] = 134217728; // No arrows
    stakeConfigId[4] = 131072; // No weapon
    stakeConfigId[5] = 262144; // No body armour
    stakeConfigId[6] = 524288; // No shield
    stakeConfigId[7] = 2097152; // No leg armour
    stakeConfigId[8] = 67108864; // No hand armour
    stakeConfigId[9] = 16777216; // No feet armour
    stakeConfigId[10] = 8388608; // No rings
    stakeConfigId[11] = 16; // No ranging
    stakeConfigId[12] = 32; // No melee
    stakeConfigId[13] = 64; // No magic
    stakeConfigId[14] = 8192; // no gear change
    stakeConfigId[15] = 4096; // fun weapons
    stakeConfigId[16] = 1; // no retreat
    stakeConfigId[17] = 128; // No drinks
    stakeConfigId[18] = 256; // No food
    stakeConfigId[19] = 512; // No prayer
    stakeConfigId[20] = 2; // movement
    stakeConfigId[21] = 1024; // obstacles
    stakeConfigId[22] = -1; // No specials
  }

  /**
   * Shows armour in the duel screen slots! (hopefully lol)
   */
  public void sendArmour() {
    for (int e = 0; e < getEquipment().length; e++) {
      // if(getEquipmentN()[e] < 1)
      // continue;
      getOutputStream().createFrameVarSizeWord(34);
      getOutputStream().writeWord(13824);
      getOutputStream().writeByte(e);
      getOutputStream().writeWord(getEquipment()[e] + 1);
      if (getEquipmentN()[e] > 254) {
        getOutputStream().writeByte(255);
        getOutputStream().writeDWord(getEquipmentN()[e]);
      } else {
        getOutputStream().writeByte(getEquipmentN()[e]); // amount
      }
      getOutputStream().endFrameVarSizeWord();
    }
  }

  public boolean hasTradeSpace() {
    if (!validClient(trade_reqId)) {
      return false;
    }
    Client o = getClient(trade_reqId);
    int spaces = 0;
    ArrayList<GameItem> items = new ArrayList<GameItem>();
    for (GameItem item : o.offeredItems) {
      if (item == null)
        continue;
      if (item.getAmount() > 0) {
        if (!items.contains(item)) {
          items.add(item);
          spaces += 1;
        } else {
          if (!item.isStackable()) {
            spaces += 1;
          }
        }
      }
    }
    if (spaces > getFreeSpace()) {
      failer = getPlayerName() + " does not have enough space to hold items being traded.";
      o.failer = getPlayerName() + " does not have enough space to hold items being traded.";
      return false;
    }
    return true;
  }

  /**
   * @return if player has enough space to remove items.
   */
  public boolean hasEnoughSpace() {
    if (!inDuel || !validClient(duel_with)) {
      return false;
    }
    Client o = getClient(duel_with);
    int spaces = 0;
    for (int i = 0; i < duelBodyRules.length; i++) {
      if (!duelBodyRules[i])
        continue;
      if (getEquipmentN()[trueSlots[i]] > 0) {
        spaces += 1;
      }
    }
    ArrayList<GameItem> items = new ArrayList<GameItem>();
    for (GameItem item : offeredItems) {
      if (item == null)
        continue;
      if (item.getAmount() > 0) {
        if (!items.contains(item)) {
          items.add(item);
          spaces += 1;
        } else {
          if (!item.isStackable()) {
            spaces += 1;
          }
        }
      }
    }
    for (GameItem item : o.offeredItems) {
      if (item == null)
        continue;
      if (item.getAmount() > 0) {
        if (!items.contains(item)) {
          items.add(item);
          spaces += 1;
        } else {
          if (!Server.itemManager.isStackable(item.getId())) {
            spaces += 1;
          }
        }
      }
    }
    if (spaces > getFreeSpace()) {
      failer = getPlayerName() + " does not have enough space to hold items being removed and/or staked.";
      o.failer = getPlayerName() + " does not have enough space to hold items being removed and/or staked.";
      return false;
    }
    return true;

  }

  public void removeEquipment() {
    for (int i = 0; i < duelBodyRules.length; i++) {
      if (!duelBodyRules[i])
        continue;
      if (getEquipmentN()[trueSlots[i]] > 0) {
        remove(getEquipment()[trueSlots[i]], trueSlots[i], true);
      }
    }
  }

  public void requestForceChat(String s) {
    forcedChat = s;
    getUpdateFlags().setRequired(UpdateFlag.FORCED_CHAT, true);
  }

  /**
   * @return the skillX
   */
  public int getSkillX() {
    return skillX;
  }

  /**
   * @param skillX
   *          the skillX to set
   */
  public void setSkillX(int skillX) {
    this.skillX = skillX;
  }

  /**
   * @return the skillY
   */
  public int getSkillY() {
    return skillY;
  }

  /**
   * @param skillY
   *          the skillY to set
   */
  public void setSkillY(int skillY) {
    this.skillY = skillY;
    if (WanneBank > 0)
      WanneBank = 0;
    if (NpcWanneTalk > 0)
      NpcWanneTalk = 0;
  }

  public OutputStream getConnection() {
    return out;
  }

  public void spendTickets() {
    int[] ids = { 249, 253, 257 };
    double[] proportion = { .80, .50, .25 };
    int slot = -1;
    for (int s = 0; s < playerItems.length; s++) {
      if ((playerItems[s] - 1) == 2996) {
        slot = s;
        break;
      }
    }
    if (slot == -1) {
      send(new SendMessage("You have no agility tickets!"));
    } else if (playerItemsN[slot] < 10) {
      send(new SendMessage("You must hand in at least 10 tickets at once"));
    } else {
      int amount = playerItemsN[slot];
      giveExperience(amount * 700, Skill.AGILITY);
      send(new SendMessage("You exchange your " + amount + " agility tickets"));
      int part = (int) (amount / 4);
      for (int a = 0; a < ids.length; a++) {
        addItem((ids[a] + 1), (int) (part * proportion[a]));
      }
      deleteItem(2996, playerItemsN[slot]);
    }
  }

  public boolean hasVoted() {
    long timeLeft = ((lastVoted + (43200000 / 2)) - System.currentTimeMillis());
    long hours = TimeUnit.MILLISECONDS.toHours(timeLeft);
    long min = TimeUnit.MILLISECONDS.toMinutes(timeLeft)
        - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeLeft));
    long sec = TimeUnit.MILLISECONDS.toSeconds(timeLeft)
        - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeLeft));
    String time = "" + hours + "h " + min + "m " + sec + "s";
    if (timeLeft > 0) {
      send(new SendMessage("You must wait " + time + " before you may claim another reward."));
      return true; // return true;
    }
    return false;
  }

  public void setLastVote(long time) {
    this.lastVoted = time;
  }

  public long getLastVote() {
    return lastVoted;
  }

  public long potTime = 0;
  public boolean mixPots = false;
  private int mixPotAmt = 0, mixPotId1 = 0, mixPotId2 = 0, mixPotId3 = 0, mixPotXp = 0;

  public void setPots(long time, int id1, int id2, int id3, int xp) {
    mixPotAmt = 14;
    potTime = time;
    mixPotId1 = id1;
    mixPotId2 = id2;
    mixPotId3 = id3;
    mixPotXp = xp;
    mixPots = true;
  }

  public void mixPots() {
    if (mixPotAmt < 1) {
      resetAction(true);
      return;
    }
    if (!playerHasItem(mixPotId1, 1) || !playerHasItem(mixPotId2, 1)) {
      resetAction(true);
      return;
    }
    mixPotAmt--;
    IsBanking = false;
    this.requestAnim(363, 0);
    getUpdateFlags().setRequired(UpdateFlag.APPEARANCE, true);
    deleteItem(mixPotId1, 1);
    deleteItem(mixPotId2, 1);
    addItem(mixPotId3, 1);
    giveExperience(mixPotXp, Skill.HERBLORE);
  }

  public int getXPForLevel(int level) {
    int points = 0;
    int output = 0;

    for (int lvl = 1; lvl <= level; lvl++) {
      points += Math.floor((double) lvl + 300.0 * Math.pow(2.0, (double) lvl / 7.0));
      if (lvl >= level) {
        return output;
      }
      output = (int) Math.floor(points / 4);
    }
    return 0;
  }

  public void guideBook() {
    send(new SendMessage("this is me a guide book!"));
    clearQuestInterface();
    showInterface(8134);
    send(new SendString("Newcomer's Guide", 8144));
    send(new SendString("---------------------------", 8145));
    send(new SendString("Welcome to Dodian.net!", 8147));
    send(new SendString("This guide is to help new players to get a general", 8148));
    send(new SendString("understanding of how Dodian works!", 8149));
    send(new SendString("", 8150));
    send(new SendString("For specific boss or skill locations", 8151));
    send(new SendString("navigate to the 'Guides' section of the forums.", 8152));
    send(new SendString("", 8153));
    send(new SendString("Here in Yanille, there are various enemies to kill,", 8154));
    send(new SendString("with armor rewards that get better the higher their level.", 8155));
    send(new SendString("", 8156));
    send(new SendString("From Yanille, you can also head North-East to access", 8157));
    send(new SendString("the mining area or South-West", 8158));
    send(new SendString("up the stairs in the magic guild to access the essence mine.", 8159));
    send(new SendString("", 8160));
    send(new SendString("If you navigate over to your spellbook, you will see", 8161));
    send(new SendString("some teleports, these all lead to key points on the server", 8162));
    send(new SendString("", 8163));
    send(new SendString("Seers, Catherby, Fishing Guild, and Gnome Stronghold", 8164));
    send(new SendString("teleports will all bring you to skilling locations.", 8165));
    send(new SendString("", 8166));
    send(new SendString("Dragon Cave, Legends Guild, and Taverly teleports", 8167));
    send(new SendString("will all bring you to locations with more monsters to train on.", 8168));
    send(new SendString("", 8169));
    send(new SendString("Teleporting to Taverly and heading up the path", 8170));
    send(new SendString("is how you access the Slayer Master!", 8171));
    send(new SendString("", 8172));
    send(new SendString("If you have more questions please visit the 'Guides'", 8173));
    send(new SendString("section of the forums, and if you still can't find the answer.", 8174));
    send(new SendString("Feel free to just ask a moderator!", 8175));
    send(new SendString("---------------------------", 8176));
  }

  private long lastVoted;

  public Prayers getPrayerManager() {
	    return prayers;
	  }

  public boolean checkKBD() {
    if (getPosition().getX() > 2704 && getPosition().getX() < 2733 && getPosition().getY() > 9790
        && getPosition().getY() < 9832)
      return true;
    return false;
  }

  private int getItem(Player p, int i, int i2) {
    if (!playerHasItem(items[i2]) && items[i2] != -1) {
      return blanks[i][i2];
    }
    return jewelry[i][i2];
  }

  public int[][] blanks = { { -1, 1649, 1650, 1651, 1652, 1653, 6575 }, { -1, 1668, 1669, 1670, 1671, 1672, 6577 },
      { -1, 1687, 1688, 1689, 1690, 1691, 6579 }, };

  public int[] startSlots = { 4233, 4239, 4245, 79 };
  public int[] items = { -1, 1607, 1605, 1603, 1601, 1615, 6573 };
  public int[] black = { 1647, 1666, 1685, 11067 };
  public int[] sizes = { 120, 100, 75, 11067 };

  public int[] moulds = { 1592, 1597, 1595, 11065 };

  public int findStrungAmulet(int amulet) {
    for (int i = 0; i < strungAmulets.length; i++) {
      if (jewelry[2][i] == amulet) {
        return strungAmulets[i];
      }
    }
    return -1;
  }

  public int[] strungAmulets = { 1692, 1694, 1696, 1698, 1700, 1702 };

  private int[][] jewelry = { { 1635, 1637, 1639, 1641, 1643, 1645, -1 }, { 1654, 1656, 1658, 1660, 1662, 1664, -1 },
      { 1673, 1675, 1677, 1679, 1681, 1683, -1 }, { 11069, 11072, 11076, 11085, 11092, 11115, 11130 } };

  private int[][] jewelry_levels = { { 5, 20, 27, 34, 43, 55, 67 }, { 6, 22, 29, 40, 56, 72, 82 },
      { 8, 23, 31, 50, 70, 80, 90 }, { 7, 24, 30, 42, 58, 74, 84 } };

  private int[][] jewelry_xp = { { 15, 40, 55, 70, 85, 100, 115 }, { 20, 55, 60, 75, 90, 105, 120 },
      { 30, 65, 70, 85, 100, 150, 165 }, { 25, 60, 65, 80, 95, 110, 125 } };

  public void showItemsGold() {
    int slot = 0;
    for (int i = 0; i < 3; i++) {
      slot = startSlots[i];
      if (!playerHasItem(moulds[i])) {
        changeInterfaceStatus(startSlots[i] - 5, true);
        changeInterfaceStatus(startSlots[i] - 1, false);
        continue;
      } else {
        changeInterfaceStatus(startSlots[i] - 5, false);
        changeInterfaceStatus(startSlots[i] - 1, true);
      }
      int itemsToShow[] = new int[6];
      for (int i2 = 0; i2 < 6; i2++) {
        itemsToShow[i2] = getItem(this, i, i2);
        if (i2 != 0 && itemsToShow[i2] != jewelry[i][i2])
          if (i2 < 6)
            sendFrame246(slot + 13 + i2 - 1 - i, sizes[i], black[i]);
          else
            sendFrame246(slot + 1788 - (i * 5), sizes[i], black[i]);
        else if (i2 != 0) {
          if (i2 < 6)
            sendFrame246(slot + 13 + i2 - 1 - i, sizes[i], -1);
          else
            sendFrame246(slot + 1788 - (i * 5), sizes[i], -1);
        }
      }
      setGoldItems(slot, itemsToShow);
    }
  }

  public void setGoldItems(int slot, int items[]) {
    outputStream.createFrameVarSizeWord(53);
    outputStream.writeWord(slot);
    outputStream.writeWord(items.length);

    for (int i = 0; i < items.length; i++) {
      outputStream.writeByte((byte) 1);
      outputStream.writeWordBigEndianA(items[i] + 1);
    }
    outputStream.endFrameVarSizeWord();
  }

  public int goldIndex = -1, goldSlot = -1;
  public int goldCraftingCount = 0;
  public boolean goldCrafting = false;

  public void goldCraft() {
    // int gem = gemReq[goldSlot];
    int level = jewelry_levels[goldIndex][goldSlot];
    int amount = goldCraftingCount;
    int item = jewelry[goldIndex][goldSlot];
    int xp = jewelry_xp[goldIndex][goldSlot];
    if (goldIndex == -1 || goldSlot == -1) {
      goldCrafting = false;
      resetAction();
      return;
    }
    if (amount <= 0) {
      goldCrafting = false;
      resetAction();
      return;
    }
    if (level > getLevel(Skill.CRAFTING)) {
      send(new SendMessage("You need a crafting level of " + level + " to make this."));
      goldCrafting = false;
      return;
    }
    if (!playerHasItem(2357)) {
      goldCrafting = false;
      send(new SendMessage("You need at least one gold bar."));
      return;
    }
    if (goldSlot != 0 && !playerHasItem(items[goldSlot])) {
      goldCrafting = false;
      send(new SendMessage("You need a " + GetItemName(items[goldSlot]).toLowerCase() + " to make this."));
      return;
    }
    goldCraftingCount--;
    if (goldCraftingCount <= 0) {
      goldCrafting = false;
    }
    requestAnim(0x383, 0);
    deleteItem(2357, 1);
    if (goldSlot != 0)
      deleteItem(items[goldSlot], 1);
    send(new SendMessage("You craft a " + GetItemName(item).toLowerCase() + "."));
    addItem(item, 1);
    giveExperience(xp * 10, Skill.CRAFTING);
  }

  public void startGoldCrafting(int interfaceID, int slot, int amount) {
    int index = 0;
    int[] inters = { 4233, 4239, 4245 };
    for (int i = 0; i < 3; i++)
      if (inters[i] == interfaceID)
        index = i;
    int level = jewelry_levels[index][slot];
    if (level > getLevel(Skill.CRAFTING)) {
      send(new SendMessage("You need a crafting level of " + level + " to make this."));
      return;
    }
    if (!playerHasItem(2357)) {
      send(new SendMessage("You need at least one gold bar."));
      return;
    }
    if (slot != 0 && !playerHasItem(items[slot])) {
      send(new SendMessage("You need a " + GetItemName(items[slot]).toLowerCase() + " to make this."));
      return;
    }
    goldCraftingCount = amount;
    goldIndex = index;
    goldSlot = slot;
    goldCrafting = true;
    send(new RemoveInterfaces());
  }

  public void deleteRunes(int[] runes, int[] qty) {
    for (int i = 0; i < runes.length; i++) {
      deleteItem(runes[i], qty[i]);
    }
  }

  public boolean hasRunes(int[] runes, int[] qty) {
    for (int i = 0; i < runes.length; i++) {
      if (!playerHasItem(runes[i], qty[i])) {
        return false;
      }
    }
    return true;
  }
  
  public void checkBow() {
      for (int i = 0; i < Constants.shortbow.length; i++) {
          if (getEquipment()[Equipment.Slot.WEAPON.getId()] == Constants.shortbow[i]
              || getEquipment()[Equipment.Slot.WEAPON.getId()] == Constants.longbow[i]) {
            UseBow = true;
            return;
          }
        }
        if (getEquipment()[Equipment.Slot.WEAPON.getId()] == 4212) {
          UseBow = true;
          return;
        }
        UseBow = false;
  }
  
	public void RottenTomato(final Client c) {
	    for (int i = 0; i < PlayerHandler.players.length; i++) {
	    	Client o = (Client) PlayerHandler.players[i];
		final int oX = c.getPosition().getX();
		final int oY = c.getPosition().getY();
		final int pX = o.getPosition().getX();
		final int pY = o.getPosition().getY();
		final int offX = (oY - pY) * -1;
		final int offY = (oX - pX) * -1;
		createProjectile(oX, oY, offX, offY, 50, 90, 1281, 21, 21, 2518 - 1);
		sendAnimation(7530);
		//c.turnPlayerTo(pX, pY);
		EventManager.getInstance().registerEvent(new Event(600) {

	        @Override
	        public void execute() {
	        	//if (c == null || c.disconnected) {
	        	        o.gfx0(1282);
	          this.stop();
	       // }
	        }
	      });
		if (playerHasItem(2518, 1)) {
			deleteItem(2518, 1);
		} else {
			deleteItem(2518, Equipment.Slot.WEAPON.getId());
			deleteItem(2518, 1);
		}
		}
	    }

	public void makesplat() {
		sendAnimation(7528);
		gfx0(1284);
		addItem(2518, 2);
	}

  
  @Override
  public boolean equals(Object o) {
	  return ((Client)o).getPlayerName().equalsIgnoreCase(this.getPlayerName());
  }
  
  private IoSession session;
  public static int isSibling;
  
  public IoSession getSession() {
		return session;
	}
  

}