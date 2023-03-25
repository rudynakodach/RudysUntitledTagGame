package io.github.rudynakodach.rudyshotpotato.Modules.DeathEffects;

import io.github.rudynakodach.rudyshotpotato.Modules.GameController;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class RemoteDetonatorDeath {

    private final GameController controller;
    private final Player target;
    private final JavaPlugin plugin;

    public RemoteDetonatorDeath(JavaPlugin plugin, Player target, GameController controller) {
        this.controller = controller;
        this.target = target;
        this.plugin = plugin;

        controller.isAwaitingExecution = true;
        startElimination();
    }

    int i = 0;
    private void startElimination() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if(i >= 2) {
                    this.cancel();
                }

                target.getWorld().playSound(target.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 2, 1F);

                i += 1;
            }
        }.runTaskTimer(plugin, 10, 10);

        new BukkitRunnable() {
            @Override
            public void run() {
                target.getWorld().playSound(target.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 2, 1.5F);
            }
        }.runTaskLater(plugin, 10 * 3 + 5);

        new BukkitRunnable() {
            @Override
            public void run() {
                controller.eliminatePlayer(target);
                target.getWorld().createExplosion(target.getLocation(), 12, true, true);
                controller.isAwaitingExecution = false;
            }
        }.runTaskLater(plugin, 10*3+5+5);
    }
}
