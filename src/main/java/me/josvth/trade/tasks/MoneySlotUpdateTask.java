package me.josvth.trade.tasks;

import me.josvth.trade.transaction.inventory.slot.MoneySlot;
import me.josvth.trade.transaction.inventory.slot.Slot;

import java.util.Set;

public class MoneySlotUpdateTask extends SlotUpdateTask {

    private final double money;

    public MoneySlotUpdateTask(Set<MoneySlot> slots, double money) {
        super(slots);
        this.money = money;
    }

    @Override
    public void run() {
        for (Slot s : slot) {
            ((MoneySlot) s).update(money);
        }
    }

}
