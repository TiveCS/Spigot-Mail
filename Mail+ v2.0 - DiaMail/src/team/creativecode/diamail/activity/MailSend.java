package team.creativecode.diamail.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import team.creativecode.diamail.Main;
import team.creativecode.diamail.util.ItemBuilder;

public class MailSend {
	
	public static HashMap<Player, MailSend> reg = new HashMap<Player, MailSend>();
	Main plugin = Main.getPlugin(Main.class);
	
	Inventory inv;
	Player user;
	OfflinePlayer target;
	List<String> msg = new ArrayList<String>();
	ItemStack item;
	
	public MailSend(Player user, OfflinePlayer target, boolean usegui) {
		this.user = user;
		this.target = target;
		if (usegui == false) {
			user.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("prefix") + " Preparing to send mail..."));
			user.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("prefix") + " Type &cExit &fto cancel send mail"));
			user.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("prefix") + " Type &eSet-Item &fto attach item using item on hand"));
			user.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("prefix") + " Type &aDone &fto finish the mail"));
			user.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("prefix") + " "));
			try {
				if (!this.target.equals(null)) {
					user.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("prefix") + " Please type the message on chat!"));
				}
			}catch(Exception e) {
				user.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("prefix") + " Please type the &etarget player's name&f on chat!"));
				user.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("prefix") + " Be careful! player's name is &6case sensitive!"));
			}
		}else if (usegui) {
			newInv();
			initMenu();
		}
	}
	
	public void addMessage(String s) {
		this.msg.add(ChatColor.translateAlternateColorCodes('&', s));
		this.user.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("prefix") + " Message added on line &a" + this.msg.size()));
	}
	
	public void sendMail() {
		MailManager.sendMail(this.user, this.target, this.msg, this.item);
	}
	
	public void setItem(ItemStack item) {
		if (item.getType().toString().equalsIgnoreCase("AIR")) {
			return;
		}
		this.item = item;
	}
	
	public void setItem(Player p) {
		ItemStack item = p.getInventory().getItemInMainHand(), cl = item.clone();
		if (hasItem()) {
			p.getInventory().addItem(this.getItem().clone());
			this.item = null;
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("prefix") + " Returning previous items.."));
		}
		
		if (!item.getType().toString().equalsIgnoreCase("AIR")) {
			setItem(cl);
			p.getInventory().remove(item);
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("prefix") + " Item has been attached!"));
		}else {
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("prefix") + " You cannot send &cAIR &fin your mail!"));
		}
	}
	
	public void setTarget(OfflinePlayer op) {
		this.target = op;
	}
	
	public void action(Player clicker, int slot) {
		if (slot == 3*9 - 5) {
			clicker.openInventory(Mailbox.mailbox.get(clicker).getInventory());
		}
		else if (slot == 2*9 - 2) {
			this.sendMail();
		}
		else if (slot == 2*9 - 4) {
			
		}
		else if (slot == 2*9 - 6) {
			
		}
		else if (slot == 2*9 - 8) {
			this.setItem(clicker);
			initMenu();
		}
	}
	
	public void newInv() {
		this.inv = Bukkit.createInventory(null, 3*9, "Mail Send Menu");
	}
	
	public void initMenu() {
		try {
			target = this.getTarget();
		}catch(Exception e) {}
		List<String> lore = new ArrayList<String>();
		for (int i = 0; i < 3*9; i++) {
			this.inv.setItem(i, ItemBuilder.createItem(Material.BLACK_STAINED_GLASS_PANE, " ", null, false));
		}
		lore.add("&7▶ Click here");
		this.inv.setItem(2*9 - 2, ItemBuilder.createItem(Material.GREEN_TERRACOTTA, "&2Send mail to player", lore, false));
		lore.clear();
		
		ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta meta = (SkullMeta) skull.getItemMeta();
		meta.setDisplayName("&dTarget Player");
		if (this.hasTarget()) {
			meta.setOwningPlayer(this.getTarget());
			lore.add("&7▶ &f" + this.getTarget().getName());
		}else {
			lore.add("&7▶ No target selected");
		}
		lore.add(" ");
		lore.add("&7▶ Click to select target player");
		meta.setLore(lore);
		skull.setItemMeta(meta);
		this.inv.setItem(2*9 - 4, ItemBuilder.rebuildItem(skull));
		lore.clear();
		
		if (this.hasMessage()) {
			for (String msg : this.getMessage()) {
				lore.add(msg);
			}
		}
		lore.add(" ");
		lore.add("&b▶ Add new line message");
		this.inv.setItem(2*9 - 6, ItemBuilder.createItem(Material.KNOWLEDGE_BOOK, "&3&lMessage&f&l:", lore, false));
		lore.clear();
		
		if (!this.hasItem()) {
			lore.add("&7▶ Click to set item");
			this.inv.setItem(2*9 - 8, ItemBuilder.createItem(Material.CHEST, "&6Attached Item", lore, false));
			lore.clear();
		}else {
			this.inv.setItem(2*9 - 8, this.getItem());
		}
		
		this.inv.setItem(3*9 - 5, ItemBuilder.createItem(Material.TNT, "&cMailbox", null, false));
	}
	
	public boolean hasItem() {
		try {
			if (this.item.equals(null)) {
				return false;
			}else {
				return true;
			}
		}catch(Exception e) {
			return false;
		}
	}
	
	public boolean hasTarget() {
		try {
			if (this.target.equals(null)) {
				return false;
			}else {
				return true;
			}
		}catch(Exception e) {
			return false;
		}
	}
	
	public boolean hasMessage() {
		return !this.msg.isEmpty();
	}
	
	public Inventory getInventory() {
		return this.inv;
	}
	
	public ItemStack getItem() {
		return this.item;
	}
	
	public Player getPlayer() {
		return this.user;
	}
	
	public OfflinePlayer getTarget() {
		return this.target;
	}
	
	public List<String> getMessage(){
		return this.msg;
	}

}
