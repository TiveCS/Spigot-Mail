package team.creativecode.diamail.manager.event.mail;

import org.bukkit.OfflinePlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import team.creativecode.diamail.manager.PlayerData;
import team.creativecode.diamail.manager.mail.Mail;

public class MailReceiveEvent extends Event implements Cancellable {
	
	private boolean isCancelled;
	private static final HandlerList handler = new HandlerList();
	private Mail mail;
	private OfflinePlayer receiver;
	private PlayerData receiverData;
	
	public MailReceiveEvent(Mail mail, OfflinePlayer receiver, PlayerData receiverData) {
		this.mail = mail;
		this.receiver = receiver;
		this.receiverData = receiverData;
		this.isCancelled = false;
	}
	
	public PlayerData getReceiverData() {
		return receiverData;
	}
	
	public OfflinePlayer getReceiver() {
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
		this.isCancelled = arg0;
	}

	@Override
	public HandlerList getHandlers() {
		return handler;
	}
	
}
