package ml.codeboy.engine;

import com.sun.istack.internal.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class Game implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener{

    private static Game instance;

    private JFrame frame;
    private BufferedImage screen;
    private int preferredX=1000,preferredY=1000;

    private final String name;
    private final boolean fullScreen;
    private Graphics2D graphics;

    private double lastFPS;
    private double deltaTime=0;
    private double lastFrame=System.nanoTime();
    private int FPS;
    private long start=System.currentTimeMillis()/1000;

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

    private TaskScheduler scheduler=new TaskScheduler(this);

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
     * @param name the name of the new Game
     * @param preferredX the width of the Game´s window
     * @param preferredY the height of the Game´s window
     */
    public Game(String name, int preferredX, int preferredY) {
        this(name,false);
        this.preferredX = preferredX;
        this.preferredY = preferredY;
    }

    /**
     * @param name the name of the new Game
     * @param fullScreen if the Game should be fullscreen - this defaults to true
     * use the other constructors instead
     */
    @Deprecated
    public Game(String name, boolean fullScreen) {
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
        frame = new JFrame(name);
        getFrame().setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        if(fullScreen) {
            getFrame().setUndecorated(true);
            getFrame().setExtendedState(Frame.MAXIMIZED_BOTH);
            getFrame().setVisible(true);
            screen = new BufferedImage(getFrame().getWidth(),getFrame().getHeight(),1);
        }
        else {
            getFrame().setSize(preferredX,preferredY);
            screen = new BufferedImage(preferredX, preferredY, 1);
            getFrame().setVisible(true);
        }

        getFrame().addKeyListener(Input.getInstance());
        getFrame().addMouseListener(Input.getInstance());
        getFrame().addMouseWheelListener(Input.getInstance());
        getFrame().addMouseMotionListener(Input.getInstance());

        graphics=screen.createGraphics();
        initialise();
        new Thread(this::startGameLoop).start();
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
     * might get removed because you can use your Game´s constructor for initialisation
     */
    @Deprecated
    protected void initialise(){}

    /**
     * starts the gameLoop
     * this is synchronised so that you wont have problems with multithreading
     */
    private synchronized void startGameLoop(){
        while(true){
            double now = System.nanoTime();
            deltaTime = now - lastFrame;
            lastFrame= now;
            deltaSeconds=deltaTime/1000000000;
            if((now -lastFPS)>1000000000) {
                FPS = (int)(1000000000 / deltaTime);
                lastFPS= now;
            }
            executeGameLogic();
            render();
        }
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
        Layer currentLayer=Layer.BACKGROUND;
        while (currentLayer.hasNext()){
            for (Sprite sprite :
                    currentLayer.getSprites()) {
                    sprite.render(graphics);
            }
            currentLayer=currentLayer.getNext();
        }
        displayStats(new String[]{"FPS: " + FPS, "some other info", "even more info"});

        getFrame().getGraphics().drawImage(screen,0,0,null);
    }

    /**
     * @param toDisplay a String array with information to be displayed on the top left of the screen
     *                  override this and call super#displayStats to change the displayed stats
     *                  even if you don´t have stats to display you should definitely override this
     */
    protected void displayStats(String[]toDisplay){
        for (int i = 0; i < toDisplay.length; i++) {
            graphics.drawString(toDisplay[i],10,10+10*i);
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
        return isPaused?0:deltaSeconds;
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



    //<editor-fold desc="getter">
    @Deprecated
    public static int getMouseX(){
        return MouseInfo.getPointerInfo().getLocation().x;
    }

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

    public int getWidth(){
        return getFrame().getWidth();
    }

    public int getHeight(){
        return getFrame().getHeight();
    }

    public void doNextTick(Runnable runnable){
        getScheduler().scheduleTask(runnable,0);
    }
    //</editor-fold>
}
