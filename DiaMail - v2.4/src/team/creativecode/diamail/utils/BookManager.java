package team.creativecode.diamail.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import net.md_5.bungee.api.chat.BaseComponent;

public class BookManager {
	
	private ItemStack book;
	private BookMeta meta;
	
	public BookManager(boolean writable) {
		if (writable) {
			book = new ItemStack(Material.WRITABLE_BOOK);
		}else {
			book = new ItemStack(Material.WRITTEN_BOOK);
		}
		meta = (BookMeta) book.getItemMeta();
	}
	
	public BaseComponent[] getComponentPage(int page) {
		return getMeta().spigot().getPage(page);
	}
	
	public String getNormalPage(int page){
		return getMeta().getPage(page);
	}
	
	public ItemStack finish() {
		getBook().setItemMeta(getMeta());
		return getBook();
	}
	
	public ItemStack getBook() {
		return book;
	}
	
	public BookMeta getMeta() {
		return meta;
	}

}
