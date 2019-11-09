package de.JHammer.RDS.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.JHammer.RDS.Main;
import de.JHammer.RDS.Enums.GameState;
import de.JHammer.RDS.Manager.KitMgr;

public class KitCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if(Main.ins.state == GameState.LOBBY) {
			if(sender instanceof Player) {
				KitMgr.openKitInv((Player) sender, null);
			} else {
				sender.sendMessage("§cDu bist kein Spieler");
			}
			
		} else {
			sender.sendMessage("§cDiesen Befehl kannst du nur in der Lobby ausführen!");
		}
		
		return true;
	}

}
