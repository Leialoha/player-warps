package com.github.leialoha.playerwarps.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.github.leialoha.playerwarps.handler.PlayerWarpsInv;

public class TempCommandExecutor implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player player) {
			Inventory inv = PlayerWarpsInv.create();
			player.openInventory(inv);
		} else {
			sender.sendMessage("§cOnly players can execute this command!");
		}
		return true;
	}
	
}
