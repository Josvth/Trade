package me.josvth.trade.transaction.inventory.slot;

import me.josvth.trade.goods.ExperienceTradeable;
import me.josvth.trade.goods.Tradeable;
import me.josvth.trade.transaction.OfferList;
import me.josvth.trade.transaction.Trader;
import me.josvth.trade.transaction.inventory.ItemDescription;
import me.josvth.trade.transaction.inventory.TransactionHolder;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Iterator;
import java.util.Map;

public class ExperienceSlot extends Slot {

	private final ItemDescription experienceDescription;

	private final int smallModifier;
	private final int largeModifier;

	public ExperienceSlot(int slot, ItemDescription experienceDescription, int smallModifier, int largeModifier) {
		super(slot);
		this.experienceDescription = experienceDescription;
		this.smallModifier = smallModifier;
		this.largeModifier = largeModifier;
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
				player.sendMessage("TEST MESSAGE: You don't have enough levels.");
				return;
			}

			player.setLevel(player.getLevel() - levelsToAdd);

			final Map<Integer, Tradeable> remainders = offers.add(new ExperienceTradeable(levelsToAdd));

			// TODO handle remainders

			// TODO update specific slots
			TradeSlot.updateTradeSlots(holder, true);
			MirrorSlot.updateMirrors(holder.getOtherHolder(), true);

		} else if (event.isRightClick()) {

			final int levelsToRemove = event.isShiftClick()? largeModifier : smallModifier;

			final Map<Integer, Tradeable> remainders = offers.remove(new ExperienceTradeable(levelsToRemove));

			final ExperienceTradeable remaining = (ExperienceTradeable) remainders.get(0);

			if (remaining != null) {
				player.sendMessage("TEST MESSAGE: Removed " + (levelsToRemove - remaining.getLevels()) + " levels.");
				player.setLevel(player.getLevel() + levelsToRemove - remaining.getLevels());
			} else {
				player.setLevel(player.getLevel() + levelsToRemove);
			}

			// TODO update specific slots
			TradeSlot.updateTradeSlots(holder, true);
			MirrorSlot.updateMirrors(holder.getOtherHolder(), true);

		}

		player.sendMessage(offers.toString());

	}

	@Override
	public void update(TransactionHolder holder) {
		setSlot(holder, experienceDescription.create("%levels%", "unknown", "%small%", String.valueOf(smallModifier), "%large%", String.valueOf(largeModifier)));
	}
}
