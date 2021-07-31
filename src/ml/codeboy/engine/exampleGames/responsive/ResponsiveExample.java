package ml.codeboy.engine.exampleGames.responsive;

import ml.codeboy.engine.Game;
import ml.codeboy.engine.UI.Button;
import ml.codeboy.engine.UI.UIText;
import ml.codeboy.engine.UI.constraints.HeightConstraint;
import ml.codeboy.engine.UI.constraints.UIConstraint;
import ml.codeboy.engine.UI.constraints.WidthConstraint;
import ml.codeboy.engine.UI.constraints.YConstraint;

import java.awt.*;

public class ResponsiveExample extends Game {
    public ResponsiveExample() {
        super("ResponsiveExample", new Dimension(1000, 1000));
    }

    @Override
    protected void initialise() {
        setInitialised();
        UIText text = (UIText) new UIText("ResponsiveExample").
                addConstraint(UIConstraint.CENTER_BOTH).
                addConstraint(new WidthConstraint(0.3)).
                addConstraint(new HeightConstraint(0.2));
        new Button("click me", () ->
        {
            text.setText(text.getText().equals("ResponsiveExample") ? "I don´t know why I did this" : "ResponsiveExample");
        }).
                addConstraint(UIConstraint.CENTER_HORIZONTALLY).
                addConstraint(new YConstraint(0.7)).
                addConstraint(new WidthConstraint(0.3)).
                addConstraint(new HeightConstraint(0.2));
    }
}
