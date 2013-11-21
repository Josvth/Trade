package me.josvth.trade.transaction.inventory.slot;

import me.josvth.trade.transaction.inventory.TransactionHolder;
import org.bukkit.event.inventory.InventoryClickEvent;

public class MoneySlot extends Slot {

	private final ItemDescription moneyDescription;

	private final double smallModifier;
	private final double largeModifier;

	public MoneySlot(int slot, ItemDescription moneyDescription, double smallModifier, double largeModifier) {
		super(slot);
		this.moneyDescription = moneyDescription;
		this.smallModifier = smallModifier;
		this.largeModifier = largeModifier;
	}

	@Override
	public void onClick(InventoryClickEvent event) {

		// We always cancel the event.
		event.setCancelled(true);

//		final TransactionHolder holder = (TransactionHolder) event.getInventory().getHolder();
//
//		final Player player = (Player) event.getWhoClicked();
//
//		final OfferList offers = holder.getOffers();
//
//		if (event.isLeftClick()) {
//
//			final double moneyToAdd = event.isShiftClick()? largeModifier : smallModifier;
//
//			// TODO add balance check
//
//			// TODO remove balance
//
//			final Map<Integer, Offer> remainders = offers.add(new MoneyOffer(moneyToAdd));
//
//			// TODO handle remainders
//
//			// TODO update specific slots
//			TradeSlot.updateTradeSlots(holder, true);
//			MirrorSlot.updateMirrors(holder.getOtherHolder(), true);
//
//		} else if (event.isRightClick()) {
//
//			final double moneyToRemove = event.isShiftClick()? largeModifier : smallModifier;
//
//			final Map<Integer, Offer> remainders = offers.remove(new MoneyOffer(moneyToRemove));
//
//			final ExperienceOffer remaining = (ExperienceOffer) remainders.get(0);
//
//			// TODO Regrant money
//			if (remaining != null) {
//				player.sendMessage("TEST MESSAGE: Removed " + (moneyToRemove - remaining.getLevels()) + " levels.");
//				//player.setLevel(player.getLevel() + moneyToRemove - remaining.getLevels());
//			} else {
//				//player.setLevel(player.getLevel() + moneyToRemove);
//			}
//
//			// TODO update specific slots
//			TradeSlot.updateTradeSlots(holder, true);
//			MirrorSlot.updateMirrors(holder.getOtherHolder(), true);
//
//		}

	}

	@Override
	public void update(TransactionHolder holder) {
		setSlot(holder, moneyDescription.create());
	}

}
