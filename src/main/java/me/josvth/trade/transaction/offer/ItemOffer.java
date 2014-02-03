package me.josvth.trade.transaction.offer;

import me.josvth.trade.transaction.action.trader.offer.OfferAction;
import me.josvth.trade.transaction.offer.description.ItemOfferDescription;
import me.josvth.trade.transaction.Trader;
import me.josvth.trade.transaction.inventory.TransactionHolder;
import me.josvth.trade.transaction.inventory.slot.MirrorSlot;
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

    public static ItemOffer create(Trader trader, ItemStack itemStack) {
        final ItemOffer offer = trader.getLayout().getOfferDescription(ItemOffer.class).createOffer();
        offer.setItem(itemStack);
        return offer;
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
        trader.getPlayer().getInventory().addItem(item);
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

	// Event handling
	@Override
	public void onClick(InventoryClickEvent event, int offerIndex) {

        final TransactionHolder holder = (TransactionHolder) event.getInventory().getHolder();

		switch (event.getAction()) {
			case PICKUP_ALL:
			case MOVE_TO_OTHER_INVENTORY:
			case HOTBAR_MOVE_AND_READD:
				item = null;
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

        OfferAction.create(holder.getTrader(), offerIndex, null).execute();

	}

	@Override
	public boolean isDraggable() {
		return true;
	}

	@Override
	public void onDrag(InventoryDragEvent event, int offerIndex, int slotIndex) {

		item = event.getNewItems().get(slotIndex).clone();

        // We only update the mirror because we assume the current view is showing the correct item stack
        MirrorSlot.updateMirrors(((TransactionHolder)event.getInventory().getHolder()).getOtherHolder(), true, offerIndex);

	}

}
