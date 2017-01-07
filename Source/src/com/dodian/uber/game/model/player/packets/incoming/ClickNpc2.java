package com.dodian.uber.game.model.player.packets.incoming;

import com.dodian.uber.game.Server;
import com.dodian.uber.game.event.Event;
import com.dodian.uber.game.event.EventManager;
import com.dodian.uber.game.model.WalkToTask;
import com.dodian.uber.game.model.entity.npc.Npc;
import com.dodian.uber.game.model.entity.player.Client;
import com.dodian.uber.game.model.player.packets.Packet;
import com.dodian.uber.game.model.player.packets.outgoing.SendMessage;
import com.dodian.uber.game.model.player.skills.Thieving2;

public class ClickNpc2 implements Packet {

	@Override
	public void ProcessPacket(Client client, int packetType, int packetSize) {
		// npcIndex = ((misc.HexToInt(getInputStream().buffer, 0, packetSize) /
		// 1000) - 128);
		// npcIndex = getInputStream().readUnsignedWordBigEndianA();
		int npcIndex = client.getInputStream().readSignedWordBigEndianA();
		Npc tempNpc = Server.npcManager.getNpc(npcIndex);
		if (tempNpc == null)
			return;
		int NPCID = tempNpc.getId();

		final WalkToTask task = new WalkToTask(WalkToTask.Action.NPC_SECOND_CLICK, NPCID, tempNpc.getPosition());
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

				if (!client.goodDistanceEntity(tempNpc, 1)) {
					return;
				}

				clickNpc2(client, tempNpc);
				client.setWalkToTask(null);
				this.stop();
			}

		});
	}

	public void clickNpc2(Client client, Npc tempNpc) {
		int NPCID = tempNpc.getId();
		client.faceNPC(tempNpc.getSlot());
		long time = System.currentTimeMillis();
		if (time - client.globalCooldown[0] <= 50) {
			client.send(new SendMessage("Action throttled... please wait longer before acting!"));
			return;
		}

		client.globalCooldown[0] = time;
		int npcX = tempNpc.getPosition().getX();
		int npcY = tempNpc.getPosition().getY();
		if (Math.abs(client.getPosition().getX() - npcX) > 50 || Math.abs(client.getPosition().getY() - npcY) > 50) {
			// send(new SendMessage("Client hack detected!");
			// break;
		}
		if (!tempNpc.isAlive()) {
			client.send(new SendMessage("That monster has been killed!"));
			return;
		}

		client.skillX = npcX;
		client.setSkillY(npcY);
		client.startFishing(NPCID, 2);
		
		switch (NPCID) {
		case 7:
		case 2235:
			Thieving2.attemptSteal(client, NPCID, tempNpc.getPosition());
			break;
		}
		if (NPCID == 494 || NPCID == 495 || NPCID == 2619) { /* Banking */
			client.WanneBank = 1;
		} else if (NPCID == 300 || NPCID == 844
				|| NPCID == 462) { /*
									 * Essence Mine Guys
									 */
			client.stairs = 26;
			client.stairDistance = 1;
			if (NPCID == 300) {
				client.Essence = 1;
			} else if (NPCID == 844) {
				client.Essence = 2;
			} else if (NPCID == 462) {
				client.Essence = 3;
			}
		} else if (NPCID == 587) {
			client.WanneShop = 39;
		} else if (NPCID == 553) { // Aubury rune shop
			client.WanneShop = 9; // Aubury Magic Shop
		} else if (NPCID == 522 || NPCID == 523) { // Shop Keeper +
			// Assistant
			client.WanneShop = 1; // Varrock General Store
		} else if (NPCID == 526 || NPCID == 527) { // Shop Keeper +
			// Assistant
			client.WanneShop = 3; // Falador General Store
		} else if (NPCID == 577) { // Cassie
			client.WanneShop = 4; // Falador Shield Shop
		} else if (NPCID == 580) { // Flynn
			client.WanneShop = 5; // Falador Mace Shop
		} else if (NPCID == 538) { // Peksa
			client.WanneShop = 6; // Barbarian Vullage Helmet Shop
		} else if (NPCID == 546) { // Zaff
			client.WanneShop = 7; // Varrock Staff Shop
		} else if (NPCID == 548) { // Thessalia
			client.WanneShop = 8; // Varrock Cloth shop
		} else if (NPCID == 551 || NPCID == 552) { // Shop Keeper +
			// Assistant
			client.WanneShop = 9; // Varrock Sword shop
		} else if (NPCID == 549) { // Horvik
			client.WanneShop = 10; // Varrock Armor shop
		} else if (NPCID == 550) { // Lowe
			client.WanneShop = 11; // Varrock Armor shop
		} else if (NPCID == 584) { // Heruin
			client.WanneShop = 12; // Falador Gem Shop
		} else if (NPCID == 581) { // Wayne
			client.WanneShop = 13; // Falador Chainmail Shop
		} else if (NPCID == 585) { // Rommik
			client.WanneShop = 14; // Rimmington Crafting Shop
		} else if (NPCID == 531 || NPCID == 530) { // Shop Keeper +
			// Assistant
			client.WanneShop = 15; // Rimmington General Store
		} else if (NPCID == 1860) { // Brian
			client.WanneShop = 16; // Rimmington Archery Shop
		} else if (NPCID == 557) { // Wydin
			client.WanneShop = 17; // Port Sarim Food Shop
		} else if (NPCID == 558) { // Gerrant
			client.WanneShop = 18; // Port Sarim Fishing Shop
		} else if (NPCID == 559) { // Brian
			client.WanneShop = 19; // Port Sarim Battleaxe Shop
		} else if (NPCID == 556) { // Grum
			client.WanneShop = 20; // Port Sarim Jewelery Shop
		} else if (NPCID == 583) { // Betty
			client.WanneShop = 21; // Port Sarim Magic Shop
		} else if (NPCID == 520 || NPCID == 521) { // Shop Keeper +
			// Assistant
			client.WanneShop = 22; // Lumbridge General Store
		} else if (NPCID == 519) { // Bob

			client.WanneShop = 23; // Lumbridge Axe Shop
		} else if (NPCID == 541) { // Zeke

			client.WanneShop = 24; // Al-Kharid Scimitar Shop
		} else if (NPCID == 545) { // Dommik

			client.WanneShop = 25; // Al-Kharid Crafting Shop
		} else if (NPCID == 524 || NPCID == 525) { // Shop Keeper +
			// Assistant

			client.WanneShop = 26; // Al-Kharid General Store
		} else if (NPCID == 542) { // Louie Legs

			client.WanneShop = 27; // Al-Kharid Legs Shop
		} else if (NPCID == 544) { // Ranael

			client.WanneShop = 28; // Al-Kharid Skirt Shop
		} else if (NPCID == 2621) { // Hur-Koz

			client.WanneShop = 29; // TzHaar Shop Weapons,Amour
		} else if (NPCID == 2622) { // Hur-Lek

			client.WanneShop = 30; // TzHaar Shop Runes
		} else if (NPCID == 2620) { // Hur-Tel

			client.WanneShop = 31; // TzHaar Shop General Store
		} else if (NPCID == 692) { // Throwing shop

			client.WanneShop = 32; // Authentic Throwing Weapons
		} else if (NPCID == 683) { // Bow and arrows

			client.WanneShop = 33; // Dargaud's Bow and Arrows
		} else if (NPCID == 682) { // Archer's Armour

			client.WanneShop = 34; // Aaron's Archery Appendages
		} else if (NPCID == 537) { // Scavvo

			client.WanneShop = 35; // Champion's Rune shop
		} else if (NPCID == 536) { // Valaine

			client.WanneShop = 36; // Champion's guild shop
		} else if (NPCID == 933) { // Legend's Shop

			client.WanneShop = 37; // Legend's Shop
		} else if (NPCID == 932) { // Legends General Store

			client.WanneShop = 38; // Legend's Gen. Store
		} else if (NPCID == 804) {

			client.WanneShop = 25; // Crafting shop
		} else {
			client.println_debug("atNPC 2: " + NPCID);
		}
	}

}
