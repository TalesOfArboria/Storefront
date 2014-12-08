package com.jcwhatever.bukkit.storefront.views.quantity;

import com.jcwhatever.bukkit.generic.mixins.ICancellable;
import com.jcwhatever.bukkit.generic.views.data.ViewArguments;
import com.jcwhatever.bukkit.generic.views.data.ViewResultKey;
import com.jcwhatever.bukkit.generic.views.data.ViewResults;

import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

/*
 * 
 */
public class QuantityViewResult extends ViewResults implements ICancellable {

    public static final ViewResultKey<ItemStack>
            ITEM_STACK = new ViewResultKey<>(ItemStack.class);

    public static final ViewResultKey<Integer>
            QUANTITY = new ViewResultKey<>(Integer.class);

    public static final ViewResultKey<Boolean>
            IS_CANCELLED = new ViewResultKey<>(Boolean.class);

    public QuantityViewResult(ItemStack itemStack, int qty) {
        super(new ViewResult(ITEM_STACK, itemStack),
              new ViewResult(QUANTITY, qty));
    }

    public QuantityViewResult(ViewArguments merge, ItemStack itemStack, int qty) {
        super(merge, new ViewResult(ITEM_STACK, itemStack),
                     new ViewResult(QUANTITY, qty));
    }

    @Nullable
    public ItemStack getItemStack () {

        return get(ITEM_STACK);
    }

    public int getQty () {

        Integer integer = get(QUANTITY);
        if (integer == null)
            return 1;

        return integer;
    }

    void setQty (int qty) {

        set(QUANTITY, qty);
    }

    @Override
    public boolean isCancelled() {
        Boolean isCancelled = get(IS_CANCELLED);
        if (isCancelled == null)
            return false;

        return isCancelled;
    }

    @Override
    public void setCancelled(boolean isCancelled) {
        set(IS_CANCELLED, isCancelled);
    }
}