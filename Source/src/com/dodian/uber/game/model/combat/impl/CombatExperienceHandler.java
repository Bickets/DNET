package com.dodian.uber.game.model.combat.impl;

import com.dodian.uber.game.model.entity.Entity;

public abstract class CombatExperienceHandler {

  public abstract void appendExperience(Entity attacker, int hit);

}
