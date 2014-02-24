package me.josvth.trade.transaction.offer;

import me.josvth.trade.transaction.action.trader.offer.ChangeOfferAction;
import me.josvth.trade.transaction.action.trader.offer.SetOfferAction;
import me.josvth.trade.transaction.inventory.slot.Slot;
import me.josvth.trade.transaction.inventory.slot.TradeSlot;
import me.josvth.trade.transaction.offer.description.OfferDescription;
import me.josvth.trade.transaction.Trader;
import me.josvth.trade.transaction.inventory.TransactionHolder;
import me.josvth.trade.util.OfferUtils;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

public abstract class Offer {

    public OfferDescription<? extends Offer> getDescription(Trader trader) {
        return trader.getLayout().getOfferDescription(this.getClass());
    }

    public abstract String getType();

    public abstract ItemStack createItem(TransactionHolder holder);

    public abstract ItemStack createMirrorItem(TransactionHolder holder);

    public abstract void grant(Trader trader);

    // Event handling
    public void onCursorClick(InventoryClickEvent event, Slot slot) {

        final TransactionHolder holder = (TransactionHolder) event.getInventory().getHolder();

        switch (event.getClick()) {
            case LEFT:
            case RIGHT:

                if (slot == null) {

                    final ItemStack item = event.getCurrentItem();

                    if (item == null || item.getType() == Material.AIR) {     // GRANT
                        holder.setCursorOffer(null);
                        grant(holder.getTrader());
                    }

                    event.setCancelled(true);
                    return;

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
                return;
            case SHIFT_LEFT:
            case SHIFT_RIGHT:

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

                        grant(holder.getTrader());

                        final SetOfferAction offerAction = new SetOfferAction(holder.getTrader());
                        offerAction.setOffer(((TradeSlot) slot).getOfferIndex(), null);
                        offerAction.execute();

                    }

                }

                event.setCancelled(true);
                return;
            default: throw new IllegalStateException("UNHANDLED ACTION: " + event.getAction().name());

        }

    }

    public void onSlotClick(InventoryClickEvent event, Slot slot){

        if (!(slot instanceof TradeSlot)) {
            event.setCancelled(true);
            return;
        }

        final TransactionHolder holder = (TransactionHolder) event.getInventory().getHolder();

        switch (event.getClick()) {
            case LEFT:
            case RIGHT:

                holder.setCursorOffer(this);

                SetOfferAction offerAction = new SetOfferAction(holder.getTrader());
                offerAction.setOffer(((TradeSlot) slot).getOfferIndex(), null);
                offerAction.execute();

                event.setCancelled(true);
                return;
            case SHIFT_LEFT:
            case SHIFT_RIGHT:

                grant(holder.getTrader());

                offerAction = new SetOfferAction(holder.getTrader());
                offerAction.setOffer(((TradeSlot) slot).getOfferIndex(), null);
                offerAction.execute();

                event.setCancelled(true);
                return;
            default: throw new IllegalStateException("UNHANDLED ACTION: " + event.getAction().name());
        }

    }

    public void onDrag(InventoryDragEvent event, int offerIndex, int slotIndex) {
        event.setCancelled(true);
    }

    public boolean isDraggable() {
        return false;
    }

    public abstract Offer clone();

}
