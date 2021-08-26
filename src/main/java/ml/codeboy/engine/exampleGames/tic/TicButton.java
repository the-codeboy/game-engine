package ml.codeboy.engine.exampleGames.tic;

import ml.codeboy.engine.UI.Button;

public class TicButton extends Button {
    private int x, y;
    private Field field;

    public TicButton(int x, int y, Field field) {
        super("", () -> {
            field.play(x, y);
        });
        this.x = x;
        this.y = y;
        this.field = field;
        setAnimated(false);
    }

    public int getXValue() {
        return x;
    }

    public int getYValue() {
        return y;
    }
}
