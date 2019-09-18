package com.rehoukrel.diamail.utils.language;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Language {

    private Plugin plugin;
    private File file;
    private FileConfiguration config;
    private String language;

    private HashMap<Path, HashMap<String, List<String>>> msg = new HashMap<Path, HashMap<String, List<String>>>();
    private boolean asDefault = true;

    public enum Path{
        EVENT, HELP, ERROR, SYSTEM
    }

    public Language(Plugin plugin, String lang){
        this.plugin = plugin;
        this.language = lang;
        this.file = new File(plugin.getDataFolder() + "/Language", lang + ".yml");
        if (!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.config = YamlConfiguration.loadConfiguration(this.file);
        initLanguageData();
    }

    // Action

    public void initLanguageData(){
        for (Path key : getMessage().keySet()){
            HashMap<String, List<String>> list = getMessage().get(key);
            for (String st : list.keySet()){
                if (list.size() > 1) {
                    getConfig().set(key.name().toLowerCase() + "." + st, list.get(st));
                }else{
                    getConfig().set(key.name().toLowerCase() + "." + st, list.get(st).get(0));
                }
            }
        }
        try {
            getConfig().save(getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Action getter

    public Object getMessageData(Path path, String st){
        List<String> list;
        try{
            list = getConfig().getStringList(path.name().toLowerCase() + "." + st);
            return list;
        }catch(Exception e){
            return getConfig().getString(path.name().toLowerCase() + "." + st);
        }
    }

    public boolean isList(Path path, String st){
        Object obj = getMessageData(path, st);
        return (obj instanceof ArrayList || obj instanceof List);
    }

    // Setter

    public void setAsDefault(boolean asDefault) {
        this.asDefault = asDefault;
    }

    // Getter

    public boolean isAsDefault() {
        return asDefault;
    }

    public String getLanguageName() {
        return language;
    }

    public HashMap<Path, HashMap<String, List<String>>>  getMessage(){
        return msg;
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public File getFile() {
        return file;
    }
}
