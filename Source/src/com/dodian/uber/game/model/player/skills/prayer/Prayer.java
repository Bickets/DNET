package com.dodian.uber.game.model.player.skills.prayer;

import com.dodian.uber.game.model.entity.player.Client;
import com.dodian.uber.game.model.player.packets.outgoing.SendMessage;
import com.dodian.uber.game.model.player.skills.Skill;

/**
 * 
 * @author Dashboard
 *
 */
public class Prayer {

  public static boolean buryBones(Client client, int itemId, int itemSlot) {
    Bones bone = Bones.getBone(itemId);
    if (bone == null) {
      return false;
    }
    client.requestAnim(827, 0);
    client.giveExperience(bone.getExperience(), Skill.PRAYER);
    client.deleteItem(itemId, itemSlot, 1);
    client.send(new SendMessage("You bury the bones."));
    return true;
  }

}
