package io.github.rudynakodach.rudysuntitledtaggame.Events;

import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

import static io.github.rudynakodach.rudysuntitledtaggame.RudysUntitledTagGame.isGameOn;

public class ItemDropHandler implements Listener {

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent e) {
        if(!isGameOn) {return;}
        if(!e.getItemDrop().getItemStack().getItemMeta().hasCustomModelData()) {return;}
        if(e.getItemDrop().getItemStack().getItemMeta().getCustomModelData() == 2137) {
            e.getPlayer().playSound(e.getPlayer(), Sound.UI_BUTTON_CLICK, 2, 2);
            e.setCancelled(true);
        }
    }
}
