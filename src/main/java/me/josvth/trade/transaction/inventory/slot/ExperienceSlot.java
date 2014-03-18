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

    private ItemStack experienceItem;

    private int smallModifier = 1;
    private int largeModifier = 5;

    public ExperienceSlot(int slot, TransactionHolder holder) {
        super(slot, holder);
    }

    public ItemStack getExperienceItem() {
        return experienceItem;
    }

    public void setExperienceItem(ItemStack experienceItem) {
        this.experienceItem = experienceItem;
    }

    public int getSmallModifier() {
        return smallModifier;
    }

    public void setSmallModifier(int smallModifier) {
        this.smallModifier = smallModifier;
    }

    public int getLargeModifier() {
        return largeModifier;
    }

    public void setLargeModifier(int largeModifier) {
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
    public void update() {
        update(getExperience(holder.getOfferList()));
    }

    public void update(int experience) {
        setGUIItem(ItemStackUtils.argument(experienceItem.clone(), "%experience%", String.valueOf(experience), "%small%", String.valueOf(smallModifier), "%large%", String.valueOf(largeModifier)));
    }

    private static int getExperience(OfferList list) {
        int experience = 0;
        for (ExperienceOffer tradeable : list.getOfClass(ExperienceOffer.class).values()) {
            experience += tradeable.getAmount();
        }
        return experience;
    }

    public static void updateExperienceSlots(TransactionHolder holder, boolean nextTick, int experience) {

        final Set<ExperienceSlot> slots = holder.getSlotsOfType(ExperienceSlot.class);

        if (!nextTick) {
            for (ExperienceSlot slot : slots) {
                slot.update(experience);
            }
        } else if (!slots.isEmpty()) {
            Bukkit.getScheduler().runTask(Trade.getInstance(), new ExperienceSlotUpdateTask(slots, experience));
        }

    }

    public static ExperienceSlot deserialize(int slotID, TransactionHolder holder, SlotDescription description) {
        final ExperienceSlot slot = new ExperienceSlot(slotID, holder);
        slot.setExperienceItem(ItemStackUtils.fromSection(description.getConfiguration().getConfigurationSection("experience-item"), Trade.getInstance().getMessageManager()));
        slot.setSmallModifier(description.getConfiguration().getInt("small-modifier", 1));
        slot.setLargeModifier(description.getConfiguration().getInt("large-modifier", 5));
        return slot;
    }

}
