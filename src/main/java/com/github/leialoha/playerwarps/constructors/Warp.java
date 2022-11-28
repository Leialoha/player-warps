package com.github.leialoha.playerwarps.constructors;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.Particle.DustOptions;
import org.bukkit.entity.Player;

public abstract class Warp implements Serializable {
	
	private static final long serialVersionUID = -295245560944900060l;
	private int version;

	private final String name;
	private final double[] coords;
	private final String worldName;

	private Visibility visibility = Visibility.UNLISTED;
	private float rotation = 0f;

	private transient Location location;
	private transient World world;

	public Warp(String name, double[] coords, String worldName, Visibility visibility, float rotation) {
		this.version = 1;
		this.name = name;
		this.coords = coords;
		this.worldName = worldName;

		this.visibility = visibility;
		this.rotation = rotation % 360;

		this.world = Bukkit.getWorld(worldName);
		if (this.world == null) throw new IllegalArgumentException("Could not find a valid world with the name of \"" + worldName + "\"");
		if (this.coords.length != 3) throw new IllegalArgumentException("There must be 3 coordinate values");
		this.location = new Location(world, coords[0], coords[1], coords[2]);
	}

	public Warp(String name, Location location, Visibility visibility, float rotation) {
		this.version = 1;
		this.name = name;
		this.coords = new double[]{location.getBlockX(), location.getBlockY(), location.getBlockZ()};
		this.worldName = location.getWorld().getName();

		this.visibility = visibility;
		this.rotation = rotation % 360;
		
		this.world = location.getWorld();
		this.location = location;
	}

	@Deprecated
	public Warp(String name, double[] coords, String worldName) {
		this(name, coords, worldName, Visibility.PUBLIC, 0f);
	}

	@Deprecated
	public Warp(String name, Location location) {
		this(name, location, Visibility.PUBLIC, 0f);
	}

	public String getName() {
		return name;
	}

	public World getWorld() {
		if (this.world == null) this.world = Bukkit.getWorld(worldName);
		return world;
	}

	public Location getLocation() {
		if (this.location == null) this.location = new Location(this.getWorld(), coords[0], coords[1], coords[2]);
		return location.clone();
	}

	public void teleport(Player player) {
		Location loc = getLocation().clone().add(0.5, 1, 0.5);
		loc.setYaw(rotation);
		player.teleport(loc);
	}

	public float getRotation() {
		return rotation;
	}

	public Visibility getVisibility() {
		return visibility;
	}

	public void setRotation(float rotation) {
		this.rotation = rotation % 360;
	}

	public void setVisibility(Visibility visibility) {
		this.visibility = visibility;
	}

	public void update() {
		if (this.version == 0) {
			this.version = 1;

			this.visibility = Visibility.PUBLIC;
			this.rotation = 0f;
		}
	}

	protected void draw(Color color) {
		boolean twoSeconds = (new Date()).getTime() / 1000 % 2 == 0;
		Calendar calendar = Calendar.getInstance();
		if (calendar.get(Calendar.MONTH) == Calendar.DECEMBER && calendar.get(Calendar.DAY_OF_MONTH) <= 24 && calendar.get(Calendar.DAY_OF_MONTH) > 20) color = (twoSeconds) ? Color.RED : Color.GREEN;
		else if (calendar.get(Calendar.MONTH) == Calendar.OCTOBER && calendar.get(Calendar.DAY_OF_MONTH) == 30) color = (twoSeconds) ? Color.ORANGE : Color.BLACK;

		Location _location = getLocation();
		_location.setX(_location.getBlockX());
		_location.setY(_location.getBlockY());
		_location.setZ(_location.getBlockZ());

		Location location01 = _location.clone().add(0, 0, 0); // X
		Location location02 = _location.clone().add(0, 1, 0); // X
		Location location03 = _location.clone().add(0, 0, 1); // X
		Location location04 = _location.clone().add(0, 1, 1); // X
		Location location08 = _location.clone().add(0, 0, 0); // Y
		Location location05 = _location.clone().add(1, 0, 0); // Y
		Location location06 = _location.clone().add(0, 0, 1); // Y
		Location location07 = _location.clone().add(1, 0, 1); // Y
		Location location12 = _location.clone().add(0, 0, 0); // Z
		Location location09 = _location.clone().add(1, 0, 0); // Z
		Location location10 = _location.clone().add(0, 1, 0); // Z
		Location location11 = _location.clone().add(1, 1, 0); // Z
		DustOptions options = new DustOptions(color, 0.35f);

		for (int i = 0; i < 20; i++) {
			_location.getWorld().spawnParticle(Particle.REDSTONE, location01.add(0.05, 0, 0), 1, 0, 0, 0, options); // X
			_location.getWorld().spawnParticle(Particle.REDSTONE, location02.add(0.05, 0, 0), 1, 0, 0, 0, options); // X
			_location.getWorld().spawnParticle(Particle.REDSTONE, location03.add(0.05, 0, 0), 1, 0, 0, 0, options); // X
			_location.getWorld().spawnParticle(Particle.REDSTONE, location04.add(0.05, 0, 0), 1, 0, 0, 0, options); // X
			_location.getWorld().spawnParticle(Particle.REDSTONE, location08.add(0, 0.05, 0), 1, 0, 0, 0, options); // Y
			_location.getWorld().spawnParticle(Particle.REDSTONE, location05.add(0, 0.05, 0), 1, 0, 0, 0, options); // Y
			_location.getWorld().spawnParticle(Particle.REDSTONE, location06.add(0, 0.05, 0), 1, 0, 0, 0, options); // Y
			_location.getWorld().spawnParticle(Particle.REDSTONE, location07.add(0, 0.05, 0), 1, 0, 0, 0, options); // Y
			_location.getWorld().spawnParticle(Particle.REDSTONE, location12.add(0, 0, 0.05), 1, 0, 0, 0, options); // Z
			_location.getWorld().spawnParticle(Particle.REDSTONE, location09.add(0, 0, 0.05), 1, 0, 0, 0, options); // Z
			_location.getWorld().spawnParticle(Particle.REDSTONE, location10.add(0, 0, 0.05), 1, 0, 0, 0, options); // Z
			_location.getWorld().spawnParticle(Particle.REDSTONE, location11.add(0, 0, 0.05), 1, 0, 0, 0, options); // Z
		}

		DustOptions rotOptions = new DustOptions(Color.WHITE, 0.35f);
		double radians = Math.toRadians(rotation);
		_location.add(0.5, 1, 0.5).add(-Math.sin(radians) * 0.2, 0, Math.cos(radians) * 0.2);
		_location.getWorld().spawnParticle(Particle.REDSTONE, _location, 1, 0, 0, 0, rotOptions);
	}

	public enum Visibility {
		PUBLIC,
		UNLISTED,
		PRIVATE;

		public static Visibility getOrDefault(String string, Visibility visibility) {
			return List.of(Visibility.values()).stream().filter(vis -> vis.name().equalsIgnoreCase(string)).findFirst().orElse(visibility);
		}
	}
}
