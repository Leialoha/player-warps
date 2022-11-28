package com.github.leialoha.playerwarps.events;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionAttachmentInfo;

import com.github.leialoha.playerwarps.constructors.AdminWarp;
import com.github.leialoha.playerwarps.constructors.PlayerWarp;
import com.github.leialoha.playerwarps.constructors.WarpSign;
import com.github.leialoha.playerwarps.constructors.Warp.Visibility;
import com.github.leialoha.playerwarps.handler.SignHandler;
import com.github.leialoha.playerwarps.handler.WarpHandler;

public class PlayerEvents implements Listener {

	@EventHandler
	public void onSignEdit(SignChangeEvent event) {
		Player player = event.getPlayer();
		String[] lines = event.getLines();
		String warpName = lines[1];
		String playerName = lines[2];
		OfflinePlayer selectedPlayer = List.of(Bukkit.getOfflinePlayers()).stream().filter(op -> op.getName().equalsIgnoreCase(playerName)).findFirst().orElse(player);
		Visibility visibility = Visibility.getOrDefault(lines[3], Visibility.PUBLIC);

		List<PermissionAttachmentInfo> playerPermission = new ArrayList<>();
		((selectedPlayer.isOnline()) ? ((Player) selectedPlayer) : player).getEffectivePermissions().forEach(permission -> {
			if (permission.getPermission().startsWith("playerwarps.warp")) playerPermission.add(permission);
		});
		int allowedWarps = 0;

		Pattern permissionPattern = Pattern.compile("^playerwarps\\.warp\\.amount\\.(unlimited|\\d+)$", Pattern.CASE_INSENSITIVE);
		Pattern permissionRemovePattern = Pattern.compile("^playerwarps\\.warp\\.amount\\.", Pattern.CASE_INSENSITIVE);

		for (PermissionAttachmentInfo permission : playerPermission) {
			if (permissionPattern.matcher(permission.getPermission()).matches()) {
				String amountStr = permissionRemovePattern.matcher(permission.getPermission()).replaceAll("");
				if (amountStr.equalsIgnoreCase("unlimited")) allowedWarps = Integer.MAX_VALUE;
				else {
					try {
						int amount = Integer.parseInt(amountStr);
						allowedWarps = Math.max(amount, allowedWarps);
					} catch (NumberFormatException e) {}
				}
			}
		}

		boolean isWallSign = event.getBlock().getType().toString().endsWith("WALL_SIGN");
		Location lodestoneLocation = event.getBlock().getLocation().clone().add(0, -1, 0);
		boolean isLodestone = false;

		if (isWallSign) {
			WallSign signData = (WallSign) event.getBlock().getBlockData();
			BlockFace face = signData.getFacing();
			lodestoneLocation.add(face.getDirection().multiply(-1)).add(0, 1, 0);
		}

		isLodestone = lodestoneLocation.getBlock().getType().equals(Material.LODESTONE);
		WarpHandler wh = WarpHandler.getInstance();
		SignHandler sh = SignHandler.getInstance();

		if (lines[0].equalsIgnoreCase("[warp]")) {
			if (isLodestone) {
				int playerWarps = wh.getAmount(selectedPlayer);
				boolean isOtherPlayer = !((OfflinePlayer) player).equals(selectedPlayer);
				boolean hasPermissionOtherPlayer = isOtherPlayer && !player.hasPermission("playerwarps.warp.create.other");
				boolean isOtherPlayerOffline = isOtherPlayer && !selectedPlayer.isOnline();

				if ( hasPermissionOtherPlayer || isOtherPlayerOffline ||
						playerWarps >= allowedWarps || warpName.length() < 3 ||
						wh.hasWarp(selectedPlayer.getUniqueId() + ":" + warpName) || wh.hasLocation(lodestoneLocation)) {
					
					if (hasPermissionOtherPlayer) {
						player.sendMessage("§cCould not create warp... §7(REASON: Invalid Permission)");
					} else if (isOtherPlayerOffline) {
						player.sendMessage("§cCould not create warp... §7(REASON: Player Offline)");
					} else if (playerWarps >= allowedWarps) {
						player.sendMessage("§cCould not create warp... §7(REASON: Limit Reached)");
					} else if (warpName.length() < 3) {
						player.sendMessage("§cCould not create warp... §7(REASON: Invalid Name)");
					} else if (wh.hasWarp(selectedPlayer.getUniqueId() + ":" + warpName)) {
						player.sendMessage("§cCould not create warp... §7(REASON: Exists)");
					} else if (wh.hasLocation(lodestoneLocation)) {
						player.sendMessage("§cCould not create warp... §7(REASON: Invalid Location)");
					}
					event.getBlock().breakNaturally();
				} else {
					PlayerWarp newWarp = new PlayerWarp(selectedPlayer, warpName, lodestoneLocation, visibility, 0f);
					wh.addWarp(newWarp);

					String amountStr = (allowedWarps == Integer.MAX_VALUE) ? "∞" : allowedWarps + "";

					event.getBlock().setType(Material.AIR);
					player.sendMessage("§7Created warp \"" + warpName + "\" successfully! §8(" + (playerWarps + 1) + "/" + amountStr + ")");
					player.sendMessage(String.format("§7Visibility of warp \"%s\" was set to §f§l%s§7.", warpName, visibility.name()));
				}
			} else {
				if (wh.hasWarp(selectedPlayer.getUniqueId() + ":" + warpName)) {
					String target = selectedPlayer.getUniqueId() + ":" + warpName;
					WarpSign warpSign = new WarpSign(target, event.getBlock().getLocation(), WarpSign.Type.PLAYER, selectedPlayer.getUniqueId());
					sh.addSign(warpSign);
				} else {
					player.sendMessage("§cCould not create sign... §7(REASON: Warp Not Found)");
					event.getBlock().breakNaturally();
				}
			}
		} else if (lines[0].equalsIgnoreCase("[awarp]")) {
			if (isLodestone) {
				if (player.hasPermission("playerwarps.warp.create.admin")) {
					if (wh.hasWarp(warpName) || wh.hasLocation(lodestoneLocation) || warpName.length() < 3) {
						if (wh.hasWarp(warpName)) {
							player.sendMessage("§cCould not create admin warp... §7(REASON: Exists)");
						} else if (wh.hasLocation(lodestoneLocation)) {
							player.sendMessage("§cCould not create admin warp... §7(REASON: Invalid Location)");
						} else if (warpName.length() < 3) {
							player.sendMessage("§cCould not create admin warp... §7(REASON: Invalid Name)");
						}
						event.getBlock().breakNaturally();
					} else {
						AdminWarp newWarp = new AdminWarp(warpName, lodestoneLocation, visibility, 0f);
						wh.addWarp(newWarp);

						event.getBlock().setType(Material.AIR);
						player.sendMessage("§7Created admin warp \"" + warpName + "\" successfully!");
						player.sendMessage(String.format("§7Visibility of admin warp \"%s\" was set to §f§l%s§7.", warpName, visibility.name()));
					}
				} else {
					player.sendMessage("§cCould not create admin warp... §7(REASON: Invalid Permission)");
					event.getBlock().breakNaturally();
				}
			} else {
				if (wh.hasWarp(warpName)) {
					WarpSign warpSign = new WarpSign(warpName, event.getBlock().getLocation(), WarpSign.Type.ADMIN, null);
					sh.addSign(warpSign);
				} else {
					player.sendMessage("§cCould not create sign... §7(REASON: Warp Not Found)");
					event.getBlock().breakNaturally();
				}
			}
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Block block = event.getBlock();

		if (block.getType().equals(Material.LODESTONE)) {
			Player player = event.getPlayer();
			WarpHandler wh = WarpHandler.getInstance();

			if (wh.hasLocation(block.getLocation())) {
				if (wh.isPlayerWarp(block.getLocation())) {
					PlayerWarp warp = wh.getPlayerWarp(block.getLocation());
					OfflinePlayer owner = warp.getOwner();

					boolean isOtherPlayer = !((OfflinePlayer) player).equals(owner);

					if (isOtherPlayer && !player.hasPermission("playerwarps.warp.remove.other")) {
						event.setCancelled(true);
						player.sendMessage("§cCould not remove warp... §7(REASON: Invalid Owner)");
					} else {
						wh.removeWarp(warp);
						ItemStack sign = new ItemStack(Material.OAK_SIGN);
						block.getWorld().dropItem(block.getLocation(), sign);
						player.sendMessage("§7Successfully removed warp \"" + warp.getName() + "\" §8(Owner: " + owner.getName() + ")");
						
						if (isOtherPlayer && owner.isOnline()) {
							((Player) owner).sendMessage("§7Warp \"" + warp.getName() + "\" was removed by " + player.getName());
						}
					}
				} else {
					AdminWarp warp = wh.getAdminWarp(block.getLocation());
					if (!player.hasPermission("playerwarps.warp.remove.admin")) {
						event.setCancelled(true);
						player.sendMessage("§cCould not remove admin warp... §7(REASON: Invalid Permission)");
					} else {
						wh.removeWarp(warp);
						ItemStack sign = new ItemStack(Material.OAK_SIGN);
						block.getWorld().dropItem(block.getLocation(), sign);
						player.sendMessage("§7Successfully removed admin warp \"" + warp.getName() + "\"");
					}
				}
			}
		}
	}

	@EventHandler
	public void onRightCkick(PlayerInteractEvent event) {

		Player player = event.getPlayer();
		SignHandler sh = SignHandler.getInstance();
		WarpHandler wh = WarpHandler.getInstance();

		if (event.getClickedBlock() != null) {
			if (event.getHand() == null) return;
			if (!event.getHand().equals(EquipmentSlot.HAND)) return;
			if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;

			Location location = event.getClickedBlock().getLocation();

			if (event.getClickedBlock().getType().toString().endsWith("_SIGN")) {
				// SIGN
				if (event.getPlayer().isSneaking()) return;
				if (!sh.hasSign(location)) return;
				
				WarpSign sign = sh.getSign(location);
				if (sign != null && wh.hasWarp(sign.getTarget())) {
					if (sign.getWarpType().equals(WarpSign.Type.PLAYER)) {
						PlayerWarp warp = wh.getPlayerWarp(sign.getTarget());
						if (warp.getVisibility().equals(Visibility.PRIVATE)) {
							if (player.hasPermission("playerwarps.warp.use.other")) warp.teleport(player);
							else if (warp.getOwnerUUID().equals(player.getUniqueId())) warp.teleport(player);
							else player.sendMessage("§cCould not warp... §7(REASON: Warp Is Hidden)");
						} else warp.teleport(player);
					} else {
						AdminWarp warp = wh.getAdminWarp(sign.getTarget());
						if (warp.getVisibility().equals(Visibility.PRIVATE)) {
							if (player.hasPermission("playerwarps.warp.use.admin")) warp.teleport(player);
							else player.sendMessage("§cCould not warp... §7(REASON: Warp Is Hidden)");
						} else warp.teleport(player);
					}
 				} else {
					player.sendMessage("§cCould not warp... §7(REASON: Warp Not Found)");
				}
			} else if (event.getClickedBlock().getType().equals(Material.LODESTONE)) {
				// LODESTONE
				if (!wh.hasLocation(location)) return;
				if (!event.getPlayer().isSneaking()) return;
				if (!player.getEquipment().getItemInMainHand().getType().equals(Material.AIR)) return;

				if (wh.isPlayerWarp(location)) {
					// Player Warp
					PlayerWarp playerWarp = wh.getPlayerWarp(location);
					if (!playerWarp.getOwnerUUID().equals(player.getUniqueId()) && !player.hasPermission("playerwarps.warp.create.other")) return;

					float rotation = playerWarp.getRotation();
					playerWarp.setRotation(rotation + 45f);
				} else if (player.hasPermission("playerwarps.warp.create.admin")) {
					// Admin Warp
					AdminWarp adminWarp = wh.getAdminWarp(location);
					float rotation = adminWarp.getRotation();
					adminWarp.setRotation(rotation + 45f);
				}
			}
		}
	}
}
