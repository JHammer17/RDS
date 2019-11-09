package de.JHammer.RDS.Listener;


import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import de.JHammer.RDS.Main;
import de.JHammer.RDS.Objects.BlockerMgr;


public class DestroyStandListener implements Listener {

	
	@EventHandler
	public void onDestroy(EntityDamageByEntityEvent e) {
		if(e.getEntity() instanceof ArmorStand) {
			int entry = -1;
			
			if(e.getDamager() instanceof Player) {
				if(Main.ins.getRDSPlayer((Player) e.getDamager()).isSpectator()) {
					e.setCancelled(true);
					e.setDamage(0);
					return;
				}
			}
			
			
			for(BlockerMgr mgr : Main.ins.entryMgrs.values()) {
				if(mgr.getArmorStandUUID() == e.getEntity().getUniqueId()) {
					e.setCancelled(true);
					
					mgr.destroyMine();
					mgr.removeDestroyArmorStand();
					mgr.spawnNextWave();
					
					
					entry = mgr.getNumber();
					
					break;
				}
				
				
			}
			
			if(entry != -1) Main.ins.entryMgrs.remove(entry);
		}
		
		
	}
	
	
	

	
	
}
