package io.github.rudynakodach.rudyshotpotato.Events;

import io.github.rudynakodach.rudyshotpotato.Modules.RandomAssignReason;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import static io.github.rudynakodach.rudyshotpotato.RudysHotPotato.*;

public class PlayerDeathHandler implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        if(!isGameOn) {return;}

        Player victim = e.getPlayer();
        if(victim.getName().equals(gameController.playerToKill.getName())) {
            gameController.assignRandomIT(RandomAssignReason.PREVIOUS_RUNNER_DIED);
        }
    }
}
