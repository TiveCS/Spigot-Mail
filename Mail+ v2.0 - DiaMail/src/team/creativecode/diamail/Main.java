package team.creativecode.diamail;

import org.bukkit.plugin.java.JavaPlugin;

import team.creativecode.diamail.cmds.DiamailCmd;
import team.creativecode.diamail.event.BasicActivity;

public class Main extends JavaPlugin {
	
	public void onEnable() {
		
		this.getConfig().options().copyDefaults(true);
		saveConfig();
		ConfigManager.createFolder(this.getDataFolder().toString() + "/PlayerData");
		
		this.getCommand("diamail").setExecutor(new DiamailCmd());
		
		this.getServer().getPluginManager().registerEvents(new BasicActivity(), this);
	}

}
