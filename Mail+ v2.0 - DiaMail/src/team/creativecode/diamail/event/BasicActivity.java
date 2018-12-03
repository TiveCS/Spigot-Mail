package team.creativecode.diamail.event;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

import team.creativecode.diamail.ConfigManager;
import team.creativecode.diamail.Main;
import team.creativecode.diamail.activity.MailInfo;
import team.creativecode.diamail.activity.MailManager;
import team.creativecode.diamail.activity.MailSend;
import team.creativecode.diamail.activity.Mailbox;
import team.creativecode.diamail.manager.DataManager;
import team.creativecode.diamail.manager.MessageManager;
import team.creativecode.diamail.manager.MessageManager.MessageType;
import team.creativecode.diamail.manager.PlayerSetting;

public class BasicActivity implements Listener {
	
	private Main plugin = Main.getPlugin(Main.class);
	private final String path = plugin.getDataFolder().toString() + "/PlayerData";
	
	public static List<Player> onlinePlayer = new ArrayList<Player>();
	
	@EventHandler
	public void onLeave(PlayerQuitEvent event) {
		onlinePlayer.remove(event.getPlayer());
	}
	
	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent event) {
		Player p = event.getPlayer();
		String cmd = event.getMessage().substring(1);
		String[] split = cmd.split(" ");
		String finalCmd = "";
		List<String> aliases = new ArrayList<String>(plugin.getConfig().getConfigurationSection("command-aliases").getKeys(false));
		for (String sp : split) {
			for (String a : aliases) {
				if (sp.equalsIgnoreCase(a)) {
					sp = sp.replace(sp, plugin.getConfig().getString("command-aliases." + a));
				}
			}
			finalCmd = finalCmd.equals("") ? sp : finalCmd + " " + sp;
		}
		if (!finalCmd.equals("")) {
			event.setCancelled(true);
			p.performCommand(finalCmd);
		}
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		try {
			File file = new File(path, event.getPlayer().getUniqueId().toString() + ".yml");
			Player p = event.getPlayer();
			onlinePlayer.add(p);
			if (!file.exists()) {
				ConfigManager.createFile(path, p.getUniqueId().toString() + ".yml");
			}
			DataManager.initSetting(p);
			ConfigManager.inputData(file, "player-name", p.getName());
			MailManager.checkMailScheduled(p);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		try {
			Player p = (Player) event.getWhoClicked();
			Mailbox mb = Mailbox.mailbox.get(p);
			MailSend ms = mb.getMailSend();
			PlayerSetting ps = mb.getPlayerSettings();
			Inventory inv = event.getClickedInventory();
			if (inv.equals(MailManager.mailInfo.get(p))) {
				event.setCancelled(true);
				MailInfo mi = new MailInfo(p, event.getSlot(), MailManager.mailInfoMail.get(p));
				mi.action();
				return;
			}
			if (inv.equals(ps.getInventory())) {
				event.setCancelled(true);
				ps.action(p, event.getSlot(), event.getClick());
				return;
			}
			if (inv.equals(ms.getInventory())) {
				event.setCancelled(true);
				ms.action(p, event.getSlot());
				return;
			}
			if (inv.equals(mb.getInventory())) {
				event.setCancelled(true);
				mb.action(p, event.getSlot());
				return;
			}
		}catch(Exception e) {}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		Player p = event.getPlayer();
		
		if (MailManager.mailSendTarget.containsKey(p)) {
			event.setCancelled(true);
			MailManager.sendMail(p, MailManager.mailSendTarget.get(p), event.getMessage(), null);
			MailManager.mailSendTarget.remove(p);
			return;
		}else if (MailSend.reg.containsKey(p)) {
			event.setCancelled(true);
			MailSend ms = MailSend.reg.get(p);
			if (event.getMessage().equalsIgnoreCase("Exit")) {
				MailSend.reg.remove(p);
				MessageManager.send(p, MessageType.PRE_SEND_MAIL_LEAVE);
				return;
			}else if (event.getMessage().equalsIgnoreCase("Set-Item")) {
				ms.setItem(p);
			}else if (event.getMessage().equalsIgnoreCase("GUI")) {
				try {
					ms.initMenu();
					p.openInventory(ms.getInventory());
				}catch(Exception e) {}
			}else if (event.getMessage().equalsIgnoreCase("Check-Mail")) {
				ms.info();
			}else if (event.getMessage().equalsIgnoreCase("Dettach-Item") || event.getMessage().equalsIgnoreCase("Deattach-Item")) {
				ms.returnItem();
			}
			else if (event.getMessage().equalsIgnoreCase("Done")) {
				if ((ms.hasMessage() || ms.hasItem())) {
					ms.sendMail();
					MailSend.reg.remove(p);
					return;
				}else {
					ms.info();
					MessageManager.send(p, MessageType.SEND_MAIL_FAILED);
					return;
				}
			}
			else {
				if (ms.hasTarget() == false && ms.isSendall() == false) {
					try {
						OfflinePlayer op = null;
						try {
							op = Bukkit.getPlayer(event.getMessage());
							if (op.equals(null)) {
								op = Bukkit.getOfflinePlayer(event.getMessage());
							}
						}catch(Exception e) {op = Bukkit.getOfflinePlayer(event.getMessage());}
						ms.setTarget(op);
					}catch(Exception e) {
						MessageManager.send(p, MessageType.SET_TARGET_NOT_FOUND);
					}
				}else {
					try {
						ms.initMenu();
					}catch(Exception e) {}
					ms.addMessage(event.getMessage());
				}
			}
			return;
		}
	}
	
}
