package de.JHammer.RDS.Manager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.scheduler.BukkitRunnable;


import de.JHammer.RDS.Main;
import de.JHammer.RDS.Objects.Boss;
import de.JHammer.RDS.Objects.BlockerMgr;
import de.JHammer.RDS.Objects.RescueSheep;
import de.JHammer.RDS.WorldLoader.WorldResetMgr;

public class MapManager {

	
	
	public static boolean isMapExists(String name) {
		if(name == null) return false;
		return Main.ins.utils.getYaml("Maps").getConfigurationSection("Maps." + name) != null;
	}
	
	public static ArrayList<String> getAllMaps(boolean ignoreEnabled) {
		ArrayList<String> maps = new ArrayList<>();
		
		YamlConfiguration cfg = Main.ins.utils.getYaml("Maps");
		
		if(cfg.getConfigurationSection("Maps") != null) 
			for(String str : cfg.getConfigurationSection("Maps").getKeys(false)) {
				
				if(ignoreEnabled) 
					maps.add(str);		
				 else 
					if(isMapEnabled(str)) maps.add(str);
					
			}
		return maps;
	}
	
	public static boolean isMapEnabled(String name) {
		if(isMapExists(name)) 
			return Main.ins.utils.getYaml("Maps").getBoolean("Maps." + name + ".Enabled");
		
		return false;
	}
	
	public static void setMapEnabled(String name, boolean enabled) {
		if(isMapExists(name)) {
			YamlConfiguration cfg = Main.ins.utils.getYaml("Maps");
			
			cfg.set("Maps." + name + ".Enabled", false);
			
			try {
				cfg.save(Main.ins.utils.getPluginFile("Maps"));
			} catch (IOException e) {
				
				e.printStackTrace();
			}
			
		}
	}
	
	public static boolean isMapOkay(String name) {
		
		if(isMapExists(name)) {
			if(isMapEnabled(name)) {
				
				if(getSpawn(name) != null) {
					
					if(!getAllSpawns(name, 1).isEmpty() &&
							!getAllSpawns(name, 2).isEmpty() &&
							!getAllSpawns(name, 3).isEmpty() &&
							!getAllSpawns(name, 4).isEmpty() &&
							!getAllSpawns(name, 5).isEmpty()) {
						if(getSheepSpawn(name) != null) {
							if(getBossRoom(name, false) != null) {
								if(getBossSpawn(name) != null) {
									
									if(getDestroyStand(name, 0) != null &&
											getDestroyStand(name, 1) != null &&
											getDestroyStand(name, 2) != null &&
											getDestroyStand(name, 3) != null &&
											getDestroyStand(name, 4) != null &&
											getDestroyStand(name, 5) != null &&
											getDestroyStand(name, 6) != null) {
										
										if(getBlocker(name, 0, true) != null &&
										   getBlocker(name, 1, true) != null &&
										   getBlocker(name, 2, true) != null &&
										   getBlocker(name, 3, true) != null &&
										   getBlocker(name, 4, true) != null &&
										   getBlocker(name, 5, true) != null &&
										   getBlocker(name, 6, true) != null) {
											
											
											return true;
											
											
										}
										
										
									}
									
									
								}
							}
						}
					}
					
					
					
				}
				
				
			}
			
		}
		
		return false;
	}
	
	public static void addMap(String name) {
		if(!isMapExists(name)) {
			YamlConfiguration cfg = Main.ins.utils.getYaml("Maps");
			cfg.set("Maps." + name + ".Enabled", true);
			try {
				cfg.save(Main.ins.utils.getPluginFile("Maps"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void removeMap(String name) {
		if(isMapExists(name)) {
			YamlConfiguration cfg = Main.ins.utils.getYaml("Maps");
			cfg.set("Maps." + name, null);
			try {
				cfg.save(Main.ins.utils.getPluginFile("Maps"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void setWorld(String name, World w) {
		if(isMapExists(name) && w != null && Bukkit.getWorld(w.getName()) != null) {
			YamlConfiguration cfg = Main.ins.utils.getYaml("Maps");
			cfg.set("Maps." + name + ".World", w.getName());
			
			try {
				cfg.save(Main.ins.utils.getPluginFile("Maps"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	public static void setSpawn(String name, Location loc) {
		if(name != null && loc != null) {
			YamlConfiguration cfg = Main.ins.utils.getYaml("Maps");
			
			cfg.set("Maps." + name + ".Spawn.X", ((double)loc.getBlockX()+0.5));
			cfg.set("Maps." + name + ".Spawn.Y", (loc.getBlockY()+3));
			cfg.set("Maps." + name + ".Spawn.Z", ((double)loc.getBlockZ())+0.5);
			
			cfg.set("Maps." + name + ".Spawn.Yaw", loc.getYaw());
			cfg.set("Maps." + name + ".Spawn.Pitch", loc.getPitch());
			
			try {
				cfg.save(Main.ins.utils.getPluginFile("Maps"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static Location getSpawn(String name) {
		if(name != null && isMapExists(name)) {
			YamlConfiguration cfg = Main.ins.utils.getYaml("Maps");
			
			if(cfg.getConfigurationSection("Maps." + name + ".Spawn") != null) {
				double x = cfg.getDouble("Maps." + name + ".Spawn.X");
				double y = cfg.getDouble("Maps." + name + ".Spawn.Y");
				double z = cfg.getDouble("Maps." + name + ".Spawn.Z");
				
				double yaw = cfg.getDouble("Maps." + name + ".Spawn.Yaw");
				double pitch = cfg.getDouble("Maps." + name + ".Spawn.Pitch");
			
				return new Location(null, x, y, z, (float)yaw, (float)pitch);
			}
		}
		return null;
	}
	
	public static void setWayBlocker(String name, Location pos1, Location pos2, int number) {
		if(name != null && pos1 != null && pos2 != null) {
			YamlConfiguration cfg = Main.ins.utils.getYaml("Maps");
			
			Location min = Main.ins.utils.getMinLoc(pos1, pos2);
			Location max = Main.ins.utils.getMaxLoc(pos1, pos2);
			
			
			cfg.set("Maps." + name + ".WayBlocker." + number +".MaxX", max.getBlockX());
			cfg.set("Maps." + name + ".WayBlocker." + number +".MaxY", max.getBlockY());
			cfg.set("Maps." + name + ".WayBlocker." + number +".MaxZ", max.getBlockZ());
			
			cfg.set("Maps." + name + ".WayBlocker." + number +".MinX", min.getBlockX());
			cfg.set("Maps." + name + ".WayBlocker." + number +".MinY", min.getBlockY());
			cfg.set("Maps." + name + ".WayBlocker." + number +".MinZ", min.getBlockZ());
			
			
			try {
				cfg.save(Main.ins.utils.getPluginFile("Maps"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void addMobSpawn(String name, Location pos, int wave) {
		if(name != null && pos != null) {
			YamlConfiguration cfg = Main.ins.utils.getYaml("Maps");
			
			int number = cfg.getInt("Maps." + name + ".Mobspawns." + wave + ".Number");
			
			
			
			cfg.set("Maps." + name + ".Mobspawns." + wave + "." + number + ".X", pos.getBlockX());
			cfg.set("Maps." + name + ".Mobspawns." + wave + "." + number + ".Y", pos.getBlockY());
			cfg.set("Maps." + name + ".Mobspawns." + wave + "." + number + ".Z", pos.getBlockZ());
			
			cfg.set("Maps." + name + ".Mobspawns." + wave + ".Number" , number+=1);
			
			try {
				cfg.save(Main.ins.utils.getPluginFile("Maps"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static ArrayList<Location> getAllSpawns(String map, int wave) {
		ArrayList<Location> locs = new ArrayList<>();
		if(map != null && isMapExists(map)) {
			YamlConfiguration cfg = Main.ins.utils.getYaml("Maps");
			
			if(cfg.getConfigurationSection("Maps." + map + ".Mobspawns." + wave) != null) {
				for(String str : cfg.getConfigurationSection("Maps." + map + ".Mobspawns." + wave).getKeys(false)) {
					if(!str.equalsIgnoreCase("Number")) {
						
						int x = cfg.getInt("Maps." + map + ".Mobspawns." + wave + "." + str + ".X");
						int y = cfg.getInt("Maps." + map + ".Mobspawns." + wave + "." + str + ".Y");
						int z = cfg.getInt("Maps." + map + ".Mobspawns." + wave + "." + str + ".Z");
						
						locs.add(new Location(null, x, y, z));
						
					}
				}
			}
			
		}
		
		return locs;
	}
	
	
	
	public static void setDestroyStand(String name, Location loc, int number) {
		if(name != null && loc != null) {
			YamlConfiguration cfg = Main.ins.utils.getYaml("Maps");
			
			
			
			cfg.set("Maps." + name + ".DS." + number + ".StandX", ((double)loc.getBlockX()+0.5));
			cfg.set("Maps." + name + ".DS." + number +".StandY", loc.getBlockY());
			cfg.set("Maps." + name + ".DS." + number +".StandZ", ((double)loc.getBlockZ()+0.5));
			
			try {
				cfg.save(Main.ins.utils.getPluginFile("Maps"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void loadMap(String name, boolean placeBlockers) {
		if(isMapSaved(name)) {
			WorldResetMgr.startReset("Arena", "RDS/Presets/" + name, placeBlockers, name);
			
		}
	}
	
	public static boolean isMapSaved(String name) {
		return new File("RDS/Presets/" + name).exists();
	}
	
	public static Location getBlocker(String map, int number, boolean min) {
		
		if(map != null && isMapExists(map)) {
			YamlConfiguration cfg = Main.ins.utils.getYaml("Maps");
			
			if(min) {
				if(cfg.getConfigurationSection("Maps." + map + ".WayBlocker." + number) != null) {
					int minX = cfg.getInt("Maps." + map + ".WayBlocker." + number + ".MinX");
					int minY = cfg.getInt("Maps." + map + ".WayBlocker." + number + ".MinY");
					int minZ = cfg.getInt("Maps." + map + ".WayBlocker." + number + ".MinZ");
					return new Location(null, minX, minY, minZ);
				}
			} else {
				if(cfg.getConfigurationSection("Maps." + map + ".WayBlocker." + number) != null) {
					int maxX = cfg.getInt("Maps." + map + ".WayBlocker." + number + ".MaxX");
					int maxY = cfg.getInt("Maps." + map + ".WayBlocker." + number + ".MaxY");
					int maxZ = cfg.getInt("Maps." + map + ".WayBlocker." + number + ".MaxZ");
					return new Location(null, maxX, maxY, maxZ);
				}
			}
			
			
		}
		return null;
	}
	
	public static Location getDestroyStand(String map, int number) {
		
		if(map != null && isMapExists(map)) {
			YamlConfiguration cfg = Main.ins.utils.getYaml("Maps");
			
				if(cfg.getConfigurationSection("Maps." + map + ".DS." + number) != null) {
					double x = cfg.getDouble("Maps." + map + ".DS." + number + ".StandX");
					double y = cfg.getDouble("Maps." + map + ".DS." + number + ".StandY");
					double z = cfg.getDouble("Maps." + map + ".DS." + number + ".StandZ");
					return new Location(null, x, y, z);
				}
			
			
			
		}
		return null;
	}
	
	public static void initBlockers(String map) {
		World w = Bukkit.getWorld("Arena");
		
		for(int i = 0; i <= 6; i++) {
			Location maxLoc = getBlocker(map, i, false);
			Location minLoc = getBlocker(map, i, true);
			Location standLoc = getDestroyStand(map, i);
			
			
			
			
			if(maxLoc != null && minLoc != null && standLoc != null) {
				
				if(Bukkit.getWorld("Arena") != null) {
					maxLoc.setWorld(Bukkit.getWorld("Arena"));
					minLoc.setWorld(Bukkit.getWorld("Arena"));
					standLoc.setWorld(Bukkit.getWorld("Arena"));
				}
				
				
				final int I = i;
				
				new BukkitRunnable() {
					
					@Override
					public void run() {
						BlockerMgr mgr = new BlockerMgr(minLoc, maxLoc, w, standLoc, I);
						
						if(I != 6) mgr.fill();
						
						
						Main.ins.entryMgrs.put(I,  mgr);
					}
				}.runTask(Main.ins);
			}
		}
	}
	
	
	
	public static void tpToArena(Player player, String map) {
		if(map == null || !isMapExists(map) || Bukkit.getWorld("Arena") == null) return;
		Location spawn = getSpawn(map);
		spawn.setWorld(Bukkit.getWorld("Arena"));
		
		player.teleport(spawn);
	}
	
	public static ArrayList<Entity> living = new ArrayList<>();
	
	public static void spawnMobs(String map, int wave) {
		if(wave == 1) {
			//5 Zombie 5 Skellete 5 Spinnen
			
			ArrayList<EntityType> en = new ArrayList<>();
			
			en.add(EntityType.ZOMBIE);
			en.add(EntityType.ZOMBIE);
			en.add(EntityType.ZOMBIE);
			en.add(EntityType.ZOMBIE);
			en.add(EntityType.ZOMBIE);
			
			en.add(EntityType.SKELETON);
			en.add(EntityType.SKELETON);
			en.add(EntityType.SKELETON);
			en.add(EntityType.SKELETON);
			en.add(EntityType.SKELETON);
			
			en.add(EntityType.SPIDER);
			en.add(EntityType.SPIDER);
			en.add(EntityType.SPIDER);
			en.add(EntityType.SPIDER);
			en.add(EntityType.SPIDER);
			
			ArrayList<Location> spawns = getAllSpawns(map, wave);
			
			while(!en.isEmpty()) {
				
				EntityType entity = en.get(Main.ins.utils.getRandom().nextInt(en.size()));
				Location spawn = spawns.get(Main.ins.utils.getRandom().nextInt(spawns.size()));
				
				if(Bukkit.getWorld("Arena") == null) return;
				
				spawn.setWorld(Bukkit.getWorld("Arena"));
				
				Entity spawned = spawn.getWorld().spawnEntity(spawn, entity);
				living.add(spawned);
				
				en.remove(entity);
			}
			
			
		} else if(wave == 2) {
			//10 Zombies, 5 Hexen und 5 Höhlenspinnen
			
			ArrayList<EntityType> en = new ArrayList<>();
			
			en.add(EntityType.ZOMBIE);
			en.add(EntityType.ZOMBIE);
			en.add(EntityType.ZOMBIE);
			en.add(EntityType.ZOMBIE);
			en.add(EntityType.ZOMBIE);
			en.add(EntityType.ZOMBIE);
			en.add(EntityType.ZOMBIE);
			en.add(EntityType.ZOMBIE);
			en.add(EntityType.ZOMBIE);
			en.add(EntityType.ZOMBIE);
			
			en.add(EntityType.WITCH);
			en.add(EntityType.WITCH);
			en.add(EntityType.WITCH);
			en.add(EntityType.WITCH);
			en.add(EntityType.WITCH);
			
			en.add(EntityType.CAVE_SPIDER);
			en.add(EntityType.CAVE_SPIDER);
			en.add(EntityType.CAVE_SPIDER);
			en.add(EntityType.CAVE_SPIDER);
			en.add(EntityType.CAVE_SPIDER);
			
			ArrayList<Location> spawns = getAllSpawns(map, wave);
			
			while(!en.isEmpty()) {
				
				EntityType entity = en.get(Main.ins.utils.getRandom().nextInt(en.size()));
				Location spawn = spawns.get(Main.ins.utils.getRandom().nextInt(spawns.size()));
				
				if(Bukkit.getWorld("Arena") == null) return;
				
				spawn.setWorld(Bukkit.getWorld("Arena"));
				
				Entity spawned = spawn.getWorld().spawnEntity(spawn, entity);
				living.add(spawned);
				
				en.remove(entity);
			}
			
			
		} else if(wave == 3) {
			//5 Zombie 5 Skellete 5 Spinnen
			
			ArrayList<EntityType> en = new ArrayList<>();
			
			en.add(EntityType.CREEPER);
			en.add(EntityType.CREEPER);
			en.add(EntityType.CREEPER);
			en.add(EntityType.CREEPER);
			en.add(EntityType.CREEPER);
			
			en.add(EntityType.SKELETON);
			en.add(EntityType.SKELETON);
			en.add(EntityType.SKELETON);
			en.add(EntityType.SKELETON);
			en.add(EntityType.SKELETON);
			en.add(EntityType.SKELETON);
			en.add(EntityType.SKELETON);
			en.add(EntityType.SKELETON);
			en.add(EntityType.SKELETON);
			en.add(EntityType.SKELETON);
			
			en.add(EntityType.BLAZE);
			en.add(EntityType.BLAZE);
			en.add(EntityType.BLAZE);
			en.add(EntityType.BLAZE);
			en.add(EntityType.BLAZE);
			
			ArrayList<Location> spawns = getAllSpawns(map, wave);
			
			while(!en.isEmpty()) {
				
				EntityType entity = en.get(Main.ins.utils.getRandom().nextInt(en.size()));
				Location spawn = spawns.get(Main.ins.utils.getRandom().nextInt(spawns.size()));
				
				if(Bukkit.getWorld("Arena") == null) return;
				
				spawn.setWorld(Bukkit.getWorld("Arena"));
				
				Entity spawned = spawn.getWorld().spawnEntity(spawn, entity);
				living.add(spawned);
				
				en.remove(entity);
			}
			
			
		} else if(wave == 4) {
			//5 Zombie 5 Skellete 5 Spinnen
			
			ArrayList<EntityType> en = new ArrayList<>();
			
			en.add(EntityType.BLAZE);
			en.add(EntityType.BLAZE);
			en.add(EntityType.BLAZE);
			en.add(EntityType.BLAZE);
			en.add(EntityType.BLAZE);
			
			en.add(EntityType.ZOMBIE);
			en.add(EntityType.ZOMBIE);
			en.add(EntityType.ZOMBIE);
			en.add(EntityType.ZOMBIE);
			en.add(EntityType.ZOMBIE);
			
			en.add(EntityType.SPIDER);
			en.add(EntityType.SPIDER);
			en.add(EntityType.SPIDER);
			en.add(EntityType.SPIDER);
			en.add(EntityType.SPIDER);
			en.add(EntityType.SPIDER);
			en.add(EntityType.SPIDER);
			en.add(EntityType.SPIDER);
			en.add(EntityType.SPIDER);
			en.add(EntityType.SPIDER);
			
			ArrayList<Location> spawns = getAllSpawns(map, wave);
			
			while(!en.isEmpty()) {
				
				EntityType entity = en.get(Main.ins.utils.getRandom().nextInt(en.size()));
				Location spawn = spawns.get(Main.ins.utils.getRandom().nextInt(spawns.size()));
				
				if(Bukkit.getWorld("Arena") == null) return;
				
				spawn.setWorld(Bukkit.getWorld("Arena"));
				
				Entity spawned = spawn.getWorld().spawnEntity(spawn, entity);
				living.add(spawned);
				
				en.remove(entity);
			}
			
			
		} else if(wave == 5) {
			//5 Zombie 5 Skellete 5 Spinnen
			
			ArrayList<EntityType> en = new ArrayList<>();
			
			en.add(EntityType.ZOMBIE);
			en.add(EntityType.ZOMBIE);
			en.add(EntityType.ZOMBIE);
			en.add(EntityType.ZOMBIE);
			en.add(EntityType.ZOMBIE);
			en.add(EntityType.ZOMBIE);
			en.add(EntityType.ZOMBIE);
			en.add(EntityType.ZOMBIE);
			en.add(EntityType.ZOMBIE);
			en.add(EntityType.ZOMBIE);
			
			
			en.add(EntityType.SKELETON);
			en.add(EntityType.SKELETON);
			en.add(EntityType.SKELETON);
			en.add(EntityType.SKELETON);
			en.add(EntityType.SKELETON);
			en.add(EntityType.SKELETON);
			en.add(EntityType.SKELETON);
			en.add(EntityType.SKELETON);
			en.add(EntityType.SKELETON);
			en.add(EntityType.SKELETON);
			
			en.add(EntityType.SPIDER);
			en.add(EntityType.SPIDER);
			en.add(EntityType.SPIDER);
			en.add(EntityType.SPIDER);
			en.add(EntityType.SPIDER);
			
			en.add(EntityType.CAVE_SPIDER);
			en.add(EntityType.CAVE_SPIDER);
			en.add(EntityType.CAVE_SPIDER);
			en.add(EntityType.CAVE_SPIDER);
			en.add(EntityType.CAVE_SPIDER);
			
			en.add(EntityType.WITCH);
			en.add(EntityType.WITCH);
			en.add(EntityType.WITCH);
			en.add(EntityType.WITCH);
			en.add(EntityType.WITCH);
			
			ArrayList<Location> spawns = getAllSpawns(map, wave);
			
			while(!en.isEmpty()) {
				
				EntityType entity = en.get(Main.ins.utils.getRandom().nextInt(en.size()));
				Location spawn = spawns.get(Main.ins.utils.getRandom().nextInt(spawns.size()));
				
				
				
				
				if(Bukkit.getWorld("Arena") == null) return;
				
				spawn.setWorld(Bukkit.getWorld("Arena"));
				
				Entity spawned = spawn.getWorld().spawnEntity(spawn, entity);
				
				if(spawned.getType() == EntityType.SKELETON) {
					((Skeleton)spawned).setSkeletonType(SkeletonType.WITHER);
					((Skeleton)spawned).getEquipment().setItemInHand(Main.ins.utils.createItem(Material.STONE_SWORD, 0, 1, null, null));
				}
				
				living.add(spawned);
				
				en.remove(entity);
			}
			
			Location sheepSpawn = getSheepSpawn(Main.ins.map);
			if(sheepSpawn != null) {
				sheepSpawn.setWorld(Bukkit.getWorld("Arena"));
				Main.ins.sheep = new RescueSheep(sheepSpawn);
				Main.ins.sheep.spawnSheep();
			}
		
			
		} else if(wave == 6) {
			Location spawn = getBossSpawn(Main.ins.map);
			
			if(Bukkit.getWorld("Arena") != null) {
				spawn.setWorld(Bukkit.getWorld("Arena"));
				
				Main.ins.boss = new Boss(spawn);
				Main.ins.boss.spawnBoss();
				
				living.add(Main.ins.boss.getGolem());
				
			}
			
			
		}
	}
	
	
	
	
	
	public static boolean allDeath() {
		for(Entity en : living) 
			if(!(en == null || en.isDead() || !en.isValid())) {
				return false;
			} 
		
		
		return true;
		
		
	}
	
	public static int getRemainingMobs() {
		int mobs = 0;
		for(Entity en : living) {
			
			if(!(en == null || en.isDead() || !en.isValid())) {
				mobs++;
			}
		}
			 
		
		
		return mobs;
		
		
	}
	
	public static void setSheepSpawn(String name, Location loc) {
		if(name != null && loc != null) {
			YamlConfiguration cfg = Main.ins.utils.getYaml("Maps");
			
			cfg.set("Maps." + name + ".Sheep.X", ((double)loc.getBlockX()+0.5));
			cfg.set("Maps." + name + ".Sheep.Y", (loc.getBlockY()));
			cfg.set("Maps." + name + ".Sheep.Z", ((double)loc.getBlockZ())+0.5);
			
			cfg.set("Maps." + name + ".Sheep.Yaw", loc.getYaw());
			cfg.set("Maps." + name + ".Sheep.Pitch", loc.getPitch());
			
			try {
				cfg.save(Main.ins.utils.getPluginFile("Maps"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	
	public static Location getSheepSpawn(String name) {
		if(name != null && isMapExists(name)) {
			YamlConfiguration cfg = Main.ins.utils.getYaml("Maps");
			
			if(cfg.getConfigurationSection("Maps." + name + ".Sheep") != null) {
				double x = cfg.getDouble("Maps." + name + ".Sheep.X");
				double y = cfg.getDouble("Maps." + name + ".Sheep.Y")+0.5;
				double z = cfg.getDouble("Maps." + name + ".Sheep.Z");
				
				double yaw = cfg.getDouble("Maps." + name + ".Sheep.Yaw");
				double pitch = cfg.getDouble("Maps." + name + ".Sheep.Pitch");
			
				return new Location(null, x, y, z, (float)yaw, (float)pitch);
			}
		}
		return null;
	}
	
	public static void setBossSpawn(String name, Location loc) {
		if(name != null && loc != null) {
			YamlConfiguration cfg = Main.ins.utils.getYaml("Maps");
			
			cfg.set("Maps." + name + ".Boss.X", ((double)loc.getBlockX()+0.5));
			cfg.set("Maps." + name + ".Boss.Y", (loc.getBlockY()));
			cfg.set("Maps." + name + ".Boss.Z", ((double)loc.getBlockZ())+0.5);
			
			cfg.set("Maps." + name + ".Boss.Yaw", loc.getYaw());
			cfg.set("Maps." + name + ".Boss.Pitch", loc.getPitch());
			
			try {
				cfg.save(Main.ins.utils.getPluginFile("Maps"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	
	public static Location getBossSpawn(String name) {
		if(name != null && isMapExists(name)) {
			YamlConfiguration cfg = Main.ins.utils.getYaml("Maps");
			
			if(cfg.getConfigurationSection("Maps." + name + ".Boss") != null) {
				double x = cfg.getDouble("Maps." + name + ".Boss.X");
				double y = cfg.getDouble("Maps." + name + ".Boss.Y")+0.5;
				double z = cfg.getDouble("Maps." + name + ".Boss.Z");
				
				double yaw = cfg.getDouble("Maps." + name + ".Boss.Yaw");
				double pitch = cfg.getDouble("Maps." + name + ".Boss.Pitch");
			
				return new Location(null, x, y, z, (float)yaw, (float)pitch);
			}
		}
		return null;
	}
	
	public static void setBossRoom(String name, Location pos1, Location pos2) {
		if(name != null && pos1 != null && pos2 != null) {
			YamlConfiguration cfg = Main.ins.utils.getYaml("Maps");
			
			Location min = Main.ins.utils.getMinLoc(pos1, pos2);
			Location max = Main.ins.utils.getMaxLoc(pos1, pos2);
			
			
			cfg.set("Maps." + name + ".BossRoom.MaxX", max.getBlockX());
			cfg.set("Maps." + name + ".BossRoom.MaxY", max.getBlockY());
			cfg.set("Maps." + name + ".BossRoom.MaxZ", max.getBlockZ());
			
			cfg.set("Maps." + name + ".BossRoom.MinX", min.getBlockX());
			cfg.set("Maps." + name + ".BossRoom.MinY", min.getBlockY());
			cfg.set("Maps." + name + ".BossRoom.MinZ", min.getBlockZ());
			
			
			try {
				cfg.save(Main.ins.utils.getPluginFile("Maps"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static Location getBossRoom(String map, boolean min) {
		
		if(map != null && isMapExists(map)) {
			YamlConfiguration cfg = Main.ins.utils.getYaml("Maps");
			
			if(min) {
				if(cfg.getConfigurationSection("Maps." + map + ".BossRoom") != null) {
					int minX = cfg.getInt("Maps." + map + ".BossRoom.MinX");
					int minY = cfg.getInt("Maps." + map + ".BossRoom.MinY");
					int minZ = cfg.getInt("Maps." + map + ".BossRoom.MinZ");
					return new Location(null, minX, minY, minZ);
				}
			} else {
				if(cfg.getConfigurationSection("Maps." + map + ".BossRoom") != null) {
					int maxX = cfg.getInt("Maps." + map + ".BossRoom.MaxX");
					int maxY = cfg.getInt("Maps." + map + ".BossRoom.MaxY");
					int maxZ = cfg.getInt("Maps." + map + ".BossRoom.MaxZ");
					return new Location(null, maxX, maxY, maxZ);
				}
			}
			
			
		}
		return null;
	}
	
	public static boolean validMapAvailable() {
		for(String map : getAllMaps(false)) {
			if(isMapOkay(map)) return true;
		}
		
		
		return false;
	}
	
}
