package ml.codeboy.engine.exampleGames.rpg.GameObjects;

import ml.codeboy.engine.Game;
import ml.codeboy.engine.GameObject;

import java.awt.*;

public class Test extends GameObject {
    public Test(Game game) {
        super(game, SpriteType.Rectangle);
        setCollision(true);
        setSize(1);
    }

    public void setActive(boolean active) {
        setColor(active ? Color.green : Color.red);
    }


}
