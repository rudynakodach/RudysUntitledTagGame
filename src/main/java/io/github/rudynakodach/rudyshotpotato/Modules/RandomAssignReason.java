package io.github.rudynakodach.rudyshotpotato.Modules;

public enum RandomAssignReason {

    PREVIOUS_RUNNER_DIED(0),
    NEXT_ROUND(1),
    GAME_BEGIN(2);

    private final int value;

    RandomAssignReason(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
