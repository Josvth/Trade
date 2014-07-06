package me.josvth.trade.transaction.inventory.slot;

import me.josvth.trade.transaction.inventory.TransactionHolder;
import me.josvth.trade.transaction.inventory.interact.ClickBehaviour;
import me.josvth.trade.transaction.inventory.interact.ClickContext;
import me.josvth.trade.transaction.inventory.interact.DragBehaviour;
import me.josvth.trade.transaction.inventory.interact.DragContext;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.DragType;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public abstract class Slot {

    protected final int slot;
    protected final TransactionHolder holder;

    protected final Map<ClickType, LinkedList<ClickBehaviour>> clickBehaviourMap = new HashMap<ClickType, LinkedList<ClickBehaviour>>();
    protected final Map<DragType, LinkedList<DragBehaviour>> dragBehaviourMap = new HashMap<DragType, LinkedList<DragBehaviour>>();

    public Slot(int slot, TransactionHolder holder) {
        this.slot = slot;
        this.holder = holder;
    }

    public int getSlot() {
        return slot;
    }

    protected ItemStack getGUIItem() {
        return holder.getInventory().getItem(slot);
    }

    protected void setGUIItem(ItemStack stack) {
        holder.getInventory().setItem(slot, stack);
    }

    // Behaviours
    public void addClickBehaviour(ClickType clickType, ClickBehaviour behaviour) {
        LinkedList<ClickBehaviour> behaviours = clickBehaviourMap.get(clickType);
        if (behaviours == null) {
            behaviours = new LinkedList<ClickBehaviour>();
            clickBehaviourMap.put(clickType, behaviours);
        }
        behaviours.add(behaviour);
    }

    public void addClickBehaviours(Map<ClickType, List<ClickBehaviour>> behaviours) {
        for (Map.Entry<ClickType, List<ClickBehaviour>> entry : behaviours.entrySet()) {
            for (ClickBehaviour behaviour : entry.getValue()) {
                addClickBehaviour(entry.getKey(), behaviour);
            }
        }
    }

    public void addDragBehaviour(DragType dragType, DragBehaviour behaviour) {
        LinkedList<DragBehaviour> behaviours = dragBehaviourMap.get(dragType);
        if (behaviours == null) {
            behaviours = new LinkedList<DragBehaviour>();
            dragBehaviourMap.put(dragType, behaviours);
        }
        behaviours.add(behaviour);
    }

    public void addDragBehaviours(Map<DragType, List<DragBehaviour>> behaviours) {
        for (Map.Entry<DragType, List<DragBehaviour>> entry : behaviours.entrySet()) {
            for (DragBehaviour behaviour : entry.getValue()) {
                addDragBehaviour(entry.getKey(), behaviour);
            }
        }
    }

    // Event handling
    public boolean onClick(ClickContext context) {

        final List<ClickBehaviour> behaviours = clickBehaviourMap.get(context.getEvent().getClick());

        if (behaviours != null) {

            final ListIterator<ClickBehaviour> iterator = behaviours.listIterator(behaviours.size());

            while (iterator.hasPrevious() && !context.isHandled()) {

                final ClickBehaviour behaviour = iterator.previous();

                if (behaviour.onClick(context, null)) {
                    context.setHandled(true);
                    context.setExecutedBehaviour(behaviour);
                }

            }

        }

        return false;

    }

    public void onDrag(DragContext context) {

        final List<DragBehaviour> behaviours = dragBehaviourMap.get(context.getEvent().getType());

        if (behaviours != null) {

            final ListIterator<DragBehaviour> iterator = behaviours.listIterator(behaviours.size());

            boolean executed = false;

            while (iterator.hasPrevious() && !executed) {
                executed = iterator.previous().onDrag(context, this, null);
            }

            if (!executed) {
                context.getEvent().setCancelled(true);
            }

        } else {
            context.getEvent().setCancelled(true);
        }

    }

    public void update() {

    }

    @Override
    public String toString() {
        return new StringBuilder(getClass().getSimpleName()).toString();
    }

}
