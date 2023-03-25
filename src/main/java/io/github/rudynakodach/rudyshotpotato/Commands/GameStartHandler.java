package io.github.rudynakodach.rudyshotpotato.Commands;

import io.github.rudynakodach.rudyshotpotato.Modules.GameController;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import static io.github.rudynakodach.rudyshotpotato.RudysHotPotato.*;

public class GameStartHandler implements CommandExecutor {
    private final JavaPlugin plugin;

    public GameStartHandler(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage("This command is intended to be used by a player!");
            return true;
        }

        if(!(args.length >= 1)) {
            sender.sendMessage(ChatColor.RED + "Round delay unspecified.");
            return true;
        }

        if(!(args.length >= 2)) {
            sender.sendMessage(ChatColor.RED + "World radius unspecified.");
            return true;
        }

        int delay = 0;
        try {
            delay = Integer.parseInt(args[0]);
        } catch (NumberFormatException ignored) {
            sender.sendMessage(ChatColor.RED + "Delay must be numeric!");
            return true;
        }

        int borderRadius = 0;
        try {
            borderRadius = Integer.parseInt(args[1]);
        } catch (NumberFormatException ignored) {
            sender.sendMessage(ChatColor.RED + "Border radius must be numeric!");
        }

        Player player = (Player) sender;

        if(isGameOn) {
            sender.sendMessage(Component.text("Cannot start a game when there already is one!").color(NamedTextColor.RED));
            return true;
        } else {
            sender.sendMessage(Component.text("Gra rozpoczęta!").color(NamedTextColor.GREEN));
            int finalDelay = delay;
            int finalBorderRadius = borderRadius;
            GameController controller = new GameController(plugin, player, finalDelay, finalBorderRadius);
            controller.startGame();
            gameController = controller;
        }

        return true;
    }
}
