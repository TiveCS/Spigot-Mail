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
import team.creativecode.diamail.manager.MessageManager;
import team.creativecode.diamail.manager.MessageManager.MessageType;
import team.creativecode.diamail.manager.PlayerMail;
import team.creativecode.diamail.util.ItemBuilder;
import team.creativecode.diamail.util.StringEditor;

public class MailSend {
	
	public static HashMap<Player, MailSend> reg = new HashMap<Player, MailSend>();
	Main plugin = Main.getPlugin(Main.class);
	
	PlayerMail pm;
	Inventory inv;
	Player user;
	OfflinePlayer target;
	List<String> msg = new ArrayList<String>();
	ItemStack item;
	
	public MailSend(Player user, OfflinePlayer target, boolean usegui) {
		this.user = user;
		this.target = target;
		this.pm = new PlayerMail(user);
		if (usegui == false) {
			if (Boolean.parseBoolean(this.pm.getSettings().get("show-tips").toString())) {
				MessageManager.send(user, MessageType.PRE_SEND_MAIL);
			}
		}else if (usegui) {
			newInv();
			initMenu();
		}
	}
	
	public String placeholder(String text) {
		text = text.replace("%sender%", this.user.getName());
		try {
			text = text.replace("%target%", this.target.getName());
		}catch(Exception e) {
			text = text.replace("%target%", "[]");
		}
		try {
			text = text.replace("%item%", ChatColor.translateAlternateColorCodes('&', (this.item.getItemMeta().hasDisplayName() ? this.item.getItemMeta().getDisplayName() : this.item.getType().toString()) + " (" + this.item.getAmount() + "x)"));
		}catch(Exception e) {
			text = text.replace("%item%", "[]");
		}
		text = text.replace("%line%", this.msg.size() + "");
		return ChatColor.translateAlternateColorCodes('&', text);
	}
	
	public List<String> placeholder(List<String> list){
		for (int i = 0; i < list.size(); i++) {
			list.set(i, placeholder(list.get(i)));
		}
		return list;
	}
	
	public void addMessage(String s) {
		this.msg.add(ChatColor.translateAlternateColorCodes('&', s));
		
		List<String> list = new ArrayList<String>();
		MessageType[] t = {MessageType.ADD_MESSAGE};
		for (MessageType type : t) {
			if (MessageManager.isList(MessageManager.get(type))) {
				if (list.size() <= 0) {
					for (String m : placeholder(MessageManager.getList(type))) {
						list.add(m);
					}
				}else {
					list = placeholder(MessageManager.getList(type));
				}
			}else {
				list.add(placeholder(MessageManager.getMsg(type)));
			}
		}
		for (String m : list ) {
			this.user.sendMessage(m);
		}
		
	}
	
	public void info() {
		List<String> list = new ArrayList<String>();
		MessageType[] t = {MessageType.PRE_SEND_MAIL_INFO};
		for (MessageType type : t) {
			if (MessageManager.useList(MessageManager.getPath(type))) {
				if (list.size() <= 0) {
					for (String m : placeholder(MessageManager.getList(type))) {
						list.add(m);
					}
				}else {
					list = placeholder(MessageManager.getList(type));
				}
			}else {
				list.add(placeholder(MessageManager.getMsg(type)));
			}
		}
		for (String m : list ) {
			this.user.sendMessage(m);
		}
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
	
	public void returnItem() {
		
		if (hasItem()) {
			this.user.getInventory().addItem(this.getItem().clone());
			this.item = null;
		}
		
		List<String> list = new ArrayList<String>();
		MessageType[] t = {MessageType.RETURN_ITEM};
		for (MessageType type : t) {
			if (MessageManager.isList(MessageManager.get(type))) {
				if (list.size() <= 0) {
					for (String m : placeholder(MessageManager.getList(type))) {
						list.add(m);
					}
				}else {
					list = placeholder(MessageManager.getList(type));
				}
			}else {
				list.add(placeholder(MessageManager.getMsg(type)));
			}
		}
		for (String m : list ) {
			this.user.sendMessage(m);
		}
	}
	
	public void setItem(Player p) {
		ItemStack item = p.getInventory().getItemInMainHand(), cl = item.clone();
		if (hasItem()) {
			p.getInventory().addItem(this.getItem().clone());
			this.item = null;
		}
		
		if (!item.getType().toString().equalsIgnoreCase("AIR")) {
			setItem(cl);
			p.getInventory().remove(item);
		}
		
		List<String> list = new ArrayList<String>();
		MessageType[] t = {MessageType.RETURN_ITEM, MessageType.SET_ITEM};
		for (MessageType type : t) {
			if ((hasItem() == false || !this.item.getType().toString().equals("AIR")) && type.equals(MessageType.RETURN_ITEM)) {
				continue;
			}
			if (MessageManager.isList(MessageManager.get(type))) {
				if (list.size() <= 0) {
					for (String m : placeholder(MessageManager.getList(type))) {
						list.add(m);
					}
				}else {
					list = placeholder(MessageManager.getList(type));
				}
			}else {
				list.add(placeholder(MessageManager.getMsg(type)));
			}
		}
		for (String m : list ) {
			this.user.sendMessage(m);
		}
		
	}
	
	public void setTarget(OfflinePlayer op) {
		this.target = op;
		
		List<String> list = new ArrayList<String>();
		MessageType[] t = {MessageType.SET_TARGET};
		for (MessageType type : t) {
			if (MessageManager.isList(MessageManager.get(type))) {
				if (list.size() <= 0) {
					for (String m : placeholder(MessageManager.getList(type))) {
						list.add(m);
					}
				}else {
					list = placeholder(MessageManager.getList(type));
				}
			}else {
				list.add(placeholder(MessageManager.getMsg(type)));
			}
		}
		for (String m : list ) {
			this.user.sendMessage(m);
		}
	}
	
	public void action(Player clicker, int slot) {
		reg.remove(clicker);
		if (slot == 3*9 - 5) {
			clicker.openInventory(Mailbox.mailbox.get(clicker).getInventory());
		}
		else if (slot == 2*9 - 2) {
			this.sendMail();
			clicker.closeInventory();
		}
		else if (slot == 2*9 - 4) {
			reg.put(clicker, Mailbox.mailsend.get(clicker));
			if (Boolean.parseBoolean(this.pm.getSettings().get("show-tips").toString())) {
				MessageManager.send(clicker, MessageType.PRE_SEND_MAIL_GUI);
			}
			clicker.closeInventory();
		}
		else if (slot == 2*9 - 6) {
			if (hasTarget()) {
				reg.put(clicker, Mailbox.mailsend.get(clicker));
				if (Boolean.parseBoolean(this.pm.getSettings().get("show-tips").toString())) {
					MessageManager.send(clicker, MessageType.PRE_SEND_MAIL_GUI);
				}
				clicker.closeInventory();
			}
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
		lore.add("&7- Click here");
		this.inv.setItem(2*9 - 2, ItemBuilder.createItem(Material.GREEN_TERRACOTTA, "&2Send mail to player", lore, false));
		lore.clear();
		
		ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta meta = (SkullMeta) skull.getItemMeta();
		meta.setDisplayName("&dTarget Player");
		if (this.hasTarget()) {
			meta.setOwningPlayer(this.getTarget());
			lore.add("&7- &f" + this.getTarget().getName());
		}else {
			lore.add("&7- No target selected");
		}
		lore.add(" ");
		lore.add("&7- Click to select target player");
		meta.setLore(lore);
		skull.setItemMeta(meta);
		this.inv.setItem(2*9 - 4, ItemBuilder.rebuildItem(skull));
		lore.clear();
		
		if (this.hasMessage()) {
			for (String msg : this.getMessage()) {
				lore.add(msg);
			}
			lore = StringEditor.normalizeColor(lore);
		}
		lore.add(" ");
		lore.add("&b- Add new line message");
		if (hasTarget()) {
			this.inv.setItem(2*9 - 6, ItemBuilder.createItem(Material.KNOWLEDGE_BOOK, "&3&lMessage&f&l:", lore, false));
		}else {
			this.inv.setItem(2*9 - 6, ItemBuilder.createItem(Material.KNOWLEDGE_BOOK, "&cNeed player target first", null, false));
		}
		lore.clear();
		
		if (!this.hasItem()) {
			lore.add("&7- Click to set item");
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
