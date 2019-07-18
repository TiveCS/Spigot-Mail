package com.rehoukrel.diamail.api.manager;

import com.rehoukrel.diamail.DiaMail;
import com.rehoukrel.diamail.menu.MailboxMenu;
import com.rehoukrel.diamail.utils.ConfigManager;
import com.rehoukrel.diamail.utils.sql.MySQLManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlayerData {

    public static DiaMail plugin = DiaMail.getPlugin(DiaMail.class);
    public static File folder = new File(plugin.getDataFolder(), "PlayerData");
    public static HashMap<OfflinePlayer, PlayerData> listedData = new HashMap<>();
    public static boolean useMySQL = false;

    private OfflinePlayer player;
    private File file;
    private ConfigManager configManager;

    private List<Mail> templates = new ArrayList<>();
    private Mailbox mailbox;

    private MailboxMenu mailboxMenu;

    public static PlayerData getPlayerData(OfflinePlayer op){
        if (listedData.containsKey(op)){
            return listedData.get(op);
        }else{
            return new PlayerData(op);
        }
    }

    public PlayerData(OfflinePlayer player){
        if (!folder.exists()){
            folder.mkdirs();
        }
        this.player = player;
        this.file = new File(folder, player.getUniqueId().toString() + ".yml");
        this.configManager = new ConfigManager(plugin, file);

        loadDefaultData();
        if (useMySQL){
            loadDefaultDataSQL();
        }
        this.mailbox = new Mailbox(this);
        this.mailboxMenu = new MailboxMenu(this);

        listedData.put(player, this);
    }

    public void loadDefaultDataSQL(){
        MySQLManager mysql = DiaMail.mysql;
        String table = DiaMail.table_pd;
        HashMap<String, Object> map = new HashMap<>();
        boolean hasUUID = false, hasName = false;
        hasUUID = mysql.getData(table, "uuid", "uuid='" + getPlayer().getUniqueId().toString() + "'").size() > 0;
        hasName = mysql.getData(table, "name", "uuid='" + getPlayer().getUniqueId().toString() + "'").size() > 0;
        map.put("name", getPlayer().getName());
        if (!hasUUID && !hasName) {
            map.put("uuid", getPlayer().getUniqueId());
            mysql.insertSingleData(table, map);
        }else {
            mysql.updateData(table, "uuid='" + getPlayer().getUniqueId().toString() +"'", map);
        }

    }

    public void loadDefaultData(){
        getConfigManager().input("name", getPlayer().getName());
        getConfigManager().initSection("blocked-player");
        getConfigManager().initSection("templates");
        getConfigManager().initSection("mailbox.inbox");
        getConfigManager().initSection("mailbox.outbox");
        getConfigManager().saveConfig();
    }

    public void updateMailbox(){
        getMailbox().update();
    }

    //---------------------------

    public Mailbox getMailbox() {
        return mailbox;
    }

    public MailboxMenu getMailboxMenu() {
        return mailboxMenu;
    }

    public File getFile() {
        return file;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public OfflinePlayer getPlayer() {
        return player;
    }

    public List<Mail> getTemplates() {
        return templates;
    }
}
