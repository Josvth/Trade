package me.josvth.trade;

import me.josvth.trade.transaction.Trader;
import me.josvth.trade.transaction.inventory.TransactionHolder;
import me.josvth.trade.transaction.inventory.TransactionLayout;
import me.josvth.trade.transaction.inventory.slot.MirrorSlot;
import me.josvth.trade.transaction.inventory.slot.Slot;
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

		Slot[] slots = new Slot[9];

		slots[0] = new TradeSlot(0,0);
		slots[1] = new TradeSlot(1,1);
		slots[2] = new TradeSlot(2,2);
		slots[3] = new TradeSlot(3,3);

		slots[5] = new MirrorSlot(5,0);
		slots[6] = new MirrorSlot(6,1);
		slots[7] = new MirrorSlot(7,2);
		slots[8] = new MirrorSlot(8,3);

		TransactionHolder holder = new TransactionHolder(this, new Trader(null, sender.getName(), 4), 9, "This is a title.", slots);

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
