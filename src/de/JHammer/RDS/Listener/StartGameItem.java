package de.JHammer.RDS.Listener;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import de.JHammer.RDS.Main;
import de.JHammer.RDS.Enums.GameState;
import de.JHammer.RDS.Static.Counter;

public class StartGameItem implements Listener {

	@EventHandler
	public void onStartGame(PlayerInteractEvent e)  {
		if(Main.ins.state == GameState.LOBBY) {
			
				if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
					if(e.getItem() != null && e.getItem().getType() == Material.FEATHER) {
						Player p = e.getPlayer();
						
						if(p.hasPermission("RDS.start")) {
							
							if(Counter.getStartCounter() > 10 && Main.ins.state == GameState.LOBBY) {
								Counter.setForceStart(true);
								Counter.setStartCounter(10);
								p.sendMessage(Main.ins.prefix + "§aDas Spiel startet in kürze!");
							} else {
								p.sendMessage(Main.ins.prefix + "§cDas Spiel wird bereits gestartet!");
							}
							
							
						}
					}
				
			}
		}
	}
	
	
}
