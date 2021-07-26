package ml.codeboy.engine;

import ml.codeboy.engine.UI.ButtonGroup;
import ml.codeboy.engine.UI.UIText;
import ml.codeboy.engine.UI.UITheme;

import java.awt.event.ComponentEvent;

public class TitleScreen extends Game {
    protected UIText title;
    protected ButtonGroup group;

    public TitleScreen(String name) {
        this(name, UITheme.DEFAULT);
    }

    public TitleScreen(String name, UITheme theme) {
        super(name, theme);
    }

    @Override
    protected void initialise() {
        super.initialise();
        group = new ButtonGroup(getWidth() / 2, (int) (getHeight() * 0.7), getWidth() / 4, getHeight() / 2, group);
        addTitle();
    }

    protected void addButton(String text, Runnable onClick) {
        group.addButton(text, onClick);
    }

    protected void addTitle() {
        if (title != null)
            title.destroy();
        title = new UIText(getName(), getWidth() / 2, getHeight() / 4, getWidth() / 2, getHeight() / 4);
    }

    @Override
    protected void displayStats(String[] toDisplay) {
    }

}
