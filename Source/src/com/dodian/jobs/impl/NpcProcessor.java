package com.dodian.jobs.impl;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.dodian.uber.game.Server;
import com.dodian.uber.game.model.entity.npc.Npc;

@DisallowConcurrentExecution

public class NpcProcessor implements Job {
  
  public void execute(JobExecutionContext context) throws JobExecutionException {
    for (Npc npc : Server.npcManager.getNpcs()) {
      long now = System.currentTimeMillis();
      npc.clearUpdateFlags();
      if (!npc.alive && npc.visible && (now - npc.getDeathTime() >= npc.getTimeOnFloor())) {
        npc.drop();
        npc.setVisible(false);
      }
      if (!npc.alive && !npc.visible && (now - (npc.getDeathTime() + npc.getTimeOnFloor()) >= (npc.getRespawn() * 1000))) {
        npc.respawn();
      }
      if (npc.alive && npc.isFighting() && now - npc.getLastAttack() >= 2000) {
        if (npc.getId() == 430 || npc.getId() == 1977)
          npc.attack_new();
        else if (npc.getId() == 3200 || npc.getId() == 2882)
          npc.bossAttack();
        else
          npc.attack();
        npc.setLastAttack(System.currentTimeMillis());
      }
//      if (npc.getId() == 2676) {
//        if (npc.getLastChatMessage() < System.currentTimeMillis() - 45000) {
//          npc.setText((VotingIncentiveManager.getMilestone() - VotingIncentiveManager.getVotes()) + " votes left until the next drop party!");
//          npc.setLastChatMessage();
//        } else if(System.currentTimeMillis() - npc.getLastChatMessage() < 5000) {
//          npc.setText((VotingIncentiveManager.getMilestone() - VotingIncentiveManager.getVotes()) + " votes left until the next drop party!");
//        }
//      }
    }
  }

}