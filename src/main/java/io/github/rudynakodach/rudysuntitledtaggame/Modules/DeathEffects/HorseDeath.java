package io.github.rudynakodach.rudysuntitledtaggame.Modules.DeathEffects;

import io.github.rudynakodach.rudysuntitledtaggame.Modules.GameManagement.GameController;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;
import java.util.logging.Level;

public class HorseDeath implements DeathEffect {

    private final Player target;
    private final JavaPlugin plugin;
    private final GameController controller;

    public HorseDeath(JavaPlugin plugin, Player player, GameController controller) {
        this.controller = controller;
        this.plugin = plugin;
        this.target = player;
        controller.isAwaitingExecution = true;

        startElimination();
    }

    @Override
    public void startElimination() {

        Horse horse = target.getWorld().spawn(target.getLocation(), Horse.class);
        horse.addPassenger(target);

        new BukkitRunnable() {
            int i = 0;
            final Random random = new Random();
            @Override
            public void run() {
                if(i >= 10) {
                    horse.getWorld().playSound(horse.getLocation(), Sound.ENTITY_HORSE_DEATH, 2, .5F);
                    horse.remove();
                    Location loc = target.getLocation();
                    controller.eliminatePlayer(target);
                    target.getWorld().createExplosion(loc, 12, true, true);

                    controller.isAwaitingExecution = false;
                    this.cancel();
                    return;
                }

                float randomFloat = 0.5f + random.nextFloat() * (1.5F);
                horse.getWorld().playSound(horse.getLocation(), Sound.ENTITY_HORSE_HURT, 2, randomFloat);
                i += 1;
            }
        }.runTaskTimer(plugin, 3, 3);
    }
}
