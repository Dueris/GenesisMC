package me.dueris.genesismc.core.choosing;

import me.dueris.genesismc.core.GenesisMC;
import me.dueris.genesismc.core.choosing.contents.origins.*;
import me.dueris.genesismc.core.files.GenesisDataFiles;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static me.dueris.api.origin.RandomOriginID.RandomOrigin;
import static me.dueris.genesismc.core.choosing.contents.MainMenuContents.GenesisMainMenuContents;
import static org.bukkit.ChatColor.*;

public class ChoosingCORE implements Listener {

    @EventHandler
    public void onOrbClick(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (p.hasPermission("genesismc.choosing.rechoose")) {
            if (GenesisDataFiles.getPlugCon().getString("orb-of-origins-enabled").equalsIgnoreCase("true")) {
                if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    if (p.getOpenInventory().getBottomInventory() != null) ;
                    ItemStack random = new ItemStack(Material.MAGMA_CREAM);

                    ItemMeta meta = random.getItemMeta();
                    meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
                    meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                    meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    meta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
                    meta.setCustomModelData(00002);
                    meta.setDisplayName(GenesisDataFiles.getOrbCon().getString("name"));
                    meta.setUnbreakable(true);
                    meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
                    random.setItemMeta(meta);
                    PersistentDataContainer data = p.getPersistentDataContainer();
                    int originid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER);
                    int phantomid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform"), PersistentDataType.INTEGER);
                    if (phantomid == 1) ;
                    if (e.getItem() != null && e.getItem().getType() != null) {
                        if (e.getItem().equals(random)) {

                            @NotNull Inventory mainmenu = Bukkit.createInventory(e.getPlayer(), 54, "Choosing Menu");
                            mainmenu.setContents(GenesisMainMenuContents());
                            e.getPlayer().openInventory(mainmenu);

                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void OnInteractCancel(InventoryClickEvent e){

        if(e.getCurrentItem() != null){
            if(e.getView().getTitle().equalsIgnoreCase("Choosing Menu")){
                if(e.getCurrentItem().getType().equals(Material.SPECTRAL_ARROW)) {
                    e.getClickedInventory().setContents(GenesisMainMenuContents());
                    e.setCancelled(true);
                }else{e.setCancelled(true);}


            }else{
                if(e.getView().getTitle().equalsIgnoreCase("Custom Origins") || e.getView().getTitle().equalsIgnoreCase("Expanded Origins")){
                    if(e.getCurrentItem().getType().equals(Material.SPECTRAL_ARROW)){
                        @NotNull Inventory mainmenu = Bukkit.createInventory(e.getWhoClicked(), 54, "Choosing Menu");
                        mainmenu.setContents(GenesisMainMenuContents());
                        e.getWhoClicked().openInventory(mainmenu);
                    }
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public static void ChooserJoin(PlayerJoinEvent e) {
        PersistentDataContainer data = e.getPlayer().getPersistentDataContainer();
        int originid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER);
        @NotNull Inventory mainmenu = Bukkit.createInventory(e.getPlayer(), 54, "Choosing Menu");
        if (originid == 0) {
            mainmenu.setContents(GenesisMainMenuContents());
            e.getPlayer().openInventory(mainmenu);
        }
    }

    @EventHandler
    public void OnChoose(InventoryClickEvent e){
        if (e.getView().getTitle().equalsIgnoreCase("Choosing Menu")) {
            //Human
            ItemStack human = new ItemStack(Material.PLAYER_HEAD);
            ItemMeta human_meta = human.getItemMeta();
            human_meta.setDisplayName("Human");
            ArrayList<String> human_lore = new ArrayList<>();
            human_lore.add(WHITE + "Human Origin");
            human_meta.addEnchant(Enchantment.ARROW_INFINITE, 0, true);
            human_meta.setLore(human_lore);
            human.setItemMeta(human_meta);

            ItemStack ender = new ItemStack(Material.ENDER_PEARL);
            ItemMeta ender_meta = ender.getItemMeta();
            ender_meta.setDisplayName("Enderian");
            ArrayList<String> ender_lore = new ArrayList<>();
            ender_lore.add(LIGHT_PURPLE + "Enderman Origin");
            human_meta.addEnchant(Enchantment.ARROW_INFINITE, 0, true);
            ender_meta.setLore(ender_lore);
            ender.setItemMeta(ender_meta);

            ItemStack shulk = new ItemStack(Material.SHULKER_SHELL);
            ItemMeta shulk_meta = shulk.getItemMeta();
            shulk_meta.setDisplayName("Shulk");
            ArrayList<String> shulk_lore = new ArrayList<>();
            shulk_lore.add(LIGHT_PURPLE + "Shulker Origin");
            shulk_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
            shulk_meta.setLore(shulk_lore);
            shulk.setItemMeta(shulk_meta);

            ItemStack spider = new ItemStack(Material.COBWEB);
            ItemMeta spider_meta = spider.getItemMeta();
            spider_meta.setDisplayName("Arachnid");
            ArrayList<String> spider_lore = new ArrayList<>();
            spider_lore.add(RED + "Spider Origin");
            spider_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
            spider_meta.setLore(spider_lore);
            spider.setItemMeta(spider_meta);

            ItemStack creep = new ItemStack(Material.GUNPOWDER);
            ItemMeta creep_meta = creep.getItemMeta();
            creep_meta.setDisplayName("Creep");
            ArrayList<String> creep_lore = new ArrayList<>();
            creep_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
            creep_lore.add(GREEN + "Creeper Origin");
            creep_meta.setLore(creep_lore);
            creep.setItemMeta(creep_meta);

            ItemStack phantom = new ItemStack(Material.PHANTOM_MEMBRANE);
            ItemMeta phantom_meta = phantom.getItemMeta();
            phantom_meta.setDisplayName("Phantom");
            ArrayList<String> phantom_lore = new ArrayList<>();
            phantom_lore.add(BLUE + "Phantom Origin");
            phantom_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
            phantom_meta.setLore(phantom_lore);
            phantom.setItemMeta(phantom_meta);

            ItemStack slime = new ItemStack(Material.SLIME_BALL);
            ItemMeta slime_meta = slime.getItemMeta();
            slime_meta.setDisplayName("Slimeling");
            ArrayList<String> slime_lore = new ArrayList<>();
            slime_lore.add(GREEN + "Slime Origin");
            slime_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
            slime_meta.setLore(slime_lore);
            slime.setItemMeta(slime_meta);

            ItemStack vex = new ItemStack(Material.IRON_SWORD);
            ItemMeta vex_meta = vex.getItemMeta();
            vex_meta.setDisplayName("Vexian");
            ArrayList<String> vex_lore = new ArrayList<>();
            vex_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
            vex_lore.add(AQUA + "Vex Origin");
            vex_meta.setLore(vex_lore);
            vex.setItemMeta(vex_meta);

            ItemStack blaze = new ItemStack(Material.BLAZE_POWDER);
            ItemMeta blaze_meta = blaze.getItemMeta();
            blaze_meta.setDisplayName("Blazeborn");
            ArrayList<String> blaze_lore = new ArrayList<>();
            blaze_lore.add(GOLD + "Blaze Origin");
            blaze_meta.setLore(blaze_lore);
            blaze_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
            blaze.setItemMeta(blaze_meta);

            ItemStack star = new ItemStack(Material.NETHER_STAR);
            ItemMeta star_meta = star.getItemMeta();
            star_meta.setDisplayName("Starborne");
            ArrayList<String> star_lore = new ArrayList<>();
            star_lore.add(LIGHT_PURPLE + "Starborne Origin");
            star_meta.setLore(star_lore);
            star_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
            star.setItemMeta(star_meta);

            ItemStack mermaid = new ItemStack(Material.COD);
            ItemMeta mermaid_meta = mermaid.getItemMeta();
            mermaid_meta.setDisplayName("Merling");
            ArrayList<String> mermaid_lore = new ArrayList<>();
            mermaid_lore.add(BLUE + "Merling Origin");
            mermaid_meta.setLore(mermaid_lore);
            mermaid_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
            mermaid.setItemMeta(mermaid_meta);

            ItemStack allay = new ItemStack(Material.AMETHYST_SHARD);
            ItemMeta allay_meta = allay.getItemMeta();
            allay_meta.setDisplayName("Allay");
            ArrayList<String> allay_lore = new ArrayList<>();
            allay_lore.add(AQUA + "Allay Origin");
            allay_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
            allay_meta.setLore(allay_lore);
            allay.setItemMeta(allay_meta);

            ItemStack rabbit = new ItemStack(Material.CARROT);
            ItemMeta rabbit_meta = rabbit.getItemMeta();
            rabbit_meta.setDisplayName("Rabbit");
            ArrayList<String> rabbit_lore = new ArrayList<>();
            rabbit_lore.add(GOLD + "Bunny Origin");
            rabbit_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
            rabbit_meta.setLore(rabbit_lore);
            rabbit.setItemMeta(rabbit_meta);

            ItemStack bee = new ItemStack(Material.HONEYCOMB);
            ItemMeta bee_meta = bee.getItemMeta();
            bee_meta.setDisplayName("Bumblebee");
            ArrayList<String> bee_lore = new ArrayList<>();
            bee_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
            bee_lore.add(YELLOW + "Bee Origin");
            bee_meta.setLore(bee_lore);
            bee.setItemMeta(bee_meta);

            ItemStack elyrtian = new ItemStack(Material.ELYTRA);
            ItemMeta elyrtian_meta = elyrtian.getItemMeta();
            elyrtian_meta.setDisplayName("Elytrian");
            ArrayList<String> elyrtian_lore = new ArrayList<>();
            elyrtian_lore.add(GRAY + "Elytrian Origin");
            elyrtian_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
            elyrtian_meta.setLore(elyrtian_lore);
            elyrtian.setItemMeta(elyrtian_meta);

            ItemStack avian = new ItemStack(Material.FEATHER);
            ItemMeta avian_meta = avian.getItemMeta();
            avian_meta.setDisplayName("Avian");
            ArrayList<String> avian_lore = new ArrayList<>();
            avian_lore.add(DARK_AQUA + "Avian Origin");
            avian_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
            avian_meta.setLore(avian_lore);
            avian.setItemMeta(avian_meta);

            ItemStack piglin = new ItemStack(Material.GOLD_INGOT);
            ItemMeta piglin_meta = piglin.getItemMeta();
            piglin_meta.setDisplayName("Piglin");
            ArrayList<String> piglin_lore = new ArrayList<>();
            piglin_lore.add(GOLD + "Piglin Origin");
            piglin_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
            piglin_meta.setLore(piglin_lore);
            piglin.setItemMeta(piglin_meta);

            ItemStack sculk = new ItemStack(Material.ECHO_SHARD);
            ItemMeta sculk_meta = sculk.getItemMeta();
            sculk_meta.setDisplayName("Sculkling");
            ArrayList<String> sculk_lore = new ArrayList<>();
            sculk_lore.add(BLUE + "Sculk Origin");
            sculk_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
            sculk_meta.setLore(sculk_lore);
            sculk.setItemMeta(sculk_meta);
            Player p = (Player) e.getWhoClicked();
            if (e.getCurrentItem() != null && e.getCurrentItem().containsEnchantment(Enchantment.ARROW_INFINITE)) {
                if(e.getCurrentItem().equals(human)){
                    p.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.1);
                    p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER, 0004013);
                    p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "can-explode"), PersistentDataType.INTEGER, 1);
                    p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform"), PersistentDataType.INTEGER, 1);
                    p.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(0.0F);
                    p.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).setBaseValue(0);
                    DefaultChoose.DefaultChoose();
                }
                if(e.getCurrentItem().equals(ender)){
                    p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER, 0401065);
                    p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "can-explode"), PersistentDataType.INTEGER, 1);
                    p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform"), PersistentDataType.INTEGER, 1);
                    ItemStack infinpearl = new ItemStack(Material.ENDER_PEARL);
                    ItemMeta pearl_meta = infinpearl.getItemMeta();
                    pearl_meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Teleport");
                    ArrayList<String> pearl_lore = new ArrayList();
                    pearl_meta.setUnbreakable(true);
                    pearl_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
                    pearl_meta.setLore(pearl_lore);
                    infinpearl.setItemMeta(pearl_meta);
                    p.getInventory().addItem(infinpearl);
                    p.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(0.0);
                    p.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(0.0F);
                    p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(24);
                    p.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.1);
                    float walk = 0.2F;
                    p.setWalkSpeed(walk);
                    p.getWorld().spawnParticle(Particle.REVERSE_PORTAL, p.getLocation(), 9);
                    p.setHealthScaled(false);
                    p.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).setBaseValue(0);
                    DefaultChoose.DefaultChoose();
                }
                if(e.getCurrentItem().equals(shulk)){
                    p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER, 6503044);
                    p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "can-explode"), PersistentDataType.INTEGER, 1);
                    p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform"), PersistentDataType.INTEGER, 1);
                    float walk = 0.185F;
                    p.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(8.0);
                    p.setWalkSpeed(walk);
                    p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
                    p.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(0.45F);
                    p.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).setBaseValue(2);
                    DefaultChoose.DefaultChoose();
                }
                if(e.getCurrentItem().equals(spider)){
                    p.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).setBaseValue(0);
                    p.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.1);
                    p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER, 1709012);
                    p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "can-explode"), PersistentDataType.INTEGER, 1);
                    p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform"), PersistentDataType.INTEGER, 1);
                    p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(14);
                    p.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(0);
                    p.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(0.0F);
                    DefaultChoose.DefaultChoose();
                }
                if(e.getCurrentItem().equals(creep)){
                    p.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).setBaseValue(0);
                    p.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.1);
                    p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER, 2356555);
                    p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "can-explode"), PersistentDataType.INTEGER, 2);
                    p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform"), PersistentDataType.INTEGER, 1);
                    p.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(0);
                    p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(18);
                    p.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(0.0F);
                    DefaultChoose.DefaultChoose();
                }
                if(e.getCurrentItem().equals(phantom)){
                    p.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).setBaseValue(0);
                    p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER, 7300041);
                    p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "can-explode"), PersistentDataType.INTEGER, 1);
                    p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform"), PersistentDataType.INTEGER, 1);
                    p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(14);
                    p.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.11);
                    p.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(0);
                    ItemStack spectatorswitch = new ItemStack(Material.PHANTOM_MEMBRANE);
                    ItemMeta switch_meta = spectatorswitch.getItemMeta();
                    switch_meta.setDisplayName(GRAY + "Phantom Form");
                    ArrayList<String> pearl_lore = new ArrayList();
                    switch_meta.setUnbreakable(true);
                    switch_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
                    switch_meta.addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS);
                    switch_meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    switch_meta.setLore(pearl_lore);
                    spectatorswitch.setItemMeta(switch_meta);
                    p.getInventory().addItem(spectatorswitch);
                    p.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(0.0F);
                    DefaultChoose.DefaultChoose();
                }
                if(e.getCurrentItem().equals(slime)){
                    p.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).setBaseValue(0);
                    p.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.1);
                    p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER, 2304045);
                    p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "can-explode"), PersistentDataType.INTEGER, 1);
                    p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform"), PersistentDataType.INTEGER, 1);
                    p.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(0.0F);
                    DefaultChoose.DefaultChoose();
                }
                if(e.getCurrentItem().equals(vex)){
                    p.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).setBaseValue(0);
                    p.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.1);
                    p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER, 9602042);
                    p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "can-explode"), PersistentDataType.INTEGER, 1);
                    p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform"), PersistentDataType.INTEGER, 1);
                    p.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(0.0F);
                    DefaultChoose.DefaultChoose();
                }
                if(e.getCurrentItem().equals(blaze)){
                    p.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).setBaseValue(0);
                    p.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.1);
                    p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER, 9811027);
                    p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "can-explode"), PersistentDataType.INTEGER, 1);
                    p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform"), PersistentDataType.INTEGER, 1);
                    p.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(0.0F);
                    DefaultChoose.DefaultChoose();
                }
                if(e.getCurrentItem().equals(star)){
                    p.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).setBaseValue(0);
                    p.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.1);
                    p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER, 7303065);
                    p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "can-explode"), PersistentDataType.INTEGER, 1);
                    p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform"), PersistentDataType.INTEGER, 1);
                    p.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(0.0F);
                    DefaultChoose.DefaultChoose();
                }
                if(e.getCurrentItem().equals(mermaid)){
                    p.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).setBaseValue(0);
                    p.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.1);
                    p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER, 1310018);
                    p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "can-explode"), PersistentDataType.INTEGER, 1);
                    p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform"), PersistentDataType.INTEGER, 1);
                    p.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(0.0F);
                    DefaultChoose.DefaultChoose();
                }
                if(e.getCurrentItem().equals(allay)){
                    p.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).setBaseValue(0);
                    p.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.1);
                    p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER, 1205048);
                    p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "can-explode"), PersistentDataType.INTEGER, 1);
                    p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform"), PersistentDataType.INTEGER, 1);
                    p.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(0.0F);
                    DefaultChoose.DefaultChoose();
                }
                if(e.getCurrentItem().equals(rabbit)){
                    p.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).setBaseValue(0);
                    p.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.1);
                    p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER, 5308033);
                    p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "can-explode"), PersistentDataType.INTEGER, 1);
                    p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform"), PersistentDataType.INTEGER, 1);
                    p.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(0.0F);
                    p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(14);
                    DefaultChoose.DefaultChoose();
                }
                if(e.getCurrentItem().equals(bee)){
                    p.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).setBaseValue(0);
                    p.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.1);
                    p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER, 8906022);
                    p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "can-explode"), PersistentDataType.INTEGER, 1);
                    p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform"), PersistentDataType.INTEGER, 1);
                    p.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(0.0F);
                    DefaultChoose.DefaultChoose();
                }
                if(e.getCurrentItem().equals(elyrtian)){
                    p.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).setBaseValue(0);
                    p.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.1);
                    p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER, 6211006);
                    p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "can-explode"), PersistentDataType.INTEGER, 1);
                    p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform"), PersistentDataType.INTEGER, 1);
                    p.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(0.0F);
                    DefaultChoose.DefaultChoose();
                }
                if(e.getCurrentItem().equals(avian)){
                    p.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).setBaseValue(0);
                    p.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.13);
                    p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER, 4501011);
                    p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "can-explode"), PersistentDataType.INTEGER, 1);
                    p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform"), PersistentDataType.INTEGER, 1);
                    p.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(0.0F);
                    p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
                    DefaultChoose.DefaultChoose();
                }
                if(e.getCurrentItem().equals(piglin)){
                    p.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).setBaseValue(0);
                    p.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.1);
                    p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER, 6211021);
                    p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "can-explode"), PersistentDataType.INTEGER, 1);
                    p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform"), PersistentDataType.INTEGER, 1);
                    p.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(0.0F);
                    DefaultChoose.DefaultChoose();
                }
                if(e.getCurrentItem().equals(sculk)){
                    p.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).setBaseValue(0);
                    p.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.1);
                    p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER, 4307015);
                    p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "can-explode"), PersistentDataType.INTEGER, 1);
                    p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform"), PersistentDataType.INTEGER, 1);
                    p.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(0.0F);
                    DefaultChoose.DefaultChoose();
                }

            }
        }

    }

    //open the menus

    @EventHandler
    public void OnOpenNew(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (e.getView().getTitle().equalsIgnoreCase("Choosing Menu")) {
            if (e.getCurrentItem() != null) {
                //Human
                if (e.getCurrentItem() != null && !e.getCurrentItem().containsEnchantment(Enchantment.ARROW_INFINITE)) {
                    if (e.getCurrentItem().getType() == Material.PLAYER_HEAD && !e.getCurrentItem().getItemMeta().getItemFlags().contains(ItemFlag.HIDE_ENCHANTS)) {
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 9);
                        if (p.hasPermission("genesismc.origins.human")) {
                            e.getClickedInventory().setContents(HumanContents.HumanContents());
                        }
                    }else if (e.getCurrentItem().getType() == Material.ENDER_PEARL && !e.getCurrentItem().getItemMeta().getItemFlags().contains(ItemFlag.HIDE_ENCHANTS)) {
                        e.setCancelled(true);
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 9);
                        if (p.hasPermission("genesismc.origins.enderian")) {
                            e.getClickedInventory().setContents(EnderianContents.EnderianContents());
                        }
                    }else if (e.getCurrentItem().getType() == Material.SHULKER_SHELL && !e.getCurrentItem().getItemMeta().getItemFlags().contains(ItemFlag.HIDE_ENCHANTS)) {
                        e.setCancelled(true);
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 9);
                        if (p.hasPermission("genesismc.origins.shulk")) {
                            e.getClickedInventory().setContents(ShulkContents.ShulkContents());
                        }
                    }else if (e.getCurrentItem().getType() == Material.COBWEB && !e.getCurrentItem().getItemMeta().getItemFlags().contains(ItemFlag.HIDE_ENCHANTS)) {
                        e.setCancelled(true);
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 9);
                        if (p.hasPermission("genesismc.origins.arachnid")) {
                            e.getClickedInventory().setContents(ArachnidContents.ArachnidContents());
                        }
                    }else if (e.getCurrentItem().getType() == Material.GUNPOWDER && !e.getCurrentItem().getItemMeta().getItemFlags().contains(ItemFlag.HIDE_ENCHANTS)) {
                        e.setCancelled(true);
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 9);
                        if (p.hasPermission("genesismc.origins.creep")) {
                            e.getClickedInventory().setContents(CreepContents.CreepContents());
                        }
                    }else if (e.getCurrentItem().getType() == Material.PHANTOM_MEMBRANE && !e.getCurrentItem().getItemMeta().getItemFlags().contains(ItemFlag.HIDE_ENCHANTS)) {
                        e.setCancelled(true);
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 9);
                        if (p.hasPermission("genesismc.origins.phantom")) {
                            e.getClickedInventory().setContents(PhantomContents.PhantomContents());
                        }
                    }else if (e.getCurrentItem().getType() == Material.SLIME_BALL && !e.getCurrentItem().getItemMeta().getItemFlags().contains(ItemFlag.HIDE_ENCHANTS)) {
                        e.setCancelled(true);
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 9);
                        if (p.hasPermission("genesismc.origins.slimeling")) {
                            e.getClickedInventory().setContents(SlimelingContents.SlimelingContents());
                        }
                    }else if (e.getCurrentItem().getType() == Material.IRON_SWORD && !e.getCurrentItem().getItemMeta().getItemFlags().contains(ItemFlag.HIDE_ENCHANTS)) {
                        e.setCancelled(true);
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 9);
                        if (p.hasPermission("genesismc.origins.vexian")) {
                            e.getClickedInventory().setContents(VexianContents.VexianContents());
                        }
                    }else if (e.getCurrentItem().getType() == Material.BLAZE_POWDER && !e.getCurrentItem().getItemMeta().getItemFlags().contains(ItemFlag.HIDE_ENCHANTS)) {
                        e.setCancelled(true);
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 9);
                        if (p.hasPermission("genesismc.origins.blazeborn")) {
                            e.getClickedInventory().setContents(BlazebornContents.BlazebornContents());
                        }
                    }else
                    if (e.getCurrentItem().getType() == Material.NETHER_STAR && !e.getCurrentItem().getItemMeta().getItemFlags().contains(ItemFlag.HIDE_ENCHANTS)) {
                        e.setCancelled(true);
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 9);
                        if (p.hasPermission("genesismc.origins.starborne")) {
                            e.getClickedInventory().setContents(StarborneContents.StarborneContents());
                        }
                    }else if (e.getCurrentItem().getType() == Material.COD && !e.getCurrentItem().getItemMeta().getItemFlags().contains(ItemFlag.HIDE_ENCHANTS)) {
                        e.setCancelled(true);
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 9);
                        if (p.hasPermission("genesismc.origins.merling")) {
                            e.getClickedInventory().setContents(MerlingContents.MerlingContents());
                        }
                    }else if (e.getCurrentItem().getType() == Material.AMETHYST_SHARD && !e.getCurrentItem().getItemMeta().getItemFlags().contains(ItemFlag.HIDE_ENCHANTS)) {
                        e.setCancelled(true);
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 9);
                        if (p.hasPermission("genesismc.origins.allay")) {
                            e.getClickedInventory().setContents(AllayContents.AllayContents());
                        }
                    }else if (e.getCurrentItem().getType() == Material.CARROT && !e.getCurrentItem().getItemMeta().getItemFlags().contains(ItemFlag.HIDE_ENCHANTS)) {
                        e.setCancelled(true);
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 9);
                        if (p.hasPermission("genesismc.origins.rabbit")) {
                            e.getClickedInventory().setContents(RabbitContents.RabbitContents());
                        }
                    }else if (e.getCurrentItem().getType() == Material.HONEYCOMB && !e.getCurrentItem().getItemMeta().getItemFlags().contains(ItemFlag.HIDE_ENCHANTS)) {
                        e.setCancelled(true);
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 9);
                        if (p.hasPermission("genesismc.origins.bee")) {
                            e.getClickedInventory().setContents(BeeContents.BeeContents());
                        }
                    }else if (e.getCurrentItem().getType() == Material.ELYTRA && !e.getCurrentItem().getItemMeta().getItemFlags().contains(ItemFlag.HIDE_ENCHANTS)) {
                        e.setCancelled(true);
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 9);
                        if (p.hasPermission("genesismc.origins.elytrian")) {
                            e.getClickedInventory().setContents(ElytrianContents.ElytrianContents());
                        }
                    }else if (e.getCurrentItem().getType() == Material.FEATHER && !e.getCurrentItem().getItemMeta().getItemFlags().contains(ItemFlag.HIDE_ENCHANTS)) {
                        e.setCancelled(true);
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 9);
                        if (p.hasPermission("genesismc.origins.avian")) {
                            e.getClickedInventory().setContents(AvianContents.AvianContents());
                        }
                    }else if (e.getCurrentItem().getType() == Material.GOLD_INGOT && !e.getCurrentItem().getItemMeta().getItemFlags().contains(ItemFlag.HIDE_ENCHANTS)) {
                        e.setCancelled(true);
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 9);
                        if (p.hasPermission("genesismc.origins.piglin")) {
                            e.getClickedInventory().setContents(PiglinContents.PiglinContents());
                        }
                    }else if (e.getCurrentItem().getType() == Material.ECHO_SHARD && !e.getCurrentItem().getItemMeta().getItemFlags().contains(ItemFlag.HIDE_ENCHANTS)) {
                        e.setCancelled(true);
                        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 9);
                        if (p.hasPermission("genesismc.origins.sculkling")) {
                            e.getClickedInventory().setContents(SculkContents.SculkContents());
                        }
                    }
                }
            }

        }
    }

}