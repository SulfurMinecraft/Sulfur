package dev.sulfurmc.Sulfur.Utils;

import dev.sulfurmc.Sulfur.Sulfur;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

import java.util.List;
import java.util.jar.JarFile;

public class LoadedPlugin {
    private String main;
    private String name;
    private List<String> authors;
    private String version;
    private List<String> depend;
    private Component message;
    private Plugin plugin;
    private JarFile jarFile;
    private String prefix;

    public LoadedPlugin() {
        this.main = "";
        this.name = "";
        this.authors = List.of();
        this.version = "";
        this.depend = List.of();
        this.message = Component.empty();
        this.plugin = null;
        this.jarFile = null;
        this.prefix = "";
    }

    public String getMain() {
        return main;
    }

    public void setMain(String main) {
        this.main = main;
        updateMessage();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        updateMessage();
    }

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
        updateMessage();
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
        updateMessage();
    }

    public List<String> getDepend() {
        return depend;
    }

    public void setDepend(List<String> depend) {
        this.depend = depend;
    }

    public Component getMessage() {
        return message;
    }

    public void setPrefix(String prefix) {

        this.prefix = prefix;

    }

    public String getPrefix() {

        return prefix;

    }

    public void updateMessage() {
        Component authorS = Component.empty();
        boolean first = true;
        for (String s : authors) {
            Component author = Component.text(first ? "" : ", ")
                    .color(NamedTextColor.WHITE)
                    .append(Component.text(s).color(NamedTextColor.GREEN));
            authorS = authorS.append(author);
            first = false;
        }
        this.message = MiniMessage.miniMessage().deserialize(
                Sulfur.conf.get("messages").getAsJsonObject().get("version").getAsString(),
                Placeholder.parsed("name", name),
                Placeholder.parsed("version", version),
                Placeholder.component("authors", authorS)
        );
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public void setPlugin(Plugin plugin) {
        this.plugin = plugin;
    }

    public JarFile getJarFile() {
        return jarFile;
    }

    public void setJarFile(JarFile jarFile) {
        this.jarFile = jarFile;
    }
}