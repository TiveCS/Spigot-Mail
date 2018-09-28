package team.creativecode.diamail.util;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import team.creativecode.diamail.manager.PlayerMail;

public class Placeholder {

	public static ItemStack playerSettingValuePlaceholder(PlayerMail pm, ItemStack item, String setting) {
		try {
			String value = pm.getConfig().get("settings." + setting).toString();
			ItemMeta meta = item.getItemMeta();
			String name = meta.getDisplayName();
			name = name.replace("%value%", value);
			meta.setDisplayName(name);
			if (meta.hasLore()) {
				for (int i = 0; i < meta.getLore().size(); i++) {
					meta.getLore().set(i, meta.getLore().get(i).replace("%value%", value));
				}
			}
			item.setItemMeta(meta);
		}catch(Exception e) {}
		return item;
	}
	
	public static String playerSettingValuePlaceholder(PlayerMail pm, String text, String setting) {
		try {
			String value = pm.getConfig().get("settings." + setting).toString();
			text = text.replace("%value%", value);
		}catch(Exception e) {}
		return text;
	}
	
}
