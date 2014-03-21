package me.josvth.trade.transaction;

import me.josvth.trade.Trade;
import me.josvth.trade.transaction.action.Action;
import me.josvth.trade.transaction.action.ActionProvoker;
import me.josvth.trade.transaction.inventory.Layout;

public class Transaction {

    private final TransactionManager manager;

    private final Layout layout;

    private final Trader traderA;
    private final Trader traderB;

    private Transaction.Stage stage = Transaction.Stage.PRE;
    private TransactionActionProvoker transactionProvoker = new TransactionActionProvoker(this);

    public Transaction(TransactionManager manager, Layout layout, String playerA, String playerB) {

        this.manager = manager;
        this.layout = layout;

        traderA = new Trader(this, playerA, layout.getOfferSize());
        traderB = new Trader(this, playerB, layout.getOfferSize());

        traderA.setOther(traderB);
        traderB.setOther(traderA);
    }

    public TransactionManager getManager() {
        return manager;
    }

    public Trade getPlugin() {
        return manager.getPlugin();
    }

    public Trader getTraderA() {
        return traderA;
    }

    public Trader getTraderB() {
        return traderB;
    }

    public Trader getTrader(String playerName) {
        if (traderA.getName().equals(playerName))
            return traderA;
        if (traderB.getName().equals(playerName))
            return traderB;
        throw new IllegalArgumentException("Player " + playerName + " is not participating in this trade or went offline.");
    }

    public Layout getLayout() {
        return layout;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public Transaction.Stage getStage() {
        return stage;
    }

    public boolean isStarted() {
        return stage == Transaction.Stage.IN_PROGRESS;
    }

    public boolean hasEnded() {
        return stage == Transaction.Stage.POST;
    }

    public void stop() {
        stage = Transaction.Stage.POST;
    }

    public void remove() {
        manager.removeTransaction(this);
    }

    public boolean useLogging() {
        return false;
    }

    public void logAction(Action action) {

    }

    public TransactionActionProvoker getTransactionProvoker() {
        return transactionProvoker;
    }

    private class TransactionActionProvoker implements ActionProvoker {

        private final Transaction transaction;

        private TransactionActionProvoker(Transaction transaction) {
            this.transaction = transaction;
        }

        @Override
        public Transaction getTransaction() {
            return transaction;
        }

        @Override
        public String getName() {
            return "TRANSACTION";
        }

    }

    public enum Stage {
        PRE,
        IN_PROGRESS,
        POST;
    }
}
