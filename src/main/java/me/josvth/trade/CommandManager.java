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

        if (args.length < 1) {
            getMessageHolder().getMessage("command.invalid-usage").send(commandSender, "%usage%", "/trade <sub command> or /trade <player>");
            return true;
        }

        // /trade reload
        if ("reload".equalsIgnoreCase(args[0])) {
            return executeReloadCommand(commandSender, Arrays.copyOfRange(args, 1, args.length));
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

        // /trade <player>
        return executeRequestCommand(commandSender, args);

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
        if (args == null) {

            final List<Request> requests = getRequestManager().getActiveRequests(player);

            if (requests == null || requests.isEmpty()) {
                getMessageHolder().getMessage("requesting.not-requested").send(commandSender);
                return true;
            }

            getRequestManager().submit(Request.createRequest(requests.get(0).getRequestedPlayer(), player, RequestMethod.COMMAND));
            return true;

        }

        if (args.length < 1) {
            getMessageHolder().getMessage("command.invalid-usage").send(commandSender, "%usage%", "/trade request <player>");
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
