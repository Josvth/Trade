package me.josvth.trade.transaction.inventory.offer;


import me.josvth.trade.transaction.Trader;
import me.josvth.trade.transaction.inventory.TransactionHolder;
import me.josvth.trade.transaction.inventory.click.ClickBehaviour;
import me.josvth.trade.transaction.inventory.click.ClickContext;
import me.josvth.trade.transaction.inventory.offer.description.OfferDescription;
import me.josvth.trade.transaction.inventory.slot.ContentSlot;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public abstract class Offer {

    private static final Map<ClickType, List<ClickBehaviour>> DEFAULT_CURSOR_BEHAVIOUR = new HashMap<ClickType, List<ClickBehaviour>>();
    private static final Map<ClickType, List<ClickBehaviour>> DEFAULT_CONTENT_BEHAVIOUR = new HashMap<ClickType, List<ClickBehaviour>>();

    static {

        // CURSOR
        DEFAULT_CURSOR_BEHAVIOUR.put(ClickType.LEFT, new LinkedList<ClickBehaviour>());
        DEFAULT_CURSOR_BEHAVIOUR.get(ClickType.LEFT).add(new ClickBehaviour() {

            @Override
            public boolean onClick(ClickContext context, Offer offer) {
                if (context.getSlot() instanceof ContentSlot) {

                    final ContentSlot contentSlot = (ContentSlot) context.getSlot();
                    final Offer stackableOffer = (Offer) offer;

                    if (contentSlot.getContents() instanceof Offer) {
                        final Offer stackableContent = (Offer) contentSlot.getContents();

                        if (stackableOffer.isSimilar(stackableContent)) {   // ADD

                            final int remaining = stackableContent.add(stackableOffer.getAmount());

                            if (remaining != stackableOffer.getAmount()) {  // We added something
                                stackableOffer.setAmount(remaining);
                                if (stackableOffer.isWorthless()) {
                                    context.getHolder().setCursorOffer(null, true);
                                } else {
                                    context.getHolder().setCursorOffer(stackableOffer, true);
                                }
                                contentSlot.setContents(stackableContent);
                            }

                            ((Player) context.getEvent().getWhoClicked()).sendMessage("ADD");

                            context.getEvent().setCancelled(true);
                            return true;

                        }

                    }

                }

                return false;

            }
        });

        DEFAULT_CURSOR_BEHAVIOUR.put(ClickType.RIGHT, new LinkedList<ClickBehaviour>());
        DEFAULT_CURSOR_BEHAVIOUR.get(ClickType.RIGHT).add(new ClickBehaviour() {
            @Override
            public boolean onClick(ClickContext context, Offer offer) {
                if (context.getSlot() instanceof ContentSlot) {

                    final ContentSlot contentSlot = (ContentSlot) context.getSlot();
                    final Offer stackableOffer = (Offer) offer;

                    if (contentSlot.getContents() == null) {    // PLACE_ONE

                        final Offer stackableContent = stackableOffer.clone();
                        stackableContent.setAmount(1);

                        stackableOffer.remove(1);
                        if (stackableOffer.isWorthless()) {
                            context.getHolder().setCursorOffer(null, true);
                        } else {
                            context.getHolder().setCursorOffer(stackableOffer, true);
                        }
                        contentSlot.setContents(stackableContent);

                        context.getEvent().setCancelled(true);
                        return true;
                    }

                }

                return false;

            }
        });

        DEFAULT_CURSOR_BEHAVIOUR.get(ClickType.RIGHT).add(new ClickBehaviour() {
            @Override
            public boolean onClick(ClickContext context, Offer offer) {
                if (context.getSlot() instanceof ContentSlot) {

                    final TransactionHolder holder = context.getHolder();
                    final ContentSlot contentSlot = (ContentSlot) context.getSlot();
                    final Offer stackableOffer = (Offer) offer;

                    if (contentSlot.getContents() instanceof Offer) {
                        final Offer stackableContent = (Offer) contentSlot.getContents();

                        if (stackableOffer.isSimilar(stackableContent)) {   // ADD_ONE

                            final int remaining = stackableContent.add(1);

                            if (remaining == 0) {   // We added one something
                                stackableOffer.remove(1);
                                if (stackableOffer.isWorthless()) {
                                    holder.setCursorOffer(null, true);
                                } else {
                                    holder.setCursorOffer(stackableOffer, true);
                                }
                                contentSlot.setContents(stackableContent);
                            }

                            context.getEvent().setCancelled(true);
                            return true;

                        }

                    }

                }

                return false;

            }
        });


        // CONTENT
        DEFAULT_CONTENT_BEHAVIOUR.put(ClickType.RIGHT, new LinkedList<ClickBehaviour>());
        DEFAULT_CONTENT_BEHAVIOUR.get(ClickType.RIGHT).add(new ClickBehaviour() {
            @Override
            public boolean onClick(ClickContext context, Offer offer) {

                final ContentSlot contentSlot = (ContentSlot) context.getSlot();

                final Offer stackableOffer = (Offer) contentSlot.getContents();
                final Offer splitOffer = Offer.split(stackableOffer);

                if (stackableOffer.isWorthless()) {
                    contentSlot.setContents(null);
                } else {
                    contentSlot.setContents(stackableOffer);
                }

                context.getHolder().setCursorOffer(splitOffer, true);

                context.getEvent().setCancelled(true);
                return true;

            }
        });

    }

    protected final Map<ClickType, List<ClickBehaviour>> cursorClickBehaviourMap = DEFAULT_CURSOR_BEHAVIOUR;
    protected final Map<ClickType, List<ClickBehaviour>> contentClickBehaviourMap = DEFAULT_CONTENT_BEHAVIOUR;

    protected boolean allowedInInventory = false;
    protected boolean canStayInInventory = false;

    //TODO Cleanup offer creation and cloning
    public static <T extends Offer> T split(T offer) {

        final int total = offer.getAmount();

        final T clone = (T) offer.clone();

        offer.setAmount(total / 2);

        clone.setAmount(total - offer.getAmount());

        return clone;

    }

    public static <T extends Offer> T takeOne(T offer) {

        if (offer.getAmount() == 1) {
            throw new IllegalArgumentException("StackableOffer must have an amount greater than 1");
        }

        final T clone = (T) offer.clone();

        offer.setAmount(offer.getAmount() - 1);

        clone.setAmount(1);

        return clone;

    }

    public abstract int getAmount();

    public abstract void setAmount(int amount);

    public abstract int getMaxAmount();

    public boolean isFull() {
        return getAmount() == getMaxAmount();
    }

    public boolean isWorthless() {
        return getAmount() == 0;
    }

    public int add(int amount) {

        if (getMaxAmount() == -1) { // If infinite stackable, add all
            setAmount(getAmount() + amount);
            return 0;
        }

        if (isFull()) {
            return amount;
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

    public abstract void grant(Trader trader, boolean nextTick, int amount);

    public abstract Offer clone();

    public abstract boolean isSimilar(Offer offer);

    public OfferDescription<? extends Offer> getDescription(Trader trader) {
        return trader.getLayout().getOfferDescription(this.getClass());
    }

    public abstract String getType();

    public abstract ItemStack createItem(TransactionHolder holder);

    public abstract ItemStack createMirrorItem(TransactionHolder holder);

    public abstract void grant(Trader trader, boolean nextTick);

    public boolean isAllowedInInventory() {
        return allowedInInventory;
    }

    public void setAllowedInInventory(boolean allowedInInventory) {
        this.allowedInInventory = allowedInInventory;
    }

    public boolean isCanStayInInventory() {
        return canStayInInventory;
    }

    public void setCanStayInInventory(boolean canStayInInventory) {
        this.canStayInInventory = canStayInInventory;
    }

    public boolean canStayInInventory() {
        return canStayInInventory;
    }

    // Behaviours
    public void addCursorBehaviour(ClickType clickType, ClickBehaviour behaviour) {
        List<ClickBehaviour> behaviours = cursorClickBehaviourMap.get(clickType);
        if (behaviours == null) {
            behaviours = new LinkedList<ClickBehaviour>();
            cursorClickBehaviourMap.put(clickType, behaviours);
        }
        behaviours.add(behaviour);
    }

    public void addCursorBehaviours(Map<ClickType, List<ClickBehaviour>> behaviours) {
        for (Map.Entry<ClickType, List<ClickBehaviour>> entry : behaviours.entrySet()) {
            for (ClickBehaviour behaviour : entry.getValue()) {
                addCursorBehaviour(entry.getKey(), behaviour);
            }
        }
    }

    public void addContentBehaviour(ClickType clickType, ClickBehaviour behaviour) {
        List<ClickBehaviour> behaviours = contentClickBehaviourMap.get(clickType);
        if (behaviours == null) {
            behaviours = new LinkedList<ClickBehaviour>();
            contentClickBehaviourMap.put(clickType, behaviours);
        }
        behaviours.add(behaviour);
    }

    public void addContentBehaviours(Map<ClickType, List<ClickBehaviour>> behaviours) {
        for (Map.Entry<ClickType, List<ClickBehaviour>> entry : behaviours.entrySet()) {
            for (ClickBehaviour behaviour : entry.getValue()) {
                addContentBehaviour(entry.getKey(), behaviour);
            }
        }
    }

    public Map<ClickType, List<ClickBehaviour>> getCursorClickBehaviourMap() {
        return cursorClickBehaviourMap;
    }

    public Map<ClickType, List<ClickBehaviour>> getContentClickBehaviourMap() {
        return contentClickBehaviourMap;
    }

    // Event handling
    public boolean onCursorClick(ClickContext context) {

        final List<ClickBehaviour> behaviours = getCursorClickBehaviourMap().get(context.getEvent().getClick());

        if (behaviours != null) {

            final ListIterator<ClickBehaviour> iterator = behaviours.listIterator(behaviours.size());

            boolean executed = false;

            while (iterator.hasPrevious() && !executed) {
                executed = iterator.previous().onClick(context, this);
            }

            if (executed) {
                return true;
            }

        }

        return false;

    }

    public boolean onDrag(InventoryDragEvent event, int offerIndex, int slotIndex) {
        event.setCancelled(true);
        return true;
    }

    public boolean onContentClick(ClickContext context) {

        final List<ClickBehaviour> behaviours = getContentClickBehaviourMap().get(context.getEvent().getClick());

        if (behaviours != null) {

            final ListIterator<ClickBehaviour> iterator = behaviours.listIterator(behaviours.size());

            boolean executed = false;

            while (iterator.hasPrevious() && !executed) {
                executed = iterator.previous().onClick(context, this);
            }

            if (executed) {
                return true;
            }

        }

        return false;

    }

    public boolean isDraggable() {
        return false;
    }
}
