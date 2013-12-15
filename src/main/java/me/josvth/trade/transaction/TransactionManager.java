package me.josvth.trade.transaction;

import me.josvth.trade.Trade;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

public class TransactionManager {

    private final Trade plugin;

    private final TransactionListener listener;

    private TransactionOptions options = new TransactionOptions();

    private Map<String, Transaction> transactions = new HashMap<String, Transaction>();

    public TransactionManager(Trade plugin) {
        this.plugin = plugin;
        this.listener = new TransactionListener(this);
    }

    public void initialize() {
        Bukkit.getServer().getPluginManager().registerEvents(listener, plugin);
    }

    public void load(ConfigurationSection section) {
        options.load(section);
    }

    public void unload() {
        for (Transaction transaction : new LinkedHashSet<Transaction>(transactions.values())) {
            transaction.stop(false);
            transaction.getTraderA().getFormattedMessage("cancelled.reload").send(transaction.getTraderA().getPlayer());
            transaction.getTraderB().getFormattedMessage("cancelled.reload").send(transaction.getTraderB().getPlayer());
        }
        transactions.clear();
    }

    public Trade getPlugin() {
        return plugin;
    }

    public TransactionOptions getOptions() {
        return options;
    }

    public Transaction createTransaction(String playerA, String playerB) {

        Transaction transaction = removeTransaction(playerA);
        if (transaction != null) transaction.cancel();

        transaction = removeTransaction(playerB);
        if (transaction != null) transaction.cancel();

        transaction = new Transaction(this, plugin.getLayoutManager().getLayout(playerA, playerB), playerA, playerB);

        return transaction;

    }

    public boolean isInTransaction(String player) {
        return transactions.containsKey(player);
    }

    public Transaction getTransaction(String player) {
        return transactions.get(player);
    }

    public void addTransaction(Transaction transaction) {
        transactions.put(transaction.getTraderA().getName(), transaction);
        transactions.put(transaction.getTraderB().getName(), transaction);
    }

    public void removeTransaction(Transaction transaction) {
        removeTransaction(transaction.getTraderA().getName());
        removeTransaction(transaction.getTraderB().getName());
    }

    private Transaction removeTransaction(String player) {
        return transactions.remove(player);
    }


}