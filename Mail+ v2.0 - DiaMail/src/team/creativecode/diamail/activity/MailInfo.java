package team.creativecode.diamail.activity;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import team.creativecode.diamail.Main;
import team.creativecode.diamail.activity.MailManager.MailType;

public class MailInfo {
	
	private Main plugin = Main.getPlugin(Main.class);
	
	Player executor;
	int slot;
	Mail m;
	
	public MailInfo(Player p, int slot, Mail mm) {
		this.executor = p;
		this.slot = slot;
		this.m = mm;
	}
	
	public Mail getMail() {
		return this.m;
	}
	
	public void action() {
		Mailbox mb = Mailbox.mailbox.get(this.executor);
		if (slot == 3*9 - 5) {
			this.executor.openInventory(mb.getInventory());
		}
		else if (slot == 2*9 - 3) {
			this.executor.closeInventory();
			try {
				if (this.m.getMailType().equals(MailType.INBOX)) {
					MailManager.sendMail(this.executor, this.m.getSender());
				}else {
					MailManager.sendMail(this.executor, this.m.getTarget());
				}
			}catch(Exception e) {
				this.executor.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("prefix") + " &cSorry! error occured inside the system.."));
				e.printStackTrace();
			}
		}
		else if (slot == 2*9 - 7) {
			this.executor.closeInventory();
			String msg = "";
			this.m.setMessage(this.m.getPlayerMail().getMessage(this.m.getMailType(), this.m.getUniqueId().toString()));
			for (String s : this.m.getMessage()) {
				if (msg.equals("")) {
					msg = "    " + s;
				}else {
					msg = msg + " " + s;
				}
			}
			msg = ChatColor.translateAlternateColorCodes('&', msg);
			this.executor.sendMessage(" ");
			this.executor.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Mail UUID: &8[" + this.m.getUniqueId().toString() + "]"));
			this.executor.sendMessage(" ");
			this.executor.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f&o" + msg));
			this.executor.sendMessage(" ");
			if (Boolean.parseBoolean(this.m.getPlayerMail().getSettings().get("delete-mail-after-read").toString())) {
				MailManager.deleteMail(this.executor, this.m.getPlayerMail().getPlayer(), this.m.getPath());
			}
		}
		else if (slot == 2*9 - 1) {
			MailManager.deleteMail(this.executor, this.m.getPlayerMail().getPlayer(), this.m.getPath());
			Mailbox.mailbox.remove(this.executor);
			Mailbox.mailbox.put(this.executor, new Mailbox(this.executor, mb.getPage(), mb.getMailboxType()));
			Mailbox mbox = Mailbox.mailbox.get(this.executor);
			mbox.getPlayerMail().setMailboxData(mbox);
			mbox.initCurrentMailbox();
			mbox.createMenu();
			this.executor.openInventory(mbox.getInventory());
		}else if (slot == 2*9 - 5) {
			try {
				if (!this.m.getItem().equals(null)) {
					MailManager.giveMailItem(this.executor, this.m.getPlayerMail().getPlayer(), this.m.getUniqueId());
					Mailbox.mailbox.remove(this.executor);
					Mailbox.mailbox.put(this.executor, new Mailbox(this.executor, 1, m.getMailType()));
					this.executor.openInventory(Mailbox.mailbox.get(this.executor).getInventory());
				}
			}catch(Exception e) {}
		}
	}

}
