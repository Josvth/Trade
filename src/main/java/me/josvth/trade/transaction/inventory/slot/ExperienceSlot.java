package me.josvth.trade.transaction.inventory.slot;

import me.josvth.bukkitformatlibrary.FormattedMessage;
import me.josvth.trade.Trade;
import me.josvth.trade.offer.ExperienceOffer;
import me.josvth.trade.tasks.ExperienceSlotUpdateTask;
import me.josvth.trade.offer.OfferList;
import me.josvth.trade.transaction.inventory.TransactionHolder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.*;

public class ExperienceSlot extends Slot {

	private final ItemDescription experienceDescription;

	private final int smallModifier;
	private final int largeModifier;

	private final FormattedMessage addMessage;
	private final FormattedMessage removeMessage;
	private final FormattedMessage insufficientMessage;

	public ExperienceSlot(int slot, ItemDescription experienceDescription, int smallModifier, int largeModifier, FormattedMessage addMessage, FormattedMessage removeMessage, FormattedMessage insufficientMessage) {
		super(slot);
		this.experienceDescription = experienceDescription;
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

            final int remainder = addExperience(holder, levelsToAdd);

			player.setLevel(player.getLevel() - levelsToAdd);

			addMessage.send(player, "%levels%", String.valueOf(levelsToAdd - remainder));

		} else if (event.isRightClick()) {

			final int levelsToRemove = event.isShiftClick()? largeModifier : smallModifier;

            final int remainder = removeExperience(holder, levelsToRemove);

            player.setLevel(player.getLevel() + levelsToRemove - remainder);

			removeMessage.send(player, "%levels%", String.valueOf(levelsToRemove - remainder));

		}

	}

	@Override
	public void update(TransactionHolder holder) {
        update(holder, getLevels(holder.getOffers()));
	}

    public void update(TransactionHolder holder, int levels) {
        setSlot(holder, experienceDescription.create("%levels%", String.valueOf(levels), "%small%", String.valueOf(smallModifier), "%large%", String.valueOf(largeModifier)));
    }

    private static int addExperience(TransactionHolder holder, int levels) {

        final LinkedList<Integer> changedIndexes = new LinkedList<Integer>();

        // First we try and fill up existing experience offers
        final Iterator<Map.Entry<Integer, ExperienceOffer>> iterator = holder.getOffers().getOfClass(ExperienceOffer.class).entrySet().iterator();

        while (iterator.hasNext() && levels > 0) {

            final Map.Entry<Integer, ExperienceOffer> entry = iterator.next();

            final int remaining = entry.getValue().add(levels);

            if (remaining < levels) {
                changedIndexes.add(entry.getKey());
            }

            levels = remaining;

        }


        // Next put the remaining levels in empty offer slots
        if (levels > 0) {

            int firstEmpty = holder.getOffers().getFirstEmpty();

            while (levels > 0 && firstEmpty != -1) {

                final int remainder = levels - 64;

                if (remainder > 0) {
                    holder.getOffers().set(firstEmpty, createOffer(holder, firstEmpty, levels));
                    levels = 0;
                } else {
                    holder.getOffers().set(firstEmpty, createOffer(holder, firstEmpty, 64));
                    levels = -1 * remainder;
                    firstEmpty = holder.getOffers().getFirstEmpty();
                }

                changedIndexes.add(firstEmpty);

            }

        }

        // If we changed anything we update the holder and mirror
        if (!changedIndexes.isEmpty()) {

            // We place our changed indexes into an array
            final int[] indexesArray = new int[changedIndexes.size()];

            int i = 0;
            for (int index : changedIndexes) {
                indexesArray[i] = index;
            }

            TradeSlot.updateTradeSlots(holder, true, indexesArray);
            MirrorSlot.updateMirrors(holder, true, indexesArray);
            ExperienceSlot.updateExperienceSlots(holder, true, getLevels(holder.getOffers()));

        }

        return levels;

    }

    private static int removeExperience(TransactionHolder holder, int levels) {

        final LinkedList<Integer> changedIndexes = new LinkedList<Integer>();

        // TODO TAKE ACCOUNT OF ORDER
        // First we try and remove from existing experience offers
        final Iterator<Map.Entry<Integer, ExperienceOffer>> iterator = holder.getOffers().getOfClass(ExperienceOffer.class).entrySet().iterator();

        while (iterator.hasNext() && levels > 0) {

            final Map.Entry<Integer, ExperienceOffer> entry = iterator.next();

            final int remaining = entry.getValue().remove(levels);

            if (remaining < levels) {
                changedIndexes.add(entry.getKey());
            }

            levels = remaining;

        }

        // If we changed anything we update the holder and mirror
        if (!changedIndexes.isEmpty()) {

            // We place our changed indexes into an array
            final int[] indexesArray = new int[changedIndexes.size()];

            int i = 0;
            for (int index : changedIndexes) {
                indexesArray[i] = index;
            }

            TradeSlot.updateTradeSlots(holder, true, indexesArray);
            MirrorSlot.updateMirrors(holder, true, indexesArray);
            ExperienceSlot.updateExperienceSlots(holder, true, getLevels(holder.getOffers()));

        }

        return levels;

    }

    private static ExperienceOffer createOffer(TransactionHolder holder, int index, int levels) {
        final ExperienceOffer offer = holder.getLayout().createOffer(ExperienceOffer.class, holder.getOffers(), index);
        offer.setLevels(levels);
        return offer;
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
