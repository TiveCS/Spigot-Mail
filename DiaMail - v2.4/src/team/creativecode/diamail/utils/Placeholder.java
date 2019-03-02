package team.creativecode.diamail.utils;

import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;

import team.creativecode.diamail.Main;

public class Placeholder {
	
	Main plugin = Main.getPlugin(Main.class);
	
	//Values
	private HashMap<String, String> replacer = new HashMap<String, String>();
	private HashMap<String, List<String>> replacerList = new HashMap<String, List<String>>();
	
	public Placeholder() {
		loadDefaultData();
	}
	
	public List<String> listUse(List<String> list){
		for (int line = 0; line < list.size(); line++) {
			String msg = list.get(line);
			int size = 0;
			for (String path : this.replacerList.keySet()) {
				if (msg.contains("%" + path + "%")) {
					msg = msg.replace("%" + path + "%", "");
					String c = msg;
					size = this.replacerList.get(path).size();
					for (int i = 0; i < size; i++) {
						String s = ChatColor.translateAlternateColorCodes('&', "&f" + this.replacerList.get(path).get(i));
						if (i >= line + size) {
							c = c.concat(s);
							break;
						}else {
							list.add(line + i, s);
							continue;
						}
					}
					list.set(line + size, c);
					break;
				}
			}
		}
		return list;
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
	
	public void inputListData(String path, List<String> list) {
		this.replacerList.put(path, list);
	}
	
	public void insertMapData(HashMap<String, String> map) {
		for (String key : map.keySet()) {
			replacer.put(key, map.get(key));
		}
	}
	
	
	public HashMap<String, String> getReplacer(){
		return this.replacer;
	}
	
	public HashMap<String, List<String>> getReplacerList(){
		return this.replacerList;
	}
	
	private void loadDefaultData() {
		replacer.put("prefix", ChatColor.translateAlternateColorCodes('&',plugin.getConfig().getString("prefix")));
		replacer.put("online-player", plugin.getServer().getOnlinePlayers().size() + "");
		replacer.put("max-player", plugin.getServer().getMaxPlayers() + "");
		replacer.put("version", plugin.getDescription().getVersion() + "");
	}
	
}

