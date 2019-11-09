package de.JHammer.RDS.Listener;


import org.bukkit.entity.EntityType;
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
import org.bukkit.util.Vector;

import de.JHammer.RDS.Main;
import de.JHammer.RDS.Enums.GameState;

public class EndGameListener implements Listener {

	@EventHandler
	public void onExplode(BlockExplodeEvent e) {
		if(Main.ins.state == GameState.END) {
			e.setCancelled(true);
			e.setYield(0);
			e.blockList().clear();
		}
	}
	
	@EventHandler
	public void onExplode(EntityExplodeEvent e) {
		if(Main.ins.state == GameState.END) {
			
			e.setYield(0);
			e.blockList().clear();
		}
	}
	
	@EventHandler
	public void onTarget(EntityTargetEvent e) {
		if(Main.ins.state == GameState.END) {
			if(e.getTarget() != null && e.getTarget().getType() != EntityType.PLAYER) {
			 e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onDrop(EntityDeathEvent e) {
		
		if(Main.ins.state == GameState.END) {
			e.getDrops().clear();
			e.setDroppedExp(0);
		}
		
	}
	
	
	@EventHandler
	public void onFriendlyFire(EntityDamageByEntityEvent e) {
		if(Main.ins.state == GameState.END) {
			e.setCancelled(true);
			e.setDamage(0);
		}
		
	}
	
	@EventHandler
	public void onSheepDmg(EntityDamageEvent e) {
		if(Main.ins.state == GameState.END) {
			
			e.setCancelled(true);
			e.setDamage(0);
			
		}
	}
	
	
	
	@EventHandler
	public void onSheepDmg(PlayerInteractAtEntityEvent e) {
		if(Main.ins.state == GameState.END) {
			e.setCancelled(true);
			
		}
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onDeath(PlayerDeathEvent e) {
		if(Main.ins.state == GameState.END) {
			e.getEntity().setHealth(20.0);
			e.getEntity().setVelocity(new Vector(0, 0, 0));
			
			
		}
	}
	
	
	@EventHandler
	public void onFoodLvlChange(FoodLevelChangeEvent e) {
		if(Main.ins.state == GameState.END) {
			e.setCancelled(true);
			e.setFoodLevel(20);
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		if(Main.ins.state == GameState.END) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		if(Main.ins.state == GameState.END) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBukkitEmpty(PlayerBucketEmptyEvent e) {
		if(Main.ins.state == GameState.END) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBukkitFill(PlayerBucketFillEvent e) {
		if(Main.ins.state == GameState.END) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onCraft(CraftItemEvent e) {
		if(Main.ins.state == GameState.END) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onDrop(PlayerDropItemEvent e) {
		if(Main.ins.state == GameState.END) {
			e.setCancelled(true);
			
		}
	}
	
	
}
