package ml.codeboy.engine;

import ml.codeboy.engine.Saving.SaveValue;

public class Damageable extends GameObject {

    @SaveValue
    private int dmg = 1;

    @SaveValue
    private int lives = 1;

    //<editor-fold desc="constructors">
    public Damageable(Game game) {
        super(game);
    }

    public Damageable(Game game, SpriteType type) {
        super(game, type);
    }

    public Damageable(String name, Game game) {
        super(name, game);
    }

    public Damageable(String name, String path, Game game) {
        super(name, path, game);
    }

    public Damageable(String name, int x, int y, Game game) {
        super(name, x, y, game);
    }

    public Damageable(String name, String path, int x, int y, Game game) {
        super(name, path, x, y, game);
    }
    //</editor-fold>


    public Damageable(String name, int x, int y, int width, int height, Game game) {
        super(name, x, y, width, height, game);
    }

    public Damageable(String name, String path, int x, int y, int width, int height, Game game) {
        super(name, path, x, y, width, height, game);
    }

    @Override
    protected void init(Game game) {
        super.init(game);
        setCollision(true);
    }

    public int getDmg() {
        return dmg;
    }

    public void setDmg(int dmg) {
        this.dmg = dmg;
    }

    public void attack(GameObject other) {
        if (other instanceof Damageable) {
            ((Damageable) other).addLives(-getDmg());
        }
    }

    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public void addLives(int lives) {
        this.lives += lives;
        if (this.lives <= 0)
            onDeath();
    }

    protected void onDeath() {
        destroy();
    }
}
