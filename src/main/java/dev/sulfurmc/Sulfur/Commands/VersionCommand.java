package dev.sulfurmc.Sulfur.Commands;

import com.google.gson.JsonObject;
import dev.sulfurmc.Sulfur.Utils.LoadedPlugin;
import dev.sulfurmc.Sulfur.Sulfur;
import dev.sulfurmc.Sulfur.Utils.SulfurCommand;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.minestom.server.command.builder.arguments.Argument;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;

public class VersionCommand extends SulfurCommand {
    public VersionCommand() {
        super("version", "ver");

        JsonObject messages = Sulfur.conf.get("messages").getAsJsonObject();

        setDefaultExecutor((sender, _) -> sender.sendMessage(MiniMessage.miniMessage().deserialize(messages.get("notEnoughArgsInVersionCommand").getAsString())));

        Argument<String> arg = ArgumentType.String("plugin");
        arg.setSuggestionCallback((_, _, suggestion) -> {
            for (String p : Sulfur.loadedPluginsByName.keySet()) {
                suggestion.addEntry(new SuggestionEntry(p));
            }
        });
        addSyntax((sender, context) -> {
            LoadedPlugin plugin = Sulfur.loadedPluginsByName.get(context.get(arg));
            if (plugin == null) {
                sender.sendMessage(MiniMessage.miniMessage().deserialize(messages.get("pluginNotFound").getAsString(), Placeholder.parsed("plugin", context.get(arg))));
                return;
            }
            sender.sendMessage(plugin.getMessage());
        }, arg);
    }
}
