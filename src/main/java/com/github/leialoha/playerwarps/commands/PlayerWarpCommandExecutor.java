package com.github.leialoha.playerwarps.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.github.leialoha.playerwarps.PlayerWarps;

public class PlayerWarpCommandExecutor implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		// sender.sendMessage(String.format("Args: %s", String.join(",", args)));
		if (args[0].equals("")) {

		} else if (args[0].equalsIgnoreCase("reload")) {
			PlayerWarps.getPlugin(PlayerWarps.class).reload();
			sender.sendMessage("Config reloaded");
		}
		return true;
	}
	
}
