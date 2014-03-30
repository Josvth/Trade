package me.josvth.trade.transaction.inventory.slot;

import me.josvth.trade.transaction.click.ClickBehaviour;
import me.josvth.trade.transaction.click.ClickContext;
import me.josvth.trade.transaction.inventory.TransactionHolder;
import me.josvth.trade.transaction.offer.Offer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class ContentSlot extends Slot {

    private static final Map<ClickType, List<ClickBehaviour>> DEFAULT_BEHAVIOURS = new LinkedHashMap<ClickType, List<ClickBehaviour>>();

    static {

        final List<ClickBehaviour> behaviours = new LinkedList<ClickBehaviour>();
        DEFAULT_BEHAVIOURS.put(ClickType.LEFT, behaviours);

        behaviours.add(new ClickBehaviour() {
            @Override
            public boolean onClick(ClickContext context, Offer offer) {

                final ContentSlot contentSlot = (ContentSlot) context.getSlot();

                final Offer cursor = context.getHolder().getCursorOffer();
                final Offer contents = contentSlot.getContents();

                if (cursor == null && contents == null) { // NOTHING
                    ((Player) context.getEvent().getWhoClicked()).sendMessage(InventoryAction.NOTHING.name());
                    context.getEvent().setCancelled(true); // TODO not cancelling this is risky but it doesn't show the updating
                    return true;
                }

                return false;
            }
        });

        behaviours.add(new ClickBehaviour() {
            @Override
            public boolean onClick(ClickContext context, Offer offer) {

                final ContentSlot contentSlot = (ContentSlot) context.getSlot();

                final Offer cursor = context.getHolder().getCursorOffer();
                final Offer contents = contentSlot.getContents();

                if (cursor == null && contents != null) { // PICKUP_ALL
                    ((Player) context.getEvent().getWhoClicked()).sendMessage(InventoryAction.PICKUP_ALL.name());

                    context.getHolder().setCursorOffer(contents, true);
                    contentSlot.setContents(null);

                    context.getEvent().setCancelled(true); // TODO not cancelling this is risky but it doesn't show the updating
                    return true;
                }

                return false;

            }
        });

        behaviours.add(new ClickBehaviour() {
            @Override
            public boolean onClick(ClickContext context, Offer offer) {

                final ContentSlot contentSlot = (ContentSlot) context.getSlot();

                final Offer cursor = context.getHolder().getCursorOffer();
                final Offer contents = contentSlot.getContents();

                if (cursor != null && contents == null) { // PLACE_ALL
                    ((Player) context.getEvent().getWhoClicked()).sendMessage(InventoryAction.PLACE_ALL.name());

                    context.getHolder().setCursorOffer(null, true);
                    contentSlot.setContents(cursor);

                    context.getEvent().setCancelled(true); // TODO not cancelling this is risky but it doesn't show the updating
                    return true;
                }

                return false;

            }
        });

        behaviours.add(new ClickBehaviour() {
            @Override
            public boolean onClick(ClickContext context, Offer offer) {

                final ContentSlot contentSlot = (ContentSlot) context.getSlot();

                final Offer cursor = context.getHolder().getCursorOffer();
                final Offer contents = contentSlot.getContents();

                if (cursor != null && contents != null) {   // SWAP_WITH_CURSOR
                    ((Player) context.getEvent().getWhoClicked()).sendMessage(InventoryAction.SWAP_WITH_CURSOR.name());

                    context.getHolder().setCursorOffer(contents, true);
                    contentSlot.setContents(cursor);

                    context.getEvent().setCancelled(true); // TODO not cancelling this is risky but it doesn't show the updating
                    return true;
                }

                return false;

            }
        });

        DEFAULT_BEHAVIOURS.put(ClickType.RIGHT, new LinkedList<ClickBehaviour>(behaviours));

    }

    public ContentSlot(int slot, TransactionHolder holder) {
        super(slot, holder);
        addBehaviours(DEFAULT_BEHAVIOURS);
    }

    public abstract Offer getContents();

    public abstract void setContents(Offer contents);

    @Override
    public void onClick(InventoryClickEvent event) {

        final Offer contents = getContents();

        if (contents != null) {

            final ClickContext context = new ClickContext(holder, event, this);
            if (contents.onContentClick(context)) {
                ((Player) event.getWhoClicked()).sendMessage("Cursor: " + ((TransactionHolder) event.getInventory().getHolder()).getCursorOffer());
                return;
            }
        }

        super.onClick(event);

    }

}
