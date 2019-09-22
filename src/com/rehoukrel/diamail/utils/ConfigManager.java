package com.rehoukrel.diamail.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ConfigManager {

    private File file;
    private FileConfiguration config;

    public ConfigManager(File file){
        this.file = file;

        if (!this.file.exists()){
            try {
                if (file.isDirectory()){
                    this.file.mkdirs();
                }else {
                    if (file.getParentFile().exists()) {
                        this.file.createNewFile();
                    }else{
                        file.getParentFile().mkdirs();
                        this.file.createNewFile();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.config = YamlConfiguration.loadConfiguration(this.file);
    }

    public boolean contains(String path){
        return getConfig().contains(path);
    }

    public Object get(String path){
        if (getConfig().contains(path)){
            return getConfig().get(path);
        }
        return null;
    }

    public void init(String path, Object value){
        if (!getConfig().contains(path)){
            getConfig().set(path, value);
        }
    }

    public void initSection(String path){
        if (!getConfig().contains(path)){
            getConfig().createSection(path);
        }
    }

    public void inputSection(String path){
        getConfig().createSection(path);
    }


    public void initAndSave(String path, Object value){
        if (!getConfig().contains(path)){
            getConfig().set(path, value);
            saveConfig();
        }
    }

    public void initAndSaveSection(String path){
        if (!getConfig().contains(path)){
            getConfig().createSection(path);
            saveConfig();
        }
    }

    public void inputAndSaveSection(String path){
        getConfig().createSection(path);
        saveConfig();
    }

    public void inputAndSave(String path, Object value){
        getConfig().set(path, value);
        saveConfig();
    }

    public void input(String path, Object value){
        getConfig().set(path, value);
    }

    public void saveConfig(){
        try {
            getConfig().save(getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public File getFile() {
        return file;
    }

    public void reloadConfig(){
        this.config = YamlConfiguration.loadConfiguration(getFile());
    }

    public FileConfiguration getConfig() {
        return config;
    }

}
