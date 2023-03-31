package io.github.rudynakodach.rudysuntitledtaggame.Modules.Events.RoundStart;

import io.github.rudynakodach.rudysuntitledtaggame.Modules.GameManagement.GameController;

public interface RoundStartEventListener {
    void roundStarted(GameController controller);
}
