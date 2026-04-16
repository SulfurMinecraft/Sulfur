package dev.sulfurmc.Sulfur.Commands;

import dev.sulfurmc.Sulfur.Utils.LoadedPlugin;
import dev.sulfurmc.Sulfur.Sulfur;
import dev.sulfurmc.Sulfur.Utils.SulfurCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

public class PluginsCommand extends SulfurCommand {
    public PluginsCommand() {
        super("plugins", "pl");

        setDefaultExecutor((sender, _) -> {
            Component plugins = MiniMessage.miniMessage().deserialize(
                    Sulfur.conf.get("messages").getAsJsonObject().get("plugins").getAsString(),
                    Placeholder.parsed("count", String.valueOf(Sulfur.loadedPlugins.size() + Sulfur.disabledPlugins.size()))
            );
            boolean first = true;
            for (LoadedPlugin plugin : Sulfur.loadedPlugins.values()) {
                Component p = Component.text(first ? "" : ", ").color(NamedTextColor.WHITE).append(
                        Component.text(plugin.getName())
                                .color(NamedTextColor.GREEN)
                                .clickEvent(ClickEvent.runCommand("/version " + plugin.getName()))
                                .hoverEvent(HoverEvent.showText(
                                        Component.text(plugin.getVersion())
                                                .color(plugin.getVersion().contains("SNAPSHOT") ? NamedTextColor.YELLOW : NamedTextColor.WHITE)
                                ))
                );
                plugins = plugins.append(p);
                first = false;
            }

            for (String plugin : Sulfur.disabledPlugins) {

                Component p = Component.text(first ? "" : ", ").color(NamedTextColor.WHITE).append(
                        Component.text(plugin)
                                .color(NamedTextColor.RED)
                                .hoverEvent(HoverEvent.showText(
                                        Component.text("Plugin disabled")
                                                .color(NamedTextColor.RED)
                                ))
                );
                plugins = plugins.append(p);

            }

            sender.sendMessage(plugins);
        });
    }
}