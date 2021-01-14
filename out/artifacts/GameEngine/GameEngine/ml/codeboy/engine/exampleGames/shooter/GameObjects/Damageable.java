package ml.codeboy.engine.exampleGames.shooter.GameObjects;

import ml.codeboy.engine.Game;
import ml.codeboy.engine.GameObject;

public class Damageable extends GameObject {

    public Damageable(Game game) {
        super(game);
    }

    public int getDmg() {
        return dmg;
    }

    public void setDmg(int dmg) {
        this.dmg = dmg;
    }

    private int dmg=1;

    public void attack(GameObject other){
        if(other instanceof Damageable){
            ((Damageable) other).addLives(-getDmg());
        }
    }



    public Damageable(Game game, SpriteType type) {
        super(game, type);
    }

    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public void addLives(int lives) {
        this.lives += lives;
        if(this.lives<=0)
            onDeath();
    }

    protected void onDeath(){
        destroy();
    }

    private int lives=1;
}
