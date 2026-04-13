package dev.sulfurmc.Sulfur.Permissions;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.sulfurmc.Sulfur.Utils.DataStore;
import net.minestom.server.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class User {
    private static final Map<UUID, DataStore> perms = new HashMap<>();

    public static void load(UUID uuid) {
        perms.computeIfAbsent(uuid, _ -> new DataStore(uuid.toString(), "permissions"));
    }

    public static void unload(UUID uuid) {
        perms.remove(uuid);
    }

    private final UUID uuid;
    private final DataStore store;
    private final JsonObject permissions;

    public User(UUID uuid) {
        this.uuid = uuid;
        store = perms.computeIfAbsent(uuid, _ -> new DataStore(uuid.toString(), "permissions"));
        permissions = store.get();
    }

    public User(Player player) {
        this(player.getUuid());
    }

    public UUID getUUID() {
        return uuid;
    }

    private void save() {
        store.save();
    }

    public boolean has(String permission) {
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
        while (pi < pattern.length && ii < input.length) {
            String p = pattern[pi];

            if (p.equals("*")) {
                return true;
            }

            if (!p.equals(input[ii])) {
                return false;
            }

            pi++;
            ii++;
        }

        return pi == pattern.length && ii == input.length;
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
