package team.creativecode.diamail.utils;

import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;

import team.creativecode.diamail.Main;

public class Placeholder {
	
	Main plugin = Main.getPlugin(Main.class);
	
	//Values
	private HashMap<String, String> replacer = new HashMap<String, String>(); 
	
	public Placeholder() {
		loadDefaultData();
	}
	
	public String use(String text) {
		for (String obj : replacer.keySet()) {
			text = text.replace("%" + obj + "%", replacer.get(obj));
		}
		return text;
	}
	
	public List<String> useAsList(List<String> list){
		for (int i = 0; i < list.size(); i++) {
			list.set(i, use(list.get(i)));
		}
		return list;
	}
	
	public void inputData(String path, String obj) {
		replacer.put(path, obj);
	}
	
	public void insertMapData(HashMap<String, String> map) {
		for (String key : map.keySet()) {
			replacer.put(key, map.get(key));
		}
	}
	
	private void loadDefaultData() {
		replacer.put("prefix", ChatColor.translateAlternateColorCodes('&',plugin.getConfig().getString("prefix")));
		replacer.put("online-player", plugin.getServer().getOnlinePlayers().size() + "");
		replacer.put("max-player", plugin.getServer().getMaxPlayers() + "");
		replacer.put("version", plugin.getDescription().getVersion() + "");
	}
	
}

