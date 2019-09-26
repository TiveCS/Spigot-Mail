package com.rehoukrel.diamail.menu;

import com.rehoukrel.diamail.DiaMail;
import com.rehoukrel.diamail.api.manager.PlayerSetting;
import com.rehoukrel.diamail.utils.XMaterial;
import com.rehoukrel.diamail.utils.menu.UneditableMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;

public class SettingMenu extends UneditableMenu implements Listener {

    public static DiaMail plugin = DiaMail.getPlugin(DiaMail.class);

    PlayerSetting setting;

    public SettingMenu(PlayerSetting playerSetting) {
        super(plugin, 4, "Player Setting");

        this.setting = playerSetting;
        getPlugin().getServer().getPluginManager().registerEvents(this, plugin);

        init();
    }

    @EventHandler
    public void onClick(InventoryClickEvent event){
        if (event.getClickedInventory() != null) {
            if (event.getClickedInventory().equals(getMenu())) {
                actionClick(event);
            }
        }
    }

    public void init(){
        addItemData("border", XMaterial.BLACK_STAINED_GLASS_PANE.parseMaterial(), " ", new ArrayList<>(), new HashMap<>());
        addItemData("next-page", XMaterial.GREEN_STAINED_GLASS_PANE.parseMaterial(), "&aNext Page", new ArrayList<>(), new HashMap<>());
        addItemData("previous-page", XMaterial.LIGHT_BLUE_STAINED_GLASS_PANE.parseMaterial(), "&bPrevious Page", new ArrayList<>(), new HashMap<>());

        addItemData("language", XMaterial.BOOK.parseMaterial(), "&eLanguage &6%setting_language%", new ArrayList<>(), new HashMap<>());
        addItemData("use-sound", XMaterial.ANVIL.parseMaterial(), "&eUse Sound &6%setting_use-sound%", new ArrayList<>(), new HashMap<>());
        addItemData("reminder", XMaterial.OAK_SIGN.parseMaterial(), "&eReminder &6%setting_reminder%", new ArrayList<>(), new HashMap<>());

    }

    public PlayerSetting getSetting() {
        return setting;
    }

    // not complete
    public void loadMenu(){
        HashMap<Integer, ItemStack> inv = new HashMap<Integer, ItemStack>();

        inv.putAll(slotItem(getFileInventoryData().get("border"), 0,1,2,3,4,5,6,7,8, 27,28,29,30,31,32,33,34,35));
        if (getSetting().getSettings().size() > 2*7) {
            inv.putAll(slotItem(getFileInventoryData().get("next-page"), 17, 26));
        }
        if (getPage() > 1) {
            inv.putAll(slotItem(getFileInventoryData().get("previous-page"), 9, 18));
        }

        addInventoryData(0, new HashMap<>(inv));
    }

    @Override
    public void open(Player p) {
        loadMenu();

        super.open(p);
    }

    @Override
    public void actionClick(InventoryClickEvent event) {

    }
}
