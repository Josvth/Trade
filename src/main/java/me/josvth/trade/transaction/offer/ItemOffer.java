package me.josvth.trade.transaction.offer;

import me.josvth.trade.Trade;
import me.josvth.trade.transaction.action.trader.offer.SetOfferAction;
import me.josvth.trade.transaction.inventory.slot.Slot;
import me.josvth.trade.transaction.inventory.slot.TradeSlot;
import me.josvth.trade.transaction.offer.behaviour.ClickBehaviour;
import me.josvth.trade.transaction.offer.behaviour.ClickCategory;
import me.josvth.trade.transaction.offer.behaviour.ClickTrigger;
import me.josvth.trade.transaction.offer.description.ItemOfferDescription;
import me.josvth.trade.transaction.Trader;
import me.josvth.trade.transaction.inventory.TransactionHolder;
import me.josvth.trade.transaction.inventory.slot.MirrorSlot;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class ItemOffer extends StackableOffer {

    private static final Map<ClickTrigger, LinkedList<ClickBehaviour>> DEFAULT_BEHAVIOURS = new HashMap<ClickTrigger, LinkedList<ClickBehaviour>>();

    static {

        final LinkedList<ClickBehaviour> cursorLeftBehaviours = new LinkedList<ClickBehaviour>();

        // ADD_ALL, ADD_SOME
        cursorLeftBehaviours.add(new ClickBehaviour() {
            @Override
            public boolean onClick(InventoryClickEvent event, Slot slot, Offer offer) {
                if (slot instanceof TradeSlot) {
                    final TransactionHolder holder = (TransactionHolder) event.getInventory().getHolder();
                    if (((TradeSlot) slot).getContents(holder) instanceof StackableOffer) {
                        final StackableOffer contents = (StackableOffer) ((TradeSlot) slot).getContents(holder);
                        final StackableOffer stackableOffer = (StackableOffer) offer;
                        final int available = (contents.getMaxAmount() == -1)? -1 : contents.getMaxAmount() - contents.getAmount();
                        if (available == -1) {
                            contents.add(stackableOffer.getAmount());
                            stackableOffer.setAmount(0);

                            final SetOfferAction action = new SetOfferAction(holder.getTrader());
                            action.setOffer(((TradeSlot) slot).getOfferIndex(), contents);
                            action.execute();

                            holder.updateCursorOffer();

                            event.setCancelled(true);
                            return true;
                        }
                        if (available > 0) {
                            final int added = Math.min(available, stackableOffer.getAmount());
                            contents.add(added);
                            stackableOffer.remove(added);

                            final SetOfferAction action = new SetOfferAction(holder.getTrader());
                            action.setOffer(((TradeSlot) slot).getOfferIndex(), contents);
                            action.execute();

                            holder.updateCursorOffer();

                            event.setCancelled(true);
                            return true;
                        }
                    }
                }
                return false;
            }
        });

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
                        ((StackableOffer) offer).grant(holder.getTrader(), 1);
                        holder.updateCursorOffer();

                        event.setCancelled(true);
                        return true;
                    }
                }
                return false;
            }
        });

        // PLACE_ONE
        cursorRightBehaviours.add(new ClickBehaviour() {
            @Override
            public boolean onClick(InventoryClickEvent event, Slot slot, Offer offer) {
                if (slot instanceof TradeSlot) {
                    final TransactionHolder holder = (TransactionHolder) event.getInventory().getHolder();
                    if (((TradeSlot) slot).getContents(holder) == null) {
                        final StackableOffer single = ((StackableOffer) offer).clone();
                        single.setAmount(1);
                        ((StackableOffer) offer).remove(1);

                        final SetOfferAction action = new SetOfferAction(holder.getTrader());
                        action.setOffer(((TradeSlot) slot).getOfferIndex(), single);
                        action.execute();

                        holder.updateCursorOffer();

                        event.setCancelled(true);
                        return true;
                    }
                }
                return false;
            }
        });

        // ADD_ONE
        cursorRightBehaviours.add(new ClickBehaviour() {
            @Override
            public boolean onClick(InventoryClickEvent event, Slot slot, Offer offer) {
                if (slot instanceof TradeSlot) {
                    final TransactionHolder holder = (TransactionHolder) event.getInventory().getHolder();
                    if (((TradeSlot) slot).getContents(holder) instanceof StackableOffer) {
                        final StackableOffer contents = (StackableOffer) ((TradeSlot) slot).getContents(holder);
                        final StackableOffer stackableOffer = (StackableOffer) offer;
                        if ((stackableOffer.isSimilar(contents)) && (contents.getAmount() + 1 <= contents.getMaxAmount() && contents.getMaxAmount() != -1)) {
                            contents.add(1);
                            stackableOffer.remove(1);

                            final SetOfferAction action = new SetOfferAction(holder.getTrader());
                            action.setOffer(((TradeSlot) slot).getOfferIndex(), contents);
                            action.execute();

                            holder.updateCursorOffer();

                            event.setCancelled(true);
                            return true;
                        }
                    }
                }
                return false;
            }
        });

        DEFAULT_BEHAVIOURS.put(new ClickTrigger(ClickCategory.CURSOR, ClickType.RIGHT), cursorRightBehaviours);

        final LinkedList<ClickBehaviour> slotRightBehaviours = new LinkedList<ClickBehaviour>();

        slotRightBehaviours.add(new ClickBehaviour() {
            @Override
            public boolean onClick(InventoryClickEvent event, Slot slot, Offer offer) {
                if (slot instanceof TradeSlot) {
                    final TransactionHolder holder = (TransactionHolder) event.getInventory().getHolder();
                    StackableOffer split = StackableOffer.split((StackableOffer) offer);
                    holder.setCursorOffer(split, true);

                    SetOfferAction offerAction = new SetOfferAction(holder.getTrader());
                    offerAction.setOffer(((TradeSlot) slot).getOfferIndex(), offer);
                    offerAction.execute();

                    event.setCancelled(true);
                    return true;
                }
                return false;
            }
        });

        DEFAULT_BEHAVIOURS.put(new ClickTrigger(ClickCategory.SLOT, ClickType.RIGHT), slotRightBehaviours);
    }

    private ItemStack item = null;

    public ItemOffer() {
        this(null);
        addBehaviours(DEFAULT_BEHAVIOURS);
    }

    public ItemOffer(ItemStack item) {
        this.item = item;
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
