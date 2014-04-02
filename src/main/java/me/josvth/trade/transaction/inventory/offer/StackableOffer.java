package me.josvth.trade.transaction.inventory.offer;


import me.josvth.trade.transaction.Trader;
import me.josvth.trade.transaction.inventory.click.ClickBehaviour;
import me.josvth.trade.transaction.inventory.click.ClickContext;
import me.josvth.trade.transaction.inventory.TransactionHolder;
import me.josvth.trade.transaction.inventory.slot.ContentSlot;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public abstract class StackableOffer extends Offer {

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
                    final StackableOffer stackableOffer = (StackableOffer) offer;

                    if (contentSlot.getContents() instanceof StackableOffer) {
                        final StackableOffer stackableContent = (StackableOffer) contentSlot.getContents();

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
                    final StackableOffer stackableOffer = (StackableOffer) offer;

                    if (contentSlot.getContents() == null) {    // PLACE_ONE

                        final StackableOffer stackableContent = stackableOffer.clone();
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
                    final StackableOffer stackableOffer = (StackableOffer) offer;

                    if (contentSlot.getContents() instanceof StackableOffer) {
                        final StackableOffer stackableContent = (StackableOffer) contentSlot.getContents();

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

                final StackableOffer stackableOffer = (StackableOffer) contentSlot.getContents();
                final StackableOffer splitOffer = StackableOffer.split(stackableOffer);

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

    public StackableOffer() {
        addCursorBehaviours(DEFAULT_CURSOR_BEHAVIOUR);
        addContentBehaviours(DEFAULT_CONTENT_BEHAVIOUR);
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

    public abstract void grant(Trader trader, boolean nextTick, int amount);

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
