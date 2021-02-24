package ml.codeboy.engine.exampleGames.rpg.GameObjects.characters;

import ml.codeboy.engine.Game;
import ml.codeboy.engine.GameObject;
import ml.codeboy.engine.Input;
import ml.codeboy.engine.Layer;
import ml.codeboy.engine.animation.State;
import ml.codeboy.engine.exampleGames.rpg.GameObjects.Item;
import ml.codeboy.engine.exampleGames.rpg.Rpg;

import java.awt.event.KeyEvent;

public class Player extends Character {

    public Player(Game game) {
        super("max.png",game);
        setSize(64);
        setLayer(Layer.MIDDLE);
        setAnimator(PlayerAnimator.getInstance());
        listenForCollision(Item.class);
    }

    @Override
    protected void onCollision(GameObject other) {
        if(other instanceof Item)
            ((Item) other).collect(getInventory());
    }

    public Rpg getGame(){
        return (Rpg) game;
    }

    int speed=100;

    @Override
    protected void tick() {
        addX(Input.horizontal()*speed*deltaTime);
        addY(Input.vertical()*speed*deltaTime);
        if(Input.horizontal()!=0||Input.vertical()!=0)
            setState(State.WALK);
        else setState(State.IDLE);
        if(Input.isKeyDown(KeyEvent.VK_R))
            rotate(deltaTime*100);
    }

    @Override
    protected void lateTick() {
        super.lateTick();
        game.getCamera().setPosition(getX()-(game.getWidth()/2),getY()-(game.getHeight()/2));
    }
}
