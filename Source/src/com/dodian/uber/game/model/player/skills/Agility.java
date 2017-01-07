package com.dodian.uber.game.model.player.skills;

import com.dodian.uber.game.event.Event;
import com.dodian.uber.game.event.EventManager;
import com.dodian.uber.game.model.UpdateFlag;
import com.dodian.uber.game.model.entity.player.Client;
import com.dodian.uber.game.model.player.packets.outgoing.SendMessage;

public class Agility {

  public static void WildyPipe(final Client c) {
    if (c.getPosition().getX() == 3004 && c.getPosition().getY() == 3937) {
      c.wildyStage = 1;
      c.UsingAgility2 = true;
      c.AddToCords(0, 13);
      final int oldEmote = c.getWalkAnim();
      c.setWalkAnim(746);
      c.getUpdateFlags().setRequired(UpdateFlag.APPEARANCE, true);
      EventManager.getInstance().registerEvent(new Event(1000) {
        int part = 0;

        public void execute() {
          if (c == null || c.disconnected) {
            stop();
            return;
          }
          if (part == 1) {
            c.setWalkAnim(oldEmote);
            c.getUpdateFlags().setRequired(UpdateFlag.APPEARANCE, true);
            c.requestAnim(748, 0);
            c.UsingAgility2 = false;
            c.giveExperience(150, Skill.AGILITY);
            stop();
          } else if (part == 0) {
            c.setWalkAnim(747);
            c.getUpdateFlags().setRequired(UpdateFlag.APPEARANCE, true);
            part++;
            this.setTick(5500);
          }

        }

      });
    }
  }

  public static void WildyRope(final Client c) {
    boolean bool = false;
    int[] x = { 3004, 3005, 3006 };
    for (int i = 0; i < x.length; i++) {
      if (c.getPosition().getX() == x[i] && c.getPosition().getY() == 3953) {
        bool = true;
      }
    }
    if (bool) {
      final int oldEmote = c.getWalkAnim();
      boolean fail = false;
      int time = 3500;
      // c.send(new SendMessage("You don't need to use a rope with your pro
      // agility skills!"));
      c.UsingAgility2 = true;
      double chance = 100 - c.getLevel(Skill.AGILITY);
      double roll = Math.random() * 100;
      if (roll < chance)
        fail = true;
      if (!fail)
        c.AddToCords(0, 5);
      else {
        c.AddToCords(0, 2);
        time = 2000;
      }
      final boolean failed = fail;
      c.setWalkAnim(751);
      c.sendFrame160(3005, 3952, 10, 2, 497);
      c.getUpdateFlags().setRequired(UpdateFlag.APPEARANCE, true);
      EventManager.getInstance().registerEvent(new Event(time) {
        public void execute() {
          if (c == null || c.disconnected) {
            stop();
            return;
          }
          c.setWalkAnim(oldEmote);
          c.UsingAgility2 = false;
          c.getUpdateFlags().setRequired(UpdateFlag.APPEARANCE, true);
          if (c.wildyStage == 1 && !failed) {
            c.wildyStage = 2;
            c.giveExperience(150, Skill.AGILITY);
          }
          if (failed) {
            c.send(new SendMessage("You accidently lose concentration and fall!"));
            c.dealDamage((int) Math.ceil(c.getCurrentHealth() * 0.1), false);
            // c.teleportToX = 3003;
            // c.teleportToY = 10355;
            c.teleportToX = 3005;
            c.teleportToY = 3953;
          }
          stop();
        }

      });
    }
  }

  public static void WildyStones(final Client c) {
    boolean bool = false;
    if (c.getPosition().getX() == 3002 && c.getPosition().getY() == 3960) {
      bool = true;
    }
    if (bool) {
      final int oldEmote = c.getWalkAnim();
      c.UsingAgility2 = true;
      c.setWalkAnim(741);
      c.getUpdateFlags().setRequired(UpdateFlag.APPEARANCE, true);
      c.AddToCords(-6, 0);
      EventManager.getInstance().registerEvent(new Event(4000) {
        public void execute() {
          if (c == null || c.disconnected) {
            stop();
            return;
          }
          c.setWalkAnim(oldEmote);
          c.UsingAgility2 = false;
          c.getUpdateFlags().setRequired(UpdateFlag.APPEARANCE, true);
          if (c.wildyStage == 2)
            c.wildyStage = 3;
          c.giveExperience(150, Skill.AGILITY);
          stop();
        }
      });
    }
  }

  public static void WildyLog(final Client c) {
    if (c.getPosition().getX() == 3002 && c.getPosition().getY() == 3945) {
      final int oldEmote = c.getWalkAnim();
      c.UsingAgility2 = true;
      c.setWalkAnim(762);
      c.getUpdateFlags().setRequired(UpdateFlag.APPEARANCE, true);
      int time = 5000;
      boolean fail = false;
      double chance = 100 - c.getLevel(Skill.AGILITY);
      double roll = Math.random() * 100;
      if (roll < chance)
        fail = true;
      if (!fail)
        c.AddToCords(-8, 0);
      else {
        c.AddToCords(-4, 0);
        time = 3000;
      }
      final boolean failed = fail;
      EventManager.getInstance().registerEvent(new Event(time) {
        public void execute() {
          if (c == null || c.disconnected) {
            stop();
            return;
          }
          c.setWalkAnim(oldEmote);
          c.UsingAgility2 = false;
          c.getUpdateFlags().setRequired(UpdateFlag.APPEARANCE, true);
          if (c.wildyStage == 3 && !failed) {
            c.wildyStage = 4;
            c.giveExperience(150, Skill.AGILITY);
          }
          if (failed) {
            c.send(new SendMessage("You fall off the log!"));
            c.dealDamage((int) Math.ceil(c.getCurrentHealth() * 0.1), false);
            // c.teleportToX = 2999;
            // c.teleportToY = 10345;
            c.teleportToX = 3002;
            c.teleportToY = 3945;
          }
          stop();
        }
      });
    }

  }

  public static void WildyClimb(final Client c) {
    int[] x = { 2993, 2994, 2995 };
    boolean bool = false;
    for (int i = 0; i < x.length; i++) {
      if (c.getPosition().getX() == x[i] && c.getPosition().getY() == 3937) {
        bool = true;
      }
    }
    if (bool) {
      final int oldEmote = c.getWalkAnim();
      c.UsingAgility2 = true;
      c.setWalkAnim(737);
      c.getUpdateFlags().setRequired(UpdateFlag.APPEARANCE, true);
      c.AddToCords(0, -5);
      EventManager.getInstance().registerEvent(new Event(3500) {
        public void execute() {
          if (c == null || c.disconnected) {
            stop();
            return;
          }
          c.setWalkAnim(oldEmote);
          c.UsingAgility2 = false;
          c.getUpdateFlags().setRequired(UpdateFlag.APPEARANCE, true);
          if (c.wildyStage == 4) {
            c.giveExperience(4150, Skill.AGILITY);
            c.addItem(2996, 1);
            c.wildyStage = 0;
          }
          stop();
        }
      });
    }

  }
}