package ml.codeboy.engine.exampleGames.shooter;

import ml.codeboy.engine.*;
import ml.codeboy.engine.UI.Button;
import ml.codeboy.engine.exampleGames.shooter.GameObjects.Bullet;
import ml.codeboy.engine.exampleGames.shooter.GameObjects.Enemy;
import ml.codeboy.engine.exampleGames.shooter.GameObjects.Player;
import ml.codeboy.engine.exampleGames.shooter.upgrades.*;

import java.util.Random;

public class Shooter extends Game {
    Player player;


    public Shooter() {
        super("Shooter");
    }

    Task spawnerTask;
    @Override
    protected void initialise() {
        player=new Player(this);
        getScheduler().scheduleTask(this::initUpgrades,0);
        spawnerTask=new Task(getScheduler()) {
            @Override
            protected void onCreation() {
                start();
            }

            @Override
            protected void run() {
                if(period>0.7)
                    period-=0.05;
                spawnEnemy();
            }
        };
        Button button=new Button(this,"||",this::togglePause);
        button.setPosition(getWidth()-50,50);
        button.setSize(20);
        unPause();
    }

    private void initUpgrades(){
        Upgrade.clear();
        new BulletSpeed(this);
        new ReloadSpeed(this);
        new MaxAmmo(this);
        new FireSpeed(this);
        new BulletSize(this);
        new Piercing(this);
    }

    public static void main(String[] args) {
        new Shooter();
    }

    public static Shooter get(){
        return (Shooter) Game.get();
    }


    @Override
    protected void tick() {
        int difficulty= (int) getSecondsRun()/2-Enemy.killed-Enemy.count;
        for (int i = 0; i < difficulty; i++) {
            spawnEnemy();
        }
    }

    Random random=new Random(1);

    int radius=getFrame().getWidth()/2;

    private void spawnEnemy(){
        double degree=random.nextDouble()*2*Math.PI;
        int x= (int) (Math.sin(degree)*radius);
        int y= (int) (Math.cos(degree)*radius);
        new Enemy(this).setPosition(getPlayer().getX()+x,getPlayer().getY()+y);
    }

    public Player getPlayer(){
        return player;
    }

    @Override
    public void restart(){
        super.restart();
        spawnerTask.cancel();
        for (GameObject object:GameObject.getGameObjects())
            object.destroy();
        Sprite.getSpritesAt(Layer.UI).clear();
        initialise();
    }


    @Override
    protected void displayStats(String[] toDisplay) {
        super.displayStats(new String[]{"FPS: "+getCurrentFPS(),"Ammo: "+ player.getAmmo(),"Coins: "+ player.getCoins(),"Lives: "+ player.getLives()});
    }
}
