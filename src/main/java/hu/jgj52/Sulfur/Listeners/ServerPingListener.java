package hu.jgj52.Sulfur.Listeners;

import com.google.gson.JsonObject;
import hu.jgj52.Sulfur.Sulfur;
import hu.jgj52.Sulfur.Utils.Listeners.Event;
import hu.jgj52.Sulfur.Utils.Listeners.Listener;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.server.ServerListPingEvent;
import net.minestom.server.ping.Status;

import java.io.*;
import java.util.List;

public class ServerPingListener extends Listener {

    private byte[] favicon;

    public ServerPingListener() {

        File file = new File("server-icon.png");

        try (InputStream inputStream = new FileInputStream(file)) {

            this.favicon = inputStream.readAllBytes();

        } catch (FileNotFoundException e) {

            this.favicon = new byte[0];

        } catch (IOException e) {

            throw new RuntimeException(e);

        }

    }

    @Event
    public void onPing(ServerListPingEvent event) {
        JsonObject server = Sulfur.conf.get("server").getAsJsonObject();
        Status.PlayerInfo.Builder builder = Status.PlayerInfo.builder()
                .onlinePlayers(MinecraftServer.getConnectionManager().getOnlinePlayerCount())
                .maxPlayers(server.get("max-players").getAsInt());
        List<Player> first5 = MinecraftServer.getConnectionManager().getOnlinePlayers().stream()
                .limit(5)
                .toList();
        for (Player player : first5) {
            builder.sample(player.getName());
        }

        Status.Builder builder1 = Status.builder()
                .description(MiniMessage.miniMessage().deserialize(server.get("motd").getAsString()))
                .playerInfo(builder.build())
                .favicon(favicon);

        event.setStatus(
                builder1.build()
        );

    }
}
