package com.rehoukrel.diamail.menu;

import com.rehoukrel.diamail.DiaMail;
import com.rehoukrel.diamail.utils.menu.UneditableMenu;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class MailboxMenu extends UneditableMenu implements Listener {

    public static DiaMail plugin = DiaMail.getPlugin(DiaMail.class);

    public MailboxMenu() {
        super(plugin, 6);
        if (plugin != null) {
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
        }else{
            System.out.println("[" + this.getClass().getSimpleName() + "] Failed to register event.");
        }

        HashMap<Integer, ItemStack> map = new HashMap<>();

        addInventoryData(0, map);
        map.clear();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        if (event.getClickedInventory().equals(getMenu())){
            actionClick(event);
        }
    }

    @Override
    public void actionClick(InventoryClickEvent event) {

    }
}
