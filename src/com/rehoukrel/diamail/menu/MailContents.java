package com.rehoukrel.diamail.menu;

import com.rehoukrel.diamail.DiaMail;
import com.rehoukrel.diamail.api.manager.Mail;
import com.rehoukrel.diamail.api.manager.PlayerData;
import com.rehoukrel.diamail.utils.DataConverter;
import com.rehoukrel.diamail.utils.XMaterial;
import com.rehoukrel.diamail.utils.language.Placeholder;
import com.rehoukrel.diamail.utils.menu.UneditableMenu;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MailContents extends UneditableMenu implements Listener {

    private static DiaMail plugin = DiaMail.getPlugin(DiaMail.class);
    private List<Integer> itemSlot = Arrays.asList(18,19,20,21,22,23,24,25,26);

    private MailboxMenu connectedMailbox;
    private Placeholder plc = new Placeholder();
    private Mail mail;
    private PlayerData playerData;

    public MailContents(PlayerData playerData, Mail mail) {
        super(plugin, 3, ChatColor.translateAlternateColorCodes('&', "&4&lMail Contents"));
        this.mail = mail;
        this.playerData = playerData;
        init();
        if (getMail() != null && getPlayerData() != null){
            plc.addReplacer("sender", getMail().getSender().getName());
            plc.addReplacer("type", getMail().getType().name());
            plc.addReplacer("uuid", getMail().getUniqueId().toString());
            plc.addReplacer("item_size", getMail().getAttachedItem().size() + "");
            plc.addReplacer("receiver_size", getMail().getReceiver().size() + "");

            List<String> receiverName = new ArrayList<>(), itemName = new ArrayList<>();
            plc.addReplacerList("message", DataConverter.colored(getMail().getMessages()));
            int count = 1;
            for (OfflinePlayer op : getMail().getReceiver()){
                receiverName.add(op.getName());
                plc.addReplacer("receiver_" + count, op.getName());
            }
            count = 1;
            for (ItemStack item : getMail().getAttachedItem()){
                String name = (item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : item.getType().name()) + " &f(" + item.getAmount() +"x)";
                itemName.add(name);
                plc.addReplacer("item_" + count, name);
            }
            plc.addReplacerList("item", itemName);
            plc.addReplacerList("receiver", receiverName);

        }else{
            return;
        }

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        init();
    }

    public void init(){

        addItemData("border", XMaterial.BLACK_STAINED_GLASS_PANE.parseMaterial(), " ", new ArrayList<>(), new HashMap<>());
        addItemData("message", XMaterial.PAPER.parseMaterial(), "&3&lMESSAGE", Arrays.asList("%message%"), new HashMap<>());
        addItemData("get-mail", XMaterial.WRITTEN_BOOK.parseMaterial(), "&b&lGET MAIL", Arrays.asList(" ", "&7Get this mail as written book item"), new HashMap<>());
        addItemData("sender", XMaterial.PLAYER_HEAD.parseMaterial(), "&e&lSENDER", Arrays.asList(" ", "&7- &f%sender%"), new HashMap<>());
        addItemData("receiver", XMaterial.PLAYER_HEAD.parseMaterial(), "&6&lRECEIVER", Arrays.asList(" ", "&f%receiver%"), new HashMap<>());
        addItemData("take-item", XMaterial.CHEST.parseMaterial(), "&a&lTAKE ITEM", new ArrayList<>(), new HashMap<>());
    }

    public void load(){
        if (getMail() == null){
            return;
        }

        HashMap<Integer, ItemStack> map = new HashMap<>();
        map.putAll(slotItem(loadItemDataFromFile("border", plc), 9,10,11,12,13,14,15,16,17));
        map.put(1, loadItemDataFromFile("message", plc));
        if (getMail().getSender().getName().equals(getPlayerData().getPlayer().getName())) {
            map.put(2, loadItemDataFromFile("receiver", plc));
        }else {
            map.put(2, loadItemDataFromFile("sender", plc));
        }
        map.put(3, loadItemDataFromFile("take-item", plc));
        map.put(4, loadItemDataFromFile("get-mail", plc));
        for (int i = 0; i < itemSlot.size(); i++){
            int slot = itemSlot.get(i);
            if (getMail().getAttachedItem().size() - 1 >= i){
                map.put(slot, getMail().getAttachedItem().get(i));
            }else{
                break;
            }
        }
        getInventoryData().put(0, new HashMap<>(map));
        map.clear();
    }

    @Override
    public void open(Player p) {
        load();
        super.open(p);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        if (event.getClickedInventory() != null){
            if (event.getClickedInventory().equals(getMenu())){
                actionClick(event);
            }
        }
    }

    @Override
    public void actionClick(InventoryClickEvent event) {

        int slot = event.getSlot();
        if (itemSlot.contains(slot)) {
            ItemStack item = getMail().getAttachedItem().get(itemSlot.indexOf(slot));
            if (item != null){
                event.getWhoClicked().getInventory().addItem(item.clone());
                getMail().getAttachedItem().remove(itemSlot.indexOf(slot));
            }
        }
        else if (slot == 1){
            StringBuilder s = new StringBuilder();
            for (String m : getMail().getMessages()){
                s.append(m);
            }
            event.getWhoClicked().sendMessage(s.toString());
        }else if (slot == 4){
            event.getWhoClicked().getInventory().addItem(getMail().getBookManager().getBook());
        }

        event.setCancelled(true);
    }

    public Mail getMail() {
        return mail;
    }

    public PlayerData getPlayerData() {
        return playerData;
    }

    public void setConnectedMailbox(MailboxMenu connectedMailbox) {
        this.connectedMailbox = connectedMailbox;
    }

    public MailboxMenu getConnectedMailbox() {
        return connectedMailbox;
    }
}
