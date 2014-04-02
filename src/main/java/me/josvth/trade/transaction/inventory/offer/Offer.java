package me.josvth.trade.transaction.inventory.offer;

import me.josvth.trade.transaction.Trader;
import me.josvth.trade.transaction.inventory.click.ClickBehaviour;
import me.josvth.trade.transaction.inventory.click.ClickContext;
import me.josvth.trade.transaction.inventory.TransactionHolder;
import me.josvth.trade.transaction.inventory.offer.description.OfferDescription;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public abstract class Offer {

    protected final Map<ClickType, List<ClickBehaviour>> cursorClickBehaviourMap = new HashMap<ClickType, List<ClickBehaviour>>();
    protected final Map<ClickType, List<ClickBehaviour>> contentClickBehaviourMap = new HashMap<ClickType, List<ClickBehaviour>>();

    protected boolean allowedInInventory = false;
    protected boolean canStayInInventory = false;

    public Offer() {

    }

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

    public boolean canStayInInventory() {
        return canStayInInventory;
    }

    public void setCanStayInInventory(boolean canStayInInventory) {
        this.canStayInInventory = canStayInInventory;
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
    public boolean onCursorClick(ClickContext context) {

        final List<ClickBehaviour> behaviours = getCursorClickBehaviourMap().get(context.getEvent().getClick());

        if (behaviours != null) {

            final ListIterator<ClickBehaviour> iterator = behaviours.listIterator(behaviours.size());

            boolean executed = false;

            while (iterator.hasPrevious() && !executed) {
                executed = iterator.previous().onClick(context, this);
            }

            if (executed) {
                return true;
            }

        }

        return false;

    }

    public boolean onDrag(InventoryDragEvent event, int offerIndex, int slotIndex) {
        event.setCancelled(true);
        return true;
    }

    public boolean onContentClick(ClickContext context) {

        final List<ClickBehaviour> behaviours = getContentClickBehaviourMap().get(context.getEvent().getClick());

        if (behaviours != null) {

            final ListIterator<ClickBehaviour> iterator = behaviours.listIterator(behaviours.size());

            boolean executed = false;

            while (iterator.hasPrevious() && !executed) {
                executed = iterator.previous().onClick(context, this);
            }

            if (executed) {
                return true;
            }

        }

        return false;

    }

    public boolean isDraggable() {
        return false;
    }

    public abstract Offer clone();

}
