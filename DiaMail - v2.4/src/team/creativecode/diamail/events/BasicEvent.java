package team.creativecode.diamail.events;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import team.creativecode.diamail.Main;
import team.creativecode.diamail.manager.Menu;
import team.creativecode.diamail.manager.PlayerData;
import team.creativecode.diamail.manager.mail.Mail;
import team.creativecode.diamail.manager.menu.MailShow;
import team.creativecode.diamail.manager.menu.Mailbox;
import team.creativecode.diamail.utils.DataConverter;

public class BasicEvent implements Listener {

	private Main plugin = Main.getPlugin(Main.class);
	File folder = new File(plugin.getDataFolder() + "/PlayerData");
	boolean hasUpdate = false;
	
	public static List<Player> players = new ArrayList<Player>();
	
	@EventHandler
	public void quit(PlayerQuitEvent event) {
		players.remove(event.getPlayer());
	}
	
	@EventHandler
	public void join(PlayerJoinEvent event) {
		Player p = event.getPlayer();
		players.add(p);
		PlayerData pd = new PlayerData(event.getPlayer());
		pd.checkMailboxScheduled(p);
		
	}
	
	@EventHandler
	public void invclick(InventoryClickEvent event) {
		Player p = (Player) event.getWhoClicked();
		if (event.getClickedInventory() == null) {return;}
		if (!event.getClick().name().startsWith("WINDOW_BORDER")) {
			if (Menu.singleMenu.containsKey(p)) {
				Menu m = Menu.singleMenu.get(p);
				if (event.getClickedInventory().equals(m.getMenu())) {
					event.setCancelled(true);
					PlayerData pd = new PlayerData(p);
					if (Boolean.parseBoolean(pd.getPlayerSetting().getSettings().get("inventory-interact-sound").toString())) {
						DataConverter.playSoundByString(p.getLocation(), plugin.getConfig().getString("settings.inventory-interact-sound"));
					}
					if (m instanceof MailShow) {
						m.action(p, event.getSlot(), event.getClick(), m);
					}else if (m instanceof Mailbox) {
						m.action(p, event.getSlot(), event.getClick(), m);
					}
					else {
						m.action(p, event.getSlot(), event.getClick());
					}
				}
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void chat(AsyncPlayerChatEvent event) {

		Player p = event.getPlayer();
		
		if (Mail.sending.containsKey(p)) {
			Mail m = Mail.sending.get(p);
			String msg = event.getMessage();
			event.setCancelled(true);
			
			if (msg.equalsIgnoreCase("SENDALL")) {
				if (p.hasPermission("diamail.access.sendall") || p.isOp()) {
					m.setSendAll(!m.isSendAll());
				}else {
					m.getPlayerData().getLanguage().sendMessage(p, m.getPlayerData().getLanguage().getMessages().get("alert.no-permission"));
				}
			}else if (msg.equalsIgnoreCase("SETITEM")) {
				if (p.hasPermission("diamail.access.sendmultiple.item")) {
					m.setItem(p.getInventory().getItemInMainHand());
				}else {
					m.getPlayerData().getLanguage().sendMessage(p, m.getPlayerData().getLanguage().getMessages().get("alert.no-permission"));
				}
			}else if (msg.equalsIgnoreCase("SEND")) {
				m.send();
				return;
			}else if (msg.equalsIgnoreCase("EXIT")) {
				m.delete(true);
				return;
			}else {
				try {
					if (m.isSendAll()) {
						m.addMessage(msg);
					}
					else if (m.getReceiver() != null || !m.getMultipleReceiver().isEmpty()) {
						if (m.getReceiver() != null || !m.getMultipleReceiver().isEmpty()) {
							m.addMessage(msg);
						}else {
							m.setReceiver(null);
						}
					}else if (m.getReceiver() == null || m.getMultipleReceiver().isEmpty()) {
						msg = msg.replace(" ", "");
						String[] split = msg.split("[,]");
						if (split.length == 1) {
							m.setReceiver(Bukkit.getOfflinePlayer(msg));
						}else if (split.length > 1) {
							if (p.hasPermission("diamail.access.sendmultiple")) {
								List<OfflinePlayer> l = new ArrayList<OfflinePlayer>();
								for (String sp : split) {	
									l.add(Bukkit.getOfflinePlayer(sp));
								}
								m.addMultiReceiver(l);
							}else {
								m.setReceiver(Bukkit.getOfflinePlayer(split[0]));
							}
						}
					}
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
			if (m.isSendAll() && !m.getItem().getType().equals(Material.AIR)) {
				if (!p.hasPermission("diamail.access.sendall") || !p.isOp()) {
					p.getInventory().addItem(m.getItem());
					m.setItem(new ItemStack(Material.AIR));
					m.getPlayerData().getLanguage().sendMessage(p, m.getPlayerData().getLanguage().getMessages().get("alert.no-permission"));
				}
			}
			
			m.showButton();
		}
		
	}

}
