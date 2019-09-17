package com.rehoukrel.diamail.utils;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class ItemBuilder {

    private ItemStack item;
    private Plugin plugin;
    private ItemMeta meta;

    public ItemBuilder(Plugin plugin, ItemStack item){
        this.item = item;
        this.plugin = plugin;
        this.meta = item.getItemMeta();
    }

    public ItemBuilder(ItemStack item){
        this.item = item;
        this.meta =  item.getItemMeta();
    }

    public ItemBuilder(ItemStack item, String displayName){
        this.item = item;
        this.meta = item.getItemMeta();
        setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
    }

    public void setDisplayName(String displayName){
        getMeta().setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
    }

    public void setUnbreakable(boolean state){
        getMeta().setUnbreakable(state);
    }

    public void setLore(List<String> l){
        getMeta().setLore(DataConverter.colored(l));
    }

    public void insertLore(List<String> l, int insertAt){
        if (getMeta().hasLore()){
            getMeta().getLore().addAll(insertAt, l);
        }else{
            List<String> newLore = new ArrayList<String>();
            for (int i = 0; i < insertAt; i++){
                newLore.add(" ");
            }
            newLore.addAll(l);
            getMeta().setLore(DataConverter.colored(newLore));
        }
    }

    public void setLoreAt(String s, int index){
        if (getMeta().hasLore()){
            int size = (getMeta().getLore().size() < index + 1 ? index + 1 : getMeta().getLore().size());
            for (int i = 0; i < size; i++){
                if (i == index){
                    getMeta().getLore().set(index, s);
                    break;
                }
                else if (getMeta().getLore().size() < index + 1){
                    getMeta().getLore().add(" ");
                }
            }
        }else{
            List<String> list = new ArrayList<String>();
            for (int i = 0; i < index + 1; i++){
                if (i == index){
                    list.add(s);
                    break;
                }else{
                    list.add(" ");
                }
            }
        }
    }

    public ItemBuilder finish(){
        getItem().setItemMeta(getMeta());
        return this;
    }

    public ItemMeta getMeta() {
        return meta;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public ItemStack getItem() {
        return item;
    }
}
