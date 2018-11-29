package team.creativecode.diamail;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import team.creativecode.diamail.cmds.DiamailCmd;
import team.creativecode.diamail.event.BasicActivity;

public class Main extends JavaPlugin {
	
	public static HashMap<String, File> files = new HashMap<String, File>();
	
	public static FileConfiguration msgConfig;
	
	public void onEnable() {
		
		this.getConfig().options().copyDefaults(true);
		saveConfig();
		loadJarFiles();
		ConfigManager.createFolder(this.getDataFolder().toString() + "/PlayerData");

		this.getCommand("diamail").setExecutor(new DiamailCmd());
		this.getServer().getPluginManager().registerEvents(new BasicActivity(), this);
		
	}
	
	public void loadJarFiles() {
		File msg = new File(this.getDataFolder(), "message.yml");
		if (!msg.exists()) {
			this.saveResource("message.yml", false);
		}
	}
	
	public static void initFile(File f, String name) {
		if (!f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else {
			files.put(name, f);
		}
	}
	
	public static void initFile(String path, String file) {
		File f = new File(path, file);
		initFile(f, file);
	}

}
