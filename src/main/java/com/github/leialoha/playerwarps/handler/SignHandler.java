package com.github.leialoha.playerwarps.handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;

import com.github.leialoha.playerwarps.PlayerWarps;
import com.github.leialoha.playerwarps.constructors.WarpSign;

public class SignHandler {
	
	private static SignHandler instance;
	private final List<WarpSign> signs;

	private SignHandler() {
		signs = new ArrayList<>();
	}

	public static SignHandler getInstance() {
		if (instance == null) instance = new SignHandler();
		return instance;
	}

	public WarpSign[] getSigns() {
		return signs.toArray(new WarpSign[0]);
	}

	public void addSign(WarpSign warpSign) {
		signs.add(warpSign);
	}
	
	public void removeSign(WarpSign warpSign) {
		signs.remove(warpSign);
	}
	
	public boolean hasSign(Location location) {
		return getSign(location) != null;
	}

	public WarpSign getSign(Location _location) {
		final Location location = _location.clone();
		location.setX(location.getBlockX());
		location.setY(location.getBlockY());
		location.setZ(location.getBlockZ());

		return signs.stream().filter(warpSign -> warpSign.getLocation().equals(location)).findFirst().orElse(null);
	}

	public void saveSigns() throws Exception {
		File folder = PlayerWarps.getPlugin(PlayerWarps.class).getDataFolder();
		File warpsDataFile = new File(folder, "warp_signs.dat");
		if (!warpsDataFile.exists()) warpsDataFile.createNewFile();

		FileOutputStream outputStream = new FileOutputStream(warpsDataFile);
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);

		objectOutputStream.writeObject(signs);

		objectOutputStream.close();
		outputStream.close();
	}

	public void loadSigns() throws Exception {
		File folder = PlayerWarps.getPlugin(PlayerWarps.class).getDataFolder();
		File warpsDataFile = new File(folder, "warp_signs.dat");
		if (!warpsDataFile.exists()) return;

		FileInputStream inputStream = new FileInputStream(warpsDataFile);
		ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

		@SuppressWarnings("unchecked")
		List<WarpSign> warpsFromFile = (List<WarpSign>) objectInputStream.readObject();

		signs.addAll(warpsFromFile);

		objectInputStream.close();
		inputStream.close();
	}
}
