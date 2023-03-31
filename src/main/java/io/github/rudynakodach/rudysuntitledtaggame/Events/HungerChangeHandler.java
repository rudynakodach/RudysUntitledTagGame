package io.github.rudynakodach.rudysuntitledtaggame.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import static io.github.rudynakodach.rudysuntitledtaggame.RudysUntitledTagGame.*;
public class HungerChangeHandler implements Listener {
    @EventHandler
    public void onHungerLevelChange(FoodLevelChangeEvent e) {
        if(!(e.getEntity() instanceof Player)) {
            return;
        }
        if(isGameOn) {
            e.setFoodLevel(20);
        }
    }
}
