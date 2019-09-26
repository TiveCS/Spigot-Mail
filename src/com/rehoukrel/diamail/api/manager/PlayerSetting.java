package com.rehoukrel.diamail.api.manager;

import com.rehoukrel.diamail.menu.SettingMenu;

import java.util.HashMap;

public class PlayerSetting {

    PlayerData playerData;
    SettingMenu settingMenu;

    HashMap<String, Object> settings = new HashMap<>();

    public PlayerSetting(PlayerData playerData){
        this.playerData = playerData;
        if (playerData != null){
            loadSettings();
            this.settingMenu = new SettingMenu(this);
        }
    }

    //============================================

    public void setSetting(String path, Object value){
        getSettings().put(path, value);
        getPlayerData().getConfigManager().inputAndSave("settings." + path, value);
    }

    public void loadSettings(){
        getPlayerData().getConfigManager().reloadConfig();

        getSettings().clear();
        for (String path : getPlayerData().getConfigManager().getConfig().getConfigurationSection("settings").getKeys(false)){
            Object obj = getPlayerData().getConfigManager().getConfig().get("settings." + path);
            getSettings().put(path, obj);
        }
    }

    //---------------------------------------------

    public HashMap<String, Object> getSettings() {
        return settings;
    }

    public SettingMenu getSettingMenu() {
        return settingMenu;
    }

    public PlayerData getPlayerData() {
        return playerData;
    }
}
