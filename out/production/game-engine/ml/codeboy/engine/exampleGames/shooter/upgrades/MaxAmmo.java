package ml.codeboy.engine.exampleGames.shooter.upgrades;

import ml.codeboy.engine.Game;

public class MaxAmmo extends Upgrade {
    public MaxAmmo(Game game) {
        super(1, "Ammo", game);
    }

    @Override
    protected void onBuy() {
        player.addMaxAmmo(1);
    }
}
