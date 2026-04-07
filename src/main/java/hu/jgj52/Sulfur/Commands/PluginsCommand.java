package hu.jgj52.Sulfur.Commands;

import hu.jgj52.Sulfur.Utils.LoadedPlugin;
import hu.jgj52.Sulfur.Sulfur;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;import net.minestom.server.command.builder.Command;

public class PluginsCommand extends Command {
    public PluginsCommand() {
        super("plugins", "pl");

        setDefaultExecutor((sender, _) -> {
            Component plugins = MiniMessage.miniMessage().deserialize(
                    Sulfur.conf.get("messages").getAsJsonObject().get("plugins").getAsString(),
                    Placeholder.parsed("count", String.valueOf(Sulfur.loadedPlugins.size()))
            );
            boolean first = true;
            for (LoadedPlugin plugin : Sulfur.loadedPlugins.values()) {
                Component p = Component.text(first ? "" : ", ").color(NamedTextColor.WHITE).append(
                        Component.text(plugin.getName())
                                .color(NamedTextColor.GREEN)
                                .clickEvent(ClickEvent.runCommand("/version " + plugin.getName()))
                                .hoverEvent(HoverEvent.showText(
                                        Component.text(plugin.getVersion())
                                                .color(plugin.getVersion().contains("SHAPSHOT") ? NamedTextColor.YELLOW : NamedTextColor.WHITE)
                                ))
                );
                plugins = plugins.append(p);
                first = false;
            }
            sender.sendMessage(plugins);
        });
    }
}
