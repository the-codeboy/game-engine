package ml.codeboy.engine.events;

import ml.codeboy.engine.Sprite;

public class DestroyEvent extends Event implements Cancelable {

    private final Sprite sprite;
    private boolean canceled = false;

    public DestroyEvent(Sprite sprite) {
        this.sprite = sprite;
    }

    public Sprite getSprite() {
        return sprite;
    }

    @Override
    public boolean isCanceled() {
        return canceled;
    }

    @Override
    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }
}
