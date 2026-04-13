package dev.sulfurmc.Sulfur.Utils;

import com.google.gson.*;

import java.io.*;
import java.sql.*;

import static dev.sulfurmc.Sulfur.Sulfur.ds;
import static dev.sulfurmc.Sulfur.Sulfur.local;

public class DataStore {
    private static final Gson gson = new Gson();

    private final String name;
    private final String path;
    private final JsonObject data;
    private File file;

    public DataStore(String name, String... parents) {
        this.name = name;
        if (local) {
            path = String.join(File.separator, parents);
            File dir = new File(path);
            if (!dir.exists()) dir.mkdirs();

            file = new File(dir, name + ".json");

            if (!file.exists()) {
                data = new JsonObject();
            } else {
                try (InputStream is = new FileInputStream(file)) {
                    InputStreamReader reader = new InputStreamReader(is);
                    JsonObject temp = gson.fromJson(reader, JsonObject.class);
                    data = temp != null ? temp : new JsonObject();
                } catch (IOException e) {
                    throw  new RuntimeException(e);
                }
            }
        } else {
            path = String.join("/", parents);
            try (Connection conn = ds.getConnection()) {
                String query = """
                        SELECT data
                        FROM sulfur_data
                        WHERE name = ? AND location = ?
                        """;

                try (PreparedStatement ps = conn.prepareStatement(query)) {
                    ps.setString(1, name);
                    ps.setString(2, path);

                    ResultSet rs = ps.executeQuery();

                    if (rs.next()) {
                        String json = rs.getString("data");
                        data = (json != null)
                                ? gson.fromJson(json, JsonObject.class)
                                : new JsonObject();
                    } else {
                        data = new JsonObject();
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public JsonObject get() {
        return data;
    }

    public void save() {
        try {
            String json = gson.toJson(data);
            if (local) {
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    fos.write(json.getBytes());
                }
            } else {
                try (Connection conn = ds.getConnection()) {
                    String query = """
                            INSERT INTO sulfur_data (name, location, data)
                            VALUES (?, ?, ?::jsonb)
                            ON CONFLICT (name, location)
                            DO UPDATE SET data = EXCLUDED.data;
                            """;

                    try (PreparedStatement ps = conn.prepareStatement(query)) {
                        ps.setString(1, name);
                        ps.setString(2, path);
                        ps.setString(3, gson.toJson(data));

                        ps.executeUpdate();
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
