package me.josvth.trade.goods;

import me.josvth.trade.Trade;
import me.josvth.trade.transaction.Trader;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

public class ItemTradeable extends Tradeable {

	private ItemStack item;

	public ItemTradeable(ItemStack item) {
		super(TradeableType.ITEM);
		Validate.notNull(item, "Item can't be null.");
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

	@Override
	public <T extends Tradeable> T add(T tradeable) {

		if (!(tradeable instanceof ItemTradeable))
			return tradeable;

		ItemTradeable remaining = ((ItemTradeable) tradeable).clone();

		if (!getItem().isSimilar(remaining.getItem())) {
			return tradeable;
		}

		final int remainingAmount = getItem().getMaxStackSize() - getItem().getAmount() - remaining.getItem().getAmount();

		if (remainingAmount >= 0) {
			item.setAmount(item.getAmount() + remaining.getItem().getAmount());
			return null;
		} else {
			remaining.getItem().setAmount( -1 * remainingAmount);
			return (T) remaining;
		}

	}

	@Override
	public <T extends Tradeable> T remove(T tradeable) {
		return tradeable;
	}

	public ItemTradeable clone() {
		return new ItemTradeable(item);
	}

	// Event handling
	@Override
	public boolean onClick(InventoryClickEvent event) {
		// TODO should I do this "predicting what's going to happen" or should I use the update system to update tradeables?
		switch (event.getAction()) {
			case PICKUP_ALL:
			case MOVE_TO_OTHER_INVENTORY:
			case HOTBAR_MOVE_AND_READD:
				item = null;
				break;
			case PICKUP_SOME:
				throw new IllegalStateException("PICKUP_SOME");
			case PICKUP_HALF:
				item.setAmount(item.getAmount() / 2);
				break;
			case PICKUP_ONE:
				item.setAmount(item.getAmount() - 1);
				break;
			case PLACE_ALL:
				item.setAmount(item.getAmount() + event.getCursor().getAmount());
				break;
			case PLACE_SOME:
				item.setAmount(item.getMaxStackSize());
				throw new IllegalStateException("PLACE_SOME");
			case PLACE_ONE:
				item.setAmount(item.getAmount() + 1);
				break;
			case SWAP_WITH_CURSOR:
				item = event.getCursor().clone(); // We clone here to make sure that our tradeable item is not bound to the inventory one
				break;
			default:
				throw new IllegalStateException("UNHANDLED ACTION: " + event.getAction().name());
		}

		return true;

	}

	@Override
	public boolean onDrag(int slot, InventoryDragEvent event) {
		item = event.getNewItems().get(slot);
		return true;
	}

}
