package com.rehoukrel.diamail.utils;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public class BookManager {

    ItemStack book = new ItemStack(XMaterial.WRITTEN_BOOK.parseMaterial());
    BookMeta meta = (BookMeta) book.getItemMeta();

    public BookManager(){

    }

    public void updateBookMeta(){
        getBook().setItemMeta(getMeta());
    }

    public BookMeta getMeta() {
        return meta;
    }

    public ItemStack getBook() {
        return book;
    }
}
