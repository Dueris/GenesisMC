package me.dueris.genesismc.commands.subcommands.power;

import me.dueris.genesismc.commands.PlayerSelector;
import me.dueris.genesismc.commands.subcommands.SubCommand;
import me.dueris.genesismc.entity.OriginPlayerUtils;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.utils.LayerContainer;
import me.dueris.genesismc.utils.PowerContainer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Has extends SubCommand {
    @Override
    public String getName() {
        return "has";
    }

    @Override
    public String getDescription() {
        return "returns if the player has that power";
    }

    @Override
    public String getSyntax() {
        return "/power has <args>";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Please provide a player arg.");
        } else if (args.length == 1) {
            sender.sendMessage(ChatColor.RED + "Please provide a power arg.");
        } else if (args.length >= 2) {
            ArrayList<Player> players = PlayerSelector.playerSelector(sender, args[1]);
            for (Player p : players) {
                boolean passed = false;
                if (players.size() == 0) return;
                for (LayerContainer layer : CraftApoli.getLayers()) {
                    for (PowerContainer power : OriginPlayerUtils.playerPowerMapping.get(p).get(layer)) {
                        if (passed) continue;
                        if (power.getTag().equals(args[2])) {
                            passed = true;
                        }
                    }
                }
                if (passed) {
                    sender.sendMessage("Test passed");
                } else {
                    sender.sendMessage(ChatColor.RED + "Test failed");
                }
            }
        }
    }
}
