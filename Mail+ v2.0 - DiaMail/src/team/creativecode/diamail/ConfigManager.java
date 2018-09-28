package team.creativecode.diamail;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigManager {
	
	private static Main plugin = Main.getPlugin(Main.class);
	
	public static void createFolder(String path) {
		File file = new File(path);
		if (!plugin.getDataFolder().exists()) {
			plugin.getDataFolder().mkdir();
		}
		if (plugin.getDataFolder().exists()) {
			if (!file.exists()) {
				file.mkdir();
			}
		}
	}
	
	public static void createFile(String path, String name) {
		File file = new File(path, name);
		if (!plugin.getDataFolder().exists()) {
			plugin.getDataFolder().mkdir();
		}
		if (plugin.getDataFolder().exists()) {
			if (!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void createFile(String path) {
		File file = new File(path);
		if (!plugin.getDataFolder().exists()) {
			plugin.getDataFolder().mkdir();
		}
		if (plugin.getDataFolder().exists()) {
			if (!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public static FileConfiguration getConfig(File file) {
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		return config;
	}
	
	public static void inputData(File file, String path, Object obj) {
		try {
			FileConfiguration config = getConfig(file);
			config.set(path, obj);
			try {
				config.save(file);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}

}
