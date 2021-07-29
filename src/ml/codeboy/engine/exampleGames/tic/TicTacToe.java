package ml.codeboy.engine.exampleGames.tic;

import ml.codeboy.engine.Game;
import ml.codeboy.engine.Layer;
import ml.codeboy.engine.UI.Button;

public class TicTacToe extends Game {
    private Field field;

    public TicTacToe() {
        super("TicTacToe");
    }

    @Override
    protected void initialise() {
        field = new Field();
        Button button = new Button("restart", this::restartGame);
        button.setPosition((int) (getWidth() * 0.9), getMiddleOfWindow().y);
        button.setWidthAndHeight((int) (getWidth() * 0.2), (int) (getHeight() * 0.1));
        button.setDepth(0);
        button = new Button("undo", this::undoMove);
        button.setPosition((int) (getWidth() * 0.9), (int) (getMiddleOfWindow().y - getHeight() * 0.1));
        button.setWidthAndHeight((int) (getWidth() * 0.2), (int) (getHeight() * 0.1));
        button.setDepth(0);
        setInitialised();
    }

    private void undoMove() {
        field.undo();
    }

    private void restartGame() {
        field.destroy();
        field = new Field();
    }

    public Field getField() {
        return field;
    }
}
