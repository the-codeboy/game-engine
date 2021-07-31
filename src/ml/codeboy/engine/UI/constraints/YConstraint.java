package ml.codeboy.engine.UI.constraints;

import ml.codeboy.engine.Game;
import ml.codeboy.engine.UI.UIObject;

public class YConstraint implements UIConstraint{
    private final double percentace;

    public YConstraint(double percentage) {
        this.percentace = percentage;
    }

    @Override
    public void resize(UIObject object) {
        object.setY(Game.get().getHeight()*percentace);
    }
}
