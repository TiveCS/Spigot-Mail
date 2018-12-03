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

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import team.creativecode.diamail.ConfigManager;
import team.creativecode.diamail.Main;
import team.creativecode.diamail.manager.MessageManager;
import team.creativecode.diamail.manager.PlayerMail;
import team.creativecode.diamail.manager.MessageManager.MessageType;
import team.creativecode.diamail.util.ItemBuilder;
import team.creativecode.diamail.util.Placeholder;
import team.creativecode.diamail.util.StringEditor;

public class MailManager {
	
	public static HashMap<Player, Inventory> mailInfo = new HashMap<Player, Inventory>();
	public static HashMap<Player, Mail> mailInfoMail = new HashMap<Player, Mail>();
	public static HashMap<Player, OfflinePlayer> mailSendTarget = new HashMap<Player, OfflinePlayer>();
	
	private static Main plugin = Main.getPlugin(Main.class);
	private final static String folder = plugin.getDataFolder().toString() + "/PlayerData";
	private final static int maxLength = plugin.getConfig().getInt("mail-perline-length");

	public static enum MailType{
		INBOX, OUTBOX, ALL;
	}
	
	public static enum PlaceholderType{
		SENDER, TARGET, SIZE, SUBJECT, LINE, INBOX_SIZE, OUTBOX_SIZE;
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
			MailInfo mi = new MailInfo(user, 1, mailInfoMail.get(user));
			String title = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("menu-manager.mailinfo.title"));
			title = Placeholder.mailinfoPlaceholder(mi, title);
			Inventory inv = Bukkit.createInventory(null, 3*9, title);
			for (int i = 0; i < 3*9; i++) {
				inv.setItem(i, ItemBuilder.createItem(Material.BLACK_STAINED_GLASS_PANE, " ", null, false));
			}
			lore = m.getMessage();
			lore.add(" ");
			lore.add(ChatColor.translateAlternateColorCodes('&', "&7- Read on the chat"));
			lore = StringEditor.normalizeColor(lore);
			inv.setItem(11, ItemBuilder.createItem(Material.PAPER, ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Message", lore, false));
			lore.clear();
			
			String ms = "", mr = "";
			try {
				if (!m.getTarget().equals(null)) {
					mr = m.getTarget().getName();
				}
				else if (!m.getSender().equals(null)) {
					ms = m.getSender().getName();
				}
			}catch(Exception e) {}
			
			if (mt.equals(MailType.INBOX)) {
				lore.add(ChatColor.translateAlternateColorCodes('&', "&7- " + ms));
			}else {
				lore.add(ChatColor.translateAlternateColorCodes('&', "&7- " + mr));
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
				lore.add(ChatColor.translateAlternateColorCodes('&', "&7- No item attached"));
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
			MessageManager.send(user, MessageType.GET_ITEM_MAIL);
			
			if (pm.getConfig().getStringList(path + ".message").isEmpty()) {
				deleteMail(user, target, path);
				user.closeInventory();
			}
		}catch(Exception e) {}
	}
	
	public static void checkMail(Player user, OfflinePlayer p) {
		PlayerMail pm = new PlayerMail(p);
		int size = pm.getInbox().size() + pm.getOutbox().size();
		if (!user.equals(null)) {
			HashMap<PlaceholderType, Object> m = new HashMap<PlaceholderType, Object>();;
			m.put(PlaceholderType.SUBJECT, "check-mail");
			m.put(PlaceholderType.SIZE, size);
			
			notificationMail(user, m);
		}
	}
	
	public static void checkMailScheduled(Player user) {
		PlayerMail pm = new PlayerMail(user);
		long interval = 20*Long.parseLong(pm.getSettings().get("checking-mailbox-interval").toString());
		if (interval < 0) {
			return;
		}
		final boolean online = user.isOnline();
		
		if (online) {
			Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, new Runnable() {

				@Override
				public void run() {
					checkMail(user, user);
					checkMailScheduled(user);
				}
				
			}, interval);
		}else {
			return;
		}
	}
	
	public static void deleteMail(Player user, OfflinePlayer target, MailType mt, UUID mailUUID) {
		try {
			File file = new File(folder, target.getUniqueId().toString() + ".yml");
			giveMailItem(user, target, mailUUID);
			ConfigManager.inputData(file, "mailbox." + mt.toString().toLowerCase() + "." + mailUUID.toString(), null);
			
			HashMap<PlaceholderType, Object> m = new HashMap<PlaceholderType, Object>();
			m.put(PlaceholderType.SUBJECT, "delete-mail");
			notificationMail(user, m);
		}catch(Exception e) {}
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
		
		HashMap<PlaceholderType, Object> m = new HashMap<PlaceholderType, Object>();
		m.put(PlaceholderType.SUBJECT, "delete-mail");
		notificationMail(user, m);
	}
	
	public static void sendallMail(Player u, List<String> msg, ItemStack item) {
		UUID mail = UUID.randomUUID();
		File playerFile = new File(folder, u.getUniqueId().toString() + ".yml");
		File[] playerFiles = new File(folder).listFiles();
		HashMap<PlaceholderType, Object> m = new HashMap<PlaceholderType, Object>();
		m.put(PlaceholderType.SENDER, u.getName());
		m.put(PlaceholderType.SUBJECT, "sendall-mail");
		
		notificationMail(u, m);
		
		for (File file : playerFiles) {
			try {
				String name = file.getName().replace(".yml", "");
				OfflinePlayer op = Bukkit.getOfflinePlayer(UUID.fromString(name));
				if (!op.equals(u)) {
					ConfigManager.inputData(file, "mailbox.inbox." + mail + ".sender", u.getUniqueId().toString());
					ConfigManager.inputData(file, "mailbox.inbox." + mail + ".message", msg);
					ConfigManager.inputData(file, "mailbox.inbox." + mail + ".item", item);
					
					if (op.isOnline()) {
						m.put(PlaceholderType.SUBJECT, "get-mail");
						notificationMail(op.getPlayer(),  m);
					}
				}
			}catch(Exception e) {}
		}
		
		ConfigManager.inputData(playerFile, "mailbox.outbox." + mail + ".target", "All Players");
		ConfigManager.inputData(playerFile, "mailbox.outbox." + mail + ".message", msg);
		ConfigManager.inputData(playerFile, "mailbox.outbox." + mail + ".item", item);
	}
	
	public static void sendMail(Player u, OfflinePlayer t) {
		mailSendTarget.remove(u);
		mailSendTarget.put(u,t);
		MessageManager.send(u, MessageType.PRE_SEND_MAIL);
	}
	
	public static void sendMail(Player u, OfflinePlayer t, List<String> msg, ItemStack item) {
		try {
			UUID mail = UUID.randomUUID();
			File uf = new File(folder, u.getUniqueId().toString() + ".yml"), tf = new File(folder, t.getUniqueId().toString() + ".yml");

			ConfigManager.inputData(tf, "mailbox.inbox." + mail + ".sender", u.getUniqueId().toString());
			ConfigManager.inputData(tf, "mailbox.inbox." + mail + ".message", msg);
			ConfigManager.inputData(tf, "mailbox.inbox." + mail + ".item", item);
			
			if (!u.equals(t)) {
				ConfigManager.inputData(uf, "mailbox.outbox." + mail + ".target", t.getUniqueId().toString());
				ConfigManager.inputData(uf, "mailbox.outbox." + mail + ".message", msg);
				ConfigManager.inputData(uf, "mailbox.outbox." + mail + ".sender", item);
			}
			
			HashMap<PlaceholderType, Object> m = new HashMap<PlaceholderType, Object>();
			m.put(PlaceholderType.SENDER, u.getName());
			m.put(PlaceholderType.TARGET, t.getName());
			m.put(PlaceholderType.SUBJECT, "send-mail");
			
			notificationMail(u, m);
			if (t.isOnline()) {
				m.put(PlaceholderType.SUBJECT, "get-mail");
				notificationMail(t.getPlayer(),  m);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void sendMail(Player u, OfflinePlayer t, String msg, ItemStack item) {
		try {
			UUID mail = UUID.randomUUID();
			File uf = new File(folder, u.getUniqueId().toString() + ".yml"), tf = new File(folder, t.getUniqueId().toString() + ".yml");

			ConfigManager.inputData(tf, "mailbox.inbox." + mail + ".sender", u.getUniqueId().toString());
			ConfigManager.inputData(tf, "mailbox.inbox." + mail + ".message", StringEditor.stringToList(msg, maxLength));
			ConfigManager.inputData(tf, "mailbox.inbox." + mail + ".item", item);
			
			if (!u.equals(t)) {
				ConfigManager.inputData(uf, "mailbox.outbox." + mail + ".target", t.getUniqueId().toString());
				ConfigManager.inputData(uf, "mailbox.outbox." + mail + ".message", StringEditor.stringToList(msg, maxLength));
				ConfigManager.inputData(uf, "mailbox.outbox." + mail + ".sender", item);
			}
			
			HashMap<PlaceholderType, Object> m = new HashMap<PlaceholderType, Object>();
			m.put(PlaceholderType.SENDER, u.getName());
			m.put(PlaceholderType.TARGET, t.getName());
			m.put(PlaceholderType.SUBJECT, "send-mail");
			
			notificationMail(u, m);
			if (t.isOnline()) {
				m.put(PlaceholderType.SUBJECT, "get-mail");
				notificationMail(t.getPlayer(),  m);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void notificationMail(Player p, HashMap<PlaceholderType, Object> map) {
		PlayerMail pm = new PlayerMail(p);
		String msg = null;
		List<String> msglist = new ArrayList<String>();
		String type = pm.getSettings().get("notification-display").toString();
		
		// Sound handler
		boolean useSound = Boolean.parseBoolean(pm.getSettings().get("notification-sound").toString());
		String subj, subj2, path;
		String[] s;
		subj = map.get(PlaceholderType.SUBJECT).toString();
		subj2 = subj.replace("-", "");
		path = "action.value." + subj;
		try {
			s = plugin.getConfig().getString("activity-sound." + subj2 + "-notification").split("-");
		}catch(Exception e) {
			s = plugin.getConfig().getString("activity-sound.default").split("-");
		}
		
		Sound sound = Sound.valueOf(s[0].toUpperCase());
		int volume = Integer.parseInt(s[2]), pitch = Integer.parseInt(s[1]);
		
		map = Placeholder.initDefaultValue(map, pm);
		
		try {
			if (MessageManager.isList(MessageManager.get(path))) {
				msglist = MessageManager.getList(path);
			}else {
				msg = ChatColor.translateAlternateColorCodes('&', MessageManager.get(path).toString());
				msglist.add(msg);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		// use placeholder
		for (int i = 0; i < msglist.size(); i++) {
			msglist.set(i, Placeholder.generalPlaceholder(msglist.get(i)));
			msglist.set(i, Placeholder.mapPlaceholder(map, msglist.get(i)));
		}
		
		if (type.equalsIgnoreCase("Message")) {
			for (String m : msglist) {
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', m));
			}
		}else if (type.equalsIgnoreCase("Actionbar")) {
			final List<String> rawmsglist = msglist;
			int count = 0;
			for (String m : rawmsglist) {
				Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, new Runnable() {

					@Override
					public void run() {
						p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder(m).create());
					}
					
				}, count*40L);
				count++;
			}
			
		}else if (type.equalsIgnoreCase("Title")) {
			final List<String> rawmsglist = msglist;
			int count = 0;
			for (String m : rawmsglist) {
				Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, new Runnable() {

					@Override
					public void run() {
						p.sendTitle(" ", m, 10, 20, 10);
					}
					
				}, count*40L);
				count++;
			}
		}
			
		if (useSound) {
			p.playSound(p.getLocation(), sound, volume, pitch);
		}
	}

}
