package com.rehoukrel.diamail.utils.language;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;

public class Placeholder {

    private HashMap<String, String> replacer = new HashMap<String, String>();
    private HashMap<String, List<String>> replacerList = new HashMap<>();
    private JavaPlugin plugin = null;

    public Placeholder(){
        initializeDefaultReplacer();
    }

    public Placeholder(HashMap<String, String> replacer){
        this();
        getReplacer().putAll(replacer);
    }

    public Placeholder(JavaPlugin plugin, HashMap<String, String> replacer){
        this();
        this.plugin = plugin;
        getReplacer().putAll(replacer);
    }

    public Placeholder(JavaPlugin plugin){
        this();
        this.plugin = plugin;
    }

    public List<String> useMass(List<String> l){
        for (int i = 0; i < l.size(); i++){
            String s = l.get(i);
            for (String r : getReplacer().keySet()){
                s = s.replace("%" + r +"%", getReplacer().get(r));
                l.set(i, s);
            }
        }
        return l;
    }

    public ItemStack useItemStack(ItemStack item){
        ItemMeta meta = item.getItemMeta();
        if (meta.hasDisplayName()){
            meta.setDisplayName(use(meta.getDisplayName()));
        }
        if (meta.hasLore()){
            meta.setLore(useMass(meta.getLore()));
        }
        return item;
    }

    public List<String> translateColor(List<String> l){
        for (int i = 0; i < l.size(); i++){
            l.set(i, ChatColor.translateAlternateColorCodes('&', l.get(i)));
        }
        return l;
    }

    public String use(String s){
        for (String r : getReplacer().keySet()){
            s = s.replace("%" + r + "%", getReplacer().get(r));
        }
        return s;
    }

    public List<String> useListReplacer(List<String> list){
    	for (int line = 0; line < list.size(); line++) {
			StringBuilder s = new StringBuilder(list.get(line));
			for (String repl : getReplacerList().keySet()) {
				if (s.toString().contains("%" + repl +"%")) {
					int index = s.indexOf("%" + repl +"%"), length = ("%" + repl +"%").length();
					String afterReplacer = s.substring((index + length)), beforeReplacer = s.substring(0, index);
					for (int i = 0; i < getReplacerList().get(repl).size(); i++) {
						if (i == 0) {
							list.set(line, beforeReplacer + getReplacerList().get(repl).get(i));
							continue;
						}
						if (i == getReplacerList().get(repl).size() - 1) {
							list.add(line + i, getReplacerList().get(repl).get(i) + afterReplacer);
							continue;
						}
						list.add(line + i, getReplacerList().get(repl).get(i));
					}
				}
			}
		}
		return list;
    }

    public void addReplacer(String key, String replacer){
        getReplacer().put(key, replacer);
    }

    public void addReplacerList(String key, List<String> replacer){
        getReplacerList().put(key, replacer);
    }

    public void initializeDefaultReplacer(){
        if (getPlugin() != null) {
            addReplacer("prefix", getPlugin().getConfig().contains("prefix") ? getPlugin().getConfig().getString("prefix") : "");
        }
    }

    public boolean isUsingPlugin(){
        return !getPlugin().equals(null);
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    public HashMap<String, String> getReplacer() {
        return replacer;
    }

    public HashMap<String, List<String>> getReplacerList() {
        return replacerList;
    }
}
