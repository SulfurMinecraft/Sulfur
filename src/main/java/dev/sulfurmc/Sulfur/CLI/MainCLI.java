package dev.sulfurmc.Sulfur.CLI;

import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandManager;
import net.minestom.server.command.ConsoleSender;

import java.util.Scanner;

public class MainCLI {

    public static void start() {

        CommandManager commandManager = MinecraftServer.getCommandManager();
        ConsoleSender consoleSender = commandManager.getConsoleSender();

        Thread consoleThread = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNextLine()) {
                commandManager.execute(consoleSender, scanner.nextLine().trim());
            }
        });
        consoleThread.setDaemon(true);
        consoleThread.start();

    }

}
