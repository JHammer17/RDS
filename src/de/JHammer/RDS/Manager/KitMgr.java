package de.JHammer.RDS.Manager;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import de.JHammer.RDS.Main;
import de.JHammer.RDS.Enums.GameState;
import de.JHammer.RDS.Enums.Kit;
import de.JHammer.RDS.Manager.SoundMgr.JSound;
import de.JHammer.RDS.Static.MySQLMgr.MySQLMgr;

public class KitMgr implements Listener {

	
	public static void openKitInv(Player p, Inventory inv) {
		boolean invGiven = true;
		if(inv == null) {
			invGiven = false;
			inv = Bukkit.createInventory(null, 9*4, "Kits");
		}
		
		
		ItemStack empty = Main.ins.utils.createItem(Material.STAINED_GLASS_PANE, 15, 1, "§a", null);
		ItemStack close = Main.ins.utils.createItem(Material.BARRIER, 0, 1, "§cMenü schließen", null);
		
		for(int slot = 0; slot <= 35; slot++) {
			inv.setItem(slot, empty);
		}
		final Inventory fInv = inv;
		for(Kit kit : Kit.values()) {
			
			new BukkitRunnable() {
				
				@Override
				public void run() {
					
					String addToLore = "";
					if(kit == Main.ins.getRDSPlayer(p).getKit()) {
						addToLore = "§a(Ausgewählt)";
					}
					
					
					ItemStack display = Main.ins.utils.createItem(
							kit.getDisplayItem(), 
							kit.getSubID(), 1, 
							kit.getDisplayName() + Main.ins.utils.formatBoolen(MySQLMgr.hasBuyed(p.getUniqueId(), kit), "§a", "§c", " (Gekauft)", " (Nicht gekauft)"), 
							kit.getDisplayLore() + "\n\n"+addToLore, 
							ItemFlag.HIDE_ATTRIBUTES, 
							ItemFlag.HIDE_DESTROYS, 
							ItemFlag.HIDE_ENCHANTS, 
							ItemFlag.HIDE_PLACED_ON, 
							ItemFlag.HIDE_POTION_EFFECTS, 
							ItemFlag.HIDE_UNBREAKABLE);
					
				
					if(Main.ins.getRDSPlayer(p).getKit() == kit) {
						display = Main.ins.utils.applyEnchant(display, Enchantment.ARROW_DAMAGE, 1);
					}
					
					fInv.setItem(kit.getSlot(), display);
					
					
				}
			}.runTaskAsynchronously(Main.ins);
			
			
		}
		
		inv.setItem(31, close);
		
		if(!invGiven) {
			p.openInventory(inv);
			Main.ins.utils.getSoundMgr().playSound(p, JSound.CHEST_OPEN, 100, (float) 0.5);
		}
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if(e.getInventory() != null && e.getClickedInventory() != null) {
			if(e.getInventory().getTitle().equalsIgnoreCase("Kits")) {
				e.setCancelled(true);
				if(e.getClickedInventory().getTitle().equalsIgnoreCase("Kits")) {
					
					
					if(e.getSlot() == 31) {
						e.getWhoClicked().closeInventory();
						return;
					}
					
					
					if(e.getWhoClicked() instanceof Player)  {
						Player p = (Player)e.getWhoClicked();
						
						if(Kit.getBySlot(e.getSlot()) != null) {
							
							
							new BukkitRunnable() {
								
								@Override
								public void run() {
									
									if(!MySQLMgr.hasBuyed(e.getWhoClicked().getUniqueId(), Kit.getBySlot(e.getSlot()))) {
										new BukkitRunnable() {
											
											@Override
											public void run() {
												KitBuyMgr.openKitBuyMgr((Player) e.getWhoClicked(), Kit.getBySlot(e.getSlot()));
												
											}
										}.runTask(Main.ins);
									} else {
										Main.ins.getRDSPlayer(p).setKit(Kit.getBySlot(e.getSlot()));
										p.closeInventory();
										p.sendMessage(Main.ins.prefix + "Du spielst nun mit dem Kit §6" + Main.ins.getRDSPlayer(p).getKit().getName());
										Main.ins.utils.getSoundMgr().playSound(p, JSound.LEVEL_UP, 100, (float) 0.5);
									}
									
								}
							}.runTaskAsynchronously(Main.ins);
							
							
//							
						}
					}
					
					
				}
			}
		}
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		if(Main.ins.state == GameState.LOBBY) {
			if(e.getItem() != null && 
					Main.ins.utils.compareItem(
							e.getItem(), 
							Main.ins.utils.getKitSelector(), 
							false)) {
				
				
				openKitInv(e.getPlayer(), null);
				e.setCancelled(true);
			}
		}
	}
	
	
	
	
	public static void giveKit(Player p, Kit kit) {
		
		
		Main.ins.utils.resetPlayer(p);
		if(kit == null) kit = Kit.STARTER;		
		switch (kit) {
		
		case SNIPER:
			ItemStack bow = Main.ins.utils.createItem(Material.BOW, 0, 1, null, null);
			
			bow = Main.ins.utils.makeUnbreakable(bow);
			
			Main.ins.utils.applyEnchant(bow, Enchantment.ARROW_KNOCKBACK, 2);
			Main.ins.utils.applyEnchant(bow, Enchantment.ARROW_FIRE, 1);
			Main.ins.utils.applyEnchant(bow, Enchantment.ARROW_INFINITE, 1);
			
			
			ItemStack woodSword = Main.ins.utils.createItem(Material.WOOD_SWORD, 0, 1, null, null);
			woodSword = Main.ins.utils.makeUnbreakable(woodSword);
			
			
			p.getInventory().setItem(0, bow);
			p.getInventory().setItem(1, woodSword);
			p.getInventory().setItem(8, Main.ins.utils.createItem(Material.ARROW, 0, 1, null, null));
			
			giveIronArmor(p);
			
			
			
			break;
		
		case WARRIOR:
			ItemStack diamondSword = Main.ins.utils.createItem(Material.DIAMOND_SWORD, 0, 1, null, null);
			
			diamondSword = Main.ins.utils.makeUnbreakable(diamondSword);
			
			p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20*6000, 0, true, true));
			
			p.getInventory().setItem(0, diamondSword);
			
			giveIronArmor(p);
			
			
			
			break;
		
		case MAGIC:
			ItemStack magicWand = Main.ins.utils.createItem(Material.BLAZE_ROD, 0, 1, "§5§lZauberstab", null);
			
			magicWand = Main.ins.utils.applyEnchant(magicWand, Enchantment.FIRE_ASPECT, 2);
			magicWand = Main.ins.utils.applyEnchant(magicWand, Enchantment.DAMAGE_ALL, 2);
			
			
			p.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20*6000, 0, true, true));
			p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 20*6000, 0, true, true));
			
			p.getInventory().setItem(0, magicWand);
			
			ItemStack helmet = Main.ins.utils.createItem(Material.GOLD_HELMET, 0, 1, null, null);
			helmet = Main.ins.utils.makeUnbreakable(helmet);
			
			ItemStack chest = Main.ins.utils.createItem(Material.GOLD_CHESTPLATE, 0, 1, null, null);
			chest = Main.ins.utils.makeUnbreakable(chest);
			
			ItemStack leggings = Main.ins.utils.createItem(Material.GOLD_LEGGINGS, 0, 1, null, null);
			leggings = Main.ins.utils.makeUnbreakable(leggings);
			
			ItemStack boots = Main.ins.utils.createItem(Material.GOLD_BOOTS, 0, 1, null, null);
			boots = Main.ins.utils.makeUnbreakable(boots);
			
			p.getInventory().setHelmet(helmet);
			p.getInventory().setChestplate(chest);
			p.getInventory().setLeggings(leggings);
			p.getInventory().setBoots(boots);
			
			
			
			break;
			
		case HEALER:
			ItemStack sword = Main.ins.utils.createItem(Material.GOLD_SWORD, 0, 1, null, null);
			
			ItemStack healPotion1 = Main.ins.utils.createItem(Material.POTION, 16421, 5, null, null);
			ItemStack regPotion1 = Main.ins.utils.createItem(Material.POTION, 16417, 2, null, null);
			
			sword = Main.ins.utils.makeUnbreakable(sword);
			
			
			
			p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*6000, 2, true, true));
			
			p.getInventory().setItem(0, sword);
			p.getInventory().setItem(1, healPotion1);
			p.getInventory().setItem(2, regPotion1);
			
			
			
			
			helmet = Main.ins.utils.createItem(Material.LEATHER_HELMET, 0, 1, null, null);
			helmet = Main.ins.utils.makeUnbreakable(helmet);
			
			chest = Main.ins.utils.createItem(Material.LEATHER_CHESTPLATE, 0, 1, null, null);
			chest = Main.ins.utils.makeUnbreakable(chest);
			
			leggings = Main.ins.utils.createItem(Material.LEATHER_LEGGINGS, 0, 1, null, null);
			leggings = Main.ins.utils.makeUnbreakable(leggings);
			
			boots = Main.ins.utils.createItem(Material.LEATHER_BOOTS, 0, 1, null, null);
			boots = Main.ins.utils.makeUnbreakable(boots);
			
			p.getInventory().setHelmet(helmet);
			p.getInventory().setChestplate(chest);
			p.getInventory().setLeggings(leggings);
			p.getInventory().setBoots(boots);
			
			
			
			break;
			
		case TANK:
			sword = Main.ins.utils.createItem(Material.IRON_SWORD, 0, 1, null, null);
			
			
			sword = Main.ins.utils.makeUnbreakable(sword);
			
			
			
			p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20*6000, 0, true, true));
			
			p.getInventory().setItem(0, sword);
			
			
			
			
			
			helmet = Main.ins.utils.createItem(Material.DIAMOND_HELMET, 0, 1, null, null);
			helmet = Main.ins.utils.makeUnbreakable(helmet);
			
			chest = Main.ins.utils.createItem(Material.DIAMOND_CHESTPLATE, 0, 1, null, null);
			chest = Main.ins.utils.makeUnbreakable(chest);
			
			leggings = Main.ins.utils.createItem(Material.DIAMOND_LEGGINGS, 0, 1, null, null);
			leggings = Main.ins.utils.makeUnbreakable(leggings);
			
			boots = Main.ins.utils.createItem(Material.DIAMOND_BOOTS, 0, 1, null, null);
			boots = Main.ins.utils.makeUnbreakable(boots);
			
			p.getInventory().setHelmet(helmet);
			p.getInventory().setChestplate(chest);
			p.getInventory().setLeggings(leggings);
			p.getInventory().setBoots(boots);
			
			
			
			break;
			
		default:
			
			ItemStack ironSword = Main.ins.utils.createItem(Material.IRON_SWORD, 0, 1, null, null);
			ironSword = Main.ins.utils.makeUnbreakable(ironSword);
			p.getInventory().setItem(0, ironSword);
			giveIronArmor(p);
			break;
		
		}
			
		p.updateInventory();
		
		
	}
	
	private static void giveIronArmor(Player p) {
		ItemStack helmet = Main.ins.utils.createItem(Material.IRON_HELMET, 0, 1, null, null);
		helmet = Main.ins.utils.makeUnbreakable(helmet);
		
		ItemStack chest = Main.ins.utils.createItem(Material.IRON_CHESTPLATE, 0, 1, null, null);
		chest = Main.ins.utils.makeUnbreakable(chest);
		
		ItemStack leggings = Main.ins.utils.createItem(Material.IRON_LEGGINGS, 0, 1, null, null);
		leggings = Main.ins.utils.makeUnbreakable(leggings);
		
		ItemStack boots = Main.ins.utils.createItem(Material.IRON_BOOTS, 0, 1, null, null);
		boots = Main.ins.utils.makeUnbreakable(boots);
		
		p.getInventory().setHelmet(helmet);
		p.getInventory().setChestplate(chest);
		p.getInventory().setLeggings(leggings);
		p.getInventory().setBoots(boots);
	}
	
	
	
	
}
