package ml.codeboy.engine;

import ml.codeboy.engine.UI.UIObject;
import ml.codeboy.engine.exampleGames.shooter.Shooter;

import java.awt.*;
import java.awt.event.*;

public class Input implements MouseListener, KeyListener, MouseWheelListener, MouseMotionListener {

    private static Input input;

    public static Input getInstance(){
        return input!=null?input:(input=new Input());
    }

    public static Point getMousePosition(){
        return MouseInfo.getPointerInfo().getLocation();
    }

    public static int getMouseX(){
        return MouseInfo.getPointerInfo().getLocation().x;
    }

    public static int getMouseY(){
        return MouseInfo.getPointerInfo().getLocation().y;
    }

    public static boolean isMouseDown() {
        return getInstance().mouseDown;
    }

    private boolean mouseDown = false;

    @Override
    public void mouseClicked(MouseEvent e) {
        for (Sprite sprite:Layer.UI.getSprites()){
            if(sprite instanceof UIObject){
                if(sprite.isTouching(getMousePosition())){
                    Game.get().getScheduler().scheduleTask(((UIObject) sprite)::press,0);
                    return;
                }
            }
        }
    }

    public static boolean isTouchingUI(){
        for (Sprite sprite:Layer.UI.getSprites()){
            if(sprite instanceof UIObject){
                if(sprite.isTouching(getMousePosition())){
                    return true;
                }
            }else System.out.println("GameObject on UI Layer that is not instance of UIObject: "+sprite);
        }
        return false;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if(isTouchingUI())
            return;
        if (e.getButton() == MouseEvent.BUTTON1) {
            mouseDown = true;
        }
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
        if(e.getKeyCode()==KeyEvent.VK_L&&isMouseDown()&&isTouchingUI()){
            if(Shooter.get()!=null)
                Shooter.get().getPlayer().setCoins(1000);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if(e.getKeyCode()==KeyEvent.VK_SPACE)
            Game.get().doNextTick(Game.get()::togglePause);
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
