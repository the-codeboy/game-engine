package ml.codeboy.engine.exampleGames;

import ml.codeboy.engine.Sound.Sound;
import ml.codeboy.engine.exampleGames.menu.Menu;

public class ExampleGames {
    public static void main(String[] args) {
        new Menu();
        Sound.createSound("Epic_Journey.wav");
    }
}
