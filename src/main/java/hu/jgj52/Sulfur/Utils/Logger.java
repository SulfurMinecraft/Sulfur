package hu.jgj52.Sulfur.Utils;

import static hu.jgj52.Sulfur.Sulfur.loadedPlugins;

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

        System.out.println("\u001B[33m[WARNING] [" + prefix + "] " + message);

    }

    public void error(Object message) {

        System.out.println("\u001B[31m[ERROR] [" + prefix + "] " + message);

    }

}
