package ml.codeboy.engine.exampleGames.tic;

import ml.codeboy.engine.Game;
import ml.codeboy.engine.UI.Button;
import ml.codeboy.engine.UI.UIObject;
import ml.codeboy.engine.UI.UIText;

import java.awt.*;
import java.util.ArrayList;

public class Field extends UIObject {

    private final ArrayList<TicPlayer[][]> history = new ArrayList<>();
    private TicPlayer[][] field = new TicPlayer[3][3];


    private ArrayList<TicButton> buttons = new ArrayList<>();

    private UIText winnerText;

    private TicPlayer currentPlayer = TicPlayer.CIRCLE;

    public Field() {
        super();
        setInteractable(false);
        setPosition(Game.get().getMiddleOfWindow());
        setSize(Game.get().getHeight());
        for (int i = 0; i < 9; i++) {
            int y = i % 3, x = (i - y) / 3;
            field[x][y] = TicPlayer.EMPTY;
            TicButton button = new TicButton(x, y, this);
            buttons.add(button);
        }
        repositionButtons();
        saveToHistory();
    }

    public TicPlayer getCurrentPlayer() {
        return currentPlayer;
    }

    private void repositionButtons() {
        for (int i = 0; i < buttons.size(); i++) {
            int y = i % 3, x = (i - y) / 3;
            TicButton button = buttons.get(i);
            button.setWidthAndHeight(getWidth() / 3, getHeight() / 3);
            button.setPosition((int) (getX() - getWidth() / 2 + (x + 0.5) * button.getWidth()), (int) (getY() - getHeight() / 2 + (y + 0.5) * button.getHeight()));
        }
    }

    private void repaintButtons() {
        for (TicButton b : buttons) {
            b.setText(getStringAt(b.getXValue(), b.getYValue()));
        }
    }

    public String getStringAt(int x, int y) {
        return getAt(x, y).toString();
    }

    public TicPlayer getAt(int x, int y) {
        return field[x][y];
    }

    @Override
    public void destroy() {
        super.destroy();
        for (Button b : buttons)
            b.destroy();
        if (winnerText != null)
            winnerText.destroy();
    }


    public String toString() {
        StringBuilder f = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                f.append(getStringAt(i, j));
            }
            f.append("\n");
        }
        return f.toString();
    }

    public void play(int x, int y) {
        if (field[x][y] == TicPlayer.EMPTY && currentPlayer != TicPlayer.EMPTY) {
            saveToHistory();
            field[x][y] = currentPlayer;
            currentPlayer = currentPlayer.getNext();
            if (getWinner() != TicPlayer.EMPTY) {
                winnerText = new UIText(getWinner().toString().toUpperCase() + " won!");
                winnerText.setPosition(game.getMiddleOfWindow().x,game.getMiddleOfWindow().y);
                winnerText.setWidthAndHeight((int) (getWidth() * 0.2), (int) (getHeight() * 0.1));
                winnerText.setColor(Color.black);
                currentPlayer = TicPlayer.EMPTY;
            }
            repaintButtons();
        }
    }

    private void saveToHistory() {
        history.add(copyField());
    }

    public TicPlayer[][] copyField() {
        TicPlayer[][] myInt = new TicPlayer[field.length][];
        for (int i = 0; i < field.length; i++) {
            myInt[i] = new TicPlayer[field[i].length];
            System.arraycopy(field[i], 0, myInt[i], 0, field[i].length);
        }
        return myInt;
    }

    public boolean undo() {
        if (history.size() > 1 && winnerText == null) {
            field = history.remove(history.size() - 1);
            repaintButtons();
            currentPlayer = currentPlayer.getNext();
            return true;
        }
        return false;
    }

    public TicPlayer getWinner() {
        for (TicPlayer i : TicPlayer.values()) {
            if (i == TicPlayer.EMPTY)
                continue;
            for (int j = 0; j < 3; j++) {
                if (field[j][0] == i && field[j][1] == i && field[j][2] == i)
                    return i;
                if (field[0][j] == i && field[1][j] == i && field[2][j] == i)
                    return i;
            }
            if (field[0][0] == i && field[1][1] == i && field[2][2] == i)
                return i;
            if (field[0][2] == i && field[1][1] == i && field[2][0] == i)
                return i;
        }
        return TicPlayer.EMPTY;
    }

    public ArrayList<Integer> getPossibleMoves() {
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (getAt(i, j) == TicPlayer.EMPTY)
                    list.add(j + i * 3);
            }
        }
        return list;
    }

    public void move(int pos, TicPlayer playerId) {
        int y = pos % 3, x = (pos - y) / 3;
        saveToHistory();
        field[x][y] = playerId;
    }
}
