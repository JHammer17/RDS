package de.JHammer.RDS.Enums;

import org.bukkit.Material;

public enum Kit {

	
	STARTER("Starter", "", 0, Material.IRON_HELMET, 0, "§8«§6§lStarter§8»", "\n §fEffekte §8»\n§8- §7Keine \n\n §fAusrüstung §8»\n§8- §7Eisenrüstung \n§8- §7Eisenschwert \n \n §6Kosten: §e0 §lGens", 14),
	SNIPER("Scharfschütze", "Sniper", 300, Material.BOW, 0, "§8«§6§lScharfschütze§8»", "\n §fEffekte §8»\n§8- §7Visier \n\n §fAusrüstung §8»\n§8- §7Eisenrüstung \n§8- §7Bogen\n§8- §7Holzschwert \n \n §6Kosten: §e300 §lGens", 10),
	WARRIOR("Warrior", "Warrior", 150, Material.DIAMOND_SWORD, 0, "§8«§6§lWarrior§8»", " \n §fEffekte §8»\n§8- §7Stärke I \n\n §fAusrüstung §8»\n§8- §7Eisenrüstung \n§8- §7Diamantschwert \n \n §6Kosten: §e150 §lGens", 24),
	MAGIC("Magic", "Magic", 100, Material.BLAZE_ROD, 0, "§8«§6§lMagier§8»", "\n §fEffekte §8»\n§8- §7Nachtsicht \n§8- §7Feuerresistenz \n§8- §740% Chance 1 Herz zu stehlen \n \n §fAusrüstung §8»\n§8- §7Goldrüstung \n§8- §7Zauberstab \n \n §6Kosten: §e100 §lGens", 12),
	HEALER("Heiler", "Healer", 150, Material.POTION, 8261, "§8«§6§lHeiler§8»", "\n §fEffekte §8»\n§8- §7Speed III \n\n §fAusrüstung §8»\n§8- §7Lederrüstung \n§8- §7Goldschwert \n§8- §7Heiltränke \n \n §6Kosten: §e150 §lGens", 16),
	TANK("Tank", "Tank", 200, Material.DIAMOND_CHESTPLATE, 0, "§8«§6§lTank§8»", "\n §fEffekte §8»\n§8- §7Langsamkeit I \n\n §fAusrüstung §8»\n§8- §7Diamantrüstung \n§8- §7Diamantschwert \n \n §6Kosten: §e200 §lGens", 20);
	
	
	private String name;
	private String mName;
	private int price;
	private Material displayItem;
	private int itemSubID;
	private String displayName;
	private String displayLore;
	private int slot;
	
	
	
	Kit(String name, String mName, int price, Material displayItem, int itemSubID, String displayName, String displayLore, int slot) {
		this.name = name;
		this.mName = mName;
		this.price = price;
		this.displayItem = displayItem;
		this.itemSubID = itemSubID;
		this.displayName = displayName;
		this.displayLore = displayLore;
		this.slot = slot;
		
	}//
	
	
	public static Kit getBySlot(int slot) {
		for(Kit kits : Kit.values()) {
			if(kits.getSlot() == slot) return kits;
		}
		return null;
	}
	
	
	public String getName() {
		return name;
	}
	
	public String getMySQLName() {
		return mName;
	}
	
	public int getPrice() {
		return price;
	}
	
	public Material getDisplayItem() {
		return displayItem;
	}
	
	public int getSubID() {
		return itemSubID;
	}
	
	public String getDisplayName() {
		return this.displayName;
	}
	
	public String getDisplayLore() {
		return this.displayLore;
	}
	
	public int getSlot() {
		return this.slot;
	}
	
	
}
