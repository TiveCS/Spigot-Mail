package com.rehoukrel.diamail.api.manager;

import com.rehoukrel.diamail.DiaMail;
import com.rehoukrel.diamail.utils.sql.MySQLManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.sql.ResultSet;
import java.util.*;

public class Mailbox {

    private List<Mail> inbox = new LinkedList<>(), outbox = new LinkedList<>();
    private PlayerData playerData;

    private DiaMail plugin = DiaMail.getPlugin(DiaMail.class);

    public Mailbox(PlayerData playerData){
        this.playerData = playerData;
        update();
    }

    public void update(){
        boolean useSql = PlayerData.useMySQL;
        if (getPlayerData() == null){return;}
        try {
            if (useSql){
                MySQLManager mysql = DiaMail.mysql;
                ResultSet result = mysql.getResultSet(DiaMail.table_inbox, "*", "receiver_uuid='" + getPlayerData().getPlayer().getUniqueId().toString() +"'");
                ResultSet resultOutbox = mysql.getResultSet(DiaMail.table_outbox, "*", "sender_uuid='" + getPlayerData().getPlayer().getUniqueId().toString() +"'");
                while (result.next()){
                    Mail mail = new Mail();
                    String sender = result.getString("sender");
                    OfflinePlayer opsender;
                    mail.setType(MailType.valueOf(result.getString("type").toUpperCase()));
                    mail.setUniqueId(UUID.fromString(result.getString("uuid")));
                    try{
                        mail.setReceiver(Arrays.asList(Bukkit.getOfflinePlayer(UUID.fromString(result.getString("receiver_uuid")))));
                    }catch (Exception e){
                        mail.setReceiver(new ArrayList<>());
                        plugin.getLogger().warning(getPlayerData().getPlayer().getName() + " cannot load mail uuid " + mail.getUniqueId().toString() + " [receiver load failed]");
                        return;
                    }
                    if (mail.getType().equals(MailType.CONSOLE_SEND)) {
                        mail.setSender(plugin.getServer().getConsoleSender());
                    }else {
                        try {
                            opsender = Bukkit.getOfflinePlayer(UUID.fromString(sender));
                            mail.setSender((CommandSender) opsender);
                        } catch (Exception e) {
                            mail.setSender(null);
                            plugin.getLogger().warning(getPlayerData().getPlayer().getName() + " cannot load mail uuid " + mail.getUniqueId().toString() + " [sender load failed]");
                            return;
                        }
                    }
                    mail.setMessages(new ArrayList<>());
                    try {
                        String[] msgSplit = result.getString("message").split(Mail.separator);
                        for (String s : msgSplit) {
                            mail.getMessages().add(ChatColor.translateAlternateColorCodes('&', s));
                        }
                    }catch (Exception ignored){}
                    getInbox().add(mail);
                }
                while (resultOutbox.next()){
                    Mail mail = new Mail();
                    String sender = resultOutbox.getString("sender_uuid");
                    OfflinePlayer opsender;
                    mail.setType(MailType.valueOf(resultOutbox.getString("type").toUpperCase()));
                    mail.setUniqueId(UUID.fromString(resultOutbox.getString("uuid")));
                    if (mail.getType().equals(MailType.CONSOLE_SEND)) {
                        mail.setSender(plugin.getServer().getConsoleSender());
                    }else {
                        try {
                            opsender = Bukkit.getOfflinePlayer(UUID.fromString(sender));
                            mail.setSender((CommandSender) opsender);
                        } catch (Exception e) {
                            mail.setSender(null);
                            plugin.getLogger().warning(getPlayerData().getPlayer().getName() + " cannot load mail uuid " + mail.getUniqueId().toString() + " [sender load failed]");
                            return;
                        }
                    }
                    mail.setMessages(new ArrayList<>());
                    try {
                        String[] msgSplit = resultOutbox.getString("message").split(Mail.separator);
                        for (String s : msgSplit) {
                            mail.getMessages().add(ChatColor.translateAlternateColorCodes('&', s));
                        }
                    }catch (Exception ignored){}
                    getOutbox().add(mail);
                }
            }
            else {
                getInbox().clear();
                for (String uuid : getPlayerData().getConfigManager().getConfig().getConfigurationSection("mailbox.inbox").getKeys(false)) {
                    Mail m = new Mail(getPlayerData(), uuid, true);
                    getInbox().add(m);
                }

                getOutbox().clear();
                for (String uuid : getPlayerData().getConfigManager().getConfig().getConfigurationSection("mailbox.outbox").getKeys(false)) {
                    Mail m = new Mail(getPlayerData(), uuid, false);
                    getOutbox().add(m);
                }
            }
        } catch (Exception ignored){}
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
