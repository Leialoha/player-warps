package com.github.leialoha.playerwarps.utils;

import java.util.List;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemUtils {
	
	public static void setDisplay(ItemStack item, String name, String... lore) {
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		meta.setLore(List.of(lore));
		item.setItemMeta(meta);
	}

}
