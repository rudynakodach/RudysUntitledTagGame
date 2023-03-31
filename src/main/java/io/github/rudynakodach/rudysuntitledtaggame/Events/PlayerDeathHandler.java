package io.github.rudynakodach.rudysuntitledtaggame.Events;

import io.github.rudynakodach.rudysuntitledtaggame.Modules.GameManagement.GameController;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import static io.github.rudynakodach.rudysuntitledtaggame.RudysUntitledTagGame.*;

public class PlayerDeathHandler implements Listener {
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        if(!isGameOn) {return;}

        e.setCancelled(true);
        GameController.getInstance().eliminatePlayer(e.getPlayer());
    }
}
