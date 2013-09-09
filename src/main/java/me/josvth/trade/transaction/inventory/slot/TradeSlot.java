package me.josvth.trade.transaction.inventory.slot;

import me.josvth.trade.goods.ItemTradeable;
import me.josvth.trade.goods.Tradeable;
import me.josvth.trade.transaction.OfferList;
import me.josvth.trade.transaction.inventory.TransactionHolder;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

public class TradeSlot extends Slot {

	private final int tradeSlot;

	public TradeSlot(int slot, int tradeSlot) {
		super(slot);
		this.tradeSlot = tradeSlot;
	}

	// Event handling
	@Override
	public boolean onClick(InventoryClickEvent event) {

		TransactionHolder holder = (TransactionHolder) event.getInventory().getHolder();

		Tradeable tradeable = holder.getOffers().get(tradeSlot);

		// If we have a tradeable on this slot we let the tradeable handle the event
		if (tradeable == null) {

			ItemStack newItem = null;

			switch (event.getAction()) {
				case PLACE_ALL:
					newItem = event.getCursor().clone();
					break;
				case PLACE_SOME:
					throw new IllegalStateException("PLACE_SOME");
				case PLACE_ONE:
					newItem = event.getCursor().clone();
					newItem.setAmount(1);
					break;
				case UNKNOWN:
					throw new IllegalStateException("UNKNOWN");
			}

			holder.getOffers().set(tradeSlot, new ItemTradeable(newItem));

		} else {

			tradeable.onClick(event);

			if (tradeable.isWorthless())
				holder.getOffers().set(tradeSlot, null);

		}

		return false;

	}

	@Override
	public boolean onDrag(InventoryDragEvent event) {
		return super.onDrag(event);    //To change body of overridden methods use File | Settings | File Templates.
	}

	@Override
	public void update(TransactionHolder holder) {
		Tradeable tradeable = holder.getOffers().get(tradeSlot);

		if (tradeable != null)
			holder.getInventory().setItem(slot, tradeable.getDisplayItem());
	}

}
