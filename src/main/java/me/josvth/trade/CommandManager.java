package me.josvth.trade;

import me.josvth.bukkitformatlibrary.message.MessageHolder;
import me.josvth.trade.request.Request;
import me.josvth.trade.request.RequestManager;
import me.josvth.trade.request.RequestMethod;
import me.josvth.trade.transaction.Transaction;
import me.josvth.trade.transaction.TransactionManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class CommandManager implements CommandExecutor {

    private final Trade plugin;

    public CommandManager(Trade plugin) {
        this.plugin = plugin;
    }

    public void initialize() {
        getPlugin().getCommand("trade").setExecutor(this);
        getPlugin().getCommand("tr").setExecutor(this);
    }

    public Trade getPlugin() {
        return plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {

        if (args.length > 0) {

            // /trade reload
            if ("reload".equalsIgnoreCase(args[0])) {
                return executeReloadCommand(commandSender, Arrays.copyOfRange(args, 1, args.length));
            }

            // /trade layout
            if ("layout".equalsIgnoreCase(args[0])) {
                return executeLayoutCommand(commandSender, Arrays.copyOfRange(args, 1, args.length));
            }

            // /trade ignore
            if ("ignore".equalsIgnoreCase(args[0])) {
                return executeIgnoreCommand(commandSender, Arrays.copyOfRange(args, 1, args.length));
            }

            // /trade request <player>
            if ("request".equalsIgnoreCase(args[0])) {
                return executeRequestCommand(commandSender, Arrays.copyOfRange(args, 1, args.length));
            }

            // /trade accept [player]
            if ("accept".equalsIgnoreCase(args[0])) {
                if (args.length == 2) {
                    return executeRequestCommand(commandSender, args);
                } else {
                    return executeRequestCommand(commandSender, null);
                }
            }

            // /trade open
            if ("open".equalsIgnoreCase(args[0])) {
                return executeOpenCommand(commandSender, Arrays.copyOfRange(args, 1, args.length));
            }

        }

        // /trade <player> or /trade to accept a request
        if (args.length <= 1) {
            return executeRequestCommand(commandSender, args);
        }

        getMessageHolder().getMessage("command.invalid-usage").send(commandSender, "%usage%", "/trade <sub command> or /trade <player>");
        return true;

    }

    private boolean executeLayoutCommand(CommandSender commandSender, String[] args) {

        if (!plugin.hasPermission(commandSender, "trade.configure")) {
            getMessageHolder().getMessage("commands.no-permission").send(commandSender);
            return true;
        }

        if (args.length < 1) {
            getMessageHolder().getMessage("commands.invalid-usage").send(commandSender, "%usage%", "/trade layout <subcommand>");
            return true;
        }

        if ("list".equalsIgnoreCase(args[0])) {
            commandSender.sendMessage("Loaded layouts: ");
            for (String layout : plugin.getLayoutManager().getLayouts().keySet()) {
                commandSender.sendMessage("- " + layout);
            }
            return true;
        }

        if ("setdefault".equalsIgnoreCase(args[0])) {

            if (args.length < 2) {
                getMessageHolder().getMessage("commands.invalid-usage").send(commandSender, "%usage%", "/trade layout setdefault <defaultlayout>");
                return true;
            }

            plugin.getTransactionManager().getOptions().setDefaultLayoutName(args[1]);
            plugin.getTransactionManager().store(plugin.getGeneralConfiguration().getConfigurationSection("trading.options"));
            plugin.getGeneralConfiguration().save();
            commandSender.sendMessage("Setted default layout to: " + args[1]);
            return true;
        }

        getMessageHolder().getMessage("commands.invalid-usage").send(commandSender, "%usage%", "/trade layout <subcommand> or /trade layout <default layout id>");
        return true;
    }

    private boolean executeReloadCommand(CommandSender commandSender, String[] args) {

        if (!plugin.hasPermission(commandSender, "trade.reload")) {
            getMessageHolder().getMessage("commands.no-permission").send(commandSender);
            return true;
        }

        getPlugin().onReload();

        return true;

    }

    private boolean executeIgnoreCommand(CommandSender commandSender, String[] strings) {

        if (!(commandSender instanceof Player)) {
            getMessageHolder().getMessage("commands.player-only").send(commandSender);
            return true;
        }

        final Player player = (Player) commandSender;

        if (!plugin.hasPermission(player, "trade.request.ignore")) {
            getMessageHolder().getMessage("commands.no-permission").send(commandSender);
            return true;
        }

        getPlugin().getRequestManager().toggleIgnoring(player);

        return true;

    }

    private boolean executeOpenCommand(CommandSender commandSender, String[] args) {

        if (!(commandSender instanceof Player)) {
            getMessageHolder().getMessage("commands.player-only").send(commandSender);
            return true;
        }

        final Player player = (Player) commandSender;

        final Transaction transaction = getTransactionManager().getTransaction(player.getName());

        if (transaction != null) {
            transaction.getTrader(player.getName()).openInventory();
        } else {
            getMessageHolder().getMessage("trading.not-trading").send(player);
        }

        return true;

    }

    private boolean executeRequestCommand(CommandSender commandSender, String[] args) {

        if (!(commandSender instanceof Player)) {
            getMessageHolder().getMessage("commands.player-only").send(commandSender);
            return true;
        }

        final Player player = (Player) commandSender;

        // Check if this player is requested before and tries to accept using /trade request
        if (args == null || args.length == 0) {

            final List<Request> requests = getRequestManager().getActiveRequests(player);

            if (requests == null || requests.isEmpty()) {
                getMessageHolder().getMessage("requesting.not-requested").send(commandSender);
                return true;
            }

            getRequestManager().submit(Request.createRequest(requests.get(0).getRequesterPlayer(), player, RequestMethod.COMMAND));
            return true;

        }

        if (args.length < 1) {
            getMessageHolder().getMessage("command.invalid-usage").send(commandSender, "%usage%", "/trade <player> or /trade request <player>");
            return true;
        }

        getRequestManager().submit(Request.createRequest(plugin.getServer().getPlayer(args[0]), player, RequestMethod.COMMAND));

        return true;

    }

    public MessageHolder getMessageHolder() {
        return getPlugin().getMessageManager().getMessageHolder();
    }

    public TransactionManager getTransactionManager() {
        return getPlugin().getTransactionManager();
    }

    public RequestManager getRequestManager() {
        return getPlugin().getRequestManager();
    }

}
