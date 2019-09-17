package com.rehoukrel.diamail.cmds;

import com.rehoukrel.diamail.DiaMail;
import com.rehoukrel.diamail.api.manager.Mail;
import com.rehoukrel.diamail.api.manager.PlayerData;
import net.wesjd.anvilgui.version.AnvilGUI;
import org.bukkit.Bukkit;
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
                        pd.updateMailbox();
                    }
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
