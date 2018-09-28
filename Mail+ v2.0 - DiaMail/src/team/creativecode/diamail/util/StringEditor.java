package team.creativecode.diamail.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;

public class StringEditor {
	
	public static List<String> normalizeColor(List<String> list){
		for (int i = 0; i < list.size(); i++) {
			String s = list.get(i);
			if (!s.startsWith("&f") || !s.startsWith(ChatColor.WHITE + "")) {
				s = ChatColor.translateAlternateColorCodes('&', "&f" + s);
				list.set(i, s);
			}
		}
		return list;
	}
	
	public static List<String> stringToList(String text, int length){
		List<String> list = new ArrayList<String>();
		int l = 0;
		String s = "";
		boolean check = false;
		String[] split = text.split(" ");
		for (int i = 0; i < split.length; i++) {
			String sp = split[i];
			if (s.equals("")) {
				check = false;
				s = sp;
				l += sp.length();
			}else {
				s = s + " " + sp;
				l += sp.length();
				if (l > length) {
					check = true;
					list.add(ChatColor.translateAlternateColorCodes('&', s));
					l = 0;
					s = "";
				}
			}
			if (s.length() > 0) {
				if (check == false) {
					if (i + 1 == split.length) {
						list.add(ChatColor.translateAlternateColorCodes('&', s));
					}
				}
			}
		}
		return list;
	}
	
}
