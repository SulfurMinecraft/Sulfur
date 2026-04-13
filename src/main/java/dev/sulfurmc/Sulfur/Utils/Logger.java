package dev.sulfurmc.Sulfur.Utils;

import static dev.sulfurmc.Sulfur.Sulfur.loadedPlugins;

public class Logger {

    private String prefix;

    public Logger(Plugin plugin) {

        this.prefix = loadedPlugins.get(plugin).getPrefix();

        if (this.prefix == null || this.prefix.isEmpty()) {

            this.prefix = "?";

        }

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

}
