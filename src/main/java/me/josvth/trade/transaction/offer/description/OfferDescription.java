package me.josvth.trade.transaction.offer.description;

import me.josvth.trade.transaction.offer.Offer;
import me.josvth.trade.transaction.offer.OfferList;
import me.josvth.trade.transaction.inventory.TransactionHolder;
import org.bukkit.inventory.ItemStack;

/*
We use OfferDescriptions to make sure that if a layout is changed mid trade all current offers show the same
 */
public abstract class OfferDescription <T extends Offer>{

    public abstract ItemStack createItem(T offer);

    public ItemStack createMirrorItem(T offer, TransactionHolder holder) {
        return createItem(offer);
    }

    public abstract T createOffer(OfferList list, int offerIndex);

    public abstract Class<T> getOfferClass();

}
