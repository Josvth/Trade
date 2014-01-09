package me.josvth.trade.transaction.offer.description;

import me.josvth.trade.transaction.offer.MoneyOffer;
import me.josvth.trade.transaction.offer.OfferList;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MoneyOfferDescription extends OfferDescription<MoneyOffer> {

    @Override
    public ItemStack createItem(MoneyOffer offer) {
        final ItemStack item = new ItemStack(Material.GOLD_INGOT, 0);

        final ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(offer.getAmount() + " monies");

        item.setItemMeta(meta);

        return item;
    }

    @Override
    public MoneyOffer createOffer() {
        return new MoneyOffer();
    }

    @Override
    public Class<MoneyOffer> getOfferClass() {
        return MoneyOffer.class;
    }

}
