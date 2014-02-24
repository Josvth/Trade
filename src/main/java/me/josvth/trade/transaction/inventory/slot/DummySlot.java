package me.josvth.trade.transaction.inventory.slot;

import me.josvth.trade.transaction.inventory.TransactionHolder;
import org.bukkit.inventory.ItemStack;

public class DummySlot extends Slot {

    private final ItemStack dummyItem;

    public DummySlot(int slot, ItemStack dummyItem) {
        super(slot);
        this.dummyItem = dummyItem;
    }

    @Override
    public void update(TransactionHolder holder) {
        setItem(holder, dummyItem.clone());
    }
}
