package ml.codeboy.engine;

import java.awt.*;

public class Camera extends GameObject{
    public Camera(Game game) {
        super(game,Sprite.SpriteType.Custom);
    }
    private Rectangle screen=new Rectangle(getX(),getY(),game.getWidth(),game.getHeight());

    @Override
    public void setChanged() {
        super.setChanged();
        screen=new Rectangle(getX(),getY(),game.getWidth(),game.getHeight());
    }

    public boolean isOnScreen(Sprite sprite){
        return sprite.intersects(screen);
    }

}
