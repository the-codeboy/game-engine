package ml.codeboy.engine.exampleGames.shooter.GameObjects;

import ml.codeboy.engine.Game;
import ml.codeboy.engine.GameObject;
import ml.codeboy.engine.UI.Button;

import java.awt.*;

public class Player extends Damageable {
    int xDir=0,yDir=0;

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    private int coins=10;

    public static Player getPlayer() {
        return player;
    }

    private static Player player;
    public Player(Game game) {
        super(game);
        if(player!=null)
            throw new IllegalStateException("player exists already");
        player=this;
        setWidth(10);
        setHeight(10);
        setX((float)Game.get().getFrame().getWidth()/2);
        setY((float)Game.get().getFrame().getHeight()/2);
        listenForCollision(Enemy.class);
        setLives(10);
    }

    @Override
    protected void onCollision(GameObject other) {
        //if(other instanceof Enemy) { // not required only listening for enemy
            destroy(other);
            addLives(-1);
        //}
    }

    @Override
    public void onDeath(){
            destroy();
            game.setGameOver(true);
            Button button=new Button(game,"restart",game::restart);
            button.setPosition(game.getMiddleOfWindow());
            button.setWidthAndHeight(game.getMiddleOfWindow().x/4,game.getMiddleOfWindow().y/4);
    }

    @Override
    protected void onDestruction() {
        player=null;
    }

    int length=10;

    public int getAmmo() {
        return (int) ammo;
    }

    public void addPiercing(int piercing) {
        this.piercing += piercing;
    }

    int piercing=1;




    public void addMaxAmmo(int maxAmmo) {
        this.maxAmmo+= maxAmmo;
    }

    int maxAmmo=50;
    double ammo=maxAmmo;
    double cooldown,maxCooldown=0.2;

      public void addFireSpeed(int speed){
          maxCooldown=(double) 1/(speed+1/maxCooldown);
      }

    public void addBulletSpeed(double bulletSpeed) {
        this.bulletSpeed+= bulletSpeed;
    }

    private double bulletSpeed=10;

    public void addReloadSpeed(double reloadSpeed) {
        this.reloadSpeed += reloadSpeed;
    }

    private double reloadSpeed=1;

    public void addBulletSize(int bulletSize) {
        this.bulletSize += bulletSize;
    }

    private int bulletSize=4;
    @Override
    protected void tick() {
        double vectorlength=getMousePosition().distance(getPosition());
        xDir= (int) (length*(getMouseX()-getX())/ vectorlength);
        yDir= (int) (length*(getMouseY()-getY())/ vectorlength);
        if(isMouseDown()){
            if(ammo>=1&&cooldown<=0) {
                cooldown=maxCooldown;
                Bullet bullet=new Bullet(game, xDir, yDir);
                bullet.setSize(bulletSize);
                bullet.setPosition(getX() + xDir, getY() + yDir);
                bullet.setLives(piercing);
                bullet.speed=bulletSpeed;
                ammo--;
            }
        }
        if(ammo<maxAmmo) ammo+=reloadSpeed*deltaTime;
        else ammo=maxAmmo;
        if(cooldown>0)
        cooldown-=deltaTime;
    }

    @Override
    public void render(Graphics2D g) {
        g.drawOval(getX()-getWidth()/2,getY()-getHeight()/2,getWidth(),getHeight());
        g.drawLine(getX(),getY(),getX()+xDir,getY()+yDir);
    }
}
