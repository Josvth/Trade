package me.josvth.trade.transaction.inventory.offer.description;

import me.josvth.trade.transaction.inventory.TransactionHolder;
import me.josvth.trade.transaction.inventory.offer.ExperienceOffer;
import me.josvth.trade.util.ItemStackUtils;
import org.bukkit.inventory.ItemStack;

public class ExperienceOfferDescription extends OfferDescription<ExperienceOffer> {

    private ItemStack experienceItem;
    private ItemStack experienceItemMirror;

    private int smallModifier;
    private int largeModifier;

    @Override
    public ItemStack createItem(ExperienceOffer offer, TransactionHolder holder) {
        final ItemStack itemStack;
        if (experienceItem != null) {
            itemStack = experienceItem.clone();

            if (offer.getAmount() > Integer.MAX_VALUE) {
                itemStack.setAmount((Integer.MAX_VALUE));
            } else {
                itemStack.setAmount(((int) offer.getAmount()));
            }

        } else {
            itemStack = null;
        }
        return ItemStackUtils.argument(itemStack, "%experience%", String.valueOf(offer.getAmount()), "%small%", String.valueOf(smallModifier), "%large%", String.valueOf(largeModifier));
    }

    @Override
    public ItemStack createMirrorItem(ExperienceOffer offer, TransactionHolder holder) {
        final ItemStack itemStack;
        if (experienceItemMirror != null) {
            itemStack = experienceItemMirror.clone();

            if (offer.getAmount() > Integer.MAX_VALUE) {
                itemStack.setAmount((Integer.MAX_VALUE));
            } else {
                itemStack.setAmount(((int) offer.getAmount()));
            }

        } else {
            itemStack = null;
        }
        return ItemStackUtils.argument(itemStack, "%player%", holder.getTrader().getName(), "%experience%", String.valueOf(offer.getAmount()));
    }

    @Override
    public ExperienceOffer createOffer() {
        return new ExperienceOffer();
    }

    @Override
    public Class<ExperienceOffer> getOfferClass() {
        return ExperienceOffer.class;
    }

    public ItemStack getExperienceItem() {
        return experienceItem;
    }

    public void setExperienceItem(ItemStack experienceItem) {
        this.experienceItem = experienceItem;
    }

    public ItemStack getExperienceItemMirror() {
        return experienceItemMirror;
    }

    public void setExperienceItemMirror(ItemStack experienceItemMirror) {
        this.experienceItemMirror = experienceItemMirror;
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
