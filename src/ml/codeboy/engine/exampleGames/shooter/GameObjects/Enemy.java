package ml.codeboy.engine.exampleGames.shooter.GameObjects;

import ml.codeboy.engine.Damageable;
import ml.codeboy.engine.Game;
import ml.codeboy.engine.events.DestroyEvent;
import ml.codeboy.engine.exampleGames.shooter.Shooter;

import java.awt.*;

public class Enemy extends Damageable {
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

    private final Color[]colors={Color.WHITE,Color.LIGHT_GRAY,Color.GRAY,Color.DARK_GRAY,Color.YELLOW,Color.ORANGE,Color.cyan,Color.BLUE,Color.pink,Color.RED};

    private int level=1;

    public void setLevel(int level){
        if(level>=colors.length)
            level=colors.length-1;
        this.level=level;
        setSize(level*10);
        setLives(level);
        if(level>0)
            setColor(colors[level]);
        else setColor(colors[colors.length-1]);
    }

    @Override
    protected void onDestruction(DestroyEvent event) {
        count--;
        killed++;
        ((Shooter)game).getPlayer().setCoins((int) (((Shooter)game).getPlayer().getCoins()+level*1.75f));
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
