package me.josvth.trade.util;

import me.josvth.trade.transaction.offer.Offer;
import me.josvth.trade.transaction.offer.StackableOffer;
import org.bukkit.GameMode;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;

public class OfferUtils {

    public static InventoryAction getAction(InventoryClickEvent event, Offer slot, Offer cursor) {

        final boolean isStackableSlot = slot instanceof StackableOffer;
        final boolean isStackableCursor = cursor instanceof StackableOffer;

        final ClickType clickType = event.getClick();

        if (clickType.isShiftClick()) {

            // MOVE_TO_OTHER_INVENTORY
            if (slot != null) {
                return InventoryAction.MOVE_TO_OTHER_INVENTORY;
            }

        } else {

            // PICKUP_...
            if (slot != null && cursor == null) {

                // PICKUP_ALL
                if (clickType.isLeftClick()) {
                    return InventoryAction.PICKUP_ALL;
                }

                // PICKUP_HALF
                if (clickType.isRightClick()) {
                    return InventoryAction.PICKUP_HALF;
                }

                // TODO Pickup some and Pickup half for over sized stacks

            }

            // PLACE_...
            if (cursor != null) {

                // On empty slot
                if (slot == null) {

                    // PLACE_ALL
                    if (clickType.isLeftClick()) {
                        return InventoryAction.PLACE_ONE;
                    }

                    // PLACE_ONE
                    if (clickType.isRightClick()) {
                        return InventoryAction.PLACE_ONE;
                    }

                }

                // On non empty slots
                if (slot != null && isStackableSlot && isStackableCursor /* && slot.isSimilar(cursor)*/ && !((StackableOffer) slot).isFull() ) {

                    final int available = ((StackableOffer) slot).getMaxAmount() - ((StackableOffer) slot).getAmount();

                    if (clickType.isLeftClick()) {

                        // PLACE_ONE
                        if (available == 1) {
                            return InventoryAction.PLACE_ONE;
                        }

                        // PLACE_ALL
                        if (available == ((StackableOffer) cursor).getAmount()) {
                            return InventoryAction.PLACE_ALL;
                        }

                        // PLACE_SOME
                        return InventoryAction.PLACE_SOME;

                    }

                    if (clickType.isRightClick()) {

                        // NOTHING
                        if (available == 0) {
                            return InventoryAction.NOTHING;
                        }

                        // PLACE_ONE
                        return InventoryAction.PLACE_ONE;

                    }

                }
            }

            // SWAP_WITH_CURSOR
            if (slot != null && cursor != null && ( (isStackableCursor && ((StackableOffer) slot).isFull()) || !isStackableSlot )) {
                return InventoryAction.SWAP_WITH_CURSOR;
            }

            // DROP_..._CURSOR
            if (slot == null && cursor != null && event.getRawSlot() == -999) {

                // DROP_ONE_CURSOR
                if (isStackableCursor && clickType.isRightClick()) {
                    return InventoryAction.DROP_ONE_CURSOR;
                }

                // DROP_ALL_CURSOR
                return InventoryAction.DROP_ALL_CURSOR;

            }

            // DROP_..._SLOT
            if (slot != null && cursor == null && clickType.isKeyboardClick() && event.getHotbarButton() == -1) {   // IMPORTANT different behaviour from Bukkit! Bukkit does not check cursor which gives an invalid action cause

                // DROP_ONE_SLOT
                if (clickType == ClickType.DROP) {
                    return InventoryAction.DROP_ONE_SLOT;
                }

                // DROP_ALL_SLOT
                if (clickType == ClickType.CONTROL_DROP) {
                    return InventoryAction.DROP_ALL_SLOT;
                }

            }

            // HOTBAR_...
            if (slot != null && cursor == null && clickType.isKeyboardClick() && event.getHotbarButton() != -1) {
                // THIS IS DISABLED WITH ALL OFFERS EXEPT ITEMOFFERS
                // TODO MAKE THIS WORK FOR ITEMOFFERS
                return InventoryAction.UNKNOWN;
            }

            // CLONE_STACK
            if (slot != null && clickType == ClickType.MIDDLE && event.getWhoClicked().getGameMode() == GameMode.CREATIVE) {

                // NOTHING
                if (cursor != null) {
                    return InventoryAction.NOTHING;
                }

                return InventoryAction.CLONE_STACK;

            }

            // COLLECT_TO_CURSOR
            // TODO MAKE THIS WORK

        }

        throw new IllegalStateException("UNHANDLED ACTION: " + event.getAction().name());

        // TODO Figure out the following actions:
        // PICKUP_SOME
        // PICKUP_HALF
        // PICKUP_ONE
        // HOTBAR_MOVE_AND_READD
        // HOTBAR_SWAP
        // COLLECT_TO_CURSOR

    }
}
