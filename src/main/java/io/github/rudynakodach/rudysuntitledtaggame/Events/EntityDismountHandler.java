package io.github.rudynakodach.rudysuntitledtaggame.Events;

import io.github.rudynakodach.rudysuntitledtaggame.Modules.GameManagement.GameController;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.spigotmc.event.entity.EntityDismountEvent;

import static io.github.rudynakodach.rudysuntitledtaggame.RudysUntitledTagGame.*;

public class EntityDismountHandler implements Listener {

    @EventHandler
    public void onEntityDismount(EntityDismountEvent e) {
        if(isGameOn) {
            if(GameController.getInstance().isAwaitingExecution) {
                e.setCancelled(true);
            }
        }
    }
}
