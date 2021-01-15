package ml.codeboy.engine;

import com.sun.istack.internal.Nullable;
import ml.codeboy.engine.UI.UITheme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ConcurrentModificationException;

public abstract class Game implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener{

    private static Game instance;

    private static final JFrame frame=new JFrame();
    private BufferedImage screen;
    private int preferredX=1000,preferredY=1000;

    static {
        frame.addKeyListener(Input.getInstance());
        frame.addMouseListener(Input.getInstance());
        frame.addMouseWheelListener(Input.getInstance());
        frame.addMouseMotionListener(Input.getInstance());
    }

    private final String name;
    private final boolean fullScreen;
    private Graphics2D graphics;

    private double lastFPS;
    private double lastFrame=System.nanoTime();
    private int FPS;
    private final long start=System.currentTimeMillis()/1000;
    private boolean closed=false;

    private BufferedImage backgroundImage;

    private double speed=1;

    protected UITheme theme=UITheme.DEFAULT;

    public UITheme getTheme(){
        return theme;
    }

    protected void setBackgroundImage(BufferedImage backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public Color getDefaultColor() {
        return defaultColor;
    }

    protected Color defaultColor=Color.WHITE;

    /**
     * @return how many seconds have passed since this Game has been instantiated
     */
    public long getSecondsRun(){
        return System.currentTimeMillis()/1000-start;
    }

    private final TaskScheduler scheduler=new TaskScheduler(this);

    /**
     * @return gets the TaskScheduler of this Game. The TaskScheduler can be used to schedule Tasks which will
     * be run at a specified interval or once a certain time. For more information see TaskScheduler#scheduleTask
     */
    public TaskScheduler getScheduler() {
        return scheduler;
    }

    public Point getMiddleOfWindow(){
        return new Point(getFrame().getWidth()/2,getFrame().getHeight()/2);
    }

    /**
     * @return the JFrame this Game is rendering to
     * might be removed in a future release but is still safe to use at the moment
     */
    @Deprecated
    public JFrame getFrame() {
        return frame;
    }

    /**
     * @return the name of this Game
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name of the new Game
     */
    public Game(String name) {
        this(name,true);
    }

    /**
     * @param name nmae of the Game
     * @param theme UITheme of the Game
     */
    public Game(String name,UITheme theme) {
        this(name,true,theme);
    }

    /**
     * @param name the name of the new Game
     * @param preferredX the width of the Game´s window
     * @param preferredY the height of the Game´s window
     * @deprecated there´s problems with this please use fullscreen
     */
    @Deprecated
    public Game(String name, int preferredX, int preferredY) {
        this.name = name;
        this.fullScreen = false;
        if(instance!=null)
            throw new IllegalStateException("can not create second instance of game!");
        instance=this;
        this.preferredX = preferredX;
        this.preferredY = preferredY;
        init();
    }


    /**
     * @param name name of the Game
     * @param fullScreen if the Game should be fullscreen or not
     */
    public Game(String name, boolean fullScreen) {
        this(name,fullScreen,UITheme.DEFAULT);
    }

    /**
     * @param name the name of the new Game
     * @param fullScreen if the Game should be fullscreen - this defaults to true
     * use the other constructors instead
     */
    @Deprecated
    public Game(String name, boolean fullScreen,UITheme theme) {
        this.theme=theme;
        this.name = name;
        this.fullScreen = fullScreen;
        if(instance!=null)
            throw new IllegalStateException("can not create second instance of game!");
        instance=this;
        init();
    }

    /**
     * creates the window for the Game and initialises listeners - also starts the gameLoop
     */
    private void init(){
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        if(fullScreen) {
            if(!getFrame().isDisplayable())
            getFrame().setUndecorated(true);
            getFrame().setExtendedState(Frame.MAXIMIZED_BOTH);
        }
        else {
            getFrame().setSize(preferredX,preferredY);
            }
        screen = new BufferedImage(Toolkit.getDefaultToolkit().getScreenSize().width, Toolkit.getDefaultToolkit().getScreenSize().height, 1);
        getFrame().setLocationRelativeTo(null);
        //panel=new JPanel(){
        //    @Override
        //    protected void paintComponent(Graphics g) {
        //        g.drawImage(screen,0,0,null);
        //    }
        //};
        //panel.setVisible(true);
        //getFrame().removeAll();
        //getFrame().add(panel);
        getFrame().setVisible(true);


        graphics=screen.createGraphics();
        initialise();
        Thread gameThread = new Thread(this::startGameLoop);
        gameThread.start();
    }


    protected void exit(){
        closed=true;
    }


    protected void setExitAction(Runnable exitAction) {
        this.exitAction = exitAction;
    }

    private Runnable exitAction=()->{};

    private void onExit(){
        Layer layer=Layer.BACKGROUND;
        while (layer!=null){
//            for (Sprite sprite:layer.getSprites())
//                sprite.destroy();
            layer.clear();
            layer=layer.getNext();
        }
//        GameObject.getGameObjects().forEach(g->g.onDestruction(new DestroyEvent(g)));
        GameObject.getGameObjects().clear();
        exitAction.run();
    }

    protected void launchGame(Class<? extends Game> game){
        Constructor<? extends Game>constructor;
        try {
            constructor= game.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(game.getName()+" does not have the right constructor!");
        }
        exit();
        setExitAction(()->{
                try {
                    constructor.newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
            });
    }

    /**
     * @return the current Game singleton
     */
    @Nullable
    public static Game get(){
        return instance;
    }

    /**
     * will get called when the Game is initialised right before the gameLoop gets started (before the first game tick)
     * override this on your Game to do initialisation
     * @deprecated might get removed because you can use your Game´s constructor for initialisation
     */
    @Deprecated
    protected void initialise(){}

    /**
     * starts the gameLoop
     * this is synchronised so that you wont have problems with multithreading
     */
    private synchronized void startGameLoop(){
        while(!closed){
            double now = System.nanoTime();
            double deltaTime = now - lastFrame;
            lastFrame= now;
            deltaSeconds= deltaTime /1000000000;
            if((now -lastFPS)>1000000000) {
                FPS = (int)(1000000000 / deltaTime);
                lastFPS= now;
            }
            try {
                executeGameLogic();
                render();
            }catch (ConcurrentModificationException e){
                e.printStackTrace();
            }
        }
        instance=null;
        onExit();
    }

    /**
     * calls the tick functions on the GameObjects and on this Game´s scheduler
     */
    private void executeGameLogic(){
        scheduler.doTick();
        for (GameObject object :
                GameObject.getGameObjects()) {
            object.internalTick();
        }
        earlyTick();
        for (GameObject object :
                GameObject.getGameObjects()) {
            object.earlyTick();
        }
        tick();
        for (GameObject object :
                GameObject.getGameObjects()) {
            object.tick();
        }
        lateTick();
        for (GameObject object :
                GameObject.getGameObjects()) {
            object.lateTick();
        }
    }

    /**
     * renders the sprites from each layer
     */
    private void render(){
        graphics.clearRect(0,0,screen.getWidth(),screen.getHeight());
        if(backgroundImage!=null)
            graphics.drawImage(backgroundImage,0,0,null);
        Layer currentLayer=Layer.BACKGROUND;
        while (currentLayer!=null){
            for (Sprite sprite :
                    currentLayer.getSprites()) {
                    sprite.render(graphics);
            }
            currentLayer=currentLayer.getNext();
        }
        graphics.setFont(graphics.getFont().deriveFont(20f));
        displayStats(new String[]{"FPS: " + FPS, "some other info", "even more info"});

        getFrame().getGraphics().drawImage(screen,0,0,getWidth(),getHeight(),null);
    }

    /**
     * @param toDisplay a String array with information to be displayed on the top left of the screen
     *                  override this and call super#displayStats to change the displayed stats
     *                  even if you don´t have stats to display you should definitely override this
     */
    protected void displayStats(String[]toDisplay){
        int textHeight = graphics.getFontMetrics().getHeight();
        for (int i = 0; i < toDisplay.length; i++) {
            graphics.drawString(toDisplay[i],10,textHeight+textHeight*i);
        }
    }

    /**
     * @return the current FPS
     */
    public int getCurrentFPS(){
        return FPS;
    }

    private double deltaSeconds;

    /**
     * @return the time in seconds that passed since the last frame
     */
    public double getDeltaTime(){
        return isPaused?0:speed*deltaSeconds;
    }

    /**
     * @param ignorePaused if paused should be ignored for the return value - this can be useful
     *                     if you want to only pause GameObjects but not UI animations
     * @return the current deltaTime or #getDeltaTime
     */
    public double getDeltaTime(boolean ignorePaused){
        return ignorePaused?deltaSeconds:getDeltaTime();
    }

    protected void setCursor(Cursor cursor){
        getFrame().setCursor(cursor);
    }

    /**
     * for restarting the Game - override to use this
     */
    public void restart(){
        setGameOver(false);
    }


    private boolean isPaused=false;

    public void pause(){
        isPaused=true;
    }

    public void unPause(){
        isPaused=false;
        isGameOver=false;
    }

    public void togglePause(){
        if(!isGameOver) {
            if (isPaused)
                unPause();
            else
                pause();
        }
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public void setGameOver(boolean gameOver) {
        isGameOver = gameOver;
        if(isGameOver)
            pause();
        else
            unPause();
    }

    private boolean isGameOver=false;


    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

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

    protected void earlyTick(){}

    protected void tick(){}

    protected void lateTick(){}


    /**
     * @return mouse x position
     * @deprecated this returns the position on screen use Input#getMouseX()
     */
    //<editor-fold desc="getter">
    @Deprecated
    public static int getMouseX(){
        return MouseInfo.getPointerInfo().getLocation().x;
    }
    /**
     * @return mouse y position
     * @deprecated this returns the position on screen use Input#getMouseY()
     */
    @Deprecated
    public static int getMouseY(){
        return MouseInfo.getPointerInfo().getLocation().y;
    }

    /**
     * @return the time in seconds that passed since the last frame or 0 if there is no Game
     * @param ignorePaused if paused should be ignored - see Game#getDeltaTime
     */
    @Nullable
    public static double deltaTime(boolean ignorePaused){
        if(get()==null)
            return 0;
        return get().getDeltaTime(ignorePaused);
    }

    /**
     * @return the time since the last frame
     */
    @Nullable
    public static double deltaTime(){
        return deltaTime(false);
    }


    /**
     * @return the width of the game - this value might change if the game is not in fullscreen mode
     */
    public int getWidth(){
        return getFrame().getWidth();
    }

    /**
     * @return the height of the game - this value might change if the game is not in fullscreen mode
     */
    public int getHeight(){
        return getFrame().getHeight();
    }


    /**
     * runs a runnable in the next Tick
     * @param runnable the runnable to run
     */
    public void doNextTick(Runnable runnable){
        getScheduler().scheduleTask(runnable,0,true);
    }

    public static void doNext(Runnable runnable){
        if(get()!=null)
        get().getScheduler().scheduleTask(runnable,0,true);
    }
    //</editor-fold>
}
