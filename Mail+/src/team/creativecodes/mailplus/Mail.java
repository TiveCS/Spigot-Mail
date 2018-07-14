package team.creativecodes.mailplus;

import org.bukkit.plugin.java.JavaPlugin;

import team.creativecodes.mailplus.cmd.MailCmd;
import team.creativecodes.mailplus.event.DataInitEvent;

public class Mail extends JavaPlugin {

	public void onEnable() {
		loadConfig();
		
		this.getCommand("mail").setExecutor(new MailCmd());

		this.getServer().getPluginManager().registerEvents(new DataInitEvent(), this);
	}
	
	public void loadConfig() {
		this.getConfig().options().copyDefaults(true);
		saveConfig();
	}
	
}
