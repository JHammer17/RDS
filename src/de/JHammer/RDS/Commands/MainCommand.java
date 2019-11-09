package de.JHammer.RDS.Commands;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import de.JHammer.RDS.Main;
import de.JHammer.RDS.Enums.GameState;
import de.JHammer.RDS.Enums.Kit;
import de.JHammer.RDS.Manager.MapManager;
import de.JHammer.RDS.Static.MySQLMgr.MySQLMgr;
import de.JHammer.RDS.WorldLoader.WorldResetMgr;

public class MainCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			
			Player p = (Player)sender;
			
			
			if(p.hasPermission("rds.admin")) {
				
				if(args.length > 0) {
					
					if(args[0].equalsIgnoreCase("edit")) {
						Main.ins.state = GameState.EDIT;
						
						p.sendMessage(Main.ins.prefix + "§aDer Server befindet sich nun im Edit-Modus!");
						p.sendMessage(Main.ins.prefix + "§aUm diesen zu beenden muss der Server neu gestartet werden!");
						return true;
					}
					
					if(args[0].equalsIgnoreCase("setKit")) {
						if(args.length == 4) {
							
							new BukkitRunnable() {
								
								@Override
								public void run() {
									if(MySQLMgr.isPlayerRegistered(MySQLMgr.getUUID(args[1]))) {
										
										try {
											Kit.valueOf(args[2].toUpperCase());
										} catch (Exception e) {
											p.sendMessage(Main.ins.prefix + "§cDieses Kit gibt es nicht! Folgende Kits gibt es:");
											StringBuilder builder = new  StringBuilder();
											for(Kit kit : Kit.values()) {
												if(kit.toString().equalsIgnoreCase("STARTER")) continue;
												
												if(builder.toString().equalsIgnoreCase("")) builder.append(kit);
												 else builder.append(", ").append(kit);
											}
											
											p.sendMessage("§c" + builder.toString());
											
											return;
										}
										
										if(Kit.valueOf(args[2].toUpperCase()) != null && !args[2].equalsIgnoreCase("Starter")) {
											if(args[3].equalsIgnoreCase("true")) {
												MySQLMgr.setBuyed(MySQLMgr.getUUID(args[1]), Kit.valueOf(args[2].toUpperCase()), true);
												p.sendMessage(Main.ins.prefix + "Kit §e" + Kit.valueOf(args[2].toUpperCase()).getName() + " §7für §b" + args[1] + " §afreigeschaltet!");
											} else {
												MySQLMgr.setBuyed(MySQLMgr.getUUID(args[1]), Kit.valueOf(args[2].toUpperCase()), false);
												p.sendMessage(Main.ins.prefix + "Kit §e" + Kit.valueOf(args[2].toUpperCase()).getName() + " §7für §b" + args[1] + " §cdeaktiviert!");
											}
										} else {
											p.sendMessage(Main.ins.prefix + "§cDieses Kit gibt es nicht! Folgende Kits gibt es:");
											StringBuilder builder = new  StringBuilder();
											for(Kit kit : Kit.values()) {
												
												if(kit.toString().equalsIgnoreCase("STARTER")) continue;
												
												if(builder.toString().equalsIgnoreCase("")) builder.append(kit);
												 else builder.append(", ").append(kit);
											}
											
											p.sendMessage("§c" + builder.toString());
										}
										
									} else {
										p.sendMessage(Main.ins.prefix + "§cSpieler nicht gefunden!");
									}
								}
							}.runTaskAsynchronously(Main.ins);
							
							return true;
						} else {
							p.sendMessage(Main.ins.prefix + "§cNutze: /rds setKit [Name] [Kit] [true|false]");
						}
					}
					
					if(args[0].equalsIgnoreCase("kits")) {
						p.sendMessage(Main.ins.prefix + "Verfügbare Kits:");
						StringBuilder builder = new  StringBuilder();
						for(Kit kit : Kit.values()) {
							
							if(kit.toString().equalsIgnoreCase("STARTER")) continue;
							
							if(builder.toString().equalsIgnoreCase("")) builder.append(kit);
							 else builder.append(", ").append(kit);
						}
						
						p.sendMessage("§e" + builder.toString());
					}
					
					if(args[0].equalsIgnoreCase("edit")) {
						Main.ins.state = GameState.EDIT;
						
						p.sendMessage(Main.ins.prefix + "§aDer Server befindet sich nun im Edit-Modus!");
						p.sendMessage(Main.ins.prefix + "§aUm diesen zu beenden muss der Server neu gestartet werden!");
						return true;
					}
					
					if(args[0].equalsIgnoreCase("addMobSpawn")) {
						if(args.length == 3) {
							if(MapManager.isMapExists(args[1])) {
								

								int number = 0;
								
								try {
									 
									number = Integer.parseInt(args[2]);
									if(number > 5 || number < 1) {
										p.sendMessage(Main.ins.prefix + "§cDu musst eine Zahl zwischen 1-5 eingeben!");
										return true;
									}
								} catch (NumberFormatException e) {
									p.sendMessage(Main.ins.prefix + "§cDu musst eine Zahl zwischen 1-5 eingeben!");
									
									return true;
								}
								
								MapManager.addMobSpawn(args[1], p.getLocation(), number);
								p.sendMessage(Main.ins.prefix + "§aSpawn gesetzt!");
							}
							
						} else {
							p.sendMessage(Main.ins.prefix + "§cNutze: /rds addMobSpawn [Map] [1-5]");
						}
					}
					
					if(args[0].equalsIgnoreCase("setSheepSpawn")) {
						if(args.length == 2) {
							if(MapManager.isMapExists(args[1])) {
								

								
								
								MapManager.setSheepSpawn(args[1], p.getLocation());;
								p.sendMessage(Main.ins.prefix + "§aSchaf Spawn gesetzt!");
							}
							
						} else {
							p.sendMessage(Main.ins.prefix + "§cNutze: /rds setSheepSpawn [Map]");
						}
					}
					
					if(args[0].equalsIgnoreCase("setBossSpawn")) {
						if(args.length == 2) {
							if(MapManager.isMapExists(args[1])) {
								

								
								
								MapManager.setBossSpawn(args[1], p.getLocation());;
								p.sendMessage(Main.ins.prefix + "§aBoss Spawn gesetzt!");
							}
							
						} else {
							p.sendMessage(Main.ins.prefix + "§cNutze: /rds setBossSpawn [Map]");
						}
					}
					
					if(args[0].equalsIgnoreCase("setLobby")) {
						YamlConfiguration cfg = Main.ins.utils.getYaml("Spawns");
						
						Location loc = p.getLocation();
						
						cfg.set("Spawns.Lobby.X", ((double)loc.getBlockX())+0.5);
						cfg.set("Spawns.Lobby.Y", loc.getBlockY()+3);
						cfg.set("Spawns.Lobby.Z", ((double)loc.getBlockZ())+0.5);
						
						cfg.set("Spawns.Lobby.Yaw", loc.getYaw());
						cfg.set("Spawns.Lobby.Pitch", loc.getPitch());
						
						cfg.set("Spawns.Lobby.World", loc.getWorld().getName());
						
						try {
							cfg.save(Main.ins.utils.getPluginFile("Spawns"));
						} catch (IOException e) {
							e.printStackTrace();
							p.sendMessage("§cFehler beim Speichern der Datei");
							return true;
						}
						
						p.sendMessage(Main.ins.prefix + "§aLobby erfolgreich gesetzt!");
						return true;
					}
					
					if(args[0].equalsIgnoreCase("createMap")) {
						if(args.length == 3) {
							if(!MapManager.isMapExists(args[1])) {
								
								if(Bukkit.getWorld(args[2]) != null) {
									MapManager.addMap(args[1]);
									MapManager.setWorld(args[1], Bukkit.getWorld(args[2]));
									
									WorldResetMgr.createNewWorldLayout(args[1], p, Bukkit.getWorld(args[2]));
									
									
									p.sendMessage(Main.ins.prefix + "§aMap erstellt!");
								
								} else {
									p.sendMessage("§cDiese Welt gibt es nicht!");
								}
								
							} else {
								p.sendMessage("§cDiese Map exestiert bereits!");
							}
						} else {
							p.sendMessage("§cNutze: /rds createMap [Map] [Welt]");
						}
					} 
					
					
					if(args[0].equalsIgnoreCase("saveMap")) {
						if(args.length == 3) {
							if(MapManager.isMapExists(args[1])) {
								
								if(Bukkit.getWorld(args[2]) != null) {
									
									MapManager.setWorld(args[1], Bukkit.getWorld(args[2]));
									
									WorldResetMgr.createNewWorldLayout(args[1], p, Bukkit.getWorld(args[2]));
									
									
									p.sendMessage(Main.ins.prefix + "§aMap überschrieben!");
								
								} else {
									p.sendMessage("§cDiese Welt gibt es nicht!");
								}
								
							} else {
								p.sendMessage("§cDiese Map exestiert nicht!");
							}
						} else {
							p.sendMessage("§cNutze: /rds saveMap [Map] [Welt]");
						}
					} 
					
					if(args[0].equalsIgnoreCase("listMaps")) {
						p.sendMessage(MapManager.getAllMaps(true).toString());
					}
					
					
					
					
					if(args[0].equalsIgnoreCase("setMineSpawn")) {
						if(args.length == 2) {
							if(MapManager.isMapExists(args[1])) {
								
								MapManager.setSpawn(args[1], p.getLocation());
								
								
								p.sendMessage(Main.ins.prefix + "§aMapspawn gesetzt!");
								
							} else {
								p.sendMessage("§cMap nicht gefunden!");
							}
						} else {
							p.sendMessage("§cNutze: /rds setMineSpawn [Map]");
						}
					}
					
					if(args[0].equalsIgnoreCase("loadMap")) {
						if(args.length == 2) {
							if(MapManager.isMapExists(args[1])) {
								if(MapManager.isMapSaved(args[1])) {
									MapManager.loadMap(args[1], false);
									p.sendMessage(Main.ins.prefix + "§aMap geladen!");
									
								} else {
									p.sendMessage("§cMap nicht gespeichert!");
								}
								
							} else {
								p.sendMessage("§cMap nicht gefunden!");
							}
						} else {
							p.sendMessage("§cNutze: /rds loadMap [Map]");
						}
					}
					
					
					
					if(args[0].equalsIgnoreCase("setWayBlocker")) {
						if(args.length == 3) {
							if(MapManager.isMapExists(args[1])) {
								
								if(Main.ins.getRDSPlayer(p).getPos1() != null && Main.ins.getRDSPlayer(p).getPos2() != null) {
									
									if(Main.ins.getRDSPlayer(p).getPos1().getWorld() != null && 
									   Main.ins.getRDSPlayer(p).getPos2().getWorld() != null &&
									   Main.ins.getRDSPlayer(p).getPos1().getWorld().getName().equalsIgnoreCase(Main.ins.getRDSPlayer(p).getPos2().getWorld().getName())) {
										
										int number = 0;
										
										try {
											 
											if(args[2].equalsIgnoreCase("boss")) number = 6;
											else if(args[2].equalsIgnoreCase("entry")) number = 0;
											else number = Integer.parseInt(args[2]);
											if(number > 6 || number < 0) {
												p.sendMessage(Main.ins.prefix + "§cDu musst eine Zahl zwischen 1-5 oder Boss/Entry eingeben!");
												return true;
											}
										} catch (NumberFormatException e) {
											p.sendMessage(Main.ins.prefix + "§cDu musst eine Zahl zwischen 1-5 oder Boss/Entry eingeben!");
											
											return true;
										}
										
										
										MapManager.setWayBlocker(args[1], Main.ins.getRDSPlayer(p).getPos1(), Main.ins.getRDSPlayer(p).getPos2(), number);
										
										p.sendMessage(Main.ins.prefix + "§aDu hast einen Blocker gesetzt!");
										
										
									} else {
										p.sendMessage("§cBeide Positionen müssen sich auf der gleichen Welt befinden!");
									}
										
									
									
								} else {
									p.sendMessage("§cDu musst beide Positionen setzen!");
								}
								
								
								
							} else {
								p.sendMessage("§cMap nicht gefunden!");
							}
						} else {
							p.sendMessage("§cNutze: /rds setWayBlocker [Map] [Entry|1-5|Boss]");
						}
					}
					
					if(args[0].equalsIgnoreCase("setBossRoom")) {
						if(args.length == 2) {
							if(MapManager.isMapExists(args[1])) {
								
								if(Main.ins.getRDSPlayer(p).getPos1() != null && Main.ins.getRDSPlayer(p).getPos2() != null) {
									
									if(Main.ins.getRDSPlayer(p).getPos1().getWorld() != null && 
									   Main.ins.getRDSPlayer(p).getPos2().getWorld() != null &&
									   Main.ins.getRDSPlayer(p).getPos1().getWorld().getName().equalsIgnoreCase(Main.ins.getRDSPlayer(p).getPos2().getWorld().getName())) {
										
										
										
										
										MapManager.setBossRoom(args[1], Main.ins.getRDSPlayer(p).getPos1(), Main.ins.getRDSPlayer(p).getPos2());
										
										p.sendMessage(Main.ins.prefix + "§aDu hast den Boss Raum gesetzt!");
										
										
									} else {
										p.sendMessage("§cBeide Positionen müssen sich auf der gleichen Welt befinden!");
									}
										
									
									
								} else {
									p.sendMessage("§cDu musst beide Positionen setzen!");
								}
								
								
								
							} else {
								p.sendMessage("§cMap nicht gefunden!");
							}
						} else {
							p.sendMessage("§cNutze: /rds setBossRoom [Map]");
						}
					}
					
					
					if(args[0].equalsIgnoreCase("setDS") || args[0].equalsIgnoreCase("setDestroyStand")) {
						if(args.length == 3) {
							if(MapManager.isMapExists(args[1])) {
								
								int number = 0;
								
								try {
									 
									if(args[2].equalsIgnoreCase("boss")) number = 6;
									else if(args[2].equalsIgnoreCase("entry")) number = 0;
									else number = Integer.parseInt(args[2]);
									if(number > 6 || number < 0) {
										p.sendMessage(Main.ins.prefix + "§cDu musst eine Zahl zwischen 1-5 oder Boss/Entry eingeben!");
										return true;
									}
								} catch (NumberFormatException e) {
									p.sendMessage(Main.ins.prefix + "§cDu musst eine Zahl zwischen 1-5 oder Boss/Entry eingeben!");
									
									return true;
								}
								
								
								MapManager.setDestroyStand(args[1], p.getLocation(), number);
								
								p.sendMessage("§aGesetzt!");
							} else {
								p.sendMessage("§cMap nicht gefunden!");
							}
						} else {
							p.sendMessage("§cNutze: /rds setDS [Map] [Entry|1-5|Boss]");
						}
					} 
					
					if(args[0].equalsIgnoreCase("help")) {
						if(args.length >= 2) {
							try {
								sendHelp(p, Integer.parseInt(args[1])-1);
							} catch (NumberFormatException e) {
								p.sendMessage(Main.ins.prefix + "§cDu musst eine Zahl angeben!");
								
							}
						} else {
							sendHelp(p, 0);
						}
					}
					
				} else {
					//TODO ÜBERSICHT ÜBER ALLE COMMANDS
					sendHelp(p, 0);
				}
				
				
			} else {
				p.sendMessage("§cDazu hast du keine Rechte!");
			}
			
			
		} else {
			sender.sendMessage("§cDu bist kein Spieler!");
		}
		
		
		
		return true;
	}

	public void sendHelp(Player p, int page) {
		p.sendMessage("§6Hilfe Seite §c" + page + "/3");
		p.sendMessage("§a");
		if(page > 2) page = 0;
		if(page == 0) {
			p.sendMessage("§7● §6edit");
			p.sendMessage("§7● §6setLobby");
			p.sendMessage("§7● §6createMap [Map] [Welt]");
			p.sendMessage("§7● §6setMineSpawn [Map]");
			p.sendMessage("§7● §6addMobSpawn [Map] [1-5]");
		} else if(page == 1) {
			p.sendMessage("§7● §6setSheepSpawn [Map]");
			p.sendMessage("§7● §6setBossSpawn [Map]");
			p.sendMessage("§7● §6setBossRoom [Map]");
			p.sendMessage("§7● §6setWayBlocker [Map] [Entry|1-5|Boss]");
			p.sendMessage("§7● §6setDS [Map] [Entry|1-5|Boss]");
		} else if(page == 2) {
			p.sendMessage("§7● §6saveMap [Map] [Welt]");
			p.sendMessage("§7● §6listMaps");
			p.sendMessage("§7● §6kits");
			p.sendMessage("§7● §6setKit [Name] [Kit] [true|false]");
			p.sendMessage("§7● §6loadMap [Map]");
		}
		
	}
	
	
}
