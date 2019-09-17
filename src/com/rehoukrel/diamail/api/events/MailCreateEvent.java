package com.rehoukrel.diamail.api.events;

import com.rehoukrel.diamail.api.manager.Mail;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MailCreateEvent extends Event implements Cancellable {

    private static final HandlerList HANDLER = new HandlerList();

    private boolean isCancelled = false;
    private Mail mail;

    public MailCreateEvent(Mail mail){
        this.mail = mail;
    }

    public void setMail(Mail mail) {
        this.mail = mail;
    }

    public Mail getMail() {
        return mail;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER;
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
