package ml.codeboy.engine.exampleGames.examples;

import ml.codeboy.engine.Sound.SoundController;
import ml.codeboy.engine.TitleScreen;
import ml.codeboy.engine.UI.UITheme;

public class Examples extends TitleScreen {
    public Examples() {
        super("Examples");
        title.setTheme(UITheme.PLAIN_TEXT);
        SoundController.getInstance().addToQueue("bumm.wav");
        SoundController.getInstance().addToQueue("Epic_Journey.wav");
        SoundController.getInstance().addToQueue("bumm.wav");
        SoundController.getInstance().setLoop(true);
    }
    @Override
    protected void initialise() {
        super.initialise();
        addButton("Play", () -> SoundController.getInstance().play());
        addButton("Pause", () -> SoundController.getInstance().pause());
        addButton("Toggle", () -> SoundController.getInstance().toggle());
        addButton("Vol+", () -> SoundController.getInstance().addVolume(2));
        addButton("Vol-", () -> SoundController.getInstance().addVolume(-2));
        addButton("Skip", () -> SoundController.getInstance().skipSound());
    }

}
