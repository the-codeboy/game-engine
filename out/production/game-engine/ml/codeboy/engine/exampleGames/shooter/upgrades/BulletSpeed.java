package ml.codeboy.engine.exampleGames.shooter.upgrades;

import ml.codeboy.engine.Game;

public class BulletSpeed extends Upgrade{
    public BulletSpeed(Game game) {
        super(5, "BulletSpeed", game);
    }

    @Override
    protected void onBuy() {
        player.addBulletSpeed(10);
    }
}
