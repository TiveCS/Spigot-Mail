package com.rehoukrel.diamail.events;

import com.rehoukrel.diamail.DiaMail;
import com.rehoukrel.diamail.api.events.MailReceiveEvent;
import com.rehoukrel.diamail.api.events.MailSendEvent;
import com.rehoukrel.diamail.api.manager.PlayerData;
import com.rehoukrel.diamail.utils.ConfigManager;
import com.rehoukrel.diamail.utils.DataConverter;
import com.rehoukrel.diamail.utils.language.Placeholder;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BasicEvent implements Listener {

    public static DiaMail plugin = DiaMail.getPlugin(DiaMail.class);
    public static File enUSFile = new File(plugin.getDataFolder() + "/Language", "en-US.yml");

    public static ConfigManager enUS = new ConfigManager(enUSFile);

    @EventHandler
    public void onMailReceive(MailReceiveEvent event){
        if (event.getReceiver().isOnline()) {
            List<String> receivemsg = new ArrayList<String>();
            ConfigManager lang = enUS;
            receivemsg = lang.getConfig().getStringList("events.mail-receive");
            Placeholder plc = new Placeholder();
            PlayerData pd = new PlayerData(event.getReceiver());

            plc.addReplacer("prefix", lang.getConfig().getString("placeholders.prefix"));
            plc.addReplacer("mail_sender", event.getMail().getSender().getName());

            StringBuilder rec = new StringBuilder();
            int count = 1; String comma = "";
            for (OfflinePlayer r : event.getMail().getReceiver()) {
                plc.addReplacer("mail_receiver_" + count, r.getName());
                rec.append(r.getName()).append(comma);
                count++;
                comma = ", ";
            }
            plc.addReplacer("mail_receiver", rec.toString());

            StringBuilder m = new StringBuilder();
            comma = ""; count = 1;
            for (String msg : event.getMail().getMessages()){
                plc.addReplacer("mail_message_" + count, msg);
                m.append(msg).append(comma);
                comma = " ";
                count++;
            }
            plc.addReplacer("mail_messages", m.toString());

            DataConverter.playSoundByString(pd.getPlayer().getPlayer().getLocation(), plugin.getConfig().getString("effect.sound.mail-receive"));

            for (String s : receivemsg) {
                event.getReceiver().getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', plc.use(s)));
            }
        }
    }

    @EventHandler
    public void onMailSend(MailSendEvent event){
        List<String> sendmsg = new ArrayList<String>();
        Placeholder plc = new Placeholder();
        ConfigManager lang = enUS;

        if (event.getSender() instanceof OfflinePlayer) {
            PlayerData pd = PlayerData.getPlayerData((OfflinePlayer) event.getSender());
            if (pd.getPlayer().isOnline()) {
                DataConverter.playSoundByString(pd.getPlayer().getPlayer().getLocation(), plugin.getConfig().getString("effect.sound.mail-send"));
            }
        }

        plc.addReplacer("prefix", lang.getConfig().getString("placeholders.prefix"));
        plc.addReplacer("mail_sender", event.getSender().getName());

        StringBuilder rec = new StringBuilder();
        int count = 1; String comma = "";
        for (OfflinePlayer r : event.getReceiver()) {
            plc.addReplacer("mail_receiver_" + count, r.getName());
            rec.append(r.getName()).append(comma);
            count++;
            comma = ", ";
        }
        plc.addReplacer("mail_receiver", rec.toString());

        StringBuilder m = new StringBuilder();
        comma = ""; count = 1;
        for (String msg : event.getMail().getMessages()){
            plc.addReplacer("mail_message_" + count, msg);
            m.append(msg).append(comma);
            comma = " ";
            count++;
        }
        plc.addReplacer("mail_messages", m.toString());

        sendmsg = lang.getConfig().getStringList("events.mail-send");

        for (String s : sendmsg){
            event.getSender().sendMessage(ChatColor.translateAlternateColorCodes('&', plc.use(s)));
        }


    }

}
