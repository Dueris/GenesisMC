package me.dueris.genesismc.core.commands;

import me.dueris.genesismc.core.commands.subcommands.SubCommand;
import me.dueris.genesismc.core.commands.subcommands.origin.*;
import me.dueris.genesismc.core.files.GenesisDataFiles;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class GenesisCommandManager implements CommandExecutor {

    //key = uuid of player
    //long = epoch time of when ran command
    private ArrayList<SubCommand> subCommands = new ArrayList<>();
    public GenesisCommandManager(){
        subCommands.add(new Purge());
        subCommands.add(new Texture());
        subCommands.add(new CommandList());
        subCommands.add(new Config());
        subCommands.add(new OriginsChoose());
        subCommands.add(new Enchant());
        subCommands.add(new Get());

    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player){
            Player p = (Player) sender;
            if (args.length > 0){
                for (int i = 0; i < getSubCommands().size(); i++){
                    if (args[0].equalsIgnoreCase(getSubCommands().get(i).getName())){
                        getSubCommands().get(i).perform(p,args);
                    }

                }
            }
            if(args.length == 0){
                p.sendMessage(ChatColor.RED + "You did not provide any args. Here is a list of commands:");
                p.sendMessage(ChatColor.YELLOW + "-----------------------------------------",
                        ChatColor.WHITE + "/origins choose",
                        ChatColor.WHITE + "/origins purge <player_name>",
                        ChatColor.WHITE + "/origins reload",
                        ChatColor.WHITE + "/origins commands",
                        ChatColor.WHITE + "/origins config",
                        ChatColor.WHITE + "/origins texture",
                        ChatColor.WHITE + "/shulker open",
                        ChatColor.WHITE + "/beta <beta-args>",
                        ChatColor.YELLOW + "-----------------------------------------"
                );
            }
        }
        return true;
    }







public ArrayList<SubCommand> getSubCommands(){
        return subCommands;
}
}