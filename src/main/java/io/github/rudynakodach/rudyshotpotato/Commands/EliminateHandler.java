package io.github.rudynakodach.rudyshotpotato.Commands;

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

        if(args.length <= 1) {
            sender.sendMessage(ChatColor.RED + "Nie podano nazwy gracza!");
            return true;
        }

        Player target =  plugin.getServer().getPlayer(args[0]);

        if(target == null) {
            sender.sendMessage(ChatColor.RED + "Nie znaleziono gracza.");
            return true;
        }

        gameController.eliminatePlayer(target);

        return false;
    }
}
