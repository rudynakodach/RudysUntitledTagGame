package io.github.rudynakodach.rudysuntitledtaggame.Events;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import static io.github.rudynakodach.rudysuntitledtaggame.RudysUntitledTagGame.isGameOn;

public class PlayerInventoryHandler implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if(!isGameOn) {return;}
        if(e.getCurrentItem() == null) {return;}
        if (e.getCurrentItem().getItemMeta() == null) {return;}
        if(!e.getCurrentItem().getItemMeta().hasCustomModelData()) {return;}

        if(e.getCurrentItem().getItemMeta().getCustomModelData() == 2137) {
            ((Player) e.getWhoClicked()).playSound(e.getWhoClicked(), Sound.UI_BUTTON_CLICK, 2, 2);
            e.setCancelled(true);
        }
    }
}
