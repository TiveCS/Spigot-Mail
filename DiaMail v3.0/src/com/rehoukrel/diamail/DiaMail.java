package com.rehoukrel.diamail;

import com.rehoukrel.diamail.api.manager.PlayerData;
import com.rehoukrel.diamail.cmds.DiaMailCmd;
import com.rehoukrel.diamail.events.BasicEvent;
import com.rehoukrel.diamail.utils.sql.MySQLManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public class DiaMail extends JavaPlugin {

    public static MySQLManager mysql;
    public static String table_pd = "diamail_playerdata";
    String host, username, password, database;
    int port;

    @Override
    public void onEnable() {
        loadConfig();
        if (PlayerData.useMySQL){
            connectSQL();
        }
        loadEvent();
        loadCmd();
    }

    @Override
    public void onDisable() {
        try {
            if (mysql.getConnection() != null && mysql.getConnection().isClosed()) {
                mysql.getConnection().close();
                getLogger().info("MySQL connection is closed..");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadConfig() {
        getConfig().options().copyDefaults(true);
        saveConfig();

        PlayerData.useMySQL = getConfig().getBoolean("mysql.enable");
    }

    private void connectSQL(){
        host = getConfig().getString("mysql.host");
        username = getConfig().getString("mysql.username");
        password = getConfig().getString("mysql.password");
        database = getConfig().getString("mysql.database");
        port = getConfig().getInt("mysql.port");

        mysql = new MySQLManager(this, username, password, database, host, port);
        mysql.openConnection();
    }

    private void loadEvent() {
        getServer().getPluginManager().registerEvents(new BasicEvent(), this);
    }

    private void loadCmd() {
        getCommand("diamail").setExecutor(new DiaMailCmd());
    }
}
