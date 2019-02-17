package team.creativecode.diamail.events;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import team.creativecode.diamail.Main;
import team.creativecode.diamail.manager.Menu;
import team.creativecode.diamail.manager.PlayerData;
import team.creativecode.diamail.manager.mail.Mail;
import team.creativecode.diamail.manager.mail.Mail.MailMode;

public class BasicEvent implements Listener {

	Main plugin = Main.getPlugin(Main.class);
	File folder = new File(plugin.getDataFolder() + "/PlayerData");
	
	@EventHandler
	public void join(PlayerJoinEvent event) {
		new PlayerData(event.getPlayer());
	}
	
	@EventHandler
	public void invclick(InventoryClickEvent event) {
		Player p = (Player) event.getWhoClicked();
		if (Menu.singleMenu.containsKey(p)) {
			Menu m = Menu.singleMenu.get(p);
			if (event.getClickedInventory().equals(m.getMenu())) {
				event.setCancelled(true);
				m.action(p, event.getSlot());
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
			switch(m.getMailMode()) {
			case ITEM:
				if (msg.equalsIgnoreCase("none")) {
					m.setItem(new ItemStack(Material.AIR));
				}else if (msg.equalsIgnoreCase("helmet")) {
					m.setItem(p.getInventory().getHelmet());
				}else if (msg.equalsIgnoreCase("chestplate")) {
					m.setItem(p.getInventory().getChestplate());
				}else if (msg.equalsIgnoreCase("leggings")) {
					m.setItem(p.getInventory().getLeggings());
				}else if (msg.equalsIgnoreCase("boots")) {
					m.setItem(p.getInventory().getBoots());
				}else if (msg.equalsIgnoreCase("offhand")) {
					m.setItem(p.getInventory().getItemInOffHand());
				}else {
					m.setItem(p.getInventory().getItemInMainHand());
				}
				p.sendMessage("Item has been set to " + m.getItem().getType().toString());
				p.sendMessage("You can add message now");
				m.setMode(MailMode.MESSAGE);
				break;
			case MESSAGE:
				m.addMessage(ChatColor.translateAlternateColorCodes('&', msg));
				p.sendMessage("Added new message");
				break;
			case RECEIVER:
				try {
					OfflinePlayer target = Bukkit.getOfflinePlayer(msg);
					File[] files = folder.listFiles();
					for (File f : files) {
						if (f.getName().startsWith(target.getUniqueId().toString())) {
							m.setTarget(target);
							p.sendMessage("Target " + m.getReceiver().getName());
							m.setMode(MailMode.ITEM);
							p.sendMessage("Switching to mode set item");
							break;
						}else {
							continue;
						}
					}
				}catch(Exception e) {
					p.sendMessage("Player is not found");
				}
				break;
			case SENDALL:
				m.setSendAll(Boolean.parseBoolean(msg));
				p.sendMessage("Mail sendall mode is " + m.isSendAll());
				p.sendMessage("Switching to set receiver");
				m.setMode(MailMode.RECEIVER);
				break;
			}
		}
		
	}

}
