package me.josvth.trade;

import me.josvth.trade.transaction.Trader;
import me.josvth.trade.transaction.Transaction;
import me.josvth.trade.transaction.TransactionListener;
import me.josvth.trade.transaction.TransactionManager;
import me.josvth.trade.transaction.inventory.TransactionHolder;
import me.josvth.trade.transaction.inventory.TransactionLayout;
import me.josvth.trade.transaction.inventory.slot.*;
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

public class Trade extends JavaPlugin {

	private static Trade instance;

	private TransactionManager transactionManager;

	// Listeners
	private TransactionListener transactionListener;

	public static Trade getInstance() {
		return instance;
	}

	@Override
	public void onEnable() {
		instance = this;

		// Load managers
		transactionManager = new TransactionManager(this);

		// Register listeners
		transactionListener = new TransactionListener(transactionManager, null);
		getServer().getPluginManager().registerEvents(transactionListener, this);

	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (!(sender instanceof Player)) {
			return true;
		}

		Player player = (Player) sender;

		if (args[0].equalsIgnoreCase("new")) {

			final Transaction transaction = transactionManager.createTransaction(sender.getName(), sender.getName());

			transaction.start();

		}

		if (args[0].equalsIgnoreCase("open")) {

			Transaction transaction = transactionManager.getTransaction(player.getName());

			if (transaction != null) {
				transaction.getTrader(player.getName()).openInventory();
			} else {
				player.sendMessage("NOT TRADING!");
			}

		}

		return true;

	}


}
