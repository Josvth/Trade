package me.josvth.trade.transaction;

import me.josvth.bukkitformatlibrary.message.FormattedMessage;
import me.josvth.trade.offer.OfferList;
import me.josvth.trade.transaction.inventory.Layout;
import me.josvth.trade.transaction.inventory.TransactionHolder;
import me.josvth.trade.transaction.inventory.slot.AcceptSlot;
import me.josvth.trade.transaction.inventory.slot.StatusSlot;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Trader {

    private final Transaction transaction;
    private final String name;

    private final OfferList offers;
    private final Layout layout;
    private final TransactionHolder holder;

    private Trader other;

    private boolean accepted = false;
    private boolean refused = false;

    public Trader(Transaction transaction, String name, int offerSize) {

        this.transaction = transaction;
        this.name = name;

        this.offers = new OfferList(this, offerSize);
        this.layout = transaction.getLayout();  //TODO Trader specific layouts?
        this.holder = new TransactionHolder(transaction.getPlugin(), this);

    }

    public Transaction getTransaction() {
        return transaction;
    }

    public String getName() {
        return name;
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
        return Bukkit.getPlayerExact(name);
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

    public void accept() {

        if (!hasAccepted()) {

            setAccepted(true);

            getFormattedMessage("accepted.self").send(getPlayer());
            holder.getOtherTrader().getFormattedMessage("accepted.other").send(getOtherTrader().getPlayer(), "%player%", getPlayer().getName());

            AcceptSlot.updateAcceptSlots(holder, true);
            StatusSlot.updateStatusSlots(holder.getOtherHolder(), true);

            if (getOtherTrader().hasAccepted()) {
                holder.getTransaction().stop(true);
            }

        }

    }

    public void deny() {

        if (hasAccepted()) {
            setAccepted(false);

            getFormattedMessage("denied.self").send(getPlayer());
            holder.getOtherTrader().getFormattedMessage("denied.other").send(getOtherTrader().getPlayer(), "%player%", getPlayer().getName());

            AcceptSlot.updateAcceptSlots(holder, true);
            StatusSlot.updateStatusSlots(holder.getOtherHolder(), true);
        }

    }

    public boolean hasRefused() {
        return refused;
    }

    public void setRefused(boolean refused) {
        this.refused = refused;
    }

    public void refuse() {

        if (!hasRefused()) {

            setRefused(true);

            getFormattedMessage("refused.self").send(getPlayer());
            getFormattedMessage("refused.other").send(getOtherTrader().getPlayer(), "%player%", holder.getTrader().getName());

            getTransaction().stop(false);

        }

    }

    public void cancelAccept() {

        if (hasAccepted()) {

            setAccepted(false);

            getFormattedMessage("denied.offer-changed").send(holder.getOtherTrader().getPlayer(), "%player%", holder.getTrader().getName());

            AcceptSlot.updateAcceptSlots(holder.getOtherHolder(), true);
            StatusSlot.updateStatusSlots(holder, true);

        }

    }

    public void openInventory() {
        getPlayer().openInventory(holder.getInventory());
    }

    public void closeInventory() {
        getPlayer().closeInventory();
    }

    public FormattedMessage getFormattedMessage(String key) {
        return getLayout().getMessage(key);
    }

    public boolean hasFormattedMessage(String key) {
        return getLayout().hasMessage(key);
    }



}
