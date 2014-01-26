package me.josvth.trade.transaction.offer.description;

import me.josvth.trade.Trade;
import me.josvth.trade.transaction.offer.MoneyOffer;
import me.josvth.trade.util.ItemStackUtils;
import org.bukkit.inventory.ItemStack;

public class MoneyOfferDescription extends OfferDescription<MoneyOffer> {

    private ItemStack moneyItem;
    private ItemStack moneyItemMirror;

    private int smallModifier;
    private int largeModifier;

    @Override
    public ItemStack createItem(MoneyOffer offer) {
        final ItemStack itemStack;
        if (moneyItem != null) {
            itemStack = moneyItem.clone();
            itemStack.setAmount(offer.getAmount());
        } else {
            itemStack = null;
        }
        return ItemStackUtils.argument(
                itemStack,
                "%money%", Trade.getInstance().getEconomy().format(((double)offer.getAmount()) / 100),
                "%small%", Trade.getInstance().getEconomy().format(((double)smallModifier) / 100),
                "%large%", Trade.getInstance().getEconomy().format(((double)largeModifier) / 100)
        );
    }

    @Override
    public MoneyOffer createOffer() {
        return new MoneyOffer();
    }

    @Override
    public Class<MoneyOffer> getOfferClass() {
        return MoneyOffer.class;
    }

    public ItemStack getMoneyItem() {
        return moneyItem;
    }

    public void setMoneyItem(ItemStack moneyItem) {
        this.moneyItem = moneyItem;
    }

    public ItemStack getMoneyItemMirror() {
        return moneyItemMirror;
    }

    public void setMoneyItemMirror(ItemStack moneyItemMirror) {
        this.moneyItemMirror = moneyItemMirror;
    }

    public int getSmallModifier() {
        return smallModifier;
    }

    public void setSmallModifier(int smallModifier) {
        this.smallModifier = smallModifier;
    }

    public int getLargeModifier() {
        return largeModifier;
    }

    public void setLargeModifier(int largeModifier) {
        this.largeModifier = largeModifier;
    }

}
