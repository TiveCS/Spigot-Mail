package com.rehoukrel.diamail.utils.hologram;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.util.*;

public class HologramManager {

    private final double difference = 0.23;

    private UUID uuid;
    private List<HologramContent> content = new LinkedList<>();
    private List<Location> contentLocation = new LinkedList<>();
    private Location location;
    private boolean activated;

    public HologramManager(Location location, String... text){
        this.location = location;
        this.uuid = UUID.randomUUID();
        this.activated = false;
        if (text.length > 0) {
            for (String s : text) {
                HologramContent c = new HologramContent(this, HologramContent.HologramType.TEXT);
                c.setText(ChatColor.translateAlternateColorCodes('&', s));
                registerContent(c);
            }
        }
    }

    public void show(){
        if (!activated){
            for (HologramContent content : getContent()){
                content.show();
            }
            this.activated = true;
        }
    }

    public void destroy(){
        if (activated){
            for (HologramContent content : getContent()){
                if (content.getEntity() != null) {
                    content.getEntity().remove();
                }
            }
            this.activated = false;
        }
    }

    public void toggle(){
        if (activated){
            destroy();
        }else{
            show();
        }
    }

    public void move(Location newLocation){
        this.location = newLocation;
        generateNewLocation(newLocation);
        for (HologramContent content : getContent()){
            if (content.getEntity() != null) {
                content.unshow();
            }
        }
        activated = false;
        show();
    }

    public void generateNewLocation(Location loc){
        this.location = loc;
        this.contentLocation.clear();
        for (int i = 0; i < this.content.size(); i++) {
            Location l = getLocation().clone().add(0, (difference + 0.5)*i, 0);
            this.contentLocation.add(l);
        }
    }

    public void registerContent(HologramContent content, Location loc){
        this.content.add(content);
        this.contentLocation.add(loc);
    }

    public void registerContent(HologramContent content){
        this.content.add(content);
        Location l = getLocation().clone().add(0,((this.content.size() / 2)*difference),0);
        this.contentLocation.add(l);
    }

    public UUID getUuid() {
        return uuid;
    }

    public List<HologramContent> getContent() {
        return content;
    }

    public Location getLocation() {
        return location;
    }

    public boolean isActivated() {
        return activated;
    }

    public List<Location> getContentLocation() {
        return contentLocation;
    }
}

