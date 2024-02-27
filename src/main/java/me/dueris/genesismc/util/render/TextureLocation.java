package me.dueris.genesismc.util.render;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.registry.Registerable;
import me.dueris.genesismc.registry.Registrar;
import me.dueris.genesismc.registry.Registries;
import me.dueris.genesismc.util.CooldownUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

public class TextureLocation implements Registerable {
    public static HashMap<String, BarColor> textureMap = new HashMap<>();

    public static void parseAll() throws IOException {
        for(File pack : CraftApoli.datapacksInDir()){
            for(File folders : pack.listFiles()){
                if(folders.getName().equalsIgnoreCase("assets")){
                    for(File root : folders.listFiles()){
                        String rootname = root.getName();
                        for(File file : root.listFiles()){
                            if(file.getName().equalsIgnoreCase("textures")){
                                Files.walk(file.toPath())
                                        .sorted((a, b) -> b.compareTo(a)) // Sort in reverse order
                                        .forEach(path -> {
                                            if(path.toString().endsWith(".png")){
                                                GenesisMC.getPlugin().registry.retrieve(Registries.TEXTURE_LOCATION).register(new TextureLocation(NamespacedKey.fromString(rootname + ":textures" + path.toAbsolutePath().toString().replace(file.getAbsolutePath(), "").replace("\\", "/"))));
                                            }
                                        });
                            }
                        }
                    }
                }
            }
        }

        for(TextureLocation location : ((Registrar<TextureLocation>)GenesisMC.getPlugin().registry.retrieve(Registries.TEXTURE_LOCATION)).values()){
            for(File pack : CraftApoli.datapacksInDir()){
                for(File folders : pack.listFiles()){
                    if(folders.getName().equalsIgnoreCase("assets")){
                        File mainRoot = new File(folders, location.key.asString().split(":")[0]);
                        if(!mainRoot.exists()) continue;
                        Path path = Path.of(mainRoot.getAbsolutePath() + File.separator + location.key.asString().split(":")[1]);
                        if(path.toFile().exists()){
                            File resource = path.toFile();
                            try {
                                BufferedImage image = ImageIO.read(resource);

                                int width = image.getWidth();
                                int height = image.getHeight();

                                Color[] originalPixels = new Color[height];
                                Color[] modifiedPixels = new Color[height];

                                for (int y = 0; y < height; y++) {
                                    originalPixels[y] = new Color(image.getRGB(2, y));
                                }

                                int currentIndex = 0;
                                while (currentIndex < height) {
                                    if(currentIndex >= 180) break;

                                    currentIndex += 2;
                                    modifiedPixels[currentIndex] = originalPixels[currentIndex];
                                    currentIndex += 9;
                                    if (height - currentIndex <= 3) {
                                        break;
                                    }
                                    modifiedPixels[currentIndex] = originalPixels[currentIndex];
                                    currentIndex += 9;
                                }

                                int index = 0;
                                for(Color color : modifiedPixels){
                                    if(color == null) continue;
                                    textureMap.put(location.getKey().asString() + "/-/" + index, CooldownUtils.convertToBarColor(color));
                                    index++;
                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }

    public static int indexOfColor(Color[] array, Color targetColor) {
        for (int i = 0; i < array.length; i++) {
            if(array[i] == null) continue;
            if (array[i].equals(targetColor)) {
                return i;
            }
        }
        return -1;
    }

    private NamespacedKey key;
    public TextureLocation(NamespacedKey key){
        this.key = key;
    }

    @Override
    public NamespacedKey getKey() {
        return key;
    }
}