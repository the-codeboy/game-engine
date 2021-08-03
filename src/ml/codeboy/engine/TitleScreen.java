package ml.codeboy.engine;

import ml.codeboy.engine.Saving.GameVariables;
import ml.codeboy.engine.UI.ButtonGroup;
import ml.codeboy.engine.UI.UIText;
import ml.codeboy.engine.UI.UITheme;
import ml.codeboy.engine.UI.constraints.HeightConstraint;
import ml.codeboy.engine.UI.constraints.UIConstraint;
import ml.codeboy.engine.UI.constraints.WidthConstraint;
import ml.codeboy.engine.UI.constraints.YConstraint;

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
        group.addConstraint(UIConstraint.CENTER_HORIZONTALLY).
                addConstraint(new YConstraint(0.7)).
                addConstraint(new WidthConstraint(0.25)).
                addConstraint(new HeightConstraint(0.5));
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
