package team.creativecode.diamail;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import team.creativecode.diamail.cmds.DiamailAdminCmd;
import team.creativecode.diamail.cmds.DiamailCmd;
import team.creativecode.diamail.events.BasicEvent;
import team.creativecode.diamail.manager.PlayerData;
import team.creativecode.diamail.manager.menu.MailShow;
import team.creativecode.diamail.manager.menu.Mailbox;
import team.creativecode.diamail.manager.menu.Setting;
import team.creativecode.diamail.utils.ConfigManager;
import team.creativecode.diamail.utils.Language;
import team.creativecode.diamail.utils.Placeholder;

public class Main extends JavaPlugin {
	
	public static Language lang;
	public static Placeholder placeholder;
	public static String version = "";
	public static int rsid = 58869;
	
	public static List<String> dependencies = new ArrayList<>();
	
	public void onEnable() {
		version = this.getDescription().getVersion();
		
		loadDependencies();
		loadFile();
		loadCmds();
		loadEvents();
		loadMenus();
	}

	private void loadMenus() {
		new Mailbox().createFile();
		new MailShow().createFile();
		new Setting().createFile();
	}
	
	private void loadDependencies() {
		hookInfo("HolographicDisplays");
	}
	
	private void hookInfo(String plugin) {
		if (getServer().getPluginManager().isPluginEnabled(plugin)) {
			getLogger().info("Hooked with " + plugin);
			dependencies.add(plugin);
		}else {
			getLogger().info( plugin + " is not enabled, failed to hook...");
		}
	}

	private void loadEvents() {
		this.getServer().getPluginManager().registerEvents(new BasicEvent(), this);
	}

	private void loadCmds() {
		this.getCommand("diamail").setExecutor(new DiamailCmd());
		this.getCommand("diamail").setTabCompleter(new DiamailCmd());
		
		this.getCommand("diamailadmin").setExecutor(new DiamailAdminCmd());
		this.getCommand("diamailadmin").setTabCompleter(new DiamailAdminCmd());
	}

	public void loadFile() {
		this.getConfig().options().copyDefaults(true);
		this.getConfig().options().copyHeader(true);
		saveConfig();
		
		ConfigManager.createFolder(this.getDataFolder() + "/PlayerData");
		
		Language.loadLanguages();
		lang = new Language(this.getConfig().getString("global-language"));
		placeholder = new Placeholder();
		
		if (this.getConfig().getBoolean("global-settings.enable")) {
			Set<String> set = this.getConfig().getConfigurationSection("global-settings").getKeys(false);
			File[] files = PlayerData.folder.listFiles();
			for (File f : files) {
				FileConfiguration config = YamlConfiguration.loadConfiguration(f);
				for (String s : set) {
					config.set("player-setting." + s, getConfig().get("global-settings." + s));
				}
				try {
					config.save(f);
				} catch (IOException e) {
					this.getLogger().warning("Failed to save global setting for file " + f.getName());
				}
			}
		}
		
	}

}
