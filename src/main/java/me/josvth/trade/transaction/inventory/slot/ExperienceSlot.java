package me.josvth.trade.transaction.inventory.slot;

import me.josvth.bukkitformatlibrary.FormattedMessage;
import me.josvth.trade.Trade;
import me.josvth.trade.offer.ExperienceOffer;
import me.josvth.trade.tasks.ExperienceSlotUpdateTask;
import me.josvth.trade.offer.OfferList;
import me.josvth.trade.transaction.inventory.TransactionHolder;
import me.josvth.trade.util.ItemStackUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ExperienceSlot extends Slot {

	private final ItemStack experienceItem;

	private final int smallModifier;
	private final int largeModifier;

	private final FormattedMessage addMessage;
	private final FormattedMessage removeMessage;
	private final FormattedMessage insufficientMessage;

	public ExperienceSlot(int slot, ItemStack experienceItem, int smallModifier, int largeModifier, FormattedMessage addMessage, FormattedMessage removeMessage, FormattedMessage insufficientMessage) {
		super(slot);
		this.experienceItem = experienceItem;
		this.smallModifier = smallModifier;
		this.largeModifier = largeModifier;
		this.addMessage = addMessage;
		this.removeMessage = removeMessage;
		this.insufficientMessage = insufficientMessage;
	}

	@Override
	public void onClick(InventoryClickEvent event) {

		// We always cancel the event.
		event.setCancelled(true);

		final TransactionHolder holder = (TransactionHolder) event.getInventory().getHolder();

		final Player player = (Player) event.getWhoClicked();

		if (event.isLeftClick()) {

			final int levelsToAdd = event.isShiftClick()? largeModifier : smallModifier;

			if (player.getLevel() < levelsToAdd) {
				insufficientMessage.send(player, "%levels%", String.valueOf(levelsToAdd));
				return;
			}

            final int remainder = holder.getOffers().removeExperience(levelsToAdd);

			player.setLevel(player.getLevel() - levelsToAdd);

			addMessage.send(player, "%levels%", String.valueOf(levelsToAdd - remainder));

		} else if (event.isRightClick()) {

			final int levelsToRemove = event.isShiftClick()? largeModifier : smallModifier;

            final int remainder = holder.getOffers().removeExperience(levelsToRemove);

            player.setLevel(player.getLevel() + levelsToRemove - remainder);

			removeMessage.send(player, "%levels%", String.valueOf(levelsToRemove - remainder));

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
