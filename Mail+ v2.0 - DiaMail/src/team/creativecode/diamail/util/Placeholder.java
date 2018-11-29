package team.creativecode.diamail.util;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import team.creativecode.diamail.Main;
import team.creativecode.diamail.activity.MailInfo;
import team.creativecode.diamail.activity.MailManager.PlaceholderType;
import team.creativecode.diamail.activity.Mailbox;
import team.creativecode.diamail.manager.PlayerMail;

public class Placeholder {
	
	static Main plugin = Main.getPlugin(Main.class);
	static String prefix = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("prefix"));

	public static String generalPlaceholder(String text) {
		text = text.replace("%prefix%", prefix);
		text = text.replace("%version%", plugin.getDescription().getVersion());
		return text;
	}
	
	public static HashMap<PlaceholderType, Object> initDefaultValue(HashMap<PlaceholderType, Object> map, PlayerMail pm){
		map.put(PlaceholderType.OUTBOX_SIZE, pm.getOutbox().size());
		map.put(PlaceholderType.INBOX_SIZE, pm.getInbox().size());
		return map;
	}
	
	public static String mapPlaceholder(HashMap<PlaceholderType, Object> map, String text) {
		for (PlaceholderType type : map.keySet()) {
			if (text.contains("%" + type.toString().toLowerCase() + "%")) {
				text = text.replace("%" + type.toString().toLowerCase() + "%", map.get(type).toString());
			}
		}
		return text;
	}
	
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
	
	public static String playermailPlaceholder(PlayerMail pm, String text) {
		return text;
	}
	 
	public static String mailinfoPlaceholder(MailInfo mi, String text) {
		String sender = "Unknown", target = "Unknown";
		try {
			try {
				sender = mi.getMail().getSender().getName();
			}catch(Exception e) { 
				sender = mi.getMail().getPlayerMail().getPlayer().getName();
			}
			try {
				target = mi.getMail().getTarget().getName();
			}catch(Exception e) {
				target = sender;
			}
		}catch(Exception e) {}
		
		try {
			text = text.replace("%mailinfo_sender%", sender);
			text = text.replace("%mailinfo_target%", target);
		}catch(Exception e) {}
		return text;
	}
	
	public static String mailboxPlaceholder(Mailbox mb, String text) {
		String mailtype = null, player = null, size = null, page = null;
		try {
			mailtype = mb.getMailboxType().toString() + "";
			player = mb.getPlayer().getName();
			size = mb.getMailboxSize() + "";
			page = mb.getPage() + "";
		}catch(Exception e) {}
		try {
			text = text.replace("%mailbox_mailtype%", mailtype);
			text = text.replace("%mailbox_player%", player);
			text = text.replace("%mailbox_size%", size);
			text = text.replace("%mailbox_page%", page);
		}catch(Exception e) {e.printStackTrace();}
		return text;
	}
	
}
