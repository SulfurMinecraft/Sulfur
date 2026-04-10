package hu.jgj52.Sulfur.Permissions;

import java.util.ArrayList;
import java.util.UUID;

public class User {

    private final UUID uuid;
    private final ArrayList<String> permissions;
    private final boolean isOperator;

    public User(UUID uuid, ArrayList<String> permissions, Boolean isOperator) {

        this.uuid = uuid;
        this.permissions = permissions;
        this.isOperator = isOperator || permissions.contains("*");

    }

    public UUID getUUID() {

        return uuid;

    }

    public ArrayList<String> getPermissions() {

        return permissions;

    }

    public boolean isOp() {

        return isOperator;

    }

    public void addPermission(String permission) {

        if (permissions.contains(permission)) return;

        permissions.add(permission);
        // syncPermissions();

    }

    private void syncPermissions() {



    }

}
