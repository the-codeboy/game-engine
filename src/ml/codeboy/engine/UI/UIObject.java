package ml.codeboy.engine.UI;

import ml.codeboy.engine.Game;
import ml.codeboy.engine.Input;
import ml.codeboy.engine.Layer;
import ml.codeboy.engine.Sprite;

import java.awt.*;

public class UIObject extends Sprite {
    protected Color background,foreground,textColor;
    protected boolean hasBorder,rounded;
    protected int borderSize;

    protected String text="";

    public UIObject(Game game) {
        super(game, SpriteType.Custom);
        setLayer(Layer.UI);
        setTheme(UITheme.DEFAULT);
    }

    UITheme theme;

    public void setTheme(UITheme theme){
        this.theme=theme;
        background=theme.getBackground();
        foreground=theme.getForeground();
        textColor=theme.getTextColor();
        hasBorder=theme.hasBorder();
        borderSize=theme.getBorderSize();
        rounded=theme.isRounded();
    }

    public void setText(String text){
        this.text=text;
    }

    public String getText(){
        return text;
    }

    public UIObject(Game game, UITheme theme) {
        super(game, SpriteType.Custom);
        setLayer(Layer.UI);
        setTheme(theme);
    }

    private double size=1, maxSize=1.1;

    private int getWidthWithSize() {
        return (int) (super.getWidth()*size);
    }

    private int getHeightWithSize() {
        return (int) (super.getHeight()*size);
    }

    @Override
    public boolean isTouching(Point point) {
        return getXDouble()-getWidth()/(float)2-borderSize<= point.getX()&&getYDouble()-getHeight()/(float)2<= point.getY()-borderSize&&
                getXDouble()+getWidth()/(float)2+borderSize>= point.getX()&&getYDouble()+getHeight()/(float)2+borderSize>= point.getY();
    }

    @Override
    public void render(Graphics2D g) {
        if(isTouching(Input.getMousePosition()))
            if(size<maxSize)
                size+=Game.deltaTime(true);
            else size=maxSize;
            else if(size>1) size-=Game.deltaTime(true);
            else size=1;
        if(hasBorder) {
            g.setColor(background);
            if (rounded)
                g.fillRoundRect(getX()-getWidthWithSize()/2 - borderSize, getY()-getHeightWithSize()/2 - borderSize, getWidthWithSize() + borderSize*2, getHeightWithSize() + borderSize*2, 10, 10);
            else
                g.fillRect(getX()-getWidthWithSize()/2 - borderSize, getY()-getHeightWithSize()/2 - borderSize, getWidthWithSize() + borderSize*2, getHeightWithSize() + borderSize*2);
        }
        g.setColor(foreground);
        if(rounded)
            g.fillRoundRect(getX()-getWidthWithSize()/2,getY()-getHeightWithSize()/2,getWidthWithSize(),getHeightWithSize(),10,10);
        else
            g.fillRect(getX()-getWidthWithSize()/2,getY()-getHeightWithSize()/2,getWidthWithSize(),getHeightWithSize());
        drawText(text,g,0);
        g.setColor(Game.get().getDefaultColor());
    }

    int space=5;
    private void drawText(String text,Graphics2D g,int offset){
        if(offset>3)
            return;
        g.setColor(textColor);
        if(getWidth()<space*2)
            return;
        StringBuilder newText= new StringBuilder();
        while (g.getFontMetrics().stringWidth(text)>getWidth()-10){
            String[]strings=text.split(" ");
            if(strings.length<2)
                break;
            newText.append(" ").append(strings[strings.length - 1]);
            text=text.replaceFirst(" "+strings[strings.length - 1],"");
        }
        if(offset==0&&newText.length()==0){
            g.drawString(text,getX()-(float)getWidthWithSize()/2+5, (float) ((float) getYDouble()+(g.getFontMetrics().getStringBounds(text,g).getHeight()/2)));
            return;
        }
        g.drawString(text,getX()-(float)getWidthWithSize()/2+5, (float) (getY()-getHeightWithSize()/2+g.getFontMetrics().getStringBounds(text,g).getHeight()*++offset));
        if(newText.length()>0)
            drawText(newText.toString(),g,offset);
    }

    public void press(){}
}
