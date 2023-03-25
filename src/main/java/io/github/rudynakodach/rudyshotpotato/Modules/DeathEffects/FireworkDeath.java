package io.github.rudynakodach.rudyshotpotato.Modules.DeathEffects;

import io.github.rudynakodach.rudyshotpotato.Modules.GameController;
import org.bukkit.*;
import org.bukkit.Color;
import org.bukkit.entity.*;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class FireworkDeath {

    private final Player target;
    private final JavaPlugin plugin;
    private final GameController controller;
    int i = 0;

    public FireworkDeath(JavaPlugin plugin, Player player, GameController controller) {
        this.controller = controller;
        this.plugin = plugin;
        this.target = player;
        controller.isAwaitingExecution = true;

        startElimination();
    }

    private void startElimination() {
        i = 0;
        new BukkitRunnable() {
            @Override
            public void run() {
                float p = .5F+(i*(.25F/2));
                target.getWorld().playSound(target.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 2F, p);

                if(i >= 12) {
                    this.cancel();
                }

                i += 1;
            }
        }.runTaskTimer(plugin, 6, 6);

        new BukkitRunnable(){
            @Override
            public void run() {
                target.getWorld().playSound(target.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 2, 1.2F);
                target.getWorld().playSound(target.getLocation(), Sound.ENTITY_BAT_TAKEOFF, 2, 1.2F);
                target.setVelocity(new Vector(0, 5, 0));
            }
        }.runTaskLater(plugin, 12*10);

        new BukkitRunnable() {
            @Override
            public void run() {
                Location loc = target.getLocation();
                target.getWorld().createExplosion(loc, 4*3, true, true);
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

                Player eliminated = controller.playerToKill;
                controller.eliminatePlayer(eliminated);
                if(controller.playersAlive.size() > 1) {
                    eliminated.setGameMode(GameMode.SPECTATOR);
                }

                controller.isAwaitingExecution = false;
            }
        }.runTaskLater(plugin, 12*14);

        i = 0;
    }
}
