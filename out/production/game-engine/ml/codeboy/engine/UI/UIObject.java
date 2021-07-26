package ml.codeboy.engine.UI;

import ml.codeboy.engine.Game;
import ml.codeboy.engine.Input;
import ml.codeboy.engine.Layer;
import ml.codeboy.engine.Sprite;

import java.awt.*;
import java.util.Collections;
import java.util.List;

public class UIObject extends Sprite {
    protected Color background,foreground,textColor;
    protected boolean hasBorder,rounded;
    protected int borderSize;

    private boolean animated=false;

    @Override
    public void setInteractable(boolean interactable) {
        super.setInteractable(interactable);
        if(!interactable)
            setAnimated(false);
    }

    public boolean isAnimated() {
        return animated;
    }

    public void setAnimated(boolean animated) {
        this.animated = animated;
    }

    private String text="";
    protected float fontSize=11;
    protected List<String> lines=Collections.emptyList();

    public UIObject() {
        this(Game.get().getTheme());
    }

    UITheme theme=UITheme.DEFAULT;

    public UIObject setTheme(UITheme theme){
        this.theme=theme;
        background=theme.getBackground();
        foreground=theme.getForeground();
        textColor=theme.getTextColor();
        hasBorder=theme.hasBorder();
        borderSize=theme.getBorderSize();
        rounded=theme.isRounded();
        return this;
    }

    public UIObject setText(String text){
        this.text=text;
        lines= Collections.singletonList(text);
        Game.doNext(this::recalculateTextSize);
        return this;
    }

    public String getText(){
        return text;
    }

    public UIObject(UITheme theme) {
        super(SpriteType.Custom);
        setLayer(Layer.UI);
        setTheme(theme);
    }

    private double size=1;

    public void setMaxSize(double maxSize) {
        this.maxSize = maxSize;
    }

    private double maxSize=1.1;

    private int getWidthWithSize() {
        return (int) (super.getWidth()*size);
    }

    private int getHeightWithSize() {
        return (int) (super.getHeight()*size);
    }

//    @Override
//    public boolean isTouching(Point point) {
//        return getXDouble()-getWidth()/(float)2-borderSize<= point.getX()&&getYDouble()-getHeight()/(float)2<= point.getY()-borderSize&&
//                getXDouble()+getWidth()/(float)2+borderSize>= point.getX()&&getYDouble()+getHeight()/(float)2+borderSize>= point.getY();
//    }

    private Graphics2D g;

    @Override
    public void customRender(Graphics2D g) {
        this.g=g;
        if(isAnimated()){
            if (isTouching(Input.getMousePosition())) {
                if (size < maxSize)
                    size += Game.deltaTime(true);
                else size = maxSize;
            } else if (size > 1) size -= Game.deltaTime(true);
            else size = 1;
        }
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
        drawText();
        g.setColor(Game.get().getDefaultColor());
    }

    private void drawText(){
        if(g==null)
            return;
        g.setColor(textColor);
        g.setFont(g.getFont().deriveFont(fontSize));
        if(lines.size()==1)
            g.drawString(lines.get(0), getX()-(float)g.getFontMetrics().stringWidth(lines.get(0))/2, (float) (getY()+g.getFontMetrics().getStringBounds(text,g).getHeight()/2));
        else
        for (int i = 0; i < lines.size(); i++) {
            g.drawString(lines.get(i), getX()-(float)g.getFontMetrics().stringWidth(lines.get(0))/2, (float) (getY()-getHeightWithSize()/2+(i+1)*g.getFontMetrics().getStringBounds(text,g).getHeight()));
        }
    }

    float borderpercentage=0.95f;

    private void recalculateTextSize(){
        if(g==null) {
            Game.doNext(this::recalculateTextSize);
            return;
        }
        float changeSteps=1f;
        while(g.getFontMetrics().stringWidth(text) > getWidth() * borderpercentage || g.getFont().getStringBounds(text, g.getFontRenderContext()).getHeight() > getHeightWithSize() * borderpercentage){
            g.setFont(g.getFont().deriveFont(g.getFont().getSize2D()-changeSteps));
        }
        boolean sizeIncreased=false;
        while (g.getFontMetrics().stringWidth(text) < getWidth() * borderpercentage && g.getFont().getStringBounds(text, g.getFontRenderContext()).getHeight() < getHeightWithSize() * borderpercentage){
            g.setFont(g.getFont().deriveFont(g.getFont().getSize2D()+changeSteps));
            sizeIncreased=true;
        }
        if(sizeIncreased)
            g.setFont(g.getFont().deriveFont(g.getFont().getSize2D() - changeSteps));
        fontSize=g.getFont().getSize2D();
    }

    private void drawText(String text,Graphics2D g,int offset){
        StringBuilder newText= new StringBuilder();
        while (g.getFontMetrics().stringWidth(text)>getWidth()-10){
            String[]strings=text.split(" ");
            if(strings.length<2)
                break;
            newText.append(" ").append(strings[strings.length - 1]);
            text=text.replaceFirst(" "+strings[strings.length - 1],"");
        }
        if(offset==0&&newText.length()==0){
            g.drawString(text,getX()-(float)g.getFontMetrics().stringWidth(text)/2+5, (float) ((float) getYDouble()+(g.getFontMetrics().getStringBounds(text,g).getHeight()/2)));
            return;
        }
        g.drawString(text,getX()-(float)getWidthWithSize()/2+5, (float) (getY()-getHeightWithSize()/2+g.getFontMetrics().getStringBounds(text,g).getHeight()*++offset));
        if(newText.length()>0)
            drawText(newText.toString(),g,offset);
    }

    public void press(){}
}
