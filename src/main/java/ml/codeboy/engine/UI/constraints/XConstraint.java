package ml.codeboy.engine.UI.constraints;

import ml.codeboy.engine.Game;
import ml.codeboy.engine.UI.UIObject;

public class XConstraint implements UIConstraint {
    private final double percentage;

    public XConstraint(double percentage) {
        this.percentage = percentage;
    }

    @Override
    public void resize(UIObject object) {
        object.setX(Game.get().getWidth() * percentage);
    }
}
