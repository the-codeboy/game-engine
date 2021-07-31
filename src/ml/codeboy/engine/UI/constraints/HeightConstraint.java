package ml.codeboy.engine.UI.constraints;

import ml.codeboy.engine.Game;
import ml.codeboy.engine.UI.UIObject;

public class HeightConstraint implements UIConstraint{
    private final double percentage;

    public HeightConstraint(double percentage) {
        this.percentage = percentage;
    }

    @Override
    public void resize(UIObject object) {
        object.setHeight((int) (Game.get().getHeight()* percentage));
    }
}
