package com.rehoukrel.diamail.utils.hologram;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class HologramContent {

    public enum HologramType{
        TEXT, ITEM
    }

    private HologramManager hologram;
    private int line;
    private ItemStack item;
    private String text;
    private HologramType type;
    private ArmorStand entity;
    private Item entityItem;

    public HologramContent(HologramManager holo){
        this.type = HologramType.TEXT;
        this.hologram = holo;
    }

    public HologramContent(HologramManager holo, HologramType type){
        this(holo);
        this.type = type;
    }

    @SuppressWarnings("deprecation")
    public void show(){
        this.line = getHologram().getContent().indexOf(this);
        if (getLine() == -1){return;}
        Location loc = getHologram().getContentLocation().get(getLine());
        World world = getHologram().getLocation().getWorld();
        ArmorStand h = world.spawn(loc, ArmorStand.class);
        h.setGravity(false);
        h.setCollidable(false);
        h.setVisible(false);
        this.entity = h;
        switch (getType()){
            case TEXT:
                h.setCustomName(getText());
                h.setCustomNameVisible(true);
                break;
            case ITEM:
                Item i = world.spawn(loc, Item.class);
                i.setItemStack(getItem());
                i.setGravity(false);
                i.setPickupDelay(999999999);
                i.setInvulnerable(true);
                this.entityItem = i;
                h.setPassenger(i);
                break;
        }
    }

    public void unshow(){
        this.line = getHologram().getContent().indexOf(this);
        switch(getType()){
            case TEXT:
                getArmorStand().remove();
                this.entity = null;
                break;
            case ITEM:
                getArmorStand().remove();
                getEntityItem().remove();
                this.entityItem = null;
                this.entity = null;
                break;
        }
    }

    public void remove(){
        unshow();
        getHologram().getContent().remove(this);
        getHologram().move(getHologram().getLocation());
    }

    //-----------


    public Entity getEntity() {
        switch (getType()){
            case TEXT:
                return this.entity;
            case ITEM:
                return this.entityItem;
        }
        return null;
    }

    public ArmorStand getArmorStand(){
        return this.entity;
    }

    public void setHologram(HologramManager hologram) {
        this.hologram = hologram;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setType(HologramType type) {
        this.type = type;
    }

    public HologramType getType() {
        return type;
    }

    public HologramManager getHologram() {
        return hologram;
    }

    public int getLine() {
        return line;
    }

    public ItemStack getItem() {
        return item;
    }

    public Item getEntityItem() {
        return entityItem;
    }

    public String getText() {
        return text;
    }
}
