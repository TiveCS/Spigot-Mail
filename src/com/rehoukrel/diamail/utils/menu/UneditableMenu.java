package com.rehoukrel.diamail.utils.menu;

import com.rehoukrel.diamail.utils.ConfigManager;
import com.rehoukrel.diamail.utils.DataConverter;
import com.rehoukrel.diamail.utils.XMaterial;
import com.rehoukrel.diamail.utils.language.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class UneditableMenu {

    public static HashMap<Player, UneditableMenu> menus = new HashMap<>();

    private Inventory menu;
    private HashMap<Integer, HashMap<Integer, ItemStack>> inventoryData = new HashMap<>();
    private HashMap<String, ItemStack> fileInventoryData = new HashMap<>();
    private int page = 1;
    private JavaPlugin plugin;

    private File folder, file;
    private ConfigManager configFile;

    public enum MenuDataPath{
        MATERIAL("material"), NAME("name"), LORE("lore"), ENCHANTMENTS("enchantments"), SKIN("options.skin");

        String path;

        MenuDataPath(String path){
            this.path = path;
        }

        public String getPath() {
            return path;
        }

        @Override
        public String toString() {
            return path;
        }
    }

    public UneditableMenu(JavaPlugin plugin, int rows){
        this(plugin, rows, null);
    }

    public UneditableMenu(JavaPlugin plugin, int rows, String title){
        this.plugin = plugin;
        loadConstructorData(rows, title);
    }

    // Misc
    public HashMap<Integer, ItemStack> slotItem(ItemStack item, List<Integer> slots){
        HashMap<Integer, ItemStack> map = new HashMap<>();
        for (int s : slots){
            map.put(s, item);
        }
        return map;
    }

    public HashMap<Integer, ItemStack> slotItem(ItemStack item, int... slots){
        HashMap<Integer, ItemStack> map = new HashMap<>();
        for (int s : slots){
            map.put(s, item);
        }
        return map;
    }

    // Setting

    private void loadConstructorData(int rows, String title){
        if (this.plugin == null){System.out.println("[" + this.getClass().getSimpleName() +"] Failed to load UneditableMenu. Plugin value is null."); return;}
        if (title != null){
            this.menu = Bukkit.createInventory(null, rows*9, ChatColor.translateAlternateColorCodes('&', title));
        }else{
            this.menu = Bukkit.createInventory(null, rows*9);
        }
        this.folder = new File(plugin.getDataFolder(), "Menu");
        this.file = new File(plugin.getDataFolder() + "/Menu", this.getClass().getSimpleName() + ".yml");
        if (!getFolder().exists()){
            getFolder().mkdir();
        }else{
            this.configFile = new ConfigManager(getFile());
        }
    }

    public void addItemData(String path, ItemStack item, String name, List<String> lore, HashMap<Enchantment, Integer> enchants){
        String p = "menu-data." + path;
        getConfigFile().init(p + "." + MenuDataPath.MATERIAL.getPath(), item.getType().name());
        ItemMeta meta = item.getItemMeta();
        if (name != null) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        }
        if (lore != null) {
            meta.setLore(DataConverter.colored(lore));
        }
        if (enchants != null) {
            item.addEnchantments(enchants);
        }
        if (meta.hasDisplayName()) {
            getConfigFile().init(p + "." + MenuDataPath.NAME.getPath(), meta.getDisplayName());
        }
        if (meta.hasLore()) {
            getConfigFile().init(p + "." + MenuDataPath.LORE.getPath(), meta.getLore());
        }
        if (meta.hasEnchants()){
            String pe = p + "." + MenuDataPath.ENCHANTMENTS.getPath();
            List<String> ench = new ArrayList<>();
            for (Enchantment en : meta.getEnchants().keySet()){
                ench.add(en.getKey().getNamespace() + ":" + meta.getEnchants().get(en));
            }
            getConfigFile().init(p + "." + MenuDataPath.ENCHANTMENTS.getPath(), ench);
        }
        getConfigFile().saveConfig();
    }

    public void addItemData(String path, Material material, String name, List<String> lore, HashMap<Enchantment, Integer> enchants){
        addItemData(path, new ItemStack(material), name, lore, enchants);
    }

    @SuppressWarnings("deprecation")
    public ItemStack loadItemDataFromFile(String path, Placeholder plc){
        ItemStack item = null;
        String p = "menu-data." + path;
        if (getConfigFile().contains(p)){
            String material = "AIR";
            if (getConfigFile().contains(p + "." + MenuDataPath.MATERIAL.getPath())){
                material = getConfigFile().getConfig().getString(p + "." + MenuDataPath.MATERIAL.getPath());
            }else{
                return null;
            }
            item = new ItemStack(XMaterial.valueOf(material).parseMaterial());
            if (!item.getType().equals(Material.AIR)) {
                ItemMeta meta = item.getItemMeta();
                if (meta instanceof SkullMeta){
                    SkullMeta skullMeta = (SkullMeta) meta;
                    if (getConfigFile().contains(p + "." + MenuDataPath.SKIN.getPath())){
                        if (DataConverter.isLegacyVersion()) {
                            skullMeta.setOwner(getConfigFile().getConfig().getString(p + "." + MenuDataPath.SKIN.getPath()));
                        }else{
                            skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(getConfigFile().getConfig().getString(p + "." + MenuDataPath.SKIN.getPath())));
                        }
                        meta = skullMeta;
                    }
                }
                if (getConfigFile().contains(p + "." + MenuDataPath.NAME.getPath())) {
                    meta.setDisplayName(plc.use(ChatColor.translateAlternateColorCodes('&', getConfigFile().getConfig().getString(p + "." + MenuDataPath.NAME.getPath()))));
                }
                if (getConfigFile().contains(p + "." + MenuDataPath.LORE.getPath())) {
                    meta.setLore(plc.useMass(plc.useListReplacer(DataConverter.colored(getConfigFile().getConfig().getStringList(p + "." + MenuDataPath.LORE.getPath())))));
                }
                item.setItemMeta(meta);
                if (getConfigFile().contains(p + "." + MenuDataPath.ENCHANTMENTS.getPath())) {
                    for (String s : getConfigFile().getConfig().getStringList(p + "." + MenuDataPath.ENCHANTMENTS.getPath())) {
                        Enchantment enchantment = null;
                        int level = 0;
                        try {
                            String[] spt = s.split(":");
                            level = Integer.parseInt(spt[1]);
                            if (DataConverter.isLegacyVersion()) {
                                enchantment = Enchantment.getByName(spt[0]);
                            } else {
                                enchantment = Enchantment.getByKey(NamespacedKey.minecraft(spt[0]));
                            }
                            try {
                                item.addUnsafeEnchantment(enchantment, level);
                            } catch (Exception e) {
                                System.out.println("[" + this.getClass().getSimpleName() + "] Menu item can't load!  (ERROR: ENCHANTMENT, " + p + ")");
                            }
                        } catch (Exception e) {
                            System.out.println("[" + this.getClass().getSimpleName() + "] Menu item can't load!  (ERROR: FALSE ARGUMENT[level:" + level + ", Enchant: " + enchantment + "], " + p + ")");
                        }
                    }
                }
            }
        }
        getFileInventoryData().put(path, item);
        return plc.useItemStack(item);
    }

    public void setPlugin(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public ConfigManager getConfigFile() {
        return configFile;
    }

    public File getFolder() {
        return folder;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void setMenu(Inventory menu) {
        this.menu = menu;
    }

    public void setInventoryData(HashMap<Integer, HashMap<Integer, ItemStack>> inventoryData) {
        this.inventoryData = inventoryData;
    }

    public void addInventoryData(int page, HashMap<Integer, ItemStack> itemSlot){
        this.inventoryData.put(page, itemSlot);
    }

    //----------------------
    // Action

    public void loadInventoryDataFromFile(){
        if (getInventoryData().isEmpty()){
            getInventoryData().put(0, new HashMap<>());
        }
        for (String p : getConfigFile().getConfig().getConfigurationSection("menu-data").getKeys(false)){
            loadItemDataFromFile(p, new Placeholder());
        }
    }

    public void loadInventoryData(int page){
        getMenu().clear();
        HashMap<Integer, ItemStack> itemSlot = getInventoryData().containsKey(page) ? getInventoryData().get(page) : new HashMap<>(),
                base = getInventoryData().containsKey(0) ? getInventoryData().get(0) : new HashMap<>();
        for (int slot : base.keySet()){
            getMenu().setItem(slot, base.get(slot));
        }
        for (int slot : itemSlot.keySet()){
            getMenu().setItem(slot, itemSlot.get(slot));
        }
    }

    public void previousPage(Player p){
        if (getPage() > 1) {
            setPage(getPage() - 1);
        }else{
            setPage(1);
        }
        open(p);
    }

    public void nextPage(Player p){
        setPage(getPage() + 1);
        open(p);
    }

    public void open(Player p){
        loadInventoryData(getPage());

        p.openInventory(getMenu());
        menus.put(p, this);
    }

    public abstract void actionClick(InventoryClickEvent event);

    //----------------------
    // Getter

    public HashMap<String, ItemStack> getFileInventoryData() {
        return fileInventoryData;
    }

    public int getPage() {
        return page;
    }

    public HashMap<Integer, HashMap<Integer, ItemStack>> getInventoryData() {
        return inventoryData;
    }

    public Inventory getMenu() {
        return menu;
    }
}
