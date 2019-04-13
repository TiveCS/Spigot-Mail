package team.creativecode.diamail.manager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import team.creativecode.diamail.Main;
import team.creativecode.diamail.utils.ConfigManager;
import team.creativecode.diamail.utils.DataConverter;
import team.creativecode.diamail.utils.Placeholder;

public abstract class Menu {
	
	public enum ClickDataType{
		RIGHT, LEFT, MIDDLE, ANY;
	}
	
	public enum SlotPriority{
		LOWEST, MEDIUM, HIGHEST;
	}
	
	Main plugin = Main.getPlugin(Main.class);
	public static HashMap<Player, Menu> singleMenu = new HashMap<Player, Menu>();

	private HashMap<String, Object> customObject = new HashMap<String, Object>();
	
	private HashMap<String, Object> variables = new HashMap<String, Object>();
	private String title = "CUSTOM MENU";
	private int rows = 3;
	private Placeholder plc = new Placeholder();
	private Inventory menu;
	private HashMap<Integer, ItemStack> invdata = new HashMap<Integer, ItemStack>();
	private HashMap<Integer, HashMap<ClickDataType, List<String>>> clickdata = new HashMap<Integer, HashMap<ClickDataType, List<String>>>();
	private int page = 1;
	private File folder = new File(plugin.getDataFolder() + "/Menu");
	private File file = new File(plugin.getDataFolder() + "/Menu", this.getClass().getSimpleName() + ".yml");
	private FileConfiguration config;

	public Menu() {
		loadMenu();
	}
	
	public abstract void actionCustom(Player clicker, int slot, ClickType click, Object... args);
	
	public void loadMenu() {
		if (!folder.exists()) {
			folder.mkdir();
		}
		if (!file.exists()) {
			plugin.saveResource("Menu/" + this.getClass().getSimpleName() + ".yml", false);
		}
		this.config = YamlConfiguration.loadConfiguration(this.file);
		plc.inputData("page", this.getPage() + "");
		
		loadVariables();
		this.title = ConfigManager.contains(getFile(), "menu-config.Title") ? ChatColor.translateAlternateColorCodes('&', this.config.getString("menu-config.Title")) : "CUSTOM MENU";
		this.rows = ConfigManager.contains(getFile(), "menu-config.Rows") ? this.config.getInt("menu-config.Rows") : 3;
		
		this.title = plc.use(title);
		
		initMenu(this.title, this.rows*9);
	}
	
	public void initMenu(String title, int size) {
		this.menu = Bukkit.createInventory(null, size, title);
	}
	
	public void loadVariables() {
		this.variables.clear();
		if (ConfigManager.contains(getFile(), "variables")) {
			for (String key : this.config.getConfigurationSection("variables").getKeys(false)) {
				this.variables.put(key, this.config.get("variables." + key));
			}
		}
	}
	
	public void action(Player clicker, int slot, ClickType click, Object... args) {
		ClickDataType cdt = ClickDataType.ANY;
		
		if (this.getClickData().containsKey(slot)) {
			if (!this.getClickData().get(slot).containsKey(ClickDataType.ANY)) {
				if (click.isLeftClick()) {
					cdt = ClickDataType.LEFT;
				}else if (click.isRightClick()) {
					cdt = ClickDataType.RIGHT;
				}else if (click.equals(ClickType.MIDDLE)) {
					cdt = ClickDataType.MIDDLE;
				}
			}
			
			List<String> input = new ArrayList<String>(this.getClickData().get(slot).get(cdt));
			for (String s : input) {
				if (s.equalsIgnoreCase("NEXT_PAGE")) {
					this.nextPage();
				}else if (s.equalsIgnoreCase("RELOAD_PAGE")) {
					this.setPage(this.getPage() - 1);
					nextPage();
				}
				else if (s.equalsIgnoreCase("PREVIOUS_PAGE")) {
					this.previousPage();
				}else if (s.startsWith("COMMAND:")) {
					s = s.replace("COMMAND:", "");
					s = plc.use(s);
					clicker.performCommand(s);
				}else if (s.startsWith("CONSOLE_COMMAND:")) {
					s = s.replace("CONSOLE_COMMAND:", "");
					s = plc.use(s);
					Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), s);
				}else if (s.startsWith("CLOSE")) {
					clicker.closeInventory();
					singleMenu.remove(clicker);
				}else if (s.startsWith("CHANGE:")) {
					s = s.replace("CHANGE:", "");
					String[] split = s.split("=");
					String v = "";
					int i = -1;
					try {
						i = Integer.parseInt(split[1]);
					}catch(Exception e) {
						v = split[1];
					}
					this.variables.put(split[0], i >= 0 ? i : v);
				}
				else if (s.startsWith("CUSTOM")) {
					actionCustom(clicker, slot, click, s, args);
				}else {
					System.out.println("Action not found! (Action Type: " + s + ", Slot: " + slot +" ) [" + this.getFile().getName() + "]");
				}
			}
		}
	}

	public void createFile() {
		if (!file.exists()) {
			plugin.saveResource("Menu/" + this.getClass().getSimpleName() + ".yml", false);
		}
	}
	
	public void update() {
		plc.inputData("page", this.getPage() + "");
		this.title = plc.use(ConfigManager.contains(getFile(), "menu-config.Title") ? ChatColor.translateAlternateColorCodes('&', this.config.getString("menu-config.Title")) : "CUSTOM MENU");
		
		List<HumanEntity> viewer = new ArrayList<HumanEntity>(this.getMenu().getViewers());
		
		this.setMenu(Bukkit.createInventory(null, this.rows*9, this.title));
		for (HumanEntity he : viewer) {
			he.closeInventory();
			he.openInventory(this.getMenu());
		}
		
		for (int key : this.getInventoryData().keySet()) {
			this.getMenu().setItem(key, this.getInventoryData().get(key));
		}
		
	}
	
	public void refresh() {
		initItemFromConfig();
		
		for (int key : this.getInventoryData().keySet()) {
			this.getMenu().setItem(key, this.getInventoryData().get(key));
		}
	}
	
	public void open(Player p) {
		initItemFromConfig();
		update();
		singleMenu.put(p, this);
		p.openInventory(this.getMenu());
	}
	
	public void setMenu(Inventory newMenu) {
		this.menu = newMenu;
	}
	
	public void remakeMenu(String menutitle, int rows) {
		this.menu = Bukkit.createInventory(null, rows, ChatColor.translateAlternateColorCodes('&', menutitle));
	}
	
	public void nextPage() {
		this.setPage(this.getPage() + 1);
		initItemFromConfig();
		update();
		plc.inputData("page", this.getPage() + "");
	}
	
	public void previousPage() {
		if (this.getPage() > 1) {
			this.setPage(this.getPage() - 1);
		}else {
			this.setPage(1);
		}
		initItemFromConfig();
		update();
		plc.inputData("page", this.getPage() + "");
	}
	
	@SuppressWarnings("deprecation")
	public ItemStack convertItem(String p, Placeholder plc) {
		ItemStack item = new ItemStack(Material.AIR);
		
		String name = "";
		List<String> lore = new ArrayList<String>();
		int amount = 1;
		byte data = 0;
		Material material = Material.AIR;
		
		try {
			name = plc.use(ConfigManager.contains(getFile(), p + ".Name") ? this.getConfig().getString(p + ".Name"): "");
			amount = ConfigManager.contains(getFile(), p + ".Amount") ? this.getConfig().getInt(p + ".Amount") : 1;
			lore = plc.useAsList(ConfigManager.contains(getFile(), p + ".Lore") ? this.getConfig().getStringList(p + ".Lore") : new ArrayList<String>());
			
			material = Material.valueOf(this.getConfig().getString(p + ".Material").toUpperCase());
			data = ConfigManager.contains(getFile(), p + ".Data") ? Byte.parseByte(ConfigManager.get(getFile(), p + ".Data").toString()) : 0;
			
			if (data != 0) {
				item = new ItemStack(material, amount);
			}else {
				item = new ItemStack(material, amount, data);
			}
			
			ItemMeta meta = item.getItemMeta();
			if (!name.equals("")) {
				meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
			}if (lore.size() > 0) {
				lore = DataConverter.colored(lore);
				lore = plc.listUse(lore);
				meta.setLore(DataConverter.colored(lore));
			}
			item.setItemMeta(meta);
		}catch(Exception e) {
			System.out.println("Failed to initialize Item from path [" + p +"] (" + this.getFile().getName() + ")");
		}
		
		return item;
	}
	
	public int getPageFromFile(String p) {
		return ConfigManager.contains(getFile(), p + ".Page") ? this.getConfig().getInt(p + ".Page") : 1;
	}
	
	public int getMinPageFromFile(String p) {
		return ConfigManager.contains(getFile(), p + ".Minimum-Page") ? this.getConfig().getInt(p + ".Minimum-Page") : 0;
	}
	
	public int getMaxPageFromFile(String p) {
		return ConfigManager.contains(getFile(), p + ".Maximum-Page") ? this.getConfig().getInt(p + ".Maximum-Page") : 0;
	}
	
	public HashMap<String, Object> getConditionsFromFile(String p) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		for (String s : this.config.getStringList(p + ".Conditions")) {
			if (s.startsWith("VARIABLE:")) {
				s = s.replace("VARIABLE:", "");
				int i = -1;
				try {
					i = Integer.parseInt(s.split("=")[1]);
				}catch(Exception e) {}
				map.put(s.split("=")[0], i >= 0 ? i : s.split("=")[1]);
			}
		}
		return map; 
	}
	
	public List<String> getPathName(int slot) {
		List<String> list = new ArrayList<String>();
		for (String p : getConfig().getConfigurationSection("menu-data").getKeys(false)) {
			if (this.getInvDataSlot("menu-data." + p).contains(slot)) {
				list.add(p);
				continue;
			}
		}
		return list;
	}
	
	public List<Integer> getInvDataSlot(String p) {
		List<Integer> slot = new ArrayList<Integer>();
		String[] split = this.getConfig().getString(p + ".Slot").split(",");
		for (String s : split) {
			if (s.contains(" ")) {
				s.replace(" ", "");
			}try {
				slot.add(Integer.parseInt(s));
			}catch(Exception e) {
				System.out.println("Failed to initialize Slot number from path [" + p +"] (" + this.getFile().getName() + ")");
			}
		}
		return slot;
	}
	
	public void initItemFromConfig() {
		List<String> path = new ArrayList<String>(this.getConfig().getConfigurationSection("menu-data").getKeys(false));
		
		this.invdata.clear();
		this.clickdata.clear();
		for (String pt : path) {
			
			String p = "menu-data." + pt;
			
			// Prepare
			int page = 1, minpage = 0, maxpage = 0;
			HashMap<String, Object> conditions = new HashMap<String, Object>();
			List<String> click = new ArrayList<String>();
			List<Integer> slot = new ArrayList<Integer>();
			ItemStack item;
			boolean stopped = false;
			
			// Input
			conditions = getConditionsFromFile(p);
			page = getPageFromFile(p);
			minpage = getMinPageFromFile(p);
			maxpage = getMaxPageFromFile(p);
			
			// Check if min and max page is set in config
			if (minpage == 0 && maxpage == 0) {
				if (page != this.getPage()) {
					continue;
				}
			}else {
				if (!(this.getPage() >= minpage && (maxpage == 0 || this.getPage() <= maxpage))) {
					continue;
				}
			}
			
			for (String key : conditions.keySet()) {
				Object v = conditions.get(key);
				Object var = this.getVariables().get(key);
				
				if (!v.equals(var)) {
					stopped = true;
					break;
				}
			}
			if (stopped) {
				continue;
			}
			
			for (int i : getInvDataSlot(p)) {
				slot.add(i);
			}
			
			item = convertItem(p, this.getPlaceholder());
			
			if (ConfigManager.contains(getFile(), p + ".Click-Type")) {
				for (int i : slot) {
					HashMap<ClickDataType, List<String>> data = new HashMap<ClickDataType, List<String>>();
					if (ConfigManager.contains(getFile(), p + ".Click-Type." + ClickDataType.ANY.toString())) {
						click = this.getConfig().getStringList(p + ".Click-Type." + ClickDataType.ANY.toString());
						data.put(ClickDataType.ANY, click);
					}else {
						for (String cs : this.config.getConfigurationSection(p + ".Click-Type").getKeys(false)) {
							ClickDataType cdt = ClickDataType.valueOf(cs.toUpperCase());
							click = this.getConfig().getStringList(p + ".Click-Type." + cdt.toString());
							data.put(cdt, click);
						}
					}
					this.clickdata.put(i, data);
				}
			}
			
			for (int i : slot) {
				this.invdata.put(i, item);
			}
		}
	}
	
	public void inputObject(String path, Object obj) {
		this.customObject.put(path, obj);
	}
	
	public void setPage(int page) {
		this.page = page;
	}

	public Placeholder getPlaceholder() {
		return this.plc;
	}
	
	public HashMap<String, Object> getVariables(){
		return this.variables;
	}
	
	public int getPage() {
		return this.page;
	}
	
	public File getFile() {
		return this.file;
	}
	
	public File getFolder() {
		return this.folder;
	}
	
	public HashMap<Integer, HashMap<ClickDataType, List<String>>> getClickData(){
		return this.clickdata;
	}
	
	public FileConfiguration getConfig() {
		return this.config;
	}
	
	public Inventory getMenu() {
		return this.menu;
	}
	
	public HashMap<Integer, ItemStack> getInventoryData(){
		return this.invdata;
	}
	
	public HashMap<String, Object> getCustomObject(){
		return this.customObject;
	}
	
}
