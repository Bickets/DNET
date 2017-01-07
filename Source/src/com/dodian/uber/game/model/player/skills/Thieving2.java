package com.dodian.uber.game.model.player.skills;

import com.dodian.cache.object.GameObjectDef;
import com.dodian.uber.game.event.Event;
import com.dodian.uber.game.event.EventManager;
import com.dodian.uber.game.model.Position;
import com.dodian.uber.game.model.entity.player.Client;
import com.dodian.uber.game.model.player.packets.outgoing.SendMessage;
import com.dodian.uber.game.model.player.packets.outgoing.Sound;
import com.dodian.utilities.Misc;
import com.dodian.utilities.Utils;


public class Thieving2 {
	
	public static final int PICKPOCKET_EMOTE = 881;
	
	public static final int STALL_THIEVING_EMOTE = 832;
	
	public static final int EMPTY_STALL_ID = 4276;
	
	public boolean isThieving;
	
	public enum ThievingType {
		PICKPOCKETING,
		STALL_THIEVING,
		OTHER
	}
		
	public enum ThievingData {
		FARMER(7, 10, 800, new int[] {314}, new int[] {Utils.random(5) + 2}, new int[] {100}, 0, ThievingType.PICKPOCKETING), 
		MASTER_FARMER(2235, 70, 1200, new int[] {314}, new int[] {Utils.random(8) + 2}, new int[] {100}, 0, ThievingType.PICKPOCKETING),
		CAGE(6836, 1, 150, new int[] {995}, new int[] {Utils.random(10) + 2}, new int[] {100}, 0, ThievingType.OTHER),
		BAKER_STALL(2561, 10, 800, new int[] {2309}, new int[] {1}, new int[] {100}, 10, ThievingType.STALL_THIEVING),
		FUR_STALL(2560, 40, 1300, new int[] {1751, 1753, 1739, 1759, 995}, new int[] {1, 1, 1, 1, 700 + Utils.random(1000)}, new int[] {5, 10, 15, 20, 100}, 20, ThievingType.STALL_THIEVING),
		SILVER_STALL(2565, 65, 1800, new int[] {2349, 2351, 2353, 2357, 2359, 995}, new int[] {1, 1, 1, 1, 1,1000 + Utils.random(1500)}, new int[] { 5, 10, 15, 20, 25, 100}, 25, ThievingType.STALL_THIEVING),
		SPICE_STALL(2564, 80, 5000, new int[] {215, 213, 209, 207, 203, 199}, new int[] {1, 1, 1, 1, 1, 1}, new int[] {5, 10, 20, 35, 55, 100}, 30, ThievingType.STALL_THIEVING),
		GEM_STALL(2562, 90, 2800, new int[] {1617, 1619, 1621, 1623, 995}, new int[] {1, 1, 1, 1, 8000 + Utils.random(1800)}, new int[] {1, 5, 10, 15, 20, 100}, 35, ThievingType.STALL_THIEVING);
		//RINGBELL(6847, 1, 0, new int[] {4084}, new int[] {1}, new int[] {100}, 25000000, ThievingType.OTHER);
		
		private ThievingData(int entityId, int requiredLevel, int receivedExperience, int[] item, int[] itemAmount, int[] itemChance, int respawnTime, ThievingType type) {
			this.entityId = entityId;
			this.requiredLevel = requiredLevel;
			this.receivedExperience = receivedExperience;
			this.item = item;
			this.itemAmount = itemAmount;
			this.itemChance = itemChance;
			this.respawnTime = respawnTime;
			this.type = type;
		}
		
		public int entityId, requiredLevel, receivedExperience, respawnTime;
		
		public int item[];
		
		public int itemAmount[];
		
		public int itemChance[];
		
		public ThievingType type;
		
		public int getEntityId() {
			return entityId;
		}
		
		public int getRequiredLevel() {
				return requiredLevel;
		}
		
		public int getReceivedExperience() {
			return receivedExperience;
		}
		
		public int[] getItemId() {
			return item;
		}
		
		public int[] getItemAmount() {
			return itemAmount;
		}
		
		public int[] getItemItemChance() {
			return itemChance;
		}
		
		public int getRespawnTime() {
			return respawnTime;
		}
		
		public ThievingType getThievingType() {
			return type;
		}
		
	}	
	
	/**
	 * This method is used to determine what information should be gathered if the entity you're thieving from exists in the Enum.
	 * @param entityId
	 * @return
	 */
	public static ThievingData forId(int entityId) {
		for (ThievingData data : ThievingData.values()) {
			if (entityId == data.getEntityId()) {
				return data;
			}
		}
		return null;
	}	
	
	/**
	 * This method is used to generate chance of failure while thieving from an entity.
	 * @return failChance
	 */
	private static int generateFailChance() {
		return 0;
	}
	
	/**
	 * This method is used to determine whether to use a, an, or some depending on the received item's name.
	 * @param itemName
	 * @return
	 */
	private static String aAnOrSome(String itemName) {
		if ((itemName.startsWith("a") || itemName.startsWith("e") || itemName.startsWith("i") || itemName.startsWith("o") || itemName.startsWith("u")) && !itemName.endsWith("s")) {
			return "an";
		} else if (itemName.endsWith("s")) {
			return "some";
		} else {
			return "a";
		}
	}
	
	
	/**
	 * Attempts to steal from the entity.
	 */
	public static void attemptSteal(final Client player, final int entityId, final Position position) {
		if (System.currentTimeMillis() - player.lastAction < 3000) {
			   return;
		}
		player.lastAction = System.currentTimeMillis();
		
		final ThievingData data = forId(entityId);
		
		final int failChance = generateFailChance();
		
		final GameObjectDef definition = Misc.getObject(entityId, position.getX(), position.getY(), player.getPosition().getZ());
		
		if (data == null) {
			return;
		}
		
		if (player.getLevel(Skill.THIEVING) < data.getRequiredLevel()) {
			player.send(new SendMessage("You need a thieving level of " + data.getRequiredLevel() + " to steal from " + data.toString().toLowerCase().replace('_', ' ') + "s."));
			return;
		}
		
		if (data.getThievingType() == ThievingType.PICKPOCKETING || data.getThievingType() == ThievingType.OTHER) {
			player.setFocus(position.getX(), position.getY());
			player.requestAnim(PICKPOCKET_EMOTE, 0);
			player.send(new SendMessage("You attempt to steal from the " + data.toString().toLowerCase().replace('_', ' ') + "..."));
		} else {
			player.requestAnim(STALL_THIEVING_EMOTE, 0);
		}
		
//		if (ThievingData.RINGBELL != null) {
//			player.setFocus(position.getX(), position.getY());
//			player.requestAnim(PICKPOCKET_EMOTE, 0);
//			player.send(new SendMessage("You ring the bell to celebrate the season!"));
//		} else {
//			player.requestAnim(STALL_THIEVING_EMOTE, 0);
//		}
		
		EventManager.getInstance().registerEvent(new Event(600) {

			@Override
			public void execute() {
				if (player == null || player.disconnected ) {
					this.stop();
					return;
				}
				
				if (failChance > 75) {
					player.send(new SendMessage("You fail to thieve from the " + data.toString().toLowerCase().replace('_', ' ') + "."));
					this.stop();
					return;
				}
				
				if (player.hasSpace()) {
					player.giveExperience(data.getReceivedExperience(), Skill.THIEVING);
					player.canPreformAction = false;
					
					if (data.getItemId().length > 1) {
						int rollChance = (int) (Math.random() * 100);
						
						for (int i = 0; i < data.getItemId().length; i++) {
							if (rollChance < data.getItemItemChance()[i]) {
								player.addItem(data.getItemId()[i], data.getItemAmount()[i]);
								player.send(new SendMessage("You receive " + aAnOrSome(player.GetItemName(data.getItemId()[i])) + " " + player.GetItemName(data.getItemId()[i]).toLowerCase() + "."));
								break;
							}
						}
						
					} else {
						player.addItem(data.getItemId()[0], data.getItemAmount()[0]);
						player.send(new SendMessage("You receive " + aAnOrSome(player.GetItemName(data.getItemId()[0])) + " " + player.GetItemName(data.getItemId()[0]).toLowerCase() + "."));
					}					
					
					player.send(new Sound(356));
					
					if (data.getThievingType() == ThievingType.STALL_THIEVING) {
						player.ReplaceObject(position.getX(), position.getY(), EMPTY_STALL_ID, definition.getFace(), definition.getType());							
						EventManager.getInstance().registerEvent(new Event(data.getRespawnTime() * 600) {

							@Override
							public void execute() {
								player.ReplaceObject(position.getX(), position.getY(), data.getEntityId(), definition.getFace(), definition.getType());
								this.stop();
								return;
							}
							
						});
					}
					this.stop();
					return;
					
				} else {
					player.send(new SendMessage("You don't have enough inventory space!"));
					this.stop();
					return;
				}			
			}			
		});			
	}
}