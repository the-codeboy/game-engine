package ml.codeboy.engine;

import java.awt.*;

public class Camera extends GameObject {
    private Rectangle screen = new Rectangle(getX(), getY(), game.getWidth(), game.getHeight());
    private double zoom=1;

    public Camera(Game game) {
        super(game, Sprite.SpriteType.Custom);
        setWidthAndHeight(game.getWidth(),game.getHeight());
    }

    public double getZoom() {
        return zoom;
    }

    public void setZoom(double zoom) {
        this.zoom = zoom;
    }

    @Override
    protected void tick() {
        super.tick();
        if(hasChanged()) {
            screen = new Rectangle(getX(), getY(), getWidth(), getHeight());
            hasChanged = false;
        }
    }

    public boolean isOnScreen(Sprite sprite) {
        return sprite.intersects(screen);
    }

    public int getWidthOnScreen(Sprite sprite){
        return sprite.getWidth();
    }

    public int getHeightOnScreen(Sprite sprite){
        return sprite.getHeight();
    }

}
