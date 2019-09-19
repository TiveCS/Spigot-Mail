package com.rehoukrel.diamail.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.bukkit.*;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class DataConverter {
	
	public static void playSoundByString(Location loc, String text) {
		String[] split = text.split("-");
		String sound = split[0].toUpperCase();
		try {
			loc.getWorld().playSound(loc, Sound.valueOf(sound), Float.parseFloat(split[1]), Float.parseFloat(split[2]));
		}catch (Exception e){
			loc.getWorld().playSound(loc, XSound.matchXSound(sound).parseSound(), Float.parseFloat(split[1]), Float.parseFloat(split[2]));
		}
	}

	public static double randomDouble(double min, double max){
		double greater = min > max ? min : max, lower = min > max ? max : min;
		return lower + (greater - lower) * new Random().nextDouble();
	}

	public static TextComponent addMultipleLineComponent(List<String> list) {
		TextComponent com = new TextComponent("");
		for (String s : list) {
			if (com.getText().equals("")) {
				com.addExtra(s);
			}else {
				com.addExtra("\n");
				com.addExtra(s);
			}
		}
		return com;
	}

	public static String returnDecimalFormated(int decimalLength, double num){
		String m = "#0.";
		for (int i = 0; i < decimalLength; i++){
			m = m.concat("0");
		}
		NumberFormat f = new DecimalFormat(m);
		String text = f.format(num);
		while (text.endsWith("0")){
			text = text.substring(0, text.length() - 1);
		}
		if (text.endsWith(".")){
			text = text.substring(0, text.length() - 1);
		}
		return text;
	}

	public static boolean chance(double num){
		return num >= new Random().nextDouble() * 100;
	}

	public static boolean isLegacyVersion(){
		Material mat;
		try{
			mat = Material.getMaterial("RED_WOOL");
			return mat == null;
		}catch (Exception e){return true;}
	}

	public static int convertStringToInt(String text) {
		ScriptEngineManager sem = new ScriptEngineManager();
		ScriptEngine se = sem.getEngineByName("JavaScript");
		int result = 0;
		
		try {
			result = Integer.parseInt(se.eval(text).toString());
		} catch (NumberFormatException e) {

		} catch (ScriptException e) {

		}
		return result;
	}

	// Require calc(expression)
	public static String calculateString(String t, int formatLength){
		ScriptEngineManager sem = new ScriptEngineManager();
		ScriptEngine se = sem.getEngineByName("JavaScript");
		try {
			if (t.contains("calc(")) {
				StringBuilder b = new StringBuilder(t);
				int s, e, c = "calc(".length();
				s = b.indexOf("calc(");
				e = b.indexOf(")", s);
				String num = b.substring((s + c), e);
				b.delete(s, e + 1);
				num = returnDecimalFormated(formatLength, Double.parseDouble(se.eval(num).toString()));
				b.insert(s, num);
				if (b.toString().contains("calc(")){
					return calculateString(b.toString(), formatLength);
				}else {
					return b.toString();
				}
			}else{
				return t;
			}
		} catch (ScriptException e) {
			return t;
		}
	}
	
	public static double convertStringToDouble(String text) {
		ScriptEngineManager sem = new ScriptEngineManager();
		ScriptEngine se = sem.getEngineByName("JavaScript");
		double result = 0;
		
		try {
			result = Double.parseDouble(se.eval(text).toString());
		} catch (NumberFormatException e) {

		} catch (ScriptException e) {

		}
		return result;
	}
	
    public static List<String> colored(List<String> list){
        for (int i = 0; i < list.size(); i++){
            list.set(i, ChatColor.translateAlternateColorCodes('&', list.get(i)));
        }
        return list;
    }
    
    public static List<String> arrayToList(String[] array){
    	List<String> list = new ArrayList<String>();
    	for (String s : array) {
    		list.add(s);
    	}
    	return colored(list);
    }
    
    public static List<String> objectToList(Object object){
        List<String> list = new ArrayList<String>();
        String s = object.toString();
        s = s.substring(1);
        s = s.substring(0, s.length() - 1);
        String[] split = s.split(", ");

        for (String sp : split){
            list.add(sp);
        }
        return colored(list);
    }
    
    public static Object matchConvert(Object str) {
    	try {
    		return Integer.parseInt(str.toString());
    	}catch(Exception e) {}
    	try {
    		return Double.parseDouble(str.toString());
    	}catch(Exception e) {}
    	
    	return str;
    }
    
    public static List<String> combineList(List<String> first, List<String> second){
    	for (String s : second) {
    		first.add(s);
    	}
    	return first;
    }

	// ItemStack Serializer & Deserializer
	public static List<HashMap<Map<String, Object>, Map<String, Object>>> serializeItemStackList(final ItemStack[] itemStackList) {
		final List<HashMap<Map<String, Object>, Map<String, Object>>> serializedItemStackList = new ArrayList<HashMap<Map<String, Object>, Map<String, Object>>>();

		for (ItemStack itemStack : itemStackList) {
			Map<String, Object> serializedItemStack, serializedItemMeta;
			HashMap<Map<String, Object>, Map<String, Object>> serializedMap = new HashMap<Map<String, Object>, Map<String, Object>>();

			if (itemStack == null) itemStack = new ItemStack(Material.AIR);
			serializedItemMeta = (itemStack.hasItemMeta())
					? itemStack.getItemMeta().serialize()
					: null;
			itemStack.setItemMeta(null);
			serializedItemStack = itemStack.serialize();

			serializedMap.put(serializedItemStack, serializedItemMeta);
			serializedItemStackList.add(serializedMap);
		}
		return serializedItemStackList;
	}

	public static HashMap<Map<String, Object>, Map<String, Object>> serializeItemStack(ItemStack itemStack) {
		final HashMap<Map<String, Object>, Map<String, Object>> serializedItemStackList = new HashMap<Map<String, Object>, Map<String, Object>>();

		Map<String, Object> serializedItemStack, serializedItemMeta;
		HashMap<Map<String, Object>, Map<String, Object>> serializedMap = new HashMap<Map<String, Object>, Map<String, Object>>();

		if (itemStack == null) itemStack = new ItemStack(Material.AIR);
		serializedItemMeta = (itemStack.hasItemMeta())
				? itemStack.getItemMeta().serialize()
				: null;
		itemStack.setItemMeta(null);
		serializedItemStack = itemStack.serialize();

		serializedMap.put(serializedItemStack, serializedItemMeta);

		return serializedItemStackList;
	}

	public static ItemStack[] deserializeItemStackList(final List<HashMap<Map<String, Object>, Map<String, Object>>> serializedItemStackList) {
		final ItemStack[] itemStackList = new ItemStack[serializedItemStackList.size()];

		int i = 0;
		for (HashMap<Map<String, Object>, Map<String, Object>> serializedItemStackMap : serializedItemStackList) {
			Map.Entry<Map<String, Object>, Map<String, Object>> serializedItemStack = serializedItemStackMap.entrySet().iterator().next();

			ItemStack itemStack = ItemStack.deserialize(serializedItemStack.getKey());
			if (serializedItemStack.getValue() != null) {
				ItemMeta itemMeta = (ItemMeta) ConfigurationSerialization.deserializeObject(serializedItemStack.getValue(), ConfigurationSerialization.getClassByAlias("ItemMeta"));
				itemStack.setItemMeta(itemMeta);
			}

			itemStackList[i++] = itemStack;
		}
		return itemStackList;
	}

	public static ItemStack deserializeItemStack(HashMap<Map<String, Object>, Map<String, Object>> serializedItemStackList) {
		ItemStack itemStackList = null;

		Map.Entry<Map<String, Object>, Map<String, Object>> serializedItemStack = serializedItemStackList.entrySet().iterator().next();

		ItemStack itemStack = ItemStack.deserialize(serializedItemStack.getKey());
		if (serializedItemStack.getValue() != null) {
			ItemMeta itemMeta = (ItemMeta) ConfigurationSerialization.deserializeObject(serializedItemStack.getValue(), ConfigurationSerialization.getClassByAlias("ItemMeta"));
			itemStack.setItemMeta(itemMeta);
		}

		itemStackList = itemStack;

		return itemStackList;
	}
	//====================================

}
