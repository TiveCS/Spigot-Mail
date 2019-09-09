package com.rehoukrel.diamail.utils.menu;

import com.rehoukrel.diamail.utils.ConfigManager;
import com.rehoukrel.diamail.utils.XMaterial;
import com.rehoukrel.diamail.utils.language.Placeholder;
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
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public abstract class Menu {
	
	public enum ClickDataType{
		RIGHT, LEFT, MIDDLE, ANY;
	}
	
	public enum SlotPriority{
		LOWEST, MEDIUM, HIGHEST;
	}
	
	private JavaPlugin plugin;
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
	private File folder;
	private File file;
	private ConfigManager cm;
	private FileConfiguration config;
	private TreeMap<Integer, List<String>> priorityList = new TreeMap<>();
	private TreeMap<Integer, HashMap<String, Integer>> countSlot = new TreeMap<>(), currentCountSlot = new TreeMap<>();
	private TreeMap<Integer, HashMap<Integer, String>> occupied = new TreeMap<>();
	private HashMap<String, String> options = new HashMap<>();

	public Menu(JavaPlugin plugin)
	{
		this.plugin = plugin;
		this.folder = new File(plugin.getDataFolder(), "Menu");
		this.file = new File(plugin.getDataFolder() + "/Menu", this.getClass().getSimpleName() + ".yml");
		this.cm = new ConfigManager(file);
		loadMenu();
	}
	
	public abstract void actionCustom(Player clicker, int slot, ClickType click, Object... args);
	
	public void loadMenu() {
		if (!folder.exists() && plugin != null) {
			folder.mkdir();
		}
		if (!file.exists() && plugin != null) {
			plugin.saveResource("Menu/" + this.getClass().getSimpleName() + ".yml", false);
		}
		if (file.exists()) {
			this.config = YamlConfiguration.loadConfiguration(this.file);
			plc.addReplacer("page", this.getPage() + "");

			loadVariables();
			loadMenuData();
			this.title = cm.getConfig().contains("menu-config.Title") ? ChatColor.translateAlternateColorCodes('&', this.config.getString("menu-config.Title")) : "CUSTOM MENU";
			this.rows = cm.getConfig().contains("menu-config.Rows") ? this.config.getInt("menu-config.Rows") : 3;

			this.title = plc.use(title);

			initMenu(this.title, this.rows * 9);
		}else{
			plugin.getLogger().warning("Menu couldn't load because of file is not exist! (" + this.getFile().getName() + ")");
		}
	}
	
	public void initMenu(String title, int size) {
		this.menu = Bukkit.createInventory(null, size, title);
	}
	
	public void loadVariables() {
		this.variables.clear();
		if (cm.getConfig().contains("variables")) {
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
		plc.addReplacer("page", this.getPage() + "");
		this.title = plc.use( cm.getConfig().contains("menu-config.Title") ? ChatColor.translateAlternateColorCodes('&', this.config.getString("menu-config.Title")) : "CUSTOM MENU");
		
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
		if (file.exists()) {
			initItemFromConfig();
			update();
			singleMenu.put(p, this);
			p.openInventory(this.getMenu());
		}else{
			plugin.getLogger().warning("Menu couldn't load because of file is not exist or empty! (" + this.getFile().getName() + ")");
		}
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
		plc.addReplacer("page", this.getPage() + "");
	}
	
	public void previousPage() {
		if (this.getPage() > 1) {
			this.setPage(this.getPage() - 1);
		}else {
			this.setPage(1);
		}
		initItemFromConfig();
		update();
		plc.addReplacer("page", this.getPage() + "");
	}
	
	@SuppressWarnings("deprecation")
	public ItemStack convertItem(String p, Placeholder plc) {
		ItemStack item = new ItemStack(Material.AIR);
		
		String name = "";
		List<String> lore = new ArrayList<String>();
		int amount = 1;
		Material material = Material.AIR;
		
		try {
			name = plc.use(cm.getConfig().contains(p + ".Name") ? this.getConfig().getString(p + ".Name"): "");
			amount = cm.getConfig().contains(p + ".Amount") ? this.getConfig().getInt(p + ".Amount") : 1;
			lore = plc.useMass(cm.getConfig().contains(p + ".Lore") ? this.getConfig().getStringList(p + ".Lore") : new ArrayList<String>());
			
			material = XMaterial.valueOf(this.getConfig().getString(p + ".Material").toUpperCase()).parseMaterial();
			
			item = new ItemStack(material, amount);
			ItemMeta meta = item.getItemMeta();
			if (meta instanceof SkullMeta){
				SkullMeta skullMeta = (SkullMeta) meta;
				if (getOptions().containsKey("skin")){
					String skin = getOptions().get("skin");
					skullMeta.setOwner(skin);
					item.setItemMeta(skullMeta);
				}
				meta = item.getItemMeta();
			}
			if (!name.equals("")) {
				meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
			}if (lore.size() > 0) {
				lore = plc.translateColor(lore);
				lore = plc.useMass(lore);
				meta.setLore(plc.translateColor(lore));
			}
			item.setItemMeta(meta);
		}catch(Exception e) {
			System.out.println("Failed to initialize Item from path [" + p +"] (" + this.getFile().getName() + ")");
		}

		return item;
	}

	public void loadMenuData(){
		List<String> priorityItem = new ArrayList<>();
		for (String s : cm.getConfig().getConfigurationSection("menu-data").getKeys(false)){
			// Priority
			int priority = getPriorityLevel(s);
			if (getPriorityList().containsKey(priority)){
				priorityItem = getPriorityList().get(priority);
				priorityItem.add(s);
				getPriorityList().put(priority, priorityItem);
			}else{
				priorityItem.add(s);
				getPriorityList().put(priority, priorityItem);
			}
		}
	}

	public int getStartCountSlot(String dataPath){
		return cm.getConfig().contains("menu-data." + dataPath + ".Count") ? cm.getConfig().getInt("menu-data." + dataPath + ".Count") : -1;
	}

	public int getPriorityLevel(String dataPath){
		return cm.getConfig().contains("menu-data." + dataPath + ".Priority") ? cm.getConfig().getInt("menu-data." + dataPath + ".Priority") : 0;
	}

	
	public int getPageFromFile(String p) {
		return  cm.getConfig().contains(p + ".Page") ? this.getConfig().getInt(p + ".Page") : 1;
	}
	
	public int getMinPageFromFile(String p) {
		return  cm.getConfig().contains(p + ".Minimum-Page") ? this.getConfig().getInt(p + ".Minimum-Page") : 0;
	}
	
	public int getMaxPageFromFile(String p) {
		return  cm.getConfig().contains(p + ".Maximum-Page") ? this.getConfig().getInt(p + ".Maximum-Page") : 0;
	}
	
	public HashMap<String, Object> getConditionsFromFile(String p) {
		String dataPath = p.split("[.]")[1];
		HashMap<String, Object> map = new HashMap<String, Object>();
		for (String s : this.config.getStringList(p + ".Conditions")) {
			if (s.startsWith("VARIABLE:")) {
				s = s.replace("VARIABLE:", "");
				int i = -1;
				try {
					i = Integer.parseInt(s.split("=")[1]);
				}catch(Exception e) {}
				map.put(s.split("=")[0], i >= 0 ? i : s.split("=")[1]);
			}else if (s.startsWith("COUNT:")){
				s = s.replace("VARIABLE:", "");
				int count = -1;
				try {
					count = Integer.parseInt(s);
				}catch(Exception e) {}
				map.put(dataPath, count);
			}
		}
		return map; 
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
		List<String> path = new ArrayList<String>();

		for (int pr : getPriorityList().descendingKeySet()){
			List<String> pit = getPriorityList().get(pr);
			path.addAll(pit);
		}

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
			loadOptionsFromFile(p);

			HashMap<String, Integer> map = getCountSlot().containsKey(page) ? getCountSlot().get(page) : new HashMap<>();
			HashMap<String, Integer> cmap = getCurrentCountSlot().containsKey(page) ? getCurrentCountSlot().get(page) : new HashMap<>();
			HashMap<Integer, String> o = new HashMap<>();
			map.put(pt, getStartCountSlot(pt));
			cmap.put(pt, 0);
			getCountSlot().put(page, map);
			getOccupied().put(page, o);
			getCurrentCountSlot().put(page, cmap);

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

			slot.addAll(getInvDataSlot(p));
			
			item = convertItem(p, this.getPlaceholder());
			
			if (cm.getConfig().contains(p + ".Click-Type")) {
				for (int i : slot) {
					HashMap<ClickDataType, List<String>> data = new HashMap<ClickDataType, List<String>>();

					if (cm.getConfig().contains(p + ".Click-Type." + ClickDataType.ANY.toString())) {
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

			int count = Integer.parseInt(getCurrentCountSlot().get(page).get(pt) + "");
			for (int i : slot) {
				HashMap<Integer, String> oc = getOccupied().get(page);
				getCurrentCountSlot().get(page).put(pt, count);

				if (oc.containsKey(i)){continue;}
				if (getCurrentCountSlot().get(page).get(pt) == getCountSlot().get(page).get(pt)){break;}

				this.invdata.put(i, item);

				count++;
				oc.put(i, pt);
				getOccupied().put(page, oc);
			}
		}
	}
	
	public void inputObject(String path, Object obj) {
		this.customObject.put(path, obj);
	}
	
	public void setPage(int page) {
		this.page = page;
	}

	public void setCountSlot(int page, int maxCount, String path){
		HashMap<String, Integer> map = new HashMap<>();
		if (getCountSlot().containsKey(page)){
			map = getCountSlot().get(page);
		}
		map.put(path, maxCount);
		getCountSlot().put(page, map);
	}

	public void setCurrentCountSlot(int page, int maxCount, String path){
		HashMap<String, Integer> map = new HashMap<>();
		if (getCurrentCountSlot().containsKey(page)){
			map = getCurrentCountSlot().get(page);
		}
		map.put(path, maxCount);
		getCurrentCountSlot().put(page, map);
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

	public HashMap<String, String> getOptions() {
		return options;
	}

	public void loadOptionsFromFile(String path){
		for (String s : getConfigManager().getConfig().getConfigurationSection(path + ".Options").getKeys(false)){
			getOptions().put(s, getConfigManager().getConfig().getString(path + ".Options." + s));
		}
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

	public TreeMap<Integer, List<String>> getPriorityList() {
		return priorityList;
	}

	public TreeMap<Integer, HashMap<String, Integer>> getCountSlot() {
		return countSlot;
	}

	public TreeMap<Integer, HashMap<String, Integer>> getCurrentCountSlot() {
		return currentCountSlot;
	}

	public TreeMap<Integer, HashMap<Integer, String>> getOccupied() {
		return occupied;
	}

	public ConfigManager getConfigManager(){
		return cm;
	}
}
