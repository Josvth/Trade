package me.josvth.trade.transaction.inventory;

import me.josvth.trade.transaction.Trader;
import me.josvth.trade.transaction.inventory.slot.MirrorSlot;
import me.josvth.trade.transaction.inventory.slot.Slot;
import me.josvth.trade.transaction.inventory.slot.SlotInfo;
import me.josvth.trade.transaction.inventory.slot.TradeSlot;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

public class TransactionLayout {

	private final int rows;

	private final SlotInfo[] slots;

	private int tradeSlotsAmount;

	public TransactionLayout(int rows) {
		this.rows = rows;
		this.slots = null;
	}

	public int getRows() {
		return rows;
	}

	public int getSlots() {
		return rows * 9;
	}

	public int getOfferSize() {
		return tradeSlotsAmount;
	}

	public void setHolders(Trader traderA, Trader traderB) {

		TransactionHolder holderA = new TransactionHolder(traderA, createInventory(traderA), new Slot[getSlots()], new TradeSlot[tradeSlotsAmount], new MirrorSlot[tradeSlotsAmount]);
		TransactionHolder holderB = new TransactionHolder(traderA, createInventory(traderB), new Slot[getSlots()], new TradeSlot[tradeSlotsAmount], new MirrorSlot[tradeSlotsAmount]);

		int tradeSlotIndex = 0;
		int mirrorSlotIndex = 0;

		for (int slot = 0; slot < getSlots(); slot++) {
			SlotInfo info = slots[slot];
			if (info != null) {

				Slot slotA = info.createSlot(holderA);
				Slot slotB = info.createSlot(holderB);

				holderA.getSlots()[slot] = slotA;
				holderB.getSlots()[slot] = slotB;

				if (info.getType() == SlotInfo.SlotType.TRADE) {
					holderA.getTradeSlots()[tradeSlotIndex] = (TradeSlot) slotA;
					holderB.getTradeSlots()[tradeSlotIndex] = (TradeSlot) slotB;
					tradeSlotIndex++;
				}

				if (info.getType() == SlotInfo.SlotType.MIRROR) {
					holderA.getMirrorSlots()[mirrorSlotIndex] = (MirrorSlot) slotA;
					holderB.getMirrorSlots()[mirrorSlotIndex] = (MirrorSlot) slotB;
					mirrorSlotIndex++;
				}

				// Link the mirror slots
				for (int tradeSlot = 0; tradeSlot < tradeSlotsAmount; tradeSlot++) {
					holderA.getTradeSlots()[tradeSlot].setMirror(holderB.getMirrorSlots()[tradeSlot]);
					holderB.getTradeSlots()[tradeSlot].setMirror(holderA.getMirrorSlots()[tradeSlot]);
				}
			}
		}

	}

	private Inventory createInventory(Trader trader) {
		return null;
	}

}
