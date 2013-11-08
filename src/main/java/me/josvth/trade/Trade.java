package me.josvth.trade;

import com.conventnunnery.libraries.config.ConventYamlConfiguration;
import me.josvth.bukkitformatlibrary.managers.YamlFormatManager;
import me.josvth.trade.request.RequestManager;
import me.josvth.trade.transaction.Transaction;
import me.josvth.trade.transaction.TransactionListener;
import me.josvth.trade.transaction.TransactionManager;
import me.josvth.trade.transaction.inventory.LayoutManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Trade extends JavaPlugin {

	private static Trade instance;

	// Configurations
	private ConventYamlConfiguration generalConfiguration;
	private ConventYamlConfiguration layoutConfiguration;
	private ConventYamlConfiguration messageConfiguration;

	// Managers
	private YamlFormatManager formatManager;
	private LayoutManager layoutManager;
	private TransactionManager transactionManager;
	private RequestManager requestManager;

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

		// Load configurations
		generalConfiguration = new ConventYamlConfiguration(new File(getDataFolder(), "config.yml"), getDescription().getVersion());
		generalConfiguration.setDefaults(getResource("config.yml"));
		generalConfiguration.load();

		messageConfiguration = new ConventYamlConfiguration(new File(getDataFolder(), "messages.yml"), getDescription().getVersion());
		messageConfiguration.setDefaults(getResource("messages.yml"));
		messageConfiguration.load();

		layoutConfiguration = new ConventYamlConfiguration(new File(getDataFolder(), "layouts.yml"), getDescription().getVersion());
		layoutConfiguration.setDefaults(getResource("layouts.yml"));
		layoutConfiguration.load();

		// Load managers
		formatManager = new YamlFormatManager();
		formatManager.loadFormatters(generalConfiguration.getConfigurationSection("formatters"));
		formatManager.loadMessages(messageConfiguration);

		layoutManager = new LayoutManager(layoutConfiguration);
		layoutManager.load();

		transactionManager = new TransactionManager(this);

		requestManager = new RequestManager(transactionManager);

		// Register listeners
		transactionListener = new TransactionListener(transactionManager, formatManager);
		getServer().getPluginManager().registerEvents(transactionListener, this);

	}

	public YamlFormatManager getFormatManager() {
		return formatManager;
	}

	public LayoutManager getLayoutManager() {
		return layoutManager;
	}

	public TransactionManager getTransactionManager() {
		return transactionManager;
	}

	public RequestManager getRequestManager() {
		return requestManager;
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
