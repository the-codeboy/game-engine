package ml.codeboy.engine;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Game {

    private JFrame frame = new JFrame();
    private BufferedImage screen;
    private int preferredX=1000,preferredY=1000;
    private String name;
    private boolean fullScreen=true;

    public Game() {
    }

    public Game(String name, int preferredX, int preferredY, boolean fullScreen) {
        this.name = name;
        this.preferredX = preferredX;
        this.preferredY = preferredY;
        this.fullScreen = fullScreen;
    }

    private void init(){
        screen = new BufferedImage(preferredX,preferredY,0);
        frame = new JFrame(name);
        frame.setLocationRelativeTo(null);
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        if(fullScreen)
            frame.setUndecorated(true);
    }
}
