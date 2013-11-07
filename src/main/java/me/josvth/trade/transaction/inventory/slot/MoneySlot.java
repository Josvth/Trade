package me.josvth.trade.transaction.inventory.slot;

import me.josvth.trade.goods.ExperienceTradeable;
import me.josvth.trade.goods.MoneyTradeable;
import me.josvth.trade.goods.Tradeable;
import me.josvth.trade.transaction.OfferList;
import me.josvth.trade.transaction.inventory.TransactionHolder;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class MoneySlot extends Slot {

	private final ItemStack moneyItem;
	private final boolean keepMeta;

	private final double smallModifier;
	private final double largeModifier;

	public MoneySlot(int slot, ItemStack moneyItem, boolean keepMeta, double smallModifier, double largeModifier) {
		super(slot);
		this.moneyItem = moneyItem;
		this.keepMeta = keepMeta;
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

			final double moneyToAdd = event.isShiftClick()? largeModifier : smallModifier;

			// TODO add balance check

			// TODO remove balance

			final Map<Integer, Tradeable> remainders = offers.add(new MoneyTradeable(moneyToAdd));

			// TODO handle remainders

			// TODO update specific slots
			TradeSlot.updateTradeSlots(holder, true);
			MirrorSlot.updateMirrors(holder.getTrader().getOther().getHolder(), true);

		} else if (event.isRightClick()) {

			final double moneyToRemove = event.isShiftClick()? largeModifier : smallModifier;

			final Map<Integer, Tradeable> remainders = offers.remove(new MoneyTradeable(moneyToRemove));

			final ExperienceTradeable remaining = (ExperienceTradeable) remainders.get(0);

			// TODO Regrant money
			if (remaining != null) {
				player.sendMessage("TEST MESSAGE: Removed " + (moneyToRemove - remaining.getLevels()) + " levels.");
				//player.setLevel(player.getLevel() + moneyToRemove - remaining.getLevels());
			} else {
				//player.setLevel(player.getLevel() + moneyToRemove);
			}

			// TODO update specific slots
			TradeSlot.updateTradeSlots(holder, true);
			MirrorSlot.updateMirrors(holder.getTrader().getOther().getHolder(), true);

		}

		player.sendMessage(offers.toString());

	}

	@Override
	public void update(TransactionHolder holder) {
		setSlot(holder, moneyItem);
	}

}
