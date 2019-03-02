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
		//PlayerData pd = new PlayerData(clicker);
		
		switch (args[0].toString().toUpperCase().split(":")[1]) {
		case "TAKE":
			if (!this.m.getItem().getType().equals(Material.AIR)) {
				if (this.m.getReceiver().getPlayer().equals(clicker)) {
					if (this.m.getReceiver().isOnline()) {
						if (ItemManager.hasAvaliableSlot(clicker, 1)) {
							//pd.getLanguage().sendMessage(clicker, this.m.getPlaceholder().useAsList(this.getPlaceholder().useAsList(pd.getLanguage().getMessages().get("notification-take"))));
							clicker.getInventory().addItem(this.m.getItem());
						}else {
							this.getPlaceholder().inputData("inventory_need_free", 1 + "");
							//pd.getLanguage().sendMessage(clicker, this.getPlaceholder().useAsList(pd.getLanguage().getMessages().get("notification-take-failed")));
						}
					}
				}else {
					if (clicker.hasPermission("diamail.admin")) {
						
					}
				}
			}
		case "READ":
			this.m.read(clicker);
			break;
		case "DELETE":
			this.m.delete(this.m.getSender().getPlayer().equals(clicker));
			break;
		}
	}



}
