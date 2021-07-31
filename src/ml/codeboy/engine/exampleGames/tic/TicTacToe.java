package ml.codeboy.engine.exampleGames.tic;

import ml.codeboy.engine.Game;
import ml.codeboy.engine.UI.Button;
import ml.codeboy.engine.UI.constraints.HeightConstraint;
import ml.codeboy.engine.UI.constraints.WidthConstraint;
import ml.codeboy.engine.UI.constraints.XConstraint;
import ml.codeboy.engine.UI.constraints.YConstraint;

import java.awt.*;

public class TicTacToe extends Game {
    private Field field;

    public TicTacToe() {
        super("TicTacToe", new Dimension(500, 500));
    }

    @Override
    protected void initialise() {
        field = new Field();
        Button button = new Button("restart", this::restartGame);
        button.addConstraints(new YConstraint(0.6),
                new XConstraint(0.85),
                new WidthConstraint(0.2),
                new HeightConstraint(0.1));
        button.setDepth(0);
        button = new Button("undo", this::undoMove);
        button.addConstraints(new YConstraint(0.4),
                new XConstraint(0.85),
                new WidthConstraint(0.2),
                new HeightConstraint(0.1));
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
