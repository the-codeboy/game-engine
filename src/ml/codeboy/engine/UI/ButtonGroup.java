package ml.codeboy.engine.UI;

import ml.codeboy.engine.Game;
import ml.codeboy.engine.events.DestroyEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class ButtonGroup extends UIObject{
    private int spaceBetweenButtons=20,buttonHeight,buttonWidth,preferredButtonHeight=50;
    private final ArrayList<Button>buttons=new ArrayList<>();

    public void setShowOutline(boolean showOutline) {
        this.showOutline = showOutline;
    }

    private boolean showOutline=false;

    public void addButton(String text,Runnable onClick){
        buttons.add(new Button(text,onClick));
        recalculateValues();
    }

    public void addButtons(HashMap<String,Runnable>buttons){
        buttons.forEach(this::addButton);
    }

    public void clear(){
        for (Button button:buttons){
            button.destroy();
        }
        buttons.clear();
        recalculateValues();
    }

    public void removeButton(Button button){
        button.destroy();
        buttons.remove(button);
        recalculateValues();
    }

    private int getNumberOfButtons(){
        return buttons.size();
    }

    private void recalculateValues(){
        if(getNumberOfButtons()==0)
            return;
        buttonWidth=getWidth();
        buttonHeight=(getHeight()/getNumberOfButtons())-spaceBetweenButtons;
        if(buttonHeight>preferredButtonHeight)
            buttonHeight=preferredButtonHeight;
        int xPos=getX();
        for (int i = 0, buttonsSize = buttons.size(); i < buttonsSize; i++) {
            Button button = buttons.get(i);
            button.setWidthAndHeight(buttonWidth,buttonHeight);
            int y=(buttonHeight+spaceBetweenButtons)*i+buttonHeight/2+getY()-getHeight()/2;
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

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        for (Button b:
             getButtons()) {
            b.setVisible(visible);
        }
    }

    public ArrayList<Button> getButtons() {
        return buttons;
    }

    public ButtonGroup() {
        super();
    }

    @Override
    protected void onDestruction(DestroyEvent event) {
        for (Button button:buttons)
            button.destroy();
    }

    @Override
    public void customRender(Graphics2D g) {
        if(showOutline){
            g.setColor(Color.RED);
            g.drawRect(getX()-getWidth()/2,getY()-getHeight()/2,getWidth(),getHeight());
        }
    }
}
