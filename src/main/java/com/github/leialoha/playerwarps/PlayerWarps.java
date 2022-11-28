package com.github.leialoha.playerwarps;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.leialoha.playerwarps.commands.PlayerWarpCommandExecutor;
import com.github.leialoha.playerwarps.commands.PlayerWarpCommandTabCompleter;
import com.github.leialoha.playerwarps.commands.TempCommandExecutor;
import com.github.leialoha.playerwarps.commands.TempCommandTabCompleter;
import com.github.leialoha.playerwarps.events.PlayerEvents;
import com.github.leialoha.playerwarps.events.WarpEvents;
import com.github.leialoha.playerwarps.events.custom.TickEvent;
import com.github.leialoha.playerwarps.handler.SignHandler;
import com.github.leialoha.playerwarps.handler.WarpHandler;

public class PlayerWarps extends JavaPlugin {
	public static Plugin PLUGIN = null;
	public static String NAME = null;
	private static Logger LOGGER = null;

	private static boolean loaded = false;

	public void onEnable() {
		PLUGIN = this;
		NAME = PLUGIN.getName();
		LOGGER = Logger.getLogger(NAME);

		LOGGER.info(String.format("%s enabled", NAME));

		loadCommands();
		loadConfigs(); //TODO: Able to reload in game
		loadEvents();

		WarpHandler warpHandler = WarpHandler.getInstance();
		SignHandler signHandler = SignHandler.getInstance();

		try { warpHandler.loadPlayerWarps(); } catch (Exception e) {
			LOGGER.warning("Could not load the player warps");
			e.printStackTrace();
		}

		try { warpHandler.loadAdminWarps(); } catch (Exception e) {
			LOGGER.warning("Could not load the admin warps");
			e.printStackTrace();
		}

		try { signHandler.loadSigns(); } catch (Exception e) {
			LOGGER.warning("Could not load the warp signs");
			e.printStackTrace();
		}

		loaded = true;
	}

	public void onDisable() {
		LOGGER.info(String.format("%s disabled", NAME));

		if (loaded) {
			WarpHandler warpHandler = WarpHandler.getInstance();
			SignHandler signHandler = SignHandler.getInstance();

			try { warpHandler.savePlayerWarps(); } catch (Exception e) {
				LOGGER.warning("Could not save the player warps");
				e.printStackTrace();
			}
			try { warpHandler.saveAdminWarps(); } catch (Exception e) {
				LOGGER.warning("Could not save the admin warps");
				e.printStackTrace();
			}
			try { signHandler.saveSigns(); } catch (Exception e) {
				LOGGER.warning("Could not save the warp signs");
				e.printStackTrace();
			}
		}
	}

	private void loadCommands() {
		PluginCommand tempCmd = this.getCommand("temp");
		tempCmd.setExecutor(new TempCommandExecutor());
		tempCmd.setTabCompleter(new TempCommandTabCompleter());

		PluginCommand playerWarpCmd = this.getCommand("playerwarps");
		playerWarpCmd.setExecutor(new PlayerWarpCommandExecutor());
		playerWarpCmd.setTabCompleter(new PlayerWarpCommandTabCompleter());
	}

	private void loadConfigs() {
		PLUGIN.saveDefaultConfig();
	}

	public void reload() {
		reloadConfig();
		loadConfigs();
	}

	private void loadEvents() {
		Bukkit.getPluginManager().registerEvents(new PlayerEvents(), PLUGIN);
		Bukkit.getPluginManager().registerEvents(new WarpEvents(), PLUGIN);

		Bukkit.getScheduler().runTaskTimer(PLUGIN, new Runnable() {
			@Override
			public void run() {
				Bukkit.getPluginManager().callEvent(new TickEvent());
			}
		}, 1L, 1L);
	}
}
