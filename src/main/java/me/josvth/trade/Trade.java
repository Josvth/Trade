package me.josvth.trade;

import com.conventnunnery.libraries.config.ConventYamlConfiguration;
import me.josvth.bukkitformatlibrary.formatter.ColorFormatter;
import me.josvth.bukkitformatlibrary.message.MessageHolder;
import me.josvth.bukkitformatlibrary.message.managers.MessageManager;
import me.josvth.bukkitformatlibrary.message.managers.YamlMessageManager;
import me.josvth.trade.request.*;
import me.josvth.trade.transaction.Transaction;
import me.josvth.trade.transaction.TransactionManager;
import me.josvth.trade.transaction.inventory.LayoutManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
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
    private YamlMessageManager messageManager;
    private LayoutManager layoutManager;
    private TransactionManager transactionManager;
    private RequestManager requestManager;

    public Trade() {

        instance = this;

        messageManager = new YamlMessageManager();
        layoutManager = new LayoutManager(this, messageManager);

        transactionManager = new TransactionManager(this);
        requestManager = new RequestManager(this, messageManager.getMessageHolder(), transactionManager);

    }

    public static Trade getInstance() {
        return instance;
    }

    @Override
    public void onLoad() {

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
        if (generalConfiguration.isConfigurationSection("formatters")) {
            messageManager.loadFormatters(generalConfiguration.getConfigurationSection("formatters"));
        }

        messageManager.loadMessages(messageConfiguration);
        messageManager.getFormatterHolder().addFormatter(new ColorFormatter("default"));

        layoutManager.load(layoutConfiguration, messageConfiguration.getConfigurationSection("trading"), generalConfiguration.getConfigurationSection("offers"));

        transactionManager.load(generalConfiguration.getConfigurationSection("trading"));

        requestManager.load(generalConfiguration.getConfigurationSection("requesting"));

    }

    @Override
    public void onEnable() {
        transactionManager.initialize();
        requestManager.initialize();
    }

    @Override
    public void onDisable() {
        requestManager.unload();
        transactionManager.unload();
        layoutManager.unload();
        messageManager.unload();
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public MessageHolder getMessageHolder() {
        return getMessageManager().getMessageHolder();
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

        if (args.length < 1) {
            getMessageHolder().getMessage("command.invalid-usage").send(player, "%usage%", "/trade <sub command> or /trade <player>");
            return true;
        }

        if ("open".equalsIgnoreCase(args[0])) {

            Transaction transaction = transactionManager.getTransaction(player.getName());

            if (transaction != null) {
                transaction.getTrader(player.getName()).openInventory();
            } else {
                player.sendMessage("NOT TRADING!");
            }

            return true;

        }

        if ("request".equalsIgnoreCase(args[0])) {

            if (args.length < 2) {
                getMessageHolder().getMessage("command.invalid-usage").send(player, "%usage%", "/trade request <player>");
                return true;
            }

            final RequestResponse response = requestManager.submit(new Request(player.getName(), args[1], RequestMethod.COMMAND));

            final RequestRestriction restriction = response.getRequestRestriction();

            if (response.getTransaction() != null) {
                response.getTransaction().start();
                // TODO add messages

            } else {
                if (restriction == RequestRestriction.METHOD) {
                    getMessageHolder().getMessage(RequestMethod.COMMAND.messagePath).send(player);
                } else {
                    getMessageHolder().getMessage(restriction.requestMessagePath).send(player, "%player%", args[1]);
                }
            }

            return true;

        }

        // /trade <player>
        final RequestResponse response = requestManager.submit(new Request(player.getName(), args[0], RequestMethod.COMMAND));

        final RequestRestriction restriction = response.getRequestRestriction();

        if (response.getTransaction() != null) {
            response.getTransaction().start();
            // TODO add messages

        } else {
            if (restriction == RequestRestriction.METHOD) {
                getMessageHolder().getMessage(RequestMethod.COMMAND.messagePath).send(player);
            } else {
                getMessageHolder().getMessage(restriction.requestMessagePath).send(player, "%player%", args[0]);
            }
        }

        return true;

    }

}
