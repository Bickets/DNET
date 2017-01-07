package com.dodian.uber.game.model.player.packets.incoming;

import com.dodian.uber.game.model.entity.player.Client;
import com.dodian.uber.game.model.item.Equipment;
import com.dodian.uber.game.model.player.content.Skillcape;
import com.dodian.uber.game.model.player.packets.Packet;
import com.dodian.uber.game.model.player.packets.outgoing.RemoveInterfaces;
import com.dodian.uber.game.model.player.packets.outgoing.SendMessage;

public class Bank10 implements Packet {

  @Override
  public void ProcessPacket(Client client, int packetType, int packetSize) {
    int interfaceID = client.getInputStream().readUnsignedWordBigEndian();
    int removeID = client.getInputStream().readUnsignedWordA();
    int removeSlot = client.getInputStream().readUnsignedWordA();
    client.println_debug("RemoveItem 10: " + removeID + " InterID: " + interfaceID + " slot: " + removeSlot);
    if (interfaceID == 3322 && client.inDuel) { // remove from bag to duel
      // window
      client.stakeItem(removeID, removeSlot, 10);
    } else if (interfaceID == 6669 && client.inDuel) { // remove from duel
      // window
      client.fromDuel(removeID, removeSlot, 10);
    } else if (interfaceID == 1688) { // remove from duel
      Skillcape skillcape = Skillcape.getSkillCape(removeID);
      if(skillcape != null) {
      if (client.getExperience(skillcape.getSkill()) < 50000000) {
        client.send(new SendMessage("Need 50 million in "
            + skillcape.name().toLowerCase().replace("_", " ").replace(" cape", "") + " to convert this cape!"));
        return;
      }
      if (!Skillcape.isTrimmed(removeID)) {
        client.getEquipment()[Equipment.Slot.CAPE.getId()] = skillcape.getTrimmedId();
        client.getEquipmentN()[Equipment.Slot.CAPE.getId()] = 1;
        client.setEquipment(skillcape.getTrimmedId(), 1, removeSlot);
        client.send(new SendMessage("You turn your cape into a trimmed version!"));
      } else {
        client.getEquipment()[Equipment.Slot.CAPE.getId()] = skillcape.getUntrimmedId();
        client.getEquipmentN()[Equipment.Slot.CAPE.getId()] = 1;
        client.setEquipment(skillcape.getUntrimmedId(), 1, removeSlot);
        client.send(new SendMessage("You turn your cape back into a untrimmed version!"));
        }
      }
    } else if (interfaceID == 5064) { // remove from bag to bank
      client.bankItem(removeID, removeSlot, 10);
    } else if (interfaceID == 5382) { // remove from bank
      client.fromBank(removeID, removeSlot, 10);
    } else if (interfaceID == 3322 && client.inTrade) { // remove from bag to
                                                        // trade
      // window
      client.tradeItem(removeID, removeSlot, 10);
    } else if (interfaceID == 3415 && client.inTrade) { // remove from trade
                                                        // window
      client.fromTrade(removeID, removeSlot, 10);
    } else if (interfaceID >= 4233 && interfaceID <= 4245) {
      client.startGoldCrafting(interfaceID, removeSlot, 10);
    } else if (interfaceID == 3823) { // Show value to sell items
      client.sellItem(removeID, removeSlot, 5);
    } else if (interfaceID == 3900) { // Show value to buy items
      client.buyItem(removeID, removeSlot, 5);
    } else if (interfaceID >= 1119 && interfaceID <= 1123) { // Smithing
      if (client.smithing[2] > 0) {
        client.smithing[4] = removeID;
        client.smithing[0] = 1;
        client.smithing[5] = 10;
        client.send(new RemoveInterfaces());
      } else {
        client.send(new SendMessage("Illigal Smithing !"));
        client.println_debug("Illigal Smithing !");
      }
    }
  }

}
