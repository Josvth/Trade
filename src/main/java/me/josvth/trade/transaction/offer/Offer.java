package me.josvth.trade.transaction.offer;

import me.josvth.trade.transaction.action.trader.offer.ChangeOfferAction;
import me.josvth.trade.transaction.action.trader.offer.SetOfferAction;
import me.josvth.trade.transaction.inventory.slot.Slot;
import me.josvth.trade.transaction.inventory.slot.TradeSlot;
import me.josvth.trade.transaction.offer.click.OfferClickListener;
import me.josvth.trade.transaction.offer.click.OfferClickType;
import me.josvth.trade.transaction.offer.description.OfferDescription;
import me.josvth.trade.transaction.Trader;
import me.josvth.trade.transaction.inventory.TransactionHolder;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Offer {

    private Map<OfferClickType, List<OfferClickListener>> clickListeners = new HashMap<OfferClickType, List<OfferClickListener>>();

    public OfferDescription<? extends Offer> getDescription(Trader trader) {
        return trader.getLayout().getOfferDescription(this.getClass());
    }

    public abstract String getType();

    public abstract ItemStack createItem(TransactionHolder holder);

    public abstract ItemStack createMirrorItem(TransactionHolder holder);

    public abstract void grant(Trader trader);

    public abstract Map<OfferClickType, List<OfferClickListener>> getClickListeners();

    // Event handling
    public void onCursorClick(InventoryClickEvent event, Slot slot) {

        // TODO I know creating all these functions is ugly but its gives the best overview.
        // Especially for overwriting in sub offers.
        switch (event.getClick()) {
            case LEFT:
                onCursorLeftClick(event, slot);
                break;
            case SHIFT_LEFT:
                onCursorShiftLeftClick(event, slot);
                break;
            case RIGHT:
                onCursorRightClick(event, slot);
                break;
            case SHIFT_RIGHT:
                onCursorShiftRightClick(event, slot);
                break;
            case WINDOW_BORDER_LEFT:
                onCursorWindowBorderLeftClick(event, slot);
                break;
            case WINDOW_BORDER_RIGHT:
                onCursorWindowBorderRightClick(event, slot);
                break;
            case MIDDLE:
                onCursorMiddleClick(event, slot);
                break;
            case NUMBER_KEY:
                onCursorNumberKeyClick(event, slot);
                break;
            case DOUBLE_CLICK:
                onCursorDoubleClick(event, slot);
                break;
            case DROP:
                onCursorDropClick(event, slot);
                break;
            case CONTROL_DROP:
                onCursorControlDropClick(event, slot);
                break;
            case CREATIVE:
                onCursorCreativeClick(event, slot);
                break;
            case UNKNOWN:
                onCursorUnknownClick(event, slot);
                break;
        }

    }

    protected void onCursorLeftClick(InventoryClickEvent event, Slot slot) {

        final TransactionHolder holder = (TransactionHolder) event.getInventory().getHolder();

        if (slot == null) {

            final ItemStack item = event.getCurrentItem();

            if (item == null || item.getType() == Material.AIR) {     // GRANT_ALL
                holder.setCursorOffer(null);
                grant(holder.getTrader());
            }

        } else if (slot instanceof TradeSlot) {

            final Offer contents = ((TradeSlot) slot).getContents(holder);

            if (contents == null) { // PLACE_ALL    (in trade slot)

                holder.setCursorOffer(null);

                final SetOfferAction offerAction = new SetOfferAction(holder.getTrader());
                offerAction.setOffer(((TradeSlot) slot).getOfferIndex(), this);
                offerAction.execute();

            } else {                // SWAP_WITH_CURSOR

                holder.setCursorOffer(((TradeSlot) slot).getContents(holder));

                final SetOfferAction offerAction = new SetOfferAction(holder.getTrader());
                offerAction.setOffer(((TradeSlot) slot).getOfferIndex(), this);
                offerAction.execute();

            }

        }

        event.setCancelled(true);

    }

    protected void onCursorShiftLeftClick(InventoryClickEvent event, Slot slot) {

        final TransactionHolder holder = (TransactionHolder) event.getInventory().getHolder();

        if (slot == null) {

            final ItemStack item = event.getCurrentItem();

            if (item != null) {     // MOVE_TO_OTHER_INVENTORY

                final ChangeOfferAction offerAction = new ChangeOfferAction(holder.getTrader());
                offerAction.setOffer(new ItemOffer(item.clone()));
                offerAction.execute();

                if (offerAction.getRemaining() > 0) {
                    item.setAmount(offerAction.getRemaining());
                    event.setCurrentItem(item);     // TODO Check if this is safe to use in event
                } else {
                    event.setCurrentItem(null);     // TODO Check if this is safe to use in event
                }

            }

        } else if (slot instanceof TradeSlot) {

            final Offer contents = ((TradeSlot) slot).getContents(holder);

            if (contents != null) { // MOVE_TO_OTHER_INVENTORY

                contents.grant(holder.getTrader());

                final SetOfferAction offerAction = new SetOfferAction(holder.getTrader());
                offerAction.setOffer(((TradeSlot) slot).getOfferIndex(), null);
                offerAction.execute();

            }

        }

        event.setCancelled(true);
    }

    protected void onCursorRightClick(InventoryClickEvent event, Slot slot) {
        onCursorLeftClick(event, slot);
    }

    protected void onCursorShiftRightClick(InventoryClickEvent event, Slot slot) {
        onCursorShiftLeftClick(event, slot);
    }

    protected void onCursorWindowBorderLeftClick(InventoryClickEvent event, Slot slot) {
        onCursorUnknownClick(event, slot);
    }

    protected void onCursorWindowBorderRightClick(InventoryClickEvent event, Slot slot) {
        onCursorUnknownClick(event, slot);
    }

    protected void onCursorMiddleClick(InventoryClickEvent event, Slot slot) {
        onCursorUnknownClick(event, slot);
    }

    protected void onCursorNumberKeyClick(InventoryClickEvent event, Slot slot) {
        onCursorUnknownClick(event, slot);
    }

    protected void onCursorDoubleClick(InventoryClickEvent event, Slot slot) {
        onCursorUnknownClick(event, slot);
    }

    protected void onCursorDropClick(InventoryClickEvent event, Slot slot) {
        onCursorUnknownClick(event, slot);
    }

    protected void onCursorControlDropClick(InventoryClickEvent event, Slot slot) {
        onCursorUnknownClick(event, slot);
    }

    protected void onCursorCreativeClick(InventoryClickEvent event, Slot slot) {
        onCursorUnknownClick(event, slot);
    }

    protected void onCursorUnknownClick(InventoryClickEvent event, Slot slot) {
        event.setCancelled(true);
    }

    public void onSlotClick(InventoryClickEvent event, Slot slot){

        switch (event.getClick()) {
            case LEFT:
                onSlotLeftClick(event, slot);
                break;
            case SHIFT_LEFT:
                onSlotShiftLeftClick(event, slot);
                break;
            case RIGHT:
                onSlotRightClick(event, slot);
                break;
            case SHIFT_RIGHT:
                onSlotShiftRightClick(event, slot);
                break;
            case WINDOW_BORDER_LEFT:
                onSlotWindowBorderLeftClick(event, slot);
                break;
            case WINDOW_BORDER_RIGHT:
                onSlotWindowBorderRightClick(event, slot);
                break;
            case MIDDLE:
                onSlotMiddleClick(event, slot);
                break;
            case NUMBER_KEY:
                onSlotNumberKeyClick(event, slot);
                break;
            case DOUBLE_CLICK:
                onSlotDoubleClick(event, slot);
                break;
            case DROP:
                onSlotDropClick(event, slot);
                break;
            case CONTROL_DROP:
                onSlotControlDropClick(event, slot);
                break;
            case CREATIVE:
                onSlotCreativeClick(event, slot);
                break;
            case UNKNOWN:
                onSlotUnknownClick(event, slot);
                break;
        }

    }

    protected void onSlotLeftClick(InventoryClickEvent event, Slot slot) {

        if (slot instanceof TradeSlot) {

            final TransactionHolder holder = (TransactionHolder) event.getInventory().getHolder();

            holder.setCursorOffer(this);

            SetOfferAction offerAction = new SetOfferAction(holder.getTrader());
            offerAction.setOffer(((TradeSlot) slot).getOfferIndex(), null);
            offerAction.execute();

        }

        event.setCancelled(true);

    }

    protected void onSlotShiftLeftClick(InventoryClickEvent event, Slot slot) {

        if (slot instanceof TradeSlot) {

            final TransactionHolder holder = (TransactionHolder) event.getInventory().getHolder();

            grant(holder.getTrader());

            SetOfferAction offerAction = new SetOfferAction(holder.getTrader());
            offerAction.setOffer(((TradeSlot) slot).getOfferIndex(), null);
            offerAction.execute();

        }

        event.setCancelled(true);

    }

    protected void onSlotRightClick(InventoryClickEvent event, Slot slot) {
        onSlotLeftClick(event, slot);
    }

    protected void onSlotShiftRightClick(InventoryClickEvent event, Slot slot) {
        onSlotShiftLeftClick(event, slot);
    }

    protected void onSlotWindowBorderLeftClick(InventoryClickEvent event, Slot slot) {
        onSlotUnknownClick(event, slot);
    }

    protected void onSlotWindowBorderRightClick(InventoryClickEvent event, Slot slot) {
        onSlotUnknownClick(event, slot);
    }

    protected void onSlotMiddleClick(InventoryClickEvent event, Slot slot) {
        onSlotUnknownClick(event, slot);
    }

    protected void onSlotNumberKeyClick(InventoryClickEvent event, Slot slot) {
        onSlotUnknownClick(event, slot);
    }

    protected void onSlotDoubleClick(InventoryClickEvent event, Slot slot) {
        onSlotUnknownClick(event, slot);
    }

    protected void onSlotDropClick(InventoryClickEvent event, Slot slot) {
        onSlotUnknownClick(event, slot);
    }

    protected void onSlotControlDropClick(InventoryClickEvent event, Slot slot) {
        onSlotUnknownClick(event, slot);
    }

    protected void onSlotCreativeClick(InventoryClickEvent event, Slot slot) {
        onSlotUnknownClick(event, slot);
    }

    protected void onSlotUnknownClick(InventoryClickEvent event, Slot slot) {
        event.setCancelled(true);
    }

    public void onDrag(InventoryDragEvent event, int offerIndex, int slotIndex) {
        event.setCancelled(true);
    }

    public boolean isDraggable() {
        return false;
    }

    public abstract Offer clone();

}
