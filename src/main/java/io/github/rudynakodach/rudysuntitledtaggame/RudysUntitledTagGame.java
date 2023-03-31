package io.github.rudynakodach.rudysuntitledtaggame;

import io.github.rudynakodach.rudysuntitledtaggame.Commands.EliminateHandler;
import io.github.rudynakodach.rudysuntitledtaggame.Commands.GameStartHandler;
import io.github.rudynakodach.rudysuntitledtaggame.Commands.GameStopHandler;
import io.github.rudynakodach.rudysuntitledtaggame.Commands.ReviveHandler;
import io.github.rudynakodach.rudysuntitledtaggame.Events.*;
import io.github.rudynakodach.rudysuntitledtaggame.Events.PowerUps.DoubleJumpInteractionEvent;
import io.github.rudynakodach.rudysuntitledtaggame.Events.PowerUps.PlayerPullerInteractionEvent;
import io.github.rudynakodach.rudysuntitledtaggame.Events.PowerUps.StunInteractionHandler;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class RudysUntitledTagGame extends JavaPlugin {

    public static boolean isGameOn = false;

    @Override
    public void onEnable() {

        PlayerInventoryHandler playerInventoryHandler = new PlayerInventoryHandler();
        getServer().getPluginManager().registerEvents(playerInventoryHandler, this);

        PlayerDamageHandler playerDamageHandler = new PlayerDamageHandler(this);
        getServer().getPluginManager().registerEvents(playerDamageHandler, this);

        PlayerDeathHandler playerDeathHandler = new PlayerDeathHandler();
        getServer().getPluginManager().registerEvents(playerDeathHandler, this);

        PlayerMoveHandler playerMoveHandler = new PlayerMoveHandler(this);
        getServer().getPluginManager().registerEvents(playerMoveHandler, this);

        ItemDropHandler itemDropHandler = new ItemDropHandler();
        getServer().getPluginManager().registerEvents(itemDropHandler, this);

        EntityDismountHandler playerEntityInteractHandler = new EntityDismountHandler();
        getServer().getPluginManager().registerEvents(playerEntityInteractHandler, this);

        HungerChangeHandler hungerChangeHandler = new HungerChangeHandler();
        getServer().getPluginManager().registerEvents(hungerChangeHandler, this);

        PlayerPullerInteractionEvent playerPullerInteractionEvent = new PlayerPullerInteractionEvent(this);
        getServer().getPluginManager().registerEvents(playerPullerInteractionEvent, this);

        StunInteractionHandler stunInteractionHandler = new StunInteractionHandler(this);
        getServer().getPluginManager().registerEvents(stunInteractionHandler, this);

        DoubleJumpInteractionEvent doubleJumpInteractionEvent = new DoubleJumpInteractionEvent(this);
        getServer().getPluginManager().registerEvents(doubleJumpInteractionEvent, this);

        GameStartHandler startHandler = new GameStartHandler(this);
        Objects.requireNonNull(getCommand("startgame")).setExecutor(startHandler);

        GameStopHandler stopHandler = new GameStopHandler();
        Objects.requireNonNull(getCommand("stopgame")).setExecutor(stopHandler);

        EliminateHandler eliminateHandler = new EliminateHandler(this);
        Objects.requireNonNull(getCommand("eliminate")).setExecutor(eliminateHandler);

        ReviveHandler reviveHandler = new ReviveHandler(this);
        Objects.requireNonNull(getCommand("revive")).setExecutor(reviveHandler);
    }

    @Override
    public void onDisable() {}
}
