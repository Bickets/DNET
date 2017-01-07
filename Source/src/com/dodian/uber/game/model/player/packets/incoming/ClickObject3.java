package com.dodian.uber.game.model.player.packets.incoming;

import com.dodian.cache.object.GameObjectData;
import com.dodian.cache.object.GameObjectDef;
import com.dodian.uber.game.event.Event;
import com.dodian.uber.game.event.EventManager;
import com.dodian.uber.game.model.Position;
import com.dodian.uber.game.model.WalkToTask;
import com.dodian.uber.game.model.entity.player.Client;
import com.dodian.uber.game.model.player.packets.Packet;
import com.dodian.utilities.Misc;

public class ClickObject3 implements Packet {

  @Override
  public void ProcessPacket(Client client, int packetType, int packetSize) {
    int objectX = client.getInputStream().readSignedWordBigEndian();
    int objectY = client.getInputStream().readUnsignedWord();
    int objectID = client.getInputStream().readUnsignedWordBigEndianA();

    final WalkToTask task = new WalkToTask(WalkToTask.Action.OBJECT_THIRD_CLICK, objectID,
        new Position(objectX, objectY));
    GameObjectDef def = Misc.getObject(objectID, objectX, objectY, client.getPosition().getZ());
    GameObjectData object = GameObjectData.forId(task.getWalkToId());
    client.setWalkToTask(task);
    if (client.randomed) {
      return;
    }
    EventManager.getInstance().registerEvent(new Event(600) {

      @Override
      public void execute() {

        if (client == null || client.disconnected) {
          this.stop();
          return;
        }

        if (client.getWalkToTask() != task) {
          this.stop();
          return;
        }

        Position objectPosition = Misc.goodDistanceObject(task.getWalkToPosition().getX(),
            task.getWalkToPosition().getY(), client.getPosition().getX(), client.getPosition().getY(),
            object.getSizeX(def.getFace()), object.getSizeY(def.getFace()), client.getPosition().getZ());
        if (objectPosition == null)
          return;

        clickObject3(client, task.getWalkToId(), task.getWalkToPosition());
        client.setWalkToTask(null);
        this.stop();
      }

    });
  }

  public void clickObject3(Client client, int objectID, Position position) {
    if (objectID == 1739) {
      client.moveTo(client.getPosition().getX(), client.getPosition().getY(), client.getPosition().getZ() - 1);
    }
  }

}
