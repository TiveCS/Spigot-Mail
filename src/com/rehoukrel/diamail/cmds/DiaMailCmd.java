package com.rehoukrel.diamail.cmds;

import com.rehoukrel.diamail.DiaMail;
import com.rehoukrel.diamail.api.manager.Mail;
import com.rehoukrel.diamail.api.manager.PlayerData;
import com.rehoukrel.diamail.events.BasicEvent;
import net.wesjd.anvilgui.version.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class DiaMailCmd implements CommandExecutor {

    DiaMail plugin = DiaMail.getPlugin(DiaMail.class);

    public static HashMap<Player, HashMap<Mail, AnvilGUI>> mailSend = new HashMap<>();

    @SuppressWarnings("deprecation")
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (command.getName().equalsIgnoreCase("diamail")){
            if (commandSender instanceof Player){
                Player p = (Player) commandSender;
                if (strings.length == 0){
                    boolean isNew = !PlayerData.listedData.containsKey(p);
                    PlayerData pd = PlayerData.getPlayerData(p);
                    if (!isNew){
                        pd.setHasUpdate(true);
                    }
                    pd.updateMailbox();
                    pd.getMailboxMenu().open(p);
                    return true;
                }else if (strings.length == 1){
                    if (strings[0].equalsIgnoreCase("reload")){
                        if (commandSender.hasPermission("diamail.access.admin")) {
                            plugin.loadConfig();
                            plugin.loadLanguage();
                            if (PlayerData.useMySQL) {
                                plugin.connectSQL();
                            }
                            plugin.loadMenu();
                            PlayerData.listedData.clear();
                            for (String st :  BasicEvent.enUS.getConfig().getStringList("system.reload")) {
                                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', st));
                            }
                            return true;
                        }
                    }
                }
                else if (strings.length == 2){
                    PlayerData pd = PlayerData.getPlayerData(p);
                    if (strings[0].equalsIgnoreCase("open")){
                        if (commandSender.hasPermission("diamail.access.admin")) {
                            OfflinePlayer t = Bukkit.getOfflinePlayer(strings[1]);
                            PlayerData pdt = PlayerData.getPlayerData(t);
                            pdt.updateMailbox();
                            pdt.getMailboxMenu().open(p);
                            return true;
                        }
                    }
                    if (strings[0].equalsIgnoreCase("check")){
                        OfflinePlayer t = Bukkit.getOfflinePlayer(strings[1]);
                        PlayerData pdt = PlayerData.getPlayerData(t);
                        commandSender.sendMessage("has update : " + pdt.hasUpdate());
                        return true;
                    }
                    if (strings[0].equalsIgnoreCase("send")) {
                        OfflinePlayer target = Bukkit.getOfflinePlayer(strings[1]);
                        Mail m = new Mail(p);
                        m.getReceiver().add(target);
                        m.getEditor().open(p);
                        return true;
                    }
                }
                return true;
            }
        }

        return false;
    }
}
