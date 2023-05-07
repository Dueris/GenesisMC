package me.dueris.genesismc.custom_origins.powers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.Random;

import static org.bukkit.Material.*;

public class WorldSpawnHandler {

    public static Location NetherSpawn() {
        for (World world : Bukkit.getWorlds()) {
            if (world.getEnvironment() == World.Environment.NETHER) {

                Random random = new Random();
                Location location = new Location(world, random.nextInt(-300, 300), 32, random.nextInt(-300, 300));

                for (int x = (int) (location.getX()-100); x < location.getX()+100; x++) {
                    for (int z = (int) (location.getZ()-100); z < location.getZ()+100; z++) {
                        yLoop:
                        for (int y = (int) (location.getY()); y < location.getY()+68; y++) {
                            if (new Location(world, x, y, z).getBlock().getType() != AIR) continue;
                            if (new Location(world, x, y+1, z).getBlock().getType() != AIR) continue;
                            Material blockBeneath = new Location(world, x, y-1, z).getBlock().getType();
                            if (blockBeneath == AIR || blockBeneath == LAVA || blockBeneath == FIRE || blockBeneath == SOUL_FIRE) continue;

                            for (int potentialX = (int) (new Location(world, x, y, z).getX()-2); potentialX < new Location(world, x, y, z).getX()+2; potentialX++) {
                                for (int potentialY = (int) (new Location(world, x, y, z).getY()); potentialY < new Location(world, x, y, z).getY()+2; potentialY++) {
                                    for (int potentialZ = (int) (new Location(world, x, y, z).getZ()-2); potentialZ < new Location(world, x, y, z).getZ()+2; potentialZ++) {
                                        if (new Location(world, potentialX, potentialY, potentialZ).getBlock().getType() != AIR) continue yLoop;
                                    }
                                }
                            }
                            return (new Location(world, x+0.5, y, z+0.5));
                        }
                    }
                }
                break;
            }
        }
        return null;
    }
}