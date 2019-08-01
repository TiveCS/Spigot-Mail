package com.rehoukrel.diamail.menu;

import com.rehoukrel.diamail.DiaMail;
import com.rehoukrel.diamail.api.manager.Mail;
import com.rehoukrel.diamail.api.manager.MailType;
import com.rehoukrel.diamail.api.manager.Mailbox;
import com.rehoukrel.diamail.api.manager.PlayerData;
import com.rehoukrel.diamail.utils.DataConverter;
import com.rehoukrel.diamail.utils.XMaterial;
import com.rehoukrel.diamail.utils.language.Placeholder;
import com.rehoukrel.diamail.utils.menu.UneditableMenu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MailboxMenu extends UneditableMenu implements Listener {

    public static DiaMail plugin = DiaMail.getPlugin(DiaMail.class);
    Placeholder plc = new Placeholder();
    PlayerData playerData;
    Mailbox mailbox;
    boolean isInbox = true;
    HashMap<Integer, Mail> inbox = new HashMap<>(), outbox = new HashMap<>();
    List<Integer> nextPage = Arrays.asList(17, 26, 35, 44), previousPage = Arrays.asList(9, 18, 27, 36);
    final List<Integer> mailSlot = Arrays.asList(10,11,12,13,14,15,16, 19,20,21,22,23,24,25, 28,29,30,31,32,33,34, 37,38,39,40,41,42,43 );

    public MailboxMenu(PlayerData playerData) {
        super(plugin, 6);
        this.playerData = playerData;
        if (plugin != null) {
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
        }else{
            System.out.println("[" + this.getClass().getSimpleName() + "] Failed to register event.");
        }
    }

    public void load(){
        if (getPlayerData() == null) {return;}
        if (!getPlayerData().getPlayer().hasPlayedBefore()){return;}
        mailbox = getPlayerData().getMailbox();
        addItemData("next-page", XMaterial.LIME_STAINED_GLASS_PANE.parseMaterial(), "&aNext Page", new ArrayList<>(), new HashMap<>());
        addItemData("previous-page", XMaterial.BLUE_STAINED_GLASS_PANE.parseMaterial(), "&3Previous Page", new ArrayList<>(), new HashMap<>());
        addItemData("border", XMaterial.BLACK_STAINED_GLASS_PANE.parseMaterial(), " ", new ArrayList<>(), new HashMap<>());
        addItemData("inbox-mailbox", XMaterial.CHEST_MINECART.parseMaterial(), "&aINBOX &8(&e%mailbox_inbox%&8)", new ArrayList<>(), new HashMap<>());
        addItemData("outbox-mailbox", XMaterial.ENDER_CHEST.parseMaterial(), "&bOUTBOX &8(&e%mailbox_outbox%&8)", new ArrayList<>(), new HashMap<>());
        addItemData("send-mail", XMaterial.WRITABLE_BOOK.parseMaterial(), "&eSend Mail", new ArrayList<>(), new HashMap<>());
        addItemData("block-list", XMaterial.TNT.parseMaterial(), "&cBlocked Players", new ArrayList<>(), new HashMap<>());
        addItemData("inbox-mail", XMaterial.KNOWLEDGE_BOOK.parseMaterial(), " ", DataConverter.colored(Arrays.asList("&2&lSENDER &a%mail_sender%", "&e&lITEM &c%mail_item_size%", " ", "&f%mail_message%")), new HashMap<>());
        addItemData("outbox-mail", XMaterial.BOOK.parseMaterial(), " ", DataConverter.colored(Arrays.asList("&2&lRECEIVER&8(&c%mail_receiver_size%&8) &a%mail_receiver_1%", "&7Contains &c%mail_item_size% &7item(s)", " ", "&f%mail_message%")), new HashMap<>());
        addItemData("summary", XMaterial.SIGN.parseMaterial(), "&6%player%'s Mailbox", DataConverter.colored(Arrays.asList(" ", "&8- &fInbox &8(&7%mailbox_inbox%&8)", "&8- &fOutbox &8(&7%mailbox_outbox%&8)")), new HashMap<>());

        HashMap<Integer, ItemStack> map = new HashMap<>();
        loadInventoryDataFromFile();

        map.putAll(slotItem(getFileInventoryData().get("border"), 0,1,2,3,5,8,7,6, 45, 48, 49, 50, 53));
        plc.addReplacer("player", getPlayerData().getPlayer().getName());
        plc.addReplacer("uuid", getPlayerData().getPlayer().getUniqueId().toString());
        plc.addReplacer("page", getPage() + "");
        plc.addReplacer("previous_page", (getPage() - 1) + "");
        plc.addReplacer("next_page", (getPage() + 1) + "");
        plc.addReplacer("mailbox_inbox", getPlayerData().getMailbox().getInbox().size() + "");
        plc.addReplacer("mailbox_outbox", getPlayerData().getMailbox().getOutbox().size() + "");
        if (getPage() > 1){
            map.putAll(slotItem(loadItemDataFromFile("previous-page", plc), previousPage));
        }else{
            map.putAll(slotItem(getFileInventoryData().get("border"), previousPage));
        }
        map.putAll(slotItem(loadItemDataFromFile("summary", plc), 4));
        if (((isInbox && mailbox.getInbox().size() >= 4*7*getPage()) || (mailbox.getOutbox().size() >= 4*7*getPage() && !isInbox))) {
            map.putAll(slotItem(loadItemDataFromFile("next-page", plc), nextPage));
        }else{
            map.putAll(slotItem(getFileInventoryData().get("border"), nextPage));
        }
        map.putAll(slotItem(loadItemDataFromFile("inbox-mailbox", plc), 46));
        map.putAll(slotItem(loadItemDataFromFile("outbox-mailbox", plc), 47));

        map.put(51, getFileInventoryData().get("send-mail"));
        map.put(52, getFileInventoryData().get("block-list"));

        inbox = getMailOnPage(mailbox.getInbox());
        outbox = getMailOnPage(mailbox.getOutbox());

        addInventoryData(0, new HashMap<>(map));
        map.clear();
        if (isInbox) {
            for (int slot : inbox.keySet()) {
                Mail m = inbox.get(slot);
                plc.addReplacer("mail_uuid", m.getUniqueId().toString());
                plc.addReplacer("mail_sender", m.getSender().getName());
                plc.addReplacer("mail_item_size", m.getAttachedItem().size() + "");
                plc.addReplacer("mail_type", m.getType().name());
                plc.addReplacerList("mail_message", m.getMessages());
                map.putAll(slotItem(loadItemDataFromFile("inbox-mail", plc), slot));
            }
        }else{
            for (int slot : outbox.keySet()) {
                Mail m = outbox.get(slot);
                plc.addReplacer("mail_uuid", m.getUniqueId().toString());
                plc.addReplacer("mail_receiver_size", m.getReceiver().size() + "");
                for (int i = 0; i < m.getReceiver().size(); i++) {
                    plc.addReplacer("mail_receiver_" + (i + 1), m.getReceiver().get(i).getName() + "");
                }
                plc.addReplacer("mail_item_size", m.getAttachedItem().size() + "");
                plc.addReplacer("mail_type", m.getType().name());
                plc.addReplacerList("mail_message", m.getMessages());
                map.putAll(slotItem(loadItemDataFromFile("outbox-mail", plc), slot));
            }
        }
        addInventoryData(getPage(), new HashMap<>(map));
    }

    public HashMap<Integer, Mail> getMailOnPage(List<Mail> mailType){
        HashMap<Integer, Mail> m = new HashMap<>();
        List<Mail> indexed = new ArrayList<>();
        int page = getPage();
        int count = (4*7)*page - 4*7;
        int max = 0;
        double checker = mailType.size() / ((4*7)*page);
        if (checker >= 1){
            max = (4*7)*page;
        }else{
            max = mailType.size();
        }
        indexed = mailType.subList(count, max);
        for (int i = 0; i < indexed.size(); i++){
            m.put(mailSlot.get(i), indexed.get(i));
        }
        return m;
    }

    @Override
    public void open(Player p) {
        load();
        if (p.getOpenInventory().getTopInventory() !=null){
            p.closeInventory();
        }
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
        event.setCancelled(true);
        int slot = event.getSlot();

        if (nextPage.contains(slot) && ((isInbox && mailbox.getInbox().size() >= 4*7*getPage()) || (mailbox.getOutbox().size() >= 4*7*getPage() && !isInbox))){
            this.nextPage((Player) event.getWhoClicked());
        }else if (Arrays.asList(previousPage).contains(slot) && getPage() > 1){
            this.previousPage((Player) event.getWhoClicked());
        }else if (slot == 46){
            isInbox = true;
            setPage(1);
            open((Player) event.getWhoClicked());
        }else if (slot == 47){
            isInbox = false;
            setPage(1);
            open((Player) event.getWhoClicked());
        }else if (slot == 51){
            Mail m = new Mail();
            m.setSender((CommandSender) getPlayerData().getPlayer());
            m.setType(MailType.PLAYER_SEND);
            m.getEditor().open((Player) event.getWhoClicked());
        }else if (mailSlot.contains(slot)){
            if (getInventoryData().get(getPage()).containsKey(slot)){
                Mail m;
                if (isInbox){
                    m = inbox.get(slot);
                }else{
                    m = outbox.get(slot);
                }
                if (m == null || getPlayerData() == null){
                    return;
                }
                MailContents contents = new MailContents(getPlayerData(), m);
                contents.open((Player) event.getWhoClicked());
                contents.setConnectedMailbox(this);
            }
        }
    }

    public PlayerData getPlayerData() {
        return playerData;
    }

    public void switchInbox(){
        isInbox = !isInbox;
    }

    public boolean isInbox() {
        return isInbox;
    }
}

