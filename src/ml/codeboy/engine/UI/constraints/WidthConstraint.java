package ml.codeboy.engine.UI.constraints;

import ml.codeboy.engine.Game;
import ml.codeboy.engine.UI.UIObject;

public class WidthConstraint implements UIConstraint{
    private final double percentace;

    public WidthConstraint(double percentage) {
        this.percentace = percentage;
    }

    @Override
    public void resize(UIObject object) {
        object.setWidth((int) (Game.get().getWidth()*percentace));
    }
}
