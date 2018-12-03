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

import team.creativecode.diamail.Main;
import team.creativecode.diamail.activity.MailManager.MailType;
import team.creativecode.diamail.manager.PlayerMail;
import team.creativecode.diamail.manager.PlayerSetting;
import team.creativecode.diamail.util.ItemBuilder;
import team.creativecode.diamail.util.Placeholder;
import team.creativecode.diamail.util.StringEditor;

public class Mailbox {
	
	public static HashMap<Player, Mailbox> mailbox = new HashMap<Player, Mailbox>();
	public static HashMap<Player, MailSend> mailsend = new HashMap<Player, MailSend>();
	
	private static Main plugin = Main.getPlugin(Main.class);
	public static String folder = plugin.getDataFolder() + "/PlayerData";
	
	Inventory inv;
	OfflinePlayer op;
	PlayerMail pm;
	MailSend ms;
	PlayerSetting ps;
	MailType mt;
	List<String> currentMailbox;
	int mailboxSize;
	int page;
	List<ItemStack> inboxItem;
	List<ItemStack> outboxItem;
	HashMap<String, ItemStack> loadedItem;
	
	public Mailbox(OfflinePlayer p, int pg, MailType mt) {
		this.ms = new MailSend((Player) p, null, true);
		this.ps = new PlayerSetting(p);
		this.pm = this.ps.getPlayerMail();
		this.mt = mt;
		this.op = p;
		this.page = pg;
		this.mailboxSize = this.pm.getMailbox(MailType.INBOX).size() + this.pm.getMailbox(MailType.OUTBOX).size();
		this.currentMailbox = new ArrayList<String>(this.pm.getMailbox(mt));
	}
	
	public Mailbox(OfflinePlayer p) {
		this.ps = new PlayerSetting(p);
		this.pm = new PlayerMail(p);
		this.page = 1;
		this.mt = MailType.valueOf(pm.getSettings().get("show-mailbox").toString().toUpperCase());
		this.op = p;
		this.mailboxSize = this.pm.getMailbox(MailType.INBOX).size() + this.pm.getMailbox(MailType.OUTBOX).size();
		this.currentMailbox = new ArrayList<String>(this.pm.getMailbox(mt));
	}
	
	public void createMenu() {
		try {
			String title = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("menu-manager.mailbox.title"));
			try {
				title = Placeholder.mailboxPlaceholder(this.pm.getMailboxData(), title);
				this.inv = Bukkit.createInventory(null, 6*9, title);
				initMailbox();
			}catch(Exception e) {this.inv.clear();this.inv = null;}
		}catch(Exception e) {}
	}
	
	public void initItems(MailType mailtype) {
		this.inboxItem = new ArrayList<ItemStack>();
		this.outboxItem = new ArrayList<ItemStack>();
		this.loadedItem = new HashMap<String, ItemStack>();
		this.loadedItem.clear();
		this.currentMailbox = new ArrayList<String>(this.pm.getMailbox(mailtype));
		List<String> mails = new ArrayList<String>(this.pm.getMailbox(mailtype));
		for (String uuid : mails) {
			try {
				ItemStack item = new ItemStack(Material.BOOK);
				String path;
				if (mailtype.equals(MailType.ALL)) {
					path = "mailbox." + uuid;
				}else {
					path = "mailbox." + mailtype.toString().toLowerCase() + "." + uuid;
				}
				if (path.contains("inbox.")) {
					item = new ItemStack(Material.BOOK);
				}else if (path.contains("outbox.")) {
					item = new ItemStack(Material.KNOWLEDGE_BOOK);
				}
				ItemMeta meta = item.getItemMeta();
				List<String> lore = new ArrayList<String>();
				OfflinePlayer object = null;
				boolean sendall = false;
				
				UUID player = null;
				try{
					if (path.contains("inbox.")) {
						player = UUID.fromString(this.pm.getConfig().getString(path + ".sender"));
					}else {
						player = UUID.fromString(this.pm.getConfig().getString(path + ".target"));
					}
				}catch(Exception e) {
					sendall = true;
				}
				List<String> msg = new ArrayList<String>(this.pm.getMessage(mailtype, uuid));
				try {
					if (!this.pm.getConfig().getItemStack(path + ".item").equals(null)) {
						item = this.pm.getConfig().getItemStack(path + ".item").clone();
						meta = item.getItemMeta();
						if (meta.hasLore()) {
							lore = meta.getLore();
						}
					}
				}catch(Exception e) {}
				
				String name = null;
				if (sendall == false) {
					try {
						object = Bukkit.getPlayer(player);
						if (object.equals(null)) {
							object = Bukkit.getOfflinePlayer(player);
						}
					}catch(Exception e) {
						object = Bukkit.getOfflinePlayer(player);
					}
					
					name = "" + object.getName();
				}else {
					name = path.contains("inbox.") ? this.pm.getConfig().getString(path + ".sender") : this.pm.getConfig().getString(path + ".target");
				}
				
				try {
					if (path.contains("inbox.")) {
						lore.add(ChatColor.translateAlternateColorCodes('&', "&2&lFrom &f" + name));
					}else {
						lore.add(ChatColor.translateAlternateColorCodes('&', "&2&lTo &f" + name));
					}
				}catch(Exception e) {e.printStackTrace();}
				lore.add(" ");
				
				lore.add(ChatColor.translateAlternateColorCodes('&', "&3&lMessage"));
				for (String m : msg) {
					lore.add(ChatColor.translateAlternateColorCodes('&', m));
				}
				lore.add(" ");
				
				lore.add(ChatColor.translateAlternateColorCodes('&', "&bClick to see more info"));
				meta.setDisplayName(" ");
				
				meta.setLore(StringEditor.normalizeColor(lore));
				item.setItemMeta(meta);
				
				if (mailtype.equals(MailType.INBOX)) {
					this.inboxItem.add(item);
				}else if (mailtype.equals(MailType.OUTBOX)) {
					this.outboxItem.add(item);
				}
				String type = mailtype.equals(MailType.ALL) ? "" : mailtype.toString().toLowerCase() + ".";
				this.loadedItem.put(type + uuid, item);
			}catch(Exception e) {}
		}
		
	}
	
	public void update() {
		this.ps = new PlayerSetting(this.getPlayer());
		this.pm = new PlayerMail(this.getPlayer());
		this.mailboxSize = this.pm.getMailbox(mt).size();
		initMailbox();
	}
	
	public void action(Player clicker, int slot) {
		try {
			String title = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("menu-manager.mailbox.title"));
			if (slot == (2*9 - 1) || slot == (3*9 - 1) || slot == (4*9 - 1) || slot == (5*9 - 1)) {
				this.page++;
				title = Placeholder.mailboxPlaceholder(this.pm.getMailboxData(), title);
				this.inv = Bukkit.createInventory(null, 6*9, ChatColor.translateAlternateColorCodes('&', title));
				initMailbox();
				clicker.openInventory(this.inv);
			}else if ((slot == (2*9 - 9) || slot == (3*9 - 9) || slot == (4*9 - 9) || slot == (5*9 - 9)) && this.page > 1) {
				this.page--;
				title = Placeholder.mailboxPlaceholder(this.pm.getMailboxData(), title);
				this.inv = Bukkit.createInventory(null, 6*9, ChatColor.translateAlternateColorCodes('&', title));
				initMailbox();
				clicker.openInventory(this.inv);
			}else if (slot == 6*9 - 8) {
				this.mt = MailType.INBOX;
				this.pm.setSetting("show-mailbox", "Inbox");
				this.inv.clear();
				initMailbox();
			}else if (slot == 6*9 - 7) {
				this.mt = MailType.OUTBOX;
				this.pm.setSetting("show-mailbox", "Outbox");
				this.inv.clear();
				initMailbox();
			}else if (slot == 6*9 - 6) {
				this.mt = MailType.ALL;
				this.pm.setSetting("show-mailbox", "All");
				this.inv.clear();
				initMailbox();
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
		initItems(this.mt);
		List<String> lore = new ArrayList<String>();
		for (int row = 0; row < 6; row++) {
			for (int slot = 0; slot < 9; slot++) {
				int s = row*9 + slot;
				if (row == 0 || row == 5) {
					this.inv.setItem(s, ItemBuilder.createItem(Material.BLACK_STAINED_GLASS_PANE, " ", null, false));
				}else {
					ItemStack item;
					if (slot == 0) {
						item = this.page > 1 ? ItemBuilder.createItem(Material.RED_STAINED_GLASS_PANE, "&cPrevious Page", null, false) : ItemBuilder.createItem(Material.BLACK_STAINED_GLASS_PANE, " ", null, false);
						this.inv.setItem(s, item);
					}
					else if (slot == 8) {
						this.inv.setItem(s, ItemBuilder.createItem(Material.GREEN_STAINED_GLASS_PANE, "&aNext Page", null, false));
					}else {
						try {
							String type = this.mt.equals(MailType.ALL) ? "" : (this.mt.equals(MailType.INBOX) ? "inbox." : "outbox.");
							String path = type + this.currentMailbox.get(num);
							item = this.loadedItem.get(path);
							this.inv.setItem(s, item);
							num++;
							continue;
						}catch(Exception e) {continue;}
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
	
	public void setInv(Inventory inv) {
		this.inv = inv;
	}
	
	public void setPlayer(OfflinePlayer p) {
		this.op = p;
	}
	
	public void setPlayerMail(PlayerMail pm) {
		this.pm = pm;
	}
	
	public void setPlayerSettings(PlayerSetting ps) {
		this.ps = ps;
	}
	
	public void setMailType(MailType mt) {
		this.mt = mt;
	}
	
	public void initCurrentMailbox() {
		this.currentMailbox = new ArrayList<String>(this.pm.getMailbox(this.mt));
	}
	
	public void setPage(int i) {
		this.page = i;
	}
	
	public void setMailboxSize(int s) {
		this.mailboxSize = s;
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
	
	public HashMap<String, ItemStack> getLoadedItems(){
		return this.loadedItem;
	}
	
	public List<ItemStack> getInboxItem(){
		return this.inboxItem;
	}
	
	public List<ItemStack> getOutboxItem(){
		return this.outboxItem;
	}

}
