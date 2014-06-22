package me.josvth.trade.transaction;

import me.josvth.bukkitformatlibrary.message.FormattedMessage;
import me.josvth.trade.transaction.action.ActionProvoker;
import me.josvth.trade.transaction.inventory.Layout;
import me.josvth.trade.transaction.inventory.TransactionHolder;
import me.josvth.trade.transaction.inventory.offer.OfferList;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Trader implements ActionProvoker {

    private final Transaction transaction;
    private final UUID id;
    private final OfferList offers;
    private final Layout layout;
    private final TransactionHolder holder;

    private Trader other;

    private State state = State.IN_GUI;

    private boolean accepted = false;
    private boolean refused = false;

    public Trader(Transaction transaction, UUID id, int offerSize) {

        this.transaction = transaction;
        this.id = id;

        this.offers = new OfferList(this, offerSize, OfferList.Type.TRADE);
        this.layout = transaction.getLayout();  //TODO Trader specific layouts?
        this.holder = new TransactionHolder(transaction.getPlugin(), this);

    }

    public UUID getID() {
        return id;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public String getName() {
        return getPlayer().getName();
    }

    public OfferList getOffers() {
        return offers;
    }

    public Layout getLayout() {
        return layout;
    }

    public TransactionHolder getHolder() {
        return holder;
    }


    public Player getPlayer() {
        return transaction.getPlugin().getServer().getPlayer(id);
    }

    public Trader getOtherTrader() {
        return other;
    }

    public void setOther(Trader other) {
        this.other = other;
    }

    public boolean hasAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public boolean hasRefused() {
        return refused;
    }

    public void setRefused(boolean refused) {
        this.refused = refused;
    }

    public void openInventory() {
        getPlayer().openInventory(holder.getInventory());
    }

    public void closeInventory() {
        getPlayer().closeInventory();
    }

    public boolean hasFormattedMessage(String key) {
        return layout.hasMessage(key);
    }

    public FormattedMessage getFormattedMessage(String key) {
        return layout.getMessage(key);
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }


    public enum State {
        IN_GUI, ROAMING;
    }
}
