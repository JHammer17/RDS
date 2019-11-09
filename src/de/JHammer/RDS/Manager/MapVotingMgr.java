package de.JHammer.RDS.Manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import de.JHammer.RDS.Main;
import de.JHammer.RDS.Enums.GameState;
import de.JHammer.RDS.Manager.SoundMgr.JSound;
import de.JHammer.RDS.Static.Counter;


public class MapVotingMgr implements Listener {

	public MapVotingMgr() {
		updateVotesMenu();
	}
	
	
	@EventHandler
	public void onClick(PlayerInteractEvent e) {
		if(Main.ins.state == GameState.LOBBY) {
			if(e.getItem() != null && 
					Main.ins.utils.compareItem(
							e.getItem(), 
							Main.ins.utils.getVoteItem(), 
							false)) {
				
				
				if(Counter.getStartCounter() > 10) {
					openVotesMenu(e.getPlayer());
					Main.ins.utils.getSoundMgr().playSound(e.getPlayer(), JSound.ITEM_PICKUP, 100, 2);
				} else {
					Main.ins.utils.getSoundMgr().playSound(e.getPlayer(), JSound.ITEM_BREAK, 100, (float) 0.5);
					e.getPlayer().sendMessage(Main.ins.prefix + "§cDas Voting ist vorbei!");
				}
				e.setCancelled(true);
			}
		}
	}
	
	static Inventory globalVotesMenu;
	
	static HashMap<Integer, MapVoting> maps = new HashMap<>();
	
	public static void updateVotesMenu() {
		if(globalVotesMenu == null) {
			globalVotesMenu = Bukkit.createInventory(null, 9*3, "Voting§a");
		}
		
		ItemStack empty = Main.ins.utils.createItem(Material.STAINED_GLASS_PANE, 15, 1, "§a", null);
		
		ItemStack exit = Main.ins.utils.createItem(Material.BARRIER, 0, 1, "§cMenü schließen", null);
		
		for(int i = 0; i <= 26; i++) {
			globalVotesMenu.setItem(i, empty);
		}
		
		globalVotesMenu.setItem(22, exit);
		
		
			ArrayList<String> m = MapManager.getAllMaps(false);
			
			int slot = 13;
			
			
			if(!m.isEmpty()) {
				
				for(String str : m) {
					
					if(!MapManager.isMapOkay(str)) continue;
					
					
					MapVoting mv;
					if(!maps.containsKey(slot)) {
						maps.put(slot, new MapVoting(str));
					} 
					mv = maps.get(slot);
					
					String lore;
					
					if(mv.getVotes() == 1) lore = "einen Vote";
					else lore = mv.getVotes() + " Votes";
					
					
					globalVotesMenu.setItem(slot, Main.ins.utils.createItem(Material.PAPER, 0, 1, "§6" + mv.getMapName(), "§e" + lore));
					
					if(slot == 13) slot = 12;
					else if(slot == 12) slot = 14;
					else if(slot == 14) slot = 11;
					else if(slot == 11) slot = 15;
					else if(slot == 15) slot = 10;
					else if(slot == 10) slot = 16;
					else if(slot == 16) break;
					
				}
			} else {
				Bukkit.broadcastMessage("§cKeine Maps gefunden!");
			}
			
			
		
		
		
	}
	
	static HashMap<UUID, Integer> playerVotes = new HashMap<>();
	
	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		playerVotes.remove(e.getPlayer().getUniqueId());
	}
	
	
	@EventHandler
	public void onVote(InventoryClickEvent e) {
		if(Main.ins.state == GameState.LOBBY) {
			if(e.getInventory() != null && e.getClickedInventory() != null) {
				if(e.getClickedInventory().getTitle().equalsIgnoreCase("Voting§a")) {
					if(e.getSlot() == 22) {
						e.getWhoClicked().closeInventory();
						return;
					}
					
					if(maps.containsKey(e.getSlot())) {
						
						
						
						
						if(playerVotes.containsKey(e.getWhoClicked().getUniqueId())) {
							maps.get(playerVotes.get(e.getWhoClicked().getUniqueId())).setVotes(maps.get(playerVotes.get(e.getWhoClicked().getUniqueId())).getVotes()-1);
						}
						
						playerVotes.put(e.getWhoClicked().getUniqueId(), e.getSlot());
						
						
						maps.get(e.getSlot()).setVotes(maps.get(e.getSlot()).getVotes()+1);

						
						Main.ins.utils.getSoundMgr().playSound((Player) e.getWhoClicked(), JSound.CLICK, 1, (float) 1.5);
						
						updateVotesMenu();
						
					}
				}
			}
		}
	}
	
	
	
	public static void openVotesMenu(Player p) {
		
		updateVotesMenu();
		
		
		p.openInventory(globalVotesMenu);
	}
	
	public static String getWonMap() {
		
		int maxVotes = -1;
		String mapName = "";
		
		
		for(MapVoting vote : maps.values()) {
			if(vote.getVotes() > maxVotes) {
				maxVotes = vote.getVotes();
				mapName = vote.getMapName();
			}
		}
		
		
		return mapName;
	}
	
	public static void cleanUp() {
		maps.clear();
		playerVotes.clear();
		globalVotesMenu = null;
	}
	
	
	private static class MapVoting {
		
		private String mapName;
		
		private int votes;

		public MapVoting(String mapName) {
			this.mapName = mapName;
			
		}
		
		public String getMapName()  {
			return mapName;
		}
		
		
		
		public int getVotes() {
			return votes;
		}
		
		public void setVotes(int votes) {
			this.votes = votes;
		}
		
		
	}
	
	
	
	
}
