package hu.jgj52.Sulfur.Commands;

import com.google.gson.JsonObject;
import hu.jgj52.Sulfur.Utils.LoadedPlugin;
import hu.jgj52.Sulfur.Sulfur;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.Argument;
import net.minestom.server.command.builder.arguments.ArgumentType;

public class VersionCommand extends Command {
    public VersionCommand() {
        super("version", "ver");

        JsonObject messages = Sulfur.conf.get("messages").getAsJsonObject();

        setDefaultExecutor((sender, _) -> {
            sender.sendMessage(MiniMessage.miniMessage().deserialize(messages.get("notEnoughArgsInVersionCommand").getAsString()));
        });

        Argument<String> arg = ArgumentType.String("plugin");
        addSyntax((sender, context) -> {
            LoadedPlugin plugin = Sulfur.loadedPlugins.get(context.get(arg));
            if (plugin == null) {
                sender.sendMessage(MiniMessage.miniMessage().deserialize(messages.get("pluginNotFound").getAsString(), Placeholder.parsed("plugin", context.get(arg))));
                return;
            }
            sender.sendMessage(plugin.getMessage());
        }, arg);
    }
}
