package me.josvth.trade.transaction.inventory.slot;

import me.josvth.trade.Trade;
import me.josvth.trade.tasks.ExperienceSlotUpdateTask;
import me.josvth.trade.transaction.action.trader.offer.ChangeExperienceAction;
import me.josvth.trade.transaction.inventory.TransactionHolder;
import me.josvth.trade.transaction.offer.ExperienceOffer;
import me.josvth.trade.transaction.offer.OfferList;
import me.josvth.trade.util.ItemStackUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public class ExperienceSlot extends Slot {

    private final ItemStack experienceItem;

    private final int smallModifier;
    private final int largeModifier;

    public ExperienceSlot(int slot, ItemStack experienceItem, int smallModifier, int largeModifier) {
        super(slot);
        this.experienceItem = experienceItem;
        this.smallModifier = smallModifier;
        this.largeModifier = largeModifier;
    }

    @Override
    public void onClick(InventoryClickEvent event) {

        // We always cancel the event.
        event.setCancelled(true);

        final TransactionHolder holder = (TransactionHolder) event.getInventory().getHolder();

        final int amount = event.isShiftClick() ? largeModifier : smallModifier;

        if (amount <= 0) { // If amount is smaller or equal to 0 we do nothing to allow disabling of shift clicking
            return;
        }

        if (event.isLeftClick()) {
            new ChangeExperienceAction(holder.getTrader(), amount).execute();
        } else if (event.isRightClick()) {
            new ChangeExperienceAction(holder.getTrader(), -1*amount).execute();
        }

    }

    @Override
    public void update(TransactionHolder holder) {
        update(holder, getExperience(holder.getOffers()));
    }

    public void update(TransactionHolder holder, int experience) {
        setItem(holder, ItemStackUtils.argument(experienceItem.clone(), "%experience%", String.valueOf(experience), "%small%", String.valueOf(smallModifier), "%large%", String.valueOf(largeModifier)));
    }

    private static int getExperience(OfferList list) {
        int experience = 0;
        for (ExperienceOffer tradeable : list.getOfClass(ExperienceOffer.class).values()) {
            experience += tradeable.getAmount();
        }
        return experience;
    }

    public static void updateExperienceSlots(TransactionHolder holder, boolean nextTick, int experience) {

        final Set<ExperienceSlot> slots = holder.getLayout().getSlotsOfType(ExperienceSlot.class);

        if (!nextTick) {
            for (ExperienceSlot slot : slots) {
                slot.update(holder, experience);
            }
        } else if (!slots.isEmpty()) {
            Bukkit.getScheduler().runTask(Trade.getInstance(), new ExperienceSlotUpdateTask(holder, slots, experience));
        }

    }

}
