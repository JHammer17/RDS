package de.JHammer.RDS.Listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

import de.JHammer.RDS.Main;
import de.JHammer.RDS.Enums.GameState;


public class LobbyEvents implements Listener {

	
	@EventHandler
	public void onDmg(EntityDamageEvent e) {
		if(Main.ins.state == GameState.LOBBY) {
			e.setCancelled(true);
			e.setDamage(0.0);
		}
	}
	
	@EventHandler
	public void onFoodLvlChange(FoodLevelChangeEvent e) {
		if(Main.ins.state == GameState.LOBBY) {
			e.setCancelled(true);
			e.setFoodLevel(20);
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		if(Main.ins.state == GameState.LOBBY) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		if(Main.ins.state == GameState.LOBBY) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBukkitEmpty(PlayerBucketEmptyEvent e) {
		if(Main.ins.state == GameState.LOBBY) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBukkitFill(PlayerBucketFillEvent e) {
		if(Main.ins.state == GameState.LOBBY) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onCraft(CraftItemEvent e) {
		if(Main.ins.state == GameState.LOBBY) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onDrop(PlayerDropItemEvent e) {
		if(Main.ins.state == GameState.LOBBY) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onMove(InventoryClickEvent e) {
		if(Main.ins.state == GameState.LOBBY) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onTarget(EntityTargetEvent e) {
		if(Main.ins.state == GameState.LOBBY) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onTarget(EntityExplodeEvent e) {
		if(Main.ins.state == GameState.LOBBY) {
			e.setCancelled(true);
		}
	}
	
}
