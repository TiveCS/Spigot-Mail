package team.creativecodes.mailplus.cmd;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import team.creativecodes.mailplus.ConfigManager;
import team.creativecodes.mailplus.Mail;
import team.creativecodes.mailplus.event.DataInitEvent;
import team.creativecodes.mailplus.menumanager.MailboxMenu;
import team.creativecodes.mailplus.util.DataManager;
import team.creativecodes.mailplus.util.TextJson;

public class MailCmd implements CommandExecutor {

	Mail plugin = Mail.getPlugin(Mail.class);
	List<String> helplist;
	Set<String> csraw;

	public void showHelp(Player p, int page) {
		ConfigurationSection cshelp = plugin.getConfig().getConfigurationSection("messages.help");
		csraw = cshelp.getKeys(false);
		helplist = new ArrayList<String>(csraw);
		try {
			int helpSize = helplist.size();
			int maxpage = 0;

			while (helpSize - 6 > 0) {
				helpSize = helpSize - 6;
				maxpage++;
			}
			if (helpSize > 0) {
				maxpage++;
			}
			p.sendMessage(" ");
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3&lMail&b&l+ &7- &av"
					+ plugin.getDescription().getVersion() + " &8[&7Page &c" + page + "/" + maxpage + "&8]"));
			p.sendMessage(" ");
			for (int i = 0; i < 6; i++) {
				int a = i + ((page - 1) * 6);
				if (!(helplist.get(a).equals(null) || (helplist.get(a).length() == 0))) {
					String path = "messages.help." + helplist.get(a);
					if (helplist.get(a).equals("mailbox-message")) {
						TextJson.sendRunCommandJson(p, plugin.getConfig().getString(path + ".syntax-message"), "mail", plugin.getConfig().getString(path + ".description-message"));
					}else {
						TextJson.sendHoverJson(p, plugin.getConfig().getString(path + ".syntax-message"),
								plugin.getConfig().getString(path + ".description-message"));
					}
				}
			}
			p.sendMessage(" ");
		} catch (Exception e) {
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (sender instanceof Player) {
			if (cmd.getName().equalsIgnoreCase("mail")) {
				Player p = (Player) sender;
				UUID uuid = p.getUniqueId();
				if (!ConfigManager.hasMailData(uuid.toString())) {
					ConfigManager.createPlayerData(uuid.toString());
					ConfigManager.putData(uuid.toString(), "mailbox", "{}");
				}
				
				if (args.length == 0) {
					try {
						ConfigManager.putData(uuid.toString(), "player", p.getName());
						if (!DataManager.mailboxData.containsKey(p)) {
							DataManager.initMailboxData(p);
						}
						MailboxMenu.mode.remove(p);
						MailboxMenu.mode.put(p, "READ");
						MailboxMenu.openMailbox(p, p, 1);
						MailboxMenu.openInv(p);
					} catch(Exception e) {}
					return true;
				}
				if (args.length == 2) {
					if (args[0].equalsIgnoreCase("item") || args[0].equalsIgnoreCase("senditem")) {
						DataInitEvent.mailmode.remove(p);
						DataInitEvent.sendmail.remove(p);
						try {
							OfflinePlayer op = Bukkit.getOfflinePlayer(args[1]);
							DataInitEvent.sendmail.put(p, op);
						} catch (Exception e) {
							e.printStackTrace();
							String sm = plugin.getConfig()
									.getString("messages.mailbox.sendmessage-error.syntax-message");
							String dm = plugin.getConfig()
									.getString("messages.mailbox.sendmessage-error.description-message");
							TextJson.sendHoverJson(p, TextJson.placeholderReplace(null, null, sm),
									TextJson.placeholderReplace(p, Bukkit.getPlayer(args[1]), dm));
							return true;
						}
						p.sendMessage("d");
						String sm = plugin.getConfig().getString("messages.mailbox.sendmessage-pre.syntax-message");
						String dm = plugin.getConfig()
								.getString("messages.mailbox.sendmessage-pre.description-message");
						try {
							DataInitEvent.mailmode.put(p, "item");
							TextJson.sendHoverJson(p, TextJson.placeholderReplace(p, Bukkit.getPlayer(args[1]), sm),
									TextJson.placeholderReplace(p, Bukkit.getPlayer(args[1]), dm));
						} catch (Exception e) {
							e.printStackTrace();
						}
						return true;
					}
					if (args[0].equalsIgnoreCase("send")) {
						if (args[1].length() > 0) {
							DataInitEvent.mailmode.remove(p);
							DataInitEvent.sendmail.remove(p);
							try {
								OfflinePlayer op = Bukkit.getOfflinePlayer(args[1]);
								DataInitEvent.sendmail.put(p, op);
							} catch (Exception e) {
								e.printStackTrace();
								String sm = plugin.getConfig()
										.getString("messages.mailbox.sendmessage-error.syntax-message");
								String dm = plugin.getConfig()
										.getString("messages.mailbox.sendmessage-error.description-message");
								TextJson.sendHoverJson(p, TextJson.placeholderReplace(null, null, sm),
										TextJson.placeholderReplace(p, Bukkit.getPlayer(args[1]), dm));
								return true;
							}
							String sm = plugin.getConfig().getString("messages.mailbox.sendmessage-pre.syntax-message");
							String dm = plugin.getConfig()
									.getString("messages.mailbox.sendmessage-pre.description-message");
							try {
								DataInitEvent.mailmode.put(p, "msg");
								TextJson.sendHoverJson(p, TextJson.placeholderReplace(p, Bukkit.getPlayer(args[1]), sm),
										TextJson.placeholderReplace(p, Bukkit.getPlayer(args[1]), dm));
							} catch (Exception e) {
								e.printStackTrace();
							}
							return true;
						}
					}
				}
				if (args.length == 1 && (args[0].equals("?") || args[0].equalsIgnoreCase("help"))) {
					showHelp(p, 1);
					return true;
				}
			}
		}

		return false;
	}

}
