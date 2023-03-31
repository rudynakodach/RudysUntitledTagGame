package io.github.rudynakodach.rudysuntitledtaggame.Events;

import io.github.rudynakodach.rudysuntitledtaggame.Modules.GameManagement.GameController;
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
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.logging.Level;

import static io.github.rudynakodach.rudysuntitledtaggame.RudysUntitledTagGame.*;
public class PlayerDamageHandler implements Listener {

    private final JavaPlugin plugin;

    public PlayerDamageHandler(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if(!(event.getEntity() instanceof Player victim)) {
            return;
        }
        if(!isGameOn) {
            return;
        }

        if(event.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) {
            event.setCancelled(true);
            return;
        } else if(event.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) {
            event.setCancelled(true);
            return;
        }

        if(event.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            return;
        }


        Entity entityAttacker = ((EntityDamageByEntityEvent) event).getDamager();
        if(!(entityAttacker instanceof Player attacker)) {
            return;
        }

        GameController gameController = GameController.getInstance();
        if(gameController.isAwaitingExecution) {
            plugin.getLogger().log(Level.INFO, "Can't attack during an execution!");
            event.setCancelled(true);
            return;
        }

        if(gameController.isWarmup) {
            plugin.getLogger().log(Level.INFO, "Can't attack during warmup!");
            event.setCancelled(true);
            return;
        }

        if(gameController.playerToKill == attacker) {
            long delay = GameController.HIT_DELAY;
            long currentTime = System.currentTimeMillis();
            HashMap<Player, Long> hitMap = GameController.getInstance().hitMap;
            if(hitMap.containsKey(attacker)) {
                long lastHit = hitMap.get(attacker);
                long timeLeft = (lastHit + delay) - currentTime;
                if(timeLeft > 0) {
                    event.setCancelled(true);
                    return;
                }
            }

            victim.playSound(attacker, Sound.BLOCK_BEACON_DEACTIVATE, 2, .75F);
            victim.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 255, false, false));
            victim.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 255, false, false));
            victim.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1, false, false));
            victim.sendMessage(Component.text("Koń!").color(NamedTextColor.RED).decorate(TextDecoration.BOLD));

            gameController.playerToKill.playSound(gameController.playerToKill, Sound.ENTITY_PLAYER_LEVELUP, 2, 2);
            gameController.playerToKill.removePotionEffect(PotionEffectType.SLOW);
            gameController.playerToKill.removePotionEffect(PotionEffectType.BLINDNESS);
            gameController.playerToKill.removePotionEffect(PotionEffectType.SPEED);
            gameController.playerToKill.sendMessage(Component.text("Jesteś bezpieczny.").color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD));

            gameController.setIT(victim);
            gameController.rearrangeTeams();
            hitMap.put(attacker, currentTime);
        }

        event.setDamage(0);
    }
}
