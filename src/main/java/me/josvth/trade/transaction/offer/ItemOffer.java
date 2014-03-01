package me.josvth.trade.transaction.offer;

import me.josvth.trade.Trade;
import me.josvth.trade.transaction.Trader;
import me.josvth.trade.transaction.inventory.TransactionHolder;
import me.josvth.trade.transaction.inventory.slot.Slot;
import me.josvth.trade.transaction.offer.behaviour.ClickBehaviour;
import me.josvth.trade.transaction.offer.behaviour.ClickCategory;
import me.josvth.trade.transaction.offer.behaviour.ClickTrigger;
import me.josvth.trade.transaction.offer.description.ItemOfferDescription;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class ItemOffer extends StackableOffer {

    private static final Map<ClickTrigger, LinkedList<ClickBehaviour>> DEFAULT_BEHAVIOURS = new HashMap<ClickTrigger, LinkedList<ClickBehaviour>>();

    static {

        final LinkedList<ClickBehaviour> cursorLeftBehaviours = new LinkedList<ClickBehaviour>();

        // PLACE_ALL
        cursorLeftBehaviours.add(new ClickBehaviour() {
            @Override
            public boolean onClick(InventoryClickEvent event, Slot slot, Offer offer) {
                if (slot == null) {
                    final TransactionHolder holder = (TransactionHolder) event.getInventory().getHolder();
                    if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) {
                        event.setCurrentItem(((ItemOffer) offer).getItem().clone());

                        holder.setCursorOffer(null, true);

                        event.setCancelled(true);
                        return true;
                    }
                }
                return false;
            }
        });

        // SWAP_WITH_CURSOR
        final ClickBehaviour swapWithCursor = new ClickBehaviour() {
            @Override
            public boolean onClick(InventoryClickEvent event, Slot slot, Offer offer) {
                if (slot == null) {
                    final TransactionHolder holder = (TransactionHolder) event.getInventory().getHolder();
                    if (event.getCurrentItem() != null) {
                        holder.setCursorOffer(new ItemOffer(event.getCurrentItem().clone()), true);

                        event.setCurrentItem(((ItemOffer) offer).getItem().clone());

                        event.setCancelled(true);
                        return true;
                    }
                }
                return false;
            }
        };

        cursorLeftBehaviours.add(swapWithCursor);

        DEFAULT_BEHAVIOURS.put(new ClickTrigger(ClickCategory.CURSOR, ClickType.LEFT), cursorLeftBehaviours);

        final LinkedList<ClickBehaviour> cursorRightBehaviours = new LinkedList<ClickBehaviour>();

        // GRANT_ONE
        cursorRightBehaviours.add(new ClickBehaviour() {
            @Override
            public boolean onClick(InventoryClickEvent event, Slot slot, Offer offer) {
                if (slot == null) {
                    final ItemStack currentItem = event.getCurrentItem();
                    if (currentItem == null) {
                        final TransactionHolder holder = (TransactionHolder) event.getInventory().getHolder();

                        final ItemOffer itemOffer = (ItemOffer) offer;
                        itemOffer.remove(1);
                        holder.updateCursorOffer();

                        final ItemStack item = itemOffer.getItem().clone();
                        item.setAmount(1);
                        event.setCurrentItem(item);

                        event.setCancelled(true);
                        return true;
                    }
                }
                return false;
            }
        });

        cursorRightBehaviours.add(swapWithCursor);

        DEFAULT_BEHAVIOURS.put(new ClickTrigger(ClickCategory.CURSOR, ClickType.RIGHT), cursorRightBehaviours);

    }

    private ItemStack item = null;

    public ItemOffer() {
        this(null);
    }

    public ItemOffer(ItemStack item) {
        this.item = item;
        addBehaviours(DEFAULT_BEHAVIOURS);
    }

    @Override
    public String getType() {
        return "item";
    }

    @Override
    public ItemOfferDescription getDescription(Trader trader) {
        return (ItemOfferDescription) super.getDescription(trader);
    }

    @Override
    public ItemStack createItem(TransactionHolder holder) {
        return getDescription(holder.getTrader()).createItem(this, holder);
    }

    @Override
    public ItemStack createMirrorItem(TransactionHolder holder) {
        return getDescription(holder.getTrader()).createMirrorItem(this, holder);
    }

    @Override
    public int getAmount() {
        return (item == null)? 0 : item.getAmount();
    }

    @Override
    public void setAmount(int amount) {

        if (item == null) {
            throw new IllegalArgumentException("Cannot set amount if item is zero");
        }

        item.setAmount(amount);

    }

    @Override
    public int getMaxAmount() {
        return (item == null)? 0 : item.getMaxStackSize();
    }

    @Override
    public boolean isFull() {
        return item != null && item.getMaxStackSize() - item.getAmount() <= 0;
    }

    @Override
    public void grant(final Trader trader) {
        Bukkit.getScheduler().runTask(Trade.getInstance(), new Runnable() {         //TODO Make this nicer
            @Override
            public void run() {
                trader.getPlayer().getInventory().addItem(item);
            }
        });
    }

    @Override
    public void grant(final Trader trader, int amount) {
        final ItemStack clone = item.clone();
        clone.setAmount(amount);
        Bukkit.getScheduler().runTask(Trade.getInstance(), new Runnable() {         //TODO Make this nicer
            @Override
            public void run() {
                trader.getPlayer().getInventory().addItem(clone);
            }
        });
    }

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public ItemOffer clone() {
        return new ItemOffer(item.clone());
    }

    @Override
    public boolean isSimilar(StackableOffer stackableOffer) {
        return stackableOffer instanceof ItemOffer && (getItem() != null) && getItem().isSimilar(((ItemOffer) stackableOffer).getItem());
    }

}
