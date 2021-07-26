package ml.codeboy.engine.exampleGames.shooter.upgrades;

import ml.codeboy.engine.Game;

public class FireSpeed extends Upgrade {
    public FireSpeed(Game game) {
        super(15, "FireSpeed", game);
    }

    @Override
    protected void onBuy() {
        player.addFireSpeed(1);
    }
}
