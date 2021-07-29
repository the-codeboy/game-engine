package ml.codeboy.engine;

import ml.codeboy.engine.UI.UIObject;
import ml.codeboy.engine.exampleGames.shooter.Shooter;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class Input implements MouseListener, KeyListener, MouseWheelListener, MouseMotionListener {

    public static boolean debugMode = false;
    private static Input input;
    private final HashMap<Integer, Boolean> keys = new HashMap<>();
    private boolean mouseDown = false;

    public static Input getInstance() {
        return input != null ? input : (input = new Input());
    }

    public static Point getMousePosition() {
        //return MouseInfo.getPointerInfo().getLocation();
        Point position = null;
        if (Game.get() != null)
            position = Game.get().getFrame().getMousePosition();
        return position == null ? new Point() : position;
    }

    public static boolean isKeyDown(int keyCode) {
        return getInstance().keys.getOrDefault(keyCode, false);
    }

    public static int getMouseX() {
        return getMousePosition().x;
    }

    public static int getMouseY() {
        return getMousePosition().y;
    }

    public static boolean isMouseDown() {
        return getInstance().mouseDown;
    }

    public static boolean isTouchingUI() {
        for (Sprite sprite : Layer.UI.getSprites()) {
            if (sprite instanceof UIObject) {
                if (sprite.isTouching(getMousePosition())) {
                    return true;
                }
            } else System.out.println("GameObject on UI Layer that is not instance of UIObject: " + sprite);
        }
        return false;
    }

    public static int horizontal() {
        return (isKeyDown(KeyEvent.VK_LEFT) || isKeyDown(KeyEvent.VK_A) ? -1 : 0) +
                (isKeyDown(KeyEvent.VK_RIGHT) || isKeyDown(KeyEvent.VK_D) ? 1 : 0);
    }

    public static int vertical() {
        return (isKeyDown(KeyEvent.VK_UP) || isKeyDown(KeyEvent.VK_W) ? -1 : 0) +
                (isKeyDown(KeyEvent.VK_DOWN) || isKeyDown(KeyEvent.VK_S) ? 1 : 0);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
//        buttonPress();
    }

    private void buttonPress() {
        Game.doNext(() -> {
            ArrayList<Sprite> list=new ArrayList<>(Layer.UI.getSprites());
            Collections.sort(list);
            for (Sprite sprite : list) {
                if (sprite instanceof UIObject) {
                    if (sprite.isTouching(getMousePosition())) {
                        Game.doNext(((UIObject) sprite)::press);
                        return;
                    }
                }
            }
        });
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            mouseDown = true;
        }
        buttonPress();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            mouseDown = false;
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_L && isMouseDown() && isTouchingUI()) {
            if (Shooter.get() != null)
                Shooter.get().getPlayer().addCoins(1000);
        }
        keys.put(e.getKeyCode(), true);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE)
            Game.get().doNextTick(Game.get()::togglePause);
        keys.put(e.getKeyCode(), false);
        if (e.getKeyCode() == KeyEvent.VK_F3) {
            debugMode = !debugMode;
            System.out.println("debug mode " + (debugMode ? "enabled" : "disabled"));
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {

    }
}
