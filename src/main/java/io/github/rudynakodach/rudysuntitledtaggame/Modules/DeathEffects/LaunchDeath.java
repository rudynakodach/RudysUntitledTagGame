package io.github.rudynakodach.rudysuntitledtaggame.Modules.DeathEffects;

import io.github.rudynakodach.rudysuntitledtaggame.Modules.GameManagement.GameController;
import org.bukkit.*;
import org.bukkit.Color;
import org.bukkit.entity.*;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class LaunchDeath implements DeathEffect {

    private final Player target;
    private final JavaPlugin plugin;
    private final GameController controller;
    int i = 0;

    public LaunchDeath(JavaPlugin plugin, Player player, GameController controller) {
        this.controller = controller;
        this.plugin = plugin;
        this.target = player;
        controller.isAwaitingExecution = true;

        startElimination();
    }

    public void startElimination() {
        i = 0;

        new BukkitRunnable() {
            @Override
            public void run() {
                if (i < 12) {
                    float p = .5F + (i * (.25F / 2));
                    target.getWorld().playSound(target.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 2F, p);
                } else if (i == 12) {
                    target.getWorld().playSound(target.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 2, 1.2F);
                    target.getWorld().playSound(target.getLocation(), Sound.ENTITY_BAT_TAKEOFF, 2, 1.2F);
                    target.setVelocity(new Vector(0, 4.5, 0));
                } else if (i == 24) {
                    controller.eliminatePlayer(target);
                    Location loc = target.getLocation();
                    target.getWorld().createExplosion(loc, 12, true, true);
                    loc.getWorld().spawn(loc, LightningStrike.class);

                    Firework firework = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
                    FireworkMeta meta = firework.getFireworkMeta();
                    FireworkEffect.Builder builder = FireworkEffect.builder();

                    builder.flicker(true)
                            .withFlicker()
                            .trail(true)
                            .with(FireworkEffect.Type.BALL_LARGE)
                            .withColor(Color.AQUA)
                            .withColor(Color.RED)
                            .withColor(Color.BLACK)
                            .withColor(Color.BLUE)
                            .withColor(Color.FUCHSIA)
                            .withColor(Color.GREEN)
                            .withColor(Color.ORANGE)
                            .withColor(Color.TEAL)
                            .withColor(Color.YELLOW)
                            .withColor(Color.NAVY);

                    meta.addEffect(builder.build());
                    meta.setPower(0);
                    firework.setFireworkMeta(meta);

                    controller.isAwaitingExecution = false;
                } else if(i > 24) {
                    this.cancel();
                }
                i += 1;
            }
        }.runTaskTimer(plugin, 6, 6);
    }
}
