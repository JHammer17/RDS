package de.JHammer.RDS.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.JHammer.RDS.Main;
import de.JHammer.RDS.Enums.GameState;
import de.JHammer.RDS.Static.Counter;

public class StartCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if(sender instanceof Player) {
			Player p = (Player)sender;
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
		
		
		return true;
	}

}
