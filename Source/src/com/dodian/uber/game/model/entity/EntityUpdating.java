package com.dodian.uber.game.model.entity;

import com.dodian.uber.game.model.entity.player.Player;
import com.dodian.utilities.Stream;

/**
 * @author Dashboard
 */
public abstract class EntityUpdating<T extends Entity> {
  
  public abstract void update(Player player, Stream stream);
  
  public abstract void appendBlockUpdate(T t, Stream stream);
  
  public abstract void appendAnimationRequest(T t, Stream stream);
  
  public abstract void appendFaceCoordinates(T t, Stream stream);
  
  public abstract void appendFaceCharacter(T t, Stream stream);
  
  public abstract void appendPrimaryHit(T t, Stream stream);
  
}
