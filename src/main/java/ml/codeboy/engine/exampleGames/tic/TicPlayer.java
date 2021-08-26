package ml.codeboy.engine.exampleGames.tic;

public enum TicPlayer {
    EMPTY, CIRCLE, CROSS;

    @Override
    public String toString() {
        switch (this) {
            case CROSS:
                return "X";
            case CIRCLE:
                return "O";
            case EMPTY:
                return "  ";
            default:
                return "Error";
        }
    }

    public TicPlayer getNext() {
        if (this == EMPTY) {
            return EMPTY;
        } else {
            if (this == CIRCLE) return CROSS;
            return CIRCLE;
        }
    }
}
