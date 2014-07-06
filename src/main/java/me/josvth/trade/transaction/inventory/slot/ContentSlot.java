package me.josvth.trade.transaction.inventory.slot;

import me.josvth.trade.Trade;
import me.josvth.trade.transaction.inventory.TransactionHolder;
import me.josvth.trade.transaction.inventory.interact.ClickBehaviour;
import me.josvth.trade.transaction.inventory.interact.ClickContext;
import me.josvth.trade.transaction.inventory.interact.DragBehaviour;
import me.josvth.trade.transaction.inventory.interact.DragContext;
import me.josvth.trade.transaction.inventory.offer.Offer;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.DragType;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class ContentSlot extends Slot {

    private static final Map<ClickType, List<ClickBehaviour>> DEFAULT_CLICK_BEHAVIOURS = new LinkedHashMap<ClickType, List<ClickBehaviour>>();
    private static final Map<DragType, List<DragBehaviour>> DEFAULT_DRAG_BEHAVIOURS = new LinkedHashMap<DragType, List<DragBehaviour>>();

    static {

        // Click behaviours
        DEFAULT_CLICK_BEHAVIOURS.put(ClickType.LEFT, new LinkedList<ClickBehaviour>());
        DEFAULT_CLICK_BEHAVIOURS.put(ClickType.RIGHT, new LinkedList<ClickBehaviour>());
        DEFAULT_CLICK_BEHAVIOURS.put(ClickType.NUMBER_KEY, new LinkedList<ClickBehaviour>());

        // PICKUP_ALL
        DEFAULT_CLICK_BEHAVIOURS.get(ClickType.LEFT).add(new ClickBehaviour() {
            @Override
            public boolean onClick(ClickContext context, Offer offer) {

                final ContentSlot slot = (ContentSlot) context.getSlot();
                final Offer cursor = context.getCursorOffer();

                if (slot.getContents() != null && cursor == null) {

                    // Update cursor
                    context.getHolder().setCursorOffer(slot.getContents(), true);

                    // Update slot
                    slot.setContents(null);

                    context.getEvent().setCancelled(true);

                    return true;

                }

                return false;
            }

            @Override
            public String getName() {
                return "PICKUP_ALL";
            }
        });

        // PICKUP_SOME ???

        // PICKUP_HALF
        DEFAULT_CLICK_BEHAVIOURS.get(ClickType.RIGHT).add(new ClickBehaviour() {
            @Override
            public boolean onClick(ClickContext context, Offer offer) {

                final ContentSlot slot = (ContentSlot) context.getSlot();

                final Offer splitOffer = Offer.split(slot.getContents());

                if (slot.getContents().isWorthless()) {
                    slot.setContents(null);
                } else {
                    slot.setContents(slot.getContents());
                }

                context.getHolder().setCursorOffer(splitOffer, true);

                context.getEvent().setCancelled(true);
                return true;

            }

            @Override
            public String getName() {
                return "PICKUP_HALF";
            }

        });

        // PICKUP_ONE ??

        // PLACE_ALL
        DEFAULT_CLICK_BEHAVIOURS.get(ClickType.LEFT).add(new ClickBehaviour() {
            @Override
            public boolean onClick(ClickContext context, Offer offer) {

                final ContentSlot slot = (ContentSlot) context.getSlot();
                final Offer cursor = context.getCursorOffer();

                if (slot.getContents() == null && cursor != null) {

                    // Update cursor
                    context.getHolder().setCursorOffer(null, true);

                    // Update slot
                    slot.setContents(cursor);

                    context.getEvent().setCancelled(true);

                    return true;

                }

                return false;
            }

            @Override
            public String getName() {
                return "PLACE_ALL";
            }
        });

        // PLACE_ONE (Note: We define PLACING as in placing in an empty slot and ADDING as adding to a partially filled slot)
        DEFAULT_CLICK_BEHAVIOURS.get(ClickType.RIGHT).add(new ClickBehaviour() {
            @Override
            public boolean onClick(ClickContext context, Offer offer) {
                if (context.getSlot() instanceof ContentSlot) {

                    final ContentSlot slot = (ContentSlot) context.getSlot();
                    final Offer cursor = context.getCursorOffer();

                    if (slot.getContents() == null) {

                        final Offer single = Offer.takeOne(cursor);

                        // Update cursor
                        if (cursor.isWorthless()) {
                            context.getHolder().setCursorOffer(null, true);
                        } else {
                            context.getHolder().setCursorOffer(cursor, true);
                        }

                        // Update slot
                        slot.setContents(single);

                        context.getEvent().setCancelled(true);
                        return true;
                    }

                }

                return false;

            }

            public String getName() {
                return "PLACE_ONE";
            }
        });

        // ADD_SOME (Bukkit: PLACE_SOME) (Note: Includes adding one which Bukkit calls PLACE_ONE)
        DEFAULT_CLICK_BEHAVIOURS.get(ClickType.LEFT).add(new ClickBehaviour() {
            @Override
            public boolean onClick(ClickContext context, Offer offer) {

                final ContentSlot slot = (ContentSlot) context.getSlot();
                final Offer cursor = context.getCursorOffer();

                if (slot.getContents() != null && cursor != null && slot.getContents().isSimilar(cursor)) {

                    final double remaining = slot.getContents().add(cursor.getAmount());

                    if (remaining != cursor.getAmount()) {  // We added something

                        cursor.setAmount(remaining);

                        // Update cursor
                        if (cursor.isWorthless()) {
                            context.getHolder().setCursorOffer(null, true);
                        } else {
                            context.getHolder().setCursorOffer(cursor, true);
                        }

                        // Update slot
                        slot.setContents(slot.getContents());

                    }

                    context.getEvent().setCancelled(true);

                    return true;

                }

                return false;
            }

            public String getName() {
                return "ADD_SOME";
            }

        });

        // ADD_ONE (Bukkit: PLACE_ONE)
        DEFAULT_CLICK_BEHAVIOURS.get(ClickType.RIGHT).add(new ClickBehaviour() {
            @Override
            public boolean onClick(ClickContext context, Offer offer) {

                final ContentSlot slot = (ContentSlot) context.getSlot();
                final Offer cursor = context.getCursorOffer();

                if (slot.getContents() != null && slot.getContents().isSimilar(cursor)) {

                    final double remaining = slot.getContents().add(1);

                    if (remaining == 0) {   // We added one something

                        cursor.remove(1);

                        // Update cursor
                        if (cursor.isWorthless()) {
                            context.setCursorOffer(null, true);
                        } else {
                            context.setCursorOffer(cursor, true);
                        }

                        // Update slot
                        slot.setContents(slot.getContents());

                    }

                    context.getEvent().setCancelled(true);
                    return true;

                }

                return false;

            }

            public String getName() {
                return "ADD_ONE";
            }

        });

        // SWAP_WITH_CURSOR
        final ClickBehaviour swapWithCursor = new ClickBehaviour() {
            @Override
            public boolean onClick(ClickContext context, Offer offer) {

                final ContentSlot slot = (ContentSlot) context.getSlot();
                final Offer cursor = context.getCursorOffer();

                if (cursor != null && slot.getContents() != null && !slot.getContents().isSimilar(cursor)) {

                    // Update cursor
                    context.setCursorOffer(slot.getContents(), true);

                    // Update slot
                    slot.setContents(cursor);

                    context.setCancelled(true); // TODO not cancelling this is risky but it doesn't show the updating
                    return true;
                }

                return false;

            }

            public String getName() {
                return "SWAP_WITH_CURSOR";
            }

        };

        DEFAULT_CLICK_BEHAVIOURS.get(ClickType.LEFT).add(swapWithCursor);
        DEFAULT_CLICK_BEHAVIOURS.get(ClickType.RIGHT).add(swapWithCursor);

        // Drag behaviours
        DEFAULT_DRAG_BEHAVIOURS.put(DragType.SINGLE, new LinkedList<DragBehaviour>());
        DEFAULT_DRAG_BEHAVIOURS.put(DragType.EVEN, new LinkedList<DragBehaviour>());

        DEFAULT_DRAG_BEHAVIOURS.get(DragType.EVEN).add(new DragBehaviour() {
            @Override
            public boolean onDrag(final DragContext context, Slot slot, Offer offer) {

                final ContentSlot contentSlot = (ContentSlot) slot;

                Offer contents = contentSlot.getContents();
                final Offer cursor = context.getCursorOffer();

                if (contents == null || contents.isSimilar(cursor)) {

//                    // We count the amount of content slots that is dragged over
//                    int amount = 0;
//                    for (Slot draggedSlot : context.getSlots()) {
//                        if (draggedSlot instanceof ContentSlot) {
//                            amount++;
//                        }
//                    }

                    final double increase = cursor.getAmount() / context.getSlots().size();

                    final double taken;

                    if (contents != null) {
                        taken = increase - contents.add(increase);
                    } else {
                        contents = cursor.clone();
                        contents.setAmount(0);      // We first set the contents to zero so we can use .add()
                        taken = increase - contents.add(increase);  // Because add returns us what we couldn't add think of adding 1.667 to a item stack
                    }

                    if (taken > 0) {    // We added something

                        contentSlot.setContents(contents);

                        // TODO Do something about this
                        Bukkit.getScheduler().runTask(Trade.getInstance(), new Runnable() {
                            @Override
                            public void run() {

                                // Check if we still have the same cursor
                                if (context.getCursorOffer() != null && context.getCursorOffer().isSimilar(cursor)) {
                                    context.getCursorOffer().remove(taken);
                                    context.getHolder().updateCursorOffer();
                                }

                            }
                        });

                    }


                }

                return false;

            }
        });

        DEFAULT_DRAG_BEHAVIOURS.get(DragType.SINGLE).add(new DragBehaviour() {
            @Override
            public boolean onDrag(final DragContext context, Slot slot, Offer offer) {

                final ContentSlot contentSlot = (ContentSlot) slot;

                Offer contents = contentSlot.getContents();
                final Offer cursor = context.getCursorOffer();

                if (contents == null || contents.isSimilar(cursor)) {

//                    // We count the amount of content slots that is dragged over
//                    int amount = 0;
//                    for (Slot draggedSlot : context.getSlots()) {
//                        if (draggedSlot instanceof ContentSlot) {
//                            amount++;
//                        }
//                    }

                    final double taken;

                    if (contents != null) {
                        taken = 1 - contents.add(1);
                    } else {
                        taken = 1;
                        contents = cursor.clone();
                        contents.setAmount(1);
                    }

                    if (taken > 0) {    // We added something

                        contentSlot.setContents(contents);

                        // TODO Do something about this
                        Bukkit.getScheduler().runTask(Trade.getInstance(), new Runnable() {
                            @Override
                            public void run() {

                                // Check if we still have the same cursor
                                if (context.getCursorOffer() != null && context.getCursorOffer().isSimilar(cursor)) {
                                    context.getCursorOffer().remove(taken);
                                    context.getHolder().updateCursorOffer();
                                }

                            }
                        });

                    }


                }

                return false;

            }
        });

    }

    public ContentSlot(int slot, TransactionHolder holder) {
        super(slot, holder);
        addClickBehaviours(DEFAULT_CLICK_BEHAVIOURS);
        addDragBehaviours(DEFAULT_DRAG_BEHAVIOURS);
    }

    public abstract Offer getContents();

    public abstract void setContents(Offer contents);

    @Override
    public boolean onClick(ClickContext context) {

        final Offer contents = getContents();

        if (contents != null) {

            contents.onContentClick(context);

            if (context.isHandled()) {
                return true;
            }

        }

        return super.onClick(context);

    }

    @Override
    public String toString() {
        return new StringBuilder(getClass().getSimpleName()).append("{").append((getContents() == null)? null : getContents().toString()).append("}").toString();
    }
}
