package com.intellectualcrafters.plot.commands;

import com.intellectualcrafters.plot.config.C;
import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.util.PlayerFunctions;
import org.bukkit.entity.Player;

/**
 * Created 2014-11-09 for PlotSquared
 *
 * @author Citymonstret
 */
public class Unban extends SubCommand {

    public Unban() {
        super(Command.UNBAN, "Alis for /plot denied remove", "/plot unban [player]", CommandCategory.ACTIONS, true);
    }

    @Override
    public boolean execute(final Player plr, final String... args) {
        if (args.length < 1) {
            return PlayerFunctions.sendMessage(plr, "&cUsage: &c" + this.usage);
        }
        if (!PlayerFunctions.isInPlot(plr)) {
            return sendMessage(plr, C.NOT_IN_PLOT);
        }
        final Plot plot = PlayerFunctions.getCurrentPlot(plr);
        if (!plot.hasRights(plr)) {
            return sendMessage(plr, C.NO_PLOT_PERMS);
        }
        return plr.performCommand("plot denied remove " + args[0]);
    }
}
