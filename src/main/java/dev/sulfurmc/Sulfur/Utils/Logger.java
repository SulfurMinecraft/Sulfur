package dev.sulfurmc.Sulfur.Utils;

import static dev.sulfurmc.Sulfur.Sulfur.loadedPlugins;

public class Logger {

    private String prefix;

    public Logger(Plugin plugin) {

        if (plugin != null) this.prefix = loadedPlugins.get(plugin).getPrefix();

    }

    public Logger() {

        this.prefix = "?";

    }

    public void info(Object message) {

        System.out.println("[" + prefix + "] " + message);

    }

    public void warning(Object message) {

        System.out.println("\u001B[33m[WARNING] [" + prefix + "] " + message + "\033[0m");

    }

    public void error(Object message) {

        System.out.println("\u001B[31m[ERROR] [" + prefix + "] " + message + "\033[0m");

    }

    public Logger setPrefix(String prefix) {

        this.prefix = prefix;

        return this;

    }

}
