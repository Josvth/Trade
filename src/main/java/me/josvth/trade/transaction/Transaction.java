package me.josvth.trade.transaction;

import me.josvth.trade.Trade;
import me.josvth.trade.transaction.inventory.TransactionLayout;
import me.josvth.trade.transaction.inventory.slot.*;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Transaction {

	private final TransactionManager manager;

	private TransactionLayout layout = null;

	private final Trader traderA;
	private final Trader traderB;

	private TransactionStage stage = TransactionStage.PRE;

	public Transaction(TransactionManager manager, String playerA, String playerB) {
		this.manager = manager;

		// TEMPORARY UNTIL LAYOUT MANAGER IS READY
		Slot[] slots = new Slot[18];

		slots[0] = new TradeSlot(0,0);
		slots[1] = new TradeSlot(1,1);
		slots[2] = new TradeSlot(2,2);

		slots[3] = new AcceptSlot(3, new ItemStack(Material.STAINED_CLAY, 0, (short) 5), new ItemStack(Material.STAINED_CLAY, 0, (short) 4));
		slots[4] = new RefuseSlot(4, new ItemStack(Material.STAINED_CLAY, 0, (short) 6));
		slots[5] = new StatusSlot(5, new ItemStack(Material.STAINED_CLAY, 0, (short) 4), new ItemStack(Material.STAINED_CLAY, 0, (short) 5));

		slots[6] = new MirrorSlot(6,0);
		slots[7] = new MirrorSlot(7,1);
		slots[8] = new MirrorSlot(8,2);

		slots[11] = new MoneySlot(11, new ItemStack(Material.GOLD_INGOT, 0), 1, 5);
		slots[12] = new ExperienceSlot(12, new ItemStack(Material.EXP_BOTTLE, 0), 1, 5);
		slots[13] = new CloseSlot(13, new ItemStack(Material.BONE, 0));

		layout = new TransactionLayout(2, slots);
		// END TEMP

		traderA = new Trader(this, playerA, layout.getOfferSize());
		// FOR DEBUGGING PURPOSES
		//traderB = new Trader(this, playerB, layout.getOfferSize());
		traderB = traderA;

		traderA.setOther(traderB);
		traderB.setOther(traderA);

	}

	public Trade getPlugin() {
		return manager.getPlugin();
	}

	public Trader getTraderA() {
		return traderA;
	}

	public Trader getTraderB() {
		return traderB;
	}

	public Trader getTrader(String playerName) {
		if (traderA.getName().equals(playerName))
			return traderA;
		if (traderB.getName().equals(playerName))
			return traderB;
		throw new IllegalArgumentException("Player " + playerName + " is not participating in this trade or went offline.");
	}

	public TransactionLayout getLayout() {
		return layout;
	}

	public void setLayout(TransactionLayout layout) {
		this.layout = layout;
	}

	public TransactionStage getStage() {
		return stage;
	}

	public boolean isStarted() {
		return stage == TransactionStage.IN_PROGRESS;
	}

	public boolean hasEnded() {
	 	return stage == TransactionStage.POST;
	}

	public void start() {

		if (isStarted()) {
			throw new IllegalArgumentException("Cannot start an already started transaction");
		}

		if (hasEnded()) {
			throw new IllegalArgumentException("Cannot start an ended transaction");
		}

		if (layout == null) {
			throw new IllegalStateException("Cannot start transaction without an layout.");
		}

		if (manager.isInTransaction(traderA.getName()) || manager.isInTransaction(traderB.getName())) {
			throw new IllegalArgumentException("One of the traders is already trading!");
		}

		manager.addTransaction(this);

		stage = TransactionStage.IN_PROGRESS;

		traderA.openInventory();
		traderB.openInventory();

	}


	public void stop() {

		if (!isStarted()) {
			throw new IllegalArgumentException("Cannot stop a non started transaction");
		}

		if (hasEnded()) {
			throw new IllegalArgumentException("Cannot stop an ended transaction");
		}

		manager.removeTransaction(this);

		traderA.closeInventory();
		traderB.closeInventory();

		traderA.getOffers().revert();
		traderB.getOffers().revert();

	}

	public void cancel() {
		stop();
	}

}
