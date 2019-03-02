package team.creativecode.diamail.manager;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.bukkit.entity.Player;

import team.creativecode.diamail.Main;
import team.creativecode.diamail.manager.menu.Setting;

public class PlayerSetting {

	Main plugin = Main.getPlugin(Main.class);
	final String path = "player-setting";
	
	private PlayerData pd;
	private Setting s = new Setting();
	private HashMap<String, Object> settingValues = new HashMap<String, Object>();
	private HashMap<String, Object> setting = new HashMap<String, Object>();
	
	public PlayerSetting(PlayerData pd) {
		this.pd = pd;
		
		for (String key : this.pd.getConfig().getConfigurationSection(path).getKeys(false)) {
			this.setting.put(key, pd.getConfig().get(path + "." + key));
		}
		
		loadSettingValues();
		
	}
	
	public void loadSettingValues() {
		for (String key : plugin.getConfig().getConfigurationSection("player-setting-values").getKeys(false)) {
			
			if (plugin.getConfig().get("player-setting-values." + key).toString().equalsIgnoreCase("TEXT")) {
				this.settingValues.put(key, "TEXT");
				continue;
			}else if (plugin.getConfig().get("player-setting-values." + key).toString().equalsIgnoreCase("NUMBER")) {
				Object obj = plugin.getConfig().get("player-setting." + key);
				if (obj instanceof Number) {
					this.settingValues.put(key, "NUMBER");
					continue;
				}else {
					plugin.getServer().getConsoleSender().sendMessage("[" + plugin.getDescription().getName() + "] " + "The value " + obj + " is not number on [player-setting-values." + key + "] (" + obj.getClass().getSimpleName() + ")");
				}
			}else {
				try {
					List<String> list;
					if (plugin.getConfig().getStringList("player-setting-values." + key).size() > 0) {
						list = plugin.getConfig().getStringList("player-setting-values." + key);
						String val = plugin.getConfig().getString("player-setting." + key);
						if (list.contains(val)) {
							this.settingValues.put(key, list);
						}else {
							plugin.getServer().getConsoleSender().sendMessage("[" + plugin.getDescription().getName() + "] " + "The value " + val + " is not listed on [player-setting-values." + key + "]");
						}
					}
				}catch(Exception e) {
					plugin.getServer().getConsoleSender().sendMessage("[" + plugin.getDescription().getName() + "] " + "Failed to load setting value from [player-setting-values." + key + "]");
				}
			}
			
		}
		
		System.out.println(this.settingValues);
	}
	
	public void changeSetting(String key, Object value) {
		this.setting.put(key, value);
		updateSetting();
	}
	
	public void updateSetting() {
		for (String key : this.setting.keySet()) {
			pd.getConfig().set(path + "." + key, this.setting.get(key));
		}
		try {
			pd.getConfig().save(pd.getFile());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void showSettingMenu(Player targetplayer) {
		this.s.open(targetplayer);
	}
	
	public HashMap<String, Object> getSettingValues(){
		return this.settingValues;
	}
	
	public PlayerData getPlayerData() {
		return this.pd;
	}
	
	public HashMap<String, Object> getSettings(){
		return this.setting;
	}
	
}
