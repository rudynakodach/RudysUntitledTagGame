package io.github.rudynakodach.rudysuntitledtaggame.Modules.Events.Generic;

import io.github.rudynakodach.rudysuntitledtaggame.Modules.GameManagement.GameController;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GameEventHandler {
    private static List<GameEventListener> listeners = new ArrayList<>();

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
}
