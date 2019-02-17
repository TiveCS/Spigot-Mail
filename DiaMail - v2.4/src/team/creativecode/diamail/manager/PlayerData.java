package team.creativecode.diamail.manager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import team.creativecode.diamail.Main;
import team.creativecode.diamail.manager.mail.Mail;
import team.creativecode.diamail.manager.mail.Mail.MailType;
import team.creativecode.diamail.manager.menu.Mailbox;
import team.creativecode.diamail.utils.ConfigManager;

public class PlayerData {
	
	Main plugin = Main.getPlugin(Main.class);
	
	private Mailbox mailbox;
	
	//Identity
	private OfflinePlayer player;
	private File file;
	private FileConfiguration config;
	
	//Data
	private List<Mail> inbox = new ArrayList<Mail>(); 
	private List<Mail> outbox = new ArrayList<Mail>();
	
	public PlayerData(OfflinePlayer op) {
		
		this.player = op;
		this.file = new File(plugin.getDataFolder() + "/PlayerData", op.getUniqueId().toString() + ".yml");
		this.config = YamlConfiguration.loadConfiguration(this.file);
		
		ConfigManager.createFile(this.file);
		this.mailbox = new Mailbox();
		if (this.file.exists()) {
			loadBasicDataFile();
			loadMails();
		}
	}
	
	public void loadBasicDataFile() {
		ConfigManager.input(getFile(), "player-name", getPlayer().getName());
	}
	
	public void loadMails() {
		this.inbox.clear();
		this.outbox.clear();
		if (ConfigManager.contains(getFile(), "mail.inbox")) {
			for (String uuid : getConfig().getConfigurationSection("mail.inbox").getKeys(false)) {
				this.inbox.add(new Mail(this, uuid, MailType.INBOX));
			}
		}
		
		if (ConfigManager.contains(getFile(), "mail.outbox")) {
			for (String uuid : getConfig().getConfigurationSection("mail.outbox").getKeys(false)) {
				this.outbox.add(new Mail(this, uuid, MailType.OUTBOX));
			}
		}
	}

	public List<Mail> getOutbox(){
		return this.outbox;
	}
	
	public List<Mail> getInbox(){
		return this.inbox;
	}
	
	public FileConfiguration getConfig() {
		return this.config;
	}
	
	public File getFile() {
		return this.file;
	}
	
	public Mailbox getMailbox() {
		return this.mailbox;
	}
	
	public OfflinePlayer getPlayer() {
		return this.player;
	}
	
	public List<Mail> getMails(MailType mt){
		List<Mail> m = new ArrayList<Mail>();
		return m;
	}

}
