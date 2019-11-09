package de.JHammer.RDS.Objects;

import java.util.UUID;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;



import de.JHammer.RDS.Main;
import de.JHammer.RDS.Enums.GameState;
import de.JHammer.RDS.Static.Counter;

public class RescueSheep {

	private Location loc;
	private Sheep sheep;
	private BukkitRunnable teleporter;
	private ArmorStand info;
	private boolean rescueAble = false;
	
	public RescueSheep(Location loc) {
		this.loc = loc;
	}
	
	public void spawnSheep() {
		sheep = (Sheep) loc.getWorld().spawnEntity(loc, EntityType.SHEEP);
		sheep.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 5000*20, 255, true, true));
		sheep.setAdult();
		sheep.setRemoveWhenFarAway(false);
		sheep.setColor(DyeColor.WHITE);
		
		startTeleportBack();
		
	}
	
	public void startTeleportBack() {
		teleporter = new BukkitRunnable() {
			
			@Override
			public void run() {
				
				if(Main.ins.state != GameState.INGAME) {
					stopTeleportBack();
					return;
				}
				
				if(sheep != null && 
						sheep.isValid() && 
						!sheep.isInsideVehicle() && 
						!sheep.isDead()) {
					if(!sheepDropped) sheep.teleport(loc.clone().add(0,-0.5,0));
					sheep.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 5000*20, 255, true, true));
					
					sheep.setRemoveWhenFarAway(false);
				}
				
			}
		};
		
		teleporter.runTaskTimer(Main.ins, 0, 20*3);
	}
	
	public void stopTeleportBack() {
		teleporter.cancel();
	}
	
	public UUID getUUID() {
		return sheep.getUniqueId();
	}
	
	public void spawnHologram() {
		info = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
		info.setCustomName("§8» §9Klicken zum Befreien §8«");
		info.setBasePlate(false);
		info.setGravity(false);
		info.setCustomNameVisible(true);
		info.setVisible(false);
		info.setSmall(true);
	}
	
	public void destroyHologram() {
		info.remove();
	}
	
	public boolean isRescueAble() {
		
		if(getCarrier() != null) return false;
		
		return this.rescueAble;
	}
	
	public void setResueAble(boolean ra) {
		this.rescueAble = ra;
	}
	
	 public void destroySheep() {
		 if(sheep.isInsideVehicle()) {
			 if(sheep.getVehicle().getType() != EntityType.PLAYER) {
				 sheep.getVehicle().remove();
			 }
		 }
		 sheep.remove();
	 }
	
	 public Player getCarrier() {
		 if(sheep != null) {
			 if(sheep.getVehicle() != null && sheep.getVehicle().isValid() &&
			    sheep.getVehicle().isValid() && sheep.getVehicle() instanceof Player) {
				 return (Player) sheep.getVehicle(); 
			 }
				 
		 }
		 return null;
	 }
	 
	 
	
	 
	 
	 public void setCarrier(Player p) {
		 
		 if(sheep.getVehicle() == null) {
			                          
			 p.setPassenger(sheep);
		 }     
	 } 
	
			 
			 
		 
		 
	 
	 
	 public void unCarrie() {
		 
		 if(sheep != null && sheep.isInsideVehicle()) {
			 if(sheep.getVehicle().isValid() && sheep.getVehicle() instanceof ArmorStand) sheep.getVehicle().remove();
			 sheep.leaveVehicle();
		 }
	 }
	 
	 public void removePotionEffect() {
		 for(PotionEffect effect : sheep.getActivePotionEffects()) {
			 sheep.removePotionEffect(effect.getType());
		 }
	 }
	 
	 public void die() {
		if(sheep != null) sheep.setHealth(0);
		removeDroppedArmorStand();
	 }
	 
	 int droppedTime = 0;
	 boolean sheepDropped = false;
	 ArmorStand droppedStand;
	 ArmorStand droppedStandBar;
	 
	 public void spawnDroppedArmorStand() {
		 
		 
				droppedTime = 0;
				 droppedStand = (ArmorStand) sheep.getWorld().spawn(sheep.getLocation().clone().add(0,0.75,0), ArmorStand.class);
				 droppedStand.setCustomName("§c" + (Main.ins.sheepMaxDropSeconds-droppedTime) + " Sekunden");
				 droppedStand.setCustomNameVisible(true);
				 droppedStand.setVisible(false);
				 droppedStand.setSmall(false);
				 
				 droppedStandBar = (ArmorStand) sheep.getWorld().spawn(sheep.getLocation(), ArmorStand.class);
				 droppedStandBar.setCustomName("§8[" + Main.ins.utils.colorByPercent((Main.ins.sheepMaxDropSeconds-droppedTime), Main.ins.sheepMaxDropSeconds, "||||||||||||||||||||", "§a", "§c") +"§8]");
				 droppedStandBar.setCustomNameVisible(true);
				 droppedStandBar.setVisible(false);
				 droppedStandBar.setSmall(true);
				 
				 
				 sheepDropped = true;
				 updateDroppedStand();
				 
				 new BukkitRunnable() {
					
					@Override
					public void run() {
						droppedStand.setGravity(false);
						droppedStand.setSmall(true);
						droppedStand.teleport(droppedStand.getLocation().add(0,0.22,0));
					}
				}.runTaskLater(Main.ins, 20);
				 
			
	 }
	 
	 BukkitRunnable droppedTimer;
	 
	 public void updateDroppedStand() {
		 droppedTimer = new BukkitRunnable() {
			
			@Override
			public void run() {
				droppedTime++;
				
				
				
				
				if(droppedStand != null && droppedStand.isValid()) {
					
					if((Main.ins.sheepMaxDropSeconds-droppedTime) == 1) {
						droppedStand.setCustomName("§ceine Sekunde");
					} else {
						droppedStand.setCustomName("§c" + (Main.ins.sheepMaxDropSeconds-droppedTime) + " Sekunden");
					}
					if(droppedStandBar != null && droppedStandBar.isValid()) droppedStandBar.setCustomName("§8[" + Main.ins.utils.colorByPercent((Main.ins.sheepMaxDropSeconds-droppedTime), Main.ins.sheepMaxDropSeconds, "||||||||||||||||||||", "§c", "§a") +"§8]");
					 
					if(sheep != null && sheep.isValid()) {
						sheep.teleport(droppedStand.getLocation());
					}
				           
				}
				
				if(droppedTime >= Main.ins.sheepMaxDropSeconds) {
					removeDroppedArmorStand();
					Counter.endGame(false);
					
					return;
				}
				
				
			}
		};
		droppedTimer.runTaskTimer(Main.ins, 0, 20);
	 }
	 
	
	 
	 public void removeDroppedArmorStand() {
		 if(droppedStand != null) droppedStand.remove();
		 if(droppedStandBar != null) droppedStandBar.remove();
		 droppedTime = 0;
		 if(droppedTimer != null) 
			 droppedTimer.cancel();
		 droppedTimer = null;
		 
			 
		 
	 }
	 
	 public Location getLocation() {
		 if(sheep != null) return sheep.getLocation();
		 return null;
	 }
	
	 
	 
}
