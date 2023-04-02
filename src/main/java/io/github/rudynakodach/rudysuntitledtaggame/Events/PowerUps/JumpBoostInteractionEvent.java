package io.github.rudynakodach.rudysuntitledtaggame.Events.PowerUps;

import io.github.rudynakodach.rudysuntitledtaggame.Modules.Events.RoundStart.RoundStartEventListener;
import io.github.rudynakodach.rudysuntitledtaggame.Modules.Events.RoundStart.RoundStartListeners;
import io.github.rudynakodach.rudysuntitledtaggame.Modules.GameManagement.GameController;
import io.github.rudynakodach.rudysuntitledtaggame.Modules.PowerUps.ItInclusive.JumpBoost;
import io.github.rudynakodach.rudysuntitledtaggame.Modules.PowerUps.ItInclusive.PlayerPuller;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static io.github.rudynakodach.rudysuntitledtaggame.RudysUntitledTagGame.isGameOn;

public class JumpBoostInteractionEvent implements Listener, RoundStartEventListener {

    private final JavaPlugin plugin;
    HashMap<Player, Long> jumpBoostUsageMap = new HashMap<>();
    public JumpBoostInteractionEvent(JavaPlugin plugin) {
        RoundStartListeners.registerRoundStartListener(this);
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (!isGameOn) {
            return;
        }

        if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        GameController controller = GameController.getInstance();
        if (controller.isAwaitingExecution || controller.isWarmup) {
            return;
        }
        // if the item is null
        if (e.getItem() == null) {
            return;
        }
        if (e.getItem().getType() != Material.RABBIT_FOOT) {
            return;
        }
        Player player = e.getPlayer();

        ItemStack item = e.getItem();
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(plugin, "jumpboost");

        if (!dataContainer.has(key)) {
            return;
        }

        if (jumpBoostUsageMap.containsKey(player)) {
            long usage = jumpBoostUsageMap.get(player);
            long delay = JumpBoost.JUMP_BOOST_DELAY;
            long currentTime = System.currentTimeMillis();

            long timeRemaining = (usage + delay) - currentTime;
            if (timeRemaining > 0) {
                player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BASS, 1.5F, .5F);
                long minutesLeft = TimeUnit.MILLISECONDS.toMinutes(timeRemaining);
                long secondsLeft = TimeUnit.MILLISECONDS.toSeconds(timeRemaining);
                long millisLeft = TimeUnit.MILLISECONDS.toMillis(timeRemaining) - (secondsLeft * 1000 + minutesLeft * 60 * 1000);
                String timeLeft = String.format("%02d:%02d.%03d", minutesLeft, secondsLeft, millisLeft);
                e.getPlayer().sendMessage(Component.text("Musisz zaczekać ").color(NamedTextColor.RED).append(
                        Component.text(timeLeft).decorate(TextDecoration.BOLD).append(
                                Component.text(" przed używaniem tego.").decoration(TextDecoration.BOLD, false)
                        )
                ));
                return;
            }
        }

        player.getWorld().spawnParticle(Particle.FLASH, player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), 3, .5, .5, .5);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 2, 2);

        new BukkitRunnable() {
            int i = 0;
            @Override
            public void run() {
                if(i >= 3) {this.cancel();}

                player.getWorld().playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 2, .5F * i);

                i += 1;
            }
        }.runTaskTimer(plugin, 0, 2);

        new BukkitRunnable() {
            @Override
            public void run() {
                player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 40, 5));
            }
        }.run();
        jumpBoostUsageMap.put(player, System.currentTimeMillis());
    }

    @Override
    public void roundStarted(GameController controller) {
        jumpBoostUsageMap.replaceAll((a, k) -> 0L);
    }
}
