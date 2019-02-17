package team.creativecode.diamail.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import team.creativecode.diamail.Main;

public class Language {
	
	
	public static HashMap<String, Language> languages = new HashMap<String, Language>();
	public static Main plugin = Main.getPlugin(Main.class);
	public static File folder = new File(plugin.getDataFolder() + "/Language");
	public static File defFile = new File(plugin.getDataFolder() + "/Language", "en-US.yml");
	
	private HashMap<String, List<String>> msg = new HashMap<String, List<String>>();
	private File file;
	private FileConfiguration config;
	
	public static void loadLanguages() {
		if (!folder.exists()) {folder.mkdir();}
		if (folder.exists()){
			if (!defFile.exists()){
				plugin.saveResource("Language/en-US.yml", false);
			}
		}
		for (File files : folder.listFiles()) {
			languages.put(files.getName(), new Language(files.getName()));
		}
		plugin.getServer().getConsoleSender().sendMessage("[" + plugin.getDescription().getName() + "] " + languages.size() + " language file(s) has been loaded");
	}
	
	public Language(String filename) {
		file = new File(plugin.getDataFolder() + "/Language", filename);
		try {
			if (!file.getName().equals(defFile.getName())) {
				FileUtils.copyFile(defFile, file);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		config = YamlConfiguration.loadConfiguration(file);
		loadData();
	}
	
	public void loadData() {
        List<String> l = new ArrayList<String>(config.getRoot().getKeys(false));
        l.remove("file-version");
		for (String root : l) {
			for (String m : config.getConfigurationSection(root).getKeys(false)) {
				List<String> list = new ArrayList<String>();
				if (config.isString(root + "." + m)) {
					list.add(ChatColor.translateAlternateColorCodes('&', config.getString(root + "." + m)));
				}
				if (config.isList(root + "." + m)) {
					list = config.getStringList(root + "." + m);
				}
				msg.put(root + "." + m, DataConverter.colored(list));
			}
		}
	}
	
	public void sendMessage(Player p, String path) {
		for (String s : getMessages().get(path)) {
			p.sendMessage(s);
		}
	}

    public void sendMessage(Player p, List<String> msg) {
        for (String s : msg) {
            p.sendMessage(s);
        }
    }
	
	public HashMap<String, List<String>> getMessages(){
		return msg;
	}
	
	public File getFile() {
		return file;
	}
	
	public FileConfiguration getConfig() {
		return config;
	}
	
}
