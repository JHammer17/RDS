package de.JHammer.RDS.Listener;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import de.JHammer.RDS.Main;
import de.JHammer.RDS.Enums.GameState;
import de.JHammer.RDS.Enums.Kit;
import de.JHammer.RDS.Manager.MapManager;
import de.JHammer.RDS.Manager.SoundMgr.JSound;

public class IngameEvents implements Listener {

	@EventHandler
	public void onExplode(BlockExplodeEvent e) {
		if(Main.ins.state == GameState.INGAME) {
			e.setCancelled(true);
			e.setYield(0);
			e.blockList().clear();
		}
	}
	
	@EventHandler
	public void onExplode(EntityExplodeEvent e) {
		if(Main.ins.state == GameState.INGAME) {
			
			e.setYield(0);
			e.blockList().clear();
		}
	}
	
	@EventHandler
	public void onTarget(EntityTargetEvent e) {
		if(Main.ins.state == GameState.INGAME) {
			if(e.getTarget() != null && e.getTarget().getType() != EntityType.PLAYER) {
			 e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onDrop(EntityDeathEvent e) {
		
		if(Main.ins.state == GameState.INGAME) {
			e.getDrops().clear();
			e.setDroppedExp(0);
		}
		
	}
	
	
	@EventHandler
	public void onFriendlyFire(EntityDamageByEntityEvent e) {
		
		if(e.getEntity() instanceof IronGolem) {
			
			
			
			if(Main.ins.boss != null && 
			   Main.ins.boss.getGolem().getUniqueId().equals(e.getEntity().getUniqueId())) {
				
				Main.ins.boss.forceAttack(e.getDamager());
				
			}
		}
		
		if(e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
			e.setCancelled(true);
			e.setDamage(0);
		}
		
		
		if(e.getDamager() instanceof Arrow) {
			
			Arrow arrow = (Arrow) e.getDamager();
			if(arrow.getShooter() instanceof Player && e.getEntity() instanceof Player) {
				
				e.setCancelled(true);
				e.setDamage(0);
			}
		}
		
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onSheepDmg(EntityDamageEvent e) {
		if(Main.ins.state == GameState.INGAME) {
			
			
			
			
			if(Main.ins.sheep != null && 
					Main.ins.sheep.getUUID() == e.getEntity().getUniqueId()) {
				e.setCancelled(true);
				e.setDamage(0);
				
				
				
			}
			
			
			
		}
	}
	
	public void tpPlayersToBoss() {
		
		if(Bukkit.getWorld("Arena") != null) {
			Location min = MapManager.getBossRoom(Main.ins.map, true);
			Location max = MapManager.getBossRoom(Main.ins.map, false);
			Location sheepSpawn = MapManager.getSheepSpawn(Main.ins.map);
			if(min == null || max == null || sheepSpawn == null) return;
		
			
			min.setWorld(Bukkit.getWorld("Arena"));
			max.setWorld(Bukkit.getWorld("Arena"));
			sheepSpawn.setWorld(Bukkit.getWorld("Arena"));
			for(Player players : Bukkit.getOnlinePlayers()) {
				
				if(!Main.ins.utils.checkRegion(players.getLocation(), min, max)) {
					players.teleport(sheepSpawn);
				}
				
				
				
			}
			
		}
		
		
		
	}
	
	@EventHandler
	public void onSheepPickup(PlayerInteractAtEntityEvent e) {
		if(Main.ins.state == GameState.INGAME) {
			if(Main.ins.sheep != null && 
					Main.ins.sheep.getUUID() == e.getRightClicked().getUniqueId()) {
				
				e.setCancelled(true);
				
				if(Main.ins.sheep.isRescueAble()) {
					
					
					if(Main.ins.sheep.getCarrier() != null) {
						return;
					}
					
					if(Main.ins.getRDSPlayer(e.getPlayer()).isSpectator()) {
						return;
					}
					
					
					Main.ins.sheep.setCarrier(e.getPlayer());
					
					
					tpPlayersToBoss();
					
					e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 5000*20,1,true,true));
					e.getPlayer().setMaxHealth(40);
					e.getPlayer().setHealth(40);
					
					Main.ins.sheep.removeDroppedArmorStand();
					
					if(Main.ins.entryMgrs.containsKey(5) && Main.ins.entryMgrs.get(5).getArmorStandUUID() == null) {
						Main.ins.sheep.destroyHologram();
						Main.ins.entryMgrs.get(5).spawnDestroyArmorStand();;
						Main.ins.entryMgrs.get(6).fill();
					}
				}
				
				
				
				
				
			}
			
			
			
		}
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onDeath(PlayerDeathEvent e) {
		if(Main.ins.state == GameState.INGAME) {
			e.getEntity().setHealth(20.0);
			e.getEntity().setVelocity(new Vector(0, 0, 0));
			
			Location back = e.getEntity().getLocation();
			
			e.getEntity().teleport(back.clone().add(0,-100,0));
			

			if(e.getEntity().getPassenger() != null &&  e.getEntity().getPassenger().isInsideVehicle()) {
				e.getEntity().eject();
				if(Main.ins.sheep != null) {
					Main.ins.sheep.spawnDroppedArmorStand();
				}
			}
				

			
			
			
			Main.ins.utils.setAsSpectator(e.getEntity());
			
			new BukkitRunnable() {
				
				@Override
				public void run() {
					e.getEntity().teleport(back);
				}
			}.runTaskLater(Main.ins, 3);
			
			
		}
	}
	
	
	@EventHandler
	public void onFoodLvlChange(FoodLevelChangeEvent e) {
		if(Main.ins.state == GameState.INGAME) {
			e.setCancelled(true);
			e.setFoodLevel(20);
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		if(Main.ins.state == GameState.INGAME) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		if(Main.ins.state == GameState.INGAME) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBukkitEmpty(PlayerBucketEmptyEvent e) {
		if(Main.ins.state == GameState.INGAME) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBukkitFill(PlayerBucketFillEvent e) {
		if(Main.ins.state == GameState.INGAME) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onCraft(CraftItemEvent e) {
		if(Main.ins.state == GameState.INGAME) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onDrop(PlayerDropItemEvent e) {
		if(Main.ins.state == GameState.INGAME) {
			e.setCancelled(true);
			
		}
	}
	
	@EventHandler 
	public void onSneak(PlayerToggleSneakEvent e) { 
		if(Main.ins.state == GameState.INGAME) {
			if(Main.ins.getRDSPlayer(e.getPlayer()).getKit() == Kit.SNIPER) {

					if(!e.getPlayer().isSneaking()) {
						e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20*6000, 2, true,  true));
					} else {
						e.getPlayer().removePotionEffect(PotionEffectType.SLOW);;
					}
				
			}
		}
	}
	
	
	@EventHandler
	public void onHearthSteal(EntityDamageByEntityEvent e) {
		if(Main.ins.state == GameState.INGAME) {
			if(e.getEntity() instanceof Player) {
				Player p = (Player)e.getEntity();
				
				if(Main.ins.getRDSPlayer(p).getKit() == Kit.MAGIC) {
					if(p.getInventory().getItemInHand() != null && p.getInventory().getItemInHand().getType() == Material.BLAZE_ROD) {
						
						int chance = Main.ins.utils.getRandom().nextInt(6);
						if(chance == 1 || chance == 2) {
							p.setHealth(p.getHealth()+3);
							Main.ins.utils.getSoundMgr().playSound(p, JSound.LEVEL_UP, 100.0F, 1F);
							
						}
						
					}
				}
			}
		}
		
		
	}
	
	
}
