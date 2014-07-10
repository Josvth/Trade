package me.josvth.trade.transaction;

import com.conventnunnery.libraries.config.ConventYamlConfiguration;
import me.josvth.trade.Trade;
import me.josvth.trade.transaction.action.EndAction;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.UUID;

public class TransactionManager {

    private final Trade plugin;

    private final TransactionListener listener;

    private TransactionOptions options = new TransactionOptions();

    private Map<UUID, Transaction> transactions = new HashMap<UUID, Transaction>();

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

    public void store(ConfigurationSection section) {
        options.store(section);
    }

    public void unload() {
        for (Transaction transaction : new LinkedHashSet<Transaction>(transactions.values())) {
            new EndAction(transaction, EndAction.Reason.RELOAD).execute();
        }
        transactions.clear();
    }

    public Trade getPlugin() {
        return plugin;
    }

    public TransactionOptions getOptions() {
        return options;
    }

    public Transaction createTransaction(Player playerA, Player playerB) {

        Transaction transaction = removeTransaction(playerA);
        if (transaction != null) {
            new EndAction(transaction, EndAction.Reason.GENERIC).execute();
        }

        transaction = removeTransaction(playerB);
        if (transaction != null) {
            new EndAction(transaction, EndAction.Reason.GENERIC).execute();
        }

        transaction = Transaction.createTransaction(this, plugin.getLayoutManager().getLayout(playerA, playerB), playerA, playerB);

        return transaction;

    }

    public boolean isInTransaction(Player player) {
        return transactions.containsKey(player.getUniqueId());
    }

    public Transaction getTransaction(String player) {
        return transactions.get(player);
    }

    public void addTransaction(Transaction transaction) {
        transactions.put(transaction.getTraderA().getID(), transaction);
        transactions.put(transaction.getTraderB().getID(), transaction);
    }

    public void removeTransaction(Transaction transaction) {
        transactions.remove(transaction.getTraderA().getID());
        transactions.remove(transaction.getTraderB().getID());
    }

    private Transaction removeTransaction(Player player) {
        return transactions.remove(player.getUniqueId());
    }


}