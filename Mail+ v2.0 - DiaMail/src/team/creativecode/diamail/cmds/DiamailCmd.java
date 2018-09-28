package team.creativecode.diamail.cmds;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import team.creativecode.diamail.Main;
import team.creativecode.diamail.activity.MailManager;
import team.creativecode.diamail.activity.MailManager.MailType;
import team.creativecode.diamail.activity.MailSend;
import team.creativecode.diamail.activity.Mailbox;
import team.creativecode.diamail.manager.PlayerMail;

public class DiamailCmd implements CommandExecutor, TabCompleter {

	private Main plugin = Main.getPlugin(Main.class);
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (cmd.getName().equalsIgnoreCase("diamail")) {
				PlayerMail pm = new PlayerMail(p);
				if (args.length == 0 || (args.length == 1 && args[0].equalsIgnoreCase("mailbox"))) {
					Mailbox mb = new Mailbox(p, 1, MailType.valueOf(pm.getSettings().get("show-mailbox").toString().toUpperCase()));
					Mailbox.mailbox.remove(p);
					Mailbox.mailbox.put(p, mb);
					p.openInventory(mb.getInventory());
					return true;
				}
				if (args.length == 3) {
					if (args[0].equalsIgnoreCase("mailbox")) {
						if (args[1].length() > 0 && (args[2].equalsIgnoreCase("inbox") || args[2].equalsIgnoreCase("outbox") || args[2].equalsIgnoreCase("all"))) {
							try {
								Mailbox mb = new Mailbox(p, Integer.parseInt(args[1]), MailType.valueOf(args[2].toUpperCase()));
								Mailbox.mailbox.remove(p);
								Mailbox.mailbox.put(p, mb);
								p.openInventory(mb.getInventory());
								return true;
							}catch(Exception e) {return false;}
						}
					}
				}
				if (args.length == 2) {
					if (args[0].equalsIgnoreCase("send")) {
						if (args[1].length() > 0) {
							try {
								MailSend ms = new MailSend(p, Bukkit.getPlayer(args[1]), false);
								MailSend.reg.put(p, ms);
							}catch(Exception e) {
								sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("prefix") + " &cPlayer not found!"));
							}
							return true;
						}
					}
					if (args[0].equalsIgnoreCase("mailbox")) {
						try {
							Mailbox mb = new Mailbox(p, Integer.parseInt(args[1]), MailType.valueOf(pm.getSettings().get("show-mailbox").toString().toUpperCase()));
							Mailbox.mailbox.remove(p);
							Mailbox.mailbox.put(p, mb);
							p.openInventory(mb.getInventory());
							return true;
						} catch (Exception e) {
							return false;
						}
					}
				}
				if (args.length == 1) {
					if (args[0].equalsIgnoreCase("aliases") || args[0].equalsIgnoreCase("alias")) {
						List<String> aliases = new ArrayList<String>(plugin.getConfig().getConfigurationSection("command-aliases").getKeys(false));
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&2Aliases &8- &7Executed command"));
						sender.sendMessage(" ");
						for (String a : aliases) {
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7- &a/" + a + " &8= &f" + plugin.getConfig().getString("command-aliases." + a)));
						}
						sender.sendMessage(" ");
						return true;
					}
					if (args[0].equalsIgnoreCase("send")) {
						MailSend ms = new MailSend(p, null, false);
						MailSend.reg.put(p, ms);
						return true;
					}
					if (args[0].equals("?") || args[0].equalsIgnoreCase("help")) {
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&3Help of &bDiaMail"));
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
								"&c<> &f= Required args, &7[] &f= Optional args"));
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " "));

						sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
								"&2/diamail &aaliases &8- &fShow all DiaMail's command aliases"));
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
								"&2/diamail &amailbox &7[Page] [Inbox/Outbox/All] &8- &fShow mailbox contents"));
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
								"&2/diamail &asend &7[Player] [Message] &8- &fSend mail to player"));
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
								"&2/diamail &adelete &7[&c<Inbox/Outbox> <Index>&7] &8- &fDelete a mail"));
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
								"&2/diamail &acheck &8- &fCheck your mailbox size"));
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
								"&2/diamail &aitem &7[Player] [Message] &8- &fSend mail with item on hand"));
						
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', " "));
						
						return true;
					}
					if (args[0].equalsIgnoreCase("check")) {
						MailManager.checkMail(p, p);
						return true;
					}
				}
				if (args.length == args.length) {
					if (args[0].equalsIgnoreCase("delete")) {
						if (args[1].length() > 0 && args[2].length() > 0) {
							List<String> list = new ArrayList<String>(pm.getMailbox(MailType.valueOf(args[1].toUpperCase())));
							MailManager.deleteMail(p, p, MailType.valueOf(args[1].toUpperCase()), UUID.fromString(list.get(Integer.parseInt(args[2]))));
							return true;
						}
					}
					if (args[0].equalsIgnoreCase("send") || args[0].equalsIgnoreCase("item")) {
						ItemStack item = null, clone = null;
						if (args[0].equalsIgnoreCase("item")) {
							if (!p.getInventory().getItemInMainHand().getType().toString().equals("AIR")) {
								item = p.getInventory().getItemInMainHand();
								clone = item.clone();
								p.getInventory().remove(item);
							}else {
								sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("prefix") + " You cannot send &cAIR &fin your mail!"));
								return true;
							}
						}
						try {
							String f = "";
							for (int i = 0; i < args.length; i++) {
								if ((i != 0 && i != 1)) {
									if (f == "") {
										f = args[i];
									} else {
										f = f + " " + args[i];
									}
								}
							}
							MailManager.sendMail(p, Bukkit.getPlayer(args[1]), f, clone);
						} catch (Exception e) { }
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = (Player) sender;
		List<String> list = new ArrayList<String>();
		if (cmd.getName().equalsIgnoreCase("diamail")) {
			if (args[0].length() <= 0) {
				list.add("?");
				list.add("send");
				list.add("delete");
				list.add("check");
				list.add("item");
				list.add("mailbox");
				list.add("aliases");
				return list;
			}
			else if (args.length == args.length) {
				if (args[0].equalsIgnoreCase("mailbox") && args[1].length() > 0) {
					list.add("Inbox");
					list.add("Outbox");
					list.add("All");
					return list;
				}
				if (args[0].equalsIgnoreCase("delete")) {
					if (args.length == 3) {
						if (args[1].length() > 0) {
							PlayerMail pm = new PlayerMail(p);
							List<String> trans = new ArrayList<String>(
									pm.getMailbox(MailType.valueOf(args[1].toUpperCase())));
							for (int i = 0; i < trans.size(); i++) {
								list.add(i + "");
							}
							return list;
						}
					} else {
						list.add("Inbox");
						list.add("Outbox");
						return list;
					}
				}
			}
		}
		return null;

	}

}
