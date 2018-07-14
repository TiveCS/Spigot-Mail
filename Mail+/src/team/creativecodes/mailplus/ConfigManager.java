package team.creativecodes.mailplus;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigManager {
	
	private static Mail plugin = Mail.getPlugin(Mail.class);
	final static String playerdatalocation = plugin.getDataFolder() + "/PlayerData";
	
	public static void createPlayerData(String uuid) {
		File file = new File(playerdatalocation, uuid + ".yml");
		
		if (!plugin.getDataFolder().exists()) {
			plugin.getDataFolder().mkdirs();
		}
		if (!new File(playerdatalocation).exists()) {
			new File(playerdatalocation).mkdirs();
		}
		if (plugin.getDataFolder().exists()) {
			if (!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void savePlayerData(String uuid) {
		try {
			File file = new File(playerdatalocation, uuid + ".yml");
			if (!file.exists()) {
				file.createNewFile();
			}
			if (file.exists()) {
				getPlayerData(uuid).save(file);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean hasMailData(String uuid) {
		File file = new File(playerdatalocation, uuid + ".yml");
		if (file.exists()) {
			return true;
		}
		return false;
	}
	
	public static void putData(String uuid, String path, Object value) {
		File file = new File(playerdatalocation, uuid + ".yml");
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		config.set(path, value);
		try {
			config.save(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static FileConfiguration getPlayerData(String uuid) {
		File file = new File(playerdatalocation, uuid + ".yml");
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		FileConfiguration fg = YamlConfiguration.loadConfiguration(file);
		return fg;
	}

}
