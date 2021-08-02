package ml.codeboy.engine.exampleGames.shooter.GameObjects;

import ml.codeboy.engine.Damageable;
import ml.codeboy.engine.Game;
import ml.codeboy.engine.GameObject;
import ml.codeboy.engine.Saving.ExcludeValue;
import ml.codeboy.engine.Saving.SaveAll;
import ml.codeboy.engine.Saving.SaveValue;
import ml.codeboy.engine.Saving.SaveClass;
import ml.codeboy.engine.UI.Button;
import ml.codeboy.engine.events.DestroyEvent;

import java.awt.*;

@SaveClass
@SaveAll
public class Player extends Damageable {
    @ExcludeValue
    private static Player player;
    @ExcludeValue
    double xDir = 0, yDir = 0;

    int length = 10;
    int piercing = 1;
    public int maxAmmo = 50;
    double ammo = maxAmmo;
    double cooldown, maxCooldown = 0.2;
    private int coins = 10;
    private double bulletSpeed = 10;
    private double reloadSpeed = 1;
    private int bulletSize = 4;
    private int bullets = 1;

    public Player(Game game) {
        super(game, SpriteType.Custom);
        if (player != null)
            throw new IllegalStateException("player exists already");
        player = this;
        setWidth(10);
        setHeight(10);
        setX((float) Game.get().getFrame().getWidth() / 2);
        setY((float) Game.get().getFrame().getHeight() / 2);
        listenForCollision(Enemy.class);
        setLives(10);
    }

    public static Player getPlayer() {
        return player;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public void addCoins(int coins) {
        this.coins += coins;
    }

    @Override
    protected void onCollision(GameObject other) {
        //if(other instanceof Enemy) { // not required only listening for enemy
        destroy(other);
        addLives(-1);
        //}
    }

    @Override
    public void onDeath() {
        destroy();
        game.setGameOver(true);
        Button button = new Button("restart", game::restart);
        button.setPosition(game.getMiddleOfWindow());
        button.setWidthAndHeight(game.getMiddleOfWindow().x / 4, game.getMiddleOfWindow().y / 4);
    }

    @Override
    protected void onDestruction(DestroyEvent event) {
        player = null;
    }

    public int getAmmo() {
        return (int) ammo;
    }

    public void addPiercing(int piercing) {
        this.piercing += piercing;
    }

    public void addMaxAmmo(int maxAmmo) {
        this.maxAmmo += maxAmmo;
    }

    public void addFireSpeed(int speed) {
        maxCooldown = (double) 1 / (speed + 1 / maxCooldown);
    }

    public void addBulletSpeed(double bulletSpeed) {
        this.bulletSpeed += bulletSpeed;
    }

    public void addReloadSpeed(double reloadSpeed) {
        this.reloadSpeed += reloadSpeed;
    }

    public void addBulletSize(int bulletSize) {
        this.bulletSize += bulletSize;
    }

    public void addBullets(int bullets) {
        this.bullets += bullets;
    }

    private void shoot() {
        double angle = 2 * Math.PI / bullets;
        for (int i = 0; i < bullets; i++) {
            double x = xDir * Math.cos(i * angle) - yDir * Math.sin(i * angle), y = (xDir * Math.sin(i * angle) + yDir * Math.cos(i * angle));
            Bullet bullet = new Bullet(game, x, y);
            bullet.setSize(bulletSize);
            bullet.setPosition((int) (getX() + x), (int) (getY() + y));
            bullet.setLives(piercing);
            bullet.speed = bulletSpeed;
        }
        ammo--;
    }

    @Override
    protected void tick() {
        double vectorlength = getMousePosition().distance(getPosition());
        xDir = (length * (getMouseX() - getX()) / vectorlength);
        yDir = (length * (getMouseY() - getY()) / vectorlength);
        if (isMouseDown()) {
            if (ammo >= 1 && cooldown <= 0) {
                cooldown = maxCooldown;
                shoot();
            }
        }
        if (ammo < maxAmmo) ammo += reloadSpeed * deltaTime;
        else ammo = maxAmmo;
        if (cooldown > 0)
            cooldown -= deltaTime;
    }

    @Override
    public void customRender(Graphics2D g) {
        g.drawOval(getXOnScreen() - getWidth() / 2, getYOnScreen() - getHeight() / 2, getWidth(), getHeight());
        g.drawLine(getXOnScreen(), getYOnScreen(), (int) (getX() + xDir), (int) (getY() + yDir));
    }
}
