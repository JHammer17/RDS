package de.JHammer.RDS.Static;


import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import de.JHammer.RDS.Main;
import de.JHammer.RDS.Enums.GameState;
import de.JHammer.RDS.Manager.KitMgr;
import de.JHammer.RDS.Manager.MapManager;
import de.JHammer.RDS.Manager.MapVotingMgr;
import de.JHammer.RDS.Manager.SoundMgr.JSound;
import de.JHammer.RDS.Objects.RDSPlayer;
import de.JHammer.RDS.Static.MySQLMgr.MySQLMgr;
import de.JHammer.RDS.Static.MySQLMgr.MySQLMgr.Stats;


public class Counter {

	public static void resetSchedulers() {
		stopIngameCounter();
		stopStartCounter();
		stopResetCounter();
		
		startTimer = Main.ins.startTimer;
		forceStart = false;
		maxStartTimer = 0;
		notEnoughPlayersTimer = 10;
		ingameTimer = 0;
		resetCounter = 10;
	}
	
	
	
	
	
	private static BukkitRunnable startC;
	
	
	public static void setStartCounter(int timer) {
		startTimer = timer;
		maxStartTimer = Math.max(startTimer, maxStartTimer);
		
	}
	
	public static void setForceStart(boolean force) {
		forceStart = force;
	}
	
	public static boolean getForeStart() {
		return forceStart;
	}
	
	public static int getStartCounter() {
		return startTimer;
	}
	
	
	
	static int startTimer = Main.ins.startTimer;
	static int maxStartTimer;
	static boolean forceStart = false;
	static int notEnoughPlayersTimer = 10; 
	
	
	public static void startStartCounter() {
		
		
		startC = new BukkitRunnable() {
			
			@Override
			public void run() {
				
				
				if(Main.ins.state != GameState.LOBBY) {
					stopStartCounter();
					return;
				}
				
				if(Bukkit.getOnlinePlayers().size() >= Main.ins.minPlayers || forceStart) {
					maxStartTimer = Math.max(startTimer, maxStartTimer);
					
					for(Player players : Bukkit.getOnlinePlayers()) {
						
						
						Main.ins.utils.sendActionBar(players, "§fKit: " +  Main.ins.getRDSPlayer(players).getKit().getDisplayName());
						
						
						players.setLevel(startTimer);
						players.setExp(((float)startTimer/(float)maxStartTimer));
						
						if(startTimer == 60) {
							players.sendMessage(Main.ins.prefix + "§7Das Spiel beginnt in §eeiner Minute!");
							Main.ins.utils.getSoundMgr().playSound(players, JSound.NOTE_PLING, 10.0f, 1.0f);
						} else if(startTimer > 60  && startTimer%60 == 0) {
							players.sendMessage(Main.ins.prefix + "§7Das Spiel beginnt in §e" + (startTimer/60) + " Minuten!");
							Main.ins.utils.getSoundMgr().playSound(players, JSound.NOTE_PLING, 10.0f, 1.0f);
						} else if(startTimer == 1) {
							players.sendMessage(Main.ins.prefix + "§7Das Spiel beginnt in §eeiner Sekunde!");
							Main.ins.utils.getSoundMgr().playSound(players, JSound.NOTE_PLING, 10.0f, 1.0f);
						} else if((startTimer < 60 && startTimer%10 == 0 && startTimer != 0) || startTimer == 5 || startTimer == 4 || startTimer == 3 || startTimer == 2) {
							if(startTimer == 10) {
								String map = MapVotingMgr.getWonMap();
								
								if(Main.ins.map == null) Main.ins.map = map;
								
								players.sendMessage(Main.ins.prefix + "§7Das §6Voting §7wurde §cbeendet!");
								players.sendMessage(Main.ins.prefix + "§7Map: §a" + Main.ins.map);
								
								
								
								
								
								
								players.closeInventory();
							}
							
							players.sendMessage(Main.ins.prefix + "§7Das Spiel beginnt in §e" + startTimer + " Sekunden!");
							Main.ins.utils.getSoundMgr().playSound(players, JSound.NOTE_PLING, 10.0f, 1.0f);
						} else if(startTimer <= 0) {
							
							new BukkitRunnable() {
								
								@Override
								public void run() {
									MapManager.tpToArena(players, Main.ins.map);
									
									players.sendMessage(Main.ins.prefix + "§aDas Spiel beginnt! Viel Glück!");
									Main.ins.utils.getSoundMgr().playSound(players, JSound.LEVEL_UP, 10.0f, 1.0f);
									
									KitMgr.giveKit(players, Main.ins.getRDSPlayer(players).getKit());
									Main.ins.getRDSPlayer(players).setHasPlayed(true);
									
								}
							}.runTask(Main.ins);
							
							
							
						}
						
					}
					
					if(startTimer == 0) {
						stopStartCounter();
						
						new BukkitRunnable() {
							
							@Override
							public void run() {
								Main.ins.entryMgrs.get(0).spawnDestroyArmorStand();
								
							}
						}.runTaskLater(Main.ins,20);
						Main.ins.state = GameState.INGAME;
						startIngameCounter();
					}
					
					if(startTimer == 10) {
						MapManager.loadMap(Main.ins.map, true);
						
						MapVotingMgr.cleanUp();
					}
					
					
					startTimer--;
				} else {
					startTimer = 60;
					maxStartTimer = 60;
					
					for(Player players : Bukkit.getOnlinePlayers()) {
						Main.ins.utils.sendActionBar(players, "§fKit: " +  Main.ins.getRDSPlayer(players).getKit().getDisplayName());
						
						players.setLevel(0);
						players.setExp(0);
					}
					
					
					if(notEnoughPlayersTimer <= 0) {
						for(Player players : Bukkit.getOnlinePlayers()) {
							
							if((Main.ins.minPlayers-Bukkit.getOnlinePlayers().size()) == 1) {
								players.sendMessage(Main.ins.prefix + "§cEs fehlt noch ein Spieler um das Spiel zu starten!");
								
								
							} else 
							
							players.sendMessage(Main.ins.prefix + "§cEs fehlen noch " + (Main.ins.minPlayers-Bukkit.getOnlinePlayers().size()) + " Spieler um das Spiel zu starten!");
						
						
						}
						notEnoughPlayersTimer = 60;//TODO AUF 15 SETZEN!
					} else notEnoughPlayersTimer--;
					
					
					
				}
				
				
				
			}
		};
		startC.runTaskTimerAsynchronously(Main.ins, 0, 20);
	}
	
	public static void stopStartCounter() {
		if(startC != null) startC.cancel();
		startC = null;
		for(Player players : Bukkit.getOnlinePlayers()) {
			players.setLevel(0);
			players.setExp(0);
		}
	}
	
	
	static int ingameTimer = 0;
	static BukkitRunnable igCounter;
	
	
	public static void startIngameCounter() {
		igCounter = new BukkitRunnable() {
			
			@Override
			public void run() {
				
				if(!MapManager.living.isEmpty() && MapManager.allDeath()) {
					
					MapManager.living.clear();
					new BukkitRunnable() {
						
						@Override
						public void run() {
							
							if(Main.ins.currentWave == 5) {
								Main.ins.sheep.spawnHologram();
								Main.ins.sheep.setResueAble(true);
							} else {
								Main.ins.entryMgrs.get(Main.ins.currentWave).spawnDestroyArmorStand();
							}
							
							
						}
					}.runTask(Main.ins);
				} else {
					if(MapManager.living.isEmpty()) {
						
						if(Main.ins.boss != null && Main.ins.boss.isSpawned() && !Main.ins.boss.getGolem().isValid()) {
							for(Player players : Bukkit.getOnlinePlayers())  {
								Main.ins.utils.sendActionBar(players, "§6§lZurück zum Eingang!");
							}
						} else {
							for(Player players : Bukkit.getOnlinePlayers())  {
								Main.ins.utils.sendActionBar(players, "§6§lWeiter zum nächsten Raum!");
							}
						}
						
						
						
					} else {
						for(Player players : Bukkit.getOnlinePlayers())  {
							if(Main.ins.boss != null && Main.ins.boss.isSpawned() && Main.ins.boss.getGolem().isValid()) {
								
								Main.ins.utils.sendActionBar(players, Main.ins.bossName + " §8[" + Main.ins.utils.colorByPercent((int)Main.ins.boss.getGolem().getHealth(), (int)Main.ins.boss.getGolem().getMaxHealth(), "||||||||||||||||||||||||||||||||||||||||||||||||||", "§c", "§a") + "§8]");
								
							} else {
								Main.ins.utils.sendActionBar(players, "§c§lGegner verbleibend: §f" + MapManager.getRemainingMobs());
							}
						}
					}
					
					
				}
				
				
				if(Main.ins.boss != null) {
					new BukkitRunnable() {
						
						@Override
						public void run() {
							
							
							Main.ins.boss.updateKI();
							if(!Main.ins.boss.hasSpawned() && Main.ins.boss.getGolem().getHealth() <= Main.ins.bossLive/2) {
								Main.ins.boss.spawnMobs();
								Main.ins.boss.setHasSpawned(true);
							}
							
						}
					}.runTask(Main.ins);
				}
				
				boolean allSpecs = true;
				
				for(RDSPlayer player : Main.ins.getRDSPlayersCopy().values()) {
					if(!player.isSpectator()) allSpecs = false;
				}
				
				if(allSpecs) {
					
					stopIngameCounter();
					endGame(false);
					return;
				}
				
				Location spawnLoc = MapManager.getSpawn(Main.ins.map);
				
				if(Bukkit.getWorld("Arena") != null) {
					spawnLoc.setWorld(Bukkit.getWorld("Arena"));
				}
				
				for(Player player : Bukkit.getOnlinePlayers()) {
					
					if(Main.ins.sheep != null && 
							Main.ins.sheep.getCarrier() != null &&
							Main.ins.sheep.getCarrier().equals(player)) {
						
						spawnLoc.setY(player.getLocation().getBlockY());
						
						if(spawnLoc.getWorld() != null && player.getWorld().getName().equals(spawnLoc.getWorld().getName())) {
							if(player.getLocation().distance(spawnLoc) <= 3) {
								
								
								if(player.getPassenger() != null) {
									Main.ins.sheep.unCarrie();
								}
								endGame(true);
								return;
							}
							
						}
						
					}
				}
				
				
				
				ingameTimer++;
				
				int remaining = Main.ins.maxIngameTime-ingameTimer;
				ScoreboardMgr.updateAllScoreboards();
				if(remaining <= 300) {
					if(remaining == 0) {
						stopIngameCounter();
						for(Player players : Bukkit.getOnlinePlayers()) {
							players.sendMessage(Main.ins.prefix + "§cDie Zeit ist abgelaufen!");
							
							
							Main.ins.utils.getSoundMgr().playSound(players, JSound.WITHER_DEATH, 10.0f, 1.5f);
						}
					} else if(remaining == 60) {
						for(Player players : Bukkit.getOnlinePlayers()) {
							players.sendMessage(Main.ins.prefix + "Es verbleibt §e1 Minute!");
							Main.ins.utils.getSoundMgr().playSound(players, JSound.NOTE_PLING, 10.0f, 1.0f);
						}
						
					} else if(remaining%60 == 0) {
						for(Player players : Bukkit.getOnlinePlayers()) {
							players.sendMessage(Main.ins.prefix + "Es verbleiben §e" + (remaining/60) + " Minuten!");
							Main.ins.utils.getSoundMgr().playSound(players, JSound.NOTE_PLING, 10.0f, 1.0f);
						}
					} else if(remaining == 1) {
						for(Player players : Bukkit.getOnlinePlayers()) {
							players.sendMessage(Main.ins.prefix + "Es verbleibt §eeine Sekunde!");
							Main.ins.utils.getSoundMgr().playSound(players, JSound.NOTE_PLING, 10.0f, 1.0f);
						}
					} else if((remaining < 60 && remaining%10 == 0) || remaining == 5 || remaining == 4 || remaining == 3 || remaining == 2) {
						for(Player players : Bukkit.getOnlinePlayers()) {
							players.sendMessage(Main.ins.prefix + "Es verbleiben §e" + remaining + " Sekunden!");
							Main.ins.utils.getSoundMgr().playSound(players, JSound.NOTE_PLING, 10.0f, 1.0f);
						}
					}
				}
				
				
				
			}
		};
		igCounter.runTaskTimerAsynchronously(Main.ins, 0, 20);
	}
	
	public static void stopIngameCounter() {
		if(igCounter != null) igCounter.cancel();
		igCounter = null;
	}
	
	public static int getIngameCounter() {
		return ingameTimer;
	}
	
	
	static int resetCounter = 10;
	
	public static int getResetCounter() {
		return resetCounter;
	}
	
	public static void setResetCounter(int counter) {
		resetCounter = counter;
	}
	
	static BukkitRunnable reset;
	
	
	public static void startResetCountDown() {
		reset = new BukkitRunnable() {
			
			@Override
			public void run() {
				
				if(resetCounter <= 0) {
					Bukkit.broadcastMessage(Main.ins.prefix + "§cDer Server wird in §ejetzt §cneu gestartet");
					for(Player players : Bukkit.getOnlinePlayers()) {
						Main.ins.utils.getSoundMgr().playSound(players, JSound.LEVEL_UP, 100, 1);
					}
					
					stopResetCounter();
					Main.ins.resetAllData();
					return;
				}
				
				if(resetCounter == 60) {
					Bukkit.broadcastMessage(Main.ins.prefix + "§cDer Server wird in §eeiner Minute §cneu gestartet");
					for(Player players : Bukkit.getOnlinePlayers()) 
						Main.ins.utils.getSoundMgr().playSound(players, JSound.NOTE_PLING, 100, 1);
					
				} else if(resetCounter > 60 && resetCounter%60 == 0) {
					Bukkit.broadcastMessage(Main.ins.prefix + "§cDer Server wird in §e" + (resetCounter/60) + " Minuten §cneu gestartet");
					for(Player players : Bukkit.getOnlinePlayers()) 
						Main.ins.utils.getSoundMgr().playSound(players, JSound.NOTE_PLING, 100, 1);
				} else if(resetCounter < 60 && resetCounter%10 == 0) {
					Bukkit.broadcastMessage(Main.ins.prefix + "§cDer Server wird in §e" + resetCounter + " Sekunden §cneu gestartet");
					for(Player players : Bukkit.getOnlinePlayers()) 
						Main.ins.utils.getSoundMgr().playSound(players, JSound.NOTE_PLING, 100, 1);
				} else if(resetCounter < 60 && resetCounter == 1) {
					Bukkit.broadcastMessage(Main.ins.prefix + "§cDer Server wird in §eeiner Sekunde §cneu gestartet");
					for(Player players : Bukkit.getOnlinePlayers()) 
						Main.ins.utils.getSoundMgr().playSound(players, JSound.NOTE_PLING, 100, 1);
				} else if(resetCounter < 60 && resetCounter <= 5) {
					Bukkit.broadcastMessage(Main.ins.prefix + "§cDer Server wird in §e" + resetCounter + " Sekunden §cneu gestartet");
					for(Player players : Bukkit.getOnlinePlayers()) 
						Main.ins.utils.getSoundMgr().playSound(players, JSound.NOTE_PLING, 100, 1);
				} 
				resetCounter -= 1;
			}
		};
		reset.runTaskTimerAsynchronously(Main.ins, 0, 20);
	}
	
	public static void stopResetCounter() {
		if(reset !=  null) reset.cancel();
		reset = null;
	}
	
	
	public static void endGame(boolean won)  {
		
		if(Main.ins.state == GameState.END) return;
		
		Main.ins.state = GameState.END;
		
		stopIngameCounter();
		
		
		
		
		if(won) {
			
			
			
			if(Main.ins.sheep != null) Main.ins.sheep.removePotionEffect();
			Random r =  Main.ins.utils.getRandom();
			
			Location spawn = MapManager.getSpawn(Main.ins.map);
			if(Bukkit.getWorld("Arena")  != null)  spawn.setWorld(Bukkit.getWorld("Arena"));
			
			spawnFireWork(spawn);
			if(r.nextBoolean())spawnFireWork(spawn.add(r.nextInt(3)-1,0,r.nextInt(3)-1));
			if(r.nextBoolean())spawnFireWork(spawn.add(r.nextInt(3)-1,0,r.nextInt(3)-1));
			if(r.nextBoolean())spawnFireWork(spawn.add(r.nextInt(3)-1,0,r.nextInt(3)-1));
			if(r.nextBoolean())spawnFireWork(spawn.add(r.nextInt(3)-1,0,r.nextInt(3)-1));
			if(r.nextBoolean())spawnFireWork(spawn.add(r.nextInt(3)-1,0,r.nextInt(3)-1));
			
			if(Main.ins.sheep != null) {
				spawnFireWork(Main.ins.sheep.getLocation());
				if(r.nextBoolean())spawnFireWork(Main.ins.sheep.getLocation().add(r.nextInt(3)-1,0,r.nextInt(3)-1));
				if(r.nextBoolean())spawnFireWork(Main.ins.sheep.getLocation().add(r.nextInt(3)-1,0,r.nextInt(3)-1));
				if(r.nextBoolean())spawnFireWork(Main.ins.sheep.getLocation().add(r.nextInt(3)-1,0,r.nextInt(3)-1));
				if(r.nextBoolean())spawnFireWork(Main.ins.sheep.getLocation().add(r.nextInt(3)-1,0,r.nextInt(3)-1));
				if(r.nextBoolean())spawnFireWork(Main.ins.sheep.getLocation().add(r.nextInt(3)-1,0,r.nextInt(3)-1));
				
				
			}
			
			for(Player players : Bukkit.getOnlinePlayers()) {
				
					spawnFireWork(players.getLocation());
					if(r.nextBoolean())spawnFireWork(players.getLocation().add(Main.ins.utils.getRandom().nextInt(3)-1,0,Main.ins.utils.getRandom().nextInt(3)-1));
					if(r.nextBoolean())spawnFireWork(players.getLocation().add(Main.ins.utils.getRandom().nextInt(3)-1,0,Main.ins.utils.getRandom().nextInt(3)-1));
					if(r.nextBoolean())spawnFireWork(players.getLocation().add(Main.ins.utils.getRandom().nextInt(3)-1,0,Main.ins.utils.getRandom().nextInt(3)-1));
					if(r.nextBoolean())spawnFireWork(players.getLocation().add(Main.ins.utils.getRandom().nextInt(3)-1,0,Main.ins.utils.getRandom().nextInt(3)-1));
					if(r.nextBoolean())spawnFireWork(players.getLocation().add(Main.ins.utils.getRandom().nextInt(3)-1,0,Main.ins.utils.getRandom().nextInt(3)-1));
					
					
				
					if(!Main.ins.getRDSPlayer(players).isSpectator()) {
						new BukkitRunnable() {
							
							@Override
							public void run() {
								MySQLMgr.setStat(players.getUniqueId(), MySQLMgr.getStat(players.getUniqueId(), Stats.PLAYED)+1, Stats.PLAYED);
								MySQLMgr.setStat(players.getUniqueId(), MySQLMgr.getStat(players.getUniqueId(), Stats.WINS)+1, Stats.WINS);
							}
						}.runTaskAsynchronously(Main.ins);
					}
				
				
				players.setMaxHealth(20);
				
				for(PotionEffect effect : players.getActivePotionEffects()) {
					players.removePotionEffect(effect.getType());
				}
				
				
				
				
				if(Main.ins.getRDSPlayer(players).hasPlayed()) {
					new BukkitRunnable() {
						
						@Override
						public void run() {
							
							if(!Main.ins.moneyEnabled) return;
							
							if(Main.ins.getRDSPlayer(players).isSpectator()) {
								MoneyManager.addMoney(players, Main.ins.moneyWinDeath);
								players.sendMessage(Main.ins.prefix + "§7Du bekommst §a+" + Main.ins.moneyWinDeath +"§6 Coins§7.");
								
							} else {
								MoneyManager.addMoney(players, Main.ins.moneyWinAlive);
								players.sendMessage(Main.ins.prefix + "§7Du bekommst §a+" + Main.ins.moneyWinAlive + "§6 Coins§7.");
								
							}
							
							
							Main.ins.getRDSPlayer(players).updateCoins();
							
						}
					}.runTaskAsynchronously(Main.ins);
				} 
				
				
				players.sendMessage(Main.ins.prefix + "§aIhr konntet das Schaf retten und habt gewonnen! §8[§7" + Main.ins.utils.getFormattedTime(getIngameCounter()) + "§8]");
			}
			
		} else {
			
			if(Main.ins.sheep != null) Main.ins.sheep.die();
			for(Player players : Bukkit.getOnlinePlayers()) {
				
				if(!Main.ins.getRDSPlayer(players).isSpectator()) {
					new BukkitRunnable() {
						
						@Override
						public void run() {
							MySQLMgr.setStat(players.getUniqueId(), MySQLMgr.getStat(players.getUniqueId(), Stats.PLAYED)+1, Stats.PLAYED);
							MySQLMgr.setStat(players.getUniqueId(), MySQLMgr.getStat(players.getUniqueId(), Stats.LOST)+1, Stats.LOST);
						}
					}.runTaskAsynchronously(Main.ins);
				}
				
				
				if(players.getPassenger() != null) players.eject();
				
				players.setMaxHealth(20);
				for(PotionEffect effect : players.getActivePotionEffects()) {
					players.removePotionEffect(effect.getType());
				}
				players.sendMessage(Main.ins.prefix + "§cIhr konntet das Schaf nicht retten und habt somit verloren...");
			}
		}
		
		startResetCountDown();
		
	}
	
	public static void spawnFireWork(Location loc) {
		new BukkitRunnable() {
			
			@Override
			public void run() {
				Firework firework = loc.getWorld().spawn(loc, Firework.class);
				FireworkMeta meta = firework.getFireworkMeta();
			    FireworkEffect.Builder builder = FireworkEffect.builder();
			    
			    Random r = Main.ins.utils.getRandom();
			    
			    if(r.nextBoolean()) builder.withTrail();
			    if(r.nextBoolean()) builder.withFlicker();
			    if(r.nextBoolean()) builder.withFade(Color.AQUA);
			    if(r.nextBoolean()) builder.withFade(Color.BLUE);
			    if(r.nextBoolean()) builder.withFade(Color.FUCHSIA);
			    if(r.nextBoolean()) builder.withFade(Color.GREEN);
			    if(r.nextBoolean()) builder.withFade(Color.WHITE);
			    if(r.nextBoolean()) builder.withFade(Color.RED);
			    if(r.nextBoolean()) builder.withFade(Color.YELLOW);
			    if(r.nextBoolean()) builder.withFade(Color.ORANGE);
			    if(r.nextBoolean()) builder.withFade(Color.MAROON);
			    
			    if(r.nextBoolean()) builder.withColor(Color.AQUA);
			    if(r.nextBoolean()) builder.withColor(Color.BLUE);
			    if(r.nextBoolean()) builder.withColor(Color.FUCHSIA);
			    if(r.nextBoolean()) builder.withColor(Color.GREEN);
			    if(r.nextBoolean()) builder.withColor(Color.WHITE);
			    if(r.nextBoolean()) builder.withColor(Color.RED);
			    if(r.nextBoolean()) builder.withColor(Color.YELLOW);
			    if(r.nextBoolean()) builder.withColor(Color.ORANGE);
			    if(r.nextBoolean()) builder.withColor(Color.MAROON);
			    
			    switch (r.nextInt(4)) {
			    	
			    	case 0:
			    		builder.with(FireworkEffect.Type.BALL);
			    		break;
			    	case 1:
			    		builder.with(FireworkEffect.Type.BALL_LARGE);
			    		break;
			    	case 2:
			    		builder.with(FireworkEffect.Type.BURST);
			    		break;
			    	case 3:
			    		builder.with(FireworkEffect.Type.STAR);
			    		break;
			    		
			    	default:
			    		builder.with(FireworkEffect.Type.BALL_LARGE);
			    		break;
			    	
			    }
			    
			    meta.addEffect(builder.build());
			    meta.setPower(r.nextInt(2)+1);
			    firework.setFireworkMeta(meta);
			}
		}.runTask(Main.ins);
	}
	
	
	
	
}
