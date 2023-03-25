package io.github.rudynakodach.rudyshotpotato.Modules;

public enum StopReason {
    GAME_END(0),
    STOPPED(1);

    private final int value;

    StopReason(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
