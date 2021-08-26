package ml.codeboy.engine.exampleGames.rpg.GameObjects.characters;

import ml.codeboy.engine.Game;

public class Enemy extends Character {

    private final int range = 5;
    private Character target;
    private Class<?>[] targets = {Player.class};

    //<editor-fold desc="constructors">
    public Enemy(String name, Game game) {
        super(name, game);
    }

    public Enemy(String name, String path, Game game) {
        super(name, path, game);
    }

    public Enemy(String name, int x, int y, Game game) {
        super(name, x, y, game);
    }
    //</editor-fold>

    public Enemy(String name, String path, int x, int y, Game game) {
        super(name, path, x, y, game);
    }

    public Enemy(String name, int x, int y, int width, int height, Game game) {
        super(name, x, y, width, height, game);
    }

    public Enemy(String name, String path, int x, int y, int width, int height, Game game) {
        super(name, path, x, y, width, height, game);
    }

    public Character getTarget() {
        return target;
    }

    public void setTarget(Character target) {
        this.target = target;
    }

    public Class<?>[] getTargets() {
        return targets;
    }

    public void setTargets(Class<?>[] targets) {
        this.targets = targets;
    }

    @Override
    protected void tick() {
        super.tick();
        if (target != null && target.isAlive()) {
            if (isInRange(target))
                attack(target);
            else moveTowards(target);
        }
    }

    protected void attack(Character target) {

    }

    protected void moveTowards(Character target) {

    }

    protected boolean isInRange(Character target) {
        return getPosition().distance(target.getPosition()) <= range;
    }


}
