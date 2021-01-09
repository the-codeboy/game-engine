package ml.codeboy.engine.exampleGames.shooter.upgrades;

import ml.codeboy.engine.Game;
import ml.codeboy.engine.exampleGames.shooter.GameObjects.Player;

public class Lives extends Upgrade{
    public Lives() {
        super(100, "Lives", Game.get());
    }

    @Override
    protected void onBuy() {
        player.addLives(1);
    }
}
