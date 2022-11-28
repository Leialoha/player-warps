package com.github.leialoha.playerwarps.handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

import com.github.leialoha.playerwarps.PlayerWarps;
import com.github.leialoha.playerwarps.constructors.AdminWarp;
import com.github.leialoha.playerwarps.constructors.PlayerWarp;

public class WarpHandler {
	
	private static WarpHandler instance;
	private final HashMap<String, PlayerWarp> playerWarp;
	private final HashMap<String, AdminWarp> adminWarp;

	private WarpHandler() {
		playerWarp = new HashMap<>();
		adminWarp = new HashMap<>();
	}

	public static WarpHandler getInstance() {
		if (instance == null) instance = new WarpHandler();
		return instance;
	}

	
	public void addWarp(String name, PlayerWarp warp) {
		playerWarp.put(name.toLowerCase(), warp);
	}
	
	public void addWarp(PlayerWarp warp) {
		addWarp(warp.getOwner().getUniqueId() + ":" + warp.getName(), warp);
	}
	
	// REMOVING WITH BLOCK BREAK OR INVALID WARP
	public void removeWarp(PlayerWarp warp) {
		for (Entry<String, PlayerWarp> entry : playerWarp.entrySet()) {
			if (entry.getValue().equals(warp)) {
				playerWarp.remove(entry.getKey());
				break;
			}
		}
	}
	
	// REMOVING WITH COMMAND
	public void removeWarp(String warpName) {
		playerWarp.remove(warpName);
	}
	
	public void removeWarp(Location location) {
		if (isPlayerWarp(location)) removeWarp(getPlayerWarp(location));
		else removeWarp(getAdminWarp(location));
	}
	
	// REMOVING WITH COMMAND
	public PlayerWarp getPlayerWarp(Location location) {
		return playerWarp.entrySet().stream().filter(entry -> entry.getValue().getLocation().equals(location))
			.map(entry -> entry.getValue()).findFirst().orElse(null);
	}
	
	public PlayerWarp getPlayerWarp(String name) {
		return playerWarp.getOrDefault(name.toLowerCase(), null);
	}

	public PlayerWarp[] getPlayerWarps() {
		return playerWarp.values().toArray(new PlayerWarp[0]);
	}

	public boolean hasWarp(String warpName) {
		return playerWarp.containsKey(warpName.toLowerCase()) || adminWarp.containsKey(warpName.toLowerCase());
	}

	public boolean hasLocation(Location lodestone) {
		return playerWarp.entrySet().stream().anyMatch(entry -> entry.getValue().getLocation().equals(lodestone)) ||
			adminWarp.entrySet().stream().anyMatch(entry -> entry.getValue().getLocation().equals(lodestone));
	}

	public boolean isPlayerWarp(Location location) {
		return playerWarp.entrySet().stream().filter(entry -> entry.getValue().getLocation().equals(location))
			.map(entry -> entry.getValue()).findFirst().orElse(null) != null;
	}

	public void addWarp(String name, AdminWarp warp) {
		adminWarp.put(name.toLowerCase(), warp);
	}

	public void addWarp(AdminWarp warp) {
		addWarp(warp.getName(), warp);
	}

	// REMOVING WITH BLOCK BREAK OR INVALID WARP
	public void removeWarp(AdminWarp warp) {
		for (Entry<String, AdminWarp> entry : adminWarp.entrySet()) {
			if (entry.getValue().equals(warp)) {
				adminWarp.remove(entry.getKey());
				break;
			}
		}
	}

	// REMOVING WITH COMMAND
	public void removeAdminWarp(String warpName) {
		adminWarp.remove(warpName);
	}

	public AdminWarp getAdminWarp(Location location) {
		return adminWarp.entrySet().stream().filter(entry -> entry.getValue().getLocation().equals(location))
			.map(entry -> entry.getValue()).findFirst().orElse(null);
	}

	public AdminWarp getAdminWarp(String name) {
		return adminWarp.getOrDefault(name.toLowerCase(), null);
	}

	public AdminWarp[] getAdminWarps() {
		return adminWarp.values().toArray(new AdminWarp[0]);
	}

	public int getAmount(OfflinePlayer player) {
		return playerWarp.entrySet().stream().filter(entry -> ((PlayerWarp) entry.getValue()).getOwnerUUID().equals(player.getUniqueId())).toArray().length;
	}

	public void savePlayerWarps() throws Exception {
		File folder = PlayerWarps.getPlugin(PlayerWarps.class).getDataFolder();
		File warpsDataFile = new File(folder, "player_warps.dat");
		if (!warpsDataFile.exists()) warpsDataFile.createNewFile();

		FileOutputStream outputStream = new FileOutputStream(warpsDataFile);
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);

		objectOutputStream.writeObject(playerWarp);

		objectOutputStream.close();
		outputStream.close();
	}

	public void loadPlayerWarps() throws Exception {
		File folder = PlayerWarps.getPlugin(PlayerWarps.class).getDataFolder();
		File warpsDataFile = new File(folder, "player_warps.dat");
		if (!warpsDataFile.exists()) return;

		FileInputStream inputStream = new FileInputStream(warpsDataFile);
		ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

		@SuppressWarnings("unchecked")
		HashMap<String, PlayerWarp> warpsFromFile = (HashMap<String, PlayerWarp>) objectInputStream.readObject();
		warpsFromFile.entrySet().forEach(entry -> entry.getValue().update());

		playerWarp.putAll(warpsFromFile);

		objectInputStream.close();
		inputStream.close();
	}

	public void saveAdminWarps() throws Exception {
		File folder = PlayerWarps.getPlugin(PlayerWarps.class).getDataFolder();
		File warpsDataFile = new File(folder, "admin_warps.dat");
		if (!warpsDataFile.exists()) warpsDataFile.createNewFile();

		FileOutputStream outputStream = new FileOutputStream(warpsDataFile);
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);

		objectOutputStream.writeObject(adminWarp);

		objectOutputStream.close();
		outputStream.close();
	}

	public void loadAdminWarps() throws Exception {
		File folder = PlayerWarps.getPlugin(PlayerWarps.class).getDataFolder();
		File warpsDataFile = new File(folder, "admin_warps.dat");
		if (!warpsDataFile.exists()) return;

		FileInputStream inputStream = new FileInputStream(warpsDataFile);
		ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

		@SuppressWarnings("unchecked")
		HashMap<String, AdminWarp> warpsFromFile = (HashMap<String, AdminWarp>) objectInputStream.readObject();
		warpsFromFile.entrySet().forEach(entry -> entry.getValue().update());

		adminWarp.putAll(warpsFromFile);

		objectInputStream.close();
		inputStream.close();
	}
}
