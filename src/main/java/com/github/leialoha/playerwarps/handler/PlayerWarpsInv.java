package com.github.leialoha.playerwarps.handler;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.github.leialoha.playerwarps.PlayerWarps;
import com.github.leialoha.playerwarps.utils.ItemUtils;

public class PlayerWarpsInv {
	
	public static Inventory create() {

		FileConfiguration config = PlayerWarps.getPlugin(PlayerWarps.class).getConfig();

		boolean min = !config.getBoolean("gui.compact");
		boolean decor = config.getBoolean("gui.decor");

		Inventory inv = Bukkit.createInventory(null, 54, "Player Warps");
		
		ItemStack nullHead = new ItemStack(Material.PLAYER_HEAD);
		ItemStack bars = new ItemStack(Material.IRON_BARS);
		ItemStack blackGlass = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
		ItemStack grayGlass = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
		ItemStack firstArrow = new ItemStack(Material.ARROW);
		ItemStack prevArrow = new ItemStack(Material.TIPPED_ARROW);
		ItemStack nextArrow = new ItemStack(Material.TIPPED_ARROW);
		ItemStack lastArrow = new ItemStack(Material.SPECTRAL_ARROW);
		ItemStack exit = new ItemStack(Material.BARRIER);

		SkullMeta nullHeadMeta = (SkullMeta) nullHead.getItemMeta();

		PotionMeta prevArrowMeta = (PotionMeta) prevArrow.getItemMeta();
		prevArrowMeta.setColor(Color.RED);
		prevArrowMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
		prevArrow.setItemMeta(prevArrowMeta);
		
		PotionMeta nextArrowMeta = (PotionMeta) nextArrow.getItemMeta();
		nextArrowMeta.setColor(Color.GREEN);
		nextArrowMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
		nextArrow.setItemMeta(nextArrowMeta);

		ItemUtils.setDisplay(nullHead, "§7§l???");
		ItemUtils.setDisplay(bars, "§0");
		ItemUtils.setDisplay(blackGlass, "§0");
		ItemUtils.setDisplay(grayGlass, "§0");
		ItemUtils.setDisplay(firstArrow, "§7First Page", "§8Click to go to the first page");
		ItemUtils.setDisplay(prevArrow, "§7Prev Page", "§8Click to go to the prev page");
		ItemUtils.setDisplay(nextArrow, "§7Next Page", "§8Click to go to the next page");
		ItemUtils.setDisplay(lastArrow, "§7Last Page", "§8Click to go to the last page");
		ItemUtils.setDisplay(exit, "§c§lExit");

		for (int i = 0; i < ((decor) ? 7 : 9); i++) {
			int offset = decor ? 1 : 0;
			inv.setItem(offset+i, nullHead);
			inv.setItem(offset+i+9, nullHead);
			inv.setItem(offset+i+18, nullHead);
			inv.setItem(offset+i+27, nullHead);
			if (!min) inv.setItem(offset+i+36, nullHead);
		}
		for (int i = 0; i < 5; i++) {
			if (decor) inv.setItem(i*9, bars);
			if (decor) inv.setItem(i*9+8, bars);
		}
		for (int i = 0; i < 2; i++) {
			inv.setItem(i+47, (min) ? grayGlass : blackGlass);
			inv.setItem(i+50, (min) ? grayGlass : blackGlass);
		}
		for (int i = 0; i < 9; i++) {
			if (min) inv.setItem(i+36, blackGlass);
		}
		inv.setItem(45, firstArrow);
		inv.setItem(46, prevArrow);
		inv.setItem(49, exit);
		inv.setItem(52, nextArrow);
		inv.setItem(53, lastArrow);

		return inv;
	}

}
