package ml.codeboy.engine.exampleGames.tic;

import ml.codeboy.engine.Game;
import ml.codeboy.engine.Layer;
import ml.codeboy.engine.Sprite;
import ml.codeboy.engine.UI.Button;

public class TicTacToe extends Game {
    private Field field;
    private Button resetButton;

    public TicTacToe() {
        super("TicTacToe");
    }

    @Override
    protected void initialise() {
        field=new Field();
        resetButton=new Button("restart",this::restartGame);
        resetButton.setPosition( (int) (getWidth()*0.9),getMiddleOfWindow().y);
        resetButton.setWidthAndHeight((int)(getWidth()*0.2),(int)(getHeight()*0.1));
        resetButton.setDepth(40);
        resetButton=new Button("undo",this::undoMove);
        resetButton.setPosition( (int) (getWidth()*0.9), (int) ( getMiddleOfWindow().y-getHeight()*0.1));
        resetButton.setWidthAndHeight((int)(getWidth()*0.2),(int)(getHeight()*0.1));
        resetButton.setDepth(40);
        setInitialised();
    }

    private void undoMove(){
        field.undo();
    }

    private void restartGame(){
        field.destroy();
        field=new Field();
        resetButton.setLayer(Layer.UI);
    }

    public Field getField() {
        return field;
    }
}
