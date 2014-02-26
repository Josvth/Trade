package me.josvth.trade.transaction.offer.behaviour;

import org.apache.commons.lang.Validate;
import org.bukkit.event.inventory.ClickType;

/**
 * Created by Jos on 26-2-14.
 */
public class ClickTrigger {

    final ClickCategory category;
    final ClickType type;

    public ClickTrigger(ClickCategory category, ClickType type) {
        Validate.notNull(category, "ClickCategory can't be null.");
        Validate.notNull(type, "ClickType can't be null.");
        this.category = category;
        this.type = type;
    }

    public ClickCategory getCategory() {
        return category;
    }

    public ClickType getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClickTrigger that = (ClickTrigger) o;

        if (category != that.category) return false;
        if (type != that.type) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = category.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }
}
