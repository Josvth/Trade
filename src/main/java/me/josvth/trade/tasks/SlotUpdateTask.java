package me.josvth.trade.tasks;

import me.josvth.trade.transaction.inventory.slot.Slot;

import java.util.Set;

public class SlotUpdateTask implements Runnable {

    protected final Slot[] slot;

    public SlotUpdateTask(Set<? extends Slot> slots) {
        this(slots.toArray(new Slot[slots.size()]));
    }

    public SlotUpdateTask(Slot... slot) {
        this.slot = slot;
    }

    @Override
    public void run() {
        for (Slot s : slot) {
            s.update();
        }
    }

}
