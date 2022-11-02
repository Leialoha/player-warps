package com.github.leialoha.playerwarps;

import java.util.logging.Logger;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerWarps extends JavaPlugin {
	public static Plugin PLUGIN = null;
	public static String NAME = null;
	private static Logger LOGGER = null;

	public void onEnable() {
		PLUGIN = this;
		NAME = PLUGIN.getName();
		LOGGER = Logger.getLogger(NAME);

		LOGGER.info(String.format("%s enabled", NAME));
	}

	public void onDisable() {
		LOGGER.info(String.format("%s disabled", NAME));
	}
}
