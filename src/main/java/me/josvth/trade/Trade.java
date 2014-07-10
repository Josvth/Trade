package me.josvth.trade;

import com.conventnunnery.libraries.config.ConventYamlConfiguration;
import me.josvth.bukkitformatlibrary.formatter.ColorFormatter;
import me.josvth.bukkitformatlibrary.message.managers.MessageManager;
import me.josvth.bukkitformatlibrary.message.managers.YamlMessageManager;
import me.josvth.trade.request.RequestManager;
import me.josvth.trade.transaction.TransactionManager;
import me.josvth.trade.transaction.inventory.LayoutManager;
import me.josvth.trade.transaction.inventory.offer.ExperienceOffer;
import me.josvth.trade.transaction.inventory.slot.*;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.RegisteredServiceProvider;
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
    private CommandManager commandManager;

    // Dependencies
    private Economy economy;

    public Trade() {

        instance = this;

        messageManager = new YamlMessageManager();
        layoutManager = new LayoutManager(this, messageManager);

        transactionManager = new TransactionManager(this);
        requestManager = new RequestManager(this, messageManager.getMessageHolder(), transactionManager);

        commandManager = new CommandManager(this);

    }

    public static Trade getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {

        // Load dependencies
        loadDependencies();

        // Load configurations
        loadConfigurations();

        // Load managers
        loadManagers();

        transactionManager.initialize();
        requestManager.initialize();
        commandManager.initialize();
    }


    private void loadConfigurations() {
        generalConfiguration = new ConventYamlConfiguration(new File(getDataFolder(), "config.yml"), getDescription().getVersion());
        generalConfiguration.setDefaults(getResource("config.yml"));

        generalConfiguration.load(false, true);

        // Update generalConfiguration nodes
        if (generalConfiguration.needToUpdate()) {

            if (generalConfiguration.isSet("trading.offers")) {
                generalConfiguration.set("trading.options.allow-inventory-closing", generalConfiguration.getBoolean("trading.allow-inventory-closing"));
                generalConfiguration.set("trading.allow-inventory-closing", null);
            }

            if (generalConfiguration.isSet("trading.offers")) {
                generalConfiguration.set("trading.options.allow-dragging", !generalConfiguration.getBoolean("trading.disable-dragging"));
                generalConfiguration.set("trading.disable-dragging", null);
            }

            if (generalConfiguration.isSet("trading.offers")) {
                generalConfiguration.set("trading.options.use-economy", generalConfiguration.get("trading.use-economy"));
                generalConfiguration.set("trading.use-economy", null);
            }

            if (generalConfiguration.isSet("trading.offers")) {
                generalConfiguration.set("trading.global-offers", generalConfiguration.getConfigurationSection("trading.offers"));
                generalConfiguration.set("trading.offers", null);
            }

            generalConfiguration.update();

        }


        messageConfiguration = new ConventYamlConfiguration(new File(getDataFolder(), "messages.yml"), getDescription().getVersion());
        messageConfiguration.setDefaults(getResource("messages.yml"));
        messageConfiguration.load();

        layoutConfiguration = new ConventYamlConfiguration(new File(getDataFolder(), "layouts.yml"), getDescription().getVersion());
        layoutConfiguration.setDefaults(getResource("layouts.yml"));
        layoutConfiguration.load();
    }

    private void loadManagers() {

        if (generalConfiguration.isConfigurationSection("formatters")) {
            messageManager.loadFormatters(generalConfiguration.getConfigurationSection("formatters"));
        }

        messageManager.getFormatterHolder().addFormatter(new ColorFormatter("default"));
        messageManager.getMessageHolder().setKeyWhenMissing(generalConfiguration.getBoolean("debug-mode", false));
        messageManager.loadMessages(messageConfiguration);

        layoutManager.load(layoutConfiguration, messageConfiguration.getConfigurationSection("trading"), generalConfiguration.getConfigurationSection("trading.global-offers"));
        layoutManager.getRegisteredSlots().put("accept", AcceptSlot.class);
        layoutManager.getRegisteredSlots().put("refuse", RefuseSlot.class);
        layoutManager.getRegisteredSlots().put("status", StatusSlot.class);
        layoutManager.getRegisteredSlots().put("close", CloseSlot.class);
        layoutManager.getRegisteredSlots().put("dummy", DummySlot.class);
        layoutManager.getRegisteredSlots().put("mirror", MirrorSlot.class);
        layoutManager.getRegisteredSlots().put("trade", TradeSlot.class);
        layoutManager.getRegisteredSlots().put("money", MoneySlot.class);
        layoutManager.getRegisteredSlots().put("experience", ExperienceSlot.class);

        transactionManager.load(generalConfiguration.getConfigurationSection("trading.options"));

        requestManager.load(generalConfiguration.getConfigurationSection("requesting"));

    }

    private void loadDependencies() {

        // Load vault economy
        if (getServer().getPluginManager().getPlugin("Vault") != null) {
            RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
            if (rsp != null) {
                economy = rsp.getProvider();
            }
        }


    }

    @Override
    public void onDisable() {
        requestManager.unload();
        transactionManager.unload();
        layoutManager.unload();
        messageManager.unload();
    }

    public void onReload() {
        getServer().getPluginManager().disablePlugin(this);
        getServer().getPluginManager().enablePlugin(this);
    }

    public boolean hasPermission(CommandSender sender, String permission) {
        return sender.hasPermission(permission);
//        if (getGeneralConfiguration().getBoolean("use-permissions", true)) {
//            return sender.hasPermission(permission);
//        }
//        return (getServer().getPluginManager().getPermission(permission).getDefault() == PermissionDefault.OP && sender.isOp()) || getServer().getPluginManager().getPermission(permission).getDefault() != PermissionDefault.OP;
    }

    public MessageManager getMessageManager() {
        return messageManager;
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

    public ConventYamlConfiguration getGeneralConfiguration() {
        return generalConfiguration;
    }

    public ConventYamlConfiguration getLayoutConfiguration() {
        return layoutConfiguration;
    }

    public ConventYamlConfiguration getMessageConfiguration() {
        return messageConfiguration;
    }

    public boolean useEconomy() {
        return getGeneralConfiguration().getBoolean("trading.use-economy", true) && getEconomy() != null;
    }

    public Economy getEconomy() {
        return economy;
    }

    public boolean isDebugMode() {
        return getGeneralConfiguration().getBoolean("debug-mode", false);
    }
}
