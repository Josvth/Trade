package me.josvth.trade.transaction.inventory.slot;

import me.josvth.trade.Trade;
import me.josvth.trade.transaction.inventory.TransactionHolder;
import me.josvth.trade.util.ItemStackUtils;
import org.bukkit.inventory.ItemStack;

public class DummySlot extends Slot {

    private ItemStack dummyItem = null;

    public DummySlot(int slot, TransactionHolder holder) {
        super(slot, holder);
    }

    public static DummySlot deserialize(int slotID, TransactionHolder holder, SlotDescription description) {
        final DummySlot slot = new DummySlot(slotID, holder);
        slot.setDummyItem(ItemStackUtils.fromSection(description.getConfiguration().getConfigurationSection("dummy-item"), Trade.getInstance().getMessageManager()));
        return slot;
    }

    public ItemStack getDummyItem() {
        return dummyItem;
    }

    public void setDummyItem(ItemStack dummyItem) {
        this.dummyItem = dummyItem;
    }

    @Override
    public void update() {
        setGUIItem(dummyItem.clone());
    }
}
