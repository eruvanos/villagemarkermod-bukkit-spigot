package de.siemering.plugin.villagemarker;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class VillageMarker extends JavaPlugin {
	private ClientUpdaterV2 updater;
	
	protected static final String VILLAGEPERMISSION = "villagemarker";
	
	@Override
	public void onEnable() {
		super.onEnable();
		
		updater = new ClientUpdaterV2();
		updater.start();
	}
	
	
	@Override
	public void onDisable() {
		updater.setStop(true);
		
		try {
			updater.join();
		} catch (InterruptedException e) {
			VillageMarker.logException(e);
		}
		super.onDisable();
	}
	
	/**
	 * Logge eine Exception samt StackTrace.
	 * 
	 * @param e Exception
	 */
	protected static void logException(Exception e){
		StringBuilder message = new StringBuilder("[VillageMarker] " +  e.getMessage() + "\n");
		
		for(StackTraceElement ste:e.getStackTrace()){
			message.append(ste.toString() + "\n");
		}
		
		Bukkit.getLogger().log(Level.WARNING,message.toString());
	}
}
