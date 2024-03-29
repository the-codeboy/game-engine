package ml.codeboy.engine.exampleGames.shooter.GameObjects;

import ml.codeboy.engine.Damageable;
import ml.codeboy.engine.Game;
import ml.codeboy.engine.GameObject;
import ml.codeboy.engine.events.DestroyEvent;

public class Bullet extends Damageable {
    public static int count = 0;
    double dirX, dirY;
    double speed = 20;

    public Bullet(Game game, double dirX, double dirY) {
        super(game, SpriteType.Circle);
        game.getScheduler().scheduleTask(this::destroy, 3);
        setSize(5);
        this.dirX = dirX;
        this.dirY = dirY;
        listenForCollision(Enemy.class);
        count++;
    }

    @Override
    protected void onDestruction(DestroyEvent event) {
        count--;
    }

    @Override
    protected void onCollision(GameObject other) {
        if (other instanceof Enemy) {
            ((Enemy) other).attack(this);
            attack(other);
        }
    }

    @Override
    protected void tick() {
        addX(dirX * (deltaTime * speed));
        addY(dirY * (deltaTime * speed));
    }


//    @Override
//    public void render(Graphics2D g) {
//        g.drawOval(getX()-getSize()/2,getY()-getSize()/2,getSize(),getSize());
//    }
}
