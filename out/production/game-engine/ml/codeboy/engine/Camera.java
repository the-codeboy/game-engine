package ml.codeboy.engine;

import java.awt.*;

public class Camera extends GameObject {
    private Rectangle screen = new Rectangle(getX(), getY(), game.getWidth(), game.getHeight());

    public Camera(Game game) {
        super(game, Sprite.SpriteType.Custom);
    }

    @Override
    public void setChanged() {
        super.setChanged();
        screen = new Rectangle(getX(), getY(), game.getWidth(), game.getHeight());
    }

    public boolean isOnScreen(Sprite sprite) {
        return sprite.intersects(screen);
    }

}
