package team.creativecode.diamail.manager.menu;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import team.creativecode.diamail.manager.Menu;
import team.creativecode.diamail.manager.PlayerData;
import team.creativecode.diamail.manager.PlayerSetting;
import team.creativecode.diamail.utils.ConfigManager;
import team.creativecode.diamail.utils.DataConverter;
import team.creativecode.diamail.utils.Placeholder;

public class Setting extends Menu {

	OfflinePlayer player;
	PlayerSetting ps;
	PlayerData pd;

	public Setting() {
		super();
		
	}
	
	public Setting(PlayerSetting ps) {
		super();
		
		this.pd = ps.getPlayerData();
		this.ps = ps;
		this.player = pd.getPlayer();
		
		getPlaceholder().inputData("player", pd.getPlayer().getName());
		initMenu(getPlaceholder().use(getMenu().getName()), getMenu().getSize());
	}
	
	@Override
	public ItemStack convertItem(String p, Placeholder plc) {
		ItemStack item = new ItemStack(Material.AIR);
		
		String name = "";
		List<String> lore = new ArrayList<String>();
		int amount = 1;
		Material material = Material.AIR;
		
		try {
			name = plc.use(ConfigManager.contains(getFile(), p + ".Name") ? this.getConfig().getString(p + ".Name"): "");
			amount = ConfigManager.contains(getFile(), p + ".Amount") ? this.getConfig().getInt(p + ".Amount") : 1;
			lore = plc.useAsList(ConfigManager.contains(getFile(), p + ".Lore") ? this.getConfig().getStringList(p + ".Lore") : new ArrayList<String>());
			
			// Apply active settings effect
			for (int line = 0; line < lore.size(); line++) {
				String m = lore.get(line);
				String path = p.split("[.]")[1];
				String set = this.ps.getSettings().get(path).toString();
				if (m.toLowerCase().endsWith(set.toLowerCase()) && m.length() >= set.length()) {
					m = m.substring(0, m.length() - set.length());
					m = m.concat(this.getVariables().get("active-settings-prefix") + set);
				}
				m = this.pd.getPlaceholder().use(m);
				lore.set(line, m);
			}
			// end
			
			material = Material.valueOf(this.getConfig().getString(p + ".Material").toUpperCase());
			
			item = new ItemStack(material, amount);
			ItemMeta meta = item.getItemMeta();
			if (!name.equals("")) {
				meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
			}if (lore.size() > 0) {
				lore = DataConverter.colored(lore);
				lore = plc.listUse(lore);
				meta.setLore(DataConverter.colored(lore));
			}
			item.setItemMeta(meta);
		}catch(Exception e) {e.printStackTrace();
			System.out.println("Failed to initialize Item from path [" + p +"] (" + this.getFile().getName() + ")");
		}
		
		return item;
	}

	@Override
	public void actionCustom(Player clicker, int slot, ClickType click, Object... args) {
		String path = args[0].toString().replace("CUSTOM:", "");
		if (this.ps.getSettings().containsKey(path)) {
			Object type = this.ps.getSettingValues().get(path);
		
			if (type.toString().equalsIgnoreCase("NUMBER")) {
				if (click.isLeftClick()) {
					this.ps.numberIncreaseSetting(path.toString(), 1);
				}else if (click.isRightClick()) {
					this.ps.numberIncreaseSetting(path.toString(), -1);
				}else if (click.equals(ClickType.MIDDLE)) {
					this.ps.changeSetting(path.toString(), -1);
				}
			}else if (type.toString().equalsIgnoreCase("TEXT")) {
				
			}else {
				try {
					this.ps.switchSetting(path.toString());
				}
				catch(Exception e) {}
			}
			this.refresh();
		}
		
	}

}
