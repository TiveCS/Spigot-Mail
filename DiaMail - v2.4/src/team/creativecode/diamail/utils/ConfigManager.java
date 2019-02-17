package team.creativecode.diamail.utils;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.google.common.io.Files;

public class ConfigManager {

    public static void input(File file, String path, Object object){
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        config.set(path, object);
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void saveCopy(File source, File target) {
    	if (!target.exists()) {
    		copy(source, target);
    	}
    }
    
    public static void copy(File source, File target) {
    	try {
			Files.copy(source, target);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public static Object getWithChecking(File file, String path) {
    	FileConfiguration config = YamlConfiguration.loadConfiguration(file);
    	if (config.contains(path)) {
    		return config.get(path);
    	}else {
    		return null;
    	}
    }

    public static Object get(File file, String path){
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        return config.get(path);
    }

    public static FileConfiguration getConfig(File file){
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        return config;
    }

    public static boolean contains(File file, String path){
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        return config.contains(path);
    }

    public static void init(File file, String path, Object obj){
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        if (!config.contains(path)) {
            config.set(path, obj);
            try {
                config.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void createFolder(String path){
        File folder = new File(path);
        if (!folder.exists()){
            folder.mkdirs();
        }
    }

    public static void createFile(String path, String filename) {
        createFolder(path);
        File file = new File(path, filename);
        createFile(file);
    }

    public static void createFile(File file) {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}