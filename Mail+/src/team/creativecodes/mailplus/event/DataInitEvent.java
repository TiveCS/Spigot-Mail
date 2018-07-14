package team.creativecodes.mailplus.event;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import team.creativecodes.mailplus.ConfigManager;
import team.creativecodes.mailplus.Mail;
import team.creativecodes.mailplus.menumanager.MailboxMenu;
import team.creativecodes.mailplus.util.DataManager;
import team.creativecodes.mailplus.util.TextJson;

public class DataInitEvent implements Listener {
	
	Mail plugin = Mail.getPlugin(Mail.class);
	
	public static HashMap<Player, String> mailmode = new HashMap<Player, String>();
	public static HashMap<Player, OfflinePlayer> sendmail = new HashMap<Player, OfflinePlayer>();

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player p = event.getPlayer();
		ConfigManager.createPlayerData(p.getUniqueId().toString());
		if (!DataManager.mailboxData.containsKey(p)) {
			DataManager.initMailboxData(p);
		}
		try {
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("prefix") + " &aYou have &b" + DataManager.mailboxData.get(p).size() + " &amails"));
		}catch(Exception e) {}
	}
	
	@EventHandler
	public void onInvClose(InventoryCloseEvent event) {
		Player p = (Player) event.getPlayer();
		
		if (MailboxMenu.inv.containsKey(p)) {
			MailboxMenu.inv.remove(p);
		}
	}
	
	@EventHandler
	public void onInvClick(InventoryClickEvent event) {
		Player p = (Player) event.getWhoClicked();
		try {
			if (MailboxMenu.inv.get(p).equals(event.getClickedInventory())) {
				event.setCancelled(true);
				MailboxMenu.menuAction(p, MailboxMenu.targets.get(p), event.getSlot());
			}
		}catch(Exception e) {}
	}
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		Player p = event.getPlayer();
		String failedmsg = plugin.getConfig().getString("messages.mailbox.sendmessage-failed.syntax-message");
		String failedmsgdesc = plugin.getConfig().getString("messages.mailbox.sendmessage-failed.description-message");
		if (mailmode.containsKey(p)) {
			event.setCancelled(true);
			if (event.getMessage().equalsIgnoreCase("cancel") || event.getMessage().equalsIgnoreCase("exit")) {
				String sm = plugin.getConfig().getString("messages.mailbox.sendmessage-cancel.syntax-message")
				, dm = plugin.getConfig().getString("messages.mailbox.sendmessage-cancel.description-message");
				TextJson.sendHoverJson(p, TextJson.placeholderReplace(p, sendmail.get(p), sm), TextJson.placeholderReplace(p, sendmail.get(p), dm));
			}
			else if (mailmode.get(p).equalsIgnoreCase("msg")) {
				DataManager.sendMail(p, sendmail.get(p), event.getMessage());
			}else if (mailmode.get(p).equalsIgnoreCase("item")) {
				if (!p.getInventory().getItemInMainHand().toString().equals("AIR")) {
					DataManager.sendItemMail(p, sendmail.get(p), event.getMessage(), p.getInventory().getItemInMainHand());
					p.getInventory().remove(p.getInventory().getItemInMainHand());
				}else {
					TextJson.sendHoverJson(p, failedmsg, failedmsgdesc);
				}
			}
			mailmode.remove(p);
			sendmail.remove(p);
		}
	}
}
