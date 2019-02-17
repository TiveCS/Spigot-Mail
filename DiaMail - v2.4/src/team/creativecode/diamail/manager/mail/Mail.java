package team.creativecode.diamail.manager.mail;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import team.creativecode.diamail.Main;
import team.creativecode.diamail.manager.PlayerData;

public class Mail {
	
	public enum MailType{
		INBOX, OUTBOX;
	}
	
	public enum MailMode{
		SENDALL, RECEIVER, ITEM, MESSAGE;
	}
	
	public static HashMap<Player, Mail> sending = new HashMap<Player, Mail>();
	
	Main plugin = Main.getPlugin(Main.class);
	
	private MailMode mailmode = MailMode.SENDALL;
	
	private String uuid;
	private OfflinePlayer sender, receiver;
	private boolean isSendAll = false;
	private List<String> msg = new ArrayList<String>();
	private ItemStack item;
	
	// For creating mail
	public Mail(PlayerData pd, Player sender, boolean isSendAll) {
		this.uuid = UUID.randomUUID().toString();
		this.sender = sender;
		this.isSendAll = isSendAll;
	 	
		sending.put(sender, this);
	}
	
	// For getting mail data
	public Mail(PlayerData pd, String uuid, MailType mt) {
		this.uuid = uuid;
		String path = "mail." + mt.toString().toLowerCase() + "." + uuid;
		File file = pd.getFile();
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		
		this.sender = Bukkit.getOfflinePlayer(UUID.fromString(config.getString(path + ".sender")));
		this.receiver= Bukkit.getOfflinePlayer(UUID.fromString(config.getString(path + ".target")));
	}
	
	public void setMode(MailMode mm) {
		this.mailmode = mm;
	}
	
	public void addMessage(String text) {
		this.msg.add(text);
	}
	
	public void setSendAll(boolean sendall) {
		this.isSendAll = sendall;
	}
	
	public void setTarget(OfflinePlayer player) {
		this.receiver = player;
	}
	
	public void setItem(ItemStack item) {
		this.item = item;
	}
	
	public String getMailUUID() {
		return this.uuid;
	}
	
	public OfflinePlayer getSender() {
		return this.sender;
	}
	
	public OfflinePlayer getReceiver() {
		return this.receiver;
	}
	
	public boolean isSendAll() {
		return this.isSendAll;
	}
	
	public ItemStack getItem() {
		return this.item;
	}
	
	public Mail getMail() {
		return this;
	}
	
	public MailMode getMailMode() {
		return this.mailmode;
	}

}
