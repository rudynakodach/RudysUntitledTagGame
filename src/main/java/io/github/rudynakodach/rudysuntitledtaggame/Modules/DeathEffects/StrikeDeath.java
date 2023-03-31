package io.github.rudynakodach.rudysuntitledtaggame.Modules.DeathEffects;

import io.github.rudynakodach.rudysuntitledtaggame.Modules.GameManagement.GameController;
import org.bukkit.Sound;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class StrikeDeath implements DeathEffect {
    private final Player target;
    private final JavaPlugin plugin;
    private final GameController controller;

    public StrikeDeath(JavaPlugin plugin, Player player, GameController controller) {
        this.controller = controller;
        this.plugin = plugin;
        this.target = player;
        controller.isAwaitingExecution = true;

        startElimination();
    }

    public void startElimination() {
        controller.eliminatePlayer(target);

        target.getWorld().spawn(target.getLocation(), LightningStrike.class);

        target.getWorld().createExplosion(target.getLocation(), 8, true, true);

        target.getWorld().playSound(target.getLocation(), Sound.ITEM_TRIDENT_RETURN, 2, .5f);
        target.getWorld().playSound(target.getLocation(), Sound.ITEM_TRIDENT_THUNDER, 2, .5f);
        target.getWorld().playSound(target.getLocation(), Sound.ITEM_GOAT_HORN_SOUND_3, .5f, .5f);
        target.getWorld().playSound(target.getLocation(), Sound.ITEM_SHIELD_BREAK, 2, .5f);

        controller.isAwaitingExecution = false;
    }
}
