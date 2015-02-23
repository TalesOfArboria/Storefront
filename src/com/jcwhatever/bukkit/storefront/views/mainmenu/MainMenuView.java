/* This file is part of Storefront for Bukkit, licensed under the MIT License (MIT).
 *
 * Copyright (c) JCThePants (www.jcwhatever.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */


package com.jcwhatever.bukkit.storefront.views.mainmenu;

import com.jcwhatever.bukkit.storefront.StoreType;
import com.jcwhatever.bukkit.storefront.Storefront;
import com.jcwhatever.bukkit.storefront.data.ISaleItem;
import com.jcwhatever.bukkit.storefront.data.ISaleItemGetter;
import com.jcwhatever.bukkit.storefront.meta.SessionMetaKey;
import com.jcwhatever.bukkit.storefront.meta.ViewSessionTask;
import com.jcwhatever.bukkit.storefront.stores.IStore;
import com.jcwhatever.bukkit.storefront.utils.StoreStackMatcher;
import com.jcwhatever.bukkit.storefront.views.AbstractMenuView;
import com.jcwhatever.bukkit.storefront.views.BuyView;
import com.jcwhatever.bukkit.storefront.views.CategoryView;
import com.jcwhatever.bukkit.storefront.views.SellView;
import com.jcwhatever.bukkit.storefront.views.SellWantedView;
import com.jcwhatever.bukkit.storefront.views.WantedView;
import com.jcwhatever.nucleus.views.View;
import com.jcwhatever.nucleus.views.ViewCloseReason;
import com.jcwhatever.nucleus.views.ViewOpenReason;
import com.jcwhatever.nucleus.views.menu.MenuItem;
import com.jcwhatever.nucleus.views.menu.PaginatorView;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

public class MainMenuView extends AbstractMenuView {

    private IStore _store;
    private boolean _isStoreOwner;
    private boolean _canSell;

    public MainMenuView() {

        _store = getStore();

        // set session store
        getViewSession().setMeta(SessionMetaKey.STORE, _store);

        _isStoreOwner = _store.getStoreType() == StoreType.PLAYER_OWNABLE
                && getPlayer().getUniqueId().equals(_store.getOwnerId());

        _canSell = !(_store.getStoreType() == StoreType.PLAYER_OWNABLE &&
                !getPlayer().getUniqueId().equals(_store.getOwnerId()) &&
                _store.getWantedItems().getAll().size() == 0);
    }

    @Override
    protected List<MenuItem> createMenuItems() {

        List<MenuItem> menuItems = new ArrayList<>(2);

        menuItems.add(getBuyItem());

        if (_canSell) {
            menuItems.add(getSellItem());
        }

        return menuItems;
    }

    @Override
    protected void onItemSelect(MenuItem item) {

        if (!(item instanceof MainMenuItem))
            throw new AssertionError();

        MainMenuItem menuItem = (MainMenuItem)item;

        ViewSessionTask taskMode = menuItem.getTask();
        if (taskMode == null)
            throw new AssertionError();

        // set persistent task mode
        getViewSession().setMeta(SessionMetaKey.TASK_MODE, taskMode);

        View view = null;

        switch (taskMode) {
            case SERVER_BUY:
                // fall through
            case PLAYER_BUY:
                view = new BuyView(menuItem.getSaleItems());
                break;

            case SERVER_SELL:
                // fall through
            case OWNER_MANAGE_SELL:
                view = new SellView(menuItem.getSaleItems());
                break;

            case PLAYER_SELL:
                view = new SellWantedView(menuItem.getSaleItems());
                break;

            case OWNER_MANAGE_BUY:
                view = new WantedView(menuItem.getSaleItems());
                break;
        }

        if (menuItem.isCategorized()) {
            CategoryView.categoryNext(getViewSession(),
                    view,
                    menuItem.getSaleItems());
        }
        else {
            PaginatorView.paginateNext(getViewSession(),
                    view,
                    menuItem.getSaleItems(),
                    StoreStackMatcher.getDurability());
        }
    }

    @Override
    protected void onShow(ViewOpenReason reason) {
        // do nothing
    }

    @Override
    public String getTitle() {
        return getStore().getTitle();
    }

    @Override
    protected void onClose(ViewCloseReason reason) {
        // do nothing
    }

    @Override
    protected IStore getStore() {
        Block block = getViewSession().getSessionBlock();
        if (block == null) {
            throw new RuntimeException("A session block is required in order to get a store instance.");
        }

        IStore store = Storefront.getStoreManager().getStore(block);
        if (store == null)
            throw new IllegalStateException("Could not get store instance.");

        return store;
    }

    /**
     * Get a new Sell Menu Item
     */
    private MenuItem getSellItem() {

        MainMenuItemBuilder builder = (MainMenuItemBuilder)new MainMenuItemBuilder(Material.GOLD_BLOCK)
                .title("{BLUE}SELL")
                .description(_store.hasOwner()
                        ? "Click to sell items to the store."
                        : "Click to sell items from the store.");

        switch (_store.getStoreType()) {

            case SERVER:
                //builder.setViewFactory(Storefront.VIEW_SELL);
                builder
                        .task(ViewSessionTask.SERVER_SELL)
                        .categorized()
                        .saleItems(new ISaleItemGetter() {
                            @Override
                            public List<ISaleItem> getSaleItems() {
                                return _store.getSaleItems(getPlayer().getUniqueId());
                            }
                        });
                break;

            case PLAYER_OWNABLE:
                // owner sell
                if (_isStoreOwner) {
                    //builder.setViewFactory(Storefront.VIEW_SELL);
                    builder
                            .task(ViewSessionTask.OWNER_MANAGE_SELL)
                            .saleItems(new ISaleItemGetter() {
                                @Override
                                public List<ISaleItem> getSaleItems() {
                                    return _store.getSaleItems();
                                }
                            });

                }
                // player sell
                else {
                    //builder.setViewFactory(Storefront.VIEW_SELL_WANTED);
                    builder
                            .task(ViewSessionTask.PLAYER_SELL)
                            .categorized()
                            .saleItems(new ISaleItemGetter() {
                                @Override
                                public List<ISaleItem> getSaleItems() {
                                    return hasCategory()
                                            ? _store.getWantedItems().get(getCategory())
                                            : _store.getWantedItems().getAll();
                                }
                            });
                }
                break;
        }

        return builder.build(1);
    }

    /**
     * Get a new Buy menu item.
     */
    private MenuItem getBuyItem() {

        switch (_store.getStoreType()) {

            case SERVER:
                return getServerBuyItem();

            case PLAYER_OWNABLE:
                return getPlayerBuyItem();

            default:
                throw new AssertionError();
        }
    }

    private MenuItem getServerBuyItem() {

        MainMenuItemBuilder builder = (MainMenuItemBuilder)new MainMenuItemBuilder(Material.CHEST)
                //item.setViewFactory (Storefront.VIEW_BUY);
                .task(ViewSessionTask.SERVER_BUY)
                .categorized()
                .saleItems(new ISaleItemGetter() {
                    @Override
                    public List<ISaleItem> getSaleItems() {

                        List<ISaleItem> items = _store.getSaleItems();

                        // remove players items from the list
                        List<ISaleItem> results = new ArrayList<ISaleItem>(items.size());
                        for (ISaleItem item : items) {
                            if (!item.getSellerId().equals(getPlayer().getUniqueId())) {
                                results.add(item);
                            }
                        }

                        return results;
                    }
                })
                .title("BUY")
                .description("Click to buy from the store.")
                ;

        return builder.build(0);
    }

    private MenuItem getPlayerBuyItem() {
        MainMenuItemBuilder builder = new MainMenuItemBuilder(Material.CHEST);

        if (_isStoreOwner) {
            builder
                    //m.setViewFactory (Storefront.VIEW_WANTED);
                    .task(ViewSessionTask.OWNER_MANAGE_BUY)
                    .saleItems(new ISaleItemGetter() {

                        @Override
                        public List<ISaleItem> getSaleItems() {
                            return _store.getWantedItems().getAll();
                        }
                    })
                    .title(ChatColor.GREEN + "WANTED")
                    .description("Click to manage items you're willing to buy.");

        }
        else {
            builder
                    //.setViewFactory (Storefront.VIEW_BUY);
                    .task(ViewSessionTask.PLAYER_BUY)
                    .categorized()
                    .saleItems(new ISaleItemGetter() {
                        @Override
                        public List<ISaleItem> getSaleItems() {
                            return _store.getSaleItems();
                        }
                    })
                    .title("BUY")
                    .description("Click to buy from the store.");

        }
        return builder.build(0);
    }

}
