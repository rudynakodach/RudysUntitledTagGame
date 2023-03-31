package io.github.rudynakodach.rudysuntitledtaggame.Events.PowerUps;

import io.github.rudynakodach.rudysuntitledtaggame.Modules.Events.RoundStart.RoundStartEventListener;
import io.github.rudynakodach.rudysuntitledtaggame.Modules.Events.RoundStart.RoundStartListeners;
import io.github.rudynakodach.rudysuntitledtaggame.Modules.GameManagement.GameController;
import io.github.rudynakodach.rudysuntitledtaggame.Modules.PowerUps.ItInclusive.PlayerPuller;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import static io.github.rudynakodach.rudysuntitledtaggame.RudysUntitledTagGame.isGameOn;

public class PlayerPullerInteractionEvent implements Listener, RoundStartEventListener {

    private final JavaPlugin plugin;
    private final HashMap<Player, Long> playerPullerMap = new HashMap<>();

    public PlayerPullerInteractionEvent(JavaPlugin plugin) {
        RoundStartListeners.registerRoundStartListener(this);
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
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
        //if the item is not the one assigned as the "PlayerPuller"
        if (e.getItem().getType() != Material.GOLDEN_HOE) {
            return;
        }
        Player player = e.getPlayer();

        ItemStack item = e.getItem();
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(plugin, "przyciongacz");

        if (!dataContainer.has(key)) {
            return;
        }

        if (playerPullerMap.containsKey(player)) {
            long usage = playerPullerMap.get(player);
            long delay = PlayerPuller.PLAYER_PULLER_DELAY;
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

        World world = player.getWorld();
        world.playSound(player.getLocation(), Sound.ENTITY_BLAZE_HURT, 2, .5F);
        world.playSound(player.getLocation(), Sound.ENTITY_BLAZE_HURT, 2, 2);
        world.playSound(player.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 2, 1);
        world.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 2, 2);
        world.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 2, 2);

        RayTraceResult result = player.rayTraceEntities(75);

        int particleCount = 5;
        double particleSpacing = 0.25;
        double distanceToTarget = player.getLocation().distance(player.getLocation());
        Vector rayDirection = player.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();
        for (double i = 0; i < distanceToTarget; i += particleSpacing) {
            Location particleLocation = player.getLocation().add(rayDirection.clone().multiply(i));
            plugin.getLogger().log(Level.INFO, "Particle spawned at " + particleLocation);
            world.spawnParticle(Particle.FLAME, particleLocation, particleCount, 0.125, 0.125, 0.125, 0.0);
        }


        if (result != null) {
            Entity entity = result.getHitEntity();
            if (entity instanceof Player target) {
                Location locationToPull = player.getLocation();
                Vector pullDirection = locationToPull.toVector().subtract(target.getLocation().toVector()).normalize();
                target.setVelocity(pullDirection.multiply(2).add(new Vector(0, .5F, 0)));
            }
        }

        playerPullerMap.put(player, System.currentTimeMillis());
    }

    @Override
    public void roundStarted(GameController controller) {
        playerPullerMap.replaceAll((e, v) -> 0L);
    }
}
