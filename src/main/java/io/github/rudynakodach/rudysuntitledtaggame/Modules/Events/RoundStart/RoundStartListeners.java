package io.github.rudynakodach.rudysuntitledtaggame.Modules.Events.RoundStart;

import io.github.rudynakodach.rudysuntitledtaggame.Modules.GameManagement.GameController;

import java.util.ArrayList;
import java.util.List;

public class RoundStartListeners {
    private static final List<RoundStartEventListener> roundStartListeners = new ArrayList<>();

    public static void registerRoundStartListener(RoundStartEventListener listener) {
        roundStartListeners.add(listener);
    }

    public static void sendRoundStartEvent() {
        for (RoundStartEventListener listener : roundStartListeners) {
            listener.roundStarted(GameController.getInstance());
        }
    }
}
