package me.josvth.trade.transaction.inventory.slot;


import me.josvth.trade.Trade;
import me.josvth.trade.tasks.SlotUpdateTask;
import me.josvth.trade.transaction.action.trader.offer.ChangeOfferAction;
import me.josvth.trade.transaction.action.trader.offer.SetOfferAction;
import me.josvth.trade.transaction.inventory.LayoutManager;
import me.josvth.trade.transaction.inventory.TransactionHolder;
import me.josvth.trade.transaction.inventory.interact.ClickBehaviour;
import me.josvth.trade.transaction.inventory.interact.ClickContext;
import me.josvth.trade.transaction.inventory.offer.Offer;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.ClickType;

import java.util.*;

public class InventorySlot extends ContentSlot {

    private static final Map<ClickType, List<ClickBehaviour>> DEFAULT_BEHAVIOURS = new LinkedHashMap<ClickType, List<ClickBehaviour>>();

    static {

        DEFAULT_BEHAVIOURS.put(ClickType.SHIFT_LEFT, new LinkedList<ClickBehaviour>());
        DEFAULT_BEHAVIOURS.put(ClickType.SHIFT_RIGHT, new LinkedList<ClickBehaviour>());
        DEFAULT_BEHAVIOURS.put(ClickType.NUMBER_KEY, new LinkedList<ClickBehaviour>());

        // MOVE_TO_OTHER_INVENTORY
        final ClickBehaviour shiftBehaviour = new ClickBehaviour() {
            @Override
            public boolean onClick(ClickContext context, Offer offer) {

                final InventorySlot inventorySlot = (InventorySlot) context.getSlot();

                Offer contents = inventorySlot.getContents();

                if (contents != null) {

                    final ChangeOfferAction action = new ChangeOfferAction(context.getHolder().getTrader(), context.getOffersList(), contents);
                    action.execute();

                    if (action.getRemaining() > 0) {
                        contents.setAmount(action.getRemaining());
                    } else {
                        contents = null;
                    }

                    // Update inventory slot
                    inventorySlot.setContents(contents);

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

                final InventorySlot slot = (InventorySlot) context.getSlot();

                if (slot.getContents() != null && context.getCursorOffer() == null) {

                    final InventorySlot inventorySlot = (InventorySlot) context.getHolder().getSlots()[context.getHolder().getSlots().length - 9 + context.getEvent().getHotbarButton()];

                    final Offer inventoryOffer = inventorySlot.getContents();

                    // Update inventory slot
                    inventorySlot.setContents(slot.getContents());

                    // Update content slot
                    slot.setContents(inventoryOffer);

                    context.setCancelled(true);

                    return true;
                }

                return false;
            }
        });

    }

    private int inventorySlot = 0;

    public InventorySlot(int slot, TransactionHolder holder) {
        super(slot, holder);
        addClickBehaviours(DEFAULT_BEHAVIOURS);
    }

    public static void updateInventorySlots(TransactionHolder holder, boolean nextTick, int... inventorySlot) {

        final Set<InventorySlot> slots = holder.getSlotsOfType(InventorySlot.class);

        final Iterator<InventorySlot> iterator = slots.iterator();

        while (iterator.hasNext()) {

            final InventorySlot slot = iterator.next();

            boolean notUpdated = true;
            for (int i = 0; i < inventorySlot.length && notUpdated; i++) {
                if (slot.getInventorySlot() == inventorySlot[i]) {
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

    public static InventorySlot deserialize(int slotID, TransactionHolder holder, SlotDescription description) {
        final InventorySlot slot = new InventorySlot(slotID, holder);
        slot.setInventorySlot(slotID - LayoutManager.PLAYER_INVENTORY_SIZE);
        return slot;
    }

    public int getInventorySlot() {
        return inventorySlot;
    }

    public void setInventorySlot(int inventorySlot) {
        this.inventorySlot = inventorySlot;
    }

    @Override
    public Offer getContents() {
        return holder.getInventoryList().get(inventorySlot);
    }

    @Override
    public void setContents(Offer contents) {
        SetOfferAction offerAction = new SetOfferAction(holder.getTrader(), holder.getInventoryList());
        offerAction.setOffer(getInventorySlot(), contents);
        offerAction.execute();
    }

    @Override
    public void update() {
        holder.getTrader().getPlayer().getInventory().setItem(getInventorySlot(), (getContents() == null) ? null : getContents().createItem(holder));
    }

}
