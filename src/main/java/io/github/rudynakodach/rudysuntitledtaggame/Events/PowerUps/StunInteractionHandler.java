package io.github.rudynakodach.rudysuntitledtaggame.Events.PowerUps;

import io.github.rudynakodach.rudysuntitledtaggame.Modules.Events.RoundStart.RoundStartEventListener;
import io.github.rudynakodach.rudysuntitledtaggame.Modules.Events.RoundStart.RoundStartListeners;
import io.github.rudynakodach.rudysuntitledtaggame.Modules.GameManagement.GameController;
import io.github.rudynakodach.rudysuntitledtaggame.Modules.PowerUps.RunnerInclusive.Stun;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static io.github.rudynakodach.rudysuntitledtaggame.RudysUntitledTagGame.isGameOn;

public class StunInteractionHandler implements Listener, RoundStartEventListener {

    private final JavaPlugin plugin;

    private final HashMap<Player, Long> stunDelayMap = new HashMap<>();
    public StunInteractionHandler(JavaPlugin plugin) {
        RoundStartListeners.registerRoundStartListener(this);
        this.plugin = plugin;
    }

    @EventHandler
    public void onStunInteraction(EntityDamageEvent event) {
        if(!isGameOn) {
            return;
        }
        if(!(event.getEntity() instanceof Player victim)) {
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
        if(attacker == gameController.playerToKill) {return;}
        if(victim != gameController.playerToKill) {return;}
        if(gameController.isAwaitingExecution) {
            return;
        } else if(gameController.isWarmup) {
            return;
        }


        ItemStack stack = attacker.getInventory().getItemInMainHand();
        if(stack.getType() == Material.AIR) {
            return;
        }

        ItemMeta meta = stack.getItemMeta();
        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(plugin, "stun");

        if(!dataContainer.has(key)) {
            return;
        }

        if (stunDelayMap.containsKey(attacker)) {
            long usage = stunDelayMap.get(attacker);
            long delay = Stun.STUN_DELAY;
            long currentTime = System.currentTimeMillis();

            long timeRemaining = (usage + delay) - currentTime;
            if (timeRemaining > 0) {
                attacker.playSound(attacker, Sound.BLOCK_NOTE_BLOCK_BASS, 1.5F, .5F);
                long minutesLeft = TimeUnit.MILLISECONDS.toMinutes(timeRemaining);
                long secondsLeft = TimeUnit.MILLISECONDS.toSeconds(timeRemaining);
                long millisLeft = TimeUnit.MILLISECONDS.toMillis(timeRemaining) - (secondsLeft * 1000 + minutesLeft * 60 * 1000);
                String timeLeft = String.format("%02d:%02d.%03d", minutesLeft, secondsLeft, millisLeft);
                attacker.sendMessage(Component.text("Musisz zaczekać ").color(NamedTextColor.RED).append(
                        Component.text(timeLeft).decorate(TextDecoration.BOLD).append(
                                Component.text(" przed używaniem tego.").decoration(TextDecoration.BOLD, false)
                        )
                ));
                return;
            }
        }

        if(victim == gameController.playerToKill) {
            victim.getWorld().playSound(victim.getLocation(), Sound.BLOCK_ANVIL_PLACE, 2, .5F);
            PotionEffect oldSlownessEffect = victim.getPotionEffect(PotionEffectType.SLOW);
            if(oldSlownessEffect != null) {
                int oldDuration = oldSlownessEffect.getDuration();
                gameController.playerToKill.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60 + oldDuration, 225));
            } else {
                gameController.playerToKill.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 225));
            }

            PotionEffect oldBlindnessEffect = victim.getPotionEffect(PotionEffectType.BLINDNESS);
            if(oldBlindnessEffect != null) {
                int oldDuration = oldBlindnessEffect.getDuration();
                gameController.playerToKill.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60 + oldDuration, 255));
            }
            gameController.playerToKill.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 225));
            stunDelayMap.put(attacker, System.currentTimeMillis());
        }
    }

    @Override
    public void roundStarted(GameController controller) {
        stunDelayMap.replaceAll((e, v) -> 0L);
    }
}
