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


package com.jcwhatever.storefront.commands.admin;

import com.jcwhatever.nucleus.managed.commands.CommandInfo;
import com.jcwhatever.nucleus.managed.commands.arguments.ICommandArguments;
import com.jcwhatever.nucleus.managed.commands.exceptions.InvalidArgumentException;
import com.jcwhatever.nucleus.managed.commands.mixins.IExecutableCommand;
import com.jcwhatever.nucleus.managed.commands.utils.AbstractCommand;
import com.jcwhatever.storefront.Storefront;
import com.jcwhatever.storefront.stores.IStore;
import com.jcwhatever.storefront.stores.StoreManager;
import com.jcwhatever.storefront.stores.StoreType;

import org.bukkit.command.CommandSender;

@CommandInfo(
        command = "add",
        staticParams = { "storeName", "server|player_ownable" },
        description = "Add a store.")

public class AddCommand extends AbstractCommand implements IExecutableCommand {

    @Override
    public void execute (CommandSender sender, ICommandArguments args) throws InvalidArgumentException {

        String storeName = args.getName("storeName");
        StoreType type = args.getEnum("server|player_ownable", StoreType.class);

        StoreManager storeManager = Storefront.getStoreManager();

        IStore store = storeManager.get(storeName);
        if (store != null) {
            tellError(sender, "A store with the name '{0}' already exists.", store.getName());
            return; // finished
        }

        store = storeManager.add(storeName, type);

        if (store == null) {
            tellError(sender, "Failed to create store.");
            return; // finished
        }

        tellSuccess(sender, "{0} store '{1}' added.", type.name(), store.getName());
    }
}
