package io.github.rudynakodach.rudysuntitledtaggame.Modules.Events.Generic;

import io.github.rudynakodach.rudysuntitledtaggame.Modules.Events.GameTickEvent;
import io.github.rudynakodach.rudysuntitledtaggame.Modules.GameManagement.GameController;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GameEventHandler {
    private static final List<GameEventListener> listeners = new ArrayList<>();

    public static void registerListener(GameEventListener listener) {
        listeners.add(listener);
    }

    public static void sendItChangeEvent(GameController controller, Player player) {
        for (GameEventListener listener : listeners) {
            listener.onItChange(controller, player);
        }
    }

    public static void sendPlayerEliminatedEvent(GameController controller, Player target) {
        for (GameEventListener listener : listeners) {
            listener.onPlayerEliminated(controller, target);
        }
    }

    public static void sendRoundStartEvent(GameController controller) {
        for (GameEventListener listener : listeners) {
            listener.onRoundStart(controller);
        }
    }

    public static void sendPlayerReviveEvent(GameController controller, Player player) {
        for (GameEventListener listener : listeners) {
            listener.onPlayerRevived(controller, player);
        }
    }

    public static void sendGameEndEvent(GameController controller) {
        for (GameEventListener listener : listeners) {
            listener.onGameEnded(controller);
        }
    }

    public static void sendGameTickEvent(GameController controller) {
        for (GameEventListener listener : listeners) {
            listener.onGameTick(new GameTickEvent(controller.currentTime, controller.delay, controller));
        }
    }
}
