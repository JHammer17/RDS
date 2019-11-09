package de.JHammer.RDS.Listener;

import java.lang.reflect.Method;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;

import de.JHammer.RDS.Main;

public class HitEntityThroughEntity implements Listener {

	@EventHandler(priority = EventPriority.LOW)
	public void onHit(EntityDamageByEntityEvent e) {
		if(e.getEntity() instanceof Sheep && Main.ins.sheep != null) {
			if(e.getEntity().getUniqueId() == Main.ins.sheep.getUUID()) {
				double finalDamage = e.getFinalDamage();
				
				if(!(e.getDamager() instanceof Player))	return;
				
				e.setCancelled(true);
				e.setDamage(0);
				
				Player p = (Player) e.getDamager();
				
				Location loc = p.getEyeLocation().add(p.getEyeLocation().getDirection());
				int i = 0;
				
				Entity hit = null;
				
				while(loc.getBlock().getType() == Material.AIR && 
						hit == null && i < 2) {
					loc = loc.add(loc.getDirection());
					
					for(Entity en : p.getNearbyEntities(4, 4, 4)) {
						
						if((en.getType() != EntityType.SHEEP || 
						   (en.getType() == EntityType.SHEEP && 
						   !en.getUniqueId().equals(Main.ins.sheep.getUUID()))) && 
								en.getType() != EntityType.PLAYER) {

							
							try {
								
								Object handleEn = en.getClass().getMethod("getHandle").invoke(en);
								
								Method getBoundingBox = handleEn.getClass().getMethod("getBoundingBox");
								
								Object box = getBoundingBox.invoke(handleEn);
										
								double x1 = (double) box.getClass().getField("a").get(box);
								double y1 = (double) box.getClass().getField("b").get(box);
								double z1 = (double) box.getClass().getField("c").get(box);
								
								double x2 = (double) box.getClass().getField("d").get(box);
								double y2 = (double) box.getClass().getField("e").get(box);
								double z2 = (double) box.getClass().getField("f").get(box);
								
								Location loc1 = new Location(p.getWorld(), x1,y1,z1);
								Location loc2 = new Location(p.getWorld(), x2,y2,z2);
								
								if(Main.ins.utils.checkRegion(
										loc, 
										loc1, 
										loc2)) {
									
									if(en instanceof LivingEntity) {
										LivingEntity a = (LivingEntity) en;
										a.damage(finalDamage, p);
										hit = en;
									
										break;
									}
									
									
								}
							} catch (Exception ex) {
								ex.printStackTrace();
							}
						}
					}
					
					if(hit  != null) break;
					i = i+1;
				}
				
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onHit(EntityShootBowEvent e) {
		if(e.getEntity() instanceof Player) {
			if(e.getEntity().getPassenger() != null) {
				Player p = (Player) e.getEntity();
				
				Location loc = p.getEyeLocation().add(p.getEyeLocation().getDirection());
				
				loc.add(p.getLocation().getDirection());
				
				e.getProjectile().teleport(loc);
			}
		}
	}	
	
	
}
