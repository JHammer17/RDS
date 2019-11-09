package de.JHammer.RDS.Objects;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import de.JHammer.RDS.Main;
import de.JHammer.RDS.Manager.MapManager;

public class Boss {

	private Location loc;
	private IronGolem golem;
	private boolean spawned = false;
	private int maxHealth = Main.ins.bossLive;
	
	public Boss(Location loc) {
		this.loc = loc;
	}
	
	public void spawnBoss() {
		this.golem = (IronGolem) loc.getWorld().spawnEntity(loc, EntityType.IRON_GOLEM);
		golem.setMaxHealth(maxHealth);
		golem.setHealth(maxHealth);
		golem.setPlayerCreated(false);
		golem.setRemoveWhenFarAway(false);
		golem.setCustomName(Main.ins.bossName);
		golem.setCustomNameVisible(true);
		golem.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20000*20, 0, true, true));
		spawned = true;
	}
	
	public boolean isSpawned() {
		return spawned;
	}
	
	public void removeBoss() {
		if(golem != null) golem.remove();
	}
	
	public IronGolem getGolem() {
		return golem;
	}
	
	UUID lastAttacked;
	
	
	Player toAttack = null;
	
	public void forceAttack(Entity en)  {
		
		if(en != null) {
			try {
				
				if(en instanceof Player) {
					toAttack = (Player) en;
					lastAttacked = en.getUniqueId();
					setTarget(toAttack);
				}
				
				
			} catch (Exception e) {
				
				e.printStackTrace();
			}
		}
	}
	
	
	public void updateKI() {
		
		if(getGolem() != null && !getGolem().isDead() && getGolem().isValid()) {
			
			
			
			
			
			
			for(Entity en : getGolem().getNearbyEntities(5, 5, 5)) {
				if(en.getType() == EntityType.PLAYER) {
					if(en.getUniqueId() == lastAttacked) {
						
						if(Main.ins.getRDSPlayer((Player)en).isSpectator()) {
							toAttack = null;
							lastAttacked = null;
							continue;
						}
						
						toAttack = (Player) en;
						
						break;
					}
				}
			}
			
			if(toAttack == null) {
				for(Entity en : getGolem().getNearbyEntities(7, 7, 7)) {
					if(en.getType() == EntityType.PLAYER) {
						if(Main.ins.getRDSPlayer((Player)en).isSpectator()) {
							toAttack = null;
							lastAttacked = null;
							continue;
						}
						toAttack = (Player) en;
						
						lastAttacked = en.getUniqueId();
						break;
					}
				}
			}
			
			if(toAttack == null) {
				if(Main.ins.sheep != null && Main.ins.sheep.getCarrier() != null) {
					if(Main.ins.sheep.getCarrier().getType() == EntityType.PLAYER) {
						if(!Main.ins.getRDSPlayer((Player)Main.ins.sheep.getCarrier()).isSpectator())  {
							toAttack = (Player) Main.ins.sheep.getCarrier();
						} else toAttack = null;
						
						
					}
				}
			}
			
			
			setTarget(toAttack);
			
		}
		
		
	}
	
	
	private void setTarget(Player target) {
		try {
			
			Object handleGolem = Main.ins.boss.getGolem().getClass().getMethod("getHandle").invoke(Main.ins.boss.getGolem());
			Object handlePlayer = target.getClass().getMethod("getHandle").invoke(target);
			
			Method m = handleGolem.getClass().getMethod("setGoalTarget",  getNMSClass("EntityLiving") ,TargetReason.class, boolean.class);
			
			m.
			invoke(
					handleGolem, 
					handlePlayer, 
					TargetReason.TARGET_ATTACKED_ENTITY, 
					false);
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	boolean hasSpawned = false;
	
	public boolean hasSpawned() {
		return hasSpawned;
	}
	
	public void setHasSpawned(boolean hasSpawned) {
		this.hasSpawned = hasSpawned;
	}
	
	public void spawnMobs() {
		
		int maxAmount = Main.ins.utils.getRandom().nextInt(16)+15;
		
		ArrayList<EntityType> en = new ArrayList<>();
		
		
		
		
		int zombies = 0;
		if(maxAmount/2 > 0) {
			zombies = Main.ins.utils.getRandom().nextInt(maxAmount/2);
			maxAmount = maxAmount-zombies;
		}
		int skeletons = 0;
		if(maxAmount/2 > 0) {
			skeletons = Main.ins.utils.getRandom().nextInt(maxAmount/2);
			maxAmount = maxAmount-skeletons;
		}
		int creeper = 0;
		if(maxAmount/2 > 0) {
			creeper = Main.ins.utils.getRandom().nextInt(maxAmount/2);
			maxAmount = maxAmount-creeper;
		}
		int spider = 0;
		if(maxAmount/2 > 0) {
			spider = Main.ins.utils.getRandom().nextInt(maxAmount/2);
			maxAmount = maxAmount-spider;
		}
		int blaze = 0;
		if(maxAmount/2 > 0) {
			blaze = Main.ins.utils.getRandom().nextInt(maxAmount/2);
			maxAmount = maxAmount-blaze;
		}
		int caveSpider = 0;
		if(maxAmount/2 > 0) {
			caveSpider = Main.ins.utils.getRandom().nextInt(maxAmount/2);
			maxAmount = maxAmount-caveSpider;
		}
		
		
		
		while(zombies > 0) {
			if(Main.ins.utils.getRandom().nextInt(2) == 0) en.add(EntityType.ZOMBIE);
			zombies--;
		}
		
		while(skeletons > 0) {
			en.add(EntityType.SKELETON);
			skeletons--;
		}
		
		while(creeper > 0) {
			en.add(EntityType.CREEPER);
			creeper--;
		}
		
		while(spider > 0) {
			en.add(EntityType.SPIDER);
			spider--;
		}
		
		while(blaze > 0) {
			en.add(EntityType.BLAZE);
			blaze--;
		}
		
		while(caveSpider > 0) {
			en.add(EntityType.CAVE_SPIDER);
			caveSpider--;
		}
		
		
		
		ArrayList<Location> spawns = MapManager.getAllSpawns(Main.ins.map, 5);
		
		
		
		while(!en.isEmpty()) {
			
			EntityType entity = en.get(Main.ins.utils.getRandom().nextInt(en.size()));
			Location spawn = spawns.get(Main.ins.utils.getRandom().nextInt(spawns.size()));
			
			
			
			
			if(Bukkit.getWorld("Arena") == null) return;
			
			spawn.setWorld(Bukkit.getWorld("Arena"));
			
			Entity spawned = spawn.getWorld().spawnEntity(spawn, entity);
			
			if(spawned.getType() == EntityType.SKELETON) {
				if(Main.ins.utils.getRandom().nextInt(2) == 0) {
					((Skeleton)spawned).setSkeletonType(SkeletonType.WITHER);
					((Skeleton)spawned).getEquipment().setItemInHand(Main.ins.utils.createItem(Material.STONE_SWORD, 0, 1, null, null));
				}
				
			}
			
			MapManager.living.add(spawned);
			
			en.remove(entity);
		}
		
	}
	
	private Class<?> getNMSClass(String name) {
		String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

		try {
			return Class.forName("net.minecraft.server." + version + "." + name);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			
			return null;
		}
	}
	
}
