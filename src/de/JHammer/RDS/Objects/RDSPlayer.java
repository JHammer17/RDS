package de.JHammer.RDS.Objects;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import de.JHammer.RDS.Main;
import de.JHammer.RDS.Enums.GameState;
import de.JHammer.RDS.Enums.Kit;
import de.JHammer.RDS.Static.MoneyManager;

public class RDSPlayer {

	private Location pos1;
	private Location pos2;
	
	private Player player;
	private UUID uuid;
	private Kit kit = Kit.STARTER;
	private Kit selectedBuy;
	private boolean hasPlayed = false;
	
	private boolean speactator = false;
	private Long coins = 0L;
	
	
	public RDSPlayer(Player p) {
		this.player = p;
		this.uuid = p.getUniqueId();
		if(Main.ins.state != GameState.EDIT) updateCoins();
	}
	
	public void updateCoins() {
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				
				coins = (long) MoneyManager.getMoney(player);
				
				///Main.ins.economy.depositPlayer(player, );
				
			}
		}.runTaskAsynchronously(Main.ins);
		
	}
	
	
	public Player getPlayer() {
		return player;
	}
	
	
	public Location getPos1() {
		return pos1;
	}
	
	public Location getPos2() {
		return pos2;
	}
	
	public void setPos1(Location pos1) {
		this.pos1 = pos1;
	}
	
	public void setPos2(Location pos2) {
		this.pos2 = pos2;
	}
	
	public Kit getKit() {
		return this.kit;
	}
	
	public void setKit(Kit kit)  {
		this.kit = kit;
	}
	
	public void setSpecator(boolean spec) {
		this.speactator = spec;
	}
	
	public boolean isSpectator() {
		return this.speactator;
	}
	
	public long getCoins() {
		return this.coins;
	}
	
	public void setSelectedBuy(Kit kit) {
		this.selectedBuy = kit;
	}
	
	public Kit getSelectedBuy() {
		return  this.selectedBuy;
	}
	
	public boolean hasPlayed() {
		return this.hasPlayed;
	}
	
	public void setHasPlayed(boolean hasPlayed) {
		this.hasPlayed = hasPlayed;
	}
	
	public UUID getUUID() {
		return this.uuid;
	}
	
}
