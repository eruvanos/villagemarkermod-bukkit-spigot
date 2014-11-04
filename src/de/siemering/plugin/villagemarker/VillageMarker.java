package de.siemering.plugin.villagemarker;

import java.io.File;
import java.io.IOException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import de.siemering.plugin.villagemarker.command.VMCommand;

public class VillageMarker extends JavaPlugin {

	protected static final String VILLAGEPERMISSION = "villagemarker";
	private static final String CONFIGFILE = "player.yml";

	private static YamlConfiguration pconfig;

	private String CONFIGPATH;
	private ClientUpdaterV2 updater;

	@Override
	public void onEnable() {
		super.onEnable();
		
		//configs
		CONFIGPATH = getDataFolder() + "/" + CONFIGFILE;
		loadConfigs();

		// updater
		updater = new ClientUpdaterV2(pconfig);
		updater.start();

		// commands
		getCommand("vm").setExecutor(new VMCommand(pconfig));

	}

	@Override
	public void onDisable() {
		super.onDisable();

		// configs
		saveConfigs();

		// updater
		updater.setStop(true);
		try {
			updater.join();
		} catch (InterruptedException e) {
			Logger.logException(e);
		}
	}

//	public YamlConfiguration getConfig() {
//		if (pconfigs == null) {
//			loadConfig();
//		}
//		return pconfig;
//	}

	private void loadConfigs() {
		pconfig = YamlConfiguration.loadConfiguration(new File(CONFIGPATH));
	}

	private void saveConfigs() {
		try {
			pconfig.save(CONFIGPATH);
		} catch (IOException e) {
			Logger.logException(e);
		}
	}

}
