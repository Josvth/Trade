package me.josvth.trade.transaction.inventory.slot;

import me.josvth.trade.Trade;
import me.josvth.trade.transaction.inventory.TransactionHolder;
import me.josvth.trade.util.ItemStackUtils;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class CloseSlot extends Slot {

	private ItemStack closeItem = null;

	public CloseSlot(int slot, TransactionHolder holder) {
		super(slot, holder);
	}

    public ItemStack getCloseItem() {
        return closeItem;
    }

    public void setCloseItem(ItemStack closeItem) {
        this.closeItem = closeItem;
    }

    @Override
	public void onClick(InventoryClickEvent event) {

		final TransactionHolder holder = (TransactionHolder) event.getInventory().getHolder();

		holder.getTrader().closeInventory();

		event.setCancelled(true);

	}

	@Override
	public void update() {
		setGUIItem(closeItem);
	}

    public static CloseSlot deserialize(int slotID, TransactionHolder holder, SlotDescription description) {
        final CloseSlot slot = new CloseSlot(slotID, holder);
        slot.setCloseItem(ItemStackUtils.fromSection(description.getConfiguration().getConfigurationSection("close-item"), Trade.getInstance().getMessageManager()));
        return slot;
    }

}
