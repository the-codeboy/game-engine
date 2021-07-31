package ml.codeboy.engine.UI.constraints;

import ml.codeboy.engine.Game;
import ml.codeboy.engine.UI.UIObject;

public class XConstraint implements UIConstraint {
    private final double percentace;

    public XConstraint(double percentage) {
        this.percentace = percentage;
    }

    @Override
    public void resize(UIObject object) {
        object.setX(Game.get().getWidth()*percentace);
    }
}
