package team.creativecode.diamail.manager;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.bukkit.entity.Player;

import team.creativecode.diamail.Main;
import team.creativecode.diamail.manager.menu.Setting;
import team.creativecode.diamail.utils.DataConverter;

public class PlayerSetting {

	Main plugin = Main.getPlugin(Main.class);
	final String path = "player-setting";
	
	private PlayerData pd;
	private Setting s;
	private HashMap<String, Object> settingValues = new HashMap<String, Object>();
	private HashMap<String, Object> setting = new HashMap<String, Object>();
	
	public PlayerSetting(PlayerData pd) {
		this.pd = pd;
		
		for (String key : this.pd.getConfig().getConfigurationSection(path).getKeys(false)) {
			this.setting.put(key, pd.getConfig().get(path + "." + key));
		}
		
		loadSettingValues();
		
		this.s = new Setting(this);
		this.s.inputObject("player", pd.getPlayer());
		this.s.inputObject("playersetting", this);
		this.s.inputObject("playerdata", pd);
		
		initPlaceholder();
		
	}
	
	public void initPlaceholder() {
		for (String key : this.setting.keySet()) {
			getPlayerData().getPlaceholder().inputData("settings_" + key, this.setting.get(key).toString());
		}
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
		
	}
	
	public void switchSetting(String key) {
		List<String> values = DataConverter.objectToList(this.getSettingValues().get(key));
		int maxKey = values.size() - 1;
		int currentKey = values.indexOf(this.getSettings().get(key).toString());
		int nextKey = 0;
		
		if (currentKey == maxKey) {
			nextKey = 0;
		}else if (currentKey < maxKey){
			nextKey = currentKey + 1;
		}
		
		Object value = values.get(nextKey);
		
		try {
			if (value.toString().equalsIgnoreCase("true") || value.toString().equalsIgnoreCase("false")) {
				value = Boolean.parseBoolean(value.toString());
			}
		}catch(Exception e) {
			value = values.get(nextKey);
		}
		
		changeSetting(key, value);
	}
	
	public void changeSetting(String key, Object value) {
		this.setting.put(key, value);
		updateSetting();
	}
	
	public void numberIncreaseSetting(String key, int inc) {
		try {
			Object o = this.setting.get(key);
			if (o instanceof Number) {
				if (o instanceof Integer) {
					int i = (int) o;
					i+=inc;
					changeSetting(key, i);
				}else if (o instanceof Double) {
					double d = (double) o;
					d+=inc;
					changeSetting(key, d);
				}
			}
		}catch(Exception e) {}
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
		initPlaceholder();
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
