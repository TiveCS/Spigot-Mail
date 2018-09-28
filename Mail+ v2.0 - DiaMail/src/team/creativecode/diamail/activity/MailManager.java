package team.creativecode.diamail.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import team.creativecode.diamail.ConfigManager;
import team.creativecode.diamail.Main;
import team.creativecode.diamail.manager.PlayerMail;
import team.creativecode.diamail.util.ItemBuilder;
import team.creativecode.diamail.util.StringEditor;

public class MailManager {
	
	public static HashMap<Player, Inventory> mailInfo = new HashMap<Player, Inventory>();
	public static HashMap<Player, Mail> mailInfoMail = new HashMap<Player, Mail>();
	public static HashMap<Player, OfflinePlayer> mailSendTarget = new HashMap<Player, OfflinePlayer>();
	
	private static Main plugin = Main.getPlugin(Main.class);
	private final static String folder = plugin.getDataFolder().toString() + "/PlayerData";
	private final static int maxLength = plugin.getConfig().getInt("mail-perline-length");
	
	public final static String mailInfoTitle = "Mail Contents Menu";

	public static enum MailType{
		INBOX, OUTBOX, ALL;
	}
	
	public static void openMailInfoMenu(Player user, OfflinePlayer target, MailType mt, String mailUUID) {
		try {
			mailInfo.remove(user);
			mailInfoMail.remove(user);
			String[] sp = {};
			if (mt.equals(MailType.ALL)) {
				sp = mailUUID.split("box.");
				mailUUID = sp[1];
				mt = MailType.valueOf(sp[0].toUpperCase() + "BOX");
			}
			Mail m = new Mail(target, mt, UUID.fromString(mailUUID));
			mailInfoMail.put(user, m);
			List<String> lore = new ArrayList<String>();
			ItemStack item = null;
			Inventory inv = Bukkit.createInventory(null, 3*9, mailInfoTitle);
			for (int i = 0; i < 3*9; i++) {
				inv.setItem(i, ItemBuilder.createItem(Material.BLACK_STAINED_GLASS_PANE, " ", null, false));
			}
			lore = m.getMessage();
			lore.add(" ");
			lore.add(ChatColor.translateAlternateColorCodes('&', "&7▶ Read on the chat"));
			lore = StringEditor.normalizeColor(lore);
			inv.setItem(11, ItemBuilder.createItem(Material.PAPER, ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Message", lore, false));
			lore.clear();
			
			String ms = "", mr = "";
			try {
				if (!m.getTarget().equals(null)) {
					mr = m.getTarget().getName();
				}
			}catch(Exception e) {
				ms = m.getSender().getName();
			}
			if (mt.equals(MailType.INBOX)) {
				lore.add(ChatColor.translateAlternateColorCodes('&', "&7▶ " + ms));
			}else {
				lore.add(ChatColor.translateAlternateColorCodes('&', "&7▶ " + mr));
			}
			lore = StringEditor.normalizeColor(lore);
			inv.setItem(15, ItemBuilder.createItem(Material.PLAYER_HEAD, ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "Reply", lore, false));
			lore.clear();

			inv.setItem(3*9 - 5, ItemBuilder.createItem(Material.RED_TERRACOTTA, ChatColor.RED + "Mailbox", null, false));
			inv.setItem(2*9 - 1, ItemBuilder.createItem(Material.TNT, ChatColor.DARK_RED + "" + ChatColor.BOLD + "Delete Mail", null, false));
			
			try {
				if (!m.getItem().equals(null)) {
					item = m.getItem();
					inv.setItem(2*9 - 5, item);
				}
			}catch(Exception e) {
				lore.add(ChatColor.translateAlternateColorCodes('&', "&7▶ No item attached"));
				inv.setItem(2*9 - 5, ItemBuilder.createItem(Material.CHEST, ChatColor.YELLOW + "" + ChatColor.BOLD + "Attached Item", lore, false));
				lore.clear();
			}
			
			mailInfo.put(user, inv);
			user.openInventory(mailInfo.get(user));
		}catch(Exception e) {
			mailInfo.remove(user);
			mailInfoMail.remove(user);
			e.printStackTrace();
		}
	}
	
	public static void giveMailItem(Player user, OfflinePlayer target, UUID mailUUID) {
		try {
			PlayerMail pm = new PlayerMail(target);
			String path = "mailbox.inbox." + mailUUID.toString();
			ItemStack item = pm.getConfig().getItemStack(path + ".item");
			user.getInventory().addItem(item);
			ConfigManager.inputData(pm.getFile(), path, null);
			user.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("prefix") + " You've got an item.."));
			if (pm.getConfig().getStringList(path + ".message").isEmpty()) {
				deleteMail(user, target, path);
			}
		}catch(Exception e) {}
	}
	
	public static void checkMail(Player user, OfflinePlayer p) {
		PlayerMail pm = new PlayerMail(p);
		int size = pm.getInbox().size() + pm.getOutbox().size();
		if (!user.equals(null)) {
			user.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("prefix") + " You have &a" + size + " &fmail(s)"));
		}
	}
	
	public static void deleteMail(Player user, OfflinePlayer target, MailType mt, UUID mailUUID) {
		try {
			File file = new File(folder, target.getUniqueId().toString() + ".yml");
			giveMailItem(user, target, mailUUID);
			ConfigManager.inputData(file, "mailbox." + mt.toString().toLowerCase() + "." + mailUUID.toString(), null);
			
			deleteMailSound(user, target, mailUUID);
		}catch(Exception e) {}
	}
	
	public static void deleteMailSound(Player user, OfflinePlayer target, UUID mailUUID) {
		PlayerMail pm = new PlayerMail(user);
		if (pm.getSettings().get("notification-display").toString().equalsIgnoreCase("Message")) {
			if (Boolean.parseBoolean(pm.getSettings().get("notification-display-enable").toString())) {
				user.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("prefix") + " &eDelete mail &7" + mailUUID.toString()));
			}
		}
		if (Boolean.parseBoolean(pm.getSettings().get("notification-sound").toString())) {
			String[] s = plugin.getConfig().getString("activity-sound.deletemail-notification").split("-");
			int volume = Integer.parseInt(s[2]), pitch = Integer.parseInt(s[1]);
			user.playSound(user.getLocation(), Sound.valueOf(s[0].toUpperCase()), volume,pitch);
		}
	}
	 
	public static void deleteMail(Player user, OfflinePlayer target, String path) {
		File file = new File(folder, target.getUniqueId().toString() + ".yml");
		String[] split = {};
		try {
			if (path.contains("mailbox.inbox.")) {
				split = path.split("mailbox.inbox.");
			}else {
				split = path.split("mailbox.outbox.");
			}
		}catch(Exception e) {e.printStackTrace();}
		UUID uuid = UUID.fromString(split[1]);
		giveMailItem(user, target, uuid);
		ConfigManager.inputData(file, path, null);
		deleteMailSound(user, target, uuid);
	}
	
	public static void sendMail(Player u, OfflinePlayer t) {
		mailSendTarget.remove(u);
		mailSendTarget.put(u,t);
		u.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("prefix") + " Preparing to send mail for &b" + t.getName()));
		u.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("prefix") + " Type &cExit &fto cancel send mail"));
		u.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("prefix") + " Type &aDone &fto finish the mail"));
		u.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("prefix") + " Please type the message on chat!"));
	}
	
	public static void sendMail(Player u, OfflinePlayer t, List<String> msg, ItemStack item) {
		try {
			UUID mail = UUID.randomUUID();
			File uf = new File(folder, u.getUniqueId().toString() + ".yml"), tf = new File(folder, t.getUniqueId().toString() + ".yml");

			ConfigManager.inputData(uf, "mailbox.inbox." + mail + ".sender", u.getUniqueId().toString());
			ConfigManager.inputData(tf, "mailbox.inbox." + mail + ".message", msg);
			ConfigManager.inputData(uf, "mailbox.inbox." + mail + ".item", item);
			
			if (!u.equals(t)) {
				ConfigManager.inputData(uf, "mailbox.outbox." + mail + ".target", t.getUniqueId().toString());
				ConfigManager.inputData(uf, "mailbox.outbox." + mail + ".message", msg);
				ConfigManager.inputData(uf, "mailbox.outbox." + mail + ".sender", item);
			}
			
			sendMailSound(u, t);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void sendMail(Player u, OfflinePlayer t, String msg, ItemStack item) {
		try {
			UUID mail = UUID.randomUUID();
			File uf = new File(folder, u.getUniqueId().toString() + ".yml"), tf = new File(folder, t.getUniqueId().toString() + ".yml");

			ConfigManager.inputData(uf, "mailbox.inbox." + mail + ".sender", u.getUniqueId().toString());
			ConfigManager.inputData(tf, "mailbox.inbox." + mail + ".message", StringEditor.stringToList(msg, maxLength));
			ConfigManager.inputData(uf, "mailbox.inbox." + mail + ".item", item);
			
			if (!u.equals(t)) {
				ConfigManager.inputData(uf, "mailbox.outbox." + mail + ".target", t.getUniqueId().toString());
				ConfigManager.inputData(uf, "mailbox.outbox." + mail + ".message", StringEditor.stringToList(msg, maxLength));
				ConfigManager.inputData(uf, "mailbox.outbox." + mail + ".sender", item);
			}
			
			sendMailSound(u, t);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void sendMailSound(Player u, OfflinePlayer t) {
		PlayerMail um = new PlayerMail(u),tm = new PlayerMail(t);
		if (um.getSettings().get("notification-display").toString().equalsIgnoreCase("Message")) {
			if (Boolean.parseBoolean(um.getSettings().get("notification-display-enable").toString())) {
				u.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("prefix") + " Your mail has been sent to &b" + t.getName()));
			}
		}
		if (Boolean.parseBoolean(um.getSettings().get("notification-sound").toString())) {
			String[] s = plugin.getConfig().getString("activity-sound.getmail-notification").split("-");
			int volume = Integer.parseInt(s[2]), pitch = Integer.parseInt(s[1]);
			u.playSound(u.getLocation(), Sound.valueOf(s[0].toUpperCase()), volume,pitch);
		}
		if (t.isOnline()) {
			Player p = t.getPlayer();
			if (tm.getSettings().get("notification-display").toString().equalsIgnoreCase("Message")) {
				if (Boolean.parseBoolean(tm.getSettings().get("notification-display-enable").toString())) {
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("prefix") + " You have got mail from &e" + t.getName()));
				}
			}
			if (Boolean.parseBoolean(tm.getSettings().get("notification-sound").toString())) {
				String[] s = plugin.getConfig().getString("activity-sound.getmail-notification").split("-");
				int volume = Integer.parseInt(s[2]), pitch = Integer.parseInt(s[1]);
				p.playSound(p.getLocation(), Sound.valueOf(s[0].toUpperCase()), volume,pitch);
			}
		}
	}

}
