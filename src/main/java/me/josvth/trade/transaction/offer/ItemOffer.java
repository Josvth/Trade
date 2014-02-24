package me.josvth.trade.transaction.offer;

import me.josvth.trade.Trade;
import me.josvth.trade.transaction.action.trader.offer.SetOfferAction;
import me.josvth.trade.transaction.inventory.slot.Slot;
import me.josvth.trade.transaction.inventory.slot.TradeSlot;
import me.josvth.trade.transaction.offer.description.ItemOfferDescription;
import me.josvth.trade.transaction.Trader;
import me.josvth.trade.transaction.inventory.TransactionHolder;
import me.josvth.trade.transaction.inventory.slot.MirrorSlot;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

public class ItemOffer extends StackableOffer {

    private ItemStack item = null;

    public ItemOffer() {
        this(null);
    }

    public ItemOffer(ItemStack item) {
        this.item = item;
    }

    @Override
    public String getType() {
        return "item";
    }

    @Override
    public ItemOfferDescription getDescription(Trader trader) {
        return (ItemOfferDescription) super.getDescription(trader);
    }

    @Override
    public ItemStack createItem(TransactionHolder holder) {
        return getDescription(holder.getTrader()).createItem(this, holder);
    }

    @Override
    public ItemStack createMirrorItem(TransactionHolder holder) {
        return getDescription(holder.getTrader()).createMirrorItem(this, holder);
    }

    @Override
    public int getAmount() {
        return (item == null)? 0 : item.getAmount();
    }

    @Override
    public void setAmount(int amount) {

        if (item == null) {
            throw new IllegalArgumentException("Cannot set amount if item is zero");
        }

        item.setAmount(amount);

    }

    @Override
    public int getMaxAmount() {
        return (item == null)? 0 : item.getMaxStackSize();
    }

    @Override
    public boolean isFull() {
        return item != null && item.getMaxStackSize() - item.getAmount() <= 0;
    }

    @Override
    public void grant(final Trader trader) {
        Bukkit.getScheduler().runTask(Trade.getInstance(), new Runnable() {         //TODO Make this nicer
            @Override
            public void run() {
                trader.getPlayer().getInventory().addItem(item);
            }
        });
    }


    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public ItemOffer clone() {
        return new ItemOffer(item);
    }

    @Override
    public boolean isSimilar(StackableOffer stackableOffer) {
        return stackableOffer instanceof ItemOffer && (getItem() != null) && getItem().isSimilar(((ItemOffer) stackableOffer).getItem());
    }

}
