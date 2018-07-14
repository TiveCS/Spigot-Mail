package team.creativecodes.mailplus.util;

import java.util.HashMap;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import team.creativecodes.mailplus.Mail;

public class TextJson {

	static Mail plugin = Mail.getPlugin(Mail.class);

	public static void sendHoverJson(Player p, String text, String hoverText) {
		TextComponent msg = new TextComponent(ChatColor.translateAlternateColorCodes('&', text));
		msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
				new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', hoverText)).create()));

		p.spigot().sendMessage(msg);
	}

	public static void sendRunCommandJson(Player p, String text, String commandText) {
		TextComponent msg = new TextComponent(ChatColor.translateAlternateColorCodes('&', text));
		msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + commandText));

		p.spigot().sendMessage(msg);
	}

	public static void sendRunCommandJson(Player p, String text, String commandText, String hoverText) {
		TextComponent msg = new TextComponent(ChatColor.translateAlternateColorCodes('&', text));
		msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + commandText));
		msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
				new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', hoverText)).create()));
		p.spigot().sendMessage(msg);
	}

	// Placeholder area
	
	public static HashMap<String, String> initPlaceholder(OfflinePlayer sender, OfflinePlayer target) {
		HashMap<String, String> placeholder = new HashMap<String, String>();
		try {
		
			placeholder.put("PREFIX", ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("prefix")));
			placeholder.put("MAILBOX_AMOUNT_TARGET", DataManager.mailboxData.get(target).size() + "");
			placeholder.put("MAILBOX_AMOUNT_SENDER", DataManager.mailboxData.get(sender).size() + "");
			placeholder.put("TARGET", target.getName());
			placeholder.put("SENDER", sender.getName());
		}catch(Exception e) {}
		
		return placeholder;
	}
	
	public static String placeholderReplace(OfflinePlayer sender, OfflinePlayer target, String msg) {
		HashMap<String, String> placeholder = new HashMap<String, String>(initPlaceholder(sender, target));
		for (String key : placeholder.keySet()) {
			if (msg.contains("{" + key + "}")) {
				msg = msg.replace("{" + key + "}", placeholder.get(key));
			}
		}
		return msg;
	}
	
	//----------------------

}
