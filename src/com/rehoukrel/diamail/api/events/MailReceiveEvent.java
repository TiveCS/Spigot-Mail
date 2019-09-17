package com.rehoukrel.diamail.api.events;

import com.rehoukrel.diamail.api.manager.Mail;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MailReceiveEvent extends Event implements Cancellable {

    private static final HandlerList HANDLER = new HandlerList();

    private boolean isCancelled = false, isBlocked = false;
    private Mail mail;
    private OfflinePlayer offlinePlayer;

    public MailReceiveEvent(OfflinePlayer receiver, Mail mail){
        this.mail = mail;
        this.offlinePlayer = receiver;
    }

    public OfflinePlayer getReceiver(){
        return offlinePlayer;
    }

    public Mail getMail() {
        return mail;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.isCancelled = b;
    }

    public static HandlerList getHandlerList() {
        return HANDLER;
    }
}
