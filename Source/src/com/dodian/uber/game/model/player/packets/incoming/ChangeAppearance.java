package com.dodian.uber.game.model.player.packets.incoming;

import com.dodian.uber.game.model.UpdateFlag;
import com.dodian.uber.game.model.entity.player.Client;
import com.dodian.uber.game.model.player.packets.Packet;

public class ChangeAppearance implements Packet {

  @Override
  public void ProcessPacket(Client client, int packetType, int packetSize) {
    int gender = client.getInputStream().readSignedByte();
    int head = client.getInputStream().readSignedByte();
    int jaw = client.getInputStream().readSignedByte();
    int torso = client.getInputStream().readSignedByte();
    int arms = client.getInputStream().readSignedByte();
    int hands = client.getInputStream().readSignedByte();
    int legs = client.getInputStream().readSignedByte();
    int feet = client.getInputStream().readSignedByte();
    int hairC = client.getInputStream().readSignedByte();
    int torsoC = client.getInputStream().readSignedByte();
    int legsC = client.getInputStream().readSignedByte();
    int feetC = client.getInputStream().readSignedByte();
    int skinC = client.getInputStream().readSignedByte();

    client.getPlayerLook()[0] = gender;
    client.setGender(gender);
    client.setHead(head);
    client.setBeard(jaw);
    client.setTorso(torso);
    client.setArms(arms);
    client.setHands(hands);
    client.setLegs(legs);
    client.setFeet(feet);
    client.getPlayerLook()[1] = hairC;
    client.getPlayerLook()[2] = torsoC;
    client.getPlayerLook()[3] = legsC;
    client.getPlayerLook()[4] = feetC;
    client.getPlayerLook()[5] = skinC;
    client.getUpdateFlags().setRequired(UpdateFlag.APPEARANCE, false);
  }

}
