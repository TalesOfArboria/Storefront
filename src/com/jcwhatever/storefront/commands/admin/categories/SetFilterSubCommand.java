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


package com.jcwhatever.storefront.commands.admin.categories;

import com.jcwhatever.nucleus.commands.AbstractCommand;
import com.jcwhatever.nucleus.commands.CommandInfo;
import com.jcwhatever.nucleus.commands.arguments.CommandArguments;
import com.jcwhatever.nucleus.commands.exceptions.InvalidArgumentException;
import com.jcwhatever.nucleus.utils.items.ItemFilterManager.FilterPolicy;
import com.jcwhatever.storefront.category.Category;
import com.jcwhatever.storefront.category.CategoryManager;
import com.jcwhatever.storefront.Storefront;

import org.bukkit.command.CommandSender;

@CommandInfo(
        parent = "categories",
        command = "setfilter",
        staticParams = { "categoryName", "whitelist|blacklist" },
        description = "Set category title.")

public class SetFilterSubCommand extends AbstractCommand {

    @Override
    public void execute (CommandSender sender, CommandArguments args) throws InvalidArgumentException {

        String categoryName = args.getName("categoryName");
        FilterPolicy filter = args.getEnum("whitelist|blacklist", FilterPolicy.class);

        CategoryManager catManager = Storefront.getCategoryManager();

        Category category = catManager.get(categoryName);
        if (category == null) {
            tellError(sender, "An item category with the name '{0}' was not found.", categoryName);
            return; // finished
        }

        category.getFilterManager().setPolicy(filter);

        tellSuccess(sender, "Category '{0}' filter mode changed to {1}.", category.getName(), filter.name());
    }
}