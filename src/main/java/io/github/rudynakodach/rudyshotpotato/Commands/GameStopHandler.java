package io.github.rudynakodach.rudyshotpotato.Commands;

import io.github.rudynakodach.rudyshotpotato.Modules.StopReason;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static io.github.rudynakodach.rudyshotpotato.RudysHotPotato.*;

public class GameStopHandler implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(isGameOn) {
            gameController.stopGame(StopReason.STOPPED);
            isGameOn = false;
            sender.sendMessage(Component.text("Gra zatrzymana!").color(NamedTextColor.GREEN));
        } else {
            sender.sendMessage(Component.text("Nie znaleziono uruchomionej gry.").color(NamedTextColor.RED));
        }
        return true;
    }
}
