package ml.codeboy.engine.exampleGames.examples;

import ml.codeboy.engine.Sound.SoundController;
import ml.codeboy.engine.TitleScreen;
import ml.codeboy.engine.UI.UITheme;
import ml.codeboy.engine.exampleGames.responsive.ResponsiveExample;
import ml.codeboy.engine.exampleGames.rpg.Rpg;
import ml.codeboy.engine.exampleGames.tic.TicTacToe;

public class Examples extends TitleScreen {
    public Examples() {
        super("Examples");
        title.setTheme(UITheme.PLAIN_TEXT);
        SoundController.getInstance().addToQueue("Epic_Journey.wav");
    }
    @Override
    protected void initialise() {
        super.initialise();
        addButton("Play", () -> SoundController.getInstance().play());
        addButton("Pause", () -> SoundController.getInstance().pause());
        addButton("Toggle", () -> SoundController.getInstance().toggle());
    }

}
