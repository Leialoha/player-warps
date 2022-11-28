package com.github.leialoha.playerwarps.constructors;

import java.io.Serializable;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Sign;

import com.github.leialoha.playerwarps.PlayerWarps;
import com.github.leialoha.playerwarps.handler.SignHandler;
import com.github.leialoha.playerwarps.handler.WarpHandler;

public class WarpSign implements Serializable {
	private final String target;
	private final double[] coords;
	private final String worldName;
	private final Type warpType;
	private final UUID owner;

	private transient Location location;
	private transient World world;

	public WarpSign(String target, double[] coords, String worldName, Type warpType, UUID owner) {
		this.target = target;
		this.coords = coords;
		this.worldName = worldName;
		this.warpType = warpType;
		this.owner = owner;

		this.world = Bukkit.getWorld(worldName);
		if (this.world == null) throw new IllegalArgumentException("Could not find a valid world with the name of \"" + worldName + "\"");
		if (this.coords.length != 3) throw new IllegalArgumentException("There must be 3 coordinate values");
		this.location = new Location(world, coords[0], coords[1], coords[2]);
	}

	public WarpSign(String target, Location location, Type warpType, UUID owner) {
		this.target = target;
		this.coords = new double[]{location.getBlockX(), location.getBlockY(), location.getBlockZ()};
		this.worldName = location.getWorld().getName();
		this.warpType = warpType;
		this.owner = owner;

		this.world = location.getWorld();
		this.location = location;
	}

	public UUID getOwnerUUID() {
		return owner;
	}

	public OfflinePlayer getOwner() {
		return (owner == null) ? null : Bukkit.getOfflinePlayer(owner);
	}

	public String getTarget() {
		return target;
	}

	public World getWorld() {
		if (this.world == null) this.world = Bukkit.getWorld(worldName);
		return world;
	}

	public Location getLocation() {
		if (this.location == null) this.location = new Location(this.getWorld(), coords[0], coords[1], coords[2]);
		return location.clone();
	}

	public Type getWarpType() {
		return warpType;
	}

	public void update() {
		SignHandler sh = SignHandler.getInstance();
		WarpHandler wh = WarpHandler.getInstance();

		if (!getLocation().getBlock().getType().toString().endsWith("_SIGN")) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(PlayerWarps.PLUGIN, () -> sh.removeSign(this));
			return;
		}

		String name = !wh.hasWarp(target) ? "§4§lDELETED" : (getWarpType().equals(Type.ADMIN) ? wh.getAdminWarp(target).getName() : wh.getPlayerWarp(target).getName());

		Sign sign = (Sign) getLocation().getBlock().getState();
		sign.setLine(0, String.format("[%s WARP]", getWarpType().toString()));
		sign.setLine(1, name);
		sign.setLine(2, getWarpType().equals(Type.ADMIN) ? "" : getOwner().getName());
		sign.setLine(3, "Click to warp");

		sign.update();
	}

	public enum Type {
		ADMIN,
		PLAYER
	}
}
