package team.creativecode.diamail.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import team.creativecode.diamail.Main;
import team.creativecode.diamail.activity.Mailbox;
import team.creativecode.diamail.util.ItemBuilder;
import team.creativecode.diamail.util.Placeholder;

public class PlayerSetting {
	
	private Main plugin = Main.getPlugin(Main.class);
	
	HashMap<String, Object> setting;
	OfflinePlayer p;
	List<String> listSetting;
	int page, maxPage;
	PlayerMail pm;
	Inventory inv;
	
	public PlayerSetting(OfflinePlayer op) {
		this.inv = Bukkit.createInventory(null, 4*9, op.getName() + " Setting");
		this.page = 1;
		this.p = op;
		this.pm = new PlayerMail(op);
		this.setting = new HashMap<String, Object>(this.pm.getSettings());
		this.listSetting = new ArrayList<String>(this.setting.keySet());
		initMaxPage();
		initSettingMenu();
	}
	
	public void initSettingMenu() {
		int num = ((2*7)*this.page) - 2*7;
		boolean end = false;
		int rs = 0;
		for (int row = 0; row < 4; row++) {
			for (int slot = 0; slot < 9; slot++) {
				rs = (row*9) + slot;
				if (row == 0 || row == 3) {
					this.inv.setItem(rs, ItemBuilder.createItem(Material.BLACK_STAINED_GLASS_PANE, " ", null, false));
				}else {
					List<String> listValues = new ArrayList<String>();
					if (slot > 0 && slot < 8 && end == false) {
						// Getting and setting the value
						String path = "";
						try{
							path = "player-settings-option." + this.listSetting.get(num);
						}catch(Exception e) {}
						if (path.equals("")) {
							end = true;
							continue;
						}
						ItemStack icon = null;
						try {
							if (!plugin.getConfig().getStringList(path + ".value").isEmpty()) {
								listValues = plugin.getConfig().getStringList(path + ".value");
							}else {
								icon = ItemBuilder.getSettingItem(path + ".icon");
								icon = Placeholder.playerSettingValuePlaceholder(this.pm, icon, this.listSetting.get(num));
								this.inv.setItem(rs, icon);
							}
						}catch(Exception e) {}
						
						// Setup the icon setting
						try {
							if (!(listValues.isEmpty() || listValues.size() == 0)) {
								this.inv.setItem(rs, ItemBuilder.getSettingItem(path + ".icon." + this.getSettings().get(listSetting.get(num))));
							}
						}catch(Exception e) {}
						num++;
					}else {
						if (slot == 0) {
							if (this.page == 1) {
								this.inv.setItem(rs, ItemBuilder.createItem(Material.GRAY_STAINED_GLASS_PANE, " ", null, false));
							}else {
								this.inv.setItem(rs, ItemBuilder.createItem(Material.BLUE_STAINED_GLASS_PANE, "&3Previous Page", null, false));
							}
						}else if (slot == 8) {
							if (this.page < this.maxPage) {
								this.inv.setItem(rs, ItemBuilder.createItem(Material.GREEN_STAINED_GLASS_PANE, "&aNext Page", null, false));
							}else {
								this.inv.setItem(rs, ItemBuilder.createItem(Material.GRAY_STAINED_GLASS_PANE, " ", null, false));
							}
						}
					}
				}
			}
		}
		this.inv.setItem(4*9 - 5, ItemBuilder.createItem(Material.TNT, "&cMailbox", null, false));
	}
	
	// Setting handler
	public void action(Player clicker, int slot, ClickType clickType) {
		try {
			if (slot == 4*9 - 5) {
				clicker.openInventory(Mailbox.mailbox.get(clicker).getInventory());
			}else if ((slot > 9 && slot < 17) || (slot > 18 && slot < 26)) {
				int angka = 0;
				if (slot > 18) {
					++angka;
				}
				int num = (((2*7)*this.page) - 2*7) + (slot - (10+(2*angka)));
				String key = this.listSetting.get(num),path = "player-settings-option." + key;
				List<String> listValues = new ArrayList<String>();
				Object currentValue = this.getSettings().get(key);
				Object value = null;
				try {
					listValues = plugin.getConfig().getStringList(path + ".value");
					int index = listValues.indexOf(currentValue.toString()), size = listValues.size() - 1;
					if (index < size) {
						++index;
					}else {
						index = 0;
					}
					this.setSettingValue(key, listValues.get(index));
					Mailbox.mailbox.get(clicker).update();
					clicker.openInventory(Mailbox.mailbox.get(clicker).getPlayerSettings().getInventory());
				}catch(Exception e) {
					value = plugin.getConfig().get(path + ".value");
					if (value.toString().equalsIgnoreCase("Number")) {
						int cv = Integer.parseInt(this.getSettings().get(key).toString());
						if (clickType.equals(ClickType.LEFT) || clickType.equals(ClickType.SHIFT_LEFT)) {
							++cv;
						}else if (clickType.equals(ClickType.MIDDLE)){
							cv = -1;
						}else {
							--cv;
						}
						this.setSettingValue(key, cv);
						Mailbox.mailbox.get(clicker).update();
						clicker.openInventory(Mailbox.mailbox.get(clicker).getPlayerSettings().getInventory());
					}
				}
			}
		}catch(Exception e) {}
	}
	
	public void initMaxPage() {
		int result = 1, size = this.setting.size(), divider = 8;
		double check = size / divider;
		while (check > 0) {
			++result;
			--check;
		}
		this.maxPage = result;
	}
	
	public void setMaxPage(int i) {
		this.maxPage = i;
	}
	
	public void setSettingValue(String key, Object value) {
		this.pm.setSetting(key, value);
	}
	
	public int getMaxPage() {
		return this.maxPage;
	}
	
	public List<String> getListSettings(){
		return this.listSetting;
	}
	
	public int getPage() {
		return this.page;
	}
	
	public Inventory getInventory() {
		return this.inv;
	}
	
	public OfflinePlayer getPlayer() {
		return this.p;
	}
	
	public PlayerMail getPlayerMail() {
		return this.pm;
	}
	
	public HashMap<String, Object> getSettings(){
		return this.setting;
	}
	
}
