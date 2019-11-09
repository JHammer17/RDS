package de.JHammer.RDS.Static;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import de.JHammer.RDS.Main;
import de.JHammer.RDS.Enums.GameState;
import de.JHammer.RDS.Objects.ScoreboardAPI;

public class ScoreboardMgr {

	public static void updateScoreboard(Player p) {
		
		ScoreboardAPI api = Main.ins.sbAPI;
		
		if(Main.ins.state == GameState.LOBBY) {
			
			api.setTitle(p, "§8» §b§lRette das §f§lSchaf §8«", "Lobby");
			api.setLine(p, "Lobby", "L16", "§8§m", "---------------", "", 16);
			api.setLine(p, "Lobby", "L15", "", "§a", "", 15);
			api.setLine(p, "Lobby", "L14", "§aSpieler §f» ", "§f" + Main.ins.utils.getAlivePlayers().size(), "§7/§f" + Main.ins.maxPlayers, 14);
			api.setLine(p, "Lobby", "L13", "", "§b", "", 13);
			api.setLine(p, "Lobby", "L12", "", "§6Coins §f» ",  "" + Main.ins.getRDSPlayer(p).getCoins(), 12);
			api.setLine(p, "Lobby", "L11", "§d", "§c", "", 11);
			api.setLine(p, "Lobby", "L10", "§8§m", "---------------§a", "", 10);
			
		} else if(Main.ins.state == GameState.INGAME || Main.ins.state == GameState.END) {
			
			api.setTitle(p, "§8» §b§lRette das §f§lSchaf §8«", "Ingame");
			api.setLine(p, "Ingame", "L16", "§8§m", "---------------", "", 16);
			api.setLine(p, "Ingame", "L15", "", "§a", "", 15);
			api.setLine(p, "Ingame", "L14", "§aSpieler §f» ", "§f" + Main.ins.utils.getAlivePlayers().size(), "§7/§f" + Main.ins.maxPlayers, 14);
			api.setLine(p, "Ingame", "L13", "", "§b", "", 13);
			api.setLine(p, "Ingame", "L12", "§cZeit verbleib", "end §f» ", Main.ins.utils.getFormattedTime(Main.ins.maxIngameTime-Counter.getIngameCounter()).replaceAll("/", "§8/§f"), 12);
			api.setLine(p, "Ingame", "L11", "", "§8", "", 11);
			api.setLine(p, "Ingame", "L10", "", "§6Coins §f» ", "" + Main.ins.getRDSPlayer(p).getCoins(), 10);
			api.setLine(p, "Ingame", "L9", "§a", "§c", "", 9);
			api.setLine(p, "Ingame", "L8", "§8§m", "---------------§a", "", 8);
		}
		
		
		
	}
	
	public static void updateAllScoreboards() {
		for(Player players : Bukkit.getOnlinePlayers()) updateScoreboard(players);
	}
	
	public static void autoUpdater()  {
		new BukkitRunnable() {
			
			@Override
			public void run() {
				updateAllScoreboards();
				
			}
		}.runTaskTimer(Main.ins, 0, 20);
	}
	
	public static void removeScoreboard(Player p) {
		Main.ins.sbAPI.remove(p);
	}
	
	
}
