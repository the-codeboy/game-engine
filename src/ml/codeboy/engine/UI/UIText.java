package ml.codeboy.engine.UI;

import java.awt.*;

public class UIText extends UIObject {

    public UIText(String text, int x, int y, int width, int height) {
        this(text);
        setPosition(x, y);
        setWidthAndHeight(width, height);
    }

    public UIText(String text) {
        super(UITheme.PLAIN_TEXT);
        setText(text);
        setInteractable(false);
        setDepth(256);
    }

    public void setTextColor(Color color) {
        if (color == null)
            throw new IllegalArgumentException("Color can not be null");
        this.textColor = color;
    }
}
