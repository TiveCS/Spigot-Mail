package team.creativecode.diamail.event;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
		List<String> aliases = new ArrayList<String>(plugin.getConfig().getConfigurationSection("command-aliases").getKeys(false));
		for (String a : aliases) {
			if (cmd.equalsIgnoreCase(a)) {
				event.setCancelled(true);
				event.setMessage(plugin.getConfig().getString("command-aliases." + a));
				p.performCommand(event.getMessage());
				break;
			}
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
			Inventory inv = event.getInventory();
			if (inv.getTitle().equalsIgnoreCase(MailManager.mailInfoTitle)) {
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
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("prefix") + " Leaving mail send mode.."));
				return;
			}else if (event.getMessage().equalsIgnoreCase("Set-Item")) {
				ms.setItem(p);
			}
			else if (event.getMessage().equalsIgnoreCase("Done")) {
				if ((ms.hasMessage() || ms.hasItem()) && (ms.hasTarget())) {
					MailManager.sendMail(ms.getPlayer(), ms.getTarget(), ms.getMessage(), ms.getItem());
					p.sendMessage(" ");
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("prefix") + " Sending the mail..."));
					MailSend.reg.remove(p);
					return;
				}else {
					p.sendMessage(" ");
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("prefix") + " Could not send the mail! some required field is empty!"));
					String target = "None";
					String item = "";
					if (ms.hasTarget()) {
						target = ms.getTarget().getName();
					}
					if (ms.hasItem()) {
						item = ms.getItem().getType().toString() + "(" + ms.getItem().getAmount() + "x)";
					}
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("prefix") + " &6Target &e" + target));
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("prefix") + " &8Item &7[" + item + "]"));
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("prefix") + " &3Message &b" + ms.getMessage().size() + " line(s)"));
					return;
				}
			}
			else {
				if (ms.hasTarget() == false) {
					try {
						ms.setTarget(Bukkit.getPlayer(event.getMessage()));
						p.sendMessage(" ");
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("prefix") + " &6Target &e" + ms.getTarget().getName()));
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("prefix") + " &fYou can add your message now.."));
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("prefix") + " &fType the message on chat.."));
					}catch(Exception e) {
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("prefix") + " &cPlayer not found!"));
					}
				}else {
					ms.addMessage(event.getMessage());
				}
			}
			return;
		}
	}
	
}
