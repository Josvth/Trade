package me.josvth.trade.transaction.offer;

import me.josvth.trade.transaction.Trader;
import me.josvth.trade.transaction.action.trader.offer.ChangeOfferAction;
import me.josvth.trade.transaction.action.trader.offer.SetOfferAction;
import me.josvth.trade.transaction.inventory.TransactionHolder;
import me.josvth.trade.transaction.inventory.slot.Slot;
import me.josvth.trade.transaction.inventory.slot.TradeSlot;
import me.josvth.trade.transaction.offer.behaviour.ClickBehaviour;
import me.josvth.trade.transaction.offer.behaviour.OfferClickType;
import me.josvth.trade.transaction.offer.description.OfferDescription;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public abstract class Offer {

    private static final Map<OfferClickType, LinkedList<ClickBehaviour>> DEFAULT_BEHAVIOUR = new HashMap<OfferClickType, LinkedList<ClickBehaviour>>();

    static {

        final ClickBehaviour grantAll = new ClickBehaviour() {
            @Override
            public boolean onClick(InventoryClickEvent event, Slot slot, Offer offer) {

                if (slot == null) {

                    final TransactionHolder holder = (TransactionHolder) event.getInventory().getHolder();

                    final ItemStack item = event.getCurrentItem();

                    if (item == null || item.getType() == Material.AIR) {
                        holder.setCursorOffer(null);
                        offer.grant(holder.getTrader());
                        return true;
                    }

                }

                return false;

            }
        };

        final ClickBehaviour placeAll = new ClickBehaviour() {
            @Override
            public boolean onClick(InventoryClickEvent event, Slot slot, Offer offer) {

                if (slot instanceof TradeSlot) {

                    final TransactionHolder holder = (TransactionHolder) event.getInventory().getHolder();

                    final Offer contents = ((TradeSlot) slot).getContents(holder);

                    if (contents != null) {

                        holder.setCursorOffer(null);

                        final SetOfferAction offerAction = new SetOfferAction(holder.getTrader());
                        offerAction.setOffer(((TradeSlot) slot).getOfferIndex(), offer);
                        offerAction.execute();

                        return true;
                    }

                }

                return false;

            }
        };

        final ClickBehaviour swapWithCursor = new ClickBehaviour() {

            @Override
            public boolean onClick(InventoryClickEvent event, Slot slot, Offer offer) {

                if (slot instanceof TradeSlot) {

                    final TransactionHolder holder = (TransactionHolder) event.getInventory().getHolder();

                    final Offer contents = ((TradeSlot) slot).getContents(holder);

                    if (contents == null) {

                        holder.setCursorOffer(((TradeSlot) slot).getContents(holder));

                        final SetOfferAction offerAction = new SetOfferAction(holder.getTrader());
                        offerAction.setOffer(((TradeSlot) slot).getOfferIndex(), offer);
                        offerAction.execute();

                        return true;
                    }

                }

                return false;

            }

        };

        final LinkedList<ClickBehaviour> cursorLeft = new LinkedList<ClickBehaviour>();
        cursorLeft.add(grantAll);
        cursorLeft.add(placeAll);
        cursorLeft.add(swapWithCursor);

        DEFAULT_BEHAVIOUR.put(OfferClickType.CURSOR_LEFT, cursorLeft);
        DEFAULT_BEHAVIOUR.put(OfferClickType.CURSOR_RIGHT, new LinkedList<ClickBehaviour>(cursorLeft));

        final ClickBehaviour moveItemToOtherInventory = new ClickBehaviour() {

            @Override
            public boolean onClick(InventoryClickEvent event, Slot slot, Offer offer) {

                if (slot == null) {

                    final TransactionHolder holder = (TransactionHolder) event.getInventory().getHolder();

                    final ItemStack item = event.getCurrentItem();

                    if (item != null) {

                        final ChangeOfferAction offerAction = new ChangeOfferAction(holder.getTrader());
                        offerAction.setOffer(new ItemOffer(item.clone()));
                        offerAction.execute();

                        if (offerAction.getRemaining() > 0) {
                            item.setAmount(offerAction.getRemaining());
                            event.setCurrentItem(item);     // TODO Check if this is safe to use in event
                        } else {
                            event.setCurrentItem(null);     // TODO Check if this is safe to use in event
                        }

                        return true;

                    }

                }

                return false;

            }

        };

        final ClickBehaviour grantContents = new ClickBehaviour() {

            @Override
            public boolean onClick(InventoryClickEvent event, Slot slot, Offer offer) {

                if (slot instanceof TradeSlot) {

                    final TransactionHolder holder = (TransactionHolder) event.getInventory().getHolder();

                    final Offer contents = ((TradeSlot) slot).getContents(holder);

                    if (contents != null) {

                        contents.grant(holder.getTrader());

                        final SetOfferAction offerAction = new SetOfferAction(holder.getTrader());
                        offerAction.setOffer(((TradeSlot) slot).getOfferIndex(), null);
                        offerAction.execute();

                        return true;

                    }

                }

                return false;

            }

        };

        final LinkedList<ClickBehaviour> cursorShiftLeft = new LinkedList<ClickBehaviour>();
        cursorShiftLeft.add(moveItemToOtherInventory);
        cursorShiftLeft.add(grantContents);

        DEFAULT_BEHAVIOUR.put(OfferClickType.CURSOR_SHIFT_LEFT, cursorShiftLeft);
        DEFAULT_BEHAVIOUR.put(OfferClickType.CURSOR_SHIFT_RIGHT, new LinkedList<ClickBehaviour>(cursorShiftLeft));

    }

    private Map<OfferClickType, LinkedList<ClickBehaviour>> behaviour = new HashMap<OfferClickType, LinkedList<ClickBehaviour>>();

    public Offer() {
        behaviour.putAll(DEFAULT_BEHAVIOUR);
    }

    public OfferDescription<? extends Offer> getDescription(Trader trader) {
        return trader.getLayout().getOfferDescription(this.getClass());
    }

    public abstract String getType();

    public abstract ItemStack createItem(TransactionHolder holder);

    public abstract ItemStack createMirrorItem(TransactionHolder holder);

    public abstract void grant(Trader trader);

    public Map<OfferClickType, LinkedList<ClickBehaviour>> getBehaviour() {
        return behaviour;
    }

    protected void addBehaviour(OfferClickType clickType, ClickBehaviour behaviour) {

        LinkedList<ClickBehaviour> list = getBehaviour().get(clickType);

        if (list == null) {
            list = new LinkedList<ClickBehaviour>();
            getBehaviour().put(clickType, list);
        }

        list.addFirst(behaviour);

    }

    // Event handling
    public void onCursorClick(InventoryClickEvent event, Slot slot) {

    }

    public void onSlotClick(InventoryClickEvent event, Slot slot) {

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
