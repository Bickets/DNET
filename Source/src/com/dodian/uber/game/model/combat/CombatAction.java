package com.dodian.uber.game.model.combat;

import com.dodian.uber.game.model.entity.Entity;

public abstract class CombatAction {

  public abstract void performCombatAction(Entity attacker, Entity victim);
}
