package me.josvth.trade.transaction.inventory.offer.description;

import me.josvth.trade.transaction.inventory.TransactionHolder;
import org.bukkit.inventory.ItemStack;

/*
We use OfferDescriptions to make sure that if a layout is changed mid trade all current offers show the same
 */
public abstract class OfferDescription<T> {

    public abstract ItemStack createItem(T offer, TransactionHolder holder);

    public ItemStack createMirrorItem(T offer, TransactionHolder holder) {
        return createItem(offer, holder);
    }

    public abstract T createOffer();

    public abstract Class<T> getOfferClass();

}
