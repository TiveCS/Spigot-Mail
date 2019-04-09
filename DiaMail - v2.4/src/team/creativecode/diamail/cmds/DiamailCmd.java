package team.creativecode.diamail.cmds;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import team.creativecode.diamail.Main;
import team.creativecode.diamail.manager.PlayerData;
import team.creativecode.diamail.manager.mail.Mail;
import team.creativecode.diamail.utils.Language;
import team.creativecode.diamail.utils.Updater;

public class DiamailCmd implements CommandExecutor, TabCompleter {

	Main plugin = Main.getPlugin(Main.class);
	
	public void help(Player p,int page) {
		List<String> index = new ArrayList<String>();
		switch(page) {
		case 1:
			index.add("help.send");
			index.add("help.sendall");
			index.add("help.check");
			index.add("help.aliases");
			break;
		case 2:
			index.add("help.block");
			index.add("help.open");
			index.add("help.mailbox");
			index.add("help.update");
			break;
		case 3:
			index.add("help.read");
			index.add("help.get");
			index.add("help.take");
			index.add("help.settings");
			break;
		}
		
		double s = Main.lang.getConfig().getConfigurationSection("help").getKeys(false).size() / 4;
		int size = (int) Math.round(s);
		
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&2DiaMail help page &8(&b" + page + "&7/&3" + size + "&8) &7- &a" + plugin.getDescription().getVersion()));
		p.sendMessage(" ");
		
		for (String i : index) {
			String msg = Main.lang.getMessages().get(i).get(0), hmsg = Main.lang.getMessages().get(i).get(1);
			TextComponent com = new TextComponent(ChatColor.translateAlternateColorCodes('&', msg));
			com.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hmsg).create()));
			p.spigot().sendMessage(com);
		}
		p.sendMessage(" ");
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender commandSender, Command command, String label, String[] strings) {
		if (command.getName().equalsIgnoreCase("diamail")) {
			if (commandSender instanceof Player) {
				Player p = (Player) commandSender;
				PlayerData pd = new PlayerData(p);
				if (strings.length == 0 || (strings.length == 1 && strings[0].equalsIgnoreCase("mailbox"))) {
					pd.getMailbox().open(p);
					return true;
				}
				if (strings.length == 1) {
					if (strings[0].equalsIgnoreCase("update")) {
						String newversion = Updater.getUpdateCheck(Main.rsid);
						Main.placeholder.inputData("version_new", newversion);
						if (newversion.equals("")) {
							Main.lang.sendMessage(p, Main.placeholder.useAsList(Main.lang.getMessages().get("alert.updater-failed")));
						}
						else if (Updater.isNewVersion()) {
							Main.lang.sendMessage(p, Main.placeholder.useAsList(Main.lang.getMessages().get("alert.updater")));
						}else{
							Main.lang.sendMessage(p,  Main.placeholder.useAsList(Main.lang.getMessages().get("alert.updater-latest")));
						}
						return true;
					}
					if (strings[0].equalsIgnoreCase("?") || strings[0].equalsIgnoreCase("help")) {
						help(p, 1);
						return true;
					}
					if (strings[0].equalsIgnoreCase("settings") || strings[0].equalsIgnoreCase("setting")) {
						pd.getPlayerSetting().showSettingMenu(p);
						return true;
					}
					if (strings[0].equalsIgnoreCase("send")) {
						if (p.hasPermission("diamail.access.readonly")) {
							Language lang = pd.getLanguage();
							lang.sendMessage(p, lang.getMessages().get("alert.no-permission"));
							return true;
						}
						new Mail(pd, false);
						return true;
					}
					if (strings[0].equalsIgnoreCase("sendall")) {
						if (!p.hasPermission("diamail.access.sendall")) {
							Language lang = pd.getLanguage();
							lang.sendMessage(p, lang.getMessages().get("alert.no-permission"));
							return true;
						}
						new Mail(pd, true);
						return true;
					}
					if (strings[0].equalsIgnoreCase("check")) {
						pd.checkMailbox(p);
						
						return true;
					}
				}if (strings.length == 2) {
					if (strings[0].equalsIgnoreCase("read")) {
						List<Mail> mailbox = pd.getInbox();
						mailbox.addAll(pd.getOutbox());
						Mail m = mailbox.get(Integer.parseInt(strings[1]));
						m.read(p);
						return true;
					}
					if (strings[0].equalsIgnoreCase("delete")) {
						List<Mail> mailbox = pd.getInbox();
						mailbox.addAll(pd.getOutbox());
						Mail m = mailbox.get(Integer.parseInt(strings[1]));
						if (m.getSender().equals(p)){
							m.delete(true);
						}else {
							m.delete(false);
						}
						return true;
					}
					if (strings[0].equalsIgnoreCase("?") || strings[0].equalsIgnoreCase("help")) {
						help(p, Integer.parseInt(strings[1]));
						return true;
					}
					if (strings[0].equalsIgnoreCase("mailbox")) {
						pd.getMailbox().setPage(Integer.parseInt(strings[1]));
						pd.getMailbox().open(p);
						return true;
					}
					if (strings[0].equalsIgnoreCase("block")) {
						pd.block(Bukkit.getOfflinePlayer(strings[1]));
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String arg2, String[] args) {
		List<String> tab = new ArrayList<String>();
		if (cmd.getName().equalsIgnoreCase("diamail")) {
			Player p = (Player) sender;
			if (args.length == 1) {
				PlayerData pd = new PlayerData(p);
				List<Mail> mailbox = pd.getInbox();
				mailbox.addAll(pd.getOutbox());
				for (String s : Main.lang.getConfig().getConfigurationSection("help").getKeys(false)) {
					tab.add(s);
				}
				return tab;
			}
			if (args.length == 2) {
				PlayerData pd = new PlayerData(p);
				List<Mail> mailbox = pd.getInbox();
				mailbox.addAll(pd.getOutbox());
				if (args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("open") || args[0].equalsIgnoreCase("take") || args[0].equalsIgnoreCase("get") || args[0].equalsIgnoreCase("read")) {
					for (int i = 0; i < mailbox.size(); i++) {
						tab.add(i + "");
					}
					return tab;
				}
			}
		}
		return tab;
	}

}
