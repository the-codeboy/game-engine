package ml.codeboy.engine.exampleGames.menu;

import ml.codeboy.engine.TitleScreen;
import ml.codeboy.engine.UI.UITheme;
import ml.codeboy.engine.exampleGames.examples.Examples;
import ml.codeboy.engine.exampleGames.responsive.ResponsiveExample;
import ml.codeboy.engine.exampleGames.rpg.Rpg;
import ml.codeboy.engine.exampleGames.shooter.Shooter;
import ml.codeboy.engine.exampleGames.tic.TicTacToe;

public class Menu extends TitleScreen {
    public Menu() {
        super("Example Games");
        title.setTheme(UITheme.PLAIN_TEXT);
    }

    @Override
    protected void initialise() {
        super.initialise();
        addButton("Shooter", this::launchShooter);
        addButton("RPG", () -> launchGame(Rpg.class));
        addButton("Examples", () -> launchGame(Examples.class));
        addButton("Tic Tac Toe", () -> launchGame(TicTacToe.class));
        addButton("ResponsiveExample", () -> launchGame(ResponsiveExample.class));
    }

    private void launchShooter() {
        launchGame(Shooter.class);
    }
}
