package me.josvth.trade.transaction.inventory.slot;

import me.josvth.trade.Trade;
import me.josvth.trade.tasks.MoneySlotUpdateTask;
import me.josvth.trade.transaction.action.trader.offer.ChangeMoneyAction;
import me.josvth.trade.transaction.inventory.TransactionHolder;
import me.josvth.trade.transaction.inventory.interact.ClickContext;
import me.josvth.trade.transaction.inventory.offer.MoneyOffer;
import me.josvth.trade.transaction.inventory.offer.OfferList;
import me.josvth.trade.util.ItemStackUtils;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public class MoneySlot extends Slot {

    public static final String TYPE_NAME = "money";

    private ItemStack moneyItem;

    private double smallModifier;
    private double largeModifier;

    public MoneySlot(int slot, TransactionHolder holder) {
        super(slot, holder);
    }

    public static double getMoney(OfferList offers) {
        double money = 0;
        for (MoneyOffer tradeable : offers.getOfClass(MoneyOffer.class).values()) {
            money += tradeable.getAmount();
        }
        return money;
    }

    public static void updateMoneySlots(TransactionHolder holder, boolean nextTick, double money) {
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
        slot.setSmallModifier(description.getConfiguration().getDouble("small-modifier", 1.0));
        slot.setLargeModifier(description.getConfiguration().getDouble("large-modifier", 5.0));
        return slot;
    }

    public ItemStack getMoneyItem() {
        return moneyItem;
    }

    public void setMoneyItem(ItemStack moneyItem) {
        this.moneyItem = moneyItem;
    }

    public double getSmallModifier() {
        return smallModifier;
    }

    public void setSmallModifier(double smallModifier) {
        this.smallModifier = smallModifier;
    }

    public double getLargeModifier() {
        return largeModifier;
    }

    public void setLargeModifier(double largeModifier) {
        this.largeModifier = largeModifier;
    }

    @Override
    public boolean onClick(ClickContext context) {

        // We always cancel the event.
        context.getEvent().setCancelled(true);

        final double amount = context.getEvent().isShiftClick() ? largeModifier : smallModifier;

        if (amount <= 0) { // If amount is smaller or equal to 0 we do nothing to allow disabling of shift clicking
            return true;
        }

        if (context.getEvent().isLeftClick()) {
            new ChangeMoneyAction(holder.getTrader(), holder.getOfferList(), amount).execute();
        } else if (context.getEvent().isRightClick()) {
            new ChangeMoneyAction(holder.getTrader(), holder.getOfferList(), -1 * amount).execute();
        }

        return true;

    }

    @Override
    public void update() {
        update(getMoney(holder.getOfferList()));
    }

    public void update(double money) {
        setGUIItem(
                ItemStackUtils.argument(
                        moneyItem.clone(),
                        "%money%", holder.getEconomy().format(money),
                        "%small%", holder.getEconomy().format(smallModifier),
                        "%large%", holder.getEconomy().format(largeModifier)
                )
        );
    }

}
