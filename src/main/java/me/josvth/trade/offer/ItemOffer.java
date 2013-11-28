package me.josvth.trade.offer;

import me.josvth.trade.Trade;
import me.josvth.trade.transaction.Trader;
import me.josvth.trade.transaction.inventory.TransactionHolder;
import me.josvth.trade.transaction.inventory.slot.MirrorSlot;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

public class ItemOffer extends Offer {

	private ItemStack item = null;

	public ItemOffer(OfferList list, int offerIndex) {
		this(list, offerIndex, null);
	}

	public ItemOffer(OfferList list, int id, ItemStack item) {
		super(list, id);
		this.item = item;
	}

	@Override
	public ItemStack getDisplayItem() {
		if (item != null && item.getAmount() != 0)
			return item;
		return null;
	}

	@Override
	public double getAmount() {
		return (item == null)? 0.0 : item.getAmount();
	}

	@Override
	public boolean isFull() {
		return item != null && item.getMaxStackSize() - item.getAmount() <= 0;
	}

	@Override
	public void grant(final Trader trader) {
		Bukkit.getScheduler().runTask(Trade.getInstance(), new Runnable() {
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
		return new ItemOffer(list, offerIndex, item);
	}

	// Event handling
	@Override
	public void onClick(InventoryClickEvent event) {

        final TransactionHolder holder = (TransactionHolder) event.getInventory().getHolder();

		switch (event.getAction()) {
			case PICKUP_ALL:
			case MOVE_TO_OTHER_INVENTORY:
			case HOTBAR_MOVE_AND_READD:
				item = null;
                holder.getOffers().set(offerIndex, null);
				break;
			case PICKUP_HALF:
				item.setAmount(item.getAmount() / 2);
				break;
			case PICKUP_ONE:
				item.setAmount(item.getAmount() - 1);
				break;
			case PLACE_ALL:
				item.setAmount(item.getAmount() + event.getCursor().getAmount());
				break;
			case PLACE_ONE:
				item.setAmount(item.getAmount() + 1);
				break;
			case SWAP_WITH_CURSOR:
				item = event.getCursor().clone(); // We clone here to make sure that our offer item is not bound to the inventory one
                break;
			default:
				throw new IllegalStateException("UNHANDLED ACTION: " + event.getAction().name());
		}

        // We only update the mirror because we assume the current view is showing the correct item stack
        MirrorSlot.updateMirrors(((TransactionHolder)event.getInventory().getHolder()).getOtherHolder(), true, offerIndex);
	}

	@Override
	public boolean isDraggable() {
		return true;
	}

	@Override
	public void onDrag(int slot, InventoryDragEvent event) {

		item = event.getNewItems().get(slot).clone();

        // We only update the mirror because we assume the current view is showing the correct item stack
        MirrorSlot.updateMirrors(((TransactionHolder)event.getInventory().getHolder()).getOtherHolder(), true, offerIndex);

	}

}
