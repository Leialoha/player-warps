package com.github.leialoha.playerwarps.events;

import java.util.Iterator;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import com.github.leialoha.playerwarps.PlayerWarps;
import com.github.leialoha.playerwarps.constructors.AdminWarp;
import com.github.leialoha.playerwarps.constructors.PlayerWarp;
import com.github.leialoha.playerwarps.constructors.WarpSign;
import com.github.leialoha.playerwarps.events.custom.TickEvent;
import com.github.leialoha.playerwarps.handler.SignHandler;
import com.github.leialoha.playerwarps.handler.WarpHandler;

public class WarpEvents implements Listener {
	
	private final WarpHandler wh = WarpHandler.getInstance();
	private final SignHandler sh = SignHandler.getInstance();

	@EventHandler
	public void onTick(TickEvent event) {
		PlayerWarp[] playerWarps = wh.getPlayerWarps();
		for (PlayerWarp playerWarp : playerWarps) playerWarp.draw();

		AdminWarp[] adminWarps = wh.getAdminWarps();
		for (AdminWarp adminWarp : adminWarps) adminWarp.draw();

		WarpSign[] warpSigns = sh.getSigns();
		for (WarpSign warpSign : warpSigns) warpSign.update();
	}

	@EventHandler
	public void onExplosion(EntityExplodeEvent event) {
		FileConfiguration config = PlayerWarps.getPlugin(PlayerWarps.class).getConfig();
		
		Iterator<Block> explodedBlocks = event.blockList().iterator();
		while (explodedBlocks.hasNext()) {
			Block explodedBlock = explodedBlocks.next();
			if (explodedBlock.getType().equals(Material.LODESTONE) && wh.hasLocation(explodedBlock.getLocation())) {
				if (config.getBoolean("protection.explosions") || !wh.isPlayerWarp(explodedBlock.getLocation())) explodedBlocks.remove();
				else wh.removeWarp(explodedBlock.getLocation());
			}
		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		FileConfiguration config = PlayerWarps.getPlugin(PlayerWarps.class).getConfig();
		
		Location location = event.getBlockPlaced().getLocation();
		for (int i = 0; i < 2; i++) {
			location = location.add(0, -1, 0);
			if (location.getBlock().getType().equals(Material.LODESTONE) && wh.hasLocation(location)) {
				if (config.getBoolean("protection.suffocation")) event.setCancelled(true);
				event.getPlayer().sendMessage("§cThere is a warp below this block, placing blocks on it will make it unacessable.");
			}
		}
	}

}
