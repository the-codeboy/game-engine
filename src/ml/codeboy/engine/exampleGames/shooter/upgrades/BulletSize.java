package ml.codeboy.engine.exampleGames.shooter.upgrades;

import ml.codeboy.engine.Game;

public class BulletSize extends Upgrade {
    public BulletSize(Game game) {
        super(50, "BulletSize", game);
    }

    @Override
    protected void onBuy() {
        player.addBulletSize(1);
    }
}
