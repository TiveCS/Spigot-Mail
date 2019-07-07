package com.rehoukrel.diamail.api.manager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class Mailbox {

    private List<Mail> inbox = new LinkedList<>(), outbox = new LinkedList<>();
    private PlayerData playerData;

    public Mailbox(PlayerData playerData){
        this.playerData = playerData;
        update();
    }

    public void update(){
        if (getPlayerData() == null){return;}
        try {
            for (String uuid : getPlayerData().getConfigManager().getConfig().getConfigurationSection("mailbox.inbox").getKeys(false)) {
                Mail m = new Mail(getPlayerData(), uuid, true);
                getInbox().add(m);
            }

            for (String uuid : getPlayerData().getConfigManager().getConfig().getConfigurationSection("mailbox.outbox").getKeys(false)) {
                Mail m = new Mail(getPlayerData(), uuid, false);
                getOutbox().add(m);
            }
        }catch (Exception e){}
    }

    public PlayerData getPlayerData() {
        return playerData;
    }

    public List<Mail> getInbox() {
        return inbox;
    }

    public List<Mail> getOutbox() {
        return outbox;
    }
}
