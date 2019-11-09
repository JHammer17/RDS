package de.JHammer.RDS.Static.MySQLMgr;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import de.JHammer.RDS.Main;

public class MySQL {

	public static String username;
	  public static String password;
	  public static String database;
	  public static String host;
	  public static String port;
	  public static Connection con;
	  
	  public static void connect() {
	    if (!isConnected()) {
	      try {
	    	  
	    	  
	        con = DriverManager.getConnection(
	            "jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true", username, 
	            password);
	        Bukkit.getConsoleSender().sendMessage("§fMySQL geladen");
	      }
	      catch (SQLException e) {
	        e.printStackTrace();
	        Bukkit.getConsoleSender().sendMessage("§cMySQL konnte nicht geladen werden...");
	      } 
	    }
	  }
	  
	  public static void reloadConnect() {
	    try {
	      con = DriverManager.getConnection(
	          "jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true", username, password);
	      Bukkit.getConsoleSender().sendMessage("§aMySQL geladen");
	    }
	    catch (SQLException e) {
	      e.printStackTrace();
	      Bukkit.getConsoleSender().sendMessage("§4MySQL konnte nicht geladen werden...");
	    } 
	  }
	  
	  public static void close() {
	    if (isConnected()) {
	      try {
	        con.close();
	        Bukkit.getConsoleSender()
	          .sendMessage("§cDie MySQL Verbindung wurde erfolgreich geschlossen.");
	      } catch (SQLException e) {
	        e.printStackTrace();
	      } 
	    }
	  }
	  
	  public static boolean isConnected() {
		  return con != null;
	  }
	  
	  public static void update(String qry) {
	    try {
	      PreparedStatement ps = con.prepareStatement(qry);
	      ps.executeUpdate(qry);
	      ps.close();
	    } catch (SQLException e) {
	      if(!isConnected()) connect();
	      System.err.println(e);
	    } 
	  }
	  
	  
	  public static void createTables() {
	    if (isConnected()) {
	      try {
	        PreparedStatement ps = con.prepareStatement("CREATE TABLE IF NOT EXISTS RDS("
	        		+ "UUID TEXT,Name TEXT,Wins INT,"
	        		+ "Lost INT, Played INT,"
	        		+ "Sniper Boolean,"
	        		+ "Magic BOOLEAN,"
	        		+ "Healer BOOLEAN,"
	        		+ "Warrior BOOLEAN,"
	        		+ "Tank BOOLEAN"
	        		+ ")");
	        
	        ps.executeUpdate();
	        
	        ps.close();
	    	  
	    	  
	      }
	      catch (SQLException e) {
	        e.printStackTrace();
	      } 
	    }
	  }
	  
	  public static void loadFile() {
	    try {
	   
	    
	      
	    	YamlConfiguration config = Main.ins.utils.getYaml("MySQL");
	      
	        if(config.getConfigurationSection("mysql") == null) {
	        	config.set("mysql.Host", "localhost");
	        	config.set("mysql.Port", "3306");
	        	config.set("mysql.Database", "db");
	        	config.set("mysql.Username", "root");
	        	config.set("mysql.Password", "pw");
	        
	        	config.save(Main.ins.utils.getPluginFile("MySQL"));
	        }
	       
	      host = config.getString("mysql.Host");
	      port = config.getString("mysql.Port");
	      database = config.getString("mysql.Database");
	      username = config.getString("mysql.Username");
	      password = config.getString("mysql.Password");
	    } catch (Exception file) {
	    	file.printStackTrace();
	    }
	  }
	
	
}
