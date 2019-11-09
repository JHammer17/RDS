package de.JHammer.RDS.Objects;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.spigotmc.AsyncCatcher;

/**
 * ######################################################
 * # @author JHammer17								    #
 * # Erstellt am 29.03.2018 21:03:59					#
 * #												   	#
 * # Alle Ihhalte dieser Klasse dürfen					#
 * # frei verwendet oder verbreitet werden.				#
 * # Es wird keine Zustimmung von JHammer17 benötigt.	#
 * #													#
 * ######################################################
*/
public class ScoreboardAPI {

	/*List for Boards*/
	private HashMap<UUID, Scoreboard> sboard = new HashMap<>();
	
	/**
	 * 
	 * @param player
	 * @return If player has a scoreboard or not
	 */
	public boolean hasScoreboard(Player player) {
		return sboard.containsKey(player.getUniqueId()) && (player.getScoreboard() != null);
	}
	
	/**
	 * 
	 * Creates a empty Scoreboard for the Player 
	 * 
	 * @param p 
	 * 
	 */
	public void create(Player p) {
		AsyncCatcher.enabled = false;
		sboard.remove(p.getUniqueId());
		
		Scoreboard sb = Bukkit.getScoreboardManager().getNewScoreboard();
		
		sboard.put(p.getUniqueId(), sb);
		
		p.setScoreboard(sb);
		AsyncCatcher.enabled = true;
	}
	
	/**
	 * Removes a Scoreboard for a Player
	 * 
	 * @param p
	 */
	public void remove(Player p) {
		AsyncCatcher.enabled = false;
		sboard.remove(p.getUniqueId());
		p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
		AsyncCatcher.enabled = true;
	}
	
	/**
	 * 
	 * @param p
	 * @return Bukkit Scoreboard Obj from a Player
	 */
	public Scoreboard getSB(Player p) {
		if(!hasScoreboard(p)) create(p);
		return sboard.get(p.getUniqueId());
	}
	
	/**
	 * 
	 * Sets the Title of a Scoreboard
	 * 
	 * @param p
	 * @param title
	 * @param objectiveName
	 */
	public void setTitle(Player p, String title, String objectiveName) {
		
		if(!hasScoreboard(p)) {
			create(p);
			setTitle(p, title, objectiveName);
			return;
		}
		
		Objective obj = getSB(p).getObjective(objectiveName);
		
		if(obj == null) {
			getSB(p).registerNewObjective(objectiveName, "dummy");
			getSB(p).getObjective(objectiveName).setDisplaySlot(DisplaySlot.SIDEBAR);
			setTitle(p, title, objectiveName);
			return;
		}
		
		obj.setDisplayName(title);
	}
	
	/**
	 * 
	 * Sets a Line on the Scoreboard
	 * 
	 * @param p
	 * @param objectiveName
	 * @param lineName
	 * @param first
	 * @param middle
	 * @param last
	 * @param points
	 */
	public void setLine(Player p, String objectiveName, String lineName, String first, String middle, String last, int points) {
		if(!hasScoreboard(p)) {
			create(p);
			setLine(p, objectiveName, lineName, first, middle, last, points);
			return;
		}
		
		Objective obj = getSB(p).getObjective(objectiveName);
		
		if(obj == null) {
			getSB(p).registerNewObjective(objectiveName, "dummy");
			getSB(p).getObjective(objectiveName).setDisplaySlot(DisplaySlot.SIDEBAR);
			setLine(p, objectiveName, lineName, first, middle, last, points);
			return;
		}
		
		if(getSB(p).getTeam(lineName) == null && getSB(p).getEntryTeam(lineName) == null) {
			getSB(p).registerNewTeam(lineName);
			setLine(p, objectiveName, lineName, first, middle, last, points);
			return;
		}
		
		Team t = getSB(p).getTeam(lineName);
		
		if(t == null) {
			getSB(p).registerNewTeam(lineName);
			setLine(p, objectiveName, lineName, first, middle, last, points);
			return;
		}
		
		
		boolean cEn = false;
		for(String en : getSB(p).getTeam(lineName).getEntries()) {
			if(en.equalsIgnoreCase(middle)) {
				cEn = true;
				break;
			}
			if(getSB(p).getTeam(lineName).hasEntry(en)) {
				getSB(p).getTeam(lineName).removeEntry(en);
				getSB(p).resetScores(en);
			}
		}
		
		t.setPrefix(first);
		t.setSuffix(last);
		if(!cEn) t.addEntry(middle);
		
		obj.getScore(middle).setScore(points);
		
	}
	
	/**
	 * 
	 * Removes a line on the Scoreboard
	 * 
	 * @param p
	 * @param objectiveName
	 * @param lineName
	 */
	public void removeLine(Player p, String objectiveName, String lineName) {
		if(getSB(p).getTeam(lineName) != null) {
			for(String en : getSB(p).getTeam(lineName).getEntries()) {
				if(getSB(p).getTeam(lineName) != null && getSB(p).getTeam(lineName).hasEntry(en)) {
					getSB(p).getTeam(lineName).removeEntry(en);
					getSB(p).resetScores(en);
				}
			}
		}
		
	}
	
	/**
	 * 
	 * @param player
	 * @param objName
	 * @param team
	 * @return if team exists or not
	 */
	public boolean isTeamExists(Player player, String objName, String team) {
		Scoreboard board = sboard.get(player.getUniqueId());
		
		if(board != null && board.getTeam(team) != null) return true;
		return false;
	}
	
	/**
	 * 
	 * @param player
	 * @param objName
	 * @param name
	 * @return score of a Line
	 */
	public int getScore(Player player, String objName, String name) {
		Objective obj = getSB(player).getObjective(objName);
		
		if(name.length() > 16) name = name.substring(0, 16);
		
		if(getSB(player).getTeam(name) != null) return obj.getScore(name).getScore();
		return 0;
	}
	
	/**
	 * 
	 * @param player
	 * @param objName
	 * @param name
	 * @return prefix of a Line
	 */
	public String getFirst(Player player, String objName, String name) {
		Scoreboard board = getSB(player);
		
		if(name.length() > 16) name = name.substring(0, 16);
		if(board.getTeam(name) != null) return board.getTeam(name).getPrefix();
		
		return "";
	}
	
	/**
	 * 
	 * @param player
	 * @param objName
	 * @param name
	 * @return Suffix of a Line
	 */
	public String getLast(Player player, String objName, String name) {
		Scoreboard board = getSB(player);
		
		if(name.length() > 16) name = name.substring(0, 16);
		
		if(board.getTeam(name) != null) return board.getTeam(name).getSuffix();
		return "";
	}
	
	/**
	 * 
	 * @param player
	 * @param objName
	 * @param name
	 * @return if line exists
	 */
	public boolean isLineExists(Player player, String objName, String name) {
		if(name.length() > 16) name = name.substring(0, 16);
		return (getSB(player).getTeam(name) != null);
	}
	
	/**
	 * 
	 * Sets Prefix and Suffix for a Player
	 * 
	 * @param player
	 * @param objName
	 * @param teamName
	 * @param prefix
	 * @param suffix
	 */
	public void setTablist(Player player, String objName, String teamName, String prefix, String target, String suffix) {
		if(!hasScoreboard(player)) {
			create(player);
			setTablist(player, objName, teamName, prefix, target, suffix);
			return;
		}
		
		Objective obj = getSB(player).getObjective(objName);
		
		if(obj == null) {
			getSB(player).registerNewObjective(objName, "dummy");
			getSB(player).getObjective(objName).setDisplaySlot(DisplaySlot.SIDEBAR);
			setTablist(player, objName, teamName, prefix, target, suffix);
			return;
		}
		
		if(getSB(player).getTeam(teamName) == null) {
			getSB(player).registerNewTeam(teamName);
			setTablist(player, objName, teamName, prefix, target, suffix);
			return;
		}
		
		Team t = getSB(player).getTeam(teamName);
		
		if(t == null) {
			getSB(player).registerNewTeam(teamName);
			setTablist(player, objName, teamName, prefix, suffix, target);
			return;
		}
		
		
		t.setPrefix(prefix);
		t.setSuffix(suffix);
		if(!t.hasEntry(target)) t.addEntry(target);
		
		
	}
	
	/**
	 * 
	 * Removes Player from Team when you know the team name
	 * 
	 * @param player
	 * @param objName
	 * @param teamName
	 */
	public void removeTablist(Player player, String objName, String teamName, String target) {
		if(!hasScoreboard(player)) {
			create(player);
			removeTablist(player, objName, teamName, target);
			return;
		}
		
		Objective obj = getSB(player).getObjective(objName);
		
		if(obj == null) {
			getSB(player).registerNewObjective(objName, "dummy");
			getSB(player).getObjective(objName).setDisplaySlot(DisplaySlot.SIDEBAR);
			removeTablist(player, objName, teamName, target);
			return;
		}
		
		if(getSB(player).getTeam(teamName) == null) {
			getSB(player).registerNewTeam(teamName);
			removeTablist(player, objName, teamName, target);
			return;
		}
		
		Team t = getSB(player).getTeam(teamName);
		
		if(t == null) {
			getSB(player).registerNewTeam(teamName);
			removeTablist(player, objName, teamName, target);
			return;
		}
		
		if(!t.hasEntry(target)) t.removeEntry(target);
	}
	
	/**
	 * 
	 * Removes Player from all Teams
	 * 
	 * @param player
	 * @param objName
	 */
	public void removeTablist(Player player, String objName, String target) {
		if(!hasScoreboard(player)) {
			create(player);
			removeTablist(player, objName, target);
			return;
		}
		
		Objective obj = getSB(player).getObjective(objName);
		
		if(obj == null) {
			getSB(player).registerNewObjective(objName, "dummy");
			getSB(player).getObjective(objName).setDisplaySlot(DisplaySlot.SIDEBAR);
			removeTablist(player, objName, target);
			return;
		}
		
		for(Team teams : getSB(player).getTeams()) 
			if(teams.hasEntry(target)) teams.removeEntry(target);
		
	}
}
