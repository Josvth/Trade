package me.josvth.trade.transaction.inventory.slot;

import me.josvth.trade.Trade;
import me.josvth.trade.tasks.MoneySlotUpdateTask;
import me.josvth.trade.transaction.action.trader.offer.ChangeMoneyAction;
import me.josvth.trade.transaction.inventory.TransactionHolder;
import me.josvth.trade.transaction.inventory.offer.MoneyOffer;
import me.josvth.trade.transaction.inventory.offer.OfferList;
import me.josvth.trade.util.ItemStackUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public class MoneySlot extends Slot {

    private ItemStack moneyItem;

	private int smallModifier;
	private int largeModifier;

	public MoneySlot(int slot, TransactionHolder holder) {
        super(slot, holder);
    }

    public ItemStack getMoneyItem() {
        return moneyItem;
    }

    public void setMoneyItem(ItemStack moneyItem) {
        this.moneyItem = moneyItem;
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
            new ChangeMoneyAction(holder.getTrader(), holder.getOfferList(), amount).execute();
        } else if (event.isRightClick()) {
            new ChangeMoneyAction(holder.getTrader(), holder.getOfferList(), -1*amount).execute();
        }

    }


    @Override
    public void update() {
        update(getMoney(holder.getOfferList()));
    }

    public static int getMoney(OfferList offers) {
        int money = 0;
        for (MoneyOffer tradeable : offers.getOfClass(MoneyOffer.class).values()) {
            money += tradeable.getAmount();
        }
        return money;
    }

    public void update(int money) {
        final double divider = Math.pow(10, holder.getEconomy().fractionalDigits());
        setGUIItem(
                ItemStackUtils.argument(
                        moneyItem.clone(),
                        "%money%", holder.getEconomy().format(money / divider),
                        "%small%", holder.getEconomy().format(smallModifier / divider),
                        "%large%", holder.getEconomy().format(largeModifier / divider)
                )
        );
    }

    public static void updateMoneySlots(TransactionHolder holder, boolean nextTick, int money) {
        final Set<MoneySlot> slots = holder.getSlotsOfType(MoneySlot.class);

        if (!nextTick) {
            for (MoneySlot slot : slots) {
                slot.update(money);
            }
        } else if (!slots.isEmpty()) {
            Bukkit.getScheduler().runTask(Trade.getInstance(), new MoneySlotUpdateTask(slots, money));
        }
    }

    public static MoneySlot deserialize(int slotID, TransactionHolder holder, SlotDescription description) {
        final MoneySlot slot = new MoneySlot(slotID, holder);
        slot.setMoneyItem(ItemStackUtils.fromSection(description.getConfiguration().getConfigurationSection("money-item"), Trade.getInstance().getMessageManager()));
        slot.setSmallModifier(description.getConfiguration().getInt("small-modifier", 1));
        slot.setLargeModifier(description.getConfiguration().getInt("large-modifier", 5));
        return slot;
    }

}
