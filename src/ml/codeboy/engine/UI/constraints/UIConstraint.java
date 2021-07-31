package ml.codeboy.engine.UI.constraints;

import ml.codeboy.engine.UI.UIObject;

/**
 * Can be used to make responsive ui using {@link ml.codeboy.engine.UI.UIObject}
 */
public interface UIConstraint {
    void resize(UIObject object);

    UIConstraint CENTER_HORIZONTALLY=new XConstraint(0.5d),
            CENTER_VERTICALLY=new YConstraint(0.5d),
            CENTER_BOTH=new CombinedConstraint(CENTER_VERTICALLY,CENTER_HORIZONTALLY);
}
