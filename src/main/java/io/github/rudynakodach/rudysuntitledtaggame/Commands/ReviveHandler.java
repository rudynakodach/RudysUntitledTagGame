package io.github.rudynakodach.rudysuntitledtaggame.Commands;

import io.github.rudynakodach.rudysuntitledtaggame.Modules.GameManagement.GameController;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import static io.github.rudynakodach.rudysuntitledtaggame.RudysUntitledTagGame.isGameOn;

public class ReviveHandler implements CommandExecutor {
    private final JavaPlugin plugin;

    public ReviveHandler(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!isGameOn) {return true;}
        if(args.length == 0) {
            sender.sendMessage(Component.text("Player unspecified!").color(NamedTextColor.RED));
            return true;
        }
        Player target = plugin.getServer().getPlayer(args[0]);

        GameController controller = GameController.getInstance();
        controller.revivePlayer(target);
        return true;
    }
}
