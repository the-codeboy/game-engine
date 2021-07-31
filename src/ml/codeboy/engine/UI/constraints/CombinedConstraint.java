package ml.codeboy.engine.UI.constraints;

import ml.codeboy.engine.UI.UIObject;

public class CombinedConstraint implements UIConstraint{
    private final UIConstraint[] constraints;

    public CombinedConstraint(UIConstraint... constraints) {
        this.constraints = constraints;
    }

    @Override
    public void resize(UIObject object) {
        for (UIConstraint constraint:constraints)
            constraint.resize(object);
    }
}
