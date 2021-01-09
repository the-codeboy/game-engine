package ml.codeboy.engine.exampleGames.shooter;

import ml.codeboy.engine.*;
import ml.codeboy.engine.UI.Button;
import ml.codeboy.engine.exampleGames.menu.Menu;
import ml.codeboy.engine.exampleGames.shooter.GameObjects.Enemy;
import ml.codeboy.engine.exampleGames.shooter.GameObjects.Player;
import ml.codeboy.engine.exampleGames.shooter.upgrades.*;

import java.util.Random;

public class Shooter extends Game {
    Player player;


    public Shooter() {
        super("Shooter");//,new UITheme(Color.GREEN,Color.BLACK,true)
        defaultColor= theme.getForeground();
    }

    Task spawnerTask;
    @Override
    protected void initialise() {
        player=new Player(this);
        getScheduler().scheduleTask(this::initUpgrades,0);
        spawnerTask=new Task(getScheduler()) {
            @Override
            protected void onCreation() {
                period=3;
                start();
            }

            @Override
            protected void run() {
                if(period>0.1)
                    period*=0.9999;
                System.out.println(period);
                spawnEnemy();
            }
        };
        Button pause=new Button("||",this::togglePause);
        pause.setTheme(theme);
        pause.setPosition(getWidth()-50,50);
        pause.setSize(30);

        Button back=new Button("back",()->{launchGame(Menu.class);});
        back.setTheme(theme);
        back.setPosition(getWidth()-200,50);
        back.setSize(30);

        Button buy;
        buy=new Button(Upgrade.getBuyAtOnce().name(),Upgrade::cycleBuyAtOnce){
            @Override
            public void press() {
                super.press();
                setText(Upgrade.getBuyAtOnce().name());
            }
        };
        buy.setTheme(theme);
        buy.setPosition(getWidth()-100,50);
        buy.setSize(30);

        Button button;
        button=new Button("x"+getSpeed(),()->{}){
            @Override
            public void press() {
                switch ((int) (getSpeed()*2)){
                    case 1:{
                        setSpeed(1);
                        break;
                    }
                    case 2:{
                        setSpeed(2);
                        break;
                    }
                    case 4:{
                        setSpeed(4);
                        break;
                    }
                    case 8:{
                        setSpeed(0.5);
                        break;
                    }
                }
                setText("x"+getSpeed());
            }
        };
        button.setTheme(theme);
        button.setPosition(getWidth()-150,50);
        button.setSize(30);

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
        new MultiShot();
        new Lives();
    }

    public static void main(String[] args) {
        new Shooter();
    }

    public static Shooter get(){
        return (Shooter) Game.get();
    }


    @Override
    protected void tick() {
//        int difficulty= (int) (getSecondsRun()/4)-Enemy.killed-Enemy.count;
//        for (int i = 0; i < difficulty; i++) {
//            spawnEnemy();
//        }
    }

    Random random=new Random(1);

    int radius= (int) (getWidth() *0.75);

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
    protected void exit() {
        super.exit();
    }

    @Override
    protected void displayStats(String[] toDisplay) {
        super.displayStats(new String[]{"FPS: "+getCurrentFPS(),"Ammo: "+ player.getAmmo(),"Coins: "+ player.getCoins(),"Lives: "+ player.getLives()});
    }
}
