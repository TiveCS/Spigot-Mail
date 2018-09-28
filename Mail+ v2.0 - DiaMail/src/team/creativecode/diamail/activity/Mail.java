package team.creativecode.diamail.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

import team.creativecode.diamail.activity.MailManager.MailType;
import team.creativecode.diamail.manager.PlayerMail;

public class Mail {

	UUID uuid;
	String path;
	List<String> message = new ArrayList<String>();
	PlayerMail pm;
	ItemStack item;
	OfflinePlayer sender, target;
	MailType mt;
	
	
	public Mail(OfflinePlayer p, MailType mt, UUID uuid) {
		PlayerMail pmail = new PlayerMail(p);
		this.mt = mt;
		this.uuid = uuid;
		this.pm = pmail;
		this.message = this.getPlayerMail().getMessage(this.getMailType(), this.getUniqueId().toString());
		this.sender = null;
		this.target = null;
		this.path = "mailbox." + this.mt.toString().toLowerCase() + "." + uuid.toString();
		this.item = this.pm.getConfig().getItemStack(path + ".item");
		try {
			if (this.mt.equals(MailType.INBOX)) {
				this.sender = Bukkit.getPlayer(UUID.fromString(this.pm.getConfig().getString("mailbox." + this.mt.toString().toLowerCase() + "." + uuid.toString() + ".sender")));
			}if (this.mt.equals(MailType.OUTBOX)) {
				this.target = Bukkit.getPlayer(this.pm.getConfig().getString("mailbox." + this.mt.toString().toLowerCase() + "." + uuid.toString() + ".target"));
			}
		}catch(Exception e) {}
	}
	
	public void setMessage(List<String> list) {
		this.message = list;
	}
	
	public void setItem(ItemStack item) {
		this.item = item;
	}
	
	public MailType getMailType() {
		return this.mt;
	}
	
	public UUID getUniqueId() {
		return this.uuid;
	}
	
	public String getPath() {
		return this.path;
	}
	
	public OfflinePlayer getSender() {
		return this.sender;
	}
	
	public OfflinePlayer getTarget() {
		return this.target;
	}
	
	public ItemStack getItem() {
		return this.item;
	}
	
	public PlayerMail getPlayerMail() {
		return this.pm;
	}
	
	public List<String> getMessage(){
		return this.message;
	}
	
}
