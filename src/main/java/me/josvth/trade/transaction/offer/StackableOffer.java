package me.josvth.trade.transaction.offer;


import me.josvth.trade.transaction.Trader;
import me.josvth.trade.transaction.action.trader.offer.SetOfferAction;
import me.josvth.trade.transaction.inventory.TransactionHolder;
import me.josvth.trade.transaction.inventory.slot.Slot;
import me.josvth.trade.transaction.inventory.slot.TradeSlot;
import me.josvth.trade.transaction.offer.behaviour.ClickBehaviour;
import me.josvth.trade.transaction.offer.behaviour.ClickCategory;
import me.josvth.trade.transaction.offer.behaviour.ClickTrigger;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public abstract class StackableOffer extends Offer {

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
                            final SetOfferAction action = new SetOfferAction(holder.getTrader());
                            action.setOffer(((TradeSlot) slot).getOfferIndex(), contents);
                            action.execute();

                            stackableOffer.setAmount(0);
                            holder.updateCursorOffer();

                            event.setCancelled(true);
                            return true;
                        }
                        if (available > 0) {
                            final int added = Math.min(available, stackableOffer.getAmount());

                            contents.add(added);
                            final SetOfferAction action = new SetOfferAction(holder.getTrader());
                            action.setOffer(((TradeSlot) slot).getOfferIndex(), contents);
                            action.execute();

                            stackableOffer.remove(added);
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
                        final StackableOffer stackableOffer = (StackableOffer) offer;

                        stackableOffer.grant(holder.getTrader(), 1);

                        stackableOffer.remove(1);
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
                        final SetOfferAction action = new SetOfferAction(holder.getTrader());
                        action.setOffer(((TradeSlot) slot).getOfferIndex(), single);
                        action.execute();

                        ((StackableOffer) offer).remove(1);
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
                            final SetOfferAction action = new SetOfferAction(holder.getTrader());
                            action.setOffer(((TradeSlot) slot).getOfferIndex(), contents);
                            action.execute();

                            stackableOffer.remove(1);
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

    public StackableOffer() {
        addBehaviours(DEFAULT_BEHAVIOURS);
    }

    public abstract int getAmount();

    public abstract void setAmount(int amount);

    public abstract int getMaxAmount();

    public boolean isFull() {
        return false;
    }

    public boolean isWorthless() {
        return getAmount() == 0;
    }

    public int add(int amount) {

        if (getMaxAmount() == -1) { // If infinite stackable, add all
            setAmount(getAmount() + amount);
            return 0;
        }

        final int remainder = getAmount() + amount - getMaxAmount();
        if (remainder > 0) {
            setAmount(getMaxAmount());
            return remainder;
        } else {
            setAmount(getAmount() + amount);
            return 0;
        }

    }

    public int remove(int amount) {
        final int remainder = getAmount() - amount;
        if (remainder > 0) {
            setAmount(remainder);
            return 0;
        } else {
            setAmount(0);
            return -1 * remainder;
        }
    }

    public abstract void grant(Trader trader, int amount);

    public abstract StackableOffer clone();

    public abstract boolean isSimilar(StackableOffer contents);

    //TODO Cleanup offer creation and cloning
    public static <T extends StackableOffer> T split(T offer) {

        final int total = offer.getAmount();

        final T clone = (T) offer.clone();

        offer.setAmount(total / 2);

        clone.setAmount(total - offer.getAmount());

        return clone;

    }

    public static <T extends StackableOffer> T takeOne(T offer) {

        if (offer.getAmount() == 1) {
            throw new IllegalArgumentException("StackableOffer must have an amount greater than 1");
        }

        final T clone = (T) offer.clone();

        offer.setAmount(offer.getAmount() - 1);

        clone.setAmount(1);

        return clone;

    }
}
