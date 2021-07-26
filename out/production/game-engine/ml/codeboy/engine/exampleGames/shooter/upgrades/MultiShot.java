package ml.codeboy.engine.exampleGames.shooter.upgrades;

import ml.codeboy.engine.Game;

public class MultiShot extends Upgrade {
    public MultiShot() {
        super(150, "MultiShot", Game.get());
    }

    @Override
    protected void onBuy() {
        player.addBullets(1);
    }
}
