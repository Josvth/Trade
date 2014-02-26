package me.josvth.trade.transaction.offer;

import me.josvth.trade.transaction.Trader;
import me.josvth.trade.transaction.action.trader.offer.ChangeOfferAction;
import me.josvth.trade.transaction.action.trader.offer.SetOfferAction;
import me.josvth.trade.transaction.inventory.TransactionHolder;
import me.josvth.trade.transaction.inventory.slot.Slot;
import me.josvth.trade.transaction.inventory.slot.TradeSlot;
import me.josvth.trade.transaction.offer.behaviour.ClickBehaviour;
import me.josvth.trade.transaction.offer.behaviour.ClickCategory;
import me.josvth.trade.transaction.offer.behaviour.ClickTrigger;
import me.josvth.trade.transaction.offer.description.OfferDescription;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

public abstract class Offer {

    private static final Map<ClickTrigger, LinkedList<ClickBehaviour>> DEFAULT_BEHAVIOURS = new HashMap<ClickTrigger, LinkedList<ClickBehaviour>>();

    static {

        final LinkedList<ClickBehaviour> cursorLeftBehaviours = new LinkedList<ClickBehaviour>();

        // Clicking an empty inventory slot with an offer (GRANT)
        cursorLeftBehaviours.add(new ClickBehaviour() {
            @Override
            public boolean onClick(InventoryClickEvent event, Slot slot, Offer offer) {
                if (slot == null) {
                    final TransactionHolder holder = (TransactionHolder) event.getInventory().getHolder();
                    final ItemStack item = event.getCurrentItem();
                    if (item == null || item.getType() == Material.AIR) {
                        holder.setCursorOffer(null, true);
                        offer.grant(holder.getTrader());
                        event.setCancelled(true);
                        return true;
                    }
                }
                return false;
            }
        });

        // Clicking an filled inventory slot with an offer (offer: GRANT, item: PICKUP_ALL)
        cursorLeftBehaviours.add(new ClickBehaviour() {
            @Override
            public boolean onClick(InventoryClickEvent event, Slot slot, Offer offer) {
                if (slot == null) {
                    final TransactionHolder holder = (TransactionHolder) event.getInventory().getHolder();
                    final ItemStack item = event.getCurrentItem();
                    if (item != null) {
                        holder.setCursorOffer(new ItemOffer(item.clone()), true);   // Place item on cursor
                        event.setCurrentItem(null);                                 // Remove item from slot
                        offer.grant(holder.getTrader());                            // Grant offer
                        event.setCancelled(true);
                        return true;
                    }
                }
                return false;
            }
        });

        // Clicking an empty trade slot with an offer (PLACE_ALL)
        cursorLeftBehaviours.add(new ClickBehaviour() {
            @Override
            public boolean onClick(InventoryClickEvent event, Slot slot, Offer offer) {
                if (slot instanceof TradeSlot) {
                    final TransactionHolder holder = (TransactionHolder) event.getInventory().getHolder();
                    final Offer contents = ((TradeSlot) slot).getContents(holder);
                    if (contents == null) {
                        holder.setCursorOffer(null, true);
                        final SetOfferAction offerAction = new SetOfferAction(holder.getTrader());
                        offerAction.setOffer(((TradeSlot) slot).getOfferIndex(), offer);
                        offerAction.execute();
                        return true;
                    }
                }
                return false;
            }
        });

        // Clicking a filled trade slot with an offer (SWAP_WITH_CURSOR)
        cursorLeftBehaviours.add(new ClickBehaviour() {
            @Override
            public boolean onClick(InventoryClickEvent event, Slot slot, Offer offer) {
                if (slot instanceof TradeSlot) {
                    final TransactionHolder holder = (TransactionHolder) event.getInventory().getHolder();
                    final Offer contents = ((TradeSlot) slot).getContents(holder);
                    if (contents != null) {
                        holder.setCursorOffer(((TradeSlot) slot).getContents(holder), true);
                        final SetOfferAction offerAction = new SetOfferAction(holder.getTrader());
                        offerAction.setOffer(((TradeSlot) slot).getOfferIndex(), offer);
                        offerAction.execute();
                        return true;
                    }
                }
                return false;
            }
        });

        DEFAULT_BEHAVIOURS.put(new ClickTrigger(ClickCategory.CURSOR, ClickType.LEFT), cursorLeftBehaviours);
        DEFAULT_BEHAVIOURS.put(new ClickTrigger(ClickCategory.CURSOR, ClickType.RIGHT), new LinkedList<ClickBehaviour>(cursorLeftBehaviours));

        final LinkedList<ClickBehaviour> cursorShiftLeftBehaviours = new LinkedList<ClickBehaviour>();

        // Shift clicking an filled inventory slot with an offer (MOVE_TO_OTHER_INVENTORY)
        cursorShiftLeftBehaviours.add(new ClickBehaviour() {
            @Override
            public boolean onClick(InventoryClickEvent event, Slot slot, Offer offer) {
                if (slot == null) {
                    final TransactionHolder holder = (TransactionHolder) event.getInventory().getHolder();
                    final ItemStack item = event.getCurrentItem();
                    if (item != null) {
                        final ChangeOfferAction offerAction = new ChangeOfferAction(holder.getTrader());
                        offerAction.setOffer(new ItemOffer(item.clone()));
                        offerAction.execute();
                        if (offerAction.getRemaining() > 0) {
                            item.setAmount(offerAction.getRemaining());
                            event.setCurrentItem(item);     // TODO Check if this is safe to use in event
                        } else {
                            event.setCurrentItem(null);     // TODO Check if this is safe to use in event
                        }
                        return true;
                    }
                }
                return false;
            }
        });

        // Shift clicking a filled trade slot with an offer (GRANT)
        cursorShiftLeftBehaviours.add(new ClickBehaviour() {
            @Override
            public boolean onClick(InventoryClickEvent event, Slot slot, Offer offer) {
                if (slot instanceof TradeSlot) {
                    final TransactionHolder holder = (TransactionHolder) event.getInventory().getHolder();
                    final Offer contents = ((TradeSlot) slot).getContents(holder);
                    if (contents != null) {
                        contents.grant(holder.getTrader());
                        final SetOfferAction offerAction = new SetOfferAction(holder.getTrader());
                        offerAction.setOffer(((TradeSlot) slot).getOfferIndex(), null);
                        offerAction.execute();
                        event.setCancelled(true);
                        return true;
                    }
                }
                return false;
            }
        });

        DEFAULT_BEHAVIOURS.put(new ClickTrigger(ClickCategory.CURSOR, ClickType.SHIFT_LEFT), cursorShiftLeftBehaviours);
        DEFAULT_BEHAVIOURS.put(new ClickTrigger(ClickCategory.CURSOR, ClickType.SHIFT_RIGHT), new LinkedList<ClickBehaviour>(cursorShiftLeftBehaviours));

        final LinkedList<ClickBehaviour> slotLeftBehaviours = new LinkedList<ClickBehaviour>();

        slotLeftBehaviours.add(new ClickBehaviour() {
            @Override
            public boolean onClick(InventoryClickEvent event, Slot slot, Offer offer) {
                if (slot instanceof TradeSlot) {
                    final TransactionHolder holder = (TransactionHolder) event.getInventory().getHolder();
                    holder.setCursorOffer(offer, true);
                    SetOfferAction offerAction = new SetOfferAction(holder.getTrader());
                    offerAction.setOffer(((TradeSlot) slot).getOfferIndex(), null);
                    offerAction.execute();
                    event.setCancelled(true);
                    return true;
                }
                return false;
            }
        });

        DEFAULT_BEHAVIOURS.put(new ClickTrigger(ClickCategory.SLOT, ClickType.LEFT), slotLeftBehaviours);
        DEFAULT_BEHAVIOURS.put(new ClickTrigger(ClickCategory.SLOT, ClickType.RIGHT), new LinkedList<ClickBehaviour>(slotLeftBehaviours));

        final LinkedList<ClickBehaviour> slotShiftLeftBehaviours = new LinkedList<ClickBehaviour>();

        slotShiftLeftBehaviours.add(new ClickBehaviour() {
            @Override
            public boolean onClick(InventoryClickEvent event, Slot slot, Offer offer) {
                if (slot instanceof TradeSlot) {
                    final TransactionHolder holder = (TransactionHolder) event.getInventory().getHolder();
                    offer.grant(holder.getTrader());
                    SetOfferAction offerAction = new SetOfferAction(holder.getTrader());
                    offerAction.setOffer(((TradeSlot) slot).getOfferIndex(), null);
                    offerAction.execute();
                    event.setCancelled(true);
                    return true;
                }
                return false;
            }
        });

        DEFAULT_BEHAVIOURS.put(new ClickTrigger(ClickCategory.SLOT, ClickType.SHIFT_LEFT), slotShiftLeftBehaviours);
        DEFAULT_BEHAVIOURS.put(new ClickTrigger(ClickCategory.SLOT, ClickType.SHIFT_RIGHT), new LinkedList<ClickBehaviour>(slotShiftLeftBehaviours));

    }

    private Map<ClickTrigger, LinkedList<ClickBehaviour>> behaviours = new HashMap<ClickTrigger, LinkedList<ClickBehaviour>>();

    public Offer() {
        addBehaviours(DEFAULT_BEHAVIOURS);
    }

    public OfferDescription<? extends Offer> getDescription(Trader trader) {
        return trader.getLayout().getOfferDescription(this.getClass());
    }

    public abstract String getType();

    public abstract ItemStack createItem(TransactionHolder holder);

    public abstract ItemStack createMirrorItem(TransactionHolder holder);

    public abstract void grant(Trader trader);

    protected void addBehaviour(ClickTrigger trigger, ClickBehaviour behaviour) {
        getOrCreateBehavioursList(trigger).addFirst(behaviour);
    }

    protected void addBehaviours(Map<ClickTrigger, LinkedList<ClickBehaviour>> behaviours) {
        for (Map.Entry<ClickTrigger, LinkedList<ClickBehaviour>> entry : behaviours.entrySet()) {
            final Iterator<ClickBehaviour> iterator = entry.getValue().descendingIterator();
            final LinkedList<ClickBehaviour> list = getOrCreateBehavioursList(entry.getKey());
            while (iterator.hasNext()) {
                list.addFirst(iterator.next());
            }
        }
    }

    private LinkedList<ClickBehaviour> getOrCreateBehavioursList(ClickTrigger trigger) {
        LinkedList<ClickBehaviour> list = behaviours.get(trigger);
        if (list == null) {
            list = new LinkedList<ClickBehaviour>();
            behaviours.put(trigger, list);
        }
        return list;
    }

    // Event handling
    public void onClick(InventoryClickEvent event, Slot slot, ClickCategory category) {

        final ClickTrigger trigger = new ClickTrigger(category, event.getClick());

        final LinkedList<ClickBehaviour> behaviours = this.behaviours.get(trigger);

        if (behaviours != null && !behaviours.isEmpty()) {

            boolean executed = false;

            final Iterator<ClickBehaviour> iterator = behaviours.iterator();

            while(!executed && iterator.hasNext()) {
                final ClickBehaviour behaviour = iterator.next();
                executed = behaviour.onClick(event, slot, this);
            }

            if (!executed) {
                event.setCancelled(true);
            }

        } else {
            event.setCancelled(true);
        }

    }

    public void onDrag(InventoryDragEvent event, int offerIndex, int slotIndex) {
        event.setCancelled(true);
    }

    public boolean isDraggable() {
        return false;
    }

    public abstract Offer clone();

}
