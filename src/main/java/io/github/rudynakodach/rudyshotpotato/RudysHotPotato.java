package io.github.rudynakodach.rudyshotpotato;

import io.github.rudynakodach.rudyshotpotato.Commands.EliminateHandler;
import io.github.rudynakodach.rudyshotpotato.Commands.GameStartHandler;
import io.github.rudynakodach.rudyshotpotato.Commands.GameStopHandler;
import io.github.rudynakodach.rudyshotpotato.Events.PlayerDamageHandler;
import io.github.rudynakodach.rudyshotpotato.Events.PlayerDeathHandler;
import io.github.rudynakodach.rudyshotpotato.Events.PlayerMoveHandler;
import io.github.rudynakodach.rudyshotpotato.Modules.GameController;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class RudysHotPotato extends JavaPlugin {

    public static GameController gameController;
    public static boolean isGameOn = false;

    @Override
    public void onEnable() {

        PlayerDamageHandler playerDamageHandler = new PlayerDamageHandler();
        getServer().getPluginManager().registerEvents(playerDamageHandler, this);

        PlayerDeathHandler playerDeathHandler = new PlayerDeathHandler();
        getServer().getPluginManager().registerEvents(playerDeathHandler, this);

        PlayerMoveHandler playerMoveHandler = new PlayerMoveHandler();
        getServer().getPluginManager().registerEvents(playerMoveHandler, this);

        GameStartHandler startHandler = new GameStartHandler(this);
        Objects.requireNonNull(getCommand("startgame")).setExecutor(startHandler);

        GameStopHandler stopHandler = new GameStopHandler();
        Objects.requireNonNull(getCommand("stopgame")).setExecutor(stopHandler);

        EliminateHandler eliminateHandler = new EliminateHandler(this);
        Objects.requireNonNull(getCommand("eliminate")).setExecutor(eliminateHandler);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
