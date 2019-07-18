package com.rehoukrel.diamail.cmds;

import com.rehoukrel.diamail.DiaMail;
import com.rehoukrel.diamail.api.manager.Mail;
import com.rehoukrel.diamail.api.manager.PlayerData;
import com.rehoukrel.diamail.utils.nms.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
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
                    PlayerData pd = PlayerData.getPlayerData(p);
                    pd.getMailboxMenu().open(p);
                    return true;
                }else if (strings.length == 2){
                    PlayerData pd = PlayerData.getPlayerData(p);
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
