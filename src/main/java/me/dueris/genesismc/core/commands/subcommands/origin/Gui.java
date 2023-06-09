package me.dueris.genesismc.core.commands.subcommands.origin;

import me.dueris.genesismc.core.GenesisMC;
import me.dueris.genesismc.core.commands.PlayerSelector;
import me.dueris.genesismc.core.commands.subcommands.SubCommand;
import me.dueris.genesismc.core.entity.OriginPlayer;
import me.dueris.genesismc.core.enums.OriginDataType;
import me.dueris.genesismc.core.factory.CraftApoli;
import me.dueris.genesismc.core.utils.LayerContainer;
import me.dueris.genesismc.core.utils.OriginContainer;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.profile.PlayerTextures;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import static me.dueris.genesismc.core.factory.powers.Powers.translucent;


public class Gui extends SubCommand {
    @Override
    public String getName() {
        return "gui";
    }

    @Override
    public String getDescription() {
        return "opens origin choosing screen";
    }

    @Override
    public String getSyntax() {
        return "/origin gui <player>";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!sender.hasPermission("genesismc.origins.cmd.gui")) return;
        if (args.length > 1) {
            ArrayList<Player> players = PlayerSelector.playerSelector(sender, args[1]);
            if (players.size() == 0) return;
            for (Player p : players) {
                for (LayerContainer layer : CraftApoli.getLayers()) {
                    OriginPlayer.unassignPowers(p, layer);
                    OriginPlayer.setOrigin(p, layer, CraftApoli.nullOrigin());
                    OriginPlayer.resetOriginData(p, OriginDataType.IN_PHASING_FORM);
                    String skinData = p.getPersistentDataContainer().get(new NamespacedKey(GenesisMC.getPlugin(), "original-skin-url"), PersistentDataType.STRING).toString();
                    if(p.getPlayerProfile().getTextures().getSkinModel() == PlayerTextures.SkinModel.CLASSIC){
                        try {
                            p.getPlayerProfile().getTextures().setSkin(new URL(skinData), PlayerTextures.SkinModel.CLASSIC);
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                    }else{
                        try {
                            p.getPlayerProfile().getTextures().setSkin(new URL(skinData), PlayerTextures.SkinModel.SLIM);
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                    }
                    for(Player pls : Bukkit.getOnlinePlayers()){
                        pls.hidePlayer(GenesisMC.getPlugin(), p);
                    }
                    for(Player pls : Bukkit.getOnlinePlayers()){
                        pls.showPlayer(GenesisMC.getPlugin(), p);
                    }
                }
            }
        } else if (args.length == 1 && sender instanceof Player p) {
            for (LayerContainer layer : CraftApoli.getLayers()) {
                OriginPlayer.unassignPowers(p, layer);
                OriginPlayer.setOrigin(p, layer, CraftApoli.nullOrigin());
//                String skinData = p.getPersistentDataContainer().get(new NamespacedKey(GenesisMC.getPlugin(), "original-skin-url"), PersistentDataType.STRING).toString();
//                if(p.getPlayerProfile().getTextures().getSkinModel() == PlayerTextures.SkinModel.CLASSIC){
//                    try {
//                        p.getPlayerProfile().getTextures().setSkin(new URL(skinData), PlayerTextures.SkinModel.CLASSIC);
//                    } catch (MalformedURLException e) {
//                        e.printStackTrace();
//                    }
//                }else{
//                    try {
//                        p.getPlayerProfile().getTextures().setSkin(new URL(skinData), PlayerTextures.SkinModel.SLIM);
//                    } catch (MalformedURLException e) {
//                        e.printStackTrace();
//                    }
//                }
                for(Player pls : Bukkit.getOnlinePlayers()){
                    pls.hidePlayer(GenesisMC.getPlugin(), p);
                }
                for(Player pls : Bukkit.getOnlinePlayers()){
                    pls.showPlayer(GenesisMC.getPlugin(), p);
                }
            }
        }
    }
}
