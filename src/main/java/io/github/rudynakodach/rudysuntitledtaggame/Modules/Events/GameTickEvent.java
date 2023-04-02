package io.github.rudynakodach.rudysuntitledtaggame.Modules.Events;

import io.github.rudynakodach.rudysuntitledtaggame.Modules.GameManagement.GameController;

public class GameTickEvent {
    private final int currentTime;
    private final int roundDelay;
    private final GameController controller;

    public GameTickEvent(int currentTime, int roundDelay, GameController controller) {
        this.currentTime = currentTime;
        this.roundDelay = roundDelay;
        this.controller = controller;
    }

    public int getCurrentTime() {
        return currentTime;
    }

    public int getRoundDelay() {
        return roundDelay;
    }

    public GameController getController() {
        return controller;
    }

}
