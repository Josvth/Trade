package me.josvth.trade.transaction.inventory.slot;

import me.josvth.trade.goods.ItemTradeable;
import me.josvth.trade.goods.Tradeable;
import me.josvth.trade.transaction.OfferList;
import me.josvth.trade.transaction.inventory.TransactionHolder;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public class TradeSlot extends Slot {

	private final int tradeSlot;

	private final OfferList offerList;

	private Tradeable tradeable;

	public TradeSlot(TransactionHolder holder, int slot, int tradeSlot) {
		super(holder, slot);
		this.tradeSlot = tradeSlot;
		this.offerList = holder.getTrader().getOffers();
	}

	public Tradeable getTradeable() {
		return tradeable;
	}

	public void setTradeable(Tradeable tradeable) {
		this.tradeable = tradeable;
		this.offerList.set(tradeSlot, tradeable);
	}

	public boolean isEmpty() {
		return tradeable == null;
	}

	// Event handling
	@Override
	public boolean onClick(InventoryClickEvent event) {

		InventoryAction action = event.getAction();

		if (tradeable == null) {

			if (InventoryAction.PLACE_ALL == action)
				setTradeable(new ItemTradeable(event.getCursor())); // TODO Clone item here?

		} else {

			tradeable.onClick(event);

			if (tradeable.isWorthless())
				setTradeable(null);

		}

		return false;

	}

	@Override
	public boolean onDrag(InventoryDragEvent event) {
		return super.onDrag(event);    //To change body of overridden methods use File | Settings | File Templates.
	}

	@Override
	public void update() {

		tradeable = offerList.get(tradeSlot);

		if (tradeable != null)
			setInventoryItem(tradeable.getDisplayItem());

	}
}
