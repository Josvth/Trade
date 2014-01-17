package me.josvth.trade;

import me.josvth.bukkitformatlibrary.message.MessageHolder;
import me.josvth.trade.request.*;
import me.josvth.trade.transaction.Transaction;
import me.josvth.trade.transaction.TransactionManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class CommandManager implements CommandExecutor {

    private final Trade plugin;

    public CommandManager(Trade plugin) {
        this.plugin = plugin;
    }

    public void load() {
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

        // /trade open
        if ("open".equalsIgnoreCase(args[0])) {
            return executeOpenCommand(commandSender, Arrays.copyOfRange(args, 1, args.length));
        }

        // /trade request <player>
        if ("request".equalsIgnoreCase(args[0])) {
            return executeRequestCommand(commandSender, Arrays.copyOfRange(args, 1, args.length));
        }

        // /trade <player>
        return executeRequestCommand(commandSender, args[0]);

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

        if (args.length < 1) {
            getMessageHolder().getMessage("command.invalid-usage").send(commandSender, "%usage%", "/trade request <player>");
            return true;
        }

        return executeRequestCommand(commandSender, args[0]);

    }

    private boolean executeRequestCommand(CommandSender commandSender, String requested) {

        if (!(commandSender instanceof Player)) {
            getMessageHolder().getMessage("commands.player-only").send(commandSender);
            return true;
        }

        final Player player = (Player) commandSender;

        final RequestResponse response = getRequestManager().submit(new Request(player.getName(), requested, RequestMethod.COMMAND));

        final RequestRestriction restriction = response.getRequestRestriction();

        if (response.getTransaction() != null) {
            response.getTransaction().start();
        } else {
            if (restriction == RequestRestriction.METHOD) {
                getMessageHolder().getMessage(RequestMethod.COMMAND.messagePath).send(player);
            } else {
                getMessageHolder().getMessage(restriction.requestMessagePath).send(player, "%player%", requested);
            }
        }

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
