////////////////////////////////////////////////////////////////////////////////////////////////////
// PlotSquared - A plot manager and world generator for the Bukkit API                             /
// Copyright (c) 2014 IntellectualSites/IntellectualCrafters                                       /
//                                                                                                 /
// This program is free software; you can redistribute it and/or modify                            /
// it under the terms of the GNU General Public License as published by                            /
// the Free Software Foundation; either version 3 of the License, or                               /
// (at your option) any later version.                                                             /
//                                                                                                 /
// This program is distributed in the hope that it will be useful,                                 /
// but WITHOUT ANY WARRANTY; without even the implied warranty of                                  /
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                                   /
// GNU General Public License for more details.                                                    /
//                                                                                                 /
// You should have received a copy of the GNU General Public License                               /
// along with this program; if not, write to the Free Software Foundation,                         /
// Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA                               /
//                                                                                                 /
// You can contact us via: support@intellectualsites.com                                           /
////////////////////////////////////////////////////////////////////////////////////////////////////

package com.intellectualcrafters.plot.commands;

import com.intellectualcrafters.plot.PlotMain;
import org.bukkit.entity.Player;

/**
 * Created by Citymonstret on 2014-08-03.
 *
 * @author Citymonstret
 */
public class CommandPermission {

    /**
     * Permission Node
     */
    public final String permission;

    /**
     * @param permission Command Permission
     */
    public CommandPermission(final String permission) {
        this.permission = permission.toLowerCase();
    }

    /**
     * @param player Does the player have the permission?
     *
     * @return true of player has the required permission node
     */
    public boolean hasPermission(final Player player) {
        return PlotMain.hasPermission(player, this.permission) || PlotMain.hasPermission(player, "plots.admin");
    }
}
