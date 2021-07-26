package ml.codeboy.engine.exampleGames.tic;

import ml.codeboy.engine.Game;
import ml.codeboy.engine.Layer;
import ml.codeboy.engine.Sprite;
import ml.codeboy.engine.UI.Button;

public class TicTacToe extends Game {
    private Field field;

    public TicTacToe() {
        super("TicTacToe");
    }

    @Override
    protected void initialise() {
        super.initialise();
        field=new Field();
        Button button=new Button("restart",this::restartGame);
        button.setPosition( (int) (getWidth()*0.9),getMiddleOfWindow().y);
        button.setWidthAndHeight((int)(getWidth()*0.2),(int)(getHeight()*0.1));
    }

    private void restartGame(){
        field.destroy();
        field=new Field();
    }



    public static void main(String[] args) {
        new TicTacToe();
    }


    public Field getField() {
        return field;
    }
}
