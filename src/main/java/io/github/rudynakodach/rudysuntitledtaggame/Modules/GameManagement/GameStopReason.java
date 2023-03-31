package io.github.rudynakodach.rudysuntitledtaggame.Modules.GameManagement;

public enum GameStopReason {
    GAME_END(0),
    STOPPED(1);

    private final int value;

    GameStopReason(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
