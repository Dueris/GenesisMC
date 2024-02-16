package me.dueris.genesismc.command.subcommands.resource;

import me.dueris.genesismc.command.PlayerSelector;
import me.dueris.genesismc.command.subcommands.SubCommand;
import me.dueris.genesismc.registry.OriginContainer;
import me.dueris.genesismc.registry.PowerContainer;
import me.dueris.genesismc.util.LangConfig;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

import static me.dueris.genesismc.util.ColorConstants.RED;

public class Has extends SubCommand {
    @Override
    public String getName() {
        return "has";
    }

    @Override
    public String getDescription() {
        return "do you has!??!?!";
    }

    @Override
    public String getSyntax() {
        return "/resource has <args>";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (args.length == 1) {
            sender.sendMessage(Component.text(LangConfig.getLocalizedString(sender, "command.resource.has.noPlayer")).color(TextColor.fromHexString(RED)));
            return;
        }
        if (args.length == 2) {
            sender.sendMessage(Component.text(LangConfig.getLocalizedString(sender, "command.resource.has.noPower")).color(TextColor.fromHexString(RED)));
            return;
        }
        ArrayList<Player> players = PlayerSelector.playerSelector(sender, args[1]);
        if (players.size() == 0) return;
        boolean tru = false;
        for (Player p : players) {
            for (OriginContainer origin : OriginPlayerAccessor.getOrigin(p).values()) {
                for (PowerContainer powerContainer : origin.getPowerContainers()) {
                    if (powerContainer.getType().equals("apoli:cooldown") || powerContainer.getType().equals("apoli:resource")) {
                        if (powerContainer.getTag().equals(args[2])) {
                            sender.sendMessage("Test passed.");
                            tru = true;
                        }
                    }
                }
            }
        }
        if (!tru) {
            sender.sendMessage(ChatColor.RED + "Test failed.");
        }
    }
}