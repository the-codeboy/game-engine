package ml.codeboy.engine;

import ml.codeboy.engine.Saving.GameVariables;
import ml.codeboy.engine.UI.ButtonGroup;
import ml.codeboy.engine.UI.UIText;
import ml.codeboy.engine.UI.UITheme;

public class TitleScreen extends Game {
    protected UIText title;
    protected ButtonGroup group;

    public TitleScreen(String name) {
        this(name, UITheme.DEFAULT);
    }

    public TitleScreen(String name, UITheme theme) {
        super(name, theme);
        setMaxFPS(30);
    }

    @Override
    protected void initialise() {
        group = new ButtonGroup(getWidth() / 2, (int) (getHeight() * 0.7), getWidth() / 4, getHeight() / 2, group);
        addTitle();
        setInitialised();
    }

    protected void addButton(String text, Runnable onClick) {
        if (group == null) {
            throw new IllegalStateException("TitleScreen not fully initialised yet please don't add buttons in constructor, and call super.initialise() in initialise method.");
        }
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
