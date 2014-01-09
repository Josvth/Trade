package me.josvth.trade.transaction.offer;


public abstract class StackableOffer extends Offer {

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

    public abstract StackableOffer clone();

}
