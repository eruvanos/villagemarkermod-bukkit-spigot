package de.siemering.plugin.villagemarker;

import java.util.logging.Level;

import org.bukkit.Bukkit;

public class Logger {
	/**
	 * Logge eine Exception samt StackTrace.
	 * 
	 * @param e
	 *            Exception
	 */
	public static void logException(Exception e) {
		StringBuilder message = new StringBuilder("[VillageMarker] " + e.getMessage() + "\n");

		for (StackTraceElement ste : e.getStackTrace()) {
			message.append(ste.toString() + "\n");
		}

		Bukkit.getLogger().log(Level.WARNING, message.toString());
	}
}
