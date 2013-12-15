package me.josvth.trade.transaction.inventory.slot;

import me.josvth.trade.Trade;
import me.josvth.trade.offer.ExperienceOffer;
import me.josvth.trade.offer.OfferList;
import me.josvth.trade.tasks.ExperienceSlotUpdateTask;
import me.josvth.trade.transaction.Trader;
import me.josvth.trade.transaction.inventory.TransactionHolder;
import me.josvth.trade.util.ItemStackUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
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

        final Trader trader = holder.getTrader();

        final Player player = (Player) event.getWhoClicked();

        if (event.isLeftClick()) {

            final int levelsToAdd = event.isShiftClick() ? largeModifier : smallModifier;

            if (player.getLevel() < levelsToAdd) {
                trader.getFormattedMessage("experience.insufficient").send(player, "%levels%", String.valueOf(levelsToAdd));
                return;
            }

            final int remainder = holder.getOffers().removeExperience(levelsToAdd);

            player.setLevel(player.getLevel() - levelsToAdd);

            trader.getFormattedMessage("experience.add.self").send(player, "%levels%", String.valueOf(levelsToAdd - remainder));
            if (trader.getOtherTrader().hasFormattedMessage("experience.add.other")) {
                trader.getOtherTrader().getFormattedMessage("experience.add.other").send(trader.getPlayer(), "%player%", player.getName(), "%levels%", String.valueOf(levelsToAdd - remainder));
            }

        } else if (event.isRightClick()) {

            final int levelsToRemove = event.isShiftClick() ? largeModifier : smallModifier;

            final int remainder = holder.getOffers().removeExperience(levelsToRemove);

            player.setLevel(player.getLevel() + levelsToRemove - remainder);

            trader.getFormattedMessage("experience.remove.self").send(player, "%levels%", String.valueOf(levelsToRemove - remainder));
            if (trader.getOtherTrader().hasFormattedMessage("experience.remove.other")) {
                trader.getOtherTrader().getFormattedMessage("experience.remove.other").send(trader.getPlayer(), "%player%", player.getName(), "%levels%", String.valueOf(levelsToRemove - remainder));
            }
        }

    }

    @Override
    public void update(TransactionHolder holder) {
        update(holder, getLevels(holder.getOffers()));
    }

    public void update(TransactionHolder holder, int levels) {
        setSlot(holder, ItemStackUtils.argument(experienceItem.clone(), "%levels%", String.valueOf(levels), "%small%", String.valueOf(smallModifier), "%large%", String.valueOf(largeModifier)));
    }

    private static int getLevels(OfferList list) {
        int levels = 0;
        for (ExperienceOffer tradeable : list.getOfClass(ExperienceOffer.class).values()) {
            levels += tradeable.getLevels();
        }
        return levels;
    }

    public static void updateExperienceSlots(TransactionHolder holder, boolean nextTick, int levels) {

        final Set<ExperienceSlot> slots = holder.getLayout().getSlotsOfType(ExperienceSlot.class);

        if (!nextTick) {
            for (ExperienceSlot slot : slots) {
                slot.update(holder, levels);
            }
        } else if (!slots.isEmpty()) {
            Bukkit.getScheduler().runTask(Trade.getInstance(), new ExperienceSlotUpdateTask(holder, slots, levels));
        }

    }

}
