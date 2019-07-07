package com.rehoukrel.diamail.utils;

import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class ParticleManager {

    private Location location;
    private Plugin plugin;
    private Entity entity;
    private double offsetX = 0, offsetY = 0, offsetZ = 0;

    public ParticleManager(Plugin plugin, Location loc){
        this.location = loc;
        this.plugin = plugin;
    }

    public ParticleManager(Plugin plugin){
        this.plugin = plugin;
    }

    public ParticleManager(Plugin plugin, Entity entity){
        this(plugin, entity.getLocation());
        this.entity = entity;
    }

    public ParticleManager(Plugin plugin, Location loc, double offsetX, double offsetY, double offsetZ){
        this.plugin = plugin;
        this.location = loc;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
    }

    public void dotParticle(Particle particle, int amount, double speed, int red, int green, int blue, Object... obj){
        try {
            if (particle.equals(Particle.REDSTONE)){
                Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(red, green, blue), 1);
                getWorld().spawnParticle(particle, getLocation().add(offsetX, offsetY, offsetZ), amount, dustOptions);
            }else if (particle.equals(Particle.SPELL_MOB)) {
                double r = red / 255D;
                double g = green / 255D;
                double b = blue / 255D;
                getWorld().spawnParticle(particle, getLocation().add(offsetX, offsetY, offsetZ), amount, r, g, b, speed);
            }else if (particle.equals(Particle.NOTE)) {
                double note = red + green + blue / 24D;
                getWorld().spawnParticle(particle, getLocation().add(offsetX, offsetY, offsetZ), amount, note,0,0, 1);
            }else if (particle.equals(Particle.BLOCK_CRACK) || particle.equals(Particle.BLOCK_DUST) || particle.equals(Particle.FALLING_DUST)){
                Material b;
                if (obj[0] instanceof Material){
                    b = (Material) obj[0];
                }else{
                    b = ((ItemStack) obj[0]).getType();
                }
                blockCrack(amount, b);
            }else if (particle.equals(Particle.ITEM_CRACK)){
                ItemStack item;
                if (obj[0] instanceof Material){
                    item = new ItemStack((Material) obj[0]);
                }else{
                    item = (ItemStack) obj[0];
                }
                getWorld().spawnParticle(Particle.ITEM_CRACK, getLocation(), amount, item);
            }
            else{
                getWorld().spawnParticle(particle, getLocation().add(offsetX, offsetY, offsetZ), amount, 0, 0, 0, speed);
            }

        }catch(Exception e){
            if (particle.equals(Particle.NOTE)) {
                double note = red + green + blue / 24D;
                getWorld().spawnParticle(particle, getLocation().add(offsetX, offsetY, offsetZ), amount, note,0,0, 1);
            }else {
                getWorld().spawnParticle(particle, getLocation().add(offsetX, offsetY, offsetZ), amount, 0, 0, 0, speed);
            }
        }
    }

    public void dotParticle(Particle particle, int amount, int red, int green, int blue){
        dotParticle(particle, amount, 0, red, green, blue);
    }

    public void trails(Particle particle, Entity entity,int particleAmount, long duration, int durationIncrement){
        for (int i = 0; i < duration; i+=durationIncrement){
            Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () ->{
                this.location = entity.getLocation();
                dotParticle(particle, particleAmount, 0, 0, 0);
            }, i);
        }
    }

    public void trailsBlockCrack(Material material, Entity entity, int particleAmount, long duration, int durationIncrement){
        for (int i = 0; i < duration; i+=durationIncrement){
            Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () ->{
                this.location = entity.getLocation();
                try {
                    blockCrack(particleAmount, material);
                }catch(Exception e){}
            }, i);
        }
    }

    public void legacyBlockCrack(int amount, Material material){
        getWorld().spawnParticle(Particle.BLOCK_CRACK, getLocation(), amount, new ItemStack(material));
    }

    public void blockCrack(int amount, Material material){
        BlockData i = material.createBlockData();
        getWorld().spawnParticle(Particle.BLOCK_CRACK, getLocation(), amount, i);
    }

    public void circle(Particle particle, double radius, double increment,long duration, int tickDelayPerIncrement){
        int now = 0;
        for (double degree = 0; degree < 360; degree+=increment){
            double radian = Math.toRadians(degree);
            double dZ = radius*Math.sin(radian), dX = radius*Math.cos(radian);
            double x = getLocation().getX() + getOffsetX() + dX, z = getLocation().getZ() + getOffsetZ() + dZ;
            Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, new Runnable() {
                @Override
                public void run() {
                    Location nLoc = new Location(getWorld(), x*dX, getLocation().getY(), z*dZ);
                    dotParticle(particle, 1, 0,0,0);
                }
            }, now);
            if (now < duration){
                now+= tickDelayPerIncrement;
            }
        }
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setOffsetX(double offsetX) {
        this.offsetX = offsetX;
    }

    public void setOffsetY(double offsetY) {
        this.offsetY = offsetY;
    }

    public void setOffsetZ(double offsetZ) {
        this.offsetZ = offsetZ;
    }

    public boolean isLegacy(){
        Material mat;
        try{
            mat = Material.getMaterial("RED_WOOL");
        }catch(Exception e){return true;}
        return mat == null;
    }

    public World getWorld(){
        return getLocation().getWorld();
    }

    public Location getLocation() {
        return location;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public double getOffsetX() {
        return offsetX;
    }

    public double getOffsetY() {
        return offsetY;
    }

    public double getOffsetZ() {
        return offsetZ;
    }
}
