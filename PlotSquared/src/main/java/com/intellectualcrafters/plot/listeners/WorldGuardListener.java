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

package com.intellectualcrafters.plot.listeners;

import com.intellectualcrafters.plot.PlotMain;
import com.intellectualcrafters.plot.events.PlayerClaimPlotEvent;
import com.intellectualcrafters.plot.events.PlotDeleteEvent;
import com.intellectualcrafters.plot.events.PlotMergeEvent;
import com.intellectualcrafters.plot.events.PlotUnlinkEvent;
import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.object.PlotId;
import com.intellectualcrafters.plot.util.PlotHelper;
import com.intellectualcrafters.plot.util.UUIDHandler;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.permissions.PermissionAttachment;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

/**
 * Created 2014-09-24 for PlotSquared
 *
 * @author Citymonstret
 * @author Empire92
 */
public class WorldGuardListener implements Listener {
    public final ArrayList<String> str_flags;
    public final ArrayList<Flag<?>> flags;

    public WorldGuardListener(final PlotMain plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.str_flags = new ArrayList<>();
        this.flags = new ArrayList<>();
        for (final Flag<?> flag : DefaultFlag.getFlags()) {
            this.str_flags.add(flag.getName());
            this.flags.add(flag);
        }
    }

    public void changeOwner(final Player requester, final UUID owner, final World world, final Plot plot) {
        // boolean op = requester.isOp();
        // requester.setOp(true);

        // 10 ticks should be enough
        final PermissionAttachment add = requester.addAttachment(PlotMain.getMain(), 10);
        add.setPermission("worldguard.region.addowner.own.*", true);

        final PermissionAttachment remove = requester.addAttachment(PlotMain.getMain(), 10);
        remove.setPermission("worldguard.region.removeowner.own.*", true);

        try {
            final RegionManager manager = PlotMain.worldGuard.getRegionManager(world);
            manager.getRegion(plot.id.x + "-" + plot.id.y);
            requester.performCommand("region setowner " + (plot.id.x + "-" + plot.id.y) + " " + UUIDHandler.getName(owner));
            requester.performCommand("region removeowner " + (plot.id.x + "-" + plot.id.y) + " " + UUIDHandler.getName(plot.getOwner()));
        } catch (final Exception e) {
            // requester.setOp(op);

        } finally {
            add.remove();
            remove.remove();
        }
    }

    public void removeFlag(final Player requester, final World world, final Plot plot, final String key) {
        final boolean op = requester.isOp();
        requester.setOp(true);
        try {
            final RegionManager manager = PlotMain.worldGuard.getRegionManager(world);
            manager.getRegion(plot.id.x + "-" + plot.id.y);
            for (final Flag<?> flag : this.flags) {
                if (flag.getName().equalsIgnoreCase(key)) {
                    requester.performCommand("region flag " + (plot.id.x + "-" + plot.id.y) + " " + key);
                }
            }
        } catch (final Exception e) {
            requester.setOp(op);
        } finally {
            requester.setOp(op);
        }
    }

    public void addFlag(final Player requester, final World world, final Plot plot, final String key, final String value) {
        final boolean op = requester.isOp();
        requester.setOp(true);
        try {
            final RegionManager manager = PlotMain.worldGuard.getRegionManager(world);
            manager.getRegion(plot.id.x + "-" + plot.id.y);
            for (final Flag<?> flag : this.flags) {
                if (flag.getName().equalsIgnoreCase(key)) {
                    requester.performCommand("region flag " + (plot.id.x + "-" + plot.id.y) + " " + key + " " + value);
                }
            }
        } catch (final Exception e) {
            requester.setOp(op);
        } finally {
            requester.setOp(op);
        }
    }

    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMerge(final PlotMergeEvent event) {
        final Plot main = event.getPlot();
        final ArrayList<PlotId> plots = event.getPlots();
        final World world = event.getWorld();
        final RegionManager manager = PlotMain.worldGuard.getRegionManager(world);
        for (final PlotId plot : plots) {
            if (!plot.equals(main.getId())) {
                manager.removeRegion(plot.x + "-" + plot.y);
            }
        }
        final ProtectedRegion region = manager.getRegion(main.id.x + "-" + main.id.y);
        final DefaultDomain owner = region.getOwners();
        final Map<Flag<?>, Object> flags = region.getFlags();
        final DefaultDomain members = region.getMembers();
        manager.removeRegion(main.id.x + "-" + main.id.y);

        final Location location1 = PlotHelper.getPlotBottomLocAbs(world, plots.get(0));
        final Location location2 = PlotHelper.getPlotTopLocAbs(world, plots.get(plots.size() - 1));

        final BlockVector vector1 = new BlockVector(location1.getBlockX(), 1, location1.getBlockZ());
        final BlockVector vector2 = new BlockVector(location2.getBlockX(), world.getMaxHeight(), location2.getBlockZ());
        final ProtectedRegion rg = new ProtectedCuboidRegion(main.id.x + "-" + main.id.y, vector1, vector2);

        rg.setFlags(flags);

        rg.setOwners(owner);

        rg.setMembers(members);

        manager.addRegion(rg);
    }

    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onUnlink(final PlotUnlinkEvent event) {
        try {
            final World w = event.getWorld();
            final ArrayList<PlotId> plots = event.getPlots();
            final Plot main = PlotMain.getPlots(w).get(plots.get(0));

            final RegionManager manager = PlotMain.worldGuard.getRegionManager(w);
            final ProtectedRegion region = manager.getRegion(main.id.x + "-" + main.id.y);

            final DefaultDomain owner = region.getOwners();
            final Map<Flag<?>, Object> flags = region.getFlags();
            final DefaultDomain members = region.getMembers();

            manager.removeRegion(main.id.x + "-" + main.id.y);
            for (int i = 1; i < plots.size(); i++) {
                final PlotId id = plots.get(i);
                final Location location1 = PlotHelper.getPlotBottomLocAbs(w, id);
                final Location location2 = PlotHelper.getPlotTopLocAbs(w, id);

                final BlockVector vector1 = new BlockVector(location1.getBlockX(), 1, location1.getBlockZ());
                final BlockVector vector2 = new BlockVector(location2.getBlockX(), w.getMaxHeight(), location2.getBlockZ());
                final ProtectedRegion rg = new ProtectedCuboidRegion(id.x + "-" + id.y, vector1, vector2);

                rg.setFlags(flags);

                rg.setOwners(owner);

                rg.setMembers(members);

                manager.addRegion(rg);
            }
        } catch (final Exception e) {
            //
        }
    }

    @SuppressWarnings("unused")
    @EventHandler
    public void onPlotClaim(final PlayerClaimPlotEvent event) {
        try {
            final Player player = event.getPlayer();
            final Plot plot = event.getPlot();
            final RegionManager manager = PlotMain.worldGuard.getRegionManager(plot.getWorld());

            final Location location1 = PlotHelper.getPlotBottomLoc(plot.getWorld(), plot.getId());
            final Location location2 = PlotHelper.getPlotTopLoc(plot.getWorld(), plot.getId());

            final BlockVector vector1 = new BlockVector(location1.getBlockX(), 1, location1.getBlockZ());
            final BlockVector vector2 = new BlockVector(location2.getBlockX(), plot.getWorld().getMaxHeight(), location2.getBlockZ());

            final ProtectedRegion region = new ProtectedCuboidRegion(plot.getId().x + "-" + plot.getId().y, vector1, vector2);

            final DefaultDomain owner = new DefaultDomain();
            owner.addPlayer(PlotMain.worldGuard.wrapPlayer(player));

            region.setOwners(owner);

            manager.addRegion(region);
        } catch (final Exception e) {
            //
        }
    }

    @SuppressWarnings("unused")
    @EventHandler
    public void onPlotDelete(final PlotDeleteEvent event) {
        try {
            final PlotId plot = event.getPlotId();
            final World world = Bukkit.getWorld(event.getWorld());

            final RegionManager manager = PlotMain.worldGuard.getRegionManager(world);
            manager.removeRegion(plot.x + "-" + plot.y);
        } catch (final Exception e) {
            //
        }
    }
}
