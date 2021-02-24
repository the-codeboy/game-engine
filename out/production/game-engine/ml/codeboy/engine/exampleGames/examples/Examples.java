package ml.codeboy.engine.exampleGames.examples;

import ml.codeboy.engine.Sound;
import ml.codeboy.engine.TitleScreen;

public class Examples extends TitleScreen {
    public Examples() {
        super("Examples");
        addButton("Play Sound", ()->Sound.play("Epic_Journey.wav"));
    }
}
