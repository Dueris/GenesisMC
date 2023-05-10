package me.dueris.genesismc.custom_origins.handlers;

import me.dueris.api.factory.CustomOriginAPI;
import me.dueris.genesismc.core.GenesisMC;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class CustomOriginExistCheck implements Listener {

    public static final List<String> builtInOriginTags = Arrays.asList("genesis:origin-human", "genesis:origin-enderian", "genesis:origin-shulk", "genesis:origin-arachnid", "genesis:origin-creep", "genesis:origin-phantom", "genesis:origin-slimeling", "genesis:origin-feline", "genesis:origin-blazeborn", "genesis:origin-starborne", "genesis:origin-merling", "genesis:origin-allay", "genesis:origin-rabbit", "genesis:origin-bee", "genesis:origin-elytrian", "genesis:origin-avian", "genesis:origin-piglin", "genesis:origin-sculkling");

    @EventHandler
    public static void onPlayerJoin(PlayerJoinEvent e) {
        PersistentDataContainer data = e.getPlayer().getPersistentDataContainer();
        @Nullable String origintag = data.get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
        if (builtInOriginTags.contains(origintag)) return;
        if (CustomOriginAPI.getCustomOriginTags().contains(origintag)) return;
        e.getPlayer().getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING, "genesis:origin-null");
        e.getPlayer().sendMessage(ChatColor.RED + "Your custom origin has been deleted! Please select a new one.");
        e.getPlayer().sendMessage(ChatColor.RED + "If you believe this is a mistake please contact your server admin(s).");
    }

    public static void customOriginExistCheck(Player p) {
        PersistentDataContainer data = p.getPersistentDataContainer();
        @Nullable String origintag = data.get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
        if (builtInOriginTags.contains(origintag)) return;
        if (CustomOriginAPI.getCustomOriginTags().contains(origintag)) return;
        p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING, "genesis:origin-null");
        p.sendMessage(ChatColor.RED + "Your custom origin has been deleted! Please select a new one.");
        p.sendMessage(ChatColor.RED + "If you believe this is a mistake please contact your server admin(s).");
    }
}
