package me.josvth.trade.offer;

import me.josvth.trade.transaction.Trader;
import me.josvth.trade.transaction.inventory.TransactionHolder;
import me.josvth.trade.util.ItemStackUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class ExperienceOffer extends Offer {

    private int levels = 0;

    private final ItemStack experienceItem;
    private final ItemStack experienceItemMirror;

	private final int smallModifier;
	private final int largeModifier;

    public ExperienceOffer(OfferList list, int offerID, int smallModifier, int largeModifier, ItemStack experienceItem, ItemStack experienceItemMirror) {
        super(list, offerID);
		Validate.notNull(experienceItem, "Experience Item can't be null.");
		Validate.notNull(experienceItemMirror, "Experience Item Mirror can't be null.");
		this.smallModifier = smallModifier;
		this.largeModifier = largeModifier;
        this.experienceItem = experienceItem;
        this.experienceItemMirror = experienceItemMirror;
    }

    public int add(int amount) {
        final int remainder = levels + amount - 64; // TODO Remove hard coded 64
        if (remainder > 0) {
            levels = 64;
            return remainder;
        } else {
            levels = levels + amount;
            return 0;
        }
    }

    public int remove(int amount) {
        final int remainder = levels - amount;
        if (remainder > 0) {
            levels = remainder;
            return 0;
        } else {
            levels = 0;
            return -1 * remainder;
        }
    }

    public int getLevels() {
        return levels;
    }

    public void setLevels(int levels) {
        this.levels = levels;
    }

    @Override
    public ItemStack getDisplayItem() {
		final ItemStack itemStack = experienceItem.clone();
		itemStack.setAmount(levels);
        return ItemStackUtils.argument(itemStack, "%levels%", String.valueOf(levels), "%small%", String.valueOf(smallModifier), "%large%", String.valueOf(largeModifier));
    }

    @Override
    public ItemStack getMirrorItem(TransactionHolder holder) {
		final ItemStack itemStack = experienceItemMirror.clone();
		itemStack.setAmount(levels);
		return ItemStackUtils.argument(itemStack, "%player%", holder.getOtherTrader().getName(), "%levels%", String.valueOf(levels));
    }

    @Override
    public boolean isWorthless() {
        return levels <= 0;
    }

    @Override
    public void grant(Trader trader) {
        grant(trader, levels);
    }

    public static void grant(Trader trader, int levels) {
        trader.getPlayer().setLevel(trader.getPlayer().getLevel() + levels);
    }

    @Override
    public void onClick(InventoryClickEvent event) {

    }

    @Override
    public String toString() {
        return "EXP: " + levels;
    }

}
