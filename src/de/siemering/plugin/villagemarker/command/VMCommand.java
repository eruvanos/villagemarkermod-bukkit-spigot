package de.siemering.plugin.villagemarker.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class VMCommand extends CommandHandler {

	private YamlConfiguration pconfig;
	
	public VMCommand(YamlConfiguration pconfig) {
		super("villagemarker", false);
		
		this.pconfig = pconfig;
	}

	@Override
	protected boolean executeCommand(CommandSender sender, Command command, String label, String[] arg) {

		//Ohne Subbefehl 
		if(arg.length == 0){
			return executeInfo((Player) sender);
		}
		
		if (arg.length != 1) {
			sender.sendMessage("Incorrect count of arguments.");
			sender.sendMessage("vm <on|off|t>");
			return true;
		}

		if (arg[0].equals("on")) {
			return executeOn((Player) sender);
		} else if (arg[0].equals("off")) {
			return executeOff((Player) sender);
		} else if (arg[0].equals("t")) {
			return executeToggel((Player) sender);
		} else {
			sender.sendMessage("Illegal arguments.");
			sender.sendMessage("vm <on|off|t>");
			return true;
		}

//		return false;

	}

	private boolean executeOn(Player sender) {
		pconfig.set(sender.getName(), true);
		sender.sendMessage("GetVillageInformation is: " + pconfig.getBoolean(sender.getName(), true));
		return true;
	}

	private boolean executeOff(Player sender) {
		pconfig.set(sender.getName(), false);
		sender.sendMessage("GetVillageInformation is: " + pconfig.getBoolean(sender.getName(), true));
		return true;
	}

	private boolean executeToggel(Player sender) {
		pconfig.set(sender.getName(), !pconfig.getBoolean(sender.getName(), true));
		sender.sendMessage("GetVillageInformation is: " + pconfig.getBoolean(sender.getName(), true));
		return true;
	}
	
	private boolean executeInfo(Player sender) {
		sender.sendMessage("GetVillageInformation is: " + pconfig.getBoolean(sender.getName(), true));
		sender.sendMessage("vm <on|off|t>");
		return true;
	}

}
