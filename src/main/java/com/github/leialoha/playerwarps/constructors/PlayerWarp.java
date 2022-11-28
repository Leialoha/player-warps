package com.github.leialoha.playerwarps.constructors;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

public class PlayerWarp extends Warp {

	private static final long serialVersionUID = -5817211929831853027l;

    public final UUID owner;
    public transient OfflinePlayer ownerPlayer;

	public PlayerWarp(OfflinePlayer owner, String name, double[] coords, String worldName, Visibility visibility, float rotation) {
        super(name, coords, worldName, visibility, rotation);
        this.owner = owner.getUniqueId();
        this.ownerPlayer = owner;
    }
    
    public PlayerWarp(OfflinePlayer owner, String name, Location location, Visibility visibility, float rotation) {
        super(name, location, visibility, rotation);
        this.owner = owner.getUniqueId();
        this.ownerPlayer = owner;
    }

	@Deprecated
    public PlayerWarp(OfflinePlayer owner, String name, double[] coords, String worldName) {
        super(name, coords, worldName);
        this.owner = owner.getUniqueId();
        this.ownerPlayer = owner;
    }
    
	@Deprecated
    public PlayerWarp(OfflinePlayer owner, String name, Location location) {
        super(name, location);
        this.owner = owner.getUniqueId();
        this.ownerPlayer = owner;
    }

    public OfflinePlayer getOwner() {
        if (ownerPlayer == null) ownerPlayer = Bukkit.getOfflinePlayer(owner);
        return ownerPlayer;
    }

    public UUID getOwnerUUID() {
        return owner;
    }

	public void draw() {
		super.draw(Color.fromRGB(51, 128, 128));
	}

}
