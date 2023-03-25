package io.github.rudynakodach.rudyshotpotato.Events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import static io.github.rudynakodach.rudyshotpotato.RudysHotPotato.*;

public class PlayerMoveHandler implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if(isGameOn) {
            if(gameController.isWarmup) {
                e.setCancelled(true);
            }
        }
    }
}
