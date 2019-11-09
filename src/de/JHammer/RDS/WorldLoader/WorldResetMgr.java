/**
 * 
 */
package de.JHammer.RDS.WorldLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.io.Files;

import de.JHammer.RDS.Main;
import de.JHammer.RDS.Manager.MapManager;


/**
 * ###################################################### # @author JHammer17 #
 * # Erstellt am 20.04.2019 17:44:29 # # # # Alle Ihhalte dieser Klasse dürfen #
 * # frei verwendet oder verbreitet werden. # # Es wird keine Zustimmung von
 * JHammer17 benötigt. # # #
 * ######################################################
 */
public class WorldResetMgr {
	
	public static void startReset(World w, String presetLocation, boolean placeBlockers, String map) {
		startReset(w.getName(), presetLocation, placeBlockers, map);
	}
	
	public static void unloadWorldArena(String worldName) {
		if(worldName == null) return;
		
			
				if (Bukkit.getWorld(worldName) != null && 
						Bukkit.getWorld(worldName).getPlayers().size() > 0) {
			
					for (Player players : Bukkit.getWorld(worldName).getPlayers()) {
						players.teleport(Main.ins.utils.getLobbySpawn());

					}
				}
				
				new BukkitRunnable() {
					
					@Override
					public void run() {
						
						Bukkit.unloadWorld("RDS/Arenas/" + worldName, true);
						
						deleteAllFiles("RDS/Arenas/" + worldName);
						
					}
				}.runTask(Main.ins);
				
				
			
		
	}
	
	
	
	public static void startReset(String worldName, String presetLocation, boolean placeBlockers, String map) {
		if (worldName == null || 
			worldName.equalsIgnoreCase("null") || 
			presetLocation == null) {
			return;
		}
		
		new BukkitRunnable() {

			@Override
			public void run() {
				
				if (Bukkit.getWorld(worldName) != null && 
						Bukkit.getWorld(worldName).getPlayers().size() > 0) {
			
					for (Player players : Bukkit.getWorld(worldName).getPlayers()) {
						players.teleport(Main.ins.utils.getLobbySpawn());
						
					}
				}

				reset(worldName, presetLocation, placeBlockers, map);
			}
		}.runTask(Main.ins);
	}
	
	
	private static void reset(String worldName, String presetLocation, boolean placeBlockers, String map) {
		Bukkit.unloadWorld(worldName, false);

		new BukkitRunnable() {

			@Override
			public void run() {
				
				deleteAllFiles(worldName);
				
				new BukkitRunnable() {
					
					@Override
					public void run() {
						copyWorldFolder(new File(presetLocation), new File(worldName));
						
						new BukkitRunnable() {
							
							@Override
							public void run() {
								
								
								Bukkit.createWorld(new WorldCreator(worldName));
								
//								AsyncWorldLoader.createAsyncWorld(new WorldCreator(worldName));
//								
								World w = Bukkit.getWorld(worldName);
								
								new BukkitRunnable() {

									@Override
									public void run() {
										if(w == null || !w.getName().equalsIgnoreCase(worldName)) {
											reset(worldName, presetLocation, placeBlockers, map);
											
											return;
										}
										
										
										
										
										w.setGameRuleValue("doDaylightCycle", "false");
										w.setGameRuleValue("doMobSpawning", "false");
										w.setGameRuleValue("doWeatherCycle", "false");
										w.setGameRuleValue("spectatorsGenerateChunks", "false");
										w.setTime(6000);

										for (Entity e : w.getEntities()) 
											if (!(e instanceof Player)) e.remove();

										if(placeBlockers) MapManager.initBlockers(map);
										
										
										
										
									}
								}.runTask(Main.ins);
							}
						}.runTaskLaterAsynchronously(Main.ins, 5);
						
						
					}
				}.runTaskLaterAsynchronously(Main.ins, 15);
				
				
				
				

			}
		}.runTaskLaterAsynchronously(Main.ins, 15);
	}
	

	@SuppressWarnings("static-access")
	public static void createNewWorldLayout(String name, Player creator, World w) {
		
		
			
			File f = new File("RDS/Presets/" + name + "/data.yml");
			if (!f.exists()) {
				try {
					new File("RDS/Presets/" + name).mkdirs();
					f.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			YamlConfiguration cfg = new YamlConfiguration().loadConfiguration(f);

			cfg.set("Data.MapName", name);

			try {
				cfg.save(f);
			} catch (IOException e) {
				e.printStackTrace();
			}

			w.setGameRuleValue("doDaylightCycle", "false");
			w.setGameRuleValue("doMobSpawning", "false");
			w.setGameRuleValue("doWeatherCycle", "false");
			w.setGameRuleValue("spectatorsGenerateChunks", "false");
			w.setTime(6000);

			for (Entity e : w.getEntities())
				if (!(e instanceof Player))
					e.remove();
			
			
			copyWorldFolder(new File(w.getName()), new File("RDS/Presets/" + name));
			
			creator.sendMessage(Main.ins.prefix + "§aLayout erstellt!");

		
	}

	

	private static void deleteAllFiles(String path) {
		if (!new File(path).exists() && !new File(path).isDirectory())
			return;

		for (File file : new File(path).listFiles()) {
			if (file.isDirectory())
				deleteAllFiles(file.getPath());
			file.delete();
		}

		new File(path).delete();
	}

	private static void copyWorldFolder(File from, File to) {
		try {
			ArrayList<String> ignore = new ArrayList<String>();
			ignore.add("session.dat");
			ignore.add("uid.dat");
			ignore.add("session.lock");
			ignore.add("data.yml");
			if (!ignore.contains(from.getName())) {
				if (from.isDirectory()) {
					if (!to.exists()) {
						to.mkdirs();
					}
					String[] files = from.list();
					for (String file : files) {
						File srcFile = new File(from, file);
						File destFile = new File(to, file);
						copyWorldFolder(srcFile, destFile);
					}
				} else {
					Files.copy(from, to);
				}
			}
		} catch (FileNotFoundException e) {
//			Bukkit.broadcastMessage("§4File Fehler! " + from + " " + to);
			copyWorldFolder(from, to, 0);
			return;
		} catch (Exception e) {
			copyWorldFolder(from, to, 0);
//			e.printStackTrace();
		}
	}
	
	private static void copyWorldFolder(File from, File to, int chance) {
		try {
			ArrayList<String> ignore = new ArrayList<String>();
			ignore.add("session.dat");
			ignore.add("uid.dat");
			ignore.add("session.lock");
			ignore.add("data.yml");
			ignore.add("Village.data");
			if (!ignore.contains(from.getName())) {
				if (from.isDirectory()) {
					if (!to.exists()) {
						to.mkdirs();
					}
					String[] files = from.list();
					for (String file : files) {
						File srcFile = new File(from, file);
						File destFile = new File(to, file);
						copyWorldFolder(srcFile, destFile);
					}
				} else {
					to.mkdirs();
					to.createNewFile();
					
					Files.copy(
							from, 
							to);
				}
			}
		} catch (FileNotFoundException e) {
			
			if(chance > 5) {
				
				return;
			}
			e.printStackTrace();
			
			copyWorldFolder(from, to, chance+=1);
			return;
		} catch (Exception e) {
			e.printStackTrace();
			
			
			
		}
	}
	
	

}
