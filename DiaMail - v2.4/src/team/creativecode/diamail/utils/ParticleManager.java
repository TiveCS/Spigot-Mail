package team.creativecode.diamail.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;

import team.creativecode.diamail.Main;

public class ParticleManager {
	
	static Main plugin = Main.getPlugin(Main.class);
	
	public static void particle(Location loc, Particle particle, int amount) {
		double x = loc.getX(), y = loc.getY(), z = loc.getZ();
		
		loc.getWorld().spawnParticle(particle, x,y,z , 1, 0,0,0, 0);
	}
	
	public static void circle(Location loc, double radius, Particle particle) {
		
		for (int degree = 0; degree < 360; degree+=10) {
			double radian = Math.toRadians(degree);
			double zet = radius*Math.sin(radian), ex = radius*Math.cos(radian);
			
			double x = loc.getX()+ex, y = loc.getY(), z = loc.getZ()+zet;
			
			loc.getWorld().spawnParticle(particle, x,y,z , 1, 0,0,0, 0);
		}
	}
	
	public static void circle(Location loc, double radius, Particle particle, boolean animation) {
		for (int degree = 0; degree < 360; degree+=10) {
			double radian = Math.toRadians(degree);
			double zet = radius*Math.sin(radian), ex = radius*Math.cos(radian);
			
			if (animation == false) {
				double x = loc.getX()+ex, y = loc.getY(), z = loc.getZ()+zet;
				loc.getWorld().spawnParticle(particle, x,y,z , 1, 0,0,0, 0);
			}else {
				Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
					double x = loc.getX()+ex, y = loc.getY(), z = loc.getZ()+zet;
					@Override
					public void run() {
						loc.getWorld().spawnParticle(particle, x,y,z , 1, 0,0,0, 0);
					}
					
				}, 3);
			}
		}
	}
	
	public static void trail(LivingEntity entity, Particle particle, int duration, int spacer) {
		for (int i = 0; i < duration; i+=spacer) {
			Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {

				@Override
				public void run() {
					Location loc = entity.getLocation();
					double x = loc.getX(), y = loc.getY(), z = loc.getZ();
					
					loc.getWorld().spawnParticle(particle, x,y,z , 1, 0,0,0, 0);
				}
				
			}, duration - i);
		}
		
	}

}
