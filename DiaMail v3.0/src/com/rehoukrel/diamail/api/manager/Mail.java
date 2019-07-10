package com.rehoukrel.diamail.api.manager;

import com.rehoukrel.diamail.DiaMail;
import com.rehoukrel.diamail.api.events.MailCreateEvent;
import com.rehoukrel.diamail.api.events.MailReceiveEvent;
import com.rehoukrel.diamail.api.events.MailSendEvent;
import com.rehoukrel.diamail.menu.MailEditor;
import com.rehoukrel.diamail.utils.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Mail {

    public static DiaMail plugin = DiaMail.getPlugin(DiaMail.class);

    private UUID uniqueId = UUID.randomUUID();
    private CommandSender sender;
    private MailType type;
    private List<OfflinePlayer> receiver = new ArrayList<>();
    private List<String> messages = new ArrayList<>();
    private List<ItemStack> attachedItem = new ArrayList<>();

    private MailEditor editor;

    public enum MailSection{

        MESSAGE("message"), ATTACHED_ITEMS("items"), RECEIVER("receiver"), SENDER("sender"), TYPE("mail-type");

        private String path;

        MailSection(String path){
            this.path = path;
        }

        public String getPath(){
            return this.path;
        }
    }

    // For getting mail
    public Mail(){
        this.editor = new MailEditor(this);
    }
    public Mail(PlayerData pd, String uuid, boolean isInbox){
        if (pd == null){return;}
        String b = isInbox ? "inbox" : "outbox";
        if (pd.getConfigManager().getConfig().contains("mailbox." + b + "." + uuid)){
            String path = "mailbox." + b +"." + uuid;
            List<String> msg = new ArrayList<>(pd.getConfigManager().getConfig().getStringList(path + "." + MailSection.MESSAGE.getPath()));

            this.setMessages(msg);

            if (isInbox){
                String sender = pd.getConfigManager().getConfig().getString(path + "." + MailSection.SENDER.getPath());
                UUID uid = UUID.fromString(sender);
                if (uid != null){
                    OfflinePlayer op = Bukkit.getOfflinePlayer(uid);
                    this.setSender((CommandSender) op);
                }else{
                    this.setSender(Bukkit.getConsoleSender());
                }
            }else {
                List<String> receive = new ArrayList<>(pd.getConfigManager().getConfig().getStringList(path + "." + MailSection.RECEIVER.getPath()));
                this.setSender((CommandSender) pd.getPlayer());
                this.setMessages(msg);
                List<OfflinePlayer> rec = new ArrayList<>();
                for (String r : receive){
                    OfflinePlayer op = Bukkit.getOfflinePlayer(UUID.fromString(r));
                    if (op.hasPlayedBefore()){
                        rec.add(op);
                    }
                }
                this.setReceiver(rec);
            }
        }
        this.editor = new MailEditor(this);
    }
    public Mail(OfflinePlayer player, String uuid, boolean isInbox){
        this(PlayerData.getPlayerData(player), uuid, isInbox);
    }

    //-------------------

    // For sending mail
    public Mail(CommandSender sender){
        MailCreateEvent event = new MailCreateEvent(this);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()){return;}
        if (sender instanceof OfflinePlayer) {
            this.type = MailType.PLAYER_SEND;
        }else if (sender instanceof ConsoleCommandSender){
            this.type = MailType.CONSOLE_SEND;
        }
        this.sender = sender;
        this.editor = new MailEditor(this);
    }

    //---------------------------------

    public boolean send(){
        if (!getReceiver().isEmpty() && !getMessages().isEmpty() && !getType().equals(MailType.TEMPLATE)){

            OfflinePlayer playerSender = getSender() instanceof OfflinePlayer ? (OfflinePlayer) getSender() : null;

            MailSendEvent sendEvent = new MailSendEvent(getSender(), getReceiver(), this);
            Bukkit.getServer().getPluginManager().callEvent(sendEvent);
            if (sendEvent.isCancelled()){return false;}

            // Execution for receivers
            for (OfflinePlayer op : getReceiver()){
                if (op.hasPlayedBefore()){
                    PlayerData pd = PlayerData.getPlayerData(op);
                    MailReceiveEvent receiveEvent = new MailReceiveEvent(op, this);
                    Bukkit.getServer().getPluginManager().callEvent(receiveEvent);
                    if (receiveEvent.isCancelled() && receiveEvent.isBlocked()){
                        getReceiver().remove(op);
                        continue;
                    }

                    ConfigManager c = pd.getConfigManager();
                    String path = "mailbox.inbox." + getUniqueId().toString();
                    c.input(path + "." + MailSection.MESSAGE.getPath(), getMessages());
                    c.input(path + "." + MailSection.TYPE.getPath(), getType().name());
                    if (playerSender != null) {
                        c.input(path + "." + MailSection.SENDER.getPath(), playerSender.getUniqueId().toString());
                    }else{
                        c.input(path + "." + MailSection.SENDER.getPath(), getSender().getName());
                    }
                    c.input(path + "." + MailSection.ATTACHED_ITEMS.getPath(), getAttachedItem());
                    c.saveConfig();
                }else{
                    getReceiver().remove(op);
                }
            }

            // Execution for sender
            if (playerSender != null && getReceiver().size() > 0){
                PlayerData pd = PlayerData.getPlayerData(playerSender);
                ConfigManager c = pd.getConfigManager();
                String path = "mailbox.outbox." + getUniqueId().toString();
                c.input(path + "." + MailSection.MESSAGE.getPath(), getMessages());
                c.input(path + "." + MailSection.TYPE.getPath(), getType().name());
                List<String> receivers = new ArrayList<>();
                for (OfflinePlayer op : getReceiver()){
                    receivers.add(op.getUniqueId().toString());
                }
                c.input(path + "." + MailSection.RECEIVER.getPath(), receivers);
                c.input(path + "." + MailSection.ATTACHED_ITEMS.getPath(), getAttachedItem());
                c.saveConfig();
            }else if (getReceiver().size() == 0){

            }
            return true;
        }else{
            getSender().sendMessage("You cannot send empty mail");
            return false;
        }
    }

    //---------------------------------


    public void setSender(CommandSender sender) {
        this.sender = sender;
    }

    public void setAttachedItem(List<ItemStack> attachedItem) {
        this.attachedItem = attachedItem;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }

    public void setReceiver(List<OfflinePlayer> receiver) {
        this.receiver = receiver;
    }

    public void setType(MailType type) {
        this.type = type;
    }

    public void setUniqueId(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public CommandSender getSender() {

        return sender;
    }

    public MailType getType() {
        return type;
    }

    public List<ItemStack> getAttachedItem() {
        return this.attachedItem;
    }

    public List<OfflinePlayer> getReceiver() {
        return this.receiver;
    }

    public List<String> getMessages() {
        return this.messages;
    }

    public MailEditor getEditor() {
        return editor;
    }

}
