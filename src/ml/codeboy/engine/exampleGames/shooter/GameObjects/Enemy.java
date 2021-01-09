package ml.codeboy.engine.exampleGames.shooter.GameObjects;

import ml.codeboy.engine.Game;
import ml.codeboy.engine.events.DestroyEvent;
import ml.codeboy.engine.exampleGames.shooter.Shooter;

public class Enemy extends Damageable{
    public static int count=0,killed=0;

    public Enemy(Game game) {
        this(game,100);
    }
    public Enemy(Game game,int speed) {
        super(game,SpriteType.Rectangle);
        this.speed=speed;
        setCollision(true);
        count++;
        setSize(10);
    }

    @Override
    protected void onDestruction(DestroyEvent event) {
        count--;
        killed++;
        ((Shooter)game).getPlayer().setCoins(((Shooter)game).getPlayer().getCoins()+1);
    }

    int speed;
    @Override
    protected void tick() {
        double vectorlength= Shooter.get().getPlayer().getPosition().distance(getPosition());
        double xDir= (deltaTime*speed*(Shooter.get().getPlayer().getX()-getX())/ vectorlength),
        yDir=  (deltaTime*speed*(Shooter.get().getPlayer().getY()-getY())/ vectorlength);
        addX(xDir);
        addY(yDir);
    }
}
