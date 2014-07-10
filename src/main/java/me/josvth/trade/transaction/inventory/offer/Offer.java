package me.josvth.trade.transaction.inventory.offer;


import me.josvth.trade.transaction.Trader;
import me.josvth.trade.transaction.inventory.TransactionHolder;
import me.josvth.trade.transaction.inventory.interact.ClickBehaviour;
import me.josvth.trade.transaction.inventory.interact.ClickContext;
import me.josvth.trade.transaction.inventory.interact.DragBehaviour;
import me.josvth.trade.transaction.inventory.interact.DragContext;
import me.josvth.trade.transaction.inventory.offer.description.OfferDescription;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.DragType;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public abstract class Offer {

    private static final Map<ClickType, List<ClickBehaviour>> DEFAULT_CURSOR_CLICK_BEHAVIOUR = new HashMap<ClickType, List<ClickBehaviour>>();
    private static final Map<ClickType, List<ClickBehaviour>> DEFAULT_CONTENT_CLICK_BEHAVIOUR = new HashMap<ClickType, List<ClickBehaviour>>();

    private static final Map<DragType, List<DragBehaviour>> DEFAULT_CURSOR_DRAG_BEHAVIOUR = new HashMap<DragType, List<DragBehaviour>>();
    private static final Map<DragType, List<DragBehaviour>> DEFAULT_CONTENT_DRAG_BEHAVIOUR = new HashMap<DragType, List<DragBehaviour>>();

    private final Map<ClickType, List<ClickBehaviour>> cursorClickBehaviourMap = DEFAULT_CURSOR_CLICK_BEHAVIOUR;
    private final Map<ClickType, List<ClickBehaviour>> contentClickBehaviourMap = DEFAULT_CONTENT_CLICK_BEHAVIOUR;

    protected boolean allowedInInventory = false;
    protected boolean canStayInInventory = false;

    //TODO Cleanup offer creation and cloning
    public static <T extends Offer> T split(T offer) {

        final double total = offer.getAmount();

        final T clone = (T) offer.clone();

        offer.setAmount(total / 2);

        clone.setAmount(total - offer.getAmount());

        return clone;

    }

    public static <T extends Offer> T takeOne(T offer) {

        if (offer.getAmount() == 1) {
            throw new IllegalArgumentException("StackableOffer must have an amount greater than 1");
        }

        final T clone = (T) offer.clone();

        offer.setAmount(offer.getAmount() - 1);

        clone.setAmount(1);

        return clone;

    }

    public abstract double getAmount();

    public abstract void setAmount(double amount);

    public abstract int getMaxAmount();

    public boolean isFull() {
        return getAmount() == getMaxAmount();
    }

    public boolean isWorthless() {
        return getAmount() == 0;
    }

    public double add(double amount) {

        if (getMaxAmount() == -1) { // If infinite stackable, add all
            setAmount(getAmount() + amount);
            return 0;
        }

        if (isFull()) {
            return amount;
        }

        final double remainder = getAmount() + amount - getMaxAmount();
        if (remainder > 0) {
            setAmount(getMaxAmount());
            return remainder;
        } else {
            setAmount(getAmount() + amount);
            return 0;
        }

    }

    public double remove(double amount) {
        final double remainder = getAmount() - amount;
        if (remainder > 0) {
            setAmount(remainder);
            return 0;
        } else {
            setAmount(0);
            return -1 * remainder;
        }
    }

    public abstract void grant(Trader trader, boolean nextTick, double amount);

    public abstract Offer clone();

    public abstract boolean isSimilar(Offer offer);

    public OfferDescription<? extends Offer> getDescription(Trader trader) {
        return trader.getLayout().getOfferDescription(this.getClass());
    }

    public abstract String getType();

    public abstract ItemStack createItem(TransactionHolder holder);

    public abstract ItemStack createMirrorItem(TransactionHolder holder);

    public abstract void grant(Trader trader, boolean nextTick);

    public boolean isAllowedInInventory() {
        return allowedInInventory;
    }

    public void setAllowedInInventory(boolean allowedInInventory) {
        this.allowedInInventory = allowedInInventory;
    }

    public boolean isCanStayInInventory() {
        return canStayInInventory;
    }

    public void setCanStayInInventory(boolean canStayInInventory) {
        this.canStayInInventory = canStayInInventory;
    }

    public boolean canStayInInventory() {
        return canStayInInventory;
    }

    // Behaviours
    public void addCursorBehaviour(ClickType clickType, ClickBehaviour behaviour) {
        List<ClickBehaviour> behaviours = cursorClickBehaviourMap.get(clickType);
        if (behaviours == null) {
            behaviours = new LinkedList<ClickBehaviour>();
            cursorClickBehaviourMap.put(clickType, behaviours);
        }
        behaviours.add(behaviour);
    }

    public void addCursorBehaviours(Map<ClickType, List<ClickBehaviour>> behaviours) {
        for (Map.Entry<ClickType, List<ClickBehaviour>> entry : behaviours.entrySet()) {
            for (ClickBehaviour behaviour : entry.getValue()) {
                addCursorBehaviour(entry.getKey(), behaviour);
            }
        }
    }

    public void addContentBehaviour(ClickType clickType, ClickBehaviour behaviour) {
        List<ClickBehaviour> behaviours = contentClickBehaviourMap.get(clickType);
        if (behaviours == null) {
            behaviours = new LinkedList<ClickBehaviour>();
            contentClickBehaviourMap.put(clickType, behaviours);
        }
        behaviours.add(behaviour);
    }

    public void addContentBehaviours(Map<ClickType, List<ClickBehaviour>> behaviours) {
        for (Map.Entry<ClickType, List<ClickBehaviour>> entry : behaviours.entrySet()) {
            for (ClickBehaviour behaviour : entry.getValue()) {
                addContentBehaviour(entry.getKey(), behaviour);
            }
        }
    }

    public Map<ClickType, List<ClickBehaviour>> getCursorClickBehaviourMap() {
        return cursorClickBehaviourMap;
    }

    public Map<ClickType, List<ClickBehaviour>> getContentClickBehaviourMap() {
        return contentClickBehaviourMap;
    }

    // Event handling
    public void onCursorClick(ClickContext context) {

        final List<ClickBehaviour> behaviours = getCursorClickBehaviourMap().get(context.getEvent().getClick());

        if (behaviours != null) {

            final ListIterator<ClickBehaviour> iterator = behaviours.listIterator(behaviours.size());

            while (iterator.hasPrevious() && !context.isHandled()) {
                final ClickBehaviour behaviour = iterator.previous();

                if (behaviour.onClick(context, this)) {
                    context.setHandled(true);
                    context.setExecutedBehaviour(behaviour);
                }
            }

        }

    }

    public boolean onCursorDrag(DragContext context) {
        return false;
    }

    public boolean onDrag(InventoryDragEvent event, int offerIndex, int slotIndex) {
        event.setCancelled(true);
        return true;
    }

    public void onContentClick(ClickContext context) {

        final List<ClickBehaviour> behaviours = getCursorClickBehaviourMap().get(context.getEvent().getClick());

        if (behaviours != null) {

            final ListIterator<ClickBehaviour> iterator = behaviours.listIterator(behaviours.size());

            while (iterator.hasPrevious() && !context.isHandled()) {
                final ClickBehaviour behaviour = iterator.previous();

                if (behaviour.onClick(context, this)) {
                    context.setHandled(true);
                    context.setExecutedBehaviour(behaviour);
                }
            }

        }

    }

    public boolean isDraggable() {
        return false;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" + getAmount() + "}";
    }
}
