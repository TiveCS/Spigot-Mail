package team.creativecode.diamail.cmds;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import team.creativecode.diamail.Main;
import team.creativecode.diamail.manager.PlayerData;
import team.creativecode.diamail.manager.mail.Mail;
import team.creativecode.diamail.utils.Placeholder;

public class DiamailCmd implements CommandExecutor {

	Main plugin = Main.getPlugin(Main.class);
	
	public void help(Player p,int page) {
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&2DiaMail help page &7- &a" + plugin.getDescription().getVersion()));
		p.sendMessage(" ");
		List<String> index = new ArrayList<String>();
		switch(page) {
		case 1:
			index.add("help.send");
			index.add("help.sendall");
			index.add("help.check");
			index.add("help.aliases");
			index.add("help.block");
		}
		
		for (String i : index) {
			String msg = Main.lang.getMessages().get(i).get(0), hmsg = Main.lang.getMessages().get(i).get(1);
			TextComponent com = new TextComponent(ChatColor.translateAlternateColorCodes('&', msg));
			com.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hmsg).create()));
			p.spigot().sendMessage(com);
		}
		p.sendMessage(" ");
	}
	
	@Override
	public boolean onCommand(CommandSender commandSender, Command command, String label, String[] strings) {
		if (command.getName().equalsIgnoreCase("diamail")) {
			if (commandSender instanceof Player) {
				Placeholder plc = new Placeholder();
				Player p = (Player) commandSender;
				PlayerData pd = new PlayerData(p);
				if (strings.length == 0) {
					pd.getMailbox().open(p);
					return true;
				}
				if (strings.length == 1) {
					if (strings[0].equalsIgnoreCase("?") || strings[0].equalsIgnoreCase("help")) {
						help(p, 1);
						return true;
					}
					if (strings[0].equalsIgnoreCase("send")) {
						new Mail(pd, p, false);
						return true;
					}
					if (strings[0].equalsIgnoreCase("check")) {
						List<String> msg = new ArrayList<String>(Main.placeholder.useAsList(Main.lang.getMessages().get("command.check")))
								, hmsg = new ArrayList<String>(Main.placeholder.useAsList(Main.lang.getMessages().get("command.check-hovertext")));
						TextComponent main = new TextComponent();
						TextComponent hover = new TextComponent();
						plc.inputData("mailbox", "" + (pd.getInbox().size() + pd.getOutbox().size()));
						plc.inputData("inbox", "" + pd.getInbox().size());
						plc.inputData("outbox", "" + pd.getOutbox().size());
						plc.useAsList(msg);
						plc.useAsList(hmsg);
						for (String m : msg) {
							main.addExtra(new TextComponent(new ComponentBuilder(m).create()));
						}
						
						for (int line = 0; line < hmsg.size(); line++) {
							hover.addExtra(new TextComponent(hmsg.get(line)));
							if (hmsg.size() > (line + 1)) {
								hover.addExtra("\n");
							}
						}
						
						main.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/diamail"));
						main.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hover).create()));
						
						p.spigot().sendMessage(main);
						
						return true;
					}
				}
			}
		}
		return false;
	}

}
