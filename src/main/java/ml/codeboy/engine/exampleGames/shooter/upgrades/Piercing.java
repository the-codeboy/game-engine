package ml.codeboy.engine.exampleGames.shooter.upgrades;

import ml.codeboy.engine.Game;

public class Piercing extends Upgrade {
    public Piercing(Game game) {
        super(100, "Piercing", game);
    }

    @Override
    protected void onBuy() {
        player.addPiercing(1);
    }
}
