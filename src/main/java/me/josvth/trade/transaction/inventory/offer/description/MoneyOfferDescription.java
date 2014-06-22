package me.josvth.trade.transaction.inventory.offer.description;

import me.josvth.trade.Trade;
import me.josvth.trade.transaction.inventory.TransactionHolder;
import me.josvth.trade.transaction.inventory.offer.MoneyOffer;
import me.josvth.trade.util.ItemStackUtils;
import org.bukkit.inventory.ItemStack;

public class MoneyOfferDescription extends OfferDescription<MoneyOffer> {

    private ItemStack moneyItem;
    private ItemStack moneyItemMirror;

    private double smallModifier;
    private double largeModifier;

    @Override
    public ItemStack createItem(MoneyOffer offer, TransactionHolder holder) {
        final ItemStack itemStack;

        if (moneyItem != null) {
            itemStack = moneyItem.clone();

            if (offer.getAmount() > Integer.MAX_VALUE) {
                itemStack.setAmount((Integer.MAX_VALUE));
            } else {
                itemStack.setAmount(((int) offer.getAmount()));
            }

        } else {
            itemStack = null;
        }

        return ItemStackUtils.argument(
                itemStack,
                "%money%", Trade.getInstance().getEconomy().format(offer.getAmount()),
                "%small%", Trade.getInstance().getEconomy().format(getSmallModifier()),
                "%large%", Trade.getInstance().getEconomy().format(getLargeModifier())
        );
    }

    @Override
    public ItemStack createMirrorItem(MoneyOffer offer, TransactionHolder holder) {
        final ItemStack itemStack;
        if (moneyItemMirror != null) {
            itemStack = moneyItemMirror.clone();

            if (offer.getAmount() > Integer.MAX_VALUE) {
                itemStack.setAmount((Integer.MAX_VALUE));
            } else {
                itemStack.setAmount(((int) offer.getAmount()));
            }

        } else {
            itemStack = null;
        }
        return ItemStackUtils.argument(itemStack, "%money%", Trade.getInstance().getEconomy().format(offer.getAmount()));
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

    public double getSmallModifier() {
        return smallModifier;
    }

    public void setSmallModifier(int smallModifier) {
        this.smallModifier = smallModifier;
    }

    public double getLargeModifier() {
        return largeModifier;
    }

    public void setLargeModifier(int largeModifier) {
        this.largeModifier = largeModifier;
    }

}
