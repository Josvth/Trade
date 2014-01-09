package me.josvth.trade.transaction.offer;

import me.josvth.trade.transaction.Trader;
import me.josvth.trade.transaction.inventory.TransactionHolder;
import me.josvth.trade.transaction.inventory.slot.ExperienceSlot;
import me.josvth.trade.transaction.inventory.slot.MirrorSlot;
import me.josvth.trade.transaction.inventory.slot.TradeSlot;
import me.josvth.trade.util.ExperienceManager;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class OfferList {

	private final Trader trader;

	private final Offer[] offers;

	public OfferList(Trader trader, int size) {
		this.trader = trader;
		this.offers = new Offer[size];
	}

    public Trader getTrader() {
        return trader;
    }

	public TransactionHolder getHolder() {
		return getTrader().getHolder();
	}

	public Offer get(int tradeSlot) {
		return offers[tradeSlot];
	}

	public void set(int slot, Offer offer) {
		offers[slot] = offer;
	}

	@Override
	public String toString() {

		final StringBuilder builder = new StringBuilder("Contents:");

		for (Offer offer : offers) {
			builder.append((offer == null)? "\n null" : "\n " + offer.toString());
		}

		return builder.toString();

	}

	public <T extends Offer> TreeMap<Integer, T> getOfClass(Class<T> clazz) {

		final TreeMap<Integer, T> found = new TreeMap<Integer, T>();

		for (int i = 0; i < offers.length; i++) {
			if (clazz.isInstance(offers[i])) {
				found.put(i, (T) offers[i]);
			}
		}

		return found;

	}

    public TreeMap<Integer, Offer> getOfType(String type) {

        final TreeMap<Integer, Offer> found = new TreeMap<Integer, Offer>();

        for (int i = 0; i < offers.length; i++) {
            if (type.equalsIgnoreCase(offers[i].getType())) {
                found.put(i, offers[i]);
            }
        }

        return found;

    }

	public int getFirstEmpty() {
		for (int i = 0; i < offers.length; i++) {
			if (offers[i] == null) {
				return i;
			}
		}
		return -1;
	}

	public void grant(Trader trader) {
		for (Offer offer : offers) {
			if (offer != null) {
				offer.grant(trader);
			}
		}
	}

    public boolean addOffer(Offer offer) {

        boolean complete = false;   // true if the whole amount could be added

        if (offer instanceof StackableOffer) {

            StackableOffer stackableOffer = (StackableOffer) offer;

            System.out.print(stackableOffer.getClass());

            // We keep a list of changed offer indexes so we can update the slots later
            final LinkedList<Integer> changedIndexes = new LinkedList<Integer>();

            // First we try and fill up existing offers
            final Iterator<? extends Map.Entry<Integer,? extends StackableOffer>> iterator = getOfClass(stackableOffer.getClass()).entrySet().iterator();

            int currentAmount = 0;  // At the moment only used by experience items

            while (iterator.hasNext()) {

                final Map.Entry<Integer, ? extends StackableOffer> entry = iterator.next();

                if (stackableOffer.getAmount() > 0) {

                    final int overflow = entry.getValue().add(stackableOffer.getAmount());

                    // If we have added something change the remaining levels and add this slot to the changed indexes
                    if (overflow < stackableOffer.getAmount()) {
                        changedIndexes.add(entry.getKey());
                        stackableOffer.setAmount(overflow);
                    }

                }

                currentAmount += entry.getValue().getAmount();  // We count the total amount currently offered

            }

            // Next put the remaining levels in empty offer slots
            if (stackableOffer.getAmount() > 0) {

                int firstEmpty = getFirstEmpty();

                while (stackableOffer.getAmount() > 0 && firstEmpty != -1) {

                    final int overflow = stackableOffer.getAmount() - stackableOffer.getMaxAmount();

                    if (overflow <= 0) {

                        set(firstEmpty, stackableOffer.clone());
                        currentAmount += stackableOffer.getAmount();

                        stackableOffer.setAmount(0); // Set the amount to 0 to make the user know there's nothing left
                        complete = true;

                        firstEmpty = -1; // End the loop

                    } else {

                        // We fill the slot up with a full stack of the offer
                        final StackableOffer fullStack = stackableOffer.clone();
                        fullStack.setAmount(stackableOffer.getMaxAmount());

                        set(firstEmpty, fullStack);
                        currentAmount += fullStack.getMaxAmount();

                        stackableOffer.setAmount(-1 * overflow);

                        firstEmpty = getFirstEmpty();

                    }

                    changedIndexes.add(firstEmpty);

                }

            }

            // If we changed anything we update the holder and mirror
            if (!changedIndexes.isEmpty()) {

                // We place our changed indexes into an array
                final int[] indexesArray = new int[changedIndexes.size()];

                int i = 0;
                for (int index : changedIndexes) {
                    indexesArray[i] = index;
                    i++;
                }

                TradeSlot.updateTradeSlots(getHolder(), true, indexesArray);
                MirrorSlot.updateMirrors(getHolder().getOtherHolder(), true, indexesArray);

                if (stackableOffer instanceof ExperienceOffer) {
                    ExperienceSlot.updateExperienceSlots(getHolder(), true, currentAmount);
                }

            }

        } else {

            final int empty = getFirstEmpty();

            if (empty != -1) {
                set(empty, offer);
                TradeSlot.updateTradeSlots(getHolder(), true, empty);
                MirrorSlot.updateMirrors(getHolder().getOtherHolder(), true, empty);
                complete = true;
            }

        }

        return complete;

    }

    public boolean removeOffer(Offer offer) {

        boolean complete = false;   // true if the whole amount could be removed

        if (offer instanceof StackableOffer) {

            final StackableOffer stackableOffer = (StackableOffer) offer;

            // We keep a list of changed offer indexes so we can update the slots later
            final LinkedList<Integer> changedIndexes = new LinkedList<Integer>();

            // TODO lowest amount first
            // First we try and remove from existing offers
            final Iterator<? extends Map.Entry<Integer, ? extends StackableOffer>> iterator = getOfClass(stackableOffer.getClass()).entrySet().iterator();

            int currentAmount = 0;  // At the moment only used by experience offers

            while (iterator.hasNext()) {

                final Map.Entry<Integer, ? extends StackableOffer> entry = iterator.next();

                if (stackableOffer.getAmount() > 0)  {

                    final int overflow = entry.getValue().remove(stackableOffer.getAmount());

                    if (overflow < stackableOffer.getAmount()) {    // We only changed something if the overflow is smaller then the amount

                        changedIndexes.add(entry.getKey());
                        stackableOffer.setAmount(overflow);

                        if (entry.getValue().getAmount() == 0) {    // If the amount of the changed offer is 0 we remove it
                            set(entry.getKey(), null);
                        }

                    }

                }

                currentAmount += entry.getValue().getAmount();

            }

            // We set our boolean if we removed everything
            complete = stackableOffer.getAmount() == 0;

            // If we changed anything we update the holder and mirror
            if (!changedIndexes.isEmpty()) {

                // We place our changed indexes into an array
                final int[] indexesArray = new int[changedIndexes.size()];

                int i = 0;
                for (int index : changedIndexes) {
                    indexesArray[i] = index;
                }

                TradeSlot.updateTradeSlots(getHolder(), true, indexesArray);
                MirrorSlot.updateMirrors(getHolder().getOtherHolder(), true, indexesArray);

                if (stackableOffer instanceof ExperienceOffer) {
                    ExperienceSlot.updateExperienceSlots(getHolder(), true, currentAmount);
                }

            }


        } else {

            final TreeMap<Integer, Offer> current = getOfType(offer.getType());

            if (!current.isEmpty()) {
                set(current.lastKey(), null);
                TradeSlot.updateTradeSlots(getHolder(), true, current.lastKey());
                MirrorSlot.updateMirrors(getHolder().getOtherHolder(), true, current.lastKey());
                complete = true;
            }

        }

        return complete;

    }

    public ItemOffer createItemOffer(ItemStack itemStack) {
        final ItemOffer offer = getTrader().getLayout().getOfferDescription(ItemOffer.class).createOffer();
        offer.setItem(itemStack);
        return offer;
    }


}
