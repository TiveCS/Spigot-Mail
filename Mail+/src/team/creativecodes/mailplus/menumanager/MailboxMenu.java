package team.creativecodes.mailplus.menumanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.md_5.bungee.api.ChatColor;
import team.creativecodes.mailplus.ConfigManager;
import team.creativecodes.mailplus.util.DataManager;

public class MailboxMenu {
	
	public static HashMap<Player, Inventory> inv = new HashMap<Player, Inventory>();
	public static HashMap<Player, OfflinePlayer> targets = new HashMap<Player, OfflinePlayer>();
	public static HashMap<Player, Integer> pages = new HashMap<Player, Integer>();
	public static HashMap<Player, String> mode = new HashMap<Player, String>();
	
	public static void openMailbox(Player p, OfflinePlayer target, int page) {
		try {
			p.closeInventory();
			pages.remove(p);
			pages.put(p, page);
			List<String> data = new ArrayList<String>(DataManager.mailboxData.get(target));
			int num = ((3*7)*page) - 3*7,
				size = 0;
			try {
				size = DataManager.mailboxData.get(p).size();
			}catch(Exception e) {}
			Inventory menu = Bukkit.createInventory(null, 5*9, ChatColor.DARK_BLUE + target.getName() + " Mailbox | " + size + " Mails");
			inv.remove(p);
			inv.put(p, menu);
			targets.remove(p);
			targets.put(p, target);
			
			for (int i= 0; i < 5; i++) {
				for (int slot = 0; slot < 9; slot++) {
					ItemStack item = null;
					ItemMeta meta;
					if (i + 1 == 1 || i + 1 == 5) {
						item = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
						meta = item.getItemMeta();
						meta.setDisplayName(" ");
						item.setItemMeta(meta);
					}else {
						if (slot == 0 || slot == 8) {
							item = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5);
							meta = item.getItemMeta();
							if (slot == 8) {
								meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&aNext Page"));
							}
							else if (slot == 0) {
								if (page > 1) {
									meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&aPrevious Page"));
								}else {
									item = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
									meta = item.getItemMeta();
									meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', " "));
								}
							}
							item.setItemMeta(meta);
						}else {
							List<String> lore = new ArrayList<String>();
							try {
								if (!data.get(num).equals(null)) {
									String msg = ChatColor.translateAlternateColorCodes('&',"&f" + ConfigManager.getPlayerData(target.getUniqueId().toString()).getString("mailbox." + data.get(num) + ".message"));
									try {
										item = ConfigManager.getPlayerData(target.getUniqueId().toString()).getItemStack("mailbox." + DataManager.mailboxData.get(target).get(num) + ".item").clone();
										String mail = data.get(num);
										lore.clear();
										meta = item.getItemMeta();
										if (meta.hasLore()) {
											lore = new ArrayList<String>(meta.getLore());
										}
										lore.add(" ");
										try {
											if (!(ConfigManager.getPlayerData(target.getUniqueId().toString()).getString("mailbox." + mail + ".sender").equals(null))) {
												lore.add(ChatColor.translateAlternateColorCodes('&', "&a&lSender&f&l: &f" + ConfigManager.getPlayerData(target.getUniqueId().toString()).getString("mailbox." + mail + ".sender")));
											}
										}catch(Exception b) {
											lore.add(ChatColor.translateAlternateColorCodes('&', "&a&lTo&f&l: &f" + ConfigManager.getPlayerData(target.getUniqueId().toString()).getString("mailbox." + mail + ".target")));
										}
										lore.add(" ");
										lore.add(ChatColor.translateAlternateColorCodes('&', "&3&lMessage&f&l:"));
										lore.add(msg);
										meta.setLore(lore);
										item.setItemMeta(meta);
									}catch(Exception e) {
										String mail = data.get(num);
										lore.clear();
										item = new ItemStack(Material.PAPER);
										meta = item.getItemMeta();
										meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', ConfigManager.getPlayerData(target.getUniqueId().toString()).getString("mailbox." + mail + ".Date")));
										lore.add(" ");
										try {
											if (!(ConfigManager.getPlayerData(target.getUniqueId().toString()).getString("mailbox." + mail + ".sender").equals(null))) {
												lore.add(ChatColor.translateAlternateColorCodes('&', "&a&lSender&f&l: &f" + ConfigManager.getPlayerData(target.getUniqueId().toString()).getString("mailbox." + mail + ".sender")));
											}
										}catch(Exception b) {
											lore.add(ChatColor.translateAlternateColorCodes('&', "&a&lTo&f&l: &f" + ConfigManager.getPlayerData(target.getUniqueId().toString()).getString("mailbox." + mail + ".target")));
										}
										lore.add(" ");
										lore.add(ChatColor.translateAlternateColorCodes('&', "&3&lMessage&f&l:"));
										lore.add(msg);
										meta.setLore(lore);
										item.setItemMeta(meta);
									}
								}
							}catch(Exception t) {}
							num++;
						}
					}
					menu.setItem(i*9 + slot, item);
				}
			}
			List<String> lore = new ArrayList<String>();
			if (mode.get(p).equals("READ")) {
				ItemStack item = new ItemStack(Material.WOOL, 1, (short) 13);
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&e&lMode&f&l: &aRead Message"));
				lore.add(" ");
				lore.add(ChatColor.translateAlternateColorCodes('&', "&fClick here to change mode to"));
				lore.add(ChatColor.translateAlternateColorCodes('&', "&cDelete Message &ffor deleting message"));
				lore.add(ChatColor.translateAlternateColorCodes('&', "&fby click the message icon"));
				meta.setLore(lore);
				item.setItemMeta(meta);
				menu.setItem(40, item);
			}else if (mode.get(p).equalsIgnoreCase("DELETE")) {
				ItemStack item = new ItemStack(Material.WOOL, 1, (short) 14);
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&e&lMode&f&l: &cDelete Message"));
				lore.add(" ");
				lore.add(ChatColor.translateAlternateColorCodes('&', "&fClick here to change mode to"));
				lore.add(ChatColor.translateAlternateColorCodes('&', "&aRead Message &fto read message"));
				lore.add(ChatColor.translateAlternateColorCodes('&', "&fby click the message icon"));
				meta.setLore(lore);
				item.setItemMeta(meta);
				menu.setItem(40, item);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void openInv(Player p) {
		p.openInventory(inv.get(p));
	}
	
	public static void menuAction(Player p, OfflinePlayer target, int slot) {
		try {
			if (slot == 17 || slot == 26 || slot == 35) {
				openMailbox(p, target, pages.get(p) + 1);
				openInv(p);
			}
			if (slot == 40) {
				if (mode.get(p).equals("READ")) {
					mode.remove(p);
					mode.put(p, "DELETE");
				}else if (mode.get(p).equalsIgnoreCase("DELETE")) {
					mode.remove(p);
					mode.put(p, "READ");
				}
				openMailbox(p, target, pages.get(p));
				openInv(p);
			}
			if ((slot > 9 && slot < 17) || (slot > 18 && slot < 26) || (slot > 27 && slot < 35)) {
				List<String> data = new ArrayList<String>(DataManager.mailboxData.get(target));
				int num = ((3*7)*pages.get(p)) - 3*7;
				int count = 0;
				for (int i = 0; i < 5; i++) {
					for (int a = 0; a < 9; a++) {
						if (i + 1 != 1 && i + 1 != 5) {
							if (a != 0 || a != 8) {
								if (count == slot) {
									break;
								}
								num++;
							}
						}
						count++;
					}
				}
				num = num - 1;
				if (data.get(num).length() > 0 || !(data.get(num).equals(null))) {
					if (mode.get(p).equals("DELETE")) {
						DataManager.deleteMail(p, targets.get(p), data.get(num));
						openMailbox(p, target, pages.get(p));
						openInv(p);
					}
					if (mode.get(p).equals("READ")) {
						p.closeInventory();
						DataManager.readMail(p, target, data.get(num));
					}
				}
			}
			if (pages.get(p) > 1) {
				if (slot == 9 || slot == 18 || slot == 27) {
					openMailbox(p, target, pages.get(p) - 1);
					openInv(p);
				}
			}
		}catch(Exception e) {}
	}

}