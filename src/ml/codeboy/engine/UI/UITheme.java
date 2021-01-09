package ml.codeboy.engine.UI;

import java.awt.*;
import java.util.ArrayList;

public class UITheme {

    public static UITheme DEFAULT =new UITheme(Color.DARK_GRAY,Color.LIGHT_GRAY,Color.BLACK,false,5,true),
            CRAZY=new UITheme(Color.YELLOW,Color.ORANGE,true),
    PLAIN_TEXT=new UITheme(new Color(0,0,0,0),Color.GRAY,false);

    private final Color background,foreground,textColor;
    private final int borderSize;
    private final boolean hasBorder;

    private final boolean rounded;

    public UITheme(Color background, Color foreground, Color textColor,boolean rounded, int borderSize, boolean hasBorder) {
        this.background = background;
        this.foreground = foreground;
        this.textColor = textColor;
        this.borderSize = borderSize;
        this.rounded = rounded;
        this.hasBorder = hasBorder;
    }

    public UITheme(Color foreground, Color textColor,boolean rounded) {
        this(Color.BLACK,foreground,textColor,rounded,0,false);
    }

    public Color getBackground() {
        return background;
    }

    public Color getForeground() {
        return foreground;
    }

    public Color getTextColor() {
        return textColor;
    }

    public boolean isRounded() {
        return rounded;
    }

    public int getBorderSize() {
        return borderSize;
    }

    public boolean hasBorder() {
        return hasBorder;
    }

    private ArrayList<UIObject>objects=new ArrayList<>();

    public void add(UIObject object){
        objects.add(object);
    }

    public void remove(UIObject object){
        objects.remove(object);
    }
}
