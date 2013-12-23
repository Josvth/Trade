package me.josvth.trade.transaction.offer.description;

import me.josvth.trade.transaction.offer.ItemOffer;
import me.josvth.trade.transaction.offer.OfferList;
import org.bukkit.inventory.ItemStack;

public class ItemOfferDescription extends OfferDescription<ItemOffer> {

    @Override
    public ItemStack createItem(ItemOffer offer) {
        if (offer.getItem() != null && offer.getItem() .getAmount() != 0) {
            return offer.getItem() ;
        }
        return null;
    }

    @Override
    public ItemOffer createOffer(OfferList list, int offerIndex) {
        return new ItemOffer(list, offerIndex);
    }

    @Override
    public Class<ItemOffer> getOfferClass() {
        return ItemOffer.class;
    }
}
