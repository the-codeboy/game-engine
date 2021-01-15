package ml.codeboy.engine.UI;

import ml.codeboy.engine.Game;

import java.awt.*;
import java.util.ArrayList;

public class ButtonGroup extends UIObject{
    private int spaceBetweenButtons=20,buttonHeight,buttonWidth,preferredButtonHeight=50;
    private final ArrayList<Button>buttons=new ArrayList<>();

    public void addButton(String text,Runnable onClick){
        buttons.add(new Button(text,onClick));
        recalculateValues();
    }

    private int getNumberOfButtons(){
        return buttons.size();
    }

    private void recalculateValues(){
        buttonWidth=getWidth();
        buttonHeight=(getHeight()/getNumberOfButtons())-spaceBetweenButtons;
        if(buttonHeight>preferredButtonHeight)
            buttonHeight=preferredButtonHeight;
        int xPos=getX();
        for (int i = 0, buttonsSize = buttons.size(); i < buttonsSize; i++) {
            Button button = buttons.get(i);
            button.setWidthAndHeight(buttonWidth,buttonHeight);
            int y=(buttonHeight+spaceBetweenButtons)*i+buttonHeight/2+getY();
            button.setPosition(xPos,y);
        }
    }

    @Override
    public UIObject setTheme(UITheme theme) {
        super.setTheme(theme);
        if(buttons!=null)
        for (Button button:buttons){
            button.setTheme(theme);
        }
        return this;
    }

    public ButtonGroup(int x, int y, int width, int height) {
        this();
        setInteractable(false);
        setX(x);
        setY(y);
        setWidthAndHeight(width, height);
    }

    public ButtonGroup() {
        super();
    }

    @Override
    public void render(Graphics2D g) {

    }
}
