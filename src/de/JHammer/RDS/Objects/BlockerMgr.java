package de.JHammer.RDS.Objects;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitRunnable;


import de.JHammer.RDS.Main;
import de.JHammer.RDS.Manager.MapManager;

public class BlockerMgr {

	private Location min;
	private Location max;

	private ArrayList<Location> placedBlocks = new ArrayList<>();
	private World world;
	
	private ArmorStand stand;
	private Location standPos;
	private int number;
	
	
	public BlockerMgr(Location min, Location max, World w, Location standPos, int number) {
		this.min = min;
		this.max = max;
		this.world = w;
		this.standPos = standPos;
		this.number = number;
	}
	
	
	@SuppressWarnings("unchecked")
	public void destroyMine() {
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				int maxY = -1;
				
				double delay = 0;
				
				
				while(!placedBlocks.isEmpty()) {
					
					for(Location loc : (ArrayList<Location>) placedBlocks.clone()) {
						if(loc.getBlockY() > maxY) {
							maxY = loc.getBlockY();
						}
					}
					
					
					ArrayList<Location> randomList = new ArrayList<>();
					
					for(Location loc : (ArrayList<Location>) placedBlocks.clone()) {
						if(loc.getBlockY() >= maxY) {
							randomList.add(loc);
							
						}
						
					}
					
					while(!randomList.isEmpty()) {
						
						Location loc = randomList.get(Main.ins.utils.getRandom().nextInt(randomList.size()));
						
						new BukkitRunnable() {
							
							@SuppressWarnings("deprecation")
							@Override
							public void run() {
								
								loc.getWorld().playEffect(loc, Effect.STEP_SOUND, loc.getBlock().getTypeId());
								loc.getBlock().setType(Material.AIR);
							}
						}.runTaskLater(Main.ins, (int)delay);
						delay += 2;
						randomList.remove(loc);
						placedBlocks.remove(loc);
					}
					
					
					
					
					
					
					maxY = -1;
					
				}
			}
		}.runTaskAsynchronously(Main.ins);
		
		
		
		Main.ins.entryMgrs.remove(getNumber());
		
		
	}
	
	public void fill() {
//		spawnDestroyArmorStand();
		new BukkitRunnable() {
			
			@Override
			public void run() {
				for(int x = getMin().getBlockX(); x <= getMax().getBlockX(); x++) {
					for(int z = getMin().getBlockZ(); z <= getMax().getBlockZ(); z++) {
						for(int y = getMin().getBlockY(); y < getMin().getBlockY()+2; y++) {
							placeRandomBlock(x, y, z);
						}
						
						for(int y = getMin().getBlockY(); y < getMin().getBlockY()+2+(Main.ins.utils.getRandom().nextInt(2)+Main.ins.utils.getRandom().nextInt(2)) ; y++) {
							
							if(!placedBlocks.contains(new Location(world, x, y, z)))
							 placeRandomBlock(x, y, z);
							
						}
						
					}
					
				}
			}
		}.runTaskAsynchronously(Main.ins);
	}
	

	public void placeRandomBlock(int x, int y, int z) {
		int r = Main.ins.utils.getRandom().nextInt(9);
		
		
			new BukkitRunnable() {
				
				@SuppressWarnings("deprecation")
				@Override
				public void run() {
					
					
				
				 if(new Location(world, x, y, z).getBlock().getType() == Material.AIR) {
						placedBlocks.add(new Location(world, x, y, z));
						
					if(r == 0 || r == 1 || r == 2 || r == 7) {
						new Location(world, x, y, z).getBlock().setType(Material.COBBLESTONE);
					} else if(r == 3) {
						new Location(world, x, y, z).getBlock().setType(Material.MOSSY_COBBLESTONE);
					} else if(r == 4) {
						
						new Location(world, x, y, z).getBlock().setType(Material.STONE);
						new Location(world, x, y, z).getBlock().setData((byte) 1);
						
					} else if(r == 5) {
						new Location(world, x, y, z).getBlock().setType(Material.STONE);
						new Location(world, x, y, z).getBlock().setData((byte) 5);
						
					} else if(r == 6) {
						new Location(world, x, y, z).getBlock().setType(Material.STONE);
						new Location(world, x, y, z).getBlock().setData((byte) 3);
					} else {
						new Location(world, x, y, z).getBlock().setType(Material.COBBLESTONE);
					}
				 }
			  }
			}.runTask(Main.ins);
		
	}
	
	public Location getMin() {
		return min.clone();
	}
	
	public Location getMax() {
		return max.clone();
	}
	
	public void spawnDestroyArmorStand() {
		
		
		
		stand = standPos.getWorld().spawn(standPos, ArmorStand.class);
		stand.setCustomName("§7Schlagen zum Abbauen!");
		stand.setCustomNameVisible(true);
		stand.setGravity(false);
		stand.setVisible(false);
		stand.setSmall(true);
		
		for(Entity en : stand.getNearbyEntities(1, 1, 1)) {
			if(en.getType() == EntityType.ARMOR_STAND) {
				if(!en.getUniqueId().equals(stand.getUniqueId())) {
					
					en.remove();
				}
			}
		}
		
		
	}
	
	public void removeDestroyArmorStand() {
		if(stand != null) stand.remove();
	}
	
	public UUID getArmorStandUUID() {
		if(stand != null) return stand.getUniqueId();
		return null;
	}
	
	
	public void cleanUp() {
		removeDestroyArmorStand();
		for(Location loc : placedBlocks) {
			loc.getBlock().setType(Material .AIR);
		}		
	}
	
	public void spawnNextWave() {
		MapManager.spawnMobs(Main.ins.map, number+1);
		Main.ins.currentWave = number+1;
	}
	
	public int getNumber() {
		return number;
	}
	
}
