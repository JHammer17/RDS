package de.JHammer.RDS.Static.MySQLMgr;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import de.JHammer.RDS.Main;
import de.JHammer.RDS.Enums.Kit;

public class MySQLMgr implements Listener {

	@EventHandler
	public void onJoin(PlayerJoinEvent  e) {
		updatePlayer(e.getPlayer());
	}
	
	public static void updatePlayer(Player  p) {
		if(p == null) return;
		if(!MySQL.isConnected()) return;
		new BukkitRunnable() {
			
			@Override
			public void run() {
				if(!isPlayerRegistered(p.getUniqueId())) 
					addPlayer(p.getUniqueId(), p.getName());
				
				updateName(p.getUniqueId(), p.getName());
			}
		}.runTaskAsynchronously(Main.ins);
	}
	
	
	public static boolean isPlayerRegistered(UUID uuid) {
	    	if(uuid == null) return false;
	    	if(!MySQL.isConnected()) return false;
	    	try {
				PreparedStatement ps = MySQL.con.prepareStatement("SELECT Name FROM RDS WHERE UUID = ?");
				ps.setString(1, uuid.toString());
				ResultSet rs = ps.executeQuery();
				boolean exists = rs.next();
				
				rs.close();
				ps.close();
				
				return exists;
			} catch (SQLException e) {
				return false;
			}
	}
	
	public static void addPlayer(UUID uuid, String name) {
		if(uuid == null || name == null) return;
		if(!MySQL.isConnected()) return;
		try {
			PreparedStatement ps = MySQL.con.prepareStatement(
					"INSERT INTO RDS"
					+ "(UUID"
					+ ",Name"
					+ ",Wins"
					+ ",Lost"
					+ ",Played"
					+ ",Sniper"
					+ ",Magic"
					+ ",Healer"
					+ ",Warrior"
					+ ",Tank) "
					+ "VALUES("
					+ "'" + uuid.toString() + "','" + name +"'"
					+ ",0,0,0,"
					+ "false,false,false,false,false)");
			
			ps.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	public static UUID getUUID(String name) {
		if(name == null) return null;
		if(!MySQL.isConnected()) return null;
		try {
			PreparedStatement ps = MySQL.con.prepareStatement("SELECT UUID FROM RDS WHERE Name='" + name +"'");
			ResultSet rs = ps.executeQuery();
			rs.next();
			try {
				UUID uuid = UUID.fromString(rs.getString("UUID"));
				rs.close();
				ps.close();
				return uuid;
			} catch (Exception e) {
				rs.close();
				ps.close();
				return null;
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
		
	}
	
	public static String getName(UUID uuid) {
		if(uuid == null) return "";
		if(!MySQL.isConnected()) return "";
		try {
			PreparedStatement ps = MySQL.con.prepareStatement("SELECT Name FROM RDS WHERE UUID='" + uuid.toString() +"'");
			ResultSet rs = ps.executeQuery();
			rs.next();
			
			String name = rs.getString("Name");
			rs.close();
			ps.close();
			return name;
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
		
	}
	
	public static void updateName(UUID uuid, String name) {
		if(uuid == null || name == null) return;
		if(!MySQL.isConnected()) return;
		try {
			PreparedStatement ps = MySQL.con.prepareStatement("UPDATE RDS SET Name='" + name +"' WHERE UUID='" + uuid.toString()+"'");
			ps.executeUpdate();	
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void setStat(UUID uuid, int points, Stats stat) {
		if(uuid != null && stat != null) {
			if(!MySQL.isConnected()) return;
			try {
				
				PreparedStatement ps = MySQL.con.prepareStatement("UPDATE RDS SET " + stat.getName() + "=" + points + " WHERE UUID='"  + uuid.toString() +  "'");
				
				ps.executeUpdate();
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static int getStat(UUID uuid, Stats stat) {
    	if(uuid == null || stat == null) return 0;
    	if(!MySQL.isConnected()) return 0;
    	try {
			PreparedStatement ps = MySQL.con.prepareStatement("SELECT " + stat.getName() +" FROM RDS WHERE UUID = ?");
			ps.setString(1, uuid.toString());
			ResultSet rs = ps.executeQuery();
			rs.next();
			int value = rs.getInt(stat.getName());
			
			rs.close();
			ps.close();
			

			
			return value;
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	public static void setBuyed(UUID uuid, Kit kit, boolean buyed) {
		if(uuid != null && kit != null) {
			if(!MySQL.isConnected()) return;
			try {
				
				PreparedStatement ps = MySQL.con.prepareStatement("UPDATE RDS SET " + kit.getMySQLName() + "=" + buyed + " WHERE UUID='"  + uuid.toString() +  "'");
				
				ps.executeUpdate();
				ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static boolean hasBuyed(UUID uuid, Kit kit) {
    	if(uuid == null || kit == null) return false;
    	
    	if(kit == Kit.STARTER) return true;
    	if(!MySQL.isConnected()) return false;
    	try {
    		
    		
    		
			PreparedStatement ps = MySQL.con.prepareStatement("SELECT " + kit.getMySQLName() +" FROM RDS WHERE UUID = ?");
			ps.setString(1, uuid.toString());
			ResultSet rs = ps.executeQuery();
			rs.next();
			boolean value = rs.getBoolean(kit.getMySQLName());
			
			rs.close();
			ps.close();
			

			
			return value;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	
	
	
	public enum Stats {
		
		WINS("Wins"),
		LOST("Lost"),
		PLAYED("Played");
		
		private String name;

		Stats(String name) {
			this.name = name;
		}
		
		public String getName() {
			return this.name;
		}
		
	}
	
}
