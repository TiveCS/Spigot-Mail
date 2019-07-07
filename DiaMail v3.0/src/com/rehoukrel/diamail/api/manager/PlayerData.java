package com.rehoukrel.diamail.api.manager;

import com.rehoukrel.diamail.DiaMail;
import com.rehoukrel.diamail.menu.MailboxMenu;
import com.rehoukrel.diamail.utils.ConfigManager;
import org.bukkit.OfflinePlayer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlayerData {

    public static DiaMail plugin = DiaMail.getPlugin(DiaMail.class);
    public static File folder = new File(plugin.getDataFolder(), "PlayerData");
    public static HashMap<OfflinePlayer, PlayerData> listedData = new HashMap<>();

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
        this.mailbox = new Mailbox(this);

        //this.mailboxMenu = new MailboxMenu(plugin, mailbox, this);

        listedData.put(player, this);
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
