package com.rehoukrel.diamail.utils.language;

import com.rehoukrel.diamail.utils.ConfigManager;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Language {

    public static HashMap<String, Language> registeredLanguages = new HashMap<String, Language>();

    // Language identity data
    JavaPlugin plugin;
    File folder;
    File file;
    String language;
    Placeholder placeholder = new Placeholder();
    boolean asTemplate = false, enabled = true;
    ConfigManager configManager = null;

    // Post data
    HashMap<String, HashMap<String, String>> placeholderSection = new HashMap<String, HashMap<String, String>>();
    HashMap<String, List<String>> defaultMessage = new HashMap<String, List<String>>(), loadedMessage = new HashMap<String, List<String>>(), hoverMessage = new HashMap<String, List<String>>();


    public Language(JavaPlugin plugin, String languageName){
        this.plugin = plugin;
        if (this.plugin == null){return;}

        this.folder = new File(plugin.getDataFolder(), "Language");
        this.file = new File(folder, languageName + ".yml");
        this.language = languageName;
        this.configManager = new ConfigManager(getFile());
    }

    //================================================

    public void message(String path, CommandSender commandSender){
        List<String> msg = getConfigManager().getConfig().getStringList(LanguageContents.MESSAGE.getPath() + "."+path);
        for (String s : msg){
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', getPlaceholder().use(s)));
        }
    }

    // Not done
    public void hoverMessage(String path, Player player){
        try {
            List<String> msg = getConfigManager().getConfig().getStringList(LanguageContents.MESSAGE.getPath() + "." + path),
                    hover = getConfigManager().getConfig().getStringList(LanguageContents.HOVER.getPath() + "." + path);
            msg = getPlaceholder().useMass(msg);
            hover = getPlaceholder().useMass(hover);
            // add hover
            ArrayList<BaseComponent> c = new ArrayList<>();
            for (String h : hover) {
                TextComponent t = new TextComponent(ChatColor.translateAlternateColorCodes('&', h));
                c.add(t);
            }
            BaseComponent[] bc = c.toArray(new BaseComponent[0]);
            // then set to textcomponent
            for (String s : msg) {
                TextComponent t = new TextComponent(ChatColor.translateAlternateColorCodes('&', s));
                t.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, bc));
                player.spigot().sendMessage(t);
            }
        }catch (Exception e){
            System.out.println("[" + plugin.getName() +"] Failed to send hover message");
            e.printStackTrace();
        }
    }

    //================================================

    // Add hardcoded placeholder
    public void addPlaceholder(String key, String placeholder, boolean forceSave){
        getConfigManager().init(LanguageContents.PLACEHOLDER.getPath() + "." + key, placeholder);
        if (forceSave){getConfigManager().saveConfig();}
    }

    // Default message will be written on file from any hardcoded
    public void addDefaultMessage(String path, List<String> messages, List<String> hoverMessages, boolean forceSave){
        getConfigManager().init(LanguageContents.MESSAGE.getPath() + "." + path, messages);
        if (hoverMessages != null && !hoverMessages.isEmpty()){
            getConfigManager().init(LanguageContents.HOVER.getPath() + "." + path, hoverMessages);
        }
        if (forceSave){getConfigManager().saveConfig();}
    }

    // Clear any existing data from variables loadedMessage and placeholder's replacer and overwrite defaultMessage variables
    // Then load any default message and additional message from file into loadedMessage variables
    public void loadData(){
        getLoadedMessage().clear();
        getPlaceholder().getReplacer().clear();

        getPlaceholder().getReplacer().putAll(getFilePlaceholders());
        getLoadedMessage().putAll(getFileMessages());
        for (String path : getDefaultMessage().keySet()){
            getDefaultMessage().put(path, getConfigManager().getConfig().getStringList(LanguageContents.MESSAGE.getPath() + "." + path));
        }
    }

    //-------------- GETTING PLACEHOLDERS ------------

    public HashMap<String, String> getFilePlaceholders(){
        HashMap<String, String> plcs = new HashMap<String, String>();
        for (String path : getConfigManager().getConfig().getConfigurationSection(LanguageContents.PLACEHOLDER.getPath()).getKeys(false)){
            String s = getConfigManager().getConfig().getString(LanguageContents.PLACEHOLDER.getPath() + "." + path);
            plcs.put(path, s);
        }
        return plcs;
    }

    //-------------- GETTING CONTENTS-------------

    public HashMap<String, List<String>> getFileMessages(){
        HashMap<String, List<String>> map = new HashMap<String, List<String>>();
        for (String path : getConfigManager().getConfig().getConfigurationSection(LanguageContents.MESSAGE.getPath()).getKeys(false)){
            List<String> l = getConfigManager().getConfig().getStringList(LanguageContents.MESSAGE.getPath() + "." + path);
            map.put(path, l);
        }
        return map;
    }


    //------------------------------------------------

    public void setAsTemplate(boolean asTemplate) {
        this.asTemplate = asTemplate;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setLoadedMessage(HashMap<String, List<String>> loadedMessage) {
        this.loadedMessage = loadedMessage;
    }

    public boolean isAsTemplate() {
        return asTemplate;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public HashMap<String, HashMap<String, String>> getPlaceholderSection() {
        return placeholderSection;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public HashMap<String, List<String>> getDefaultMessage() {
        return defaultMessage;
    }

    public HashMap<String, List<String>> getLoadedMessage() {
        return loadedMessage;
    }

    public HashMap<String, List<String>> getHoverMessage() {
        return hoverMessage;
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    public Placeholder getPlaceholder() {
        return placeholder;
    }

    public File getFolder() {
        return folder;
    }

    public File getFile() {
        return file;
    }

    public String getLanguage() {
        return language;
    }
}
