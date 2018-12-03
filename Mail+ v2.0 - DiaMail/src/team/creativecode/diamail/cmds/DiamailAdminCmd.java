package team.creativecode.diamail.cmds;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class DiamailAdminCmd implements CommandExecutor {

	Inventory menu = Bukkit.createInventory(null, 4*9, "DiaMail Admin");
	
	public void update() {
		
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (cmd.getName().equalsIgnoreCase("diamailadmin")) {
				if (args.length == 0) {
					p.openInventory(menu);
					return true;
				}
			}
		}
		
		return false;
	}

}
