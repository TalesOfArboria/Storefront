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


package com.jcwhatever.storefront.commands.admin.regions;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.commands.AbstractCommand;
import com.jcwhatever.nucleus.commands.CommandInfo;
import com.jcwhatever.nucleus.commands.arguments.CommandArguments;
import com.jcwhatever.nucleus.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.regions.IRegion;
import com.jcwhatever.nucleus.utils.text.TextUtils;
import com.jcwhatever.storefront.stores.StoreManager;
import com.jcwhatever.storefront.Storefront;
import com.jcwhatever.storefront.regions.StoreRegion;
import com.jcwhatever.storefront.stores.IStore;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;


@CommandInfo(
        parent = "regions",
        command = "setext",
        staticParams = { "storeName", "regionName=" },
        description = "Set the specified store region to the region you are standing in. If there is more than 1, specify with [regionName].")

public class SetExtSubCommand extends AbstractCommand {

    @Override
    public void execute (CommandSender sender, CommandArguments args) throws CommandException {

        CommandException.checkNotConsole(this, sender);
        
        String storeName = args.getName("storeName");
        String regionName = args.getString("regionName");
        
        Player p = (Player)sender;
        
        StoreManager storeManager = Storefront.getStoreManager();

        IStore store = storeManager.get(storeName);
        if (store == null) {
            tellError(sender, "A store with the name '{0}' was not found.", storeName);
            return; // finished
        }
        
        
        List<IRegion> regions = Nucleus.getRegionManager().getRegions(p.getLocation());
        
        if (regions.isEmpty()) {
            tellError(sender, "No Nucleus region was found where you are standing.");
            return; // finished
        }
        
        if (regions.size() > 1 && regionName.isEmpty()) {
            tellError(sender, "More than one region was found. Please specify with one of the following region names:");
            
            List<String> regionNames = new ArrayList<String>(regions.size());
            for (IRegion region : regions) {
                regionNames.add(region.getName() + '(' + region.getPlugin().getName() + ')');
            }

            tell(sender, TextUtils.concat(regionNames, ", "));
            return; // finished
        }
        
        
        IRegion extRegion = null;
        
        for (IRegion region : regions) {
            
            if ((regions.size() == 1 && regionName.isEmpty()) ||
                    region.getName().equalsIgnoreCase(regionName)) {
                
            
                extRegion = region;
                break;
            }
        }
        
        if (extRegion == null) {
            tellError(sender, regionName.isEmpty()
                    ? "Could not find a region."
                    : "Could not find a region where you are standing named '{0}'.", regionName);
            
            return; // finished
        }
        
        IStore extStore = extRegion.getMeta().get(StoreRegion.REGION_STORE);
        if (extStore != null) {
            tellError(sender, "Region '{0}' is already assigned to store named '{1}'.", extRegion.getName(), extStore.getName());
            return; // finished
        }

        store.setExternalRegion(extRegion);
        
        tellSuccess(sender, "Store '{0}' region set to region named '{1}' from plugin named '{2}'.", store.getName(), extRegion.getName(), extRegion.getPlugin().getName());
    }
}