package team.creativecode.diamail.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import team.creativecode.diamail.activity.MailManager.MailType;
import team.creativecode.diamail.manager.PlayerMail;
import team.creativecode.diamail.manager.PlayerSetting;
import team.creativecode.diamail.util.ItemBuilder;
import team.creativecode.diamail.util.StringEditor;

public class Mailbox {
	
	public static HashMap<Player, Mailbox> mailbox = new HashMap<Player, Mailbox>();
	public static HashMap<Player, MailSend> mailsend = new HashMap<Player, MailSend>();
	
	Inventory inv;
	OfflinePlayer op;
	PlayerMail pm;
	MailSend ms;
	PlayerSetting ps;
	MailType mt;
	List<String> currentMailbox;
	int mailboxSize;
	int page;
	
	public Mailbox(OfflinePlayer p, int pg, MailType mt) {
		this.ms = new MailSend((Player) p, null, true);
		this.ps = new PlayerSetting(p);
		this.pm = new PlayerMail(p);
		this.mt = mt;
		this.op = p;
		this.page = pg;
		this.mailboxSize = this.pm.getMailbox(mt).size();
		this.inv = Bukkit.createInventory(null, 6*9, ChatColor.translateAlternateColorCodes('&', this.mailboxSize + " Mails | Page " + this.page + " (" + mt.toString() + ")"));
		initMailbox();
	}
	
	public Mailbox(OfflinePlayer p) {
		this.ps = new PlayerSetting(p);
		this.pm = new PlayerMail(p);
		this.op = p;
		this.mailboxSize = this.pm.getMailbox(mt).size();
		this.inv = Bukkit.createInventory(null, 6*9, ChatColor.translateAlternateColorCodes('&', this.mailboxSize + " Mails | Page " + this.page + " (" + mt.toString() + ")"));
		initMailbox();
	}
	
	public MailSend getMailSend() {
		return this.ms;
	}

	public List<String> getMailbox(){
		return this.currentMailbox;
	}
	
	public int getMailboxSize() {
		return this.mailboxSize;
	}
	
	public PlayerMail getPlayerMail() {
		return this.pm;
	}
	
	public OfflinePlayer getPlayer() {
		return this.op;
	}
	
	public Inventory getInventory() {
		return this.inv;
	}
	
	public int getPage() {
		return this.page;
	}
	
	public MailType getMailboxType() {
		return this.mt;
	}
	
	public PlayerSetting getPlayerSettings() {
		return this.ps;
	}
	
	public void update() {
		this.ps = new PlayerSetting(this.getPlayer());
		this.pm = new PlayerMail(this.getPlayer());
		this.mailboxSize = this.pm.getMailbox(mt).size();
		initMailbox();
	}
	
	public void action(Player clicker, int slot) {
		try {
			if (slot == (2*9 - 1) || slot == (3*9 - 1) || slot == (4*9 - 1) || slot == (5*9 - 1)) {
				++this.page;
				this.inv = Bukkit.createInventory(null, 6*9, ChatColor.translateAlternateColorCodes('&', mailboxSize + " Mails | Page " + this.page + " (" + mt.toString() + ")"));
				initMailbox();
				clicker.openInventory(this.inv);
			}else if (slot == (2*9 - 9) || slot == (3*9 - 9) || slot == (4*9 - 9) || slot == (5*9 - 9)) {
				--this.page;
				this.inv = Bukkit.createInventory(null, 6*9, ChatColor.translateAlternateColorCodes('&', mailboxSize + " Mails | Page " + this.page + " (" + mt.toString() + ")"));
				initMailbox();
				clicker.openInventory(this.inv);
			}else if (slot == 6*9 - 8) {
				this.mt = MailType.INBOX;
				this.pm.setSetting("show-mailbox", "Inbox");
				this.inv = Bukkit.createInventory(null, 6*9, ChatColor.translateAlternateColorCodes('&', mailboxSize + " Mails | Page " + this.page + " (" + mt.toString() + ")"));
				initMailbox();
				clicker.openInventory(this.inv);
			}else if (slot == 6*9 - 7) {
				this.mt = MailType.OUTBOX;
				this.pm.setSetting("show-mailbox", "Outbox");
				this.inv = Bukkit.createInventory(null, 6*9, ChatColor.translateAlternateColorCodes('&', mailboxSize + " Mails | Page " + this.page + " (" + mt.toString() + ")"));
				initMailbox();
				clicker.openInventory(this.inv);
			}else if (slot == 6*9 - 6) {
				this.mt = MailType.ALL;
				this.pm.setSetting("show-mailbox", "All");
				this.inv = Bukkit.createInventory(null, 6*9, ChatColor.translateAlternateColorCodes('&', mailboxSize + " Mails | Page " + this.page + " (" + mt.toString() + ")"));
				initMailbox();
				clicker.openInventory(this.inv);
			}else if ((slot > 2*9 - 9 && slot < 2*9 - 1 ) || (slot > 3*9 - 9 && slot < 3*9 - 1 ) || (slot > 4*9 - 9 && slot < 4*9 - 1 ) || (slot > 5*9 - 9 && slot < 5*9 - 1 )) {
				int angka = 0;
				if (slot > 2*9) {
					++angka;
				}
				if (slot > 3*9) {
					++angka;
				}
				if (slot > 4*9) {
					++angka;
				}
				int num = ((4*7*page) - 4*7) + (slot - (10+(2*angka)));
				MailManager.openMailInfoMenu(clicker, this.op, this.mt, this.currentMailbox.get(num));
			}else if (slot == 6*9 - 4) {
				clicker.openInventory(this.ps.getInventory());
			}else if (slot == 6*9 - 3) {
				MailSend ms = new MailSend(clicker, null, true);
				this.ms = ms;
				mailsend.remove(clicker);
				mailsend.put(clicker, ms);
				clicker.openInventory(ms.getInventory());
			}
		}catch(Exception e) {}
	}
	
	public void initMailbox() {
		int num = ((4*7)*this.page) - 4*7;
		this.currentMailbox = new ArrayList<String>(this.pm.getMailbox(this.mt));
		List<String> lore = new ArrayList<String>();
		for (int row = 0;row < 6; row++) {
			for (int slot=0; slot < 9; slot++) {
				int s = row*9 + slot;
				if (row == 0 || row == 5) {
					this.inv.setItem(s, ItemBuilder.createItem(Material.BLACK_STAINED_GLASS_PANE, " ", null, false));
				}else {
					if (slot == 0) {
						if (this.page > 1) {
							this.inv.setItem(s, ItemBuilder.createItem(Material.RED_STAINED_GLASS_PANE, "&cPrevious Page", null, false));
						}else {
							this.inv.setItem(s, ItemBuilder.createItem(Material.BLACK_STAINED_GLASS_PANE, " ", null, false));
						}
					}else if (slot == 8) {
						this.inv.setItem(s, ItemBuilder.createItem(Material.GREEN_STAINED_GLASS_PANE, "&aNext Page", null, false));
					}else {
						List<String> pre = new ArrayList<String>();
						try {
							if (this.mt.equals(MailType.ALL)) {
								lore = this.pm.getConfig().getStringList("mailbox." + this.currentMailbox.get(num) + ".message");
							}else if (mt.equals(MailType.INBOX)) {
								lore = this.pm.getConfig().getStringList("mailbox.inbox." + this.currentMailbox.get(num) + ".message");
							}else if (mt.equals(MailType.OUTBOX)) {
								lore = this.pm.getConfig().getStringList("mailbox.outbox." + this.currentMailbox.get(num) + ".message");
							}
							lore = StringEditor.normalizeColor(lore);
						}catch(Exception e) {continue;}
						try {
							ItemStack item = new ItemStack(Material.AIR);
							if (this.mt.equals(MailType.ALL)) {
								item = this.pm.getConfig().getItemStack("mailbox." + this.currentMailbox.get(num) + ".item");
							}else if (mt.equals(MailType.INBOX)){
								item = this.pm.getConfig().getItemStack("mailbox.inbox."+ this.currentMailbox.get(num) + ".item");
							}else if (mt.equals(MailType.OUTBOX)){
								item = this.pm.getConfig().getItemStack("mailbox.outbox."+ this.currentMailbox.get(num) + ".item");
							}
							ItemMeta meta = item.getItemMeta();
							if (meta.hasLore()) {
								lore = meta.getLore();
								lore.add(" ");
								lore.add(ChatColor.translateAlternateColorCodes('&', "&bClick to see more info.."));
							}else {
								lore.add(" ");
								lore.add(ChatColor.translateAlternateColorCodes('&', "&bClick to see more info.."));
							}
							
							if (mt.equals(MailType.ALL)) {
								if (this.currentMailbox.get(num).toLowerCase().contains("inbox")) {
									pre.add(ChatColor.translateAlternateColorCodes('&', "&2&lFrom &f" + Bukkit.getPlayer(UUID.fromString(this.pm.getConfig().getString("mailbox." + this.currentMailbox.get(num) + ".sender"))).getName()));
								}
								if (this.currentMailbox.get(num).toLowerCase().contains("outbox")) {
									pre.add(ChatColor.translateAlternateColorCodes('&', "&2&lFor &f" + Bukkit.getPlayer(UUID.fromString(this.pm.getConfig().getString("mailbox." + this.currentMailbox.get(num) + ".target"))).getName()));
								}
							}
							if (mt.equals(MailType.INBOX)) {
								pre.add(ChatColor.translateAlternateColorCodes('&', "&2&lFrom &f" + Bukkit.getPlayer(UUID.fromString(this.pm.getConfig().getString("mailbox.inbox." + this.currentMailbox.get(num) + ".sender"))).getName()));
							}
							if (mt.equals(MailType.OUTBOX)) {
								pre.add(ChatColor.translateAlternateColorCodes('&', "&2&lFor &f" + Bukkit.getPlayer(UUID.fromString(this.pm.getConfig().getString("mailbox.outbox." + this.currentMailbox.get(num) + ".target"))).getName()));
							}
							pre.add(" ");
							pre.add(ChatColor.translateAlternateColorCodes('&', "&3&lMessage"));
							for (String msg : lore) {
								pre.add(msg);
							}
							meta.setLore(pre);
							item.setItemMeta(meta);
							this.inv.setItem(s, item);
						}catch(Exception e) {
							if (mt.equals(MailType.ALL)) {
								if (this.currentMailbox.get(num).toLowerCase().contains("inbox")) {
									pre.add(ChatColor.translateAlternateColorCodes('&', "&2&lFrom &f" + Bukkit.getPlayer(UUID.fromString(this.pm.getConfig().getString("mailbox." + this.currentMailbox.get(num) + ".sender"))).getName()));
								}
								if (this.currentMailbox.get(num).toLowerCase().contains("outbox")) {
									pre.add(ChatColor.translateAlternateColorCodes('&', "&2&lFor &f" + Bukkit.getPlayer(UUID.fromString(this.pm.getConfig().getString("mailbox." + this.currentMailbox.get(num) + ".target"))).getName()));
								}
							}
							if (mt.equals(MailType.INBOX)) {
								pre.add(ChatColor.translateAlternateColorCodes('&', "&2&lFrom &f" + Bukkit.getPlayer(UUID.fromString(this.pm.getConfig().getString("mailbox.inbox." + this.currentMailbox.get(num) + ".sender"))).getName()));
							}
							if (mt.equals(MailType.OUTBOX)) {
								pre.add(ChatColor.translateAlternateColorCodes('&', "&2&lFrom &f" + Bukkit.getPlayer(UUID.fromString(this.pm.getConfig().getString("mailbox.outbox." + this.currentMailbox.get(num) + ".ty"))).getName()));
							}
							pre.add(" ");
							pre.add(ChatColor.translateAlternateColorCodes('&', "&3&lMessage"));
							lore.add(" ");
							lore.add(ChatColor.translateAlternateColorCodes('&', "&bClick to see more info.."));
							for (String msg : lore) {
								pre.add(msg);
							}
							this.inv.setItem(s, ItemBuilder.createItem(Material.BOOK, " ", pre, false));
						}
						lore.clear();
						num++;
					}
				}
			}
		}
		
		lore.add(ChatColor.translateAlternateColorCodes('&', "&7" + this.pm.getInbox().size() + " Mail(s)"));
			this.inv.setItem(6*9 - 8, ItemBuilder.createItem(Material.GREEN_TERRACOTTA, "&eInbox", lore, false, true));
			this.inv.setItem(6*9 - 8, ItemBuilder.createItem(Material.CHEST, "&eInbox", lore, false));
		lore.clear();
		
		lore.add(ChatColor.translateAlternateColorCodes('&', "&7" + this.pm.getOutbox().size() + " Mail(s)"));
		this.inv.setItem(6*9 - 7, ItemBuilder.createItem(Material.ENDER_CHEST, "&2Outbox", lore, false));
		lore.clear();
		
		lore.add(ChatColor.translateAlternateColorCodes('&', "&7" + (this.pm.getOutbox().size() + this.pm.getInbox().size()) + " Mail(s)"));
		this.inv.setItem(6*9 - 6, ItemBuilder.createItem(Material.CYAN_SHULKER_BOX, "&3Inbox & Outbox", lore, false));
		lore.clear();
		
		lore.add(ChatColor.translateAlternateColorCodes('&', " "));
		this.inv.setItem(6*9 - 4, ItemBuilder.createItem(Material.ANVIL, "&bSettings", null, false));
		lore.clear();
		
		lore.add(ChatColor.translateAlternateColorCodes('&', "&7Create, editing and sending"));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&7mail using chest GUI mode."));
		this.inv.setItem(6*9 - 3, ItemBuilder.createItem(Material.COMPASS, "&aSend Mail", lore, false));
		lore.clear();
	}

}
