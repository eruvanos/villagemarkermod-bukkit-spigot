package de.siemering.plugin.villagemarker.command;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public abstract class CommandHandler implements CommandExecutor {

	private String permission;
	private boolean allowConsol;
	
	public CommandHandler(String permission, boolean allowConsol){
		this.permission = permission;
		this.allowConsol = allowConsol;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] arg) {

		//Consolenbefehle ignorieren
		if (! (sender instanceof Player) || allowConsol){
			sender.sendMessage("Just player are able to use this command.");
			return false;
		}
		
		//�berpr�fe, ob Sender Rechte hat
		if(! sender.hasPermission("villagemarker")){
			sender.sendMessage("You doesn't have the permission to use this command.");
			return false;
		}
		
		return executeCommand(sender, command, label, arg);
	}
	
	/**
	 * Diese Methode wird aufgerufen, sobald ueberprueft wurde, ob der Sender die benoetigten Bedingungen erfuellt.
	 */
	protected abstract boolean executeCommand(CommandSender sender, Command command, String label, String[] arg);


}
