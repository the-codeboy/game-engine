package ml.codeboy.engine.exampleGames.menu;

import ml.codeboy.engine.TitleScreen;
import ml.codeboy.engine.UI.UITheme;
import ml.codeboy.engine.exampleGames.creeperSweeper.CreeperSweeperTitleScreen;
import ml.codeboy.engine.exampleGames.shooter.Shooter;

public class Menu extends TitleScreen {
    public Menu() {
        super("Example Games");
        title.setTheme(UITheme.PLAIN_TEXT);
        addButton("Shooter",this::launchShooter);
        addButton("Creeper Sweeper",()->launchGame(CreeperSweeperTitleScreen.class));
    }

    private void launchShooter(){
        launchGame(Shooter.class);
    }
}
