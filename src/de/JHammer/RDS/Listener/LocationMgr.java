package de.JHammer.RDS.Listener;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import de.JHammer.RDS.Main;
import de.JHammer.RDS.Enums.GameState;

public class LocationMgr implements Listener {

	@EventHandler
	public void onClick(PlayerInteractEvent e) {
		if(e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_BLOCK) {
			if(e.getItem() != null) {
				
				if(Main.ins.state == GameState.EDIT && e.getItem().getType() == Material.GOLD_AXE) {
					e.setCancelled(true);
					
					if(e.getAction() == Action.LEFT_CLICK_BLOCK) {
						Main.ins.getRDSPlayer(e.getPlayer()).setPos1(e.getClickedBlock().getLocation());
						e.getPlayer().sendMessage(Main.ins.prefix + "§aPosition 1 gesetzt!");
					} else {
						Main.ins.getRDSPlayer(e.getPlayer()).setPos2(e.getClickedBlock().getLocation());
						e.getPlayer().sendMessage(Main.ins.prefix + "§aPosition 2 gesetzt!");
					}
					
					
				}
				
				
			}
		}
	}
	
	
}
