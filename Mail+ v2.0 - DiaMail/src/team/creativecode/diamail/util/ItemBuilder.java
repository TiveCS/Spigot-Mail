package team.creativecode.diamail.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import team.creativecode.diamail.Main;

public class ItemBuilder {
	
	private static Main plugin = Main.getPlugin(Main.class);

	public static ItemStack rebuildItem(ItemStack item) {
		List<String> lore = new ArrayList<String>();
		ItemMeta meta = item.getItemMeta();
		String name = meta.getDisplayName();
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
		if (meta.hasLore()) {
			lore = meta.getLore();
		}
		try {
			for (int i = 0; i < lore.size(); i++) {
				lore.set(i, ChatColor.translateAlternateColorCodes('&', lore.get(i)));
			}
		}catch(Exception e) {}
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
	
	public static ItemStack createItem(Material mat, String name, List<String> lore, boolean unbreakable) {
		ItemStack item = new ItemStack(mat);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
		try {
			for (int i = 0; i < lore.size(); i++) {
				lore.set(i, ChatColor.translateAlternateColorCodes('&', lore.get(i)));
			}
		}catch(Exception e) {}
		meta.setLore(lore);
		meta.setUnbreakable(unbreakable);
		item.setItemMeta(meta);
		return item;
	}
	
	public static ItemStack createItem(Material mat, String name, List<String> lore,boolean unbreakable, boolean shiny) {
		ItemStack item = new ItemStack(mat);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
		try {
			for (int i = 0; i < lore.size(); i++) {
				lore.set(i, ChatColor.translateAlternateColorCodes('&', lore.get(i)));
			}
		}catch(Exception e) {}
		meta.setLore(lore);
		meta.setUnbreakable(unbreakable);
		/*if (shiny == true) {
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		}*/
		item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
		item.setItemMeta(meta);
		return item;
	}
	
	// path = player-settings-option.<setting>.icon.<value>
	// Example: path = player-settings-option.notification-display.icon.Message
	public static ItemStack getSettingItem(String path) {
		ItemStack item = null;
		try {
			Material mat = Material.valueOf(plugin.getConfig().get(path + ".Material").toString().toUpperCase());
			String name = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString(path + ".Name"));
			List<String> lore = new ArrayList<String>(plugin.getConfig().getStringList(path + ".Lore"));
			try {
				for (int i = 0; i < lore.size(); i++) {
					lore.set(i, ChatColor.translateAlternateColorCodes('&', lore.get(i)));
				}
			}catch(Exception e) {}
			item = new ItemStack(mat);
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(name);
			meta.setLore(lore);
			item.setItemMeta(meta);
		}catch(Exception e) {
			System.out.println("Item or setting path is not found! [" + path + "]");
		}
		return item;
	}
	
	public static ItemStack getSettingItem(String setting, String value) {
		String path = "player-settings-option." + setting + ".icon." + value;
		return getSettingItem(path);
	}
	
}
