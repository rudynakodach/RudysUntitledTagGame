package io.github.rudynakodach.rudysuntitledtaggame.Modules.DeathEffects;

import io.github.rudynakodach.rudysuntitledtaggame.Modules.GameManagement.GameController;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class RemoteDetonatorDeath implements DeathEffect {
    private final GameController controller;
    private final Player target;
    private final JavaPlugin plugin;

    public RemoteDetonatorDeath(JavaPlugin plugin, Player player, GameController controller) {
        this.controller = controller;
        this.target = player;
        this.plugin = plugin;

        controller.isAwaitingExecution = true;
        startElimination();
    }

    int i = 0;
    public void startElimination() {
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
        }.runTaskLater(plugin, 10 * 3 + 3);

        new BukkitRunnable() {
            @Override
            public void run() {
                controller.eliminatePlayer(target);
                target.getWorld().createExplosion(target.getLocation(), 8, true, true);
                controller.isAwaitingExecution = false;
            }
        }.runTaskLater(plugin, 10*3+3+5);
    }
}
