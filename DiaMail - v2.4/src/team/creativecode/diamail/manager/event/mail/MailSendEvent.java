package team.creativecode.diamail.manager.event.mail;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import team.creativecode.diamail.manager.PlayerData;
import team.creativecode.diamail.manager.mail.Mail;

public class MailSendEvent extends Event implements Cancellable {

	private boolean isCancelled = false;
	private Mail mail;
	private OfflinePlayer sender;
	private boolean isSendall = false;
	private List<OfflinePlayer> receiver = new ArrayList<>();
	private static final HandlerList handler = new HandlerList();
	private PlayerData senderData;
	
	public MailSendEvent(Mail mail, OfflinePlayer sender, List<OfflinePlayer> receiver, boolean isSendall, PlayerData senderData) {
		if (isCancelled()) {
			return;
		}
		this.isSendall = isSendall;
		this.mail = mail;
		this.sender = sender;
		this.receiver = receiver;
		this.senderData = senderData;
	}
	
	public PlayerData getSenderData() {
		return senderData;
	}
	
	
	public boolean isSendall() {
		return isSendall;
	}
	
	public OfflinePlayer getSender() {
		return sender;
	}
	
	public List<OfflinePlayer> getReceiver(){
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
	public void setCancelled(boolean arg0) {
		isCancelled = arg0;
	}

	@Override
	public HandlerList getHandlers() {
		// TODO Auto-generated method stub
		return handler;
	}

}
