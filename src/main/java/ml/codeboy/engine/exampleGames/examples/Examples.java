package ml.codeboy.engine.exampleGames.examples;

import ml.codeboy.engine.Sound.Sound;
import ml.codeboy.engine.Sound.SoundController;
import ml.codeboy.engine.TitleScreen;
import ml.codeboy.engine.UI.UITheme;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;

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
        addButton("Bumm", () -> Sound.play("bumm.wav"));
        addButton("Epic_Journey", () -> Sound.play("Epic_Journey.wav"));
        addButton("Play", () -> SoundController.getInstance().play());
        addButton("Pause", () -> SoundController.getInstance().pause());
        addButton("Toggle", () -> SoundController.getInstance().toggle());
        addButton("Vol+", () -> SoundController.getInstance().addVolume(2));
        addButton("Vol-", () -> SoundController.getInstance().addVolume(-2));
        addButton("Skip", () -> SoundController.getInstance().skipSound());
    }

}
