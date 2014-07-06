package me.josvth.trade.transaction.inventory.offer.description;

import me.josvth.trade.transaction.inventory.TransactionHolder;
import me.josvth.trade.transaction.inventory.offer.ItemOffer;
import org.bukkit.inventory.ItemStack;

public class ItemOfferDescription extends OfferDescription<ItemOffer> {

    @Override
    public ItemStack createItem(ItemOffer offer, TransactionHolder holder) {
        if (offer.createItemStack() != null && offer.createItemStack().getAmount() != 0) {
            return offer.createItemStack();
        }
        return null;
    }

    @Override
    public ItemOffer createOffer() {
        return new ItemOffer();
    }

    @Override
    public Class<ItemOffer> getOfferClass() {
        return ItemOffer.class;
    }
}
