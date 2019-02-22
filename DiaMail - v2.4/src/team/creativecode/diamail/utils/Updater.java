package team.creativecode.diamail.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.bukkit.Bukkit;

import team.creativecode.diamail.Main;

public class Updater{
	
	public static String spigot = "http://www.spigotmc.org/api/general.php";
	
	public static boolean isNewVersion() {
		return !Main.version.equals(getUpdateCheck(Main.rsid));
	}
	
	public static String getUpdateCheck(int rsid) {
			
		String nv = "";
		
		try {
			HttpsURLConnection connection = (HttpsURLConnection) new URL("https://api.spigotmc.org/legacy/update.php?resource=" + rsid).openConnection();
            int timed_out = 1250; 
            connection.setConnectTimeout(timed_out);
            connection.setReadTimeout(timed_out);
            nv = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();
            connection.disconnect();
		} catch (IOException e) {
			Bukkit.getServer().getConsoleSender().sendMessage("[" + Main.getPlugin(Main.class).getDescription().getName() + "] Cannot connect to update server!");
			e.printStackTrace();
		} 
		
		return nv;
		
	}

}
