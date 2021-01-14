package ml.codeboy.engine.exampleGames.creeperSweeper;

import ml.codeboy.engine.TitleScreen;

public class CreeperSweeperTitleScreen extends TitleScreen {
    public CreeperSweeperTitleScreen() {
        super("CreeperSweeper");
        addButton("PLAY",()->launchGame(CreeperSweeper.class));
    }
}
