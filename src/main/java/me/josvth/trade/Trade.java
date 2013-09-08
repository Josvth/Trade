package me.josvth.trade;

import me.josvth.trade.transaction.Trader;
import me.josvth.trade.transaction.inventory.TransactionHolder;
import me.josvth.trade.transaction.inventory.TransactionLayout;
import me.josvth.trade.transaction.inventory.slot.SlotInfo;
import me.josvth.trade.transaction.inventory.slot.TradeSlot;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Trade extends JavaPlugin implements Listener {

	private static Trade instance;

	public static Trade getInstance() {
		return instance;
	}

	@Override
	public void onEnable() {
		instance = this;
		getServer().getPluginManager().registerEvents(this, this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		List<SlotInfo> slots = new ArrayList<SlotInfo>();

		slots.add(SlotInfo.create(TradeSlot.class, 0));
		slots.add(SlotInfo.create(TradeSlot.class, 1));
		slots.add(SlotInfo.create(TradeSlot.class, 2));
		slots.add(SlotInfo.create(TradeSlot.class, 3));

		TransactionHolder holder = new TransactionHolder(new Trader(null, sender.getName()), new TransactionLayout(1, slots));

		((Player)sender).openInventory(holder.getInventory());

		return true;

	}

	@EventHandler
	public void onDrag(InventoryDragEvent event) {

		((Player)event.getWhoClicked()).sendMessage("DRAG!"); // Just a message to show a click is registered.

		event.getInventory().addItem(new ItemStack(Material.ANVIL, 1)); // Adding items to the inventory in an event causes out of sync issues.

	}

	@EventHandler
	public void onClick(InventoryClickEvent event){

		((Player)event.getWhoClicked()).sendMessage("CLICK!"); // Just a message to show a click is registered.

		event.getInventory().addItem(new ItemStack(Material.ANVIL, 1)); // Adding items to the inventory in an event causes out of sync issues.

	}
}
