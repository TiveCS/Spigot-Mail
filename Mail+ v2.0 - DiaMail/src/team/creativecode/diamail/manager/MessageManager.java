package team.creativecode.diamail.manager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import team.creativecode.diamail.Main;

public class MessageManager {
	
	static Main plugin = Main.getPlugin(Main.class);
	static FileConfiguration config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "message.yml"));
	
	public enum MessageType{
		
		PRE_SEND_MAIL, PRE_SEND_MAIL_GUI, SET_TARGET, SET_ITEM, ADD_MESSAGE, REMOVE_MESSAGE, UNSET_ITEM,
		PRE_SEND_MAIL_INFO, RETURN_ITEM, PRE_SEND_MAIL_LEAVE, PRE_SEND_MAIL_ALL,
		
		SEND_MAIL, DELETE_MAIL, CHECK_MAIL, GET_ITEM_MAIL,			
	
		INTERNAL_ERROR, UNKNOWN_COMMAND, SET_TARGET_NOT_FOUND, SEND_MAIL_FAILED, NO_PERMISSION,
	
		COMMAND_HELP;
	}
	
	public static void send(Player p, List<String> list) {
		for (String s : list) {
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
		}
	}
	
	public static void send(Player p, MessageType type) {
		String path = getPath(type);
		String[] keys = path.split("[.]");
		
		try {
			send(p, keys[0] + ".header");
			send(p, path);
			send(p, keys[0] + ".footer");
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void send(Player p, String path) {
		List<String> list = new ArrayList<String>();
		String msg = "";
		
		try {
			if (isList(config.get(path))) {
				list = config.getStringList(path);
			}else {
				msg = config.getString(path);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		if (list.size() > 0) {
			for (String m : list) {
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', m));
			}
		}else {
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
		}
	}
	
	public static boolean isList(Object obj) {
		return (obj instanceof List);
	}
	
	public static boolean useList(String path) {
		boolean bol = config.get(path) instanceof List;
		return bol;
	}
	
	public static Object get(MessageType type) {
		String path = getPath(type);
		try {
			List<String> list = new ArrayList<String>();
			if (isList(path)) {
				list = config.getStringList(path);
				return list;
			}else {
				return ChatColor.translateAlternateColorCodes('&', config.getString(path));
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getPath(MessageType type) {
		String s = null;
		String t = type.toString().toLowerCase();
		t = t.replace("_", "-");
		ConfigurationSection cs = config.getRoot();
		for (String k : cs.getKeys(false)) {
			if (config.getConfigurationSection(k + ".value").getKeys(false).contains(t)) {
				s = k + ".value." + t;
			}
		}
		return s;
	}
	
	public static Object get(String path) {
		try {
			if (isList(config.get(path))) {
				List<String> arr = new ArrayList<String>();
				arr = config.getStringList(path);
				for (int i = 0; i < arr.size(); i++) {
					arr.set(i, ChatColor.translateAlternateColorCodes('&', arr.get(i)));
				}
				return arr;
			}else {
				return ChatColor.translateAlternateColorCodes('&', config.getString(path));
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static List<String> getList(MessageType type) {
		List<String> list = new ArrayList<String>();
		String t = type.toString().toLowerCase().replace("_", "-"), path = null;
		ConfigurationSection cs = config.getRoot();
		try {
			for (String c : cs.getKeys(false)) {
				try {
					if (config.getConfigurationSection(c + ".value").getKeys(false).contains(t)) {
						path = c + ".value." + t;
						break;
					}
				}catch(Exception e) {}
			}
			
			if (path != null && useList(path) == true) {
				list = config.getStringList(path);
			}
		}catch(Exception e) {
			e.printStackTrace();
			list.add(ChatColor.translateAlternateColorCodes('&', config.getString("error.value.internal-error")));
		}
		return list;
	}
	
	public static List<String> getList(String path){
		List<String> list = new ArrayList<String>();
		try {
			if (useList(path)) {
				list = config.getStringList(path);
			}
		}catch(Exception e) {
			e.printStackTrace();
			list.add(ChatColor.translateAlternateColorCodes('&', config.getString("error.value.internal-error")));
		}
		return list;
	}
	
	public static String getMsg(String path) {
		String s = "";
		try {
			if (useList(path) == false) {
				s = config.getString(path);
			}
		}catch(Exception e) {
			e.printStackTrace();
			if (useList("error.value.internal-error")) {
				for (String err : config.getStringList("error.value.internal-error")) {
					s = s.equals("") ? config.getStringList("error.value.internal-error").get(0) : s + ". " + err;
				}
			}else {
				s = config.getString("error.value.internal-error");
			}
		}
		s = ChatColor.translateAlternateColorCodes('&', s);
		return s;
	}
	
	public static String getMsg(MessageType type) {
		String s = null;
		ConfigurationSection cs = config.getRoot();
		String t = type.toString().toLowerCase().replace("_", "-");
		String path = null;
		
		for (String c : cs.getKeys(false)) {
			try {
				if (config.getConfigurationSection(c + ".value").getKeys(false).contains(t)) {
					path = c + ".value." + t;
					break;
				}
			}catch(Exception e) {}
		}
		
		if (path != null && useList(path) == false) {
			s = config.getString(path);
		}
		s = ChatColor.translateAlternateColorCodes('&', s);
		return s;
	}

}
