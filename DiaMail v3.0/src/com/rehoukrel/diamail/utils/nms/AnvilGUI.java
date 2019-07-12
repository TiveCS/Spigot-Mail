package com.rehoukrel.diamail.utils.nms;

import com.rehoukrel.diamail.utils.nms.version.VersionMatcher;
import com.rehoukrel.diamail.utils.nms.version.VersionWrapper;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.function.BiFunction;

/**
 * An anvil gui, used for gathering a user's input
 *
 * @author Wesley Smith
 * @since 1.0
 */
public class AnvilGUI {

	/**
	 * The local {@link VersionWrapper} object for the server's version
	 */
	private static VersionWrapper WRAPPER = new VersionMatcher().match();

	/**
	 * The player who has the GUI open
	 */
	private final Player holder;
	/**
	 * The ItemStack that is in the {@link Slot#INPUT_LEFT} slot.
	 */
	private final ItemStack insert;
	/**
	 * Called when the player clicks the {@link Slot#OUTPUT} slot
	 */
	private final BiFunction<Player, String, String> biFunction;

	/**
	 * The container id of the inventory, used for NMS methods
	 */
	private final int containerId;
	/**
	 * The inventory that is used on the Bukkit side of things
	 */
	private final Inventory inventory;
	/**
	 * The listener holder class
	 */
	private final ListenUp listener = new ListenUp();

	/**
	 * Represents the state of the inventory being open
	 */
	private boolean open;

	/**
	 * Create an AnvilGUI and open it for the player
	 *
	 * @param plugin       A {@link org.bukkit.plugin.java.JavaPlugin} instance
	 * @param holder       The {@link Player} to open the inventory for
	 * @param insert       What to have the text already set to
	 * @param clickHandler A {@link ClickHandler} that is called when the player clicks the {@link Slot#OUTPUT} slot
	 * @throws NullPointerException If the server version isn't supported
	 * @deprecated As of version 1.1, use {@link AnvilGUI(Plugin, Player, String, BiFunction)}
	 */
	@Deprecated
	public AnvilGUI(Plugin plugin, Player holder, String insert, ClickHandler clickHandler) {
		this(plugin, holder, insert, clickHandler::onClick);
	}

	/**
	 * Create an AnvilGUI and open it for the player.
	 *
	 * @param plugin     A {@link org.bukkit.plugin.java.JavaPlugin} instance
	 * @param holder     The {@link Player} to open the inventory for
	 * @param insert     What to have the text already set to
	 * @param biFunction A {@link BiFunction} that is called when the player clicks the {@link Slot#OUTPUT} slot
	 * @throws NullPointerException If the server version isn't supported
	 */
	public AnvilGUI(Plugin plugin, Player holder, String insert, BiFunction<Player, String, String> biFunction) {
		this.holder = holder;
		this.biFunction = biFunction;

		final ItemStack paper = new ItemStack(Material.PAPER);
		final ItemMeta paperMeta = paper.getItemMeta();
		paperMeta.setDisplayName(insert);
		paper.setItemMeta(paperMeta);
		this.insert = paper;
		if (new VersionMatcher().matchLegacy() != null) {
			WRAPPER.handleInventoryCloseEvent(holder);
			WRAPPER.setActiveContainerDefault(holder);

			Bukkit.getPluginManager().registerEvents(listener, plugin);

			final Object container = WRAPPER.newContainerAnvil(holder);

			inventory = WRAPPER.toBukkitInventory(container);
			inventory.setItem(Slot.INPUT_LEFT, this.insert);

			containerId = WRAPPER.getNextContainerId(holder);
			WRAPPER.sendPacketOpenWindow(holder, containerId);
			WRAPPER.setActiveContainer(holder, container);
			WRAPPER.setActiveContainerId(container, containerId);
			WRAPPER.addActiveContainerSlotListener(container, holder);
			open = true;
		} else {
			try {
				holder.closeInventory();
			}catch (Exception ignore){}

			Bukkit.getPluginManager().registerEvents(listener, plugin);

			final Object container = WRAPPER.newContainerAnvil(holder);

			inventory = WRAPPER.toBukkitInventory(container);
			inventory.setItem(Slot.INPUT_LEFT, this.insert);

			containerId = WRAPPER.getNextContainerId(holder);
			WRAPPER.sendPacketOpenWindow(holder, containerId);
			WRAPPER.addActiveContainerSlotListener(container, holder);
			open = true;
		}
	}

	/**
	 * Closes the inventory if it's open.
	 *
	 * @throws IllegalArgumentException If the inventory isn't open
	 */
	public void closeInventory() {
		if (new VersionMatcher().matchLegacy() != null) {
			Validate.isTrue(open, "You can't close an inventory that isn't open!");
			open = false;

			WRAPPER.handleInventoryCloseEvent(holder);
			WRAPPER.setActiveContainerDefault(holder);
			WRAPPER.sendPacketCloseWindow(holder, containerId);

			HandlerList.unregisterAll(listener);
		} else {
			if (open)
				return;

			open = false;

			holder.closeInventory();

			HandlerList.unregisterAll(listener);
		}

	}

	/**
	 * Returns the Bukkit inventory for this anvil gui
	 *
	 * @return the {@link Inventory} for this anvil gui
	 */
	public Inventory getInventory() {
		return inventory;
	}

	/**
	 * Simply holds the listeners for the GUI
	 */
	private class ListenUp implements Listener {

		@EventHandler
		public void onInventoryClick(InventoryClickEvent e) {
			if (e.getInventory().equals(inventory)) {
				e.setCancelled(true);
				final Player clicker = (Player) e.getWhoClicked();
				if (e.getRawSlot() == Slot.OUTPUT) {
					final ItemStack clicked = inventory.getItem(e.getRawSlot());
					if (clicked == null || clicked.getType() == Material.AIR) return;
					final String ret = biFunction.apply(clicker, clicked.hasItemMeta() ? clicked.getItemMeta().getDisplayName() : clicked.getType().toString());
					if (ret != null) {
						final ItemMeta meta = clicked.getItemMeta();
						meta.setDisplayName(ret);
						clicked.setItemMeta(meta);
						inventory.setItem(e.getRawSlot(), clicked);
					} else try{closeInventory();}catch (Exception ignore){}
				}
			}
		}

		@EventHandler
		public void onInventoryClose(InventoryCloseEvent e) {
			if (open && e.getInventory().equals(inventory)) try{closeInventory();}catch (Exception ignore){}
		}

	}

	/**
	 * Handles the click of the output slot
	 *
	 * @deprecated Since version 1.1, use {@link AnvilGUI(Plugin, Player, String, BiFunction)} instead
	 */
	@Deprecated
	public static abstract class ClickHandler {

		/**
		 * Is called when a {@link Player} clicks on the output in the GUI
		 *
		 * @param clicker The {@link Player} who clicked the output
		 * @param input   What the item was renamed to
		 * @return What to replace the text with, or null to close the inventory
		 */
		public abstract String onClick(Player clicker, String input);

	}

	/**
	 * Class wrapping the magic constants of slot numbers in an anvil GUI
	 */
	public static class Slot {

		/**
		 * The slot on the far left, where the first input is inserted. An {@link ItemStack} is always inserted
		 * here to be renamed
		 */
		public static final int INPUT_LEFT = 0;
		/**
		 * Not used, but in a real anvil you are able to put the second item you want to combine here
		 */
		public static final int INPUT_RIGHT = 1;
		/**
		 * The output slot, where an item is put when two items are combined from {@link #INPUT_LEFT} and
		 * {@link #INPUT_RIGHT} or {@link #INPUT_LEFT} is renamed
		 */
		public static final int OUTPUT = 2;

	}

}
