package team.creativecode.diamail.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemManager {

	public static boolean hasAvaliableSlot(Player player, int howmanyclear){
		Inventory inv = player.getInventory();
		int check=0;
		
		for (ItemStack item: inv.getContents()) {
			if(item == null) {
				check++;
			}
		}
		
		if(check>=howmanyclear){
			return true;
		}else{
			return false;
		}
	}
	
    public static ItemStack generateItem(Material material, String displayname, List<String> lore){

        ItemStack item = new ItemStack(material);
        return generateItem(item, displayname, lore);
    }

    public static ItemStack generateItem(ItemStack item, String displayname, List<String> lore){
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayname));
        lore = DataConverter.colored(lore);
        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }

    @SuppressWarnings("deprecation")
    public static ItemStack generateItem(Material material,int amount, short damage, String displayname, List<String> lore){
        ItemStack item = new ItemStack(material, amount, damage);
        return generateItem(item, displayname, lore);
    }

    public static ItemStack generateItemFromRaw(File file, String path){
        ItemStack item = new ItemStack(Material.AIR);
        try{
            item = new ItemStack(Material.valueOf(ConfigManager.get(file, path + ".material").toString().toUpperCase()));
            ItemMeta meta = item.getItemMeta();
            if (ConfigManager.contains(file, path + ".amount")){
                item.setAmount(Integer.parseInt(ConfigManager.get(file, path + ".amount").toString()));
            }

            if (ConfigManager.contains(file, path + ".displayname")){
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', ConfigManager.get(file, path + ".displayname").toString()));
            }

            if (ConfigManager.contains(file, path + ".lore")){
                meta.setLore(DataConverter.colored(DataConverter.objectToList(ConfigManager.get(file, path + ".lore"))));
            }

            if (ConfigManager.contains(file, path + ".enchantments")){
                List<String> enchantment = new ArrayList<String>();
                enchantment = DataConverter.objectToList(ConfigManager.get(file, path + ".enchantments"));
                for (String en : enchantment){
                    String[] split = en.split(":");
                    int level = Integer.parseInt(split[1]);
                    Enchantment ench = Enchantment.getByKey(NamespacedKey.minecraft(split[0].toLowerCase()));
                    meta.addEnchant(ench, level, true);
                }
            }
            item.setItemMeta(meta);
        }catch (Exception e){e.printStackTrace();}
        return item;
    }

}
