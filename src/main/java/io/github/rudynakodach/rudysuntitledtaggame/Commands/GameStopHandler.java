package io.github.rudynakodach.rudysuntitledtaggame.Commands;

import io.github.rudynakodach.rudysuntitledtaggame.Modules.GameManagement.GameController;
import io.github.rudynakodach.rudysuntitledtaggame.Modules.GameManagement.GameStopReason;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static io.github.rudynakodach.rudysuntitledtaggame.RudysUntitledTagGame.*;

public class GameStopHandler implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(isGameOn) {
            GameController.getInstance().stopGame(GameStopReason.STOPPED);
            isGameOn = false;
            sender.sendMessage(Component.text("Gra zatrzymana!").color(NamedTextColor.GREEN));
        } else {
            sender.sendMessage(Component.text("Nie znaleziono uruchomionej gry.").color(NamedTextColor.RED));
        }
        return true;
    }
}
