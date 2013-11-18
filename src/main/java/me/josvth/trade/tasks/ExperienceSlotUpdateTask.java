package me.josvth.trade.tasks;

import me.josvth.trade.transaction.inventory.TransactionHolder;
import me.josvth.trade.transaction.inventory.slot.ExperienceSlot;
import me.josvth.trade.transaction.inventory.slot.Slot;

import java.util.Set;

public class ExperienceSlotUpdateTask extends SlotUpdateTask{

    private final int levels;

    public ExperienceSlotUpdateTask(TransactionHolder holder, Set<? extends Slot> slots, int levels) {
        super(holder, slots);
        this.levels = levels;
    }

    @Override
    public void run() {
        for (Slot s : slot) {
            ((ExperienceSlot)s).update(holder, levels);
        }
    }

}
