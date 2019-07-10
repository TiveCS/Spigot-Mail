package com.rehoukrel.diamail.menu;

import com.rehoukrel.diamail.DiaMail;
import com.rehoukrel.diamail.api.manager.Mail;
import com.rehoukrel.diamail.api.manager.MailType;
import com.rehoukrel.diamail.utils.DataConverter;
import com.rehoukrel.diamail.utils.XMaterial;
import com.rehoukrel.diamail.utils.language.Placeholder;
import com.rehoukrel.diamail.utils.menu.UneditableMenu;
import com.rehoukrel.diamail.utils.nms.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MailEditor extends UneditableMenu implements Listener {

    public static DiaMail plugin = DiaMail.getPlugin(DiaMail.class);
    private Mail mail;
    private int addMessageSlot, addReceiverSlot;
    private List<Integer> messageSlot = new ArrayList<>(), receiverSlot = new ArrayList<>();

    public MailEditor(Mail mail) {
        super(plugin, 6);
        this.mail = mail;
        if (plugin != null) {
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
        }else{
            System.out.println("[" + this.getClass().getSimpleName() + "] Failed to register event.");
        }
    }

    @SuppressWarnings("deprecation")
    public void load(){
        addItemData("border", XMaterial.BLACK_STAINED_GLASS_PANE.parseMaterial(), " ", new ArrayList<>(), new HashMap<>());
        addItemData("send", XMaterial.LIME_TERRACOTTA.parseMaterial(), "&a&lSend Mail", new ArrayList<>(), new HashMap<>());
        addItemData("sendall-true", XMaterial.LIME_WOOL.parseMaterial(), "&3&lSend All&7: &a&lTrue", new ArrayList<>(), new HashMap<>());
        addItemData("sendall-false", XMaterial.RED_WOOL.parseMaterial(), "&3&lSend All&7: &c&lFalse", new ArrayList<>(), new HashMap<>());
        addItemData("sendall-unpermitted", XMaterial.BARRIER.parseMaterial(), "&c&lCannot use send all", new ArrayList<>(), new HashMap<>());
        addItemData("cancel", XMaterial.TNT.parseMaterial(), "&c&lCancel Sending", new ArrayList<>(), new HashMap<>());
        addItemData("receiver", XMaterial.PLAYER_HEAD.parseMaterial(), "&e%receiver%", new ArrayList<>(), new HashMap<>());
        addItemData("add-receiver", XMaterial.SKELETON_SKULL.parseMaterial(), "&6Add receiver", Arrays.asList(" ", "&fClick here to add"), new HashMap<>());
        addItemData("message", XMaterial.PAPER.parseMaterial(), "&cRemove message &7(Line #%message_line%)", Arrays.asList(" ", "&f%message%"), new HashMap<>());
        addItemData("add-message", XMaterial.WRITABLE_BOOK.parseMaterial(), "&bAdd message &7(Line #%message_line%)", new ArrayList<>(), new HashMap<>());
        addItemData("error", XMaterial.FIRE.parseMaterial(), "&4&lERROR", new ArrayList<>(), new HashMap<>());

        HashMap<Integer, ItemStack> map = new HashMap<>();
        loadInventoryDataFromFile();
        map.putAll(slotItem(getFileInventoryData().get("border"), 7, 16, 25, 34, 43, 52, 18, 19, 20, 21, 22, 23, 24, 30,39,48));
        map.put(8, getFileInventoryData().get("send"));
        map.put(53, getFileInventoryData().get("cancel"));

        // Message
        int count = 0;
        boolean end = false;
        for (int i = 0; i < 1; i++){
            for (int o = 0; o < 7; o++){
                int slot = o + i*9;
                Placeholder plc = new Placeholder();
                plc.addReplacer("message_line", (count + 1) + "");
                if (count < getMail().getMessages().size()){
                    plc.addReplacer("message", getMail().getMessages().get(count));
                    map.put(slot, loadItemDataFromFile("message", plc));
                    messageSlot.add(slot);
                    count++;
                }else{
                    map.put(slot, loadItemDataFromFile("add-message", plc));
                    addMessageSlot = slot;
                    end = true;
                    break;
                }
            }
            if (end) {end = false;count = 0;break;}
        }

        // Receiver
        for (int i = 3; i < 5; i++){
            for (int o = 0; o < 2; o++){
                int slot = o + i*9;
                Placeholder plc = new Placeholder();
                if (count < getMail().getReceiver().size()){
                    plc.addReplacer("receiver", getMail().getReceiver().get(count).getName() + "");
                    ItemStack item = loadItemDataFromFile("receiver", plc);
                    ItemMeta meta = item.getItemMeta();
                    if (meta instanceof SkullMeta){
                        SkullMeta skullMeta = (SkullMeta) meta;
                        if (DataConverter.isLegacyVersion()){
                            skullMeta.setOwner(getMail().getReceiver().get(count).getName());
                        }else {
                            skullMeta.setOwningPlayer(getMail().getReceiver().get(count));
                        }
                        item.setItemMeta(skullMeta);
                    }
                    receiverSlot.add(slot);
                    map.put(slot, item);
                    count++;
                }else{
                    map.put(slot, loadItemDataFromFile("add-receiver", plc));
                    end = true;
                    addReceiverSlot = slot;
                    break;
                }
            }
            if (end) {break;}
        }
        count = 0;
        end = false;

        // Send All
        if (getMail().getSender() != null) {
            if (getMail().getSender().hasPermission("diamail.access.sendall")) {
                if (getMail().getType().equals(MailType.SEND_ALL)) {
                    map.put(17, getFileInventoryData().get("sendall-true"));
                } else if (getMail().getType().equals(MailType.PLAYER_SEND) || getMail().getType().equals(MailType.CONSOLE_SEND)) {
                    map.put(17, getFileInventoryData().get("sendall-false"));
                }
            } else {
                map.put(17, getFileInventoryData().get("sendall-unpermitted"));
            }
        }else{
            map.put(17, getFileInventoryData().get("error"));
        }

        addInventoryData(0, new HashMap<>(map));
        map.clear();
    }

    @Override
    public void open(Player p) {
        load();
        super.open(p);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        if (event.getClickedInventory() != null) {
            if (event.getClickedInventory().equals(getMenu())) {
                actionClick(event);
            }
        }
    }

    @Override
    public void actionClick(InventoryClickEvent event) {
        Player p = null;
        if (event.getWhoClicked() instanceof Player){p = (Player) event.getWhoClicked();}

        int slot = event.getSlot();
        if (!(slot == 31 || slot == 32 || slot == 33 || slot == 40 || slot == 41 || slot == 42 || slot == 49 || slot == 50 || slot == 51)){
            event.setCancelled(true);
        }
        if (slot == 17){
            if (getMail().getType().equals(MailType.SEND_ALL)) {
                getMail().setType(MailType.PLAYER_SEND);
            }else if (getMail().getType().equals(MailType.PLAYER_SEND) || getMail().getType().equals(MailType.CONSOLE_SEND)){
                getMail().setType(MailType.SEND_ALL);
            }
        }else if (slot == 8){
            getMail().send();
        }else if (slot == addMessageSlot){
            if (p != null) {
                AnvilGUI input = new AnvilGUI(plugin, p, "Message here...", (player, reply) -> {
                    getMail().getMessages().add(ChatColor.translateAlternateColorCodes('&', reply));
                    load();
                    open(player);
                    return null;
                });

            }
        }else if (messageSlot.contains(slot)){
            int line = messageSlot.indexOf(slot);
            getMail().getMessages().remove(line);
            load();
            open(p);
        }else if (slot == 53){
            p.closeInventory();
        }
    }

    public Mail getMail() {
        return mail;
    }
}
