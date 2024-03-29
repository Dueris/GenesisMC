package me.dueris.calio.builder;

import it.unimi.dsi.fastutil.Pair;
import me.dueris.calio.util.NamespaceUtils;
import org.bukkit.NamespacedKey;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

public class ObjectRemapper {
    /**
     * List<Pair<CURRENT -> REMAPPED>>
     */
    @Deprecated
    public static ArrayList<Pair<String, String>> typeMappings = new ArrayList<>();
    /**
     * Map<KEY, Pair<CURRENT -> REMAPPED>>
     */
    private static HashMap<String, ArrayList<Pair<Object, Object>>> objectMappings = new HashMap<>();

    /**
     * Creates a remapped JSON object by parsing the contents of the specified file and remapping the keys using the given current namespace.
     *
     * @param file             the file to be parsed
     * @param currentNamespace the current namespace used for remapping
     * @return the remapped JSON object
     */
    public static JSONObject createRemapped(File file, NamespacedKey currentNamespace) {
        try {
            JSONObject powerParser = (JSONObject) new JSONParser().parse(new FileReader(file));
            remapJsonObject(powerParser, currentNamespace);
            return powerParser;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }

    /**
     * Adds a mapping for the given key and mapper.
     *
     * @param key    the key for the mapping
     * @param mapper the mapper to be added
     * @return void
     */
    public static void addObjectMapping(String key, Pair<Object, Object> mapper) {
        if (objectMappings.containsKey(key)) {
            objectMappings.get(key).add(mapper);
        } else {
            ArrayList list = new ArrayList<>();
            list.add(mapper);
            objectMappings.put(key, list);
        }
    }

    /**
     * Recursively remaps the keys and values of a JSON object based on predefined mappings and dynamic namespaces.
     *
     * @param obj              the JSON object to be remapped
     * @param currentNamespace the current namespace used for dynamic namespace remapping
     */
    private static void remapJsonObject(JSONObject obj, NamespacedKey currentNamespace) {
        for (Object key : obj.keySet()) {
            Object valueInst = obj.get(key.toString());
            // Object mappings
            for (String keyName : objectMappings.keySet()) {
                if (keyName.equalsIgnoreCase(key.toString())) {
                    for (Pair<Object, Object> objectMapping : objectMappings.get(key.toString())) {
                        if (valueInst.equals(objectMapping.left())) {
                            obj.replace(key, objectMapping.right());
                        }
                    }
                }
            }
            // DynamicNamespace remapping
            if (valueInst instanceof String st) {
                if (st.contains(":") && st.contains("*")) {
                    obj.replace(key, NamespaceUtils.getDynamicNamespace(currentNamespace.asString(), st).asString());
                }
            }
            // Depreciated
            if (valueInst instanceof String) {
                for (Pair<String, String> pair : typeMappings) {
                    if (key.toString().equalsIgnoreCase("type") && valueInst.toString().startsWith(pair.left())) {
                        obj.put(key, pair.right() + ":" + valueInst.toString().split(":")[1]);
                    }
                }
            } else if (valueInst instanceof JSONObject) {
                remapJsonObject((JSONObject) valueInst, currentNamespace);
            } else if (valueInst instanceof JSONArray array) {
                for (Object ob : array) {
                    if (ob instanceof JSONObject) {
                        remapJsonObject((JSONObject) ob, currentNamespace);
                    }
                }
            }
        }
    }

}