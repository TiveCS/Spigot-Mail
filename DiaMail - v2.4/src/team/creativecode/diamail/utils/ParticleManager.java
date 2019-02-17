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
	
	public static void atomic(Location loc, double radius, Particle particle, boolean animation) {
		
		int maxtime = 0;
		for (int time = 0; time < 4; time++) {
			final int t = time;
			for (int degree = 0; degree < 360; degree+=10) {
				double radian = Math.toRadians(degree);
				double zet = radius*Math.sin(radian), ex = radius*Math.cos(radian);
				
				if (animation == false) {
					double tan = 0;
					if (time == 0) {
						tan = radius*Math.sin(radian);
					}
					else if (time == 1) {
						tan = radius*Math.asin(radian);
					}
					else if (time == 2) {
						tan = radius*Math.cos(radian);
					}
					else if (time ==3) {
						tan = radius*Math.acos(radian);
					}
					double x = loc.getX()+ex, y = loc.getY()+tan, z = loc.getZ()+zet;
					loc.getWorld().spawnParticle(particle, x,y,z , 1, 0,0,0, 0);
				}if (animation == true) {
					Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
		
						@Override
						public void run() {
							double tan = 0;
							if (t == 0) {
								tan = radius*Math.sin(radian);
							}
							else if (t == 1) {
								tan = radius*Math.asin(radian);
							}
							else if (t == 2) {
								tan = radius*Math.cos(radian) + 0.5;
							}
							else if (t ==3) {
								tan = radius*Math.acos(radian) + 0.5;
							}
							double x = loc.getX()+ex, y = loc.getY()+tan, z = loc.getZ()+zet;
							loc.getWorld().spawnParticle(particle, x,y,z , 1, 0,0.2,0, 0);
						}
						
					}, maxtime);
				}
				maxtime++;
			}
		}
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
