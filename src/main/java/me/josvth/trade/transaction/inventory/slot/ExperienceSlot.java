package me.josvth.trade.transaction.inventory.slot;

import me.josvth.trade.Trade;
import me.josvth.trade.offer.ExperienceOffer;
import me.josvth.trade.offer.OfferList;
import me.josvth.trade.tasks.ExperienceSlotUpdateTask;
import me.josvth.trade.transaction.inventory.TransactionHolder;
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

        if (event.isLeftClick()) {

            holder.getOffers().addExperience(event.isShiftClick()? largeModifier : smallModifier);

        } else if (event.isRightClick()) {

            holder.getOffers().removeExperience(event.isShiftClick()? largeModifier : smallModifier);

        }

    }

    @Override
    public void update(TransactionHolder holder) {
        update(holder, getExperience(holder.getOffers()));
    }

    public void update(TransactionHolder holder, int experience) {
        setSlot(holder, ItemStackUtils.argument(experienceItem.clone(), "%experience%", String.valueOf(experience), "%small%", String.valueOf(smallModifier), "%large%", String.valueOf(largeModifier)));
    }

    private static int getExperience(OfferList list) {
        int experience = 0;
        for (ExperienceOffer tradeable : list.getOfClass(ExperienceOffer.class).values()) {
            experience += tradeable.getExperience();
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
