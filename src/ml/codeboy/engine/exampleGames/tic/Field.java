package ml.codeboy.engine.exampleGames.tic;

import ml.codeboy.engine.Game;
import ml.codeboy.engine.UI.Button;
import ml.codeboy.engine.UI.ButtonGroup;
import ml.codeboy.engine.UI.UIObject;

import java.util.ArrayList;

public class Field extends UIObject {

    private final ArrayList<short[][]>history=new ArrayList<>();
    private short [][]field=new short[3][3];


    private ArrayList<TicButton>buttons=new ArrayList<>();

    private int currentPlayer=1;

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public Field(){
        setInteractable(false);
        setPosition(Game.get().getMiddleOfWindow());
        setSize(Game.get().getHeight());
        for (int i = 0; i < 9; i++) {
            int y=i%3,x=(i-y)/3;
            TicButton button=new TicButton(x,y,this);
            buttons.add(button);
        }
        repositionButtons();
    }

    private void repositionButtons(){
        for (int i = 0; i < buttons.size(); i++) {
            int y=i%3,x=(i-y)/3;
            TicButton button=buttons.get(i);
            button.setWidthAndHeight(getWidth()/3,getHeight()/3);
            button.setPosition((int) (getLeftX()+(x+0.5)*button.getWidth()), (int) (getTopY()+(y+0.5)*button.getHeight()));
        }
    }

    private void repaintButtons(){
        for (TicButton b : buttons) {
            b.setText(getStringAt(b.getXValue(), b.getYValue()));
        }
    }

    public String getStringAt(int x, int y){
        return format(getAt(x,y));
    }

    public int getAt(int x, int y){
        return field[x][y];
    }

    @Override
    public void destroy() {
        super.destroy();
        for (Button b:buttons)
            b.destroy();
    }

    private String format(int i){
        switch (i){
            case 1:
                return "X";
            case -1:
                return "O";
            default:
                return "  ";
        }
    }


    public String toString() {
        StringBuilder f= new StringBuilder();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                f.append(getStringAt(i, j));
            }
            f.append("\n");
        }
        return f.toString();
    }

    public void play(int x,int y,int player){
        saveToHistory();
        field[x][y]= (short) player;
        currentPlayer*=-1;
        repaintButtons();
    }

    private void saveToHistory(){
        history.add(field);
    }

    public void undo(){
        field=history.remove(history.size()-1);
    }

    public int getWinner(){
        for (int i = -1; i < 3; i++) {
            if(i==0)
                continue;
            for (int j = 0; j < 3; j++) {
                if(field[j][0]==i&&field[j][1]==i&&field[j][2]==i)
                    return i;
                if(field[0][j]==i&&field[1][j]==i&&field[2][j]==i)
                    return i;
            }
            if(field[0][0]==i&&field[1][1]==i&&field[2][2]==i)
                return i;
            if(field[0][2]==i&&field[1][1]==i&&field[2][0]==i)
                return i;
        }
        return 0;
    }

    public ArrayList<Integer>getPossibleMoves(){
        ArrayList<Integer>list=new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if(getAt(i,j)==0)
                    list.add(j+i*3);
            }
        }
        return list;
    }

    public void move(int pos,int playerId){
        int y=pos%3,x=(pos-y)/3;
        saveToHistory();
        field[x][y]= (short) playerId;
    }
}
