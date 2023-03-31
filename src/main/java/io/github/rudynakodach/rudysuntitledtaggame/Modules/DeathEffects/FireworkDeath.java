package io.github.rudynakodach.rudysuntitledtaggame.Modules.DeathEffects;

import io.github.rudynakodach.rudysuntitledtaggame.Modules.GameManagement.GameController;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class FireworkDeath implements DeathEffect {
    private final Player target;
    private final JavaPlugin plugin;
    private final GameController controller;

    public FireworkDeath(JavaPlugin plugin, Player player, GameController controller) {
        this.controller = controller;
        this.plugin = plugin;
        this.target = player;
        controller.isAwaitingExecution = true;

        startElimination();
    }

    public void startElimination() {

        Firework firework = (Firework) target.getLocation().getWorld().spawnEntity(target.getLocation(), EntityType.FIREWORK);

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
        meta.setPower(2);
        firework.setFireworkMeta(meta);
        firework.addPassenger(target);

        new BukkitRunnable() {
            @Override
            public void run() {
                controller.eliminatePlayer(target);
                target.getWorld().spawn(target.getLocation(), LightningStrike.class);
                target.getWorld().createExplosion(target.getLocation(), 8);

                controller.isAwaitingExecution = false;
            }
        }.runTaskLater(plugin, 23);
    }
}
