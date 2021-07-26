package ml.codeboy.engine.exampleGames.shooter;

import ml.codeboy.engine.*;
import ml.codeboy.engine.UI.Button;
import ml.codeboy.engine.exampleGames.menu.Menu;
import ml.codeboy.engine.exampleGames.shooter.GameObjects.Enemy;
import ml.codeboy.engine.exampleGames.shooter.GameObjects.Player;
import ml.codeboy.engine.exampleGames.shooter.upgrades.*;

import java.awt.event.ComponentEvent;
import java.util.Random;

public class Shooter extends Game {
    Player player;
    Task spawnerTask;
    Random random = new Random(1);
    int radius = (int) (getWidth() * 0.75);

    public Shooter() {
        super("Shooter");//,new UITheme(Color.GREEN,Color.BLACK,true)
        defaultColor = theme.getForeground();
    }

    public static Shooter get() {
        return (Shooter) Game.get();
    }

    @Override
    public void componentResized(ComponentEvent ce) {
        super.componentResized(ce);

    }

    @Override
    protected void initialise() {
        player = new Player(this);
        getScheduler().scheduleTask(this::initUpgrades, 0);
        spawnerTask = new Task(getScheduler()) {
            int wave = 1;

            @Override
            protected void onCreation() {
                period = 10;
                start();
                getScheduler().scheduleTask(this::run, 0);
            }

            @Override
            protected void run() {
                if (period > 5)
                    period *= 0.9999;
                wave++;
                for (int i = 0, times = 1 + (int) (Math.log(wave)); i < times; i++) {
                    int level = 1 + (int) (Math.random() * wave * 0.1);
                    System.out.println("spawning enemy level " + level);
                    spawnEnemy((int) (radius + radius * Math.random())).setLevel(level);
                }
            }
        };
        Button pause = new Button("||", this::togglePause);
        pause.setTheme(theme);
        pause.setPosition(getWidth() - 50, 50);
        pause.setSize(30);

        Button back = new Button("back", () -> {
            launchGame(Menu.class);
        });
        back.setTheme(theme);
        back.setPosition(getWidth() - 200, 50);
        back.setSize(30);

        Button buy;
        buy = new Button(Upgrade.getBuyAtOnce().name(), Upgrade::cycleBuyAtOnce) {
            @Override
            public void press() {
                super.press();
                setText(Upgrade.getBuyAtOnce().name());
            }
        };
        buy.setTheme(theme);
        buy.setPosition(getWidth() - 100, 50);
        buy.setSize(30);

        Button button;
        button = new Button("x" + getSpeed(), () -> {
        }) {
            @Override
            public void press() {
                switch ((int) (getSpeed() * 2)) {
                    case 1: {
                        setSpeed(1);
                        break;
                    }
                    case 2: {
                        setSpeed(2);
                        break;
                    }
                    case 4: {
                        setSpeed(4);
                        break;
                    }
                    case 8: {
                        setSpeed(0.5);
                        break;
                    }
                }
                setText("x" + getSpeed());
            }
        };
        button.setTheme(theme);
        button.setPosition(getWidth() - 150, 50);
        button.setSize(30);

        unPause();
    }

    private void initUpgrades() {
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

    @Override
    protected void tick() {
//        int speed=100;
//        if(Input.isKeyDown(KeyEvent.VK_LEFT))
//            getCamera().addX(-deltaTime()*speed);
//        if(Input.isKeyDown(KeyEvent.VK_RIGHT))
//            getCamera().addX(deltaTime()*speed);
//        if(Input.isKeyDown(KeyEvent.VK_UP))
//            getCamera().addY(-deltaTime()*speed);
//        if(Input.isKeyDown(KeyEvent.VK_DOWN))
//            getCamera().addY(deltaTime()*speed);

//        int difficulty= (int) (getSecondsRun()/4)-Enemy.killed-Enemy.count;
//        for (int i = 0; i < difficulty; i++) {
//            spawnEnemy();
//        }
    }

    private Enemy spawnEnemy(int radius) {
        double degree = random.nextDouble() * 2 * Math.PI;
        int x = (int) (Math.sin(degree) * radius);
        int y = (int) (Math.cos(degree) * radius);
        Enemy enemy = new Enemy(this);
        enemy.setPosition(getPlayer().getX() + x, getPlayer().getY() + y);
        return enemy;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public void restart() {
        super.restart();
        spawnerTask.cancel();
        for (GameObject object : GameObject.getGameObjects())
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
        super.displayStats(new String[]{"FPS: " + getCurrentFPS(), "Ammo: " + player.getAmmo(), "Coins: " + player.getCoins(), "Lives: " + player.getLives()});
    }
}
