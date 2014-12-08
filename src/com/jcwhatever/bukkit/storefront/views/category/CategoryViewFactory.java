package com.jcwhatever.bukkit.storefront.views.category;

import com.jcwhatever.bukkit.generic.views.IView;
import com.jcwhatever.bukkit.generic.views.ViewFactory;
import com.jcwhatever.bukkit.generic.views.ViewSession;
import com.jcwhatever.bukkit.generic.views.data.ViewArguments;
import com.jcwhatever.bukkit.generic.views.data.ViewOpenReason;
import com.jcwhatever.bukkit.storefront.Storefront;

import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;

/*
 * 
 */
public class CategoryViewFactory extends ViewFactory<CategoryView> {

    public CategoryViewFactory(String name) {
        super(null, name, CategoryView.class);
    }

    @Override
    public Plugin getPlugin() {
        return Storefront.getInstance();
    }

    @Override
    protected boolean onOpen(ViewOpenReason reason, CategoryView view) {
        view.show(reason);
        return true;
    }

    @Override
    protected void onDispose() {
        // do nothing
    }

    @Override
    public IView create(@Nullable String title, ViewSession session, ViewArguments arguments) {
        return new CategoryView(session, this, arguments);
    }

}