package de.JHammer.RDS.Objects;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.spigotmc.AsyncCatcher;


import de.JHammer.RDS.Main;
import de.JHammer.RDS.Manager.SoundMgr;
import net.md_5.bungee.api.ChatColor;

public class Utils {

	private ItemStack mapVote;
	private ItemStack kitSelector;
	private ItemStack startItem;
	private ItemStack leaveItem;
	
	private Location mainSpawn = null;
	
	
	
	private SoundMgr sound = new SoundMgr();
	
	
	private Random r = new Random();
	
	
	public void reloadBasics() {
		mapVote = createItem(Material.NETHER_STAR, 0, 1, "§6Mapvoting", null);
		kitSelector = createItem(Material.CHEST, 0, 1, "§6Kits", null);
		startItem = createItem(Material.FEATHER, 0, 1, "§aSpiel starten", null);
		leaveItem = createItem(Material.WOOL, 14, 1, "§c➼ §r§7Zurück zur Lobby", null);
		
		
		
		YamlConfiguration cfg = getYaml("Spawns");
		
		double x = cfg.getDouble("Spawns.Lobby.X");
		int y = cfg.getInt("Spawns.Lobby.Y");
		double z = cfg.getDouble("Spawns.Lobby.Z");
		
		float yaw = (float) cfg.getDouble("Spawns.Lobby.Yaw");
		float pitch = (float) cfg.getDouble("Spawns.Lobby.Pitch");
		
		
		String world = cfg.getString("Spawns.Lobby.World");
		
		if(world != null && Bukkit.getWorld(world) != null) 
			mainSpawn = new Location(Bukkit.getWorld(world), (double)x,y+2,(double)z,yaw,pitch);
		
		
		
		
		
		
	}
	
	public ItemStack getVoteItem() {
		return mapVote;
	}
	
	public ItemStack getKitSelector() {
		return kitSelector;
	}
	
	public ItemStack getStartItme() {
		return startItem;
	}
	
	public ItemStack getLeaveItem() {
		return leaveItem;
	}
	
	
	
	public Location getLobbySpawn() {
		if(mainSpawn == null) return null;
		return mainSpawn.clone();
	}
	
	
	
	public void giveLobbyItems(Player p) {
		p.getInventory().clear();
		p.getInventory().setArmorContents(null);
		
		
		
		p.getInventory().setItem(0, mapVote);
		p.getInventory().setItem(4, kitSelector);
		if(p.hasPermission("RDS.start")) p.getInventory().setItem(6, startItem);
		p.getInventory().setItem(8, leaveItem);
	}
	
	

	
	
	public void sendTitle(Player player, Integer fadeIn, Integer stay, Integer fadeOut, String title, String subtitle) {

		new BukkitRunnable() {

			@Override
			public void run() {
				try {

					Object resetTitle = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("RESET")
							.get(null);
					Object resetChat = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0]
							.getMethod("a", String.class).invoke(null, "");

					Constructor<?> resetConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(
							getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0],
							getNMSClass("IChatBaseComponent"));
					Object rPacket = resetConstructor.newInstance(resetTitle, resetChat);
					sendPacket(player, rPacket);

					Object timesTitle = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TIMES")
							.get(null);
					Object timesChat = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0]
							.getMethod("a", String.class).invoke(null, "");

					Constructor<?> timesConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(
							getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0],
							getNMSClass("IChatBaseComponent"), int.class, int.class, int.class);
					Object timesPacket = timesConstructor.newInstance(timesTitle, timesChat, fadeIn, stay, fadeOut);
					sendPacket(player, timesPacket);

					Object enumTitle = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TITLE")
							.get(null);
					Object chat = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class)
							.invoke(null, "{\"text\":\"" + title + "\"}");

					Object SubenumTitle = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("SUBTITLE")
							.get(null);
					Object Subchat = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0]
							.getMethod("a", String.class).invoke(null, "{\"text\":\"" + subtitle + "\"}");

					Constructor<?> titleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(
							getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0],
							getNMSClass("IChatBaseComponent"), int.class, int.class, int.class);
					Object packet = titleConstructor.newInstance(enumTitle, chat, fadeIn, stay, fadeOut);

					Constructor<?> SubtitleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(
							getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0],
							getNMSClass("IChatBaseComponent"), int.class, int.class, int.class);
					Object Subpacket = SubtitleConstructor.newInstance(SubenumTitle, Subchat, fadeIn, stay, fadeOut);

					sendPacket(player, packet);
					sendPacket(player, Subpacket);
				}

				catch (Exception e1) {
					e1.printStackTrace();
				}

			}
		}.runTaskAsynchronously(Main.ins);

	}

	public void sendActionBar(Player p, String msg) {
		new BukkitRunnable() {

			@Override
			public void run() {
				if (getNMSVersion().startsWith("v1_12_")) {
					sendActionBar1_12(p, msg);
					return;
				}
				try {

					Object chat = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class)
							.invoke(null, "{\"text\":\"" + msg + "\"}");
					Constructor<?> titleConstructor = getNMSClass("PacketPlayOutChat")
							.getConstructor(getNMSClass("IChatBaseComponent"), byte.class);
					Object packet = titleConstructor.newInstance(chat, (byte) 2);
					sendPacket(p, packet);
				} catch (Exception e1) {
					e1.printStackTrace();
				}

			}
		}.runTaskAsynchronously(Main.ins);

	}

	public ItemStack createItem(Material mat, int SubID, int Amount, String Display, String Lore) {
		return createItem(mat, SubID, Amount, Display, Lore, null);

	}
	
	public ItemStack createItem(int id, int SubID, int Amount, String Display, String Lore) {
		return createItem(id, SubID, Amount, Display, Lore, null);

	}
	
	public ItemStack createItem(Material mat, int SubID, int Amount, String Display, String Lore, ItemFlag... flags) {
		ItemStack itemstack = new ItemStack(mat);
		if(mat == null || mat == Material.AIR) return itemstack;
		ItemMeta itemMeta = itemstack.getItemMeta();
		ArrayList<String> LoreList = new ArrayList<String>();
		if (Display != null) {
			itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', Display));
		}

		if (Lore != null) {
			String[] Lines = ChatColor.translateAlternateColorCodes('&', Lore).split("\n");
			for (int i = 0; i < Lines.length; i++) {
				LoreList.add(Lines[i]);
			}
		}
		
		
		

		itemMeta.setLore(LoreList);
		
		if(flags != null)for(ItemFlag flag : flags) itemMeta.addItemFlags(flag);
		
		itemstack.setItemMeta(itemMeta);
		itemstack.setAmount(Amount);
		itemstack.setDurability((short) SubID);
		
		return itemstack;

	}

	@SuppressWarnings("deprecation")
	public ItemStack createItem(int id, int SubID, int Amount, String Display, String Lore, ItemFlag... flags) {
		ItemStack itemstack = new ItemStack(id);
		ItemMeta itemMeta = itemstack.getItemMeta();
		ArrayList<String> LoreList = new ArrayList<String>();
		if (Display != null) {
			itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', Display));
		}

		if (Lore != null) {
			String[] Lines = ChatColor.translateAlternateColorCodes('&', Lore).split("\n");
			for (int i = 0; i < Lines.length; i++) {
				LoreList.add(Lines[i]);
			}
		}

		
		
		itemMeta.setLore(LoreList);
		
		if(flags != null)for(ItemFlag flag : flags) itemMeta.addItemFlags(flag);
		
		itemstack.setItemMeta(itemMeta);
		itemstack.setAmount(Amount);
		itemstack.setDurability((short) SubID);
		return itemstack;

	}
	
	public ItemStack makeUnbreakable(ItemStack stack) {
		
		if(stack != null)  {
			ItemMeta meta = stack.getItemMeta();
			meta.spigot().setUnbreakable(true);
			stack.setItemMeta(meta);
		}
		return stack;
		
	}
	
	

	public ItemStack applyEnchant(ItemStack stack, Enchantment ench, int level) {
		if (level <= 0) {
			stack.removeEnchantment(ench);
			return stack;
		}
		stack.addUnsafeEnchantment(ench, level);

		return stack;
	}

	public ItemStack applyEnchant(ItemStack stack, Enchantment ench, int level, int maxlvl) {
		if (level <= 0) {
			stack.removeEnchantment(ench);
			return stack;
		}
		if (level > maxlvl) {
			level = maxlvl;
		}
		stack.addUnsafeEnchantment(ench, level);

		return stack;
	}

	public int getEnchLevel(ItemStack stack, Enchantment ench) {
		if (stack.hasItemMeta()) {
			if (stack.getItemMeta().hasEnchants()) {
				if (stack.getItemMeta().hasEnchant(ench)) {
					return stack.getEnchantmentLevel(ench);
				}
			}
		}
		return 0;
	}

	private void sendActionBar1_12(Player player, String message) {

		try {
			Class<?> craftPlayerClass = Class
					.forName("org.bukkit.craftbukkit." + getNMSVersion() + ".entity.CraftPlayer");
			Object craftPlayer = craftPlayerClass.cast(player);

			Class<?> c4 = Class.forName("net.minecraft.server." + getNMSVersion() + ".PacketPlayOutChat");
			Class<?> c5 = Class.forName("net.minecraft.server." + getNMSVersion() + ".Packet");
			Class<?> c2 = Class.forName("net.minecraft.server." + getNMSVersion() + ".ChatComponentText");
			Class<?> c3 = Class.forName("net.minecraft.server." + getNMSVersion() + ".IChatBaseComponent");
			Class<?> chatMessageTypeClass = Class
					.forName("net.minecraft.server." + getNMSVersion() + ".ChatMessageType");
			Object[] chatMessageTypes = chatMessageTypeClass.getEnumConstants();
			Object chatMessageType = null;
			for (Object obj : chatMessageTypes)
				if (obj.toString().equals("GAME_INFO"))
					chatMessageType = obj;

			Object o = c2.getConstructor(new Class[] { String.class }).newInstance(new Object[] { message });
			Object ppoc = c4.getConstructor(new Class[] { c3, chatMessageTypeClass })
					.newInstance(new Object[] { o, chatMessageType });
			Method m1 = craftPlayerClass.getDeclaredMethod("getHandle", new Class[0]);
			Object h = m1.invoke(craftPlayer, new Object[0]);
			Field f1 = h.getClass().getDeclaredField("playerConnection");
			Object pc = f1.get(h);
			Method m5 = pc.getClass().getDeclaredMethod("sendPacket", new Class[] { c5 });
			m5.invoke(pc, new Object[] { ppoc });
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void sendPacket(Player player, Object packet) {
		try {
			Object handle = player.getClass().getMethod("getHandle").invoke(player);
			Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
			playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Class<?> getNMSClass(String name) {
		String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
		try {
			return Class.forName("net.minecraft.server." + version + "." + name);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			
			return null;
		}
	}

	private String getNMSVersion() {
		return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
	}

	public YamlConfiguration defaultYML() {
		if (defaultFile() != null) {
			YamlConfiguration cfg = YamlConfiguration.loadConfiguration(defaultFile());
			return cfg;
		} else
			return null;
	}

	public File defaultFile() {
		
		File file = new File("plugins/RDS/config.yml");
		File dir = new File("plugins/RDS");
		
		if (!file.exists()) {
			
			try {
				dir.mkdirs();
				file.createNewFile();
				YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);

				file = new File("plugins/RDS/config.yml");
				cfg = YamlConfiguration.loadConfiguration(file);

				generateDefaultFileConfig(false);

				cfg.save(file);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}


		return file;
	}

	public void generateDefaultFileConfig(boolean reset) {
		
		if (!reset) {
			File file = new File("plugins/RDS/config.yml");
			if (!file.exists())
				Main.ins.saveResource("config.yml", reset);

		} else
			Main.ins.saveResource("config.yml", reset);
	}
	
	public File getPluginFile(String Pfad) {
		File file = new File("plugins/RDS/" + Pfad + ".yml");
		if (!file.exists() || file == null) {
			try {
				YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
				cfg.save(file);
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
		return file;
	}

	public boolean existFile(String Pfad) {
		File file = new File("plugins/RDS/" + Pfad + ".yml");
		try {
			if (!file.exists() || file == null)
				return false;
		} catch (Exception e) {
			return false;
		}

		return true;
	}

	public YamlConfiguration getYaml(String Pfad) {
		if (getPluginFile(Pfad) != null) {
			YamlConfiguration cfg = YamlConfiguration.loadConfiguration(getPluginFile(Pfad));
			return cfg;
		}
		return null;
	}
	
	
	
	
	
	public boolean checkRegion(Location checkLoc, Location loc1, Location loc2) {
		
		if(loc1 == null || 
				loc2 == null || 
				loc1.getWorld() == null || 
				loc2.getWorld() == null || 
				checkLoc == null || 
				checkLoc.getWorld() == null) return false;
		
		Location min = getMinLoc(loc1, loc2);
		Location max = getMaxLoc(loc1, loc2);
		
	    if(min == null || max == null) return false;
	    
	   
	    
	    if(min.getWorld() == null || max.getWorld() == null) return false;
	    
	    
        if(min.getBlockY() > checkLoc.getBlockY() ||
           max.getBlockY() < checkLoc.getBlockY()) {
        	return false;
        }
        if (!checkLoc.getWorld().getName().equalsIgnoreCase(min.getWorld().getName())) {
            return false;
        }
        int hx = checkLoc.getBlockX();
        int hz = checkLoc.getBlockZ();
        if (hx < min.getBlockX()) return false;
        if (hx > max.getBlockX()) return false;
        if (hz < min.getBlockZ()) return false;
        if (hz > max.getBlockZ()) return false;
        return true;
    
	}
	
	public Location getMinLoc(Location loc1, Location loc2) {
		
		if(loc1 == null || 
		   loc2 == null || 
		   loc1.getWorld() == null || 
		   loc2.getWorld() == null) return null;
		
		int minX = Math.min(loc1.getBlockX(), loc2.getBlockX());
		int minY = Math.min(loc1.getBlockY(), loc2.getBlockY());
		int minZ = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
		
		if(!loc1.getWorld().getName().equals(loc2.getWorld().getName())) return null;
		
		return new Location(loc1.getWorld(), minX, minY, minZ);
	}
	
	public Location getMaxLoc(Location loc1, Location loc2) {
		
		if(loc1 == null || 
		   loc2 == null|| 
		   loc1.getWorld() == null || 
		   loc2.getWorld() == null) return null;
		
		int maxX = Math.max(loc1.getBlockX(), loc2.getBlockX());
		int maxY = Math.max(loc1.getBlockY(), loc2.getBlockY());
		int maxZ = Math.max(loc1.getBlockZ(), loc2.getBlockZ());
		
		if(!loc1.getWorld().getName().equals(loc2.getWorld().getName())) return null;
		
		return new Location(loc1.getWorld(), maxX, maxY, maxZ);
	}
	
	
	
	public SoundMgr getSoundMgr() {
		return sound;
	}
	
	
	
	public String getUnknownSkinTexture() {
		return "eyJ0aW1lc3RhbXAiOjE1MjA5Njg1NjM2ODcsInByb2ZpbGVJZCI6IjYwNmUyZmYwZWQ3NzQ4NDI5ZDZjZTFkMzMyMWM3ODM4IiwicHJvZmlsZU5hbWUiOiJNSEZfUXVlc3Rpb24iLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzUxNjNkYWZhYzFkOTFhOGM5MWRiNTc2Y2FhYzc4NDMzNjc5MWE2ZTE4ZDhmN2Y2Mjc3OGZjNDdiZjE0NmI2In19fQ====";
	}
	
	public boolean isMainThread() {
		try {
			AsyncCatcher.catchOp("Test");
			return true;
		} catch (Exception e) {
			return false;
		}
		
	}
	
	
	public boolean isMultiverseInstalled() {
		return Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core") != null;
	}
	
	
	public String formatBoolean(boolean b) {
		return b == true ? "§aJa" : "§cNein";
	}
	
	public String formatBoolen(boolean b, String colorCodeYes, String colorCodeNo) {
		return b == true ? colorCodeYes + "Ja" : colorCodeNo + "Nein";
	}
	
	public String formatBoolen(boolean b, String colorCodeYes, String colorCodeNo, String bTrue, String bFalse) {
		return b == true ? colorCodeYes + bTrue : colorCodeNo + bFalse;
	}
	
	
	public ItemStack createPotion(Material material, int subId, int amount, String displayName, String lore, boolean hideEffects, PotionEffect... effects) {
		
		ItemStack potion = createItem(material, subId, amount, displayName, lore);
		PotionMeta meta = (PotionMeta) potion.getItemMeta();
		
		if(effects == null) {
			meta.clearCustomEffects();
		} else {
			for(PotionEffect effect : effects) 
				meta.addCustomEffect(effect, true);
			
		}
		
		if(hideEffects) meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
		
		potion.setItemMeta(meta);
		
		return potion;
	}
	
	public String getFormattedTime(int secs) {
		int mins = 0;
		
		while(secs >= 60) {
			secs -= 60;
			mins++;
		}
		
		if(secs < 10) return mins + ":0" + secs; 
		 else return mins + ":" + secs;
	}
	
	public boolean compareItems(ItemStack item1, ItemStack item2) {
		return compareItem(item1, item2, false);
	}
	
	public boolean compareItem(ItemStack item1, ItemStack item2, boolean compareAmount) {
		if(item1 == null || item2 == null) return false;
		if(item1.getType() != item2.getType()) return false;
		if(item1.hasItemMeta() != item2.hasItemMeta()) return false;
		if(compareAmount && (item1.getAmount() != item2.getAmount())) return false;
		if(item1.getDurability() != item2.getDurability()) return false;
		if(!item1.getItemMeta().getDisplayName().equals(item2.getItemMeta().getDisplayName())) return false;
		if(item1.getItemMeta().hasEnchants() != item2.getItemMeta().hasEnchants()) return false;
		if(item1.getItemMeta().hasLore() && item2.getItemMeta().hasLore() && !item1.getItemMeta().getLore().containsAll(item2.getItemMeta().getLore())) return false;
		return true;
	}
	
	

	
	public Random getRandom() {
		return r;
	}
	
	
	public void setToLobby(Player p) {
		if(getLobbySpawn() != null) 
			p.teleport(getLobbySpawn());
		resetPlayer(p);
		giveLobbyItems(p);
		
	}
	
	@SuppressWarnings("deprecation")
	public void resetPlayer(Player p) {
		p.resetMaxHealth();
		p.setHealth(20);
		p.setMaxHealth(20);
		p.setFoodLevel(20);
		p.setAllowFlight(false);
		p.setFlying(false);
		p.setFlySpeed(0.1f);
		p.setWalkSpeed(0.2f);
		p.setExp(0);
		p.setLevel(0);
		p.resetTitle();
		p.setFireTicks(0);
		p.setGameMode(GameMode.ADVENTURE);
		
		p.getInventory().setArmorContents(null);
		p.getInventory().clear();
		
		for(Player a : Bukkit.getOnlinePlayers()) {
			for(Player b : Bukkit.getOnlinePlayers()) {
				a.showPlayer(b);
				b.showPlayer(a);
			}
		}
		
		
		for(PotionEffect effect : p.getActivePotionEffects()) {
			p.removePotionEffect(effect.getType());
		}
	}
	
	
	public void teleportAllPlayersToLobby() {
		for(Player players : Bukkit.getOnlinePlayers()) {
			setToLobby(players);
		}
	}
	
	public void setAsSpectator(Player p) {
		
		resetPlayer(p);
		Main.ins.getRDSPlayer(p).setSpecator(true);
		for(Player players : Bukkit.getOnlinePlayers()) {
			if(Main.ins.getRDSPlayer(players).isSpectator()) {
				for(Player player: Bukkit.getOnlinePlayers()) {
					if(player.getUniqueId() != players.getUniqueId()) player.hidePlayer(players);
				}
			}
		}
		p.setGameMode(GameMode.ADVENTURE);
		p.setAllowFlight(true);
		
		p.spigot().setCollidesWithEntities(false);
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				p.getInventory().setItem(0, Main.ins.utils.createItem(Material.COMPASS, 0, 1, " §8« §3Kompass §8»", null));
				p.getInventory().setItem(8, Main.ins.utils.createItem(Material.PAPER, 0, 1, "§cZurück in die Lobby §8§l➜", null));
				p.updateInventory();
			}
		}.runTaskLater(Main.ins, 10);
	}
	
	public ArrayList<Player> getAlivePlayers() {
		ArrayList<Player> alive = new ArrayList<>();
		for(Player players : Bukkit.getOnlinePlayers()) {
			if(!Main.ins.getRDSPlayer(players).isSpectator()) alive.add(players);
		}
		
		return alive;
	}
	
	public String colorByPercent(int value, int max, final String msg, String defaultColor, String colorColor) {
		
		if(value == max) return new String(colorColor + msg);
		int ln = new String(msg).length();
		String[] split = new String(msg).split("");
		
		double percent =  (((double)value/(double)max)*100.0);
		
		double toColor = ((double)ln/(double)100)*percent;
		
		
		if(toColor >= split.length) return new String(colorColor + msg);
		
		split[(int) toColor] = defaultColor + split[(int) toColor];
		
		boolean first = true;
		StringBuilder builder = new StringBuilder();
		for(String str : split) {
			if(first) {
				builder.append(colorColor);
				first = false;
			}
			builder.append(str);
		}
		return builder.toString();
	}
	
	
}
