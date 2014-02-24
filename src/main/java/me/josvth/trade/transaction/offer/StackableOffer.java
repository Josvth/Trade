package me.josvth.trade.transaction.offer;


import me.josvth.trade.transaction.action.trader.offer.ChangeOfferAction;
import me.josvth.trade.transaction.action.trader.offer.SetOfferAction;
import me.josvth.trade.transaction.inventory.TransactionHolder;
import me.josvth.trade.transaction.inventory.slot.Slot;
import me.josvth.trade.transaction.inventory.slot.TradeSlot;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public abstract class StackableOffer extends Offer {

    public abstract int getAmount();

    public abstract void setAmount(int amount);

    public abstract int getMaxAmount();

    public boolean isFull() {
        return false;
    }

    public boolean isWorthless() {
        return getAmount() == 0;
    }

    public int add(int amount) {

        if (getMaxAmount() == -1) { // If infinite stackable, add all
            setAmount(getAmount() + amount);
            return 0;
        }

        final int remainder = getAmount() + amount - getMaxAmount();
        if (remainder > 0) {
            setAmount(getMaxAmount());
            return remainder;
        } else {
            setAmount(getAmount() + amount);
            return 0;
        }

    }

    public int remove(int amount) {
        final int remainder = getAmount() - amount;
        if (remainder > 0) {
            setAmount(remainder);
            return 0;
        } else {
            setAmount(0);
            return -1 * remainder;
        }
    }

    public abstract StackableOffer clone();

    public abstract boolean isSimilar(StackableOffer contents);

    //TODO Cleanup offer creation and cloning
    public static <T extends StackableOffer> T split(T offer) {

        final int total = offer.getAmount();

        final T clone = (T) offer.clone();

        offer.setAmount(total / 2);

        clone.setAmount(total - offer.getAmount());

        return clone;

    }

    public static <T extends StackableOffer> T takeOne(T offer) {

        if (offer.getAmount() == 1) {
            throw new IllegalArgumentException("StackableOffer must have an amount greater than 1");
        }

        final T clone = (T) offer.clone();

        offer.setAmount(offer.getAmount() - 1);

        clone.setAmount(1);

        return clone;

    }
}
