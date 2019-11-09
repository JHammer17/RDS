package de.JHammer.RDS.Listener;


import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import de.JHammer.RDS.Main;
import de.JHammer.RDS.Enums.GameState;
import de.JHammer.RDS.Objects.RDSPlayer;

public class SpectatorListener implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onTarget(EntityTargetEvent e) {
		
		if(e.getTarget() instanceof Player) {
			if(Main.ins.getRDSPlayer((Player) e.getTarget()).isSpectator()) {
				
				
				
				e.setCancelled(true);
				
			}
		}
	}
	
	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		if(e.getEntity() instanceof Player) {
			if(Main.ins.getRDSPlayer((Player) e.getEntity()).isSpectator()) {
				e.setCancelled(true);
				e.setDamage(0);
			}
		}
	}
	
	@EventHandler
	public void onDamage(EntityDamageByEntityEvent e) {
		if(e.getEntity() instanceof Player) {
			if(Main.ins.getRDSPlayer((Player) e.getEntity()).isSpectator()) {
				e.setCancelled(true);
				e.setDamage(0);
			}
		}
		if(e.getDamager() instanceof Player) {
			if(Main.ins.getRDSPlayer((Player) e.getDamager()).isSpectator()) {
				e.setCancelled(true);
				e.setDamage(0);
			}
		}
	}
	
	@EventHandler
	public void onOpenTeleporter(PlayerInteractEvent e)  {
		if(Main.ins.state == GameState.INGAME) {
			if(Main.ins.getRDSPlayer(e.getPlayer()).isSpectator()) {
				if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
					if(e.getItem() != null && e.getItem().getType() == Material.COMPASS) {
						openSpecInv(e.getPlayer());
					}
				}
			}
		}
	}
	
	public void openSpecInv(Player p) {
		Inventory inv = Bukkit.createInventory(null, 9*3, "Zuschauen§a");
		
		ItemStack empty = Main.ins.utils.createItem(Material.STAINED_GLASS_PANE, 15, 1, "§a", null);
		
		for(int i = 0; i <= 26; i++) inv.setItem(i, empty);
		
		
		int slot = 11;
		
		for(RDSPlayer players : Main.ins.getRDSPlayersCopy().values()) {
			if(!players.isSpectator()) {
				if(slot <= 15) {
					ItemStack skull = Main.ins.utils.createItem(Material.SKULL_ITEM, 3, 1, "§6" + players.getPlayer().getName(), null);
					
					
					SkullMeta meta = (SkullMeta) skull.getItemMeta();
					
					meta.setOwner(players.getPlayer().getName());
					
					skull.setItemMeta(meta);
					
					
					inv.setItem(slot, skull);
					slot++;
					
				} else break;
			}
		}
		
		
		p.openInventory(inv);
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if(e.getInventory() != null && e.getClickedInventory() != null) {
			if(e.getWhoClicked() instanceof Player) {
				if(e.getInventory().getTitle().equalsIgnoreCase("Zuschauen§a")) {
					e.setCancelled(true);
					if(e.getClickedInventory().getTitle().equalsIgnoreCase("Zuschauen§a")) {
						
						if(e.getCurrentItem() != null) {
							if(e.getCurrentItem().getType() == Material.SKULL_ITEM) {
								
								ItemStack item = e.getCurrentItem();
								if(item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
									
									String user = item.getItemMeta().getDisplayName().replaceAll("§6", "");
									
									
									if(user != null && Bukkit.getPlayer(user) != null) {
										
										if(!Main.ins.getRDSPlayer(Bukkit.getPlayer(user)).isSpectator()) {
											e.getWhoClicked().teleport(Bukkit.getPlayer(user));
										} else {
											e.getWhoClicked().sendMessage(Main.ins.prefix + "§cDieser Spieler ist bereits ausgeschieden!");
											openSpecInv((Player) e.getWhoClicked());
										}
										
										
									} else {
										e.getWhoClicked().sendMessage(Main.ins.prefix + "§cDieser Spieler konnte nicht gefunden werden!");
									}
									
									
								}
								
								
							}
						}
						
						
					}
				}
			}
		}
	}
	
}
