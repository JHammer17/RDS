package de.JHammer.RDS.Manager;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import de.JHammer.RDS.Main;
import de.JHammer.RDS.Enums.Kit;
import de.JHammer.RDS.Manager.SoundMgr.JSound;
import de.JHammer.RDS.Static.MoneyManager;
import de.JHammer.RDS.Static.MySQLMgr.MySQLMgr;

public class KitBuyMgr implements Listener {

	public static  void openKitBuyMgr(Player p, Kit selected) {
		 Main.ins.getRDSPlayer(p).setSelectedBuy(selected);
		 Inventory inv = Bukkit.createInventory(null, 9*1, "Kit kaufen§a");
		 
		 ItemStack item = Main.ins.utils.createItem(selected.getDisplayItem(), selected.getSubID(), 1, selected.getDisplayName(), selected.getDisplayLore());
		 ItemStack accept = Main.ins.utils.createItem(Material.WOOL, 5, 1, "§aKaufen", null);
		 ItemStack decline = Main.ins.utils.createItem(Material.WOOL, 14, 1, "§cNicht kaufen", null);		 
		 
		 inv.setItem(0, accept);
		 inv.setItem(4, item);
		 inv.setItem(8, decline);
		 
		 p.openInventory(inv);
		 
	}
	
	@EventHandler
	public void onCloseInventory(InventoryCloseEvent e) {
		if(e.getInventory().getTitle().equalsIgnoreCase("Kit kaufen§a")) {
			Main.ins.getRDSPlayer((Player) e.getPlayer()).setSelectedBuy(null);
		}
	}
	
	@EventHandler
	public void onInvClick(InventoryClickEvent e) {
		if(e.getCurrentItem() != null && e.getInventory() != null) {
			if(e.getInventory().getTitle().equalsIgnoreCase("Kit kaufen§a")) {
				e.setCancelled(true);
				if(e.getClickedInventory().getTitle().equalsIgnoreCase("Kit kaufen§a")) {
					e.setCancelled(true);
					
					if(e.getSlot() == 0) {
						Kit toBuy = Main.ins.getRDSPlayer((Player) e.getWhoClicked()).getSelectedBuy();
						
						new BukkitRunnable() {
							
							@Override
							public void run() {
								Player p = (Player)e.getWhoClicked();
								Main.ins.getRDSPlayer(p).updateCoins();
								if(MoneyManager.getMoney(p) >= toBuy.getPrice()) {
									
									MySQLMgr.setBuyed(p.getUniqueId(), Main.ins.getRDSPlayer(p).getSelectedBuy(), true);
									p.sendMessage(Main.ins.prefix + "§aDu hast das Kit §6" + Main.ins.getRDSPlayer(p).getSelectedBuy().getDisplayName() + " §agekauft!");
									
									MoneyManager.removeMoney(p, Main.ins.getRDSPlayer(p).getSelectedBuy().getPrice());
//									CoinsAPI.setCoins(p.getUniqueId().toString(), CoinsSetType.REMOVE, Main.ins.getRDSPlayer(p).getSelectedBuy().getPrice());
									Main.ins.getRDSPlayer(p).setKit(Main.ins.getRDSPlayer(p).getSelectedBuy());
									Main.ins.getRDSPlayer(p).setSelectedBuy(null);
									
									p.sendMessage(Main.ins.prefix + "Du spielst nun mit dem Kit §6" + Main.ins.getRDSPlayer(p).getKit().getName());
									p.closeInventory();
									Main.ins.utils.getSoundMgr().playSound(p, JSound.LEVEL_UP, 100, (float) 0.5);
								} else {
									e.getWhoClicked().sendMessage(Main.ins.prefix + "§cDu hast nicht genug Gens um dir dieses Kit zu kaufen!");
								}
								
							}
						}.runTaskAsynchronously(Main.ins);
					}
					
					if(e.getSlot() == 8) {
						e.getWhoClicked().closeInventory();
					}
					
					
				}
			}
		}
	}
	
	
	
}
