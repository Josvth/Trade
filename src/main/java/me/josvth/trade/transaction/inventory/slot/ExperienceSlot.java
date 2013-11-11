package me.josvth.trade.transaction.inventory.slot;

import me.josvth.bukkitformatlibrary.FormattedMessage;
import me.josvth.trade.Trade;
import me.josvth.trade.tradeable.ExperienceTradeable;
import me.josvth.trade.tasks.SlotUpdateTask;
import me.josvth.trade.transaction.OfferList;
import me.josvth.trade.util.ItemDescription;
import me.josvth.trade.transaction.inventory.TransactionHolder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Set;

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

		final OfferList offers = holder.getOffers();

		if (event.isLeftClick()) {

			final int levelsToAdd = event.isShiftClick()? largeModifier : smallModifier;

			if (player.getLevel() < levelsToAdd) {
				insufficientMessage.send(player, "%levels%", String.valueOf(levelsToAdd));
				return;
			}

			final ExperienceTradeable remainder = (ExperienceTradeable) offers.add(new ExperienceTradeable(levelsToAdd)).get(0);

			if (remainder != null) {
				player.setLevel(player.getLevel() - levelsToAdd + remainder.getLevels());
			} else {
				player.setLevel(player.getLevel() - levelsToAdd);
			}

			addMessage.send(player, "%levels%", String.valueOf(levelsToAdd - ((remainder == null)? 0 : remainder.getLevels())));

			ExperienceSlot.updateExperienceSlots(holder, true);

			// TODO update specific slots
			TradeSlot.updateTradeSlots(holder, true);
			MirrorSlot.updateMirrors(holder.getOtherHolder(), true);

		} else if (event.isRightClick()) {

			final int levelsToRemove = event.isShiftClick()? largeModifier : smallModifier;

			final ExperienceTradeable remaining = (ExperienceTradeable) offers.remove(new ExperienceTradeable(levelsToRemove)).get(0);

			if (remaining != null) {
				player.setLevel(player.getLevel() + levelsToRemove - remaining.getLevels());
			} else {
				player.setLevel(player.getLevel() + levelsToRemove);
			}

			removeMessage.send(player, "%levels%", String.valueOf(levelsToRemove - ((remaining == null)? 0 : remaining.getLevels())));

			ExperienceSlot.updateExperienceSlots(holder, true);

			// TODO update specific slots
			TradeSlot.updateTradeSlots(holder, true);
			MirrorSlot.updateMirrors(holder.getOtherHolder(), true);

		}

	}

	@Override
	public void update(TransactionHolder holder) {
		int levels = 0;
		for (ExperienceTradeable tradeable : holder.getOffers().getOfClass(ExperienceTradeable.class).values()) {
			levels += tradeable.getLevels();
		}
		setSlot(holder, experienceDescription.create("%levels%", String.valueOf(levels), "%small%", String.valueOf(smallModifier), "%large%", String.valueOf(largeModifier)));
	}

	public static void updateExperienceSlots(TransactionHolder holder, boolean nextTick) {

		final Set<ExperienceSlot> slots = holder.getLayout().getSlotsOfType(ExperienceSlot.class);

		if (!nextTick) {
			for (Slot slot : slots) {
				slot.update(holder);
			}
		} else if (!slots.isEmpty()) {
			Bukkit.getScheduler().runTask(Trade.getInstance(), new SlotUpdateTask(holder, slots));
		}

	}

}
