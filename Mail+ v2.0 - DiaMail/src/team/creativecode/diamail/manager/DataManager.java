package team.creativecode.diamail.manager;

import java.io.File;
import java.util.Set;

import org.bukkit.OfflinePlayer;

import team.creativecode.diamail.ConfigManager;
import team.creativecode.diamail.Main;

public class DataManager {

	private static Main plugin = Main.getPlugin(Main.class);

	public static void initSetting(OfflinePlayer p) {
		File f = new File(plugin.getDataFolder() + "/PlayerData", p.getUniqueId().toString() + ".yml");
		Set<String> s = plugin.getConfig().getConfigurationSection("player-settings").getKeys(false);
		for (String st : s) {
			try {
				if (ConfigManager.getConfig(f).get("settings." + st).equals(null)) {
					ConfigManager.inputData(f, "settings." + st, plugin.getConfig().get("player-settings." + st));
				}
			}catch(Exception e) {
				ConfigManager.inputData(f, "settings." + st, plugin.getConfig().get("player-settings." + st));
			}
			
		}
		initMailbox(p);
	}
	
	public static void initMailbox(OfflinePlayer p) {
		File f = new File(plugin.getDataFolder() + "/PlayerData", p.getUniqueId().toString() + ".yml");
		try {
			if (ConfigManager.getConfig(f).get("mailbox.inbox.1").equals(null) || ConfigManager.getConfig(f).get("mailbox.outbox.1").equals(null)) {
			}
		}catch(Exception e) {
			ConfigManager.inputData(f, "mailbox.inbox.1", "a");
			ConfigManager.inputData(f, "mailbox.inbox.1", null);
			ConfigManager.inputData(f, "mailbox.outbox.1", "a");
			ConfigManager.inputData(f, "mailbox.outbox.1", null);
		}
	}

}
