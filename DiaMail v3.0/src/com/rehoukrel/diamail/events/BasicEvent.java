package com.rehoukrel.diamail.events;

import com.rehoukrel.diamail.api.events.MailSendEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class BasicEvent implements Listener {

    @EventHandler
    public void onMailSend(MailSendEvent event){
        event.getSender().sendMessage("Mail successfully send to " + event.getReceiver().get(0).getUniqueId());
    }

}
