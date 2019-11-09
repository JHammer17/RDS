package de.JHammer.RDS.WorldLoader;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;

public class AsyncWorldLoader {

	
	 public static World createAsyncWorld(WorldCreator creator) {
	    	try {
	    		CraftServer cS = ((CraftServer)Bukkit.getServer());
	    		World w = cS.createWorld(creator);
	    		return w;
	    	} catch (Exception e) {
	    		return null;
			} 
	 }
	
}
