package io.github.rudynakodach.rudysuntitledtaggame.Events;

import io.github.rudynakodach.rudysuntitledtaggame.Modules.GameManagement.GameController;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

import static io.github.rudynakodach.rudysuntitledtaggame.RudysUntitledTagGame.*;

public class PlayerMoveHandler implements Listener {
    private final JavaPlugin plugin;

    public PlayerMoveHandler(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if(isGameOn) {
            if(GameController.getInstance().isWarmup) {
                e.setCancelled(true);
            }
        }
    }
}
