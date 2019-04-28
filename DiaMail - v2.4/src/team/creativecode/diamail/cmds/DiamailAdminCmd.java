package team.creativecode.diamail.cmds;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import team.creativecode.diamail.manager.AdminManager;

public class DiamailAdminCmd implements CommandExecutor, TabCompleter {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("diamailadmin")) {
			if (args.length == 1) {
				if (sender instanceof Player) {
					Player p = (Player) sender;
					AdminManager admin = new AdminManager(p);
					if (args[0].equalsIgnoreCase("physicalMailbox")) {
						Location loc = p.getLocation();
						loc.setY(loc.getY() - 1);
						if (!loc.getBlock().getType().equals(Material.AIR)) {
							admin.physicalMailbox(loc.getBlock());
						}
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> list = new ArrayList<>();
		if (args.length == 1) {
			list.add("physicalMailbox");
			return list;
		}
		return null;
	}

}
