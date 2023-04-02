package io.github.rudynakodach.rudysuntitledtaggame.Commands;

import io.github.rudynakodach.rudysuntitledtaggame.Modules.GameManagement.GameController;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static io.github.rudynakodach.rudysuntitledtaggame.RudysUntitledTagGame.*;

public class GameStartHandler implements CommandExecutor, TabCompleter {
    private final JavaPlugin plugin;

    public GameStartHandler(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage("This command is intended to be used by a player!");
            return true;
        }

        if(isGameOn) {
            sender.sendMessage(ChatColor.RED + "A game is already on.");
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

        if(!(args.length >= 3)) {
            sender.sendMessage(ChatColor.RED + "Time indicator visibility unspecified.");
            return true;
        }

        if(!(args.length >= 4)) {
            sender.sendMessage(ChatColor.RED + "Colored glow effects unspecified.");
            return true;
        }

        int delay;
        try {
            delay = Integer.parseInt(args[0]);
        } catch (NumberFormatException ignored) {
            sender.sendMessage(ChatColor.RED + "Delay must be numeric!");
            return true;
        }

        int borderRadius;
        try {
            borderRadius = Integer.parseInt(args[1]);
        } catch (NumberFormatException ignored) {
            sender.sendMessage(ChatColor.RED + "Border radius must be numeric!");
            return true;
        }

        boolean isTimeLeftVisible = Boolean.parseBoolean(args[2]);
        boolean isGlowColored = Boolean.parseBoolean(args[3]);

        if(isGameOn) {
            sender.sendMessage(Component.text("Cannot start a game when there already is one!").color(NamedTextColor.RED));
            return true;
        } else {
            sender.sendMessage(Component.text("Game started!").color(NamedTextColor.GREEN));
            new GameController(plugin, player, delay, borderRadius, isTimeLeftVisible, isGlowColored);
            GameController.getInstance().startGame();
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> items = new ArrayList<>();
        if(args.length == 1) {
            items.add("10");
            items.add("15");
            items.add("30");
            items.add("45");
            items.add("60");
            items.add("90");
            return items;
        } else if(args.length == 2) {
            items.add("10");
            items.add("25");
            items.add("30");
            items.add("45");
            items.add("50");
            items.add("75");
            return items;
        } else if(args.length == 3) {
            items.add("true");
            items.add("false");
            return items;
        } else if(args.length == 4) {
            items.add("true");
            items.add("false");
            return items;
        } else {
            return null;
        }
    }
}
