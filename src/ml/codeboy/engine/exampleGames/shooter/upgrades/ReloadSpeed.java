package ml.codeboy.engine.exampleGames.shooter.upgrades;

import ml.codeboy.engine.Game;

public class ReloadSpeed extends Upgrade{
    public ReloadSpeed(Game game) {
        super(10,"reload speed", game);
    }

    @Override
    protected void onBuy() {
        player.addReloadSpeed(1);
    }
}
