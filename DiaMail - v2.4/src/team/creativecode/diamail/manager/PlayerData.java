package team.creativecode.diamail.manager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import team.creativecode.diamail.Main;
import team.creativecode.diamail.events.BasicEvent;
import team.creativecode.diamail.manager.mail.Mail;
import team.creativecode.diamail.manager.mail.Mail.MailType;
import team.creativecode.diamail.manager.menu.Mailbox;
import team.creativecode.diamail.utils.ConfigManager;
import team.creativecode.diamail.utils.DataConverter;
import team.creativecode.diamail.utils.Language;
import team.creativecode.diamail.utils.Language.SendMode;
import team.creativecode.diamail.utils.Placeholder;

public class PlayerData {
	
	public static Main plugin = Main.getPlugin(Main.class);
	public static File folder = new File(plugin.getDataFolder() + "/PlayerData");
	
	private Mailbox mailbox;
	
	//Identity
	private Placeholder plc = new Placeholder();
	private OfflinePlayer player;
	private Language language;
	private List<String> block = new ArrayList<String>();
	private File file;
	private FileConfiguration config;
	private PlayerSetting ps;
	
	//Data
	private List<Mail> inbox = new ArrayList<Mail>(); 
	private List<Mail> outbox = new ArrayList<Mail>();
	
	public PlayerData(OfflinePlayer op) {
		
		this.player = op;
		this.file = new File(plugin.getDataFolder() + "/PlayerData", op.getUniqueId().toString() + ".yml");
		this.config = YamlConfiguration.loadConfiguration(this.file);
		
		ConfigManager.createFile(this.file);
		this.mailbox = new Mailbox();
		this.mailbox.getPlaceholder().inputData("player", op.getName());
		this.mailbox.getPlaceholder().inputData("player_uuid", op.getUniqueId().toString());
		this.mailbox.inputObject("playerdata", this);
		if (this.file.exists()) {
			loadBasicDataFile();
			loadMails();
			this.ps = new PlayerSetting(this);
		}
	}
	
	public void checkMailbox(Player p) {
		SendMode mode = SendMode.valueOf(getPlayerSetting().getSettings().get("notification-display").toString().toUpperCase());
		if (mode.equals(SendMode.NONE)) {
			return;
		}
		DataConverter.playSoundByString(p.getLocation(), plugin.getConfig().getString("settings.notification-check"));
		List<String> msg = new ArrayList<String>(Main.placeholder.useAsList(Main.lang.getMessages().get("command.check")))
				, hmsg = new ArrayList<String>(Main.placeholder.useAsList(Main.lang.getMessages().get("command.check-hovertext")));
		TextComponent main = new TextComponent();
		TextComponent hover = new TextComponent();
		plc.inputData("mailbox", "" + (getInbox().size() + getOutbox().size()));
		plc.inputData("inbox", "" + getInbox().size());
		plc.inputData("outbox", "" + getOutbox().size());
		plc.useAsList(msg);
		plc.useAsList(hmsg);
		for (String m : msg) {
			main.addExtra(new TextComponent(new ComponentBuilder(m).create()));
		}
		
		for (int line = 0; line < hmsg.size(); line++) {
			hover.addExtra(new TextComponent(hmsg.get(line)));
			if (hmsg.size() > (line + 1)) {
				hover.addExtra("\n");
			}
		}
		
		if (plugin.getConfig().getBoolean("json-settings.mail-check")) {
			main.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/diamail"));
		}
		main.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hover).create()));
		
		getLanguage().sendMessage(p, mode, main);
	}
	
	public void checkMailboxScheduled(Player p) {
		long interval = Integer.parseInt(getPlayerSetting().getSettings().get("checking-mailbox-interval").toString());
		
		if (this.getInbox().size() > 0 || this.getOutbox().size() > 0) {
			checkMailbox(p);
		}
		if (interval > 0) {
			Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, new Runnable() {
				@Override
				public void run() {
					if (BasicEvent.players.contains(p)) {
						checkMailboxScheduled(p);
					}
				}
				
			}, interval*20);
		}
	}
	
	public void saveAccess() {
		if (this.player.isOnline()) {
			Player p = this.player.getPlayer();
			List<String> permission = new ArrayList<>();
			permission.add("diamail.access.receive.sendall");
			permission.add("diamail.access.readonly");
			for (String perm : permission) {
				if (!p.hasPermission(perm)) {
					permission.remove(perm);
				}
			}
			ConfigManager.input(getFile(), "permission", permission);
		}
	}
	
	public void loadBasicDataFile() {
		ConfigManager.input(getFile(), "player-name", getPlayer().getName());
		
		Set<String> settings = plugin.getConfig().getBoolean("global-settings.enable") ? plugin.getConfig().getConfigurationSection("global-settings").getKeys(false) : plugin.getConfig().getConfigurationSection("player-setting").getKeys(false);
		if (plugin.getConfig().getBoolean("global-settings.enable")) {
			settings.remove("enable");
			for (String s : settings) {
				ConfigManager.input(getFile(),"player-setting." + s, plugin.getConfig().get("player-setting." + s));
			}
		}else {
			for (String s : settings) {
				ConfigManager.init(getFile(),"player-setting." + s, plugin.getConfig().get("player-setting." + s));
			}
		}
		
		this.language = new Language(this.getConfig().getString("player-setting.language"));
		this.block = ConfigManager.contains(getFile(), "blocked-player") ? this.getConfig().getStringList("blocked-player") : new ArrayList<String>();
	}
	
	public void loadMails() {
		this.inbox.clear();
		this.outbox.clear();
		if (ConfigManager.contains(getFile(), "mailbox.inbox")) {
			for (String uuid : getConfig().getConfigurationSection("mailbox.inbox").getKeys(false)) {
				this.inbox.add(new Mail(this, uuid, MailType.INBOX));
			}
		}
		
		if (ConfigManager.contains(getFile(), "mailbox.outbox")) {
			for (String uuid : getConfig().getConfigurationSection("mailbox.outbox").getKeys(false)) {
				this.outbox.add(new Mail(this, uuid, MailType.OUTBOX));
			}
		}
	}
	
	public void block(OfflinePlayer player) {
		if (player.hasPlayedBefore()) {
			this.block.add(player.getUniqueId().toString());
			this.getConfig().set("blocked-player", this.block);
			if (this.getPlayer().isOnline()) {
				DataConverter.playSoundByString(this.getPlayer().getPlayer().getLocation(), plugin.getConfig().getString("settings.notification-mail-block"));
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
	
	public List<String> getBlockedPlayer(){
		return this.block;
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
	
	public Language getLanguage() {
		return this.language;
	}
	
	public PlayerSetting getPlayerSetting() {
		return this.ps;
	}
	
	public Placeholder getPlaceholder() {
		return this.plc;
	}

}
