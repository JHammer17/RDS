package de.JHammer.RDS.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.JHammer.RDS.Main;
import de.JHammer.RDS.Manager.MapVotingMgr;
import de.JHammer.RDS.Manager.SoundMgr.JSound;
import de.JHammer.RDS.Static.Counter;

public class VoteCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player)sender;
			if(Counter.getStartCounter() > 10) {
				MapVotingMgr.openVotesMenu(p);
				Main.ins.utils.getSoundMgr().playSound(p, JSound.ITEM_PICKUP, 100, 2);
			} else {
				Main.ins.utils.getSoundMgr().playSound(p, JSound.ITEM_BREAK, 100, (float) 0.5);
				p.sendMessage(Main.ins.prefix + "§cDas Voting ist vorbei!");
			}
		}
		return true;
	}

}
