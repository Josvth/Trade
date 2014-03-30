package me.josvth.trade.tasks;

import me.josvth.trade.transaction.inventory.slot.ExperienceSlot;
import me.josvth.trade.transaction.inventory.slot.Slot;

import java.util.Set;

public class ExperienceSlotUpdateTask extends SlotUpdateTask{

    private final int levels;

    public ExperienceSlotUpdateTask(Set<? extends Slot> slots, int levels) {
        super(slots);
        this.levels = levels;
    }

    @Override
    public void run() {
        for (Slot s : slot) {
            ((ExperienceSlot)s).update(levels);
        }
    }

}
