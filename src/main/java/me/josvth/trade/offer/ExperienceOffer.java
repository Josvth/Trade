package me.josvth.trade.offer;

import me.josvth.trade.transaction.Trader;
import me.josvth.trade.util.ItemStackUtils;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class ExperienceOffer extends Offer {

    private static final ItemStack DEFAULT_ITEM_STACK = ItemStackUtils.setMeta(
            new ItemStack(Material.EXP_BOTTLE),
            "You added %levels% levels.",
            Arrays.asList(new String[] {
                    "Left click to add %small% level(s)",
                    "Right click to remove %small% level(s)",
                    "Shift left click to add %large% levels",
                    "Shift right click to remove %large% levels"})
    );

    private static final ItemStack DEFAULT_ITEM_STACK_MIRROR = ItemStackUtils.setMeta(
            new ItemStack(Material.EXP_BOTTLE),
            "%player% added %levels% levels.",
            null);

    private int levels = 0;

    private final ItemStack experienceItem;
    private final ItemStack experienceItemMirror;

    public ExperienceOffer(OfferList list, int offerID) {
        this(list, offerID, 0, DEFAULT_ITEM_STACK, DEFAULT_ITEM_STACK_MIRROR);
    }

    public ExperienceOffer(OfferList list, int offerID, int levels) {
        this(list, offerID, levels, DEFAULT_ITEM_STACK, DEFAULT_ITEM_STACK_MIRROR);
    }

    public ExperienceOffer(OfferList list, int offerID, int levels, ItemStack experienceItem, ItemStack experienceItemMirror) {
        super(list, offerID);
        this.levels = levels;
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
        return ItemStackUtils.argument(experienceItem, "%levels%", String.valueOf(levels));
    }

    @Override
    public ItemStack getMirrorItem() {
        return ItemStackUtils.argument(experienceItemMirror, "%player%", "null", "%levels%", String.valueOf(levels));
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

    public ExperienceOffer clone() {
        return new ExperienceOffer(list, offerIndex, levels, experienceItem, experienceItemMirror);
    }

    @Override
    public void onClick(InventoryClickEvent event) {

    }

    @Override
    public String toString() {
        return "EXP: " + levels;
    }


}
