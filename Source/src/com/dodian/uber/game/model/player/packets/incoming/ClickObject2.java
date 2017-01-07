package com.dodian.uber.game.model.player.packets.incoming;

import java.util.Random;

import com.dodian.cache.object.GameObjectData;
import com.dodian.cache.object.GameObjectDef;
import com.dodian.uber.game.event.Event;
import com.dodian.uber.game.event.EventManager;
import com.dodian.uber.game.model.Position;
import com.dodian.uber.game.model.UpdateFlag;
import com.dodian.uber.game.model.WalkToTask;
import com.dodian.uber.game.model.entity.player.Client;
import com.dodian.uber.game.model.object.RS2Object;
import com.dodian.uber.game.model.player.packets.Packet;
import com.dodian.uber.game.model.player.skills.Thieving2;
import com.dodian.utilities.Misc;

public class ClickObject2 implements Packet {

	@Override
	public void ProcessPacket(Client client, int packetType, int packetSize) {
		int objectID = client.getInputStream().readUnsignedWordBigEndianA(); // 5292
		int objectY = client.getInputStream().readSignedWordBigEndian();
		int objectX = client.getInputStream().readUnsignedWordA();

		final WalkToTask task = new WalkToTask(WalkToTask.Action.OBJECT_SECOND_CLICK, objectID,
				new Position(objectX, objectY));
		GameObjectDef def = Misc.getObject(objectID, objectX, objectY, client.getPosition().getZ());
		GameObjectData object = GameObjectData.forId(task.getWalkToId());
		client.setWalkToTask(task);
		if (objectID == 2646) {
			client.addItem(1779, 1);
		}
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
				Position objectPosition = Misc.goodDistanceObject(task.getWalkToPosition().getX(), task.getWalkToPosition().getY(), client.getPosition().getX(), client.getPosition().getY(), object.getSizeX(def.getFace()), object.getSizeY(def.getFace()), client.getPosition().getZ());
				if (objectPosition == null)
					return;
				clickObject2(client, task.getWalkToId(), task.getWalkToPosition());
				client.setWalkToTask(null);
				this.stop();
			}

		});
	}

	public void clickObject2(Client client, int objectID, Position position) {
		System.out.println("atObject2: " + position.getX() + "," + position.getY() + " objectID: " + objectID);
		if (client.adding) {
			client.objects.add(new RS2Object(objectID, position.getX(), position.getY(), 2));
		}
		if (client.playerRights > 1) {
			client.println_debug("atObject2: " + position.getX() + "," + position.getY() + " objectID: " + objectID);
		}
		client.setFocus(position.getX(), position.getY());
		
		switch(objectID) {
		case 2561:
		case 6836:
		case 2560:
		case 2565:
		case 2564:
		case 2562:
			Thieving2.attemptSteal(client, objectID, position);
			break;
		}
		
		/*if (objectID == 2564) {
			client.skillX = position.getX();
			client.setSkillY(position.getY());
			client.WanneThieve = 2564;
		}
		if (objectID == 2563) {
			client.skillX = position.getX();
			client.setSkillY(position.getY());
			client.WanneThieve = 2563;
		}
		if (objectID == 2565) {
			client.skillX = position.getX();
			client.setSkillY(position.getY());
			client.WanneThieve = 2565;
		}
		//if (objectID == 2561) {
		//	client.skillX = position.getX();
		//	client.setSkillY(position.getY());
		//	client.WanneThieve = 2561;
		//}
		if (objectID == 2560) {
			client.skillX = position.getX();
			client.setSkillY(position.getY());
			client.WanneThieve = 2560;
		}*/
		if (objectID == 4877) {
			client.skillX = position.getX();
			client.setSkillY(position.getY());
			client.WanneThieve = 4877;
		}
		/*if (objectID == 2562) {
			client.skillX = position.getX();
			client.setSkillY(position.getY());
			client.WanneThieve = 2562;
		}*/
		if (objectID == 2644 && position.getX() == 2710 && position.getY() == 3471) {
			client.spinning = true;
			client.getUpdateFlags().setRequired(UpdateFlag.APPEARANCE, true);
		}
		if (objectID == 823) {
			Random r = new Random();
			client.moveTo(2602 + r.nextInt(5), 3162 + r.nextInt(5), client.getPosition().getZ());
		}
		if ((objectID == 2213) || (objectID == 2214) || (objectID == 3045) || (objectID == 5276)
				|| (objectID == 6084)) {
			//System.out.println("Banking..");
			client.skillX = position.getX();
			client.setSkillY(position.getY());
			client.WanneBank = 1;
			client.WanneShop = -1;
		}
	}

}
