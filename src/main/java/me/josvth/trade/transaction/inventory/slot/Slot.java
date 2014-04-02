package me.josvth.trade.transaction.inventory.slot;

import me.josvth.trade.transaction.inventory.click.ClickBehaviour;
import me.josvth.trade.transaction.inventory.click.ClickContext;
import me.josvth.trade.transaction.inventory.TransactionHolder;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public abstract class Slot {

	protected final int slot;
    protected final TransactionHolder holder;

    protected final Map<ClickType, LinkedList<ClickBehaviour>> clickBehaviourMap = new HashMap<ClickType, LinkedList<ClickBehaviour>>();

	public Slot(int slot, TransactionHolder holder) {
        this.slot = slot;
        this.holder = holder;
    }

    public int getSlot() {
        return slot;
    }

    protected void setGUIItem(ItemStack stack) {
		holder.getInventory().setItem(slot, stack);
	}

	protected ItemStack getGUIItem() {
		return holder.getInventory().getItem(slot);
	}

    // Behaviours
    public void addBehaviour(ClickType clickType, ClickBehaviour behaviour) {
        LinkedList<ClickBehaviour> behaviours = clickBehaviourMap.get(clickType);
        if (behaviours == null) {
            behaviours = new LinkedList<ClickBehaviour>();
            clickBehaviourMap.put(clickType, behaviours);
        }
        behaviours.add(behaviour);
    }

    public void addBehaviours(Map<ClickType, List<ClickBehaviour>> behaviours) {
        for (Map.Entry<ClickType, List<ClickBehaviour>> entry : behaviours.entrySet()) {
            for (ClickBehaviour behaviour : entry.getValue()) {
                addBehaviour(entry.getKey(), behaviour);
            }
        }
    }

	// Event handling
	public void onClick(InventoryClickEvent event) {

        final List<ClickBehaviour> behaviours = clickBehaviourMap.get(event.getClick());

        if (behaviours != null) {

            final ListIterator<ClickBehaviour> iterator = behaviours.listIterator(behaviours.size());

            final ClickContext context = new ClickContext(holder, event, this);

            boolean executed = false;

            while (iterator.hasPrevious() && !executed) {
                executed = iterator.previous().onClick(context, null);
            }

            if (!executed) {
                event.setCancelled(true);
            }

        } else {
            event.setCancelled(true);
        }

	}

	public void onDrag(InventoryDragEvent event) {

        event.setCancelled(true);
	}

	public void update() {

	}

}
