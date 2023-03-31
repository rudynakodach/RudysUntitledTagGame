package io.github.rudynakodach.rudysuntitledtaggame.Modules.GameManagement;

public enum RandomAssignReason {

    PREVIOUS_IT_DIED(0),
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
