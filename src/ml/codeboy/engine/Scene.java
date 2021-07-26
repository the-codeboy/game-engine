package ml.codeboy.engine;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Scene {
    private final Game game;
    private final Graphics2D graphics;
    private final BufferedImage screen;
    private final BufferedImage backgroundImage;

    public Scene(Game game) {
        this.game = game;
        graphics = game.getGraphics();
        screen = game.getScreen();
        backgroundImage = game.getBackgroundImage();
    }

    private void render() {
        graphics.clearRect(0, 0, screen.getWidth(), screen.getHeight());
        if (backgroundImage != null)
            graphics.drawImage(backgroundImage, 0, 0, null);
        Layer currentLayer = Layer.BACKGROUND;
        while (currentLayer != null) {
            for (Sprite sprite :
                    currentLayer.getSprites()) {
                sprite.render(graphics);
            }
            currentLayer = currentLayer.getNext();
        }
        graphics.setFont(graphics.getFont().deriveFont(20f));
        game.displayStats(new String[]{"FPS: " + game.getCurrentFPS()});

        game.getFrame().getGraphics().drawImage(screen, 0, 0, game.getWidth(), game.getHeight(), null);
    }
}
