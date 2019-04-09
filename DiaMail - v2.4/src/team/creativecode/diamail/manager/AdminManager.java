package team.creativecode.diamail.manager;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import team.creativecode.diamail.Main;

public class AdminManager {
	
	Main plugin = Main.getPlugin(Main.class);
	File physicalBox = new File(plugin.getDataFolder(), "block.yml");
	FileConfiguration physicalBoxConfig;
	
	public AdminManager(Player p) {
		if (!getBlockFile().exists()) {
			try {
				getBlockFile().createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		physicalBoxConfig = YamlConfiguration.loadConfiguration(getBlockFile());
	}
	
	public enum PhysicalBlock{
		MAILBOX
	}
	
	public void physicalMailbox(Block block) {
		Location loc = block.getLocation();
		double x = loc.getX(),y = loc.getY(),z = loc.getZ();
		String world = loc.getWorld().getName();
		
		String path = "mailbox." + UUID.randomUUID();
		getBlockConfig().set(path + ".world", world);
		getBlockConfig().set(path + ".x", x);
		getBlockConfig().set(path + ".y", y);
		getBlockConfig().set(path + ".z", z);
		
		try {
			getBlockConfig().save(getBlockFile());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public FileConfiguration getBlockConfig() {
		return physicalBoxConfig;
	}
	
	public File getBlockFile() {
		return this.physicalBox;
	}

}
