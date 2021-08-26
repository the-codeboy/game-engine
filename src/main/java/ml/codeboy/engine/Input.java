package ml.codeboy.engine;

import ml.codeboy.engine.UI.UIObject;
import ml.codeboy.engine.exampleGames.shooter.Shooter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Set;

public class Input implements MouseListener, KeyListener, MouseWheelListener, MouseMotionListener {

    public static boolean debugMode = false;
    private static Input input;
    private final Set<Integer> keys = new HashSet<>();
    private Set<Integer> lastTickKeys=new HashSet<>();
    private boolean mouseDown = false;

    public static Input getInstance() {
        return input != null ? input : (input = new Input());
    }

    public static Point getMousePosition() {
        //return MouseInfo.getPointerInfo().getLocation();
        Point position = null;
        Game game=Game.get();
        if (game != null) {
            JFrame frame=game.getFrame();
            if(frame!=null)
                position = frame.getMousePosition();
        }
        return position == null ? new Point() : position;
    }

    void tick(){
        lastTickKeys=new HashSet<>(keys);
    }

    /**
     * @param keyCode the KeyCode of the key
     * @return whether the key was just pressed this tick and wasn´t pressed before
     */
    public static boolean isKeyPressed(int keyCode) {
        return getInstance().keys.contains(keyCode)&&!getInstance().lastTickKeys.contains(keyCode);
    }
    /**
     * @param keyCode the KeyCode of the key
     * @return whether the key is currently down
     */
    public static boolean isKeyDown(int keyCode) {
        return getInstance().keys.contains(keyCode);
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
            for (Sprite sprite : Layer.UI.getSprites()) {
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
        keys.add(e.getKeyCode());
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE)
            Game.get().doNextTick(Game.get()::togglePause);
        keys.remove(e.getKeyCode());
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
