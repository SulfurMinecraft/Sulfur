package dev.sulfurmc.Sulfur.Utils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.sulfurmc.Sulfur.Sulfur;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@SuppressWarnings("unchecked")
public class Configuration {
    private final File file;
    private JsonObject config;

    public Configuration(String name, Plugin plugin) {
        LoadedPlugin loaded = Sulfur.loadedPlugins.get(plugin);
        if (loaded == null) {
            file = null;
            return;
        }

        file = new File("plugins" + File.separator + loaded.getName(), name + ".yml");

        Map<String, Object> defaults;
        try (InputStream is = loaded.getClassLoader().getResourceAsStream(name + ".yml")) {
            if (is == null) throw new RuntimeException(name + ".yml not in jar!");
            defaults = new Yaml().load(is);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try (InputStream in = loaded.getClassLoader().getResourceAsStream(name + ".yml");
                 FileOutputStream out = new FileOutputStream(file)) {

                if (in == null) throw new RuntimeException(name + ".yml not in jar!");

                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        Map<String, Object> existing;
        try (InputStream in = new FileInputStream(file)) {
            existing = new Yaml().load(in);
            if (existing == null) existing = new java.util.HashMap<>();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        merge(defaults, existing);

        Gson gson = new Gson();

        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);

        try (FileOutputStream out = new FileOutputStream(file)) {
            out.write(new Yaml(options).dump(existing).getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        String jsonString = gson.toJson(existing);
        JsonElement element = gson.fromJson(jsonString, JsonElement.class);
        if (!element.isJsonObject()) throw new RuntimeException("YAML root is not an object!");
        config = element.getAsJsonObject();
    }

    private void merge(Map<String, Object> defaults, Map<String, Object> target) {
        for (Map.Entry<String, Object> entry : defaults.entrySet()) {
            String key = entry.getKey();
            Object defaultVal = entry.getValue();

            if (!target.containsKey(key)) {
                target.put(key, defaultVal);
            } else if (defaultVal instanceof Map && target.get(key) instanceof Map) {
                merge((Map<String, Object>) defaultVal, (Map<String, Object>) target.get(key));
            }
        }
    }

    public JsonObject getConfig() {
        return config;
    }

    public void saveConfig() {
        if (file == null || config == null) return;

        Gson gson = new Gson();
        Map<String, Object> map = (Map<String, Object>) gson.fromJson(config, Map.class);

        try (FileOutputStream out = new FileOutputStream(file)) {
            String yaml = new Yaml().dump(map);
            out.write(yaml.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void reloadConfig() {
        if (file == null) return;

        Map<String, Object> existing;
        try (InputStream in = new FileInputStream(file)) {
            existing = new Yaml().load(in);
            if (existing == null) existing = new java.util.HashMap<>();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Gson gson = new Gson();
        String jsonString = gson.toJson(existing);
        JsonElement element = gson.fromJson(jsonString, JsonElement.class);
        if (!element.isJsonObject()) throw new RuntimeException("YAML root is not an object!");
        config = element.getAsJsonObject();
    }
}