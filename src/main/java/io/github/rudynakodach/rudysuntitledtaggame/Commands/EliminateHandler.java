package io.github.rudynakodach.rudysuntitledtaggame.Commands;

import io.github.rudynakodach.rudysuntitledtaggame.Modules.GameManagement.GameController;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import static io.github.rudynakodach.rudysuntitledtaggame.RudysUntitledTagGame.*;

public class EliminateHandler implements CommandExecutor {
    private final JavaPlugin plugin;

    public EliminateHandler(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(!isGameOn) {
            sender.sendMessage(Component.text("Nie znaleziono uruchomionej gry.").color(NamedTextColor.RED));
            return true;
        }

        if(args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Nie podano nazwy gracza!");
            return true;
        }
        int deathMethod = -1;
        if(args.length >= 2) {
            try {
                deathMethod = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Death method not found.");
            }
        }

        Player target =  plugin.getServer().getPlayer(args[0]);

        if(target == null) {
            sender.sendMessage(ChatColor.RED + "Nie znaleziono gracza.");
            return true;
        }

        if(deathMethod != -1) {
            GameController.getInstance().forcefullyStartElimination(target, deathMethod);
        } else {
            GameController.getInstance().eliminatePlayer(target);
        }

        return false;
    }
}
