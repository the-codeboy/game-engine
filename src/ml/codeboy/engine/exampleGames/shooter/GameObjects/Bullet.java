package ml.codeboy.engine.exampleGames.shooter.GameObjects;

import ml.codeboy.engine.Game;
import ml.codeboy.engine.GameObject;
import ml.codeboy.engine.events.DestroyEvent;

import java.awt.*;

public class Bullet extends Damageable {
    double dirX,dirY;
    double speed=20;
    public static int count=0;
    public Bullet(Game game,double dirX,double dirY) {
        super(game);
        game.getScheduler().scheduleTask(this::destroy,3);
        setSize(5);
        this.dirX=dirX;
        this.dirY=dirY;
        listenForCollision(Enemy.class);
        count++;
    }

    @Override
    protected void onDestruction(DestroyEvent event) {
        count--;
    }

    @Override
    protected void onCollision(GameObject other) {
        if(other instanceof Enemy){
            ((Enemy) other).attack(this);
            attack(other);
        }
    }

    @Override
    protected void tick() {
        addX(dirX*(deltaTime*speed));
        addY(dirY*(deltaTime*speed));
    }

    @Override
    public void render(Graphics2D g) {
        g.drawOval(getX()-getSize()/2,getY()-getSize()/2,getSize(),getSize());
    }
}
