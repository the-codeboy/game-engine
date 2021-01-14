package ml.codeboy.engine;

import ml.codeboy.engine.UI.UIObject;
import ml.codeboy.engine.exampleGames.shooter.Shooter;

import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;

public class Input implements MouseListener, KeyListener, MouseWheelListener, MouseMotionListener {

    private static Input input;

    static Input getInstance(){
        return input!=null?input:(input=new Input());
    }

    private final HashMap<Integer,Boolean> keys=new HashMap<>();

    public static Point getMousePosition(){
        //return MouseInfo.getPointerInfo().getLocation();
        Point position = null;
        if(Game.get()!=null)
        position= Game.get().getFrame().getMousePosition();
        return position==null?new Point():position;
    }

    public static boolean isKeyDown(int keyCode){
        return getInstance().keys.getOrDefault(keyCode,false);
    }

    public static int getMouseX(){
        return getMousePosition().x;
    }

    public static int getMouseY(){
        return getMousePosition().y;
    }

    public static boolean isMouseDown() {
        return getInstance().mouseDown;
    }

    private boolean mouseDown = false;

    @Override
    public void mouseClicked(MouseEvent e) {
        Game.doNext(()->{
            for (Sprite sprite:Layer.UI.getSprites()){
                if(sprite instanceof UIObject&&sprite.isInteractable()){
                    if(sprite.isTouching(getMousePosition())){
                        ((UIObject) sprite).press();
                        return;
                    }
                }
            }
        });
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
                Shooter.get().getPlayer().addCoins(1000);
        }
        keys.put(e.getKeyCode(),true);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if(e.getKeyCode()==KeyEvent.VK_SPACE)
            Game.get().doNextTick(Game.get()::togglePause);
        keys.put(e.getKeyCode(),false);
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
