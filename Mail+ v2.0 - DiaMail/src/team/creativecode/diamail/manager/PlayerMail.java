package team.creativecode.diamail.manager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import team.creativecode.diamail.ConfigManager;
import team.creativecode.diamail.Main;
import team.creativecode.diamail.activity.MailManager.MailType;
import team.creativecode.diamail.activity.Mailbox;

public class PlayerMail {

	Main plugin = Main.getPlugin(Main.class);
	Mailbox mb;
	Player p;
	File file;
	FileConfiguration config;
	List<String> inbox, outbox, mails;
	HashMap<String, Object> settings;

	public PlayerMail(OfflinePlayer p) {
		try {
			this.p = p.getPlayer();
			this.file = new File(plugin.getDataFolder() + "/PlayerData", p.getUniqueId().toString() + ".yml");
			this.config = YamlConfiguration.loadConfiguration(file);
			
			ConfigurationSection ci = config.getConfigurationSection("mailbox.inbox"),
					co = config.getConfigurationSection("mailbox.outbox");
			Set<String> sk = config.getConfigurationSection("settings").getKeys(false);
			
			this.inbox = new ArrayList<String>(ci.getKeys(false));
			this.outbox = new ArrayList<String>(co.getKeys(false));
			this.settings = new HashMap<String, Object>();
			for (String k : sk) {
				settings.put(k, config.get("settings." + k));
			}
			try {
				this.mb = Mailbox.mailbox.get(p);
			}catch(Exception e){}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public Player getPlayer() {
		return this.p;
	}
	
	public void setSetting(String path, Object value) {
		ConfigManager.inputData(this.file, "settings." + path, value);
		Set<String> sk = config.getConfigurationSection("settings").getKeys(false);
		for (String k : sk) {
			this.settings.put(k, config.get("settings." + k));
		}
	}
	public void setSetting(HashMap<String, Object> map) {
		this.settings = map;
	}
	
	public HashMap<String, Object> getSettings(){
		return this.settings;
	}
	
	public List<String> getMessage(MailType mt, String uuid){
		List<String> list = new ArrayList<String>();
		try {
			if (mt.equals(MailType.ALL)) {
				list = this.getConfig().getStringList("mailbox." + uuid + ".message");
			}else{
				list = this.getConfig().getStringList("mailbox." + mt.toString().toLowerCase() + "." + uuid + ".message");
			}
		}catch(Exception e) {e.printStackTrace();}
		return list;
	}
	
	public Mailbox getMailboxData() {
		return this.mb;
	}
	
	public List<String> getMailbox(MailType mt){
		if (mt.equals(MailType.INBOX)) {
			return this.inbox;
		}else if (mt.equals(MailType.OUTBOX)) {
			return this.outbox;
		}
		else if (mt.equals(MailType.ALL)) {
			List<String> l = new ArrayList<String>();
			for (String i : this.inbox) {
				l.add("inbox." + i);
			}
			for (String o : this.outbox) {
				l.add("outbox." + o);
			}
			return l;
		}
		else {
			return null;
		}
	}
	
	public File getFile() {
		return this.file;
	}
	
	public FileConfiguration getConfig() {
		return this.config;
	}
	
	public List<String> getInbox(){
		return this.inbox;
	}
	
	public List<String> getOutbox(){
		return this.outbox;
	}
	
}
