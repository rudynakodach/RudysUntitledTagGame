package io.github.rudynakodach.rudysuntitledtaggame.Modules.Events.Generic;

import io.github.rudynakodach.rudysuntitledtaggame.Modules.Events.GameTickEvent;
import io.github.rudynakodach.rudysuntitledtaggame.Modules.GameManagement.GameController;
import org.bukkit.entity.Player;

public interface GameEventListener {
    void onItChange(GameController controller, Player player);
    void onPlayerEliminated(GameController controller, Player player);
    void onRoundStart(GameController controller);
    void onPlayerRevived(GameController controller, Player player);
    void onGameEnded(GameController controller);
    void onGameTick(GameTickEvent event);
}
