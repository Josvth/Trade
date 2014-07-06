package me.josvth.trade.transaction.inventory.offer;

import me.josvth.trade.Trade;
import me.josvth.trade.transaction.Trader;
import me.josvth.trade.transaction.inventory.TransactionHolder;
import me.josvth.trade.transaction.inventory.offer.description.ItemOfferDescription;
import me.josvth.trade.util.ItemStackUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

public class ItemOffer extends Offer {

    public static final String TYPE_NAME = "item";

//    static {
//
//        final LinkedList<OfferClickBehaviour> cursorLeftBehaviours = new LinkedList<OfferClickBehaviour>();
//
//        // PLACE_ALL
//        cursorLeftBehaviours.add(new OfferClickBehaviour() {
//            @Override
//            public boolean onClick(InventoryClickEvent event, Slot slot, Offer offer) {
//                if (slot == null) {
//                    final TransactionHolder holder = (TransactionHolder) event.getInventory().getHolder();
//                    if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) {
//                        event.setCurrentItem(((ItemOffer) offer).getItem().clone());
//
//                        holder.setCursorOffer(null, true);
//
//                        event.setCancelled(true);
//                        return true;
//                    }
//                }
//                return false;
//            }
//        });
//
//        // SWAP_WITH_CURSOR
//        final OfferClickBehaviour swapWithCursor = new OfferClickBehaviour() {
//            @Override
//            public boolean onClick(InventoryClickEvent event, Slot slot, Offer offer) {
//                if (slot == null) {
//                    final TransactionHolder holder = (TransactionHolder) event.getInventory().getHolder();
//                    if (event.getCurrentItem() != null) {
//                        holder.setCursorOffer(new ItemOffer(event.getCurrentItem().clone()), true);
//
//                        event.setCurrentItem(((ItemOffer) offer).getItem().clone());
//
//                        event.setCancelled(true);
//                        return true;
//                    }
//                }
//                return false;
//            }
//        };
//
//        cursorLeftBehaviours.add(swapWithCursor);
//
//        DEFAULT_BEHAVIOURS.put(new ClickTrigger(ClickCategory.CURSOR, ClickType.LEFT), cursorLeftBehaviours);
//
//        final LinkedList<OfferClickBehaviour> cursorRightBehaviours = new LinkedList<OfferClickBehaviour>();
//
//        // GRANT_ONE
//        cursorRightBehaviours.add(new OfferClickBehaviour() {
//            @Override
//            public boolean onClick(InventoryClickEvent event, Slot slot, Offer offer) {
//                if (slot == null) {
//                    final ItemStack currentItem = event.getCurrentItem();
//                    if (currentItem == null) {
//                        final TransactionHolder holder = (TransactionHolder) event.getInventory().getHolder();
//
//                        final ItemOffer itemOffer = (ItemOffer) offer;
//                        itemOffer.remove(1);
//                        holder.updateCursorOffer();
//
//                        final ItemStack item = itemOffer.getItem().clone();
//                        item.setAmount(1);
//                        event.setCurrentItem(item);
//
//                        event.setCancelled(true);
//                        return true;
//                    }
//                }
//                return false;
//            }
//        });
//
//        cursorRightBehaviours.add(swapWithCursor);
//
//        DEFAULT_BEHAVIOURS.put(new ClickTrigger(ClickCategory.CURSOR, ClickType.RIGHT), cursorRightBehaviours);
//
//    }


    // 6-7-2014 I decided to not use an ItemStack field anymore because ItemStacks have some hard programmed behaviour
    // For example: ItemStack.setAmount(0) will always make the ItemStack of type AIR
    //private ItemStack item = null;
    private Material type;
    private int amount;
    private MaterialData data;
    private short durability;
    private ItemMeta meta;

    public ItemOffer() {
        this(null);
    }

    public ItemOffer(ItemStack item) {
        this(item.getType(), item.getAmount(), item.getDurability(), item.getData(), item.getItemMeta());
    }

    public ItemOffer(Material type, int amount, short durability, MaterialData data, ItemMeta meta) {
        this.type = type;
        this.amount = amount;
        this.data = data;
        this.durability = durability;
        this.meta = meta;
        setAllowedInInventory(true);
        setCanStayInInventory(true);
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
    public double add(double amount) {
        final double integerAmount = Math.floor(amount);
        return (amount - integerAmount) + super.add(integerAmount);
    }

    @Override
    public double remove(double amount) {
        final double integerAmount = Math.floor(amount);
        return (amount - integerAmount) + super.remove(integerAmount);
    }

    @Override
    public double getAmount() {
        return getIntAmount();
    }

    public int getIntAmount() {
        return amount;
    }

    @Override
    public void setAmount(double amount) {
        if (amount > Integer.MAX_VALUE) {
            setIntAmount(Integer.MAX_VALUE);
        } else {
            setIntAmount((int) amount);
        }
    }

    public void setIntAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public int getMaxAmount() {
        return type.getMaxStackSize();
    }

    @Override
    public boolean isFull() {
        return amount >= type.getMaxStackSize();
    }

    @Override
    public void grant(final Trader trader, boolean nextTick) {
        if (nextTick) {
            Bukkit.getScheduler().runTask(Trade.getInstance(), new Runnable() {         //TODO Make this nicer
                @Override
                public void run() {
                    trader.getPlayer().getInventory().addItem(createItemStack());
                }
            });
        } else {
            trader.getPlayer().getInventory().addItem(createItemStack());
        }
    }

    @Override
    public void grant(final Trader trader, boolean nextTick, double amount) {

        final ItemStack item = createItemStack();

        if (amount > Integer.MAX_VALUE) {
            item.setAmount((Integer.MAX_VALUE));
        } else {
            item.setAmount(((int) amount));
        }

        if (nextTick) {
            Bukkit.getScheduler().runTask(Trade.getInstance(), new Runnable() {         //TODO Make this nicer
                @Override
                public void run() {
                    trader.getPlayer().getInventory().addItem(item);
                }
            });
        } else {
            trader.getPlayer().getInventory().addItem(item);
        }
    }

    public ItemStack createItemStack() {
        return ItemStackUtils.create(type, amount, durability, data, meta);
    }

    public void setItem(ItemStack item) {
        type = item.getType();
        amount = item.getAmount();
        durability = item.getDurability();
        data = item.getData();
        meta = item.getItemMeta();
    }

    public ItemOffer clone() {
        return new ItemOffer(type, amount, durability, data, meta);
    }

    @Override
    public boolean isSimilar(Offer offer) {
        return offer instanceof ItemOffer && (createItemStack() != null) && createItemStack().isSimilar(((ItemOffer) offer).createItemStack());
    }

}
