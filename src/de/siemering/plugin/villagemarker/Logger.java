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
		StringBuilder message = new StringBuilder("[VillageMarker] ").append(e.getMessage()).append("\n");

		for (StackTraceElement ste : e.getStackTrace()) {
			message.append(ste.toString()).append("\n");
		}

		Bukkit.getLogger().log(Level.WARNING, message.toString());
	}
}
