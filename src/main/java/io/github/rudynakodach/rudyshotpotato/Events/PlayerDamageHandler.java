package io.github.rudynakodach.rudyshotpotato.Events;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import static io.github.rudynakodach.rudyshotpotato.RudysHotPotato.*;
public class PlayerDamageHandler implements Listener {

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if(!(event.getEntity() instanceof Player)) {
            return;
        }
        if(!(event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK)) {
            return;
        }
        Entity attacker = ((EntityDamageByEntityEvent) event).getDamager();
        if(!(attacker instanceof Player)) {
            return;
        }
        if(!isGameOn) {
            return;
        }

        if(gameController.isAwaitingExecution) {
            event.setCancelled(true);
            return;
        }

        if(gameController.isWarmup) {
            event.setCancelled(true);
            return;
        }

        String attackerName = attacker.getName();
        Player victim = (Player)event.getEntity();

        if(gameController.playerToKill.getName().equalsIgnoreCase(attackerName)) {
            victim.playSound(attacker, Sound.ENTITY_ITEM_BREAK, 2, .75F);
            victim.sendMessage(Component.text("Gonisz!").color(NamedTextColor.AQUA).decorate(TextDecoration.BOLD));

            gameController.playerToKill.sendMessage(Component.text("Jesteś bezpieczny.").color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD));
            gameController.setPlayerToKill(victim);
        }

        event.setDamage(0);
    }
}
