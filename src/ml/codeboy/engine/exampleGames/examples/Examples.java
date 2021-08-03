package ml.codeboy.engine.exampleGames.examples;

import ml.codeboy.engine.Sound.SoundController;
import ml.codeboy.engine.TitleScreen;

public class Examples extends TitleScreen {
    public Examples() {
        super("Examples");
        SoundController.getInstance().addToQueue("Epic_Journey.wav");
        addButton("Play Sound", () -> SoundController.getInstance().play());
    }
}
