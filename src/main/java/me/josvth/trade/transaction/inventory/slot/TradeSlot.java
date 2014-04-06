package me.josvth.trade.transaction.inventory.slot;

import me.josvth.trade.Trade;
import me.josvth.trade.tasks.SlotUpdateTask;
import me.josvth.trade.transaction.action.trader.offer.ChangeOfferAction;
import me.josvth.trade.transaction.action.trader.offer.SetOfferAction;
import me.josvth.trade.transaction.inventory.TransactionHolder;
import me.josvth.trade.transaction.inventory.interact.ClickBehaviour;
import me.josvth.trade.transaction.inventory.interact.ClickContext;
import me.josvth.trade.transaction.inventory.offer.Offer;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.ClickType;

import java.util.*;

public class TradeSlot extends ContentSlot {

    private static final Map<ClickType, List<ClickBehaviour>> DEFAULT_BEHAVIOURS = new LinkedHashMap<ClickType, List<ClickBehaviour>>();

    static {

        DEFAULT_BEHAVIOURS.put(ClickType.SHIFT_LEFT, new LinkedList<ClickBehaviour>());
        DEFAULT_BEHAVIOURS.put(ClickType.SHIFT_RIGHT, new LinkedList<ClickBehaviour>());
        DEFAULT_BEHAVIOURS.put(ClickType.NUMBER_KEY, new LinkedList<ClickBehaviour>());

        // MOVE_TO_OTHER_INVENTORY
        final ClickBehaviour shiftBehaviour = new ClickBehaviour() {
            @Override
            public boolean onClick(ClickContext context, Offer offer) {

                final TradeSlot tradeSlot = (TradeSlot) context.getSlot();

                Offer contents = tradeSlot.getContents();

                if (contents != null) {

                    final ChangeOfferAction action = new ChangeOfferAction(context.getHolder().getTrader(), context.getHolder().getInventoryList(), contents);
                    action.execute();

                    if (action.getRemaining() > 0) {
                        contents.setAmount(action.getRemaining());
                    } else {
                        contents = null;
                    }

                    // Update inventory slot
                    tradeSlot.setContents(contents);

                    context.getEvent().setCancelled(true);

                    return true;
                }

                return false;
            }
        };
        DEFAULT_BEHAVIOURS.get(ClickType.SHIFT_LEFT).add(shiftBehaviour);
        DEFAULT_BEHAVIOURS.get(ClickType.SHIFT_RIGHT).add(shiftBehaviour);

        // HOTBAR_SWAP
        DEFAULT_BEHAVIOURS.get(ClickType.NUMBER_KEY).add(new ClickBehaviour() {
            @Override
            public boolean onClick(ClickContext context, Offer offer) {

                final TradeSlot slot = (TradeSlot) context.getSlot();

                if (slot.getContents() != null && context.getCursorOffer() == null) {

                    final InventorySlot inventorySlot = (InventorySlot) context.getHolder().getSlots()[context.getHolder().getSlots().length - 9 + context.getEvent().getHotbarButton()];

                    // Add the inventory offer to the inventory list
                    final ChangeOfferAction action = new ChangeOfferAction(context.getTrader(), context.getInventoryList(), inventorySlot.getContents());
                    action.execute();

                    // Update inventory slot
                    inventorySlot.setContents(slot.getContents());

                    // Update trade slot
                    slot.setContents(null);

                    context.setCancelled(true);

                    return true;
                }

                return false;
            }
        });

    }

    private int offerIndex;

    public TradeSlot(int slot, TransactionHolder holder) {
        super(slot, holder);
        addClickBehaviours(DEFAULT_BEHAVIOURS);
    }

    public static void updateTradeSlots(TransactionHolder holder, boolean nextTick, int... offerIndex) {

        final Set<TradeSlot> slots = holder.getSlotsOfType(TradeSlot.class);

        final Iterator<TradeSlot> iterator = slots.iterator();

        while (iterator.hasNext()) {

            final TradeSlot slot = iterator.next();

            boolean notUpdated = true;
            for (int i = 0; i < offerIndex.length && notUpdated; i++) {
                if (slot.getOfferIndex() == offerIndex[i]) {
                    if (!nextTick) {
                        slot.update();
                    }
                    notUpdated = false;
                }
            }

            if (notUpdated) {
                iterator.remove();
            }

        }

        if (nextTick && !slots.isEmpty()) {
            Bukkit.getScheduler().runTask(Trade.getInstance(), new SlotUpdateTask(slots));
        }

    }

    public static TradeSlot deserialize(int slotID, TransactionHolder holder, SlotDescription description) {
        final TradeSlot slot = new TradeSlot(slotID, holder);
        slot.setOfferIndex(description.getConfiguration().getInt("offer-index", 0));
        return slot;
    }

    public int getOfferIndex() {
        return offerIndex;
    }

    public void setOfferIndex(int offerIndex) {
        this.offerIndex = offerIndex;
    }

    @Override
    public Offer getContents() {
        return holder.getOfferList().get(getOfferIndex());
    }

    @Override
    public void setContents(Offer offer) {
        SetOfferAction offerAction = new SetOfferAction(holder.getTrader(), holder.getOfferList());
        offerAction.setOffer(getOfferIndex(), offer);
        offerAction.execute();
    }

    @Override
    public void update() {

        final Offer offer = getContents();

        if (offer != null) {
            holder.getInventory().setItem(slot, offer.createItem(holder));
        } else {
            holder.getInventory().setItem(slot, null);
        }

    }

}
