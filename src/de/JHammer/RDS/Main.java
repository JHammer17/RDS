package de.JHammer.RDS;


import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;


import de.JHammer.RDS.Commands.KitCommand;
import de.JHammer.RDS.Commands.MainCommand;
import de.JHammer.RDS.Commands.StartCommand;
import de.JHammer.RDS.Commands.VoteCommand;
import de.JHammer.RDS.Enums.GameState;
import de.JHammer.RDS.Listener.DestroyStandListener;
import de.JHammer.RDS.Listener.EndGameListener;
import de.JHammer.RDS.Listener.GlobalEvents;
import de.JHammer.RDS.Listener.HitEntityThroughEntity;
import de.JHammer.RDS.Listener.IngameEvents;
import de.JHammer.RDS.Listener.LobbyEvents;
import de.JHammer.RDS.Listener.LocationMgr;
import de.JHammer.RDS.Listener.SpectatorListener;
import de.JHammer.RDS.Listener.StartGameItem;
import de.JHammer.RDS.Manager.KitBuyMgr;
import de.JHammer.RDS.Manager.KitMgr;
import de.JHammer.RDS.Manager.MapManager;
import de.JHammer.RDS.Manager.MapVotingMgr;
import de.JHammer.RDS.Objects.Boss;
import de.JHammer.RDS.Objects.BlockerMgr;
import de.JHammer.RDS.Objects.RDSPlayer;
import de.JHammer.RDS.Objects.RescueSheep;
import de.JHammer.RDS.Objects.ScoreboardAPI;
import de.JHammer.RDS.Objects.Utils;
import de.JHammer.RDS.Static.Counter;
import de.JHammer.RDS.Static.ScoreboardMgr;
import de.JHammer.RDS.Static.MySQLMgr.MySQL;
import de.JHammer.RDS.Static.MySQLMgr.MySQLMgr;
import net.milkbowl.vault.economy.Economy;

/**
 * ######################################################
 * # @author JHammer17								    #
 * # Erstellt am 16.10.2019 21:03:59					#
 * #												   	#
 * # Alle Ihhalte dieser Klasse dürfen					#
 * # frei verwendet oder verbreitet werden.				#
 * # Es wird keine Zustimmung von JHammer17 benötigt.	#
 * #													#
 * ######################################################
*/
public class Main extends JavaPlugin implements Listener {

	public static Main ins;
	
	public Economy economy = null;
	
	public Utils utils;
	public String prefix = "§7§lRette das §f§lSchaf§r §8» §7";
	public ScoreboardAPI sbAPI = new ScoreboardAPI();
	HashMap<UUID, RDSPlayer> rdsPlayers = new HashMap<UUID, RDSPlayer>();
	    
	public int maxPlayers = 5;
	public int minPlayers = 2;
	
	
	public GameState state = GameState.LOBBY;
	public String map;
	public int maxIngameTime = 600;
	public int currentWave = 0;
	
	public RescueSheep sheep;
	public Boss boss;
	
	public HashMap<Integer, BlockerMgr> entryMgrs = new HashMap<>();
	
	public int startTimer = 60;
	public String bossName = "§7§lFerrum";
	public int bossLive = 1000;
	public int sheepMaxDropSeconds = 15;
	
	public boolean moneyEnabled = true;
	public int moneyWinAlive = 15;
	public int moneyWinDeath = 10;
	
	
	
	public void resetAllData() {
		
		
		Bukkit.getServer().shutdown();
		
	}
	
	
	@Override
	public void onEnable() {
		ins = this;
		
//		getServer().getPluginManager().registerEvents(this, this);
		
		
		
		
		
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				
				reloadConfig();
				
				utils = new Utils();
				utils.reloadBasics();
				

				new BukkitRunnable() {
					
					@Override
					public void run() {
						
						
						MySQL.loadFile();
					    MySQL.connect();
					    MySQL.createTables();
					    for(Player players : Bukkit.getOnlinePlayers()) {
							MySQLMgr.updatePlayer(players);
						}
						
					}
				}.runTaskAsynchronously(Main.ins);
				
				registerListeners();
				registerCommands();
				
				
				checkUp();
			
				
				if(state == GameState.LOBBY) {
					setupEconomy();

					for(Player player : Bukkit.getOnlinePlayers()) {
						addPlayer(player.getUniqueId());
						Main.ins.utils.setToLobby(player);
					}
					
					ScoreboardMgr.autoUpdater();
					Counter.startStartCounter();
				} else if(state == GameState.EDIT) {
					for(Player player : Bukkit.getOnlinePlayers()) 
						addPlayer(player.getUniqueId());
				}
			}
		}.runTaskLater(Main.ins, 10);
		
		
		
		
	}
	
	private void registerCommands() {
		getServer().getPluginCommand("rds").setExecutor(new MainCommand());
		getServer().getPluginCommand("start").setExecutor(new StartCommand());
		getServer().getPluginCommand("kits").setExecutor(new KitCommand());
		getServer().getPluginCommand("vote").setExecutor(new VoteCommand());
	}
	
	private  void registerListeners() {
		Main.ins.getServer().getPluginManager().registerEvents(new LocationMgr(), this);
		Main.ins.getServer().getPluginManager().registerEvents(new DestroyStandListener(), this);
		Main.ins.getServer().getPluginManager().registerEvents(new GlobalEvents(), this);
		Main.ins.getServer().getPluginManager().registerEvents(new LobbyEvents(), this);
		Main.ins.getServer().getPluginManager().registerEvents(new MapVotingMgr(), this);
		Main.ins.getServer().getPluginManager().registerEvents(new IngameEvents(), this);
		Main.ins.getServer().getPluginManager().registerEvents(new KitMgr(), this);
		Main.ins.getServer().getPluginManager().registerEvents(new SpectatorListener(), this);
		Main.ins.getServer().getPluginManager().registerEvents(new StartGameItem(), this);
		Main.ins.getServer().getPluginManager().registerEvents(new EndGameListener(), this);
		Main.ins.getServer().getPluginManager().registerEvents(new MySQLMgr(), this);
		Main.ins.getServer().getPluginManager().registerEvents(new KitBuyMgr(), this);
		Main.ins.getServer().getPluginManager().registerEvents(new HitEntityThroughEntity(), this);
		
	}
	
	
	
//	Sheep testSheep;
//	
//	@EventHandler
//	public void onClick(PlayerInteractEvent e) {
//		if(e.getItem() != null && e.getItem().getType() == Material.STICK) {
//			Sheep sheep = e.getPlayer().getWorld().spawn(e.getPlayer().getLocation(), Sheep.class);
//			
//			e.getPlayer().setPassenger(sheep);
//			testSheep = sheep;
//			
//			sheep.setMaxHealth(2000);
//			sheep.setHealth(2000);
//		}
//	}
	
			

	
	
	
	@Override
	public void onDisable() {
		
		for(BlockerMgr mgr : entryMgrs.values()) mgr.cleanUp();
		for(Player player : Bukkit.getOnlinePlayers()) {
			if(player.getPassenger() != null) player.eject();
		}
		if(sheep != null) {
			sheep.destroySheep();
			sheep.destroyHologram();
			sheep = null;
		}
		
		if(boss != null) {
			boss.removeBoss();
			boss = null;
		}
		
		MySQL.close();
		
	}
	
	public RDSPlayer getRDSPlayer(Player p) {
        if(p == null) return new RDSPlayer(p);
        
        if(rdsPlayers.containsKey(p.getUniqueId())) {
            return rdsPlayers.get(p.getUniqueId());
        }

        return new RDSPlayer(p);
    }

    public RDSPlayer getRDSPlayer(UUID uuid) {
        if(Bukkit.getPlayer(uuid) == null) return new RDSPlayer(Bukkit.getPlayer(uuid));
        if(rdsPlayers.containsKey(Bukkit.getPlayer(uuid).getUniqueId()))
            return rdsPlayers.get(Bukkit.getPlayer(uuid).getUniqueId());
        return new RDSPlayer(Bukkit.getPlayer(uuid));
    }

    public int getRDSPlayerSize() {
        return rdsPlayers.size();
    }

    public void removePlayer(UUID uuid) {
        while(rdsPlayers.containsKey(uuid)) rdsPlayers.remove(uuid);
    }

    public boolean isInRDSPlayers(UUID uuid) {
        return (rdsPlayers.containsKey(uuid));
    }

    public void addPlayer(UUID uuid) {

        if(Bukkit.getPlayer(uuid) != null) {
        	
        	
        	
            RDSPlayer player = new RDSPlayer(Bukkit.getPlayer(uuid));
           
            rdsPlayers.put(uuid, player);
        }
    }

    @SuppressWarnings("unchecked")
    public HashMap<UUID, RDSPlayer> getRDSPlayersCopy() {
        return (HashMap<UUID, RDSPlayer>) rdsPlayers.clone();
    }
	
    public void checkUp() {
    	
    	
    	if(utils.getLobbySpawn() == null || utils.getLobbySpawn().getWorld() == null) {
    		
    		Bukkit.broadcastMessage("§4Der Lobbyspawn wurde noch nicht gesetzt! Der Server wurde automatisch in den Edit-Modus gesetzt!");
    		state = GameState.EDIT;
    		return;
    	}
    	if(!MapManager.validMapAvailable()) {
    		
    		Bukkit.broadcastMessage("§4Es wurden noch keine Maps erstellt! Der Server wurde automatisch in den Edit-Modus gesetzt!");
    		state = GameState.EDIT;
    		return;
    	}
    	
    }
    
    private boolean setupEconomy() {
		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager()
				.getRegistration(net.milkbowl.vault.economy.Economy.class);
		if (economyProvider != null)
			economy = economyProvider.getProvider();
		return (economy != null);
	}
    
    public void reloadConfig() {
    	try {
    		if(!utils.existFile("config.yml")) saveResource("config.yml", false);
        	
    		YamlConfiguration cfg = utils.getYaml("config");
    		
    		startTimer = cfg.getInt("config.startTimer");
    		bossName = cfg.getString("config.bossName");
    		bossLive = cfg.getInt("config.bossLive");
    		sheepMaxDropSeconds = cfg.getInt("config.sheepMaxDropSeconds");
    		moneyEnabled = cfg.getBoolean("config.money.giveMoney");
    		moneyWinAlive = cfg.getInt("config.money.winAlive");
    		moneyWinDeath = cfg.getInt("config.money.winDeath");
    		
    		
    		
    	} catch (Exception e) {
			e.printStackTrace();
			
			startTimer = 60;
    		bossName = "§7§lFerrum";
    		bossLive = 1000;
    		sheepMaxDropSeconds = 15;
    		
    		moneyEnabled = true;
    		moneyWinAlive = 15;
    		moneyWinDeath = 10;
		}
    	
    	
    	
    }
    
    
}
