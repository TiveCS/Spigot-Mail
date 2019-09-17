package com.rehoukrel.diamail;

import com.rehoukrel.diamail.api.manager.PlayerData;
import com.rehoukrel.diamail.cmds.DiaMailCmd;
import com.rehoukrel.diamail.events.BasicEvent;
import com.rehoukrel.diamail.menu.MailContents;
import com.rehoukrel.diamail.menu.MailEditor;
import com.rehoukrel.diamail.menu.MailboxMenu;
import com.rehoukrel.diamail.utils.sql.MySQLManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.SQLException;

public class DiaMail extends JavaPlugin {

    public static MySQLManager mysql;
    public static String table_pd = "diamail_playerdata", table_inbox = "diamail_inbox", table_outbox = "diamail_outbox";
    String host, username, password, database;
    int port;

    @Override
    public void onEnable() {
        loadConfig();
        loadLanguage();
        if (PlayerData.useMySQL){
            connectSQL();
        }
        loadMenu();
        loadEvent();
        loadCmd();
    }

    private void loadLanguage() {
        File folder = new File(getDataFolder(), "Language"), enUS = new File(folder, "en-US.yml");
        if (!folder.exists()){
            folder.mkdir();
        }
        if (!enUS.exists()) {
            saveResource("Language/en-US.yml", false);
        }
    }

    private void loadMenu() {
        new MailboxMenu(null);
        new MailContents(null, null);
        new MailEditor(null);
    }

    @Override
    public void onDisable() {
        try {
            if (mysql != null) {
                if (mysql.getConnection() != null && !mysql.getConnection().isClosed()) {
                    mysql.getConnection().close();
                    getLogger().info("MySQL connection is closed..");
                }
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
        String pdt = "CREATE TABLE IF NOT EXISTS " + table_pd + " (uuid VARCHAR(36) NOT NULL, name VARCHAR(40) NOT NULL, blocked TEXT, PRIMARY KEY (uuid))";
        String ibt = "CREATE TABLE IF NOT EXISTS " + table_inbox + " (receiver_uuid VARCHAR(36) NOT NULL, sender TEXT NOT NULL, uuid VARCHAR(36) NOT NULL, message TEXT, item LONGTEXT, type VARCHAR(100) NOT NULL)";
        String obt = "CREATE TABLE IF NOT EXISTS " + table_outbox + " (sender_uuid VARCHAR(36) NOT NULL, receiver TEXT NOT NULL, uuid VARCHAR(36) NOT NULL, message TEXT, item LONGTEXT, type VARCHAR(100) NOT NULL)";
        mysql.runCommand(pdt);
        mysql.runCommand(ibt);
        mysql.runCommand(obt);
    }

    private void loadEvent() {
        getServer().getPluginManager().registerEvents(new BasicEvent(), this);
    }

    private void loadCmd() {
        getCommand("diamail").setExecutor(new DiaMailCmd());
    }
}
