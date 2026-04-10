package hu.jgj52.Sulfur.Permissions;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minestom.server.entity.Player;

import java.io.*;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class User {
    private static final Gson gson = new Gson();
    private static final Map<UUID, JsonObject> perms = new HashMap<>();

    public static void load(UUID uuid) {
        perms.computeIfAbsent(uuid, _ -> {
            File file = new File("permissions", uuid + ".json");
            if (!file.exists()) return new JsonObject();
            try (InputStream is = new FileInputStream(file)) {
                InputStreamReader reader = new InputStreamReader(is);
                return gson.fromJson(reader, JsonObject.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static void unload(UUID uuid) {
        perms.remove(uuid);
    }

    private final UUID uuid;
    private final JsonObject permissions;
    private final File file;

    public User(UUID uuid) {
        this.uuid = uuid;
        file = new File(Path.of("permissions",uuid + ".json").toUri());
        permissions = perms.computeIfAbsent(uuid, _ -> {
            if (!file.exists()) return new JsonObject();
            try (InputStream is = new FileInputStream(file)) {
                InputStreamReader reader = new InputStreamReader(is);
                return gson.fromJson(reader, JsonObject.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public User(Player player) {
        this(player.getUuid());
    }

    public UUID getUUID() {
        return uuid;
    }

    private void save() {
        if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(gson.toJson(permissions).getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean has(String permission) {
        if (checkPermission("*")) return true;
        for (String key : permissions.keySet()) {
            if (matchesWildcard(key, permission) && checkPermission(key)) {
                return true;
            }
        }
        return false;
    }

    private boolean matchesWildcard(String pattern, String input) {
        String[] patternParts = pattern.split("\\.");
        String[] inputParts = input.split("\\.");

        return matchParts(patternParts, inputParts, 0, 0);
    }

    private boolean matchParts(String[] pattern, String[] input, int pi, int ii) {
        if (pi == pattern.length && ii == input.length) return true;

        if (pi == pattern.length || ii == input.length) {
            return false;
        }

        String p = pattern[pi];

        if (p.equals("*")) {
            return matchParts(pattern, input, pi + 1, ii + 1);
        }

        if (p.equals(input[ii])) {
            return matchParts(pattern, input, pi + 1, ii + 1);
        }

        return false;
    }

    private boolean checkPermission(String key) {
        JsonElement element = permissions.get(key);
        if (element == null) return false;
        JsonObject perm = element.getAsJsonObject();
        JsonElement expires = perm.get("expires");
        if (expires == null) return true;
        long expire = expires.getAsLong();
        if (expire <= System.currentTimeMillis()) {
            permissions.remove(key);
            save();
            return false;
        }
        return true;
    }

    public Long expires(String permission) {
        JsonElement element = permissions.get(permission);
        if (element == null) return null;
        JsonObject perm = element.getAsJsonObject();
        JsonElement expires = perm.get("expires");
        if (expires == null) return null;
        return expires.getAsLong();
    }

    public void expires(String permission, long expires) {
        JsonElement element = permissions.get(permission);
        if (element == null) return;
        JsonObject perm = element.getAsJsonObject();
        perm.addProperty("expires", expires);
        save();
    }

    public void add(String permission, Long expires) {
        JsonObject perm = new JsonObject();
        if (expires != null) {
            perm.addProperty("expires", expires);
        }
        permissions.add(permission, perm);
        save();
    }

    public void add(String permission) {
        add(permission, null);
    }

    public void remove(String permission) {
        permissions.remove(permission);
        save();
    }
}
