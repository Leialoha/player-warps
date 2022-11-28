package com.github.leialoha.playerwarps.constructors;

import org.bukkit.Color;
import org.bukkit.Location;

public class AdminWarp extends Warp {

	private static final long serialVersionUID = 4564294817524973426l;

	public AdminWarp(String name, double[] coords, String worldName, Visibility visibility, float rotation) {
        super(name, coords, worldName, visibility, rotation);
    }

	public AdminWarp(String name, Location location, Visibility visibility, float rotation) {
        super(name, location, visibility, rotation);
    }

	@Deprecated
    public AdminWarp(String name, double[] coords, String worldName) {
        super(name, coords, worldName);
    }

	@Deprecated
    public AdminWarp(String name, Location location) {
        super(name, location);
    }
    
	public void draw() {
		super.draw(Color.fromRGB(128, 51, 51));
	}
}
