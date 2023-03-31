package io.github.rudynakodach.rudysuntitledtaggame.Events.PowerUps;

import io.github.rudynakodach.rudysuntitledtaggame.Modules.Events.RoundStart.RoundStartEventListener;
import io.github.rudynakodach.rudysuntitledtaggame.Modules.Events.RoundStart.RoundStartListeners;
import io.github.rudynakodach.rudysuntitledtaggame.Modules.GameManagement.GameController;
import io.github.rudynakodach.rudysuntitledtaggame.Modules.PowerUps.DoubleJump;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import static io.github.rudynakodach.rudysuntitledtaggame.RudysUntitledTagGame.isGameOn;

public class DoubleJumpInteractionEvent implements Listener, RoundStartEventListener {

    private final JavaPlugin plugin;
    HashMap<Player, Long> doubleJumpUsageMap = new HashMap<>();
    public DoubleJumpInteractionEvent(JavaPlugin plugin) {
        RoundStartListeners.registerRoundStartListener(this);
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if(!isGameOn) {return;}
        if(e.getItem() == null) {return;}
        GameController controller = GameController.getInstance();

        if(controller.isAwaitingExecution || controller.isWarmup) {return;}
        Player player = e.getPlayer();
        if(!doubleJumpUsageMap.containsKey(player)) {
            doubleJumpUsageMap.put(player, System.currentTimeMillis());
        } else {
            double currentTime = System.currentTimeMillis();
            double useTime = doubleJumpUsageMap.get(player);
            double delay = DoubleJump.DOUBLE_JUMP_DELAY;

            double timeRemaining = (useTime+delay)-currentTime;
            //still on a cooldown
            if(timeRemaining > 0) {
                player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BASS, 1.5F, .5F);
                long minutesLeft = TimeUnit.MILLISECONDS.toMinutes((long) timeRemaining);
                long secondsLeft = TimeUnit.MILLISECONDS.toSeconds((long) timeRemaining);
                long millisLeft = TimeUnit.MILLISECONDS.toMillis((long) timeRemaining) - (secondsLeft*1000+minutesLeft*60*1000);
                String timeLeft = String.format("%02d:%02d.%03d", minutesLeft, secondsLeft, millisLeft);
                e.getPlayer().sendMessage(Component.text("Musisz zaczekać ").color(NamedTextColor.RED).append(
                        Component.text(timeLeft).decorate(TextDecoration.BOLD).append(
                                Component.text(" przed używaniem tego.").decoration(TextDecoration.BOLD, false)
                        )
                ));
                return;
            }
        }

        if(e.getItem().getType() != Material.FEATHER) {
            return;
        }

        ItemStack stack = e.getItem();
        ItemMeta meta = stack.getItemMeta();
        NamespacedKey doubleJumpKey = new NamespacedKey(plugin, "doublejump");
        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();

        if(dataContainer.has(doubleJumpKey)) {
            doubleJumpUsageMap.put(player, System.currentTimeMillis());
            Vector oldSpeed = player.getVelocity();
            player.setVelocity(new Vector(
                    (oldSpeed.getX() > 0 ? oldSpeed.getX() + .2F : oldSpeed.getX() - .2F),
                    Math.min(oldSpeed.getY(), 0) + .5F,
                    (oldSpeed.getZ() > 0 ? oldSpeed.getZ() + .2F : oldSpeed.getZ() - .2F)));
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_BAT_TAKEOFF, 2, 2);
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_IRON_GOLEM_ATTACK, 2, 1);
            player.getWorld().playSound(player.getLocation(), Sound.BLOCK_PISTON_EXTEND, 2, 1);

            player.getWorld().spawnParticle(Particle.BLOCK_CRACK, player.getLocation(), 25, .5F, .5F, .5F, 1, Material.REDSTONE_BLOCK.createBlockData());

            Location playerLocation = player.getLocation();
            World world = player.getWorld();
            double radius = 2;
            double angleIncrement = Math.PI / 16.0;
            for (double angle = 0; angle < 2 * Math.PI; angle += angleIncrement) {
                double x = playerLocation.getX() + radius * Math.cos(angle);
                double y = playerLocation.getY();
                double z = playerLocation.getZ() + radius * Math.sin(angle);
                world.spawnParticle(Particle.REDSTONE, x, y, z, 1, new Particle.DustOptions(Color.WHITE, 3));
            }
        }
    }

    @Override
    public void roundStarted(GameController controller) {
        doubleJumpUsageMap.replaceAll((e, v) -> 0L);
    }
}
