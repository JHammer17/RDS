package de.JHammer.RDS.Listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import de.JHammer.RDS.Main;
import de.JHammer.RDS.Enums.GameState;
import de.JHammer.RDS.Manager.MapManager;
import de.JHammer.RDS.Static.ScoreboardMgr;

public class GlobalEvents implements Listener {

	
	@EventHandler
	public void onConnect(PlayerLoginEvent e) {
		if(Main.ins.state == GameState.LOBBY) {
			
			if(Bukkit.getOnlinePlayers().size() >= Main.ins.maxPlayers) {
				e.disallow(null, Main.ins.prefix + "§cDieser Server ist voll");
			}
			
			
		}
		
		if(Main.ins.state == GameState.EDIT) {
			if(!e.getPlayer().hasPermission("rds.joinedit")) {
				e.disallow(null, Main.ins.prefix + "§cDieser Server ist im Editmodus");
			}
		}
		
		if(Main.ins.state == GameState.END) {
				e.disallow(null, Main.ins.prefix + "§cDieser Server ist startet gerade neu!");
		}
	}
	
	@EventHandler(priority=EventPriority.HIGH)
	public void onJoin(PlayerJoinEvent e) {
		
		
		
		
		
		
		Main.ins.addPlayer(e.getPlayer().getUniqueId());
		
		if(Main.ins.state == GameState.LOBBY) {
			e.setJoinMessage(Main.ins.prefix + "§e" + e.getPlayer().getName() + " §7ist dem Spiel beigetreten! §8[§e" + (Main.ins.utils.getAlivePlayers().size()) + "/" + Main.ins.maxPlayers + "§8]");
		} else if(Main.ins.state != GameState.EDIT) {
			e.setJoinMessage(null);
		}
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				
				if(Main.ins.state == GameState.LOBBY) {
					Main.ins.utils.setToLobby(e.getPlayer());
				} else if(Main.ins.state == GameState.INGAME) {
					Main.ins.utils.setAsSpectator(e.getPlayer());
					e.getPlayer().teleport(MapManager.getSpawn(Main.ins.map));
				} 
				
					
			}
		}.runTaskLater(Main.ins, 10);
	}
	
	@EventHandler(priority=EventPriority.HIGH)
	public void onQuit(PlayerQuitEvent e) {
		
		if(Main.ins.state == GameState.LOBBY || !Main.ins.getRDSPlayer(e.getPlayer()).isSpectator()) {
			e.setQuitMessage(Main.ins.prefix + "§e" + e.getPlayer().getName() + " §7hat das Spiel verlassen! §8[§e" + (Main.ins.utils.getAlivePlayers().size()-1) + "/" + Main.ins.maxPlayers + "§8]");
			
		} else {
			e.setQuitMessage(null);
		}
		
		
		Main.ins.removePlayer(e.getPlayer().getUniqueId());
		
		ScoreboardMgr.removeScoreboard(e.getPlayer());
		
	}
	
	@EventHandler
	public void onDrop(CreatureSpawnEvent e) {
		if(e.getEntityType() != EntityType.PLAYER) {
			if(Main.ins.state != GameState.EDIT && e.getSpawnReason() != SpawnReason.CUSTOM) {
				e.setCancelled(true);
			}
		}
		
	}
	
	@EventHandler
	public void onDrop(SpawnerSpawnEvent e) {
		
		if(Main.ins.state != GameState.EDIT) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onWLoad(WorldInitEvent e) {
		if(e.getWorld().getName().equalsIgnoreCase("Arena")) {
			e.getWorld().setKeepSpawnInMemory(false);
		}
	}
	
}
