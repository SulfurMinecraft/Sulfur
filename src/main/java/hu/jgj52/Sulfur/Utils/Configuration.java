package hu.jgj52.Sulfur.Utils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import hu.jgj52.Sulfur.Sulfur;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@SuppressWarnings("unchecked")
public abstract class Configuration {
    private final File file;
    private JsonObject config;
    public Configuration() {
        LoadedPlugin loaded = null;
        for (LoadedPlugin lp : Sulfur.loadedPlugins.values()) {
            if (lp.getPlugin() == getPlugin()) {
                loaded = lp;
                break;
            }
        }
        if (loaded == null) {
            file = null;
            return;
        }

        file = new File(loaded.getName(), getName() + ".yml");

        Map<String, Object> defaults;
        try (InputStream defIn = getPlugin().getClass().getClassLoader()
                .getResourceAsStream(getName() + ".yml")) {
            if (defIn == null) throw new RuntimeException(getName() + ".yml not in jar!");
            defaults = new Yaml().load(defIn);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (!file.exists()) {
            try (InputStream in = getPlugin().getClass().getClassLoader()
                    .getResourceAsStream(getName() + ".yml");
                 FileOutputStream out = new FileOutputStream(file)) {
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

    @SuppressWarnings("unchecked")
    public void saveConfig() {
        if (file == null || config == null) return;
        Gson gson = new Gson();
        Map<String, Object> map = (Map<String, Object>) gson.fromJson(config, Map.class);

        try (FileOutputStream out = new FileOutputStream(file)) {
            String yaml = new Yaml().dump(map);
            out.write(yaml.getBytes());
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

    public abstract String getName();
    public abstract Plugin getPlugin();
}
