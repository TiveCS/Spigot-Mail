package team.creativecode.diamail.manager.menu;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import team.creativecode.diamail.manager.Menu;
import team.creativecode.diamail.manager.mail.Mail;
import team.creativecode.diamail.utils.ItemManager;

public class MailShow extends Menu{

	Player p;
	Mail m;
	
	public void loadMailShow() {
		this.p = (Player) this.getCustomObject().get("player");
		this.m = (Mail) this.getCustomObject().get("mail");
		this.m.initPlaceholder();
		
		for (int slot : this.getInventoryData().keySet()) {
			ItemStack item = this.getInventoryData().get(slot);
			if (!this.m.getItem().getType().equals(Material.AIR) && this.getPathName(slot).contains("item")) {
				item = this.m.getItem().clone();
			}
			
			ItemMeta meta = item.getItemMeta();
			if (meta.hasLore()) {
				List<String> lore = meta.getLore();
				lore = this.m.getPlaceholder().useAsList(lore);
				lore = this.m.getPlaceholder().listUse(lore);
				meta.setLore(lore);
			}
			
			item.setItemMeta(meta);
			this.getInventoryData().put(slot, item);
		}
	}
	
	@Override
	public void open(Player p) {
		initItemFromConfig();
		
		loadMailShow();
		
		update();
		Menu.singleMenu.put(p, this);
		p.openInventory(this.getMenu());
	}
	
	@Override
	public void actionCustom(Player clicker, int slot, ClickType click, Object... args) {
		
		switch (args[0].toString().toUpperCase().split(":")[1]) {
		case "TAKE":
			if (this.m.getReceiver().isOnline()) {
				if (ItemManager.hasAvaliableSlot(this.m.getReceiver().getPlayer(), 1)) {
					if (this.m.getItem() != null || !this.m.getItem().getType().equals(Material.AIR)) {
						this.m.takeItem(this.m.getReceiver().getPlayer());
					}
				}
			}
			break;
		case "READ":
			this.m.read(clicker);
			break;
		case "DELETE":
			try {
				this.m.delete(this.m.getSender().getPlayer().equals(clicker));
			}catch(Exception e) {this.m.forceDelete(clicker);}
			break;
		}
	}



}
