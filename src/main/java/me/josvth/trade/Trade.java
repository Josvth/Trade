package me.josvth.trade;

import me.josvth.trade.transaction.Transaction;
import me.josvth.trade.transaction.TransactionListener;
import me.josvth.trade.transaction.TransactionManager;
import me.josvth.trade.transaction.inventory.LayoutManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Trade extends JavaPlugin {

	private static Trade instance;

	private LayoutManager layoutManager;
	private TransactionManager transactionManager;

	// Listeners
	private TransactionListener transactionListener;

	public static Trade getInstance() {
		return instance;
	}

	public Trade() {
		instance = this;
	}

	@Override
	public void onEnable() {

		// Load managers
		layoutManager = new LayoutManager(null);
		layoutManager.load();

		transactionManager = new TransactionManager(this);
		transactionManager.load();

		// Register listeners
		transactionListener = new TransactionListener(transactionManager, null);
		getServer().getPluginManager().registerEvents(transactionListener, this);

	}

	public LayoutManager getLayoutManager() {
		return layoutManager;
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
