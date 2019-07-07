package com.rehoukrel.diamail;

import com.rehoukrel.diamail.cmds.DiaMailCmd;
import com.rehoukrel.diamail.events.BasicEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class DiaMail extends JavaPlugin {

    @Override
    public void onEnable() {
        loadConfig();
        loadEvent();
        loadCmd();
    }

    private void loadConfig() {
        getConfig().options().copyDefaults(true);
        saveConfig();

    }

    private void loadEvent() {
        getServer().getPluginManager().registerEvents(new BasicEvent(), this);
    }

    private void loadCmd() {
        getCommand("diamail").setExecutor(new DiaMailCmd());
    }
}
