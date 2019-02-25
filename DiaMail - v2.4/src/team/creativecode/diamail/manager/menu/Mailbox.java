package team.creativecode.diamail.manager.menu;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import team.creativecode.diamail.manager.Menu;
import team.creativecode.diamail.manager.PlayerData;
import team.creativecode.diamail.manager.mail.Mail;
import team.creativecode.diamail.utils.DataConverter;

public class Mailbox extends Menu{
	
	public enum MailboxType{
		INBOX, OUTBOX, ALL;
	}
	
	public Mailbox() {
		super();
	}
	
	public void loadMail() {
		// Remove slot that are not mail slot
		List<Integer> inbox = new ArrayList<Integer>(this.getInvDataSlot("menu-data.mail-inbox")),
				outbox = new ArrayList<Integer>(this.getInvDataSlot("menu-data.mail-outbox")),
				total = new ArrayList<Integer>(inbox);
		
		PlayerData pd = (PlayerData) this.getCustomObject().get("playerdata");
		List<Mail> ab = new ArrayList<Mail>(), ib = pd.getInbox(), ob = pd.getOutbox();
		ab.addAll(ib);
		// Filtering
		for (Mail m : ib) {
			for (Mail mb : ob) {
				if (m.getMailUUID().equals(mb.getMailUUID())) {
					ob.remove(mb);
					break;
				}
			}
		}
		ab.addAll(ob);
		
		for (int i = 0; i < total.size();i++) {
			int a = total.get(i);
			if (outbox.contains(a)) {
				outbox.remove(outbox.indexOf(a));
			}
		}
		total.addAll(outbox);
		
		int num = total.size()*(this.getPage() - 1);
		for (int a = 0; a < total.size(); a++) {
			int i = total.get(a);
			if (this.getInventoryData().containsKey(i)) {
				if (num < ab.size()) {
					try {
						Mail m = ab.get(num);
						ItemStack item = new ItemStack(Material.AIR);
						List<String> clore;
						m.initPlaceholder();
						if (ib.contains(ab.get(num))) {
							item = this.convertItem("menu-data.mail-inbox", m.getPlaceholder());
							clore = item.getItemMeta().hasLore() ? item.getItemMeta().getLore() : new ArrayList<String>();
						}else {
							item = this.convertItem("menu-data.mail-outbox", m.getPlaceholder());
							clore = item.getItemMeta().hasLore() ? item.getItemMeta().getLore() : new ArrayList<String>();
						}
						if (m.hasItem()) {
							item = m.getItem().clone();
							ItemMeta meta = item.getItemMeta();
							if (meta.hasLore()) {
								meta.setLore(DataConverter.combineList(meta.getLore(), clore));
							}else {
								meta.setLore(clore);
							}
							item.setItemMeta(meta);
						}
						this.getInventoryData().put(i, item);
					}catch(Exception e) {
					}
					num++;
				}else {
					this.getInventoryData().remove(i);
				}
			}
		}
	}
	
	@Override
	public void open(Player p) {
		initItemFromConfig();
		
		loadMail();
		
		update();
		Menu.singleMenu.put(p, this);
		p.openInventory(this.getMenu());
	}
	
	@Override
	public void nextPage() {
		this.setPage(this.getPage() + 1);
		initItemFromConfig();
		loadMail();
		update();
		getPlaceholder().inputData("page", this.getPage() + "");
	}
	
	@Override
	public void previousPage() {
		if (this.getPage() > 1) {
			this.setPage(this.getPage() - 1);
		}else {
			this.setPage(1);
		}
		initItemFromConfig();
		loadMail();
		update();
		getPlaceholder().inputData("page", this.getPage() + "");
	}

	@Override
	public void actionCustom(Player clicker, int slot, ClickType click, Object... args) {
		List<Integer> inbox = new ArrayList<Integer>(this.getInvDataSlot("menu-data.mail-inbox")),
				outbox = new ArrayList<Integer>(this.getInvDataSlot("menu-data.mail-outbox")),
				total = new ArrayList<Integer>(inbox);
		
		PlayerData pd = (PlayerData) this.getCustomObject().get("playerdata");
		List<Mail> ab = new ArrayList<Mail>(), ib = pd.getInbox(), ob = pd.getOutbox();
		ab.addAll(ib);
		// Filtering
		for (Mail m : ib) {
			for (Mail mb : ob) {
				if (m.getMailUUID().equals(mb.getMailUUID())) {
					ob.remove(mb);
					break;
				}
			}
		}
		ab.addAll(ob);
		
		for (int i = 0; i < total.size();i++) {
			int a = total.get(i);
			if (outbox.contains(a)) {
				outbox.remove(outbox.indexOf(a));
			}
		}
		total.addAll(outbox);

		int num = total.size()*(this.getPage() - 1);
		
		try {
			num += total.indexOf(slot);
			Mail mail = ab.get(num);
			mail.showInGui(clicker);
		}catch(Exception e) {}
	}

}
