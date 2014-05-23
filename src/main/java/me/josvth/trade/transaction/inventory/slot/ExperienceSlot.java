package me.josvth.trade.transaction.inventory.slot;

import me.josvth.trade.Trade;
import me.josvth.trade.tasks.ExperienceSlotUpdateTask;
import me.josvth.trade.transaction.action.trader.offer.ChangeExperienceAction;
import me.josvth.trade.transaction.inventory.TransactionHolder;
import me.josvth.trade.transaction.inventory.interact.ClickBehaviour;
import me.josvth.trade.transaction.inventory.interact.ClickContext;
import me.josvth.trade.transaction.inventory.offer.ExperienceOffer;
import me.josvth.trade.transaction.inventory.offer.Offer;
import me.josvth.trade.transaction.inventory.offer.OfferList;
import me.josvth.trade.util.ItemStackUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ExperienceSlot extends Slot {

    private static final Map<ClickType, List<ClickBehaviour>> DEFAULT_BEHAVIOURS = new LinkedHashMap<ClickType, List<ClickBehaviour>>();

    static {

        // LEFT
        final LinkedList<ClickBehaviour> leftBehaviours = new LinkedList<ClickBehaviour>();
        DEFAULT_BEHAVIOURS.put(ClickType.LEFT, leftBehaviours);

        leftBehaviours.add(new ClickBehaviour() {
            @Override
            public boolean onClick(ClickContext context, Offer offer) {

                final ExperienceSlot experienceSlot = (ExperienceSlot) context.getSlot();
                new ChangeExperienceAction(context.getHolder().getTrader(), context.getHolder().getOfferList(), experienceSlot.getSmallModifier()).execute();

                context.getEvent().setCancelled(true);

                return true;
            }
        });


        // SHIFT_LEFT
        final LinkedList<ClickBehaviour> shiftLeftBehaviours = new LinkedList<ClickBehaviour>();
        DEFAULT_BEHAVIOURS.put(ClickType.SHIFT_LEFT, shiftLeftBehaviours);

        shiftLeftBehaviours.add(new ClickBehaviour() {
            @Override
            public boolean onClick(ClickContext context, Offer offer) {

                final ExperienceSlot experienceSlot = (ExperienceSlot) context.getSlot();
                new ChangeExperienceAction(context.getHolder().getTrader(), context.getHolder().getOfferList(), experienceSlot.getLargeModifier()).execute();

                context.getEvent().setCancelled(true);

                return true;

            }
        });

        // RIGHT
        final LinkedList<ClickBehaviour> rightBehaviours = new LinkedList<ClickBehaviour>();
        DEFAULT_BEHAVIOURS.put(ClickType.RIGHT, rightBehaviours);

        rightBehaviours.add(new ClickBehaviour() {
            @Override
            public boolean onClick(ClickContext context, Offer offer) {

                final ExperienceSlot experienceSlot = (ExperienceSlot) context.getSlot();
                new ChangeExperienceAction(context.getHolder().getTrader(), context.getHolder().getOfferList(), -1 * experienceSlot.getSmallModifier()).execute();

                context.getEvent().setCancelled(true);

                return true;

            }
        });

        // SHIFT_RIGHT
        final LinkedList<ClickBehaviour> shiftRightBehaviours = new LinkedList<ClickBehaviour>();
        DEFAULT_BEHAVIOURS.put(ClickType.SHIFT_RIGHT, shiftRightBehaviours);

        shiftRightBehaviours.add(new ClickBehaviour() {
            @Override
            public boolean onClick(ClickContext context, Offer offer) {

                final ExperienceSlot experienceSlot = (ExperienceSlot) context.getSlot();
                new ChangeExperienceAction(context.getHolder().getTrader(), context.getHolder().getOfferList(), -1 * experienceSlot.getLargeModifier()).execute();

                context.getEvent().setCancelled(true);

                return true;

            }
        });


    }

    private ItemStack experienceItem;

    private int smallModifier = 1;
    private int largeModifier = 5;

    public ExperienceSlot(int slot, TransactionHolder holder) {
        super(slot, holder);
        addClickBehaviours(DEFAULT_BEHAVIOURS);
    }

    public static double getExperience(OfferList list) {
        double experience = 0;
        for (ExperienceOffer tradeable : list.getOfClass(ExperienceOffer.class).values()) {
            experience += tradeable.getAmount();
        }
        return experience;
    }

    public static void updateExperienceSlots(TransactionHolder holder, boolean nextTick, double experience) {

        final Set<ExperienceSlot> slots = holder.getSlotsOfType(ExperienceSlot.class);

        if (!nextTick) {
            for (ExperienceSlot slot : slots) {
                slot.update(experience);
            }
        } else if (!slots.isEmpty()) {
            Bukkit.getScheduler().runTask(Trade.getInstance(), new ExperienceSlotUpdateTask(slots, experience));
        }

    }

    public static ExperienceSlot deserialize(int slotID, TransactionHolder holder, SlotDescription description) {
        final ExperienceSlot slot = new ExperienceSlot(slotID, holder);
        slot.setExperienceItem(ItemStackUtils.fromSection(description.getConfiguration().getConfigurationSection("experience-item"), Trade.getInstance().getMessageManager()));
        slot.setSmallModifier(description.getConfiguration().getInt("small-modifier", 1));
        slot.setLargeModifier(description.getConfiguration().getInt("large-modifier", 5));
        return slot;
    }

    public ItemStack getExperienceItem() {
        return experienceItem;
    }

    public void setExperienceItem(ItemStack experienceItem) {
        this.experienceItem = experienceItem;
    }

    public int getSmallModifier() {
        return smallModifier;
    }

    public void setSmallModifier(int smallModifier) {
        this.smallModifier = smallModifier;
    }

    public int getLargeModifier() {
        return largeModifier;
    }

    public void setLargeModifier(int largeModifier) {
        this.largeModifier = largeModifier;
    }

    @Override
    public void update() {
        update(getExperience(holder.getOfferList()));
    }

    public void update(double experience) {
        setGUIItem(ItemStackUtils.argument(experienceItem.clone(), "%experience%", String.valueOf(experience), "%small%", String.valueOf(smallModifier), "%large%", String.valueOf(largeModifier)));
    }

}
