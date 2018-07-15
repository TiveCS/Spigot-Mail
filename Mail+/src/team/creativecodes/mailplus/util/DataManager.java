package team.creativecodes.mailplus.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import team.creativecodes.mailplus.ConfigManager;
import team.creativecodes.mailplus.Mail;
import team.creativecodes.mailplus.menumanager.MailboxMenu;

public class DataManager {

	public static HashMap<OfflinePlayer, List<String>> mailboxData = new HashMap<OfflinePlayer, List<String>>();

	static Mail plugin = Mail.getPlugin(Mail.class);
	static String msg = null;
	final static String prefix = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("prefix"));

	public static void initMailboxData(OfflinePlayer p) {
		mailboxData.remove(p);
		mailboxData.put(p, getMailboxData(p));
	}

	public static List<String> getMailboxData(OfflinePlayer p) {
		List<String> list = null;
		try {
			ConfigurationSection cs = ConfigManager.getPlayerData(p.getUniqueId().toString()).getConfigurationSection("mailbox");
			Set<String> s = cs.getKeys(false);
			list = new ArrayList<String>(s);
			return list;
		} catch (Exception e) {
			return null;
		}
	}
	
	public static void readMail(Player sender, OfflinePlayer target, String uuid) {
		String msg = ChatColor.translateAlternateColorCodes('&', "&f&o" + ConfigManager.getPlayerData(target.getUniqueId().toString()).getString("mailbox." + uuid + ".message"));
		try {
			String s = ConfigManager.getPlayerData(target.getUniqueId().toString()).getString("mailbox." + uuid + ".sender"), t = ConfigManager.getPlayerData(target.getUniqueId().toString()).getString("mailbox." + uuid + ".target");
			if (t.length() > 0) {
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&lTo &f" + t));
			}else {
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&lSender &f" + s));
			}
		}catch(Exception e) {
			String s = ConfigManager.getPlayerData(target.getUniqueId().toString()).getString("mailbox." + uuid + ".sender");
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&lSender &f" + s));
		}
		sender.sendMessage(" ");
		sender.sendMessage(msg);
		sender.sendMessage(" ");
	}

	public static void sendMail(Player sender, OfflinePlayer target, String msg) {
		try {
			int date = Calendar.getInstance().get(Calendar.DATE), month = Calendar.getInstance().get(Calendar.MONTH),
					year = Calendar.getInstance().get(Calendar.YEAR);
			UUID mailuuid = UUID.randomUUID();
			String path = "mailbox." + mailuuid;
			
			ConfigManager.putData(sender.getUniqueId().toString(), path + ".message", msg);
			ConfigManager.putData(target.getUniqueId().toString(), path + ".message", msg);
			ConfigManager.putData(target.getUniqueId().toString(), path + ".sender", sender.getName());
			ConfigManager.putData(sender.getUniqueId().toString(), path + ".target", target.getName());
			ConfigManager.putData(sender.getUniqueId().toString(), path + ".Date", date + "/" + month + "/" + year);
			ConfigManager.putData(target.getUniqueId().toString(), path + ".Date", date + "/" + month + "/" + year);
	
			String sendermsg = plugin.getConfig().getString("messages.mailbox.sendmessage-sender.syntax-message"), senderdesc = plugin.getConfig().getString("messages.mailbox.sendmessage-sender.description-message");
			String targetmsg = plugin.getConfig().getString("messages.mailbox.sendmessage-target.syntax-message"), targetdesc = plugin.getConfig().getString("messages.mailbox.sendmessage-target.description-message");
			
			TextJson.sendHoverJson(sender, TextJson.placeholderReplace(sender, target, sendermsg),
					TextJson.placeholderReplace(sender, target, senderdesc));
			if (target.isOnline()) {
				TextJson.sendHoverJson(Bukkit.getPlayer(target.getName()), TextJson.placeholderReplace(sender, target, targetmsg),
						TextJson.placeholderReplace(sender, target, targetdesc));
			}
			
			initMailboxData(sender);
			initMailboxData(target);
		}catch(Exception e) {
			String sm = plugin.getConfig()
					.getString("messages.mailbox.sendmessage-error.syntax-message");
			String dm = plugin.getConfig()
					.getString("messages.mailbox.sendmessage-error.description-message");
			TextJson.sendHoverJson(sender, TextJson.placeholderReplace(sender, target, sm),
					TextJson.placeholderReplace(sender, target, dm));
			e.printStackTrace();
		}
	}
	
	public static void sendItemMail(Player sender, OfflinePlayer target, String msg, ItemStack item) {
		try {
			int date = Calendar.getInstance().get(Calendar.DATE), month = Calendar.getInstance().get(Calendar.MONTH),
					year = Calendar.getInstance().get(Calendar.YEAR);
			UUID mailuuid = UUID.randomUUID();
			String path = "mailbox." + mailuuid;
			
			ConfigManager.putData(sender.getUniqueId().toString(), path + ".message", msg);
			ConfigManager.putData(target.getUniqueId().toString(), path + ".message", msg);
			ConfigManager.putData(sender.getUniqueId().toString(), path + ".item", item);
			ConfigManager.putData(target.getUniqueId().toString(), path + ".item", item);
			ConfigManager.putData(target.getUniqueId().toString(), path + ".sender", sender.getName());
			ConfigManager.putData(sender.getUniqueId().toString(), path + ".target", target.getName());
			ConfigManager.putData(sender.getUniqueId().toString(), path + ".Date", date + "/" + month + "/" + year);
			ConfigManager.putData(target.getUniqueId().toString(), path + ".Date", date + "/" + month + "/" + year);
	
			String sendermsg = plugin.getConfig().getString("messages.mailbox.sendmessage-sender.syntax-message"), senderdesc = plugin.getConfig().getString("messages.mailbox.sendmessage-sender.description-message");
			String targetmsg = plugin.getConfig().getString("messages.mailbox.sendmessage-target.syntax-message"), targetdesc = plugin.getConfig().getString("messages.mailbox.sendmessage-target.description-message");
			
			TextJson.sendHoverJson(sender, TextJson.placeholderReplace(sender, target, sendermsg),
					TextJson.placeholderReplace(sender, target, senderdesc));
			if (target.isOnline()) {
				TextJson.sendHoverJson((Player) target, TextJson.placeholderReplace(sender, target, targetmsg),
						TextJson.placeholderReplace(sender, target, targetdesc));
			}
			
			initMailboxData(sender);
			initMailboxData(target);
		}catch(Exception e) {
			String sm = plugin.getConfig()
					.getString("messages.mailbox.sendmessage-error.syntax-message");
			String dm = plugin.getConfig()
					.getString("messages.mailbox.sendmessage-error.description-message");
			TextJson.sendHoverJson(sender, TextJson.placeholderReplace(sender, target, sm),
					TextJson.placeholderReplace(sender, target, dm));
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("deprecation")
	public static void deleteMail(Player executor, OfflinePlayer target, String mailuuid) {
		String path = "mailbox." + mailuuid;
		OfflinePlayer sender = null, rec = null;
		boolean notice = false;
		try {
			sender = Bukkit.getOfflinePlayer(ConfigManager.getPlayerData(target.getUniqueId().toString()).getString(path + ".sender"));
			rec = Bukkit.getOfflinePlayer(ConfigManager.getPlayerData(sender.getUniqueId().toString()).getString(path + ".target"));
		}catch(Exception e) {}
		try {
			if (sender != null && rec != null) {
				if (target.equals(sender) || !target.equals(sender)) {
					if (!ConfigManager.getPlayerData(target.getUniqueId().toString()).getItemStack(path + ".item").equals(null)) {
						TextJson.sendHoverJson(executor, TextJson.placeholderReplace(executor, target, plugin.getConfig().getString("messages.mailbox.claimitem-get.syntax-message")),
								TextJson.placeholderReplace(executor, target,plugin.getConfig().getString("messages.mailbox.claimitem-get.description-message")));
						ItemStack item = ConfigManager.getPlayerData(target.getUniqueId().toString()).getItemStack(path + ".item");
						executor.getInventory().addItem(item);
						notice = true;
					}
				}
			}
		}
		catch(Exception e) {}
		if (notice == false) {
			TextJson.sendHoverJson(executor, TextJson.placeholderReplace(executor, target, plugin.getConfig().getString("messages.mailbox.deletemail-info.syntax-message")),
				TextJson.placeholderReplace(executor, target, plugin.getConfig().getString("messages.mailbox.deletemail-info.description-message")));
		}
		ConfigManager.putData(target.getUniqueId().toString(), path, null);
		initMailboxData(target);
		if (MailboxMenu.inv.containsKey(executor)) {
			MailboxMenu.openMailbox(executor, target, MailboxMenu.pages.get(executor));
		}
		if (target.isOnline()) {
			if (MailboxMenu.inv.containsKey(target)) {
				MailboxMenu.openMailbox((Player) target, target, MailboxMenu.pages.get(target));
			}
		}
	}

}
