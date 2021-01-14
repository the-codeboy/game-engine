package ml.codeboy.engine.events;

import ml.codeboy.engine.Sprite;

public class DestroyEvent extends Event implements Cancelable{

    private boolean canceled=false;
    private final Sprite sprite;

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
        this.canceled=canceled;
    }
}
