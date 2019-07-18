package com.rehoukrel.diamail.api.events;

import com.rehoukrel.diamail.api.manager.Mail;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;
import java.util.List;

public class MailSendEvent extends Event implements Cancellable {
    private static final HandlerList HANDLER = new HandlerList();

    private boolean isCancelled = false;
    private Mail mail;
    private CommandSender sender;
    private List<OfflinePlayer> receiver = new ArrayList<>();

    public MailSendEvent(CommandSender sender, List<OfflinePlayer> receiver, Mail mail){
        this.mail = mail;
        this.sender = sender;
        this.receiver = receiver;
    }

    public CommandSender getSender() {
        return sender;
    }

    public List<OfflinePlayer> getReceiver() {
        return receiver;
    }

    public Mail getMail() {
        return mail;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.isCancelled = b;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER;
    }

    public static HandlerList getHandlerList() {
        return HANDLER;
    }
}
